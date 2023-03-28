package com.haiyisoft.entry;

import lombok.Data;

import java.io.Serializable;

/**
 * IVR业务实体
 * Created by Chr.yl on 2023/3/7.
 *
 * @author Chr.yl
 */
@Data
public class IVRModel implements Serializable {

    //xcc返回code
    private Integer code;
    //xcc返回msg
    private String xccMsg;
    //ngd返回指令
    private String retKey;
    //ngd返回话术
    private String retValue;


}
