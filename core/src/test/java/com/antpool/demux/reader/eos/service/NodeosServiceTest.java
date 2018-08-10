package com.antpool.demux.reader.eos.service;

import com.antpool.demux.reader.eos.model.raw.EosChainBlockRequest;
import com.antpool.demux.reader.eos.model.raw.EosRawBlock;
import com.antpool.demux.reader.eos.model.raw.EosRawChainInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeosServiceTest {

    private NodeosService nodeosService;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.bp.antpool.com")
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .client(new DefaultOKHttpClient().getHttpClient(HttpLoggingInterceptor.Level.BODY)) //test print body
                .build();
        nodeosService = retrofit.create(NodeosService.class);
    }

    @Test
    public void getChainInfo() throws Exception {
        Call<EosRawChainInfo> callResponse = nodeosService.getChainInfo();

        Response<EosRawChainInfo> response = callResponse.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();

        EosRawChainInfo rawChainInfo = response.body();
        assertThat(rawChainInfo).isNotNull();
    }

    @Test
    public void getChainBlock() throws Exception {
        Call<EosRawBlock> callResponse = nodeosService.getChainBlock(new EosChainBlockRequest("10000000"));

        Response<EosRawBlock> response = callResponse.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();

        EosRawBlock rawBlock = response.body();
        assertThat(rawBlock).isNotNull();
        assertThat(rawBlock.getBlockNum()).isEqualTo(10000000);
    }

    @Test
    public void getChainBlockWithStringTrx() throws Exception {
        Call<EosRawBlock> callResponse = nodeosService.getChainBlock(new EosChainBlockRequest("10408376"));

        Response<EosRawBlock> response = callResponse.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();

        EosRawBlock rawBlock = response.body();
        assertThat(rawBlock).isNotNull();
        assertThat(rawBlock.getBlockNum()).isEqualTo(10408376);
    }
}