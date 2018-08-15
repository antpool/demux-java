package com.antpool.demux.example.eos.transfers;

import com.antpool.demux.handler.AbstractActionHandler;
import com.antpool.demux.handler.Effect;
import com.antpool.demux.handler.HandleWithArgs;
import com.antpool.demux.handler.Updater;
import com.antpool.demux.model.Block;
import com.antpool.demux.model.IndexState;
import com.antpool.demux.reader.AbstractActionReader;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TransferActionHandler<TState extends TransferState, TPayload, TContext> extends AbstractActionHandler<TState, TPayload, TContext> {

    private final AbstractActionReader actionReader;

    public TransferActionHandler(AbstractActionReader actionReader, Updater updaters, Effect effects, TState state) {
        this(actionReader, Lists.newArrayList(updaters), Lists.newArrayList(effects), state, null);
    }

    public TransferActionHandler(AbstractActionReader actionReader, List<Updater> updaters, List<Effect> effects, TState state, TContext context) {
        super(updaters, effects, state, context);
        this.actionReader = actionReader;
    }

    @Override
    protected void updateIndexState(TState state, Block<TPayload> block, boolean isReplay, TContext context) {
        state.setBlockNumber(block.getBlockNumber());
        state.setBlockHash(block.getBlockHash());
        state.setTimestamp(block.getTimestamp());
    }

    @Override
    protected IndexState loadIndexState() {
        //TODO load indexState from storage
        return new TransferState();
    }

    @Override
    protected void handleWithState(HandleWithArgs<TState, TContext> args) {
    }

    @Override
    protected void rollbackTo(long blockNumber) {
        log.info("rollbackTo blockNumber={}", blockNumber);
        this.lastProcessedBlockNumber = blockNumber - 1;
        Block block = actionReader.getBlock(this.lastProcessedBlockNumber);
        if (block != null) {
            this.lastProcessedBlockHash = block.getBlockHash();
        }
        //TODO rollback state
    }
}
