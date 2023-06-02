package com.haiyisoft.config;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConfigProperty;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.util.IpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 配置多台xswitch
 * Created by Chr.yl on 2023/5/30.
 *
 * @author Chr.yl
 */
@Slf4j
public class XCCConfiguration {

    public static void xccConfig(XCCConfigProperty xccConfigProperty) {
        log.warn("Local HostAddress: {} ", IpUtil.INTERNET_IP);
        try {
            if (xccConfigProperty.isCluster()) {
                List<JSONObject> natsList = xccConfigProperty.getNatsList();
                for (JSONObject nodeBean : natsList) {
                    String node = nodeBean.getString(XCCConstants.NODE);
                    if (IpUtil.INTERNET_IP.equals(node)) {
                        String natsUrl = nodeBean.getString(XCCConstants.NATS);
                        xccConfigProperty.setNatsUrl(natsUrl);
                        log.warn("装配完成 node: [{}] , nats: [{}]", node, natsUrl);
                        break;
                    }
                }
            }
            log.info("初始化 XCC_CONFIG_PROPERTY 成功");
        } catch (Exception e) {
            log.error("初始化 XCC_CONFIG_PROPERTY 失败");
            e.printStackTrace();
        }
    }
}
