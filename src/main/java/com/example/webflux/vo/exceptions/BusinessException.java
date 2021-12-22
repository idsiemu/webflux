package com.example.webflux.vo.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BusinessException extends RuntimeException implements AutoCloseable {

    private String msg;

    private String errCode;

    private HttpStatus errHttpStatus = HttpStatus.BAD_REQUEST;

    public BusinessException(){}

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String msg, HttpStatus httpStatus, String errCode){
        this.msg = msg;
        this.errHttpStatus = httpStatus;
        this.errCode = errCode;
    }

    @Override
    public void close() throws Exception {

    }
}