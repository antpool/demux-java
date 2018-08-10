package com.antpool.demux.reader;

import com.antpool.demux.model.Block;
import lombok.Data;

@Data
public class BlockReadResult {
    private Block blockData;
    private boolean isRollback;

    public BlockReadResult(Block blockData, boolean isRollback) {
        this.blockData = blockData;
        this.isRollback = isRollback;
    }
}
