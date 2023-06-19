package com.haiyisoft.ivr;

import com.alibaba.fastjson2.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.handler.IVRHandler;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * IVR
 * Created By Chryl on 2023-02-08.
 */
@Slf4j
@Component
public class IVRController {

    @Autowired
    private IVRService ivrService;
    @Autowired
    private IVRServiceV2 ivrServiceV2;

    //测试xswitch能否支持线程池
    //    @Async
    public void show() {
        System.out.println(" Ivr Controller started ");
/*
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        countDownLatch.countDown();
        for (int i = 0; i < 500; i++) {
//            new XCCHandler().ivrDomain(null, null);
            ivrService.ivrDomain(null, null);
            countDownLatch.countDown();
        }
        try {
            countDownLatch.await();

        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        try {
            //获取nats连接
            Connection nc = Nats.connect(XCCConstants.NATS_URL);
            //从nats获取订阅主题
            Subscription sub = nc.subscribe(XCCConstants.XCTRL_SUBJECT);
            while (true) {
                //订阅接收的消息
                Message subMsg = sub.nextMessage(Duration.ofMillis(500000));
                for (int i = 0; i < 500; i++) {
                    ivrService.ivrDomain();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * waiting for incoming call
     */
    public void domain() {
        try {
            //获取nats连接
            Connection nc = Nats.connect(IVRInit.XCC_CONFIG_PROPERTY.getNatsUrl());
            //从nats获取订阅主题
            Subscription sub = nc.subscribe(IVRInit.XCC_CONFIG_PROPERTY.getXctrlSubject());
            log.warn("Ivr Controller started");
            while (true) {
                //订阅消息
                Message subMsg = sub.nextMessage(Duration.ofMillis(50000));
                if (subMsg == null) {
                    log.warn(" this subMsg is null ");
                } else {

                        /*
                        System.out.println("Got a new Call ...");
                        // handle this call in a new thread so we can continue to listen for other calls
//                        new IvrThread(nc, params).start();
//                        new Ivr().ivrDomain(nc, params);
//                        ivrService.ivrDomain();
                        new XCCHandler().ivrDomain();
*/

                    //订阅事件
                    //XNode收到呼叫后，向NATS广播来话消息（Event.Channel（state = START）），Ctrl收到后进行处理。
                    String eventStr = new String(subMsg.getData(), StandardCharsets.UTF_8);
                    JSONObject eventJson = JSONObject.parseObject(eventStr);
                    log.info("订阅事件 eventJson:{}", eventJson);
                    //event状态:Event.Channel（state=START）
                    String method = eventJson.getString("method");
                    if (XCCConstants.Event_Channel.equals(method)) {
                        JSONObject params = eventJson.getJSONObject("params");
                        //convert param
                        ChannelEvent event = IVRHandler.convertParams(params);
                        //asr domain
//                        ivrService.handlerChannelEvent(nc, event);
                        ivrServiceV2.handlerChannelEvent(nc, event);

                    } else if (XCCConstants.Event_DetectedFace.equals(method)) {
                        log.info("事件 event======Event-Name : {}", "Event.DetectedFace");
                    } else if (XCCConstants.Event_NativeEvent.equals(method)) {
                        log.info("事件 event======Event-Name : {}", "Event_NativeEvent");
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("IVRController 发生异常：{}", e);
        }
    }
}
