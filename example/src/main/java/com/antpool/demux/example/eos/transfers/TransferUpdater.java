package com.antpool.demux.example.eos.transfers;

import com.antpool.demux.handler.Updater;
import com.antpool.demux.model.BlockInfo;
import com.antpool.demux.reader.eos.model.EosPayload;
import com.antpool.demux.util.BeanMapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class TransferUpdater<TState extends TransferState, TPayload extends EosPayload, TContext>  extends Updater<TState, TPayload, TContext>  {

    public TransferUpdater(String actionType) {
        super(actionType);
    }

    @Override
    public void execute(TState state, TPayload payload, BlockInfo blockInfo, TContext context) {
        if(payload ==null || payload.getData()==null){
            log.error("updater payload error, payload = {}", payload );
            return;
        }
        TransferData data = BeanMapper.map(payload.getData(), TransferData.class);
        data.parseTokenString().ifPresent(token -> {
            if(state.getVolumeBySymbol().containsKey(token.getSymbol())){
                BigDecimal amount = state.getVolumeBySymbol().get(token.getSymbol());
                state.getVolumeBySymbol().put(token.getSymbol(), amount.add(token.getAmount()));
            }else {
                state.getVolumeBySymbol().put(token.getSymbol(), token.getAmount());
            }
            state.setTotalTransfers(state.getTotalTransfers() + 1);
        });

    }
}
