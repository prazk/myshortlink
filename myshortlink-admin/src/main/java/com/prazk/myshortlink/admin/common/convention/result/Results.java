package com.prazk.myshortlink.admin.common.convention.result;

import com.prazk.myshortlink.admin.common.convention.errorcode.BaseErrorCode;

public class Results {
    public static <T> Result<T> success(T t) {
        return new Result<>(BaseErrorCode.SUCCESS_CODE.msg(), BaseErrorCode.SUCCESS_CODE.code(), t);
    }
    public static <T> Result<T> failure(BaseErrorCode errorCode) {
        return new Result<>(errorCode.msg(), errorCode.code());
    }
}
