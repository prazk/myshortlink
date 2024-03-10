package com.prazk.myshortlink.admin.common.convention.result;

import com.prazk.myshortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.admin.common.convention.exception.AbstractException;

public class Results {
    public static <T> Result<T> success(T t) {
        return new Result<>(BaseErrorCode.SUCCESS_CODE.msg(), BaseErrorCode.SUCCESS_CODE.code(), t);
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
