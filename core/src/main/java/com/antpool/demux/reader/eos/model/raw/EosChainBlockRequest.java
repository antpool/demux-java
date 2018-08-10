package com.antpool.demux.reader.eos.model.raw;

import lombok.Data;

@Data
public class EosChainBlockRequest {
    private String blockNumOrId;

    public EosChainBlockRequest(String blockNumOrId) {
        this.blockNumOrId = blockNumOrId;
    }
}
