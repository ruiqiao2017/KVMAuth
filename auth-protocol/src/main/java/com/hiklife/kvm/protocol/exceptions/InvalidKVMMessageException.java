package com.hiklife.kvm.protocol.exceptions;

public class InvalidKVMMessageException extends Exception{
     public InvalidKVMMessageException() {
        super();
    }

    public InvalidKVMMessageException(String msg) {
        super(msg);
    }
}
