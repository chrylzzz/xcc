package com.haiyisoft.constant;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 可配置
 * XCC和NGD常量
 * Created by Chr.yl on 2023/3/28.
 *
 * @author Chr.yl
 */
@Data
@ConfigurationProperties(prefix = "xcc")
//@JsonNaming
public class XCCConfigProperty {

    // nats url
    private String natsUrl;
    // ctrl subject
    private String xctrlSubject;
    // node subject prefix
    private String xnodeSubjectPrefix;

    public void setXnodeSubjectPrefix(String xnodeSubjectPrefix) {
        this.xnodeSubjectPrefix = xnodeSubjectPrefix + ".";
    }

    // tts engine
    private String ttsEngine;
    // tts engine
    private String ttsVoice;
    // 语速
    private String prosodyRate;
    // asr engine
    private String asrEngine;
    // xcc tts no break
    private String noBreak;
    // ngd query url
    private String ngdCoreQueryUrl;
    // ngd bot token auth
    private String ngdBotToken;
    // convert solved
    private boolean convertSolved;
    // cluster true or false
    private boolean cluster;
    // parse engine xml data
    private boolean handleEngineData;
    // nats list
    private List<JSONObject> natsList;


}
