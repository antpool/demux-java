package com.antpool.demux.reader.eos;

import com.antpool.demux.exception.DemuxException;
import com.antpool.demux.model.Block;
import com.antpool.demux.reader.AbstractActionReader;
import com.antpool.demux.reader.eos.model.raw.EosChainBlockRequest;
import com.antpool.demux.reader.eos.model.raw.EosRawBlock;
import com.antpool.demux.reader.eos.model.raw.EosRawChainInfo;
import com.antpool.demux.reader.eos.service.DefaultOKHttpClient;
import com.antpool.demux.reader.eos.service.NodeosService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

/**
 * Reads from an EOSIO nodeos node to get blocks of actions.
 * It is important to note that deferred transactions will not be included,
 * as these are currently not accessible without the use of plugins.
 */
@Slf4j
public class NodeosActionReader extends AbstractActionReader {

    private NodeosService nodeosService;

    public NodeosActionReader(String nodeosEndpoint) {
        this(nodeosEndpoint, 1, false, 600);
    }

    public NodeosActionReader(String nodeosEndpoint, long startAtBlock) {
        this(nodeosEndpoint, startAtBlock, false, 600);
    }

    public NodeosActionReader(String nodeosEndpoint, long startAtBlock, boolean onlyIrreversible, int maxHistoryLength) {
        super(startAtBlock, onlyIrreversible, maxHistoryLength);
        initNodeosService(nodeosEndpoint);
    }

    protected void initNodeosService(String nodeosEndpoint) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(nodeosEndpoint)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .client(new DefaultOKHttpClient().getHttpClient(HttpLoggingInterceptor.Level.NONE))
                .build();
        nodeosService = retrofit.create(NodeosService.class);
    }

    @Override
    protected long getHeadBlockNumber() {
        Call<EosRawChainInfo> callResponse = nodeosService.getChainInfo();
        try {
            Response<EosRawChainInfo> response = callResponse.execute();
            if (!response.isSuccessful()) {
                String errorBody = response.errorBody().string();
                log.error("nodeosService getChainInfo error : {}", errorBody);
                throw new DemuxException(errorBody);
            }
            EosRawChainInfo rawChainInfo = response.body();
            return rawChainInfo.getHeadBlockNum();
        } catch (IOException ex) {
            log.error("getHeadBlockNumber exception", ex);
            throw new DemuxException("getHeadBlockNumber exception", ex);
        }
    }

    @Override
    protected Block getBlock(long blockNumber) {
        Call<EosRawBlock> callResponse = nodeosService.getChainBlock(new EosChainBlockRequest(blockNumber + ""));
        try {
            Response<EosRawBlock> response = callResponse.execute();
            if (!response.isSuccessful()) {
                String errorBody = response.errorBody().string();
                log.error("nodeosService getChainBlock error : {}", errorBody);
                throw new DemuxException(errorBody);
            }
            EosRawBlock rawBlock = response.body();
            return new NodeosBlock(rawBlock);
        } catch (IOException ex) {
            log.error("getBlock exception, blockNumber={}", blockNumber, ex);
            throw new DemuxException("getBlock exception", ex);
        }
    }
}
