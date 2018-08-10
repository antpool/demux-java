package com.antpool.demux.handler;

import com.antpool.demux.model.IndexState;
import lombok.Data;

@Data
public class TestIndexState extends IndexState {
    private int count;
}
