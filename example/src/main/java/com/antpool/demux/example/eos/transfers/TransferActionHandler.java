package com.antpool.demux.example.eos.transfers;

import com.antpool.demux.exception.DemuxException;
import com.antpool.demux.handler.AbstractActionHandler;
import com.antpool.demux.handler.Effect;
import com.antpool.demux.handler.HandleWithArgs;
import com.antpool.demux.handler.Updater;
import com.antpool.demux.model.Block;
import com.antpool.demux.model.IndexState;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TransferActionHandler<TState extends TransferState, TPayload, TContext> extends AbstractActionHandler<TState, TPayload, TContext> {

    public TransferActionHandler(List<Updater> updaters, List<Effect> effects, TState state) {
        this(updaters, effects, state, null);
    }

    public TransferActionHandler(List<Updater> updaters, List<Effect> effects, TState state, TContext context) {
        super(updaters, effects, state, context);
    }

    @Override
    protected void updateIndexState(TState state, Block<TPayload> block, boolean isReplay, TContext context) {
        state.setBlockNumber(block.getBlockNumber());
        state.setBlockHash(block.getBlockHash());
    }

    @Override
    protected IndexState loadIndexState() {
        //TODO load indexState from storage
        return new TransferState();
    }

    @Override
    protected void handleWithState(HandleWithArgs<TState, TContext> args) {
    }

    @Override
    protected void rollbackTo(long blockNumber) {
        throw new DemuxException(String.format("Cannot roll back to %d; \\`rollbackTo\\` not implemented.", blockNumber));
    }
}
