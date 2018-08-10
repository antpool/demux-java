package com.antpool.demux.reader.eos.model.raw;

import lombok.Data;

@Data
public class EosRawBlockTrx {
    private String id;
    private EosRawTransaction transaction;

    public EosRawBlockTrx() {
    }

    public EosRawBlockTrx(String id, EosRawTransaction transaction) {
        this.id = id;
        this.transaction = transaction;
    }
}
