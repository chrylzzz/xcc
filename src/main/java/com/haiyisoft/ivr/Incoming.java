package com.haiyisoft.ivr;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.util.JsonFormat;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 来电
 * Created By Chryl on 2023-02-08.
 */
@Slf4j
public class Incoming {

    /**
     * 来电主方法
     */
    public void domain() {
        try {
            //获取nats连接
            Connection nc = Nats.connect(XCCConstants.NATS_URL);
            //从nats获取订阅主题
            Subscription sub = nc.subscribe(XCCConstants.XCTRL_SUBJECT);

            while (true) {
                //订阅接收的消息
                Message subMsg = sub.nextMessage(Duration.ofMillis(500000));
                log.info("订阅接收的消息：{}", subMsg);
                //订阅事件
                String eventStr = new String(subMsg.getData(), StandardCharsets.UTF_8);
                log.info("订阅事件 string data:{}", eventStr);

                JSONObject eventJson = JSONObject.parseObject(eventStr);
                log.info("订阅事件 json data:{}", eventJson);
                //event状态,Event.Channel（state=START）
                String method = eventJson.getString("method");
                log.info("event method :{}", method);
                //XNode收到呼叫后，向NATS广播来话消息（Event.Channel（state = START）），Ctrl收到后进行处理。
                switch (method) {
                    case XCCConstants.Event_Channel:
                        JSONObject params = eventJson.getJSONObject("params");
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
                            new IvrHandler().HandlerChannelEvent(nc, event);
                        }
                    case "Event.DetectedFace":
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
