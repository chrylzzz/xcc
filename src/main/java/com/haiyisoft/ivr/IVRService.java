package com.haiyisoft.ivr;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.handler.IVRHandler;
import com.haiyisoft.handler.NGDHandler;
import com.haiyisoft.handler.XCCHandler;
import com.haiyisoft.util.IdGenerator;
import com.haiyisoft.util.XCCUtil;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created By Chryl on 2023-02-08.
 */
@Slf4j
@Component
@Scope(value = "prototype")
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
//            NGDEvent ngdEvent1 = new NGDEvent();
            log.info(" start this call channelId: {} , state :{} ", channelId, state);
            if (XCCConstants.Channel_START.equals(state)) {
                //开始接管,第一个指令必须是Accept或Answer
                XCCHandler.answer(nc, channelEvent);
                //
                String retKey = XCCConstants.YYSR;
                String retValue = XCCConstants.WELCOME_TEXT;
                while (true) {
                    if (XCCConstants.YYSR.equals(retKey)) {//调用播报收音
                        xccEvent = XCCHandler.detectSpeechPlayTTSNoDTMF(nc, channelEvent, retValue);
                    } else if (XCCConstants.AJSR.equals(retKey)) {//调用xcc收集按键方法，多位按键
                        xccEvent = XCCHandler.playAndReadDTMF(nc, channelEvent, retValue, 18);
                    } else if (XCCConstants.YWAJ.equals(retKey)) {//调用xcc收集按键方法，一位按键
                        xccEvent = XCCHandler.playAndReadDTMF(nc, channelEvent, retValue, 1);
                    } else if (XCCConstants.RGYT.equals(retKey)) {//转人工
                        /**
                         * 需要设计转人工流程,不直接转
                         * 转到华为座席,然后挂断
                         */
                        xccEvent = XCCHandler.bridgeExtension(nc, channelEvent, retValue);
                    }

                    //处理是否挂机
                    boolean handleHangup = XCCHandler.handleSomeHangup(xccEvent, channelId);
                    if (handleHangup) {//挂机
                        break;
                    } else {//正常通话
                        //处理是否识别
                        boolean xccInput = XCCHandler.handleXccInput(xccEvent);
                        //判断是否识别到(dtmf/speech)
                        if (xccInput) {//xcc已识别
                            //xcc识别数据
                            String xccRecognitionResult = xccEvent.getXccRecognitionResult();
                            //获取指令和话术
                            NGDEvent ngdEvent = NGDHandler.handlerNlu(xccRecognitionResult, channelId);
                            //handle ngd agent
                            boolean handleSource = NGDHandler.handleSource(ngdEvent);
                            //判断是否为机器回复
                            if (handleSource) {//system
                                //触发转人工规则
                                ivrEvent = IVRHandler.transferRule(ivrEvent, channelEvent, nc);
                                if (ivrEvent.isTransferFlag()) {
                                    //转人工
                                    break;
                                }
                            } else {
                                ivrEvent = IVRHandler.transferRuleClean(ivrEvent);
                            }
                            retKey = ngdEvent.getRetKey();
                            retValue = ngdEvent.getRetValue();
                        } else {//xcc未识别
                            //触发转人工规则
                            ivrEvent = IVRHandler.transferRule(ivrEvent, channelEvent, nc);
                            if (ivrEvent.isTransferFlag()) {
                                //转人工
                                break;
                            }
                            if (XCCConstants.DETECT_SPEECH.equals(xccEvent.getXccMethod())) {
                                retKey = "YYSR";
                                retValue = "已检测到您没说话, 您请说";
                            } else if (XCCConstants.READ_DTMF.equals(xccEvent.getXccMethod())) {
                                retKey = "AJSR";
                                retValue = "已检测到您未输入, 请输入";
                            }

                        }
                        log.info("ivrEvent data: {}", ivrEvent);
                    }

                }

                //开发记录ngd节点

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
            XCCHandler.hangup(nc, channelEvent);
            log.info("hangup this call channelId: {} ", channelId);
        }
    }


}
