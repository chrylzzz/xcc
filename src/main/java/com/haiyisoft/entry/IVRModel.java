package com.haiyisoft.entry;

import java.io.Serializable;

/**
 * IVR业务实体
 * Created by Chr.yl on 2023/3/7.
 *
 * @author Chr.yl
 */
public class IVRModel implements Serializable {
    private static final long serialVersionUID = 7055115971236055547L;

    //xcc返回code
    private Integer code;
    //xcc返回msg
    private String xccMsg;
    //ngd返回指令
    private String retKey;
    //ngd返回话术
    private String retValue;


    public IVRModel() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getXccMsg() {
        return xccMsg;
    }

    public void setXccMsg(String xccMsg) {
        this.xccMsg = xccMsg;
    }

    public String getRetKey() {
        return retKey;
    }

    public void setRetKey(String retKey) {
        this.retKey = retKey;
    }

    public String getRetValue() {
        return retValue;
    }

    public void setRetValue(String retValue) {
        this.retValue = retValue;
    }
}
