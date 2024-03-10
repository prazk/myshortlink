package com.prazk.myshortlink.admin.common.convention.result;

public class Results {
    public static <T> Result<T> success(T t) {
        return new Result<>("success", "0", t);
    }
}
