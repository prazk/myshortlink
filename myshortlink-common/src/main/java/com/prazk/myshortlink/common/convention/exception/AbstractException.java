package com.prazk.myshortlink.common.convention.exception;

import com.prazk.myshortlink.common.convention.errorcode.IErrorCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Getter
public abstract class AbstractException extends RuntimeException {
    // 抽象类属性：默认是 public static final 的
    String errorCode;
    String errorMessage;

    public AbstractException(String msg, Throwable throwable, IErrorCode errorCode) {
        super(msg, throwable);
        this.errorCode = errorCode.code();
        // 如果传入的msg有长度，则展示msg，否则展示errorCode的msg
        this.errorMessage = Optional.ofNullable(StringUtils.hasLength(msg) ? msg : null).orElse(errorCode.message());
    }
}
