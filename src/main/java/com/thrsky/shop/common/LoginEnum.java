package com.thrsky.shop.common;

/**
 * Created by thRShy on 2017/5/1.
 */
public enum LoginEnum {
    NO_USER(3,"用户不存在"),
    ERROR_PASSWORD(-1,"密码错误"),
    LOGIN_SUCCESS(1,"登录成功"),
    ;

    private int status;

    private String msg;

    LoginEnum(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
