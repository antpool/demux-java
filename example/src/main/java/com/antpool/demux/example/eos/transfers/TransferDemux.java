package com.antpool.demux.example.eos.transfers;

import com.antpool.demux.reader.eos.NodeosActionReader;
import com.antpool.demux.watcher.BaseActionWatcher;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDemux {

    public static void main(String[] args) {
        NodeosActionReader actionReader = new NodeosActionReader("http://api.bp.antpool.com", 0);
        TransferUpdater updater = new TransferUpdater("eosio.token::transfer");
        TransferEffects effect = new TransferEffects("eosio.token::transfer");
        TransferActionHandler actionHandler = new TransferActionHandler(actionReader, Lists.newArrayList(updater), Lists.newArrayList(effect), new TransferState());
        BaseActionWatcher watcher = new BaseActionWatcher(actionReader, actionHandler, 500);
        watcher.watch();
    }
}
