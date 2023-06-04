package com.haiyisoft.ivr;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.handler.IVRHandler;
import com.haiyisoft.handler.NGDHandler;
import com.haiyisoft.util.IdGenerator;
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
public class IVRService {

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
    public void handlerChannelEvent(Connection nc, ChannelEvent channelEvent) {
        String state = channelEvent.getState();
        if (state == null) {
            log.error("state is null ");
        } else {
            //使用channelId作为callId,sessionId
            String channelId = channelEvent.getUuid();
            //ivr event
            IVREvent ivrEvent = new IVREvent(channelId);
            XCCEvent xccEvent = new XCCEvent();
            NGDEvent ngdEvent = new NGDEvent();
            log.info(" start this call channelId: {} , state :{} ", channelId, state);
            if (XCCConstants.Channel_START.equals(state)) {
                //开始接管,第一个指令必须是Accept或Answer
                XCCUtil.answer(nc, channelEvent);
                //ngd return msg
                String xccRecognitionResult = "";
                //
                String retKey = XCCConstants.YYSR;
                String retValue = XCCConstants.WELCOME_TEXT;
                //xcc返回数据
                String xccResMsg = "";
                while (true) {
                    if (XCCConstants.YYSR.equals(retKey)) {//调用播报收音
                        xccEvent = XCCUtil.detectSpeechPlayTTSNoDTMF(nc, channelEvent, retValue);
                    } else if (XCCConstants.AJSR.equals(retKey)) {//调用xcc收集按键方法，多位按键
                        xccEvent = XCCUtil.playAndReadDTMF(nc, channelEvent, retValue, 18);
                    } else if (XCCConstants.YWAJ.equals(retKey)) {//调用xcc收集按键方法，一位按键
                        xccEvent = XCCUtil.playAndReadDTMF(nc, channelEvent, retValue, 1);
                    } else if (XCCConstants.RGYT.equals(retKey)) {//转人工
                        /**
                         * 转到华为座席,然后挂断
                         */
                        xccEvent = XCCUtil.bridgeExtension(nc, channelEvent, retValue);
                    }
                    /**
                     * 处理xcc 返回的code,包括no_input,异常的
                     */
                    //handle code agent
                    boolean handleXcc = IVRHandler.handleXccAgent(xccEvent, ivrEvent, channelEvent, nc);
                    if (handleXcc) {//xcc处理失败
                        //转人工
                        break;
//                        continue;
                    } else {//xcc 处理正常

                    }


                    //xcc识别数据
                    xccRecognitionResult = xccEvent.getXccRecognitionResult();
                    //获取指令和话术
                    ngdEvent = NGDHandler.handlerNlu(xccRecognitionResult, channelId);
                    //handle ngd agent
//                        ngdEvent = IVRHandler.handleNgdAgent(ngdEvent);
                    /**
                     * 处理ngd 返回 包括 不理解处理,ngd不理解次数
                     */
//                        if (ivrEvent.isAgent()) {
//                            //转人工
//                            log.info("ivrEvent agent: {}", ivrEvent);
//                            break;
//                        }
                    retKey = ngdEvent.getRetKey();
                    retValue = ngdEvent.getRetValue();
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
            XCCUtil.hangup(nc, channelEvent);
            log.info("hangup this call channelId: {} ", channelId);
        }
    }


}
