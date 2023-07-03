package com.haiyisoft.ivr;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.handler.IVRHandler;
import com.haiyisoft.handler.NGDHandler;
import com.haiyisoft.handler.WebHookHandler;
import com.haiyisoft.handler.XCCHandler;
import com.haiyisoft.model.NGDNodeMetaData;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * V4版本:
 * 欢迎语在百度ngd流程配置(先调用百度)
 * <p>
 * Created By Chr.yl on 2023-02-08.
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class IVRServiceV4 {

    @Async
    public void handlerChannelEvent(Connection nc, ChannelEvent channelEvent) {
        String state = channelEvent.getState();
        if (state == null) {
            log.error("state is null ");
        } else {

            //event
            IVREvent ivrEvent = IVRHandler.convertIVREvent(channelEvent);
            XCCEvent xccEvent = new XCCEvent();
            NGDEvent ngdEvent = new NGDEvent();
            //fs call id
            String channelId = ivrEvent.getChannelId();
            //来电号码
            String callerIdNumber = ivrEvent.getCidNumber();
            log.info(" start this call channelId: {} , state :{} ", channelId, state);
            log.info(" start this call IVREvent: {}", ivrEvent);

            if (XCCConstants.Channel_START.equals(state)) {
                //开始接管,第一个指令必须是Accept或Answer
                XCCHandler.answer(nc, channelEvent);
                //
                while (true) {
                    //xcc识别数据
                    String xccRecognitionResult = xccEvent.getXccRecognitionResult();
                    //获取指令和话术
                    ngdEvent = NGDHandler.handlerNlu(xccRecognitionResult, channelId, callerIdNumber);
                    String retKey = ngdEvent.getRetKey();
                    String retValue = ngdEvent.getRetValue();
                    //
                    NGDNodeMetaData ngdNodeMetaData = ngdEvent.getNgdNodeMetaData();
                    ivrEvent.getNgdNodeMetadataArray().add(ngdNodeMetaData);

                    xccEvent = IVRHandler.domain(nc, channelEvent, retKey, retValue, ivrEvent, ngdEvent, callerIdNumber);

                    //处理是否挂机
                    boolean handleHangup = XCCHandler.handleSomeHangup(xccEvent, channelId);
                    if (handleHangup) {//挂机
                        //先存IVR对话日志,这里挂机不需要单独处理
                        log.info("挂断部分");
                        break;
                    }
                    log.info("revert ivrEvent data: {}", ivrEvent);

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

            log.info("writeCDR ivrEvent data: {}", ivrEvent);
            WebHookHandler.writeCDR(ivrEvent);

            //挂断双方
            XCCHandler.hangup(nc, channelEvent);
            log.info("hangup this call channelId: {} ", channelId);
        }
    }

}