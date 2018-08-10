package com.antpool.demux.reader.eos.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
@Builder
public class EosPayload<T> {
    private String account;
    private int actionIndex;
    private List<EosAuthorization> authorization;
    private T data;
    private String name;
    private String transactionId;

    @Tolerate
    public EosPayload() {
        //lombok Tolerate
    }
}
