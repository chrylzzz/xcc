package com.haiyisoft.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.XCCEvent;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * XCC工具类
 * Created By Chryl on 2023-02-08.
 **/
@Slf4j
public class XCCUtil {

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
//          Xctrl.ChannelEvent.Builder cevent = Xctrl.ChannelEvent.newBuilder();
//          JsonFormat.parser().ignoringUnknownFields().merge(params.toString(), cevent);
//          log.info("订阅事件 cevent :{}", cevent);
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

    /**
     * 获取媒体对象
     *
     * @param playType play类型
     * @param content  内容,可为text,file
     * @return JSONObject
     */
    public static JSONObject getPlayMedia(String playType, String content) {
        JSONObject media = new JSONObject();
        /**
         *  type：枚举字符串，文件类型，
         *        FILE：文件
         *        TEXT：TTS，即语音合成
         *        SSML：TTS，SSML格式支持（并非所有引擎都支持SSML）
         */
        media.put("type", playType);
        media.put("data", content);
        //引擎TTS engine,若使用xswitch配置unimrcp,则为unimrcp:profile
        media.put("engine", IVRInit.XCC_CONFIG_PROPERTY.getTtsEngine());
        //嗓音Voice-Name，由TTS引擎决定，默认为default。
        media.put("voice", IVRInit.XCC_CONFIG_PROPERTY.getTtsVoice());
        return media;
    }

    /**
     * 获取按键对象
     *
     * @param maxDigits 最大位长
     * @return
     */
    public static JSONObject getDtmf(int maxDigits) {
//        min_digits：最小位长。
//        max_digits：最大位长。
//        timeout：超时，默认5000ms。
//        digit_timeout：位间超时，默认2000ms。
//        terminators：结束符，如#。
        JSONObject dtmf = new JSONObject();
        dtmf.put("min_digits", 1);
        dtmf.put("max_digits", maxDigits);
        dtmf.put("timeout", 15000);
        dtmf.put("digit_timeout", 2000);
        dtmf.put("terminators", XCCConstants.DTMF_TERMINATORS);
        return dtmf;
    }

    /**
     * 获取语音识别对象
     *
     * @return JSONObject
     */
    public static JSONObject getSpeech() {
        JSONObject speech = new JSONObject();
        //默认传，default为docker grammar file: /usr/local/freeswitch/grammar/default.gram
//        speech.put("grammar", "default");
        speech.put("grammar", "builtin:grammar/boolean?language=zh-CN;y=1;n=2 builtin");
        //引擎ASR engine,若使用xswitch配置unimrcp,则为unimrcp:profile.
        speech.put("engine", IVRInit.XCC_CONFIG_PROPERTY.getAsrEngine());
        //禁止打断。用户讲话不会打断放音。
        speech.put("nobreak", XCCConstants.NO_BREAK);
        //正整数，未检测到语音超时，默认为5000ms
        speech.put("no_input_timeout", 5 * 1000);
        //语音超时，即如果对方讲话一直不停超时，最大只能设置成6000ms，默认为6000ms。
        speech.put("speech_timeout", 6 * 1000);
        //是否返回中间结果
        speech.put("partial_event", true);
        //默认会发送Event.DetectedData事件，如果为true则不发送。
        speech.put("disable_detected_data_event", true);
        return speech;
    }


    public void setVar(Connection nc, ChannelEvent channelEvent) {
        RequestUtil request = new RequestUtil();
        JSONObject params = new JSONObject();
        Map<String, String> data = new HashMap<>();
        data.put("disable_img_fit", "true");
        params.put("ctrl_uuid", "chryl-ivvr");
        params.put("uuid", channelEvent.getUuid());
        params.put("data", data);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
//        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.SET_VAR, params, 1000);
    }


    //获取当前通道状态
    public void getState(Connection nc, ChannelEvent channelEvent) {
        RequestUtil request = new RequestUtil();
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        params.put("uuid", channelEvent.getUuid());
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
//        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.GET_STATE, params, 10000);
    }


    //接管话务
    public static void accept(Connection nc, ChannelEvent channelEvent) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelUuid = channelEvent.getUuid();
        params.put("uuid", channelUuid);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
//        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.ACCEPT, params, 10000);
    }

    //应答
    public static XCCEvent answer(Connection nc, ChannelEvent channelEvent) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelId = channelEvent.getUuid();
        params.put("uuid", channelId);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        XCCEvent xccEvent = RequestUtil.natsRequestFutureByAnswer(nc, service, XCCConstants.ANSWER, params, 1000);
        return xccEvent;
    }

    //挂断
    public static void hangup(Connection nc, ChannelEvent channelEvent) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        params.put("uuid", channelEvent.getUuid());
        //flag integer 值为,0：挂断自己,1：挂断对方,2：挂断双方
        params.put("flag", 2);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        RequestUtil.natsRequestFutureByHangup(nc, service, XCCConstants.HANGUP, params, 3000);
    }

    /**
     * 播放text
     *
     * @param nc
     * @param channelEvent
     * @param ttsContent   内容
     */
    public static XCCEvent playTTS(Connection nc, ChannelEvent channelEvent, String ttsContent) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelId = channelEvent.getUuid();
        params.put("uuid", channelId);
        log.info("TTS播报内容为:{}", ttsContent);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
        params.put("media", media);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        XCCEvent xccEvent = RequestUtil.natsRequestFutureByPlayTTS(nc, service, XCCConstants.PLAY, params, 1000);
        return xccEvent;
    }

    /**
     * 播放file
     *
     * @param nc
     * @param channelEvent
     * @param file         file_path/png_file
     */
    public void playFILE(Connection nc, ChannelEvent channelEvent, String file) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelUuid = channelEvent.getUuid();
        params.put("uuid", channelUuid);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_FILE, file);
        params.put("media", media);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.PLAY, params, 1000);
    }

    /**
     * 语音识别,播放语音,收集语音(不收集按键)
     *
     * @param nc
     * @param channelEvent
     * @return
     */
    public static XCCEvent detectSpeechPlayTTSNoDTMF(Connection nc, ChannelEvent channelEvent, String ttsContent) {
        JSONObject params = new JSONObject();
        //ctrl_uuid:ctrl_uuid
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelId = channelEvent.getUuid();
        params.put("uuid", channelId);
        log.info("TTS播报内容为:{}", ttsContent);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
        params.put("media", media);
        //如果不需要同时检测DTMF，可以不传该参数。
//        params.put("dtmf", null);
        JSONObject speech = getSpeech();
        params.put("speech", speech);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        XCCEvent xccEvent = RequestUtil.natsRequestFutureByDetectSpeech(nc, service, XCCConstants.DETECT_SPEECH, params, 0);
        return xccEvent;
    }

    /**
     * 播报并收集按键
     *
     * @param nc
     * @param channelEvent
     * @param ttsContent   播报内容
     * @param maxDigits    最大位长
     * @return
     */
    public static XCCEvent playAndReadDTMF(Connection nc, ChannelEvent channelEvent, String ttsContent, int maxDigits) {
//        播放一个语音并获取用户按键信息，将在收到满足条件的按键后返回。
//        data：播放的媒体，可以是语音文件或TTS。
//        返回结果：
//
//        dtmf：收到的按键。
//        terminator：结束符，如果有的话。
//        本接口将在收到第一个DTMF按键后打断当前的播放。
//
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelId = channelEvent.getUuid();
        params.put("uuid", channelId);
        JSONObject dtmf = getDtmf(maxDigits);
        params.put("dtmf", dtmf);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
//        params.put("data", media);
        params.put("media", media);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        XCCEvent xccEvent = RequestUtil.natsRequestFutureByReadDTMF(nc, service, XCCConstants.READ_DTMF, params, 5000);
        return xccEvent;
    }

    /**
     * 转接
     *
     * @param nc
     * @param channelEvent
     * @param ttsContent
     * @return
     */
    public static XCCEvent bridge(Connection nc, ChannelEvent channelEvent, String ttsContent) {
/*
        {
            "jsonrpc": "2.0",
                "method": "XNode.Bridge",
                "id": "call2",
                "params": {
            "ctrl_uuid": "6e68eb16-9272-4ca6-80e2-26253ac29e25",
                    "uuid": "08a53c50-fbea-413b-a3df-08959d3030e2",
                    "destination": {
                "global_params": {},
                "call_params": [{
                    "uuid": "d3dd612f-b634-4aaa-aa25-0794bef046ad",
                            "dial_string": "user/1001"
                }]
            }
        }
        }

        */
        //正在转接人工坐席,请稍后
        XCCUtil.playTTS(nc, channelEvent, ttsContent);
        //全局参数
        JSONObject user2user = new JSONObject();
        user2user.put("queueName", "1123123");
        user2user.put("phoneNum", "11231");
        user2user.put("adsCode", "11231");
        user2user.put("huaweiCallId", "123311");
        JSONObject global_params = new JSONObject();
        global_params.put("sip_h_X-User-to-User", user2user);
        //呼叫参数
        JSONObject call_params = new JSONObject();
        call_params.put("uuid", IdGenerator.simpleUUID());
        //https://docs.xswitch.cn/xcc-api/reference/#dial-string
//        call_params.put("dial_string", "sofia/default/1002@140.143.134.19:22501");
        //生产转接
//        call_params.put("dial_string", "sofia/default/4001/10.194.31.200:5060");

        //分机使用user
        call_params.put("dial_string", "user/1001");
        //[{},{}]
        JSONArray callParamArray = new JSONArray();
        callParamArray.add(call_params);

        JSONObject destination = new JSONObject();
        destination.put("global_params", global_params);
        destination.put("call_params", callParamArray);

        JSONObject params = new JSONObject();
        //当前channel 的uuid
        String channelId = channelEvent.getUuid();
        params.put("uuid", channelId);
        params.put("ctrl_uuid", "chryl-ivvr");
        params.put("flow_control", XCCConstants.ANY);
        params.put("destination", destination);


        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        XCCEvent xccEvent = RequestUtil.natsRequestFutureByBridge(nc, service, XCCConstants.BRIDGE, params, 2000);
        return xccEvent;
    }

    /**
     * 转接分机测试
     *
     * @param nc
     * @param channelEvent
     * @param ttsContent
     * @return
     */
    public static XCCEvent bridgeExtension(Connection nc, ChannelEvent channelEvent, String ttsContent) {
        /*
        {
            "jsonrpc": "2.0",
                "method": "XNode.Bridge",
                "id": "call2",
                "params": {
            "ctrl_uuid": "6e68eb16-9272-4ca6-80e2-26253ac29e25",
                    "uuid": "08a53c50-fbea-413b-a3df-08959d3030e2",
                    "destination": {
                "global_params": {},
                "call_params": [{
                    "uuid": "d3dd612f-b634-4aaa-aa25-0794bef046ad",
                            "dial_string": "user/1001"
                }]
            }
        }
        }

        */
        //正在转接人工坐席,请稍后
        playTTS(nc, channelEvent, ttsContent);
        //全局参数
        JSONObject user2user = new JSONObject();
        user2user.put("queueName", "111");
        user2user.put("phoneNum", "111");
        user2user.put("adsCode", "111");
        user2user.put("huaweiCallId", "111");
        JSONObject global_params = new JSONObject();
        global_params.put("sip_h_X-User-to-User", user2user);
        //呼叫参数
        JSONObject call_params = new JSONObject();
        call_params.put("uuid", IdGenerator.simpleUUID());
        //https://docs.xswitch.cn/xcc-api/reference/#dial-string
//        call_params.put("dial_string", "sofia/default/1002@140.143.134.19:22501");
        //生产转接
//        call_params.put("dial_string", "sofia/default/4001/10.194.31.200:5060");
        //分机使用user
        call_params.put("dial_string", "user/1001");
        //[{},{}]
        JSONArray callParamArray = new JSONArray();
        callParamArray.add(call_params);

        JSONObject destination = new JSONObject();
        destination.put("global_params", global_params);
        destination.put("call_params", callParamArray);

        JSONObject params = new JSONObject();
        //当前channel 的uuid
        String channelId = channelEvent.getUuid();
        params.put("uuid", channelId);
        params.put("ctrl_uuid", "chryl-ivvr");
        params.put("flow_control", XCCConstants.ANY);
        params.put("destination", destination);
        String service = IVRInit.XCC_CONFIG_PROPERTY.getXnodeSubjectPrefix() + channelEvent.getNodeUuid();
        XCCEvent xccEvent = RequestUtil.natsRequestFutureByBridge(nc, service, XCCConstants.BRIDGE, params, 2000);
        return xccEvent;
    }
}
