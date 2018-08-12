package com.antpool.demux.handler;

import com.antpool.demux.exception.DemuxException;
import com.antpool.demux.model.Action;
import com.antpool.demux.model.Block;
import com.antpool.demux.model.IndexState;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Takes `block`s output from implementations of `AbstractActionReader` and processes their actions through
 * `Updater`s and `Effect`s. Pass an object exposing a persistence API as `state` in the `handleWithState`
 * method. Persist and retrieve information about the last block processed with `updateIndexState` and
 * `loadIndexState`.
 */
@Slf4j
public abstract class AbstractActionHandler<TState extends IndexState, TPayload, TContext> {
    protected long lastProcessedBlockNumber;
    protected String lastProcessedBlockHash;

    protected final List<Updater> updaters;
    protected final List<Effect> effects;

    protected TState state;
    protected TContext context;

    protected ExecutorService effectExecutorService;

    public AbstractActionHandler(List<Updater> updaters, List<Effect> effects, TState state, TContext context) {
        this.updaters = updaters;
        this.effects = effects;
        this.state = state;
        this.context = context;
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("effect-%d").setDaemon(true).build();
        this.effectExecutorService = Executors.newFixedThreadPool(20, threadFactory);
    }

    /**
     * Updates the `lastProcessedBlockNumber` and `lastProcessedBlockHash` meta state, coinciding with the block
     * that has just been processed. These are the same values read by `updateIndexState()`.
     */
    protected abstract void updateIndexState(TState state, Block<TPayload> block, boolean isReplay, TContext context);

    /**
     * Returns a promise for the `lastProcessedBlockNumber` and `lastProcessedBlockHash` meta state, coinciding with the block
     * that has just been processed. These are the same values written by `updateIndexState()`.
     *
     * @returns A promise that resolves to an `IndexState`
     */
    protected abstract IndexState loadIndexState();

    protected abstract void handleWithState(HandleWithArgs<TState, TContext> args);

    /**
     * Will run when a rollback block number is passed to handleActions. Implement this method to
     * handle reversing actions full blocks at a time, until the last applied block is the block
     * number passed to this method. If replay is true, effects should not be processed
     */
    protected abstract void rollbackTo(long blockNumber);

    public BlockHandleResult handleBlock(Block<TPayload> block, boolean isRollback, boolean isFirstBlock) {
        return handleBlock(block, isRollback, isFirstBlock, false);
    }

    /**
     * Receive block, validate, and handle actions with updaters and effects
     */
    public BlockHandleResult handleBlock(Block<TPayload> block, boolean isRollback, boolean isFirstBlock, boolean isReplay) {
        if (block == null) {
            log.error("handleBlock block is null");
            return new BlockHandleResult(false, 0);
        }
        if (isRollback) {
            //this.rollbackTo(block.getBlockNumber() - 1);
            //TODO confirm rollback blockNumber
            this.rollbackTo(block.getBlockNumber());
        }
        if (StringUtils.isBlank(lastProcessedBlockHash) && this.lastProcessedBlockNumber == 0) {
            IndexState indexState = this.loadIndexState();
            if (indexState != null) {
                this.lastProcessedBlockNumber = indexState.getBlockNumber();
                this.lastProcessedBlockHash = indexState.getBlockHash();
            }
        }
        // Just processed this block; skip
        if (block.getBlockNumber() == this.lastProcessedBlockNumber
                && StringUtils.equals(block.getBlockHash(), this.lastProcessedBlockHash)) {
            return new BlockHandleResult(false, 0);
        }

        long nextBlockNeeded = this.lastProcessedBlockNumber + 1;
        // If it's the first block but we've already processed blocks, seek to next block
        if (isFirstBlock && StringUtils.isNotBlank(this.lastProcessedBlockHash)) {
            log.warn("handleBlock needToSeek: isFirstBlock & lastProcessedBlockHash is not black");
            return new BlockHandleResult(true, nextBlockNeeded);
        }
        // Only check if this is the block we need if it's not the first block
        if (!isFirstBlock) {
            if (block.getBlockNumber() != nextBlockNeeded) {
                log.warn("handleBlock needToSeek: isNotFirstBlock & blockNumber={} != lastProcessedBlockNumber={}", block.getBlockNumber(), lastProcessedBlockNumber);
                return new BlockHandleResult(true, nextBlockNeeded);
            }
            // Block sequence consistency should be handled by the ActionReader instance
            //TODO confirm rollback -> lastProcessedBlockHash
            /*if (!StringUtils.equals(block.getPreviousBlockHash(), this.lastProcessedBlockHash)) {
                throw new DemuxException("Block hashes do not match; block not part of current chain.");
            }*/
        }

        HandleWithArgs<TState, TContext> args = this.handleActions(this.state, block, isReplay, this.context);
        this.handleWithState(args);
        return new BlockHandleResult(false, 0);
    }

    /**
     * Calls `runUpdaters` and `runEffects` on the given actions
     */
    protected HandleWithArgs<TState, TContext> handleActions(TState state, Block<TPayload> block, boolean isReplay, TContext context) {
        this.runUpdaters(state, block, context);
        if (!isReplay) {
            this.runEffects(state, block, context);
        }
        this.updateIndexState(state, block, isReplay, context);
        this.lastProcessedBlockNumber = block.getBlockNumber();
        this.lastProcessedBlockHash = block.getBlockHash();
        return new HandleWithArgs<>(state, context);
    }

    /**
     * Process actions against deterministically accumulating updater functions.
     */
    protected void runUpdaters(TState state, Block<TPayload> block, TContext context) {
        runExecutor(updaters, state, block, context);
    }

    /**
     * Process actions against asynchronous side effects.
     */
    protected void runEffects(TState state, Block<TPayload> block, TContext context) {
        runExecutor(effects, state, block, context);
    }

    protected <T extends Executor> void runExecutor(List<T> executors, TState state, Block<TPayload> block, TContext context) {
        if (block == null) {
            return;
        }
        for (Action<TPayload> action : block.getActions()) {
            for (Executor executor : executors) {
                if (executor.getActionType().equals(action.getType())) {
                    if (executor instanceof Updater) {
                        executor.execute(state, action.getPayload(), block, context);
                    } else if (executor instanceof Effect) {
                        effectExecutorService.submit(() -> executor.execute(state, action.getPayload(), block, context));
                    }
                }
            }
        }
    }
}
