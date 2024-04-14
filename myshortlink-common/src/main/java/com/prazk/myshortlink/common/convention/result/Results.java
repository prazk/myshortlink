package com.prazk.myshortlink.common.convention.result;


import com.prazk.myshortlink.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.common.convention.exception.AbstractException;

public class Results {
    public static <T> Result<T> success(T t) {
        return new Result<>(BaseErrorCode.SUCCESS_CODE.msg(), BaseErrorCode.SUCCESS_CODE.code(), t);
    }

    public static <T> Result<T> success() {
        return new Result<>(BaseErrorCode.SUCCESS_CODE.msg(), BaseErrorCode.SUCCESS_CODE.code(), null);
    }

    /**
     * 顶级异常
     */
    public static Result<Void> failure() {
        return new Result<>(BaseErrorCode.SERVICE_ERROR.msg(), BaseErrorCode.SERVICE_ERROR.code());
    }

    public static Result<Void> failure(AbstractException e) {
        return new Result<>(e.getErrorMessage(), e.getErrorCode());
    }
}
