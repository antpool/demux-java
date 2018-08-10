package com.antpool.demux.handler;

import com.antpool.demux.model.IndexState;
import lombok.Data;

@Data
public class HandleWithArgs<TState extends IndexState, TContext> {
    private TState state;
    private TContext context;

    public HandleWithArgs(TState state, TContext context) {
        this.state = state;
        this.context = context;
    }
}
