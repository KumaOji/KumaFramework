package com.kuma.boot.core.chain.core.exception;

public class ChainException extends RuntimeException {

    private final String chainId;

    public ChainException(String chainId, String message) {
        super(message);
        this.chainId = chainId;
    }

    public ChainException(String chainId, String message, Throwable cause) {
        super(message, cause);
        this.chainId = chainId;
    }

    public String getChainId() {
        return chainId;
    }
}
