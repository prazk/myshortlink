package com.prazk.myshortlink.common.convention.result;

import com.prazk.myshortlink.common.convention.errorcode.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 5679018624309023727L;

    private String message;
    private String code;
    private T data;

    public Result(String message, String code) {
        this.message = message;
        this.code = code;
    }

    // is 开头的 public 方法自动执行，并将返回值序列化为 JSON 字段
    public boolean isSuccess() {
        return BaseErrorCode.SUCCESS_CODE.code().equals(code);
    }
}
