package com.antpool.demux.handler;

import com.antpool.demux.model.IndexState;

public abstract class Updater<TState extends IndexState, TPayload, TContext> extends Executor<TState, TPayload, TContext> {

    public Updater(String actionType) {
        super(actionType);
    }
}
