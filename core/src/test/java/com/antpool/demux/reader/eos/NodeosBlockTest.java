package com.antpool.demux.reader.eos;

import com.antpool.demux.TestBase;
import com.antpool.demux.reader.eos.model.raw.EosRawBlock;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeosBlockTest extends TestBase {

    private EosRawBlock rawBlock;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        rawBlock = getData("/nodeosblock.json", EosRawBlock.class);
    }

    @Test
    public void testConstructor() throws Exception {
        NodeosBlock block = new NodeosBlock(rawBlock);
        assertThat(block).isNotNull();
    }
}