package com.antpool.demux.example.eos.transfers;

import com.antpool.demux.model.IndexState;
import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class TransferState extends IndexState {
    private Map<String, BigDecimal>  volumeBySymbol = Maps.newLinkedHashMap();
    private long totalTransfers;
}
