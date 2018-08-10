package com.antpool.demux.reader.eos.model.mapper;

import com.antpool.demux.reader.eos.model.raw.EosRawBlockTrx;
import com.antpool.demux.reader.eos.model.raw.EosRawTransaction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class EosRawBlockTrxDeserializer extends JsonDeserializer<EosRawBlockTrx> {

    @Override
    public EosRawBlockTrx deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken jsonToken = jp.getCurrentToken();
        if (jsonToken == JsonToken.VALUE_STRING) {
            JsonNode node = jp.getCodec().readTree(jp);
            return new EosRawBlockTrx(node.asText(), new EosRawTransaction());
        }
        return ctxt.readValue(jp, EosRawBlockTrx.class);
    }
}
