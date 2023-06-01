package com.haiyisoft.ivr;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.util.ExceptionAdvice;
import com.haiyisoft.util.IdGenerator;
import com.haiyisoft.util.NGDUtil;
import com.haiyisoft.util.XCCUtil;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
            //使用channelId作为callId,sessionId
            String channelId = event.getUuid();
            //ivr event
            IVREvent ivrEvent = new IVREvent(channelId);
            log.info(" start this call channelId: {} , state :{} ", channelId, state);
            if (XCCConstants.Channel_START.equals(state)) {
                //开始接管,第一个指令必须是Accept或Answer
//                XCCUtil.accept(nc, event);
                XCCUtil.answer(ivrEvent, nc, event);
//                XCCUtil.playTTS(nc, event, XCCConstants.WELCOME_TEXT);
                //ngd return msg
                String ngdResMsg = "";
                //
                String retKey = XCCConstants.YYSR;
                String retValue = XCCConstants.WELCOME_TEXT;
                //xcc返回数据
                String xccResMsg = "";
                while (true) {
                    if (XCCConstants.YYSR.equals(retKey)) {//调用播报收音
                        ivrEvent = XCCUtil.detectSpeechPlayTTSNoDTMF(ivrEvent, nc, event, retValue);
                    } else if (XCCConstants.AJSR.equals(retKey)) {//调用xcc收集按键方法，多位按键
                        ivrEvent = XCCUtil.playAndReadDTMF(ivrEvent, nc, event, retValue, 18);
                    } else if (XCCConstants.YWAJ.equals(retKey)) {//调用xcc收集按键方法，一位按键
                        ivrEvent = XCCUtil.playAndReadDTMF(ivrEvent, nc, event, retValue, 1);
                    } else if (XCCConstants.RGYT.equals(retKey)) {//转人工
//                            测试bridge
                        ivrEvent = XCCUtil.bridge(ivrEvent, nc, event);
                    }
                    //handle code agent
                    boolean handleXcc = ExceptionAdvice.handleXccAgent(ivrEvent);
                    if (handleXcc) {
                        //转人工
                        break;
                    }
                    xccResMsg = ivrEvent.getXccMsg();
                    //调用百度知识库
                    ngdResMsg = NGDUtil.invokeNGD(xccResMsg, channelId);
                    //处理指令和话术
                    ivrEvent = NGDUtil.convertResText(ngdResMsg, ivrEvent);
                    //handle ngd agent
                    ivrEvent = ExceptionAdvice.handleNgdAgent(ivrEvent);
                    if (ivrEvent.isAgent()) {
                        //转人工
                        log.info("ivrEvent agent: {}", ivrEvent);
                        break;
                    }
                    retKey = ivrEvent.getRetKey();
                    retValue = ivrEvent.getRetValue();
                    log.info("ivrEvent data: {}", ivrEvent);
                }
            } else if (XCCConstants.Channel_CALLING.equals(state)) {
                log.info("Channel_CALLING this call channelId: {}", channelId);
            } else if (XCCConstants.Channel_RINGING.equals(state)) {
                log.info("Channel_RINGING this call channelId: {}", channelId);
            } else if (XCCConstants.Channel_BRIDGE.equals(state)) {
                log.info("Channel_BRIDGE this call channelId: {}", channelId);
            } else if (XCCConstants.Channel_READY.equals(state)) {
                log.info("Channel_READY this call channelId: {}", channelId);
            } else if (XCCConstants.Channel_MEDIA.equals(state)) {
                log.info("Channel_MEDIA this call channelId: {}", channelId);
            } else if (XCCConstants.CHANNEL_DESTROY.equals(state)) {
                log.info("CHANNEL_DESTROY this call channelId: {}", channelId);
            }

            //挂断双方
            XCCUtil.hangup(ivrEvent, nc, event);
            log.info("hangup this call channelId: {} ", channelId);
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

            event.setUuid(uuid);
            event.setNodeUuid(node_uuid);
            event.setState(state);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("服务器发生异常：{}", e);
        }
        return event;
    }

}
