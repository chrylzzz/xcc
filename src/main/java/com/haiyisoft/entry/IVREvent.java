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
public class IVREvent implements Serializable {

    public IVREvent(String channelId) {
        this.channelId = channelId;
        //转人工次数:2次转人工, 0, 1, 2
        this.agentTime = 0;
        //是否转人工
        this.isAgent = false;
    }

    //call id
    private String channelId;
    //xcc返回code
    private Integer code;
    //xcc返回msg
    private String xccMsg;
    //ngd返回指令
    private String retKey;
    //ngd返回话术
    private String retValue;
    //转人工次数:2次转人工
    private Integer agentTime;
    //是否转人工
    private boolean isAgent;
    //华为callId
    private String huaweiCallId;
    //归属地编码
    private String phoneAds;
    //来电手机号码
    private String phone;


}
