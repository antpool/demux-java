package com.antpool.demux.exception;

public class DemuxException extends RuntimeException {

    public DemuxException(String message) {
        super(message);
    }

    public DemuxException(String message, Throwable cause) {
        super(message, cause);
    }
}
