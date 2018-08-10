package com.antpool.demux.reader.eos.model;

import lombok.Data;

@Data
public class EosAuthorization {
    private String actor;
    private String permission;
}
