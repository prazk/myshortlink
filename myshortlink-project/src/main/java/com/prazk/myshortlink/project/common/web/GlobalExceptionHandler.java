package com.prazk.myshortlink.project.common.web;

import com.prazk.myshortlink.project.common.convention.exception.AbstractException;
import com.prazk.myshortlink.project.common.convention.result.Result;
import com.prazk.myshortlink.project.common.convention.result.Results;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public Result<Void> ThrowableExceptionHandler(Throwable e) {
        return Results.failure();
    }

    @ExceptionHandler(AbstractException.class)
    public Result<Void> AbstractExceptionHandler(AbstractException e) {
        return Results.failure(e);
    }

}
