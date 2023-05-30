package com.haiyisoft.config;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConfigProperty;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
                JSONObject natsMap = xccConfigProperty.getNatsMap();
                String node = natsMap.getString("node-1");
                String natsUrl = "";
                if (hostAddress.equals(node)) {
                    natsUrl = natsMap.getString("nats-1");
                } else {
//                    String node2 = natsMap.getString("node-2");
                    natsUrl = natsMap.getString("nats-2");
                }
                log.warn("加载配置: {}", natsUrl);
                xccConfigProperty.setNatsUrl(natsUrl);
            }
            log.info("初始化 XCC_CONFIG_PROPERTY 成功");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            log.info("初始化 XCC_CONFIG_PROPERTY 失败");
        }
    }
}
