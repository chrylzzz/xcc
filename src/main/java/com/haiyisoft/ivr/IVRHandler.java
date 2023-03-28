package com.haiyisoft.ivr;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVRModel;
import com.haiyisoft.util.IdGenerator;
import com.haiyisoft.util.NGDUtil;
import com.haiyisoft.util.XCCUtil;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By Chryl on 2023-02-08.
 */
@Slf4j
@Component
public class IVRHandler {

    /**
     * 一定要交给spring管理,哪怕是此方法被调用
     */
    @Async
    public void ivrDomain() {
        for (int i = 0; i < 500; i++) {
            for (int j = 0; j < 500; j++) {
                System.out.println(Thread.currentThread().getName() + "===" + IdGenerator.snowflakeId());
            }
        }
    }


    @Async
    public void handlerChannelEvent(Connection nc, ChannelEvent event) {
        String state = event.getState();
        if (state == null) {
            log.info("state is null ");
        } else {
            log.info("state :{} ", state);
            switch (state) {
                case XCCConstants.Channel_START:
                    //开始接管
//                XCCUtil.accept(nc, event);
                    XCCUtil.answer(nc, event);
//                XCCUtil.playTTS(nc, event, XCCConstants.WELCOME_TEXT);
                    //调用多轮
                    String ngdResMsg = "";
                    //处理指令和内容;获取多轮指令和内容
                    IVRModel ivrModel = new IVRModel();
                    String retKey = XCCConstants.YYSR;
                    //欢迎语
                    String retValue = XCCConstants.WELCOME_TEXT;
                    //xcc返回数据
                    String xccResMsg = "";
                    //使用channelId作为callId,sessionId
                    String sessionId = event.getUuid();
                    while (true) {
                        if (XCCConstants.YYSR.equals(retKey)) {//调用播报收音
                            ivrModel = XCCUtil.detectSpeechPlayTTSNoDTMF(nc, event, retValue);
                        } else if (XCCConstants.AJSR.equals(retKey)) {//调用xcc收集按键方法，多位按键
                            ivrModel = XCCUtil.playAndReadDTMF(nc, event, retValue, 18);
                        } else if (XCCConstants.YWAJ.equals(retKey)) {//调用xcc收集按键方法，一位按键
                            ivrModel = XCCUtil.playAndReadDTMF(nc, event, retValue, 1);
                        } else if (XCCConstants.RGYT.equals(retKey)) {//转人工

                        }
                        /**
                         * TODO
                         * 挂机api code为404, 此处先这样处理
                         */
                        if (ivrModel.getCode() == 404) {
                            break;
                        }
                        xccResMsg = ivrModel.getXccMsg();
                        //调用百度知识库
                        ngdResMsg = NGDUtil.invokeNGD(xccResMsg, sessionId);
                        //处理指令和话术
                        ivrModel = NGDUtil.convertResText(ngdResMsg, ivrModel);
                        retKey = ivrModel.getRetKey();
                        retValue = ivrModel.getRetValue();
                    }
//                xcc.SetVar(nc, event);
                    //播放一段音频
//                xcc.Play(nc, event);
                    //获取当前通道状态
//                xcc.GetState(nc, event);
                    //执行原生APP
//                xcc.NativeApp(nc, event);
                    //语音进入IVR
//                IvrHandler ivr = new IvrHandler();
//                ivr.Ivr(nc, event);

                case "CALLING":
                case "RINGING":
                case "BRIDGE":
                case "READY":
//                xcc.Bridge(nc, event);
                case "MEDIA":
                case XCCConstants.DESTROY:
                    System.out.println("DESTROY");
            }

            //挂断双方
            XCCUtil.hangup(nc, event);
        }
    }


    public static ChannelEvent convertParams(JSONObject params) {
        ChannelEvent event = new ChannelEvent();
        try {
//            we have to serialize the params into a string and parse it again
//            unless we can find a way to convert JsonElement to protobuf class
//        Xctrl.ChannelEvent.Builder cevent = Xctrl.ChannelEvent.newBuilder();
//        JsonFormat.parser().ignoringUnknownFields().merge(params.toString(), cevent);
//        log.info("订阅事件 cevent :{}", cevent);
//        String state = cevent.getState();


            String uuid = params.getString("uuid");
            String node_uuid = params.getString("node_uuid");
            //当前Channel的状态,如START--Event.Channel（state=START）
            String state = params.getString("state");

//        boolean answered = params.getBooleanValue("answered");
//        String billsec = params.getString("billsec");
//        String cause = params.getString("cause");
//        boolean video = params.getBooleanValue("video");


            event.setUuid(uuid);
            event.setNodeUuid(node_uuid);
            event.setState(state);

//        event.setAnswered(answered);
//        event.setBillsec(billsec);
//        event.setCause(cause);
//        event.setVideo(video);
//        event.setRingEpoch((int) result.get("ring_epoch"));
//        event.setCreateEpoch((int) result.get("create_epoch"));
//        event.setCidName((String) result.get("cid_name"));
//        event.setCidNumber((String) result.get("cid_number"));
//        event.setDestNumber((String) result.get("dest_number"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }


}
