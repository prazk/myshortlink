package com.prazk.myshortlink.project.common.convention.exception;

import com.prazk.myshortlink.project.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.project.common.convention.errorcode.IErrorCode;

public class ServerException extends AbstractException {
    /**
     * 使用errorCode中的错误码和错误信息
     */
    public ServerException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    /**
     * 使用第一宏观错误码及自定义信息
     */
    public ServerException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    /**
     * 使用自定义信息以及错误码
     */
    public ServerException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServerException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ServerException {" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
