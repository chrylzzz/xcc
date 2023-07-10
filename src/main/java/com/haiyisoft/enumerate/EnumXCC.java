package com.haiyisoft.enumerate;

/**
 * Created by Chr.yl on 2023/6/6.
 *
 * @author Chr.yl
 */
public enum EnumXCC implements CommonXCC {

    //NGD 是否通过身份校验流程
    USER_OK("userOK", "YES"),
    USER_NO("userOK", "NO"),
    //NGD错误码
    NGD_REQUEST_TO_MUCH("4000019", "ngd 用户请求过于频繁，请稍后再试"),
    NGD_BOT_TOKEN_ERROR("4002409", "ngd bot token错误"),


    //
    ;

    private String property;
    private String value;


    EnumXCC(String property, String value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public String getProperty() {
        return this.property;
    }

    @Override
    public String getValue() {
        return this.value;
    }

}
