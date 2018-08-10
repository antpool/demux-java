package com.antpool.demux.model;

import lombok.Data;

import java.util.Date;

@Data
public class BlockInfo {
    protected String blockHash;
    protected long blockNumber;
    protected String previousBlockHash;
    protected Date timestamp;
}
