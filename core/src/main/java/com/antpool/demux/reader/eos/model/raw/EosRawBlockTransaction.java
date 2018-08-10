package com.antpool.demux.reader.eos.model.raw;

import com.antpool.demux.reader.eos.model.mapper.EosRawBlockTrxDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class EosRawBlockTransaction {

    @JsonDeserialize(using = EosRawBlockTrxDeserializer.class)
    private EosRawBlockTrx trx;

}
