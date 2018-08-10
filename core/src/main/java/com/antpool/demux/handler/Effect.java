package com.antpool.demux.handler;

import com.antpool.demux.model.IndexState;

public abstract class Effect<TState extends IndexState, TPayload, TContext> extends Executor<TState, TPayload, TContext> {

    public Effect(String actionType) {
        super(actionType);
    }
}
