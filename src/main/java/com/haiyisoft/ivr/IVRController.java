package com.haiyisoft.ivr;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.util.JsonFormat;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.util.XCCUtil;
import com.haiyisoft.xctrl.Xctrl;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * IVR
 * Created By Chryl on 2023-02-08.
 */
@Slf4j
@Component
public class IVRController {

    public static void main(String[] args) {
        new IVRController().domain();
    }

    @Autowired
    private IVRHandler ivrHandler;

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
//            Connection nc = Nats.connect(XCCConstants.NATS_URL);
//            //从nats获取订阅主题
//            Subscription sub = nc.subscribe(XCCConstants.XCTRL_SUBJECT);
            while (true) {
                //订阅接收的消息
//                Message subMsg = sub.nextMessage(Duration.ofMillis(500000));
                for (int i = 0; i < 500; i++) {
                    ivrHandler.ivrDomain(null, null);
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
            System.out.println(" Ivr Controller started ");
            //获取nats连接
            Connection nc = Nats.connect(XCCConstants.NATS_URL);
            //从nats获取订阅主题
            Subscription sub = nc.subscribe(XCCConstants.XCTRL_SUBJECT);

//            Connection nc = Nats.connect("nats://demo:demoxytdemo@nats.xswitch.cn:4222");
//            Subscription sub = nc.subscribe("cn.xswitch.ctrl");
//            Subscription sub = nc.subscribe("cn.xswitch.ctrl.>");

            while (true) {
                //订阅接收的消息
                Message subMsg = sub.nextMessage(Duration.ofMillis(500000));
//                log.info("订阅接收的消息：{}", subMsg);
                //订阅事件
                String eventStr = new String(subMsg.getData(), StandardCharsets.UTF_8);
                log.info("订阅事件 string data:{}", eventStr);

                JSONObject eventJson = JSONObject.parseObject(eventStr);
//                log.info("订阅事件 json data:{}", eventJson);
                //event状态,Event.Channel（state=START）
                String method = eventJson.getString("method");
//                log.info("event method :{}", method);
                //XNode收到呼叫后，向NATS广播来话消息（Event.Channel（state = START）），Ctrl收到后进行处理。
                switch (method) {
                    case XCCConstants.Event_Channel:
                        JSONObject params = eventJson.getJSONObject("params");

                        // we have to serialize the params into a string and parse it again
                        // unless we can find a way to convert JsonElement to protobuf class
//                        Xctrl.ChannelEvent.Builder cevent = Xctrl.ChannelEvent.newBuilder();
//                        JsonFormat.parser().ignoringUnknownFields().merge(params.toString(), cevent);
//                        log.info("订阅事件 cevent======:{}", cevent);
//                        XCCUtil.playTTS(nc, cevent, "欢迎语播报：尊敬的用户您好，请说出您要咨询的问题。");


                        String uuid = params.getString("uuid");
                        String node_uuid = params.getString("node_uuid");
                        //当前Channel的状态,如START--Event.Channel（state=START）
                        String state = params.getString("state");
                        boolean answered = params.getBooleanValue("answered");
                        String billsec = params.getString("billsec");
                        String cause = params.getString("cause");
                        boolean video = params.getBooleanValue("video");

                        ChannelEvent event = new ChannelEvent();

                        /**
                         * 待测试
                         */
//                        ChannelEvent.Builder event = ChannelEvent.newBuilder();
//                        JsonFormat.parser().ignoringUnknownFields().merge(sparams, cevent);

                        event.setUuid(uuid);
                        event.setNodeUuid(node_uuid);
                        event.setState(state);
                        event.setAnswered(answered);
                        event.setBillsec(billsec);
                        event.setCause(cause);
                        event.setVideo(video);
//                        event.setRingEpoch((int) result.get("ring_epoch"));
//                        event.setCreateEpoch((int) result.get("create_epoch"));
//                        event.setCidName((String) result.get("cid_name"));
//                        event.setCidNumber((String) result.get("cid_number"));
//                        event.setDestNumber((String) result.get("dest_number"));

                        if (state != null) {
                            new IVRHandler().HandlerChannelEvent(nc, event);
                        }
                    case XCCConstants.Event_DetectedFace:
                        log.info("事件 event======:{}", "Event.DetectedFace");
                    case XCCConstants.Event_NativeEvent:
                        log.info("事件 event======:{}", "Event.NativeEvent");
//                        XCCUtil.playTTS(nc,ChannelEvent);

                }
/*
                if (state != null) {
                    if (CallConst.Channel_START.equals(state)) {
                        System.out.println("Got a new Call ...");
                        // handle this call in a new thread so we can continue to listen for other calls
                        new IvrThread(nc, params).start();
                        new Ivr().ivrDomain(nc, params);
                    }
                }
                */

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
