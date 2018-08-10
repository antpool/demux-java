package com.antpool.demux.model;

import lombok.Data;

import java.util.List;

@Data
public class Block<TPayload> extends BlockInfo {
    protected List<Action<TPayload>> actions;
}
