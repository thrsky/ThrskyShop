package com.thrsky.shop.common;

/**
 * 枚举类
 * Created by thRShy on 2017/5/1.
 */
public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLAGAL_ARGUMENT(2,"ILLAGAL_ARGUMENT"),
    ;

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }
}
