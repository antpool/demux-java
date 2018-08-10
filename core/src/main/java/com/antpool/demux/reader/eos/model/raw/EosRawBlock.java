package com.antpool.demux.reader.eos.model.raw;

import lombok.Data;

import java.util.List;

@Data
public class EosRawBlock {
    private String previous;
    private String id;
    private long blockNum;
    private List<EosRawBlockTransaction> transactions;
}
