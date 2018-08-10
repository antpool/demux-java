package com.antpool.demux.reader;

import com.antpool.demux.exception.DemuxException;
import com.antpool.demux.model.Block;
import com.google.common.collect.Lists;
import lombok.Setter;

import java.util.List;

public class JsonActionReader extends AbstractActionReader {
    @Setter
    private List<Block> blockchain = Lists.newArrayList();

    @Override
    protected long getHeadBlockNumber() {
        Block block = this.blockchain.get(this.blockchain.size() - 1);
        if (this.blockchain.size() != block.getBlockNumber()) {
            throw new DemuxException(String.format("Block at position %d indicates position %d incorrectly.", this.blockchain.size(), block.getBlockNumber()));
        }
        return block.getBlockNumber();
    }

    @Override
    protected Block getBlock(long blockNumber) {
        Block block = this.blockchain.get((int) (blockNumber - 1));
        if (block == null) {
            throw new DemuxException(String.format("Block at position %d does not exist.", blockNumber));
        }
        if (block.getBlockNumber() != blockNumber) {
            throw new DemuxException(String.format("Block at position %d indicates position %d incorrectly.", blockNumber, block.getBlockNumber()));
        }
        return block;
    }
}
