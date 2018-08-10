package com.antpool.demux.watcher;

import com.antpool.demux.handler.AbstractActionHandler;
import com.antpool.demux.handler.BlockHandleResult;
import com.antpool.demux.model.Block;
import com.antpool.demux.reader.AbstractActionReader;
import com.antpool.demux.reader.BlockReadResult;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Cooredinates implementations of `AbstractActionReader`s and `AbstractActionHandler`s in
 * a polling loop.
 */
@Slf4j
public class BaseActionWatcher {

    private final AbstractActionReader actionReader;
    private final AbstractActionHandler actionHandler;
    private final int pollInterval;

    public BaseActionWatcher(AbstractActionReader actionReader, AbstractActionHandler actionHandler, int pollInterval) {
        this.actionReader = actionReader;
        this.actionHandler = actionHandler;
        this.pollInterval = pollInterval;
    }

    /**
     * Starts a polling loop running in replay mode.
     */
    public void replay() {
        this.actionReader.seekToBlock(this.actionReader.getStartAtBlock());
        this.watch();
    }

    /**
     * Uses the given actionReader and actionHandler to poll and process new blocks.
     */
    public void watch() {
        // Record start time
        long startTime = System.currentTimeMillis();

        // Process blocks until we're at the head block
        long headBlockNumber = 0;
        while (headBlockNumber > 0 || this.actionReader.getCurrentBlockNumber() < headBlockNumber) {
            try {
                headBlockNumber = explore(headBlockNumber);
            } catch (Exception ex) {
                log.error("explore error, headBlockNumber={}, currentBlockNumber={}", headBlockNumber, this.actionReader.getCurrentBlockNumber(), ex);
            }
        }

        // Calculate timing for next iteration
        long waitTime = this.pollInterval - (System.currentTimeMillis() - startTime);
        if (waitTime < 0) {
            waitTime = 0;
        }
        // Schedule next iteration
        try {
            TimeUnit.MILLISECONDS.sleep(waitTime);
        } catch (InterruptedException ex) {
            log.error("watch sleep error", ex);
        }
        this.watch();
    }

    protected long explore(long headBlockNumber) {
        BlockReadResult readResult = this.actionReader.nextBlock();
        if (readResult == null) {
            return headBlockNumber;
        }
        Block blockData = readResult.getBlockData();
        boolean isRollback = readResult.isRollback();

        // Handle block (and the actions within them)
        boolean needToSeek = false;
        long seekBlockNum = 0;
        if (blockData != null) {
            BlockHandleResult handleResult = this.actionHandler.handleBlock(blockData, isRollback, this.actionReader.isFirstBlock());
            needToSeek = handleResult.isNeedToSeek();
            seekBlockNum = handleResult.getSeekBlockNum();
        }

        // Seek to next needed block at the request of the action handler
        if (needToSeek) {
            this.actionReader.seekToBlock(seekBlockNum - 1);
        }

        return this.actionReader.headBlockNumber();
    }

}
