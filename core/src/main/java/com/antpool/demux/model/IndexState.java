package com.antpool.demux.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class IndexState {
    private long blockNumber;
    private String blockHash;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    protected Date timestamp;

    public IndexState() {
    }

    public IndexState(long blockNumber, String blockHash) {
        this.blockNumber = blockNumber;
        this.blockHash = blockHash;
    }
}
