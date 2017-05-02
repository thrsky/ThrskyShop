package com.thrsky.shop.common;

/**
 * 注册情况下的枚举
 * Created by thRShy on 2017/5/2.
 */
public enum RegisterEnum {

    USERNAME_EXIST(-1,"用户名已被占用"),
    EMAIL_EXIST(-2,"邮箱已被使用"),
    PARAMETER_ERROR(-3,"参数错误"),
    REGISTER_ERROR(-3,"注册失败"),
    REGISTER_SUCESS(1,"注册成功");

    private int status;
    private String msg;

    RegisterEnum(int status, String msg) {
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
