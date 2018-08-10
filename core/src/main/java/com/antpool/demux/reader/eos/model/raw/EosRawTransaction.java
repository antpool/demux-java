package com.antpool.demux.reader.eos.model.raw;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class EosRawTransaction {
    private List<EosRawAction> actions = Lists.newArrayList();
}
