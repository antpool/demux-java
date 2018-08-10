package com.antpool.demux.reader.eos;

import com.antpool.demux.model.Action;
import com.antpool.demux.model.Block;
import com.antpool.demux.reader.eos.model.EosPayload;
import com.antpool.demux.reader.eos.model.raw.EosRawAction;
import com.antpool.demux.reader.eos.model.raw.EosRawBlock;
import com.antpool.demux.reader.eos.model.raw.EosRawBlockTrx;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class NodeosBlock extends Block<EosPayload> {

    public NodeosBlock(EosRawBlock rawBlock) {
        this.actions = collectActionsFromBlock(rawBlock);
        this.blockNumber = rawBlock.getBlockNum();
        this.blockHash = rawBlock.getId();
        this.previousBlockHash = rawBlock.getPrevious();
    }

    protected List<Action<EosPayload>> collectActionsFromBlock(EosRawBlock rawBlock) {
        List<Action<EosPayload>> actions = Lists.newArrayList();
        if (rawBlock.getTransactions() == null || rawBlock.getTransactions().isEmpty()) {
            return actions;
        }
        List<EosRawBlockTrx> trxList = rawBlock.getTransactions().stream()
                .map(item -> item.getTrx()).collect(Collectors.toList());
        if (trxList.isEmpty()) {
            return actions;
        }
        for (EosRawBlockTrx trx : trxList) {
            if (trx.getTransaction() != null) {
                int i = 0;
                for (EosRawAction rawAction : trx.getTransaction().getActions()) {
                    String type = rawAction.getAccount() + "::" + rawAction.getName();
                    EosPayload payload = EosPayload.builder()
                            .actionIndex(i++)
                            .name(rawAction.getName())
                            .account(rawAction.getAccount())
                            .authorization(rawAction.getAuthorization())
                            .data(rawAction.getData())
                            .transactionId(trx.getId())
                            .build();
                    actions.add(new Action<>(type, payload));
                }
            }
        }
        return actions;
    }
}
