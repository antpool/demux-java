package com.antpool.demux.model;

import lombok.Data;

@Data
public class IndexState {
    private long blockNumber;
    private String blockHash;

    public IndexState() {
    }

    public IndexState(long blockNumber, String blockHash) {
        this.blockNumber = blockNumber;
        this.blockHash = blockHash;
    }
}
