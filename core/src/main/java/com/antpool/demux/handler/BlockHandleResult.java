package com.antpool.demux.handler;

import lombok.Data;

@Data
public class BlockHandleResult {
    private boolean needToSeek;
    private long seekBlockNum;

    public BlockHandleResult(boolean needToSeek, long seekBlockNum) {
        this.needToSeek = needToSeek;
        this.seekBlockNum = seekBlockNum;
    }
}
