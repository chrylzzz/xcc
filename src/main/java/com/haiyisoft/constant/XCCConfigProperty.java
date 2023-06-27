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

    // tts语速
    private String xttsS;

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


    // DTMF 输入超时
    private int dtmfNoInputTimeout;
    // DTMF 位间超时
    private int digitTimeout;

    // speech 未检测到语音超时
    private int speechNoInputTimeout;
    // speech 语音超时，即如果对方讲话一直不停超时
    private int maxSpeechTimeout;

    public void setDtmfNoInputTimeout(int dtmfNoInputTimeout) {
        this.dtmfNoInputTimeout = dtmfNoInputTimeout * 1000;
    }

    public void setDigitTimeout(int digitTimeout) {
        this.digitTimeout = digitTimeout * 1000;
    }

    public void setSpeechNoInputTimeout(int speechNoInputTimeout) {
        this.speechNoInputTimeout = speechNoInputTimeout * 1000;
    }

    public void setMaxSpeechTimeout(int maxSpeechTimeout) {
        this.maxSpeechTimeout = maxSpeechTimeout * 1000;
    }
}
