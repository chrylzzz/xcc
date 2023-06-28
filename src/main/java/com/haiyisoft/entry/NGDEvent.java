package com.haiyisoft.entry;

import com.alibaba.fastjson2.JSONArray;
import com.haiyisoft.model.NGDNodeMetaData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每次调用NGD接收业务实体
 * Created by Chr.yl on 2023/6/3.
 *
 * @author Chr.yl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NGDEvent {

    //sessionId == callId == xcc uuid == channelId == channel uuid
    private String sessionId;
    //ngd返回code
    private Integer code;
    //ngd返回msg
    private String msg;
    // answer 来源
    private String source;
    //ngd返回的数据, 此字段根据百度api获取的最优answer
    private String answer;
    //ngd返回solved 是否解决
    private boolean solved;

    public NGDEvent(String sessionId, Integer code, String msg, String source, String answer, boolean solved) {
        this.sessionId = sessionId;
        this.code = code;
        this.msg = msg;
        this.source = source;
        this.answer = answer;
        this.solved = solved;
    }

    //对话日志
    private JSONArray conversation;
    //记录完整的 ngd 节点
    private NGDNodeMetaData ngdNodeMetaData;

    //uid为用户编号
    private String uid;
    //是否通过身份验证流程,true通过 : userOk is true: NGD 已校验完成身份验证,可对SIP HEADER处理
    private boolean userOk;


    //----------------N G D交互标识
    //播报指令
    private String retKey;
    //播报话术
    private String retValue;
    //----------------N G D交互标识


}
