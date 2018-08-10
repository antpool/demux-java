package com.antpool.demux.model;

import lombok.Data;

@Data
public class Action<TPayload> {
    private String type;
    private TPayload payload;

    public Action() {
    }

    public Action(String type, TPayload payload) {
        this.type = type;
        this.payload = payload;
    }
}
