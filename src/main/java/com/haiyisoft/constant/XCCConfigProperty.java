package com.haiyisoft.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    // asr engine
    private String asrEngine;
    // ngd query url
    private String ngdQueryUrl;
    // ngd query auth
    private String ngdQueryAuthorization;

}
