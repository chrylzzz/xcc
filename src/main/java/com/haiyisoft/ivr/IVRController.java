package com.haiyisoft.ivr;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private IVRHandler ivrHandler;

    //测试xswitch能否支持线程池
    //    @Async
    public void show() {
        System.out.println(" Ivr Controller started ");
/*
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        countDownLatch.countDown();
        for (int i = 0; i < 500; i++) {
//            new IVRHandler().ivrDomain(null, null);
            ivrHandler.ivrDomain(null, null);
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
                    ivrHandler.ivrDomain();
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
            log.warn(" Ivr Controller started ");
            //获取nats连接
            Connection nc = Nats.connect(IVRInit.XCC_CONFIG_PROPERTY.getNatsUrl());
            //从nats获取订阅主题
            Subscription sub = nc.subscribe(IVRInit.XCC_CONFIG_PROPERTY.getXctrlSubject());
            while (true) {
                try {
                    //订阅接收的消息
                    Message subMsg = sub.nextMessage(Duration.ofMillis(50000));
//                log.info("订阅接收的消息：{}", subMsg);
                    if (subMsg == null) {
                        log.info("this subMsg is null ,{}", subMsg);
                    } else {


                        /*
                        System.out.println("Got a new Call ...");
                        // handle this call in a new thread so we can continue to listen for other calls
//                        new IvrThread(nc, params).start();
//                        new Ivr().ivrDomain(nc, params);
//                        ivrHandler.ivrDomain();
                        new IVRHandler().ivrDomain();
*/


                        log.info(" subMsg ,{}", subMsg);
                        //订阅事件
                        String eventStr = new String(subMsg.getData(), StandardCharsets.UTF_8);
//                        log.info(" eventStr data:{}", eventStr);

                        JSONObject eventJson = JSONObject.parseObject(eventStr);
//                log.info("订阅事件 json data:{}", eventJson);
                        //event状态,Event.Channel（state=START）
                        String method = eventJson.getString("method");
//                log.info("event method :{}", method);
                        //XNode收到呼叫后，向NATS广播来话消息（Event.Channel（state = START）），Ctrl收到后进行处理。
                        switch (method) {
                            case XCCConstants.Event_Channel:
                                JSONObject params = eventJson.getJSONObject("params");
                                //convert param
                                ChannelEvent event = IVRHandler.convertParams(params);
                                //asr domain
                                ivrHandler.handlerChannelEvent(nc, event);
//                                new IVRHandler().handlerChannelEvent(nc, event);
                            case XCCConstants.Event_DetectedFace:
                                log.info("事件 event======:{}", "Event.DetectedFace");
                            case XCCConstants.Event_NativeEvent:
                                log.info("事件 event======:{}", "Event.NativeEvent");
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
