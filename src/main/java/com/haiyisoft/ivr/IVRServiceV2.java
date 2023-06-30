package com.haiyisoft.ivr;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.handler.IVRHandler;
import com.haiyisoft.handler.NGDHandler;
import com.haiyisoft.handler.XCCHandler;
import com.haiyisoft.model.NGDNodeMetaData;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * V2版本:
 * 依托于百度知识库配置,注重与知识库配合使用
 * 流程不做任何复杂处理
 * <p>
 * Created By Chr.yl on 2023-02-08.
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class IVRServiceV2 {

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
                String retKey = XCCConstants.YYSR;
                String retValue = XCCConstants.WELCOME_TEXT;
                while (true) {

                    xccEvent = IVRHandler.domain(nc, channelEvent, retKey, retValue, ivrEvent, ngdEvent, callerIdNumber);

                    //处理是否挂机
                    boolean handleHangup = XCCHandler.handleSomeHangup(xccEvent, channelId);
                    if (handleHangup) {//挂机
                        log.info("挂断部分");
                        //记录已挂机的IVR对话日志
                        NGDNodeMetaData ngdNodeMetaData = new NGDNodeMetaData(retValue, "");
                        ivrEvent.getNgdNodeMetadataArray().add(ngdNodeMetaData);
                        break;
                    } else {//正常通话

                        //xcc识别数据
                        String xccRecognitionResult = xccEvent.getXccRecognitionResult();
                        //获取指令和话术
                        ngdEvent = NGDHandler.handlerNlu(xccRecognitionResult, channelId, callerIdNumber);
                        retKey = ngdEvent.getRetKey();
                        retValue = ngdEvent.getRetValue();
                        //获取ngd node metadata
                        NGDNodeMetaData ngdNodeMetaData = ngdEvent.getNgdNodeMetaData();
                        ivrEvent.getNgdNodeMetadataArray().add(ngdNodeMetaData);
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

            //挂断双方
            XCCHandler.hangup(nc, channelEvent);
            log.info("hangup this call channelId: {} ", channelId);
        }
    }

    public static void main(String[] args) {
        IVREvent ivrEvent = new IVREvent("aaaa");
        NGDNodeMetaData ngdNodeMetaData = new NGDNodeMetaData();
        ngdNodeMetaData.setAnswer("1");
        ngdNodeMetaData.setQuery("1");
        ngdNodeMetaData.setSource("1");
        ivrEvent.getNgdNodeMetadataArray().add(ngdNodeMetaData);

//        JSONArray ngdNodeMetadataArr = ivrEvent.getNgdNodeMetadataArr();
//        ngdNodeMetadataArr.add(ngdNodeMetaData);
//        ivrEvent.setNgdNodeMetadataArr(ngdNodeMetadataArr);

        System.out.println(ivrEvent);
    }

}
