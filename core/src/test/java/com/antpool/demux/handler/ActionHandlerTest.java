package com.antpool.demux.handler;

import com.antpool.demux.TestBase;
import com.antpool.demux.model.Block;
import com.antpool.demux.model.BlockInfo;
import com.antpool.demux.reader.eos.model.EosPayload;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionHandlerTest extends TestBase {

    private TestActionHandler actionHandler;
    private Block<EosPayload> block;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        block = getData("/actionhandler.json", mapper.getTypeFactory().constructParametricType(Block.class, EosPayload.class));
        Updater updater = new Updater<TestIndexState, EosPayload, Object>("testing::action") {
            @Override
            public void execute(TestIndexState state, EosPayload payload, BlockInfo blockInfo, Object context) {
                state.setCount(state.getCount() + 1);
            }
        };
        Effect effect = new Effect<TestIndexState, EosPayload, Object>("testing::action") {
            @Override
            public void execute(TestIndexState state, EosPayload payload, BlockInfo blockInfo, Object context) {
                System.out.println(payload);
            }
        };

        actionHandler = new TestActionHandler<TestIndexState, EosPayload, Object>(Lists.newArrayList(updater), Lists.newArrayList(effect), new TestIndexState(), null);
    }

    @Test
    public void testHandleFirstBlock() {
        BlockHandleResult result = actionHandler.handleBlock(block, false, true);
        assertThat(result).isNotNull();
    }

    @Test
    public void testHandleBlock() {
        BlockHandleResult result = actionHandler.handleBlock(block, false, false);
        assertThat(result).isNotNull();
    }
}