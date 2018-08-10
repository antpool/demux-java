package com.antpool.demux.reader.eos.model.raw;

import lombok.Data;

@Data
public class EosRawChainInfo {
    private long headBlockNum;
    private String headBlockId;
    private long lastIrreversibleBlockNum;
    private String lastIrreversibleBlockId;
    private String headBlockTime;
    private String headBlockProducer;
}
