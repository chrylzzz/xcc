package com.haiyisoft.config;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConfigProperty;
import com.haiyisoft.constant.XCCConstants;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            String hostAddress = addr.getHostAddress();
            log.warn("Local HostAddress: {} , HostName: {}", hostAddress, hostname);
            if (xccConfigProperty.isCluster()) {
                List<JSONObject> natsList = xccConfigProperty.getNatsList();
                for (JSONObject nodeBean : natsList) {
                    String node = nodeBean.getString(XCCConstants.NODE);
                    if (hostAddress.equals(node)) {
                        String natsUrl = nodeBean.getString(XCCConstants.NATS);
                        xccConfigProperty.setNatsUrl(natsUrl);
                        log.warn("装配完成 node: [{}] , nats: [{}]", node, natsUrl);
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            log.info("初始化 XCC_CONFIG_PROPERTY 失败");
        }
    }
}
