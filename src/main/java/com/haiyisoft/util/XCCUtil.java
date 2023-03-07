package com.haiyisoft.util;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVRModel;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * XCC工具类
 * Created By Chryl on 2023-02-08.
 **/
@Slf4j
public class XCCUtil {


    /**
     * 声明nats连接
     *
     * @throws IOException          IOException
     * @throws InterruptedException 中断异常
     */
    public Connection getConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(XCCConstants.NATS_URL)
                // Set a user and plain text password
                .userInfo("myname", "password")
                .build();
        return Nats.connect(options);
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
        //TTS引擎
        media.put("engine", XCCConstants.TTS_ENGINE);
        //嗓音，由TTS引擎决定，默认为default。
        media.put("voice", "default");
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
        dtmf.put("digit_timeout", 20000);
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
//        speech.put("grammar", "default");
        //ASR引擎
        speech.put("engine", XCCConstants.ASR_ENGINE);
        //禁止打断。用户讲话不会打断放音。
        speech.put("nobreak", XCCConstants.NO_BREAK);
        //正整数，未检测到语音超时，默认为5000ms
        speech.put("no_input_timeout", 10 * 1000);
        //语音超时，即如果对方讲话一直不停超时，最大只能设置成6000ms，默认为6000ms。
        speech.put("speech_timeout", 15 * 1000);
        //是否返回中间结果
        speech.put("partial_event", false);
        //默认会发送Event.DetectedData事件，如果为true则不发送。
        speech.put("disable_detected_data_event", true);
        return speech;
    }


    public void setVar(Connection nc, ChannelEvent event) {
        RequestUtil request = new RequestUtil();
        JSONObject params = new JSONObject();
        Map<String, String> data = new HashMap<>();
        data.put("disable_img_fit", "true");
        params.put("ctrl_uuid", "chryl-ivvr");
        params.put("uuid", event.getUuid());
        params.put("data", data);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.SET_VAR, params, 1000);
    }


    //获取当前通道状态
    public void getState(Connection nc, ChannelEvent event) {
        RequestUtil request = new RequestUtil();
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        params.put("uuid", event.getUuid());
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.GET_STATE, params, 10000);
    }


    //接管话务
    public static void accept(Connection nc, ChannelEvent event) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelUuid = event.getUuid();
        params.put("uuid", channelUuid);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.ACCEPT, params, 10000);
    }

    //应答
    public static void answer(Connection nc, ChannelEvent event) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelId = event.getUuid();
        params.put("uuid", channelId);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.ANSWER, params, 10000);
    }

    //挂断
    public static void hangup(Connection nc, ChannelEvent event) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        params.put("uuid", event.getUuid());
        //flag integer 值为,0：挂断自己,1：挂断对方,2：挂断双方
        params.put("flag", 2);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.HANGUP, params, 1000);
    }

    /**
     * 播放text
     *
     * @param nc
     * @param event
     * @param ttsContent 内容
     */
    public static void playTTS(Connection nc, ChannelEvent event, String ttsContent) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelId = event.getUuid();
        params.put("uuid", channelId);
        log.info("TTS播报内容为:{}", ttsContent);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
        params.put("media", media);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.PLAY, params, 10000);
    }


    /**
     * 播放file
     *
     * @param nc
     * @param event
     * @param file  file_path/png_file
     */
    public void playFILE(Connection nc, ChannelEvent event, String file) {
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelUuid = event.getUuid();
        params.put("uuid", channelUuid);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_FILE, file);
        params.put("media", media);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        RequestUtil.natsRequestTimeOut(nc, service, XCCConstants.PLAY, params, 1000);
    }

    /**
     * 语音识别,播放语音,收集语音(不收集按键)
     *
     * @param nc
     * @param event
     * @return
     */
    public static IVRModel detectSpeechPlayTTSNoDTMF(Connection nc, ChannelEvent event, String ttsContent) {
        Map<String, String> xccMap = new HashMap<>();
        JSONObject params = new JSONObject();
        //ctrl_uuid:ctrl_uuid
        params.put("ctrl_uuid", "chryl-ivvr");
        //当前channel 的uuid
        String channelId = event.getUuid();
        params.put("uuid", channelId);
        log.info("TTS播报内容为:{}", ttsContent);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
        params.put("media", media);
        //如果不需要同时检测DTMF，可以不传该参数。
//        params.put("dtmf", null);
        JSONObject speech = getSpeech();
        params.put("speech", speech);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();
        IVRModel ivrModel = RequestUtil.natsRequestFutureByDetectSpeech(nc, service, XCCConstants.DETECT_SPEECH, params, 10000);
        return ivrModel;
    }

    /**
     * 播报并收集按键
     *
     * @param nc
     * @param event
     * @param ttsContent 播报内容
     * @param maxDigits  最大位长
     */
    public static IVRModel playAndReadDTMF(Connection nc, ChannelEvent event, String ttsContent, int maxDigits) {
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
        String channelId = event.getUuid();
        params.put("uuid", channelId);
        JSONObject dtmf = getDtmf(maxDigits);
        params.put("dtmf", dtmf);
        JSONObject media = getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
//        params.put("data", media);
        params.put("media", media);
        String service = XCCConstants.XNODE_SUBJECT_PREFIX + event.getNodeUuid();

        IVRModel ivrModel = RequestUtil.natsRequestFutureByReadDTMF(nc, service, XCCConstants.READ_DTMF, params, 10000);
        return ivrModel;
    }

}
