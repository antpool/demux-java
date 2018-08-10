package com.antpool.demux.reader.eos.model.raw;

import com.antpool.demux.reader.eos.model.EosAuthorization;
import lombok.Data;

import java.util.List;

@Data
public class EosRawAction {
    private String account;
    private String name;
    private List<EosAuthorization> authorization;
    private Object data;
    private String hexData;
}
