package com.bbnc.voice.exception;

public class InvalidArgsException extends ArgsException {

    public InvalidArgsException() {
        this("无效参数");
    }

    public InvalidArgsException(String message) {
        super(message);
    }

}
