package com.antpool.demux.handler;

import com.antpool.demux.model.Block;
import com.antpool.demux.model.IndexState;

import java.util.List;

public class TestActionHandler<TState extends IndexState, TPayload, TContext> extends AbstractActionHandler<TState, TPayload, TContext> {

    public TestActionHandler(List<Updater> updaters, List<Effect> effects, TState state, TContext context) {
        super(updaters, effects, state, context);
    }

    @Override
    protected void updateIndexState(TState state, Block<TPayload> block, boolean isReplay, TContext context) {
    }

    @Override
    protected IndexState loadIndexState() {
        return null;
    }

    @Override
    protected void handleWithState(HandleWithArgs<TState, TContext> args) {

    }

    @Override
    protected void rollbackTo(long blockNumber) {

    }
}
