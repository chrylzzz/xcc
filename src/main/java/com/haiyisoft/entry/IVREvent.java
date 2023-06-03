package com.haiyisoft.entry;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * IVR业务实体
 * Created by Chr.yl on 2023/3/7.
 *
 * @author Chr.yl
 */
@Data
public class IVREvent {

    public IVREvent(String channelId) {
        this.channelId = channelId;
        //转人工次数:2次转人工, 0, 1, 2 : agentTime == 2 时转人工
        this.agentTime = 0;
        //是否转人工
        this.isAgent = false;
    }

    //xcc call id
    private String channelId;
    //---------------------------xcc ngd : 没一次
    //xcc返回code
//    private Integer code;
//    //xcc返回message
//    private String message;
//    //xcc识别返回结果,包括 utterance/dtmf
//    private String xccRecognitionResult;
//    //ngd返回指令
//    private String retKey;
//    //ngd返回话术
//    private String retValue;
    private XCCEvent xccEvent;
    private NGDEvent ngdEvent;
    //---------------------------
    //转人工次数:2次转人工
    private Integer agentTime;
    //是否转人工
    private boolean isAgent;
    //华为callId
    private String huaweiCallId;
    //归属地编码
    private String phoneAddressCode;
    //来电手机号码
    private String phone;
    //对话日志
    private List<JSONObject> conversation;

}
