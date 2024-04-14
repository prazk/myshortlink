package com.prazk.myshortlink.common.convention.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {


    SUCCESS_CODE("0000000", "success"),
    // ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR("A000001", "用户端错误"),
    // ========== 二级宏观错误码 用户注册错误 ==========
    USER_REGISTER_ERROR("A000100", "用户注册错误"),
    USER_SAVE_ERROR("A000101", "保存用户出错"),
    USER_HAS_LOGIN("A000102", "用户已登录"),
    USER_NOT_LOGIN("A000103", "用户未登录"),
    USER_NAME_VERIFY_ERROR("A000110", "用户名校验失败"),
    USER_NAME_EXIST_ERROR("A000111", "用户名已存在"),
    USER_NOT_EXIST_ERROR("A000112", "用户不存在"),

    // ========== 二级宏观错误码 分组错误码 ==========
    GROUP_REACH_LIMIT_ERROR("A000300", "分组达到上限"),
    GROUP_NOT_FOUND_ERROR("A000301", "分组不存在"),

    // ========== 二级宏观错误码 短链接错误码 ==========
    LINK_NOT_EXISTS_ERROR("A000400", "短链接不存在"),
    LINK_VALID_DATE_ERROR("A000401", "短链接有效期设置有误"),
    LINK_EXPIRED_ERROR("A000402", "短链接已过期"),

    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),

    SERVICE_BUSY_ERROR("B000101", "系统繁忙，请稍后再试"),

    // ========== 一级宏观错误码 调用第三方服务出错 ==========
    REMOTE_ERROR("C000001", "调用第三方服务出错");


    private final String code;

    private final String msg;

    BaseErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
