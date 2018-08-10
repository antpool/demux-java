package com.antpool.demux.reader.eos.service;

import com.antpool.demux.reader.eos.model.raw.EosChainBlockRequest;
import com.antpool.demux.reader.eos.model.raw.EosRawBlock;
import com.antpool.demux.reader.eos.model.raw.EosRawChainInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NodeosService {

    @GET("/v1/chain/get_info")
    Call<EosRawChainInfo> getChainInfo();

    @POST("/v1/chain/get_block")
    Call<EosRawBlock> getChainBlock(@Body EosChainBlockRequest request);
}
