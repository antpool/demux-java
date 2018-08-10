package com.antpool.demux.handler;

import com.antpool.demux.model.BlockInfo;
import com.antpool.demux.model.IndexState;

public abstract class Executor<TState extends IndexState, TPayload, TContext> {

    private final String actionType;

    public Executor(String actionType) {
        this.actionType = actionType;
    }

    public abstract void execute(TState state, TPayload payload, BlockInfo blockInfo, TContext context);

    public String getActionType() {
        return actionType;
    }
}
