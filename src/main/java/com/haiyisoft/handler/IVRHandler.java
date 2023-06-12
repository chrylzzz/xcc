package com.haiyisoft.handler;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.util.XCCUtil;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Chr.yl on 2023/6/4.
 *
 * @author Chr.yl
 */
@Slf4j
public class IVRHandler {


    /**
     * IVR INVOKE
     *
     * @param nc
     * @param channelEvent
     * @param retKey
     * @param retValue
     * @return
     */
    public static XCCEvent domain(Connection nc, ChannelEvent channelEvent, String retKey, String retValue) {
        XCCEvent xccEvent;
        if (XCCConstants.YYSR.equals(retKey)) {//调用播报收音
            xccEvent = XCCHandler.detectSpeechPlayTTSNoDTMF(nc, channelEvent, retValue);
        } else if (XCCConstants.AJSR.equals(retKey)) {//调用xcc收集按键方法，多位按键
            xccEvent = XCCHandler.playAndReadDTMF(nc, channelEvent, retValue, 16);
        } else if (XCCConstants.YWAJ.equals(retKey)) {//调用xcc收集按键方法，一位按键
            xccEvent = XCCHandler.playAndReadDTMF(nc, channelEvent, retValue, 1);
        } else if (XCCConstants.RGYT.equals(retKey)) {//转人工
            /**
             * 需要设计转人工流程,不直接转
             * 转到华为座席,然后挂断
             */
            //分机
//            xccEvent = XCCHandler.bridgeExtension(nc, channelEvent, retValue);
            //外部
//            xccEvent = XCCHandler.bridgeExternalExtension(nc, channelEvent, retValue);
            //转人工
            xccEvent = XCCHandler.bridgeArtificial(nc, channelEvent, retValue);
        } else if (XCCConstants.JZLC.equals(retKey)) {//转到精准IVR
            xccEvent = XCCHandler.bridgeIVR(nc, channelEvent, retValue);
        } else {
            log.error("严格根据配置的指令开发");
            xccEvent = new XCCEvent();
        }
        return xccEvent;
    }

    /**
     * 触发转人工规则
     * ngd: 机器回复一次+1,连续两次机器回复转人工
     * xcc: 未识别一次+1,连续两次未识别转人工
     */
    public static IVREvent transferRule(IVREvent ivrEvent, ChannelEvent channelEvent, Connection nc) {
        boolean transferFlag = ivrEvent.isTransferFlag();
        if (transferFlag) {//转人工
            XCCUtil.handleTransferArtificial(nc, channelEvent, XCCConstants.ARTIFICIAL_TEXT);
        } else {
            //累加次数
            int transferTime = ivrEvent.getTransferTime();
            int newTransferTime = transferTime + 1;
            if (newTransferTime >= XCCConstants.TRANSFER_ARTIFICIAL_TIME) {
                ivrEvent.setTransferFlag(true);
                log.info("transferRule 次数 {} 已累加至 {} , 转人工 channelId : {}", XCCConstants.DEFAULT_TRANSFER_TIME, XCCConstants.TRANSFER_ARTIFICIAL_TIME, ivrEvent.getChannelId());
                XCCUtil.handleTransferArtificial(nc, channelEvent, XCCConstants.ARTIFICIAL_TEXT);
            } else {
                log.info("transferRule 已累加 channelId : {} , transferTime : {} ,newTransferTime : {}", ivrEvent.getChannelId(), transferTime, newTransferTime);
                ivrEvent.setTransferTime(newTransferTime);
            }
        }
        return ivrEvent;
    }


    /**
     * 转人工规则清零
     *
     * @param ivrEvent
     * @return
     */
    public static IVREvent transferRuleClean(IVREvent ivrEvent) {
        ivrEvent.setTransferTime(1);
        ivrEvent.setTransferFlag(false);
        log.info("transferRuleClean 已重置 channelId : {} , transferTime : {}", ivrEvent.getChannelId(), ivrEvent.getTransferTime());
        return ivrEvent;
    }


    /**
     * 获取话务数据
     *
     * @param params
     * @return
     */
    public static ChannelEvent convertParams(JSONObject params) {
        ChannelEvent event = new ChannelEvent();
        try {
//          we have to serialize the params into a string and parse it again
//          unless we can find a way to convert JsonElement to protobuf class
//            Xctrl.ChannelEvent.Builder xccEvent = Xctrl.ChannelEvent.newBuilder();
//            JsonFormat.parser().ignoringUnknownFields().merge(params.toString(), xccEvent);
//            log.info("Xctrl convert xcc event :{}", xccEvent);
//          String state = cevent.getState();


            String uuid = params.getString("uuid");
            String node_uuid = params.getString("node_uuid");
            //当前Channel的状态,如START--Event.Channel（state=START）
            String state = params.getString("state");

            event.setUuid(uuid);
            event.setNodeUuid(node_uuid);
            event.setState(state);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("convertParams 发生异常：{}", e);
        }
        return event;
    }
}
