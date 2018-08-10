package com.antpool.demux.example.eos.transfers;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Data
public class TransferData {
    private String from;
    private String to;
    private String quantity;
    private String memo;

    @Data
    public class Token {
        private BigDecimal amount;
        private String symbol;

        public Token(BigDecimal amount, String symbol) {
            this.amount = amount;
            this.symbol = symbol;
        }
    }
    public Optional<Token> parseTokenString(){
        if(StringUtils.isBlank(quantity)){
            return Optional.empty();
        }
        String[] tokenStr = quantity.split(" ");
        if(tokenStr == null){
            return Optional.empty();
        }
        return Optional.of(new Token(new BigDecimal(tokenStr[0]), tokenStr[1]));
    }
}
