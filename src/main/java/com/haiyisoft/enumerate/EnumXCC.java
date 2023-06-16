package com.haiyisoft.enumerate;

/**
 * Created by Chr.yl on 2023/6/6.
 *
 * @author Chr.yl
 */
public enum EnumXCC implements CommonXCC {

    //ngd 是否通过身份校验流程
    USER_OK("userOK", "YES"),


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
