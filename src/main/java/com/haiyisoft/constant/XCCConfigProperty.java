package com.haiyisoft.constant;

import com.alibaba.fastjson.JSONObject;
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
public class XCCConfigProperty {

    // nats url
    private String natsUrl;
    // ctrl subject
    private String xctrlSubject;
    // node subject prefix
    private String xnodeSubjectPrefix;
    // tts engine
    private String ttsEngine;
    // tts engine
    private String ttsVoice;
    // asr engine
    private String asrEngine;
    // ngd query url
    private String ngdQueryUrl;
    // ngd query auth
    private String ngdQueryAuthorization;
    // cluster true or false
    private boolean cluster;
    // nats list
    private List<JSONObject> natsList;


}
