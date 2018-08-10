package com.antpool.demux.reader;

import com.antpool.demux.TestBase;
import com.antpool.demux.model.Block;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonActionReaderTest extends TestBase {

    private JsonActionReader jsonActionReader;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        List<Block> blockchain = getData("/blockchain.json", mapper.getTypeFactory().constructCollectionType(List.class, Block.class));
        jsonActionReader = new JsonActionReader();
        jsonActionReader.setBlockchain(blockchain);
    }

    @Test
    public void testNextBlock() throws Exception {
        BlockReadResult blockReadResult = jsonActionReader.nextBlock();
        assertThat(blockReadResult).isNotNull();
    }

    @Test
    public void seekToBlock() throws Exception {
        jsonActionReader.seekToBlock(2);
    }
}