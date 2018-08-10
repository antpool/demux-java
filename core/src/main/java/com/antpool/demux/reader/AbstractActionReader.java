package com.antpool.demux.reader;

import com.antpool.demux.exception.DemuxException;
import com.antpool.demux.model.Block;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Reads blocks from a blockchain, outputting normalized `Block` objects.
 */
@Slf4j
public abstract class AbstractActionReader {
    @Getter
    private long startAtBlock;
    private boolean onlyIrreversible;
    private int maxHistoryLength;

    private long headBlockNumber = 0;
    @Getter
    private long currentBlockNumber;
    @Getter
    private boolean isFirstBlock = true;

    private Block currentBlockData;
    private List<Block> blockHistory = Lists.newArrayList();

    public AbstractActionReader() {
        this(1, false, 600);
    }

    public AbstractActionReader(long startAtBlock) {
        this(startAtBlock, false, 600);
    }

    public AbstractActionReader(long startAtBlock, boolean onlyIrreversible, int maxHistoryLength) {
        this.startAtBlock = startAtBlock;
        this.onlyIrreversible = onlyIrreversible;
        this.maxHistoryLength = maxHistoryLength;
        this.currentBlockNumber = startAtBlock - 1;
    }

    /**
     * Loads the head block number, returning an int.
     * If onlyIrreversible is true, return the most recent irreversible block number
     *
     * @return
     */
    protected abstract long getHeadBlockNumber();

    /**
     * Loads a block with the given block number
     *
     * @param blockNumber
     * @return
     */
    protected abstract Block getBlock(long blockNumber);

    /**
     * Loads the next block with chainInterface after validating, updating all relevant state.
     * If block fails validation, rollback will be called, and will update state to last block unseen.
     *
     * @return
     */
    public BlockReadResult nextBlock() {
        Block blockData;
        boolean isRollback = false;

        if (this.currentBlockNumber == this.headBlockNumber || this.headBlockNumber == 0) {
            this.headBlockNumber = this.getHeadBlockNumber();
        }

        if (this.currentBlockNumber < 0 && this.blockHistory.size() == 0) {
            this.currentBlockNumber = this.headBlockNumber + this.currentBlockNumber;
            this.startAtBlock = this.currentBlockNumber + 1;
        }

        if (this.currentBlockNumber < this.headBlockNumber) {
            Block unvalidatedBlockData = this.getBlock(this.currentBlockNumber + 1);

            String expectedHash = this.currentBlockData != null ? this.currentBlockData.getBlockHash() : "INVALID";
            String actualHash = unvalidatedBlockData.getPreviousBlockHash();

            // Continue if the new block is on the same chain as our history, or if we've just started
            if (expectedHash.equals(actualHash) || this.blockHistory.size() == 0) {
                blockData = unvalidatedBlockData; // Block is now validated
                if (this.currentBlockData != null) {
                    this.blockHistory.add(this.currentBlockData); // No longer current, belongs on history
                }
                int trimLen = this.blockHistory.size() - this.maxHistoryLength;
                if (trimLen > 0) {
                    this.blockHistory = this.blockHistory.subList(trimLen - 1, this.blockHistory.size()); // Trim history
                }
                this.currentBlockData = blockData; // Replaced with the real current block
                this.currentBlockNumber = this.currentBlockData.getBlockNumber();
            } else {
                // Since the new block did not match our history, we can assume our history is wrong
                // and need to roll back
                this.rollback();
                isRollback = true; // Signal action handler that we must roll back
                // Reset for safety, as new fork could have less blocks than the previous fork
                this.headBlockNumber = this.getHeadBlockNumber();
            }
        }

        // Let handler know if this is the earliest block we'll send
        this.isFirstBlock = this.currentBlockNumber == this.startAtBlock;

        if (this.currentBlockData == null) {
            throw new DemuxException("blockData must not be null.");
        }
        return new BlockReadResult(this.currentBlockData, isRollback);
    }

    /**
     * Incrementally rolls back reader state one block at a time, comparing the blockHistory with
     * newly fetched blocks. Rollback is finished when either the current block's previous hash
     * matches the previous block's hash, or when history is exhausted.
     */
    protected void rollback() {
        log.info("!! Fork detected !!");

        int blocksToRewind = 0;
        // Rewind at least 1 block back
        int blockHistorySize = this.blockHistory.size();
        if (blockHistorySize > 0) {
            Block block = popBlockHistory().orElse(null);
            if (block == null) {
                throw new DemuxException("block history should not have undefined entries.");
            }
            this.currentBlockData = this.getBlock(block.getBlockNumber());
            blocksToRewind = 1;
        }

        // Pop off blocks from cached block history and compare them with freshly fetched blocks
        while (this.blockHistory.size() > 0) {
            Block cachedPreviousBlockData = this.blockHistory.get(this.blockHistory.size() - 1);
            Block previousBlockData = this.getBlock(cachedPreviousBlockData.getBlockNumber());
            Block currentBlock = this.currentBlockData;
            if (currentBlock != null) {
                if (currentBlock.getPreviousBlockHash().equals(previousBlockData.getBlockHash())) {
                    log.info("✓ BLOCK {} MATCH:", currentBlock.getBlockNumber());
                    log.info("  expected: {}", currentBlock.getPreviousBlockHash());
                    log.info("  received: {}", previousBlockData.getBlockHash());
                    log.info("Rewinding {} blocks to block ({})...", blocksToRewind, currentBlock.getBlockNumber());
                    break;
                }
                log.info("✕ BLOCK {} MATCH:", currentBlock.getBlockNumber());
                log.info("  expected: {}", currentBlock.getPreviousBlockHash());
                log.info("  received: {}", previousBlockData.getBlockHash());
                log.info("Rollback history has been exhausted!");
            }

            this.currentBlockData = previousBlockData;
            popBlockHistory();
            blocksToRewind += 1;
        }
        if (this.blockHistory.size() == 0) {
            this.rollbackExhausted();
        }
    }

    protected void rollbackExhausted() {
        throw new DemuxException("Rollback history has been exhausted, and no rollback exhaustion handling has been implemented.");
    }

    /**
     * Move to the specified block.
     */
    public void seekToBlock(long blockNumber) {
        // Clear current block data
        this.currentBlockData = null;
        this.headBlockNumber = 0;

        // If we're going back to the first block, we don't want to get the preceding block
        if (blockNumber == 1) {
            this.blockHistory = Lists.newCopyOnWriteArrayList();
            return;
        }

        // Check if block exists in history
        int toDelete = -1;
        for (int i = this.blockHistory.size() - 1; i >= 0; i--) {
            if (this.blockHistory.get(i).getBlockNumber() == blockNumber) {
                break;
            } else {
                toDelete += 1;
            }
        }
        if (toDelete >= 0) {
            int blockHistorySize = this.blockHistory.size();
            this.blockHistory = this.blockHistory.subList(0, blockHistorySize - toDelete);
            // pop blockHistory
            popBlockHistory().ifPresent(block -> this.currentBlockData = block);
        }

        // Load current block
        this.currentBlockNumber = blockNumber - 1;
        if (this.currentBlockData == null) {
            this.currentBlockData = this.getBlock(this.currentBlockNumber);
        }
    }

    public long headBlockNumber() {
        return headBlockNumber;
    }

    private Optional<Block> popBlockHistory() {
        int blockHistorySize = this.blockHistory.size();
        if (blockHistorySize == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.blockHistory.remove(blockHistorySize - 1));
    }
}
