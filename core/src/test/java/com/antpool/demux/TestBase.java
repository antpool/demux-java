package com.antpool.demux;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;

public class TestBase {
    protected ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    protected <T> T getData(String fileName, Class<T> clazz) throws Exception {
        return mapper.readValue(this.getClass().getResourceAsStream(fileName), clazz);
    }

    protected <T> T getData(String fileName, JavaType valueType) throws Exception {
        return mapper.readValue(this.getClass().getResourceAsStream(fileName), valueType);
    }
}
