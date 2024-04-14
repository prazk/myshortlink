package com.prazk.myshortlink.common.convention.exception;


import com.prazk.myshortlink.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.common.convention.errorcode.IErrorCode;

/**
 * ClientException 设计：提供多个构造方法用于不同场景
 */
public class ClientException extends AbstractException {
    /**
     * 使用errorCode中的错误码和错误信息
     */
    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    /**
     * 使用第一宏观错误码及自定义信息
     */
    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    /**
     * 使用自定义信息以及错误码
     */
    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ClientException {" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
