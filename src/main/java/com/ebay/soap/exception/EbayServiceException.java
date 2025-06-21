package com.ebay.soap.exception;

public class EbayServiceException extends RuntimeException {
    
    private String errorCode;
    
    public EbayServiceException(String message) {
        super(message);
    }
    
    public EbayServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public EbayServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}