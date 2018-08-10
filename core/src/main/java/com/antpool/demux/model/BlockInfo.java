package com.antpool.demux.model;

import lombok.Data;

@Data
public class BlockInfo {
    protected String blockHash;
    protected long blockNumber;
    protected String previousBlockHash;
}
