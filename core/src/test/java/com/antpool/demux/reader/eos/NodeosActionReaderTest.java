package com.antpool.demux.reader.eos;

import com.antpool.demux.exception.DemuxException;
import com.antpool.demux.model.Block;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeosActionReaderTest {

    private NodeosActionReader actionReader;

    private long blockNumber;

    @Before
    public void setUp() throws Exception {
        actionReader = new NodeosActionReader("http://api.bp.antpool.com");
        blockNumber = 10000000;
    }

    @Test
    public void getHeadBlockNumber() {
        long headBlockNumber = actionReader.getHeadBlockNumber();
        assertThat(headBlockNumber).isGreaterThan(blockNumber);
    }

    @Test
    public void getBlock() {
        Block block = actionReader.getBlock(blockNumber);
        assertThat(block).isNotNull();
        assertThat(block.getBlockNumber()).isEqualTo(blockNumber);
        assertThat(block.getActions()).isNotEmpty();
    }

    @Test(expected = DemuxException.class)
    public void getNotExistBlock() {
        Block block = actionReader.getBlock(-1);
        assertThat(block).isNull();
    }

    @Test
    public void getBlockWithStringTrx() {
        Block block = actionReader.getBlock(10408376);
        assertThat(block).isNotNull();
        assertThat(block.getBlockNumber()).isEqualTo(10408376);
        assertThat(block.getActions()).isNotEmpty();
    }
}