package com.haiyisoft.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * XCC工具类
 * Created By Chryl on 2023-02-08.
 **/
@Slf4j
public class XCCUtil {


    String service = "cn.xswitch.node.test";
    String natsUrl = "nats://hy:h8klu6bRwW@nats.xswitch.cn:4222";
    //    String natsUrl = "nats://172.20.200.7:4222";
    String dialStr = "sofia/public/sip401007@xyt.xswitch.cn:18880;transport=tcp";

    /**
     * 声明nats连接
     *
     * @throws IOException          IOException
     * @throws InterruptedException 中断异常
     */
    public Connection getConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(natsUrl)
                // Set a user and plain text password
                .userInfo("myname", "password")
                .build();
        return Nats.connect(options);
    }

    /**
     * 获取rpc对象
     *
     * @param method 方法名
     * @param id     id
     * @return JSONObject
     */
//    public JSONObject getRpc(String method, String id) {
//        JSONObject rpc = new JSONObject();
//        rpc.put("jsonrpc", "2.0");
//        rpc.put("id", id);
//        rpc.put("method", "XNode." + method);
//
//        return rpc;
//    }

    /**
     * 外呼
     *
     * @param uuid
     * @return
     */
    public JSONObject dial(Connection nc, String id, String uuid, String tel) throws IOException,
            InterruptedException {
        JSONObject rpc = RequestUtil.getJsonRpc("Dial", id);

        JSONObject globalParams = new JSONObject();
        globalParams.put("ignore_early_media", "true");
        JSONObject destination = new JSONObject();
        destination.put("global_params", globalParams);
        JSONObject callParam = new JSONObject();
        callParam.put("uuid", uuid);
        callParam.put("dial_string", new StringBuilder("sofia/public/sip40").append(tel).append("@xyt.xswitch.cn:18880;transport=tcp"));
        //        callParam.put("dial_string", "user/1007");
        JSONObject cParams = new JSONObject();
        cParams.put("absolute_codec_string", "PCMA");
        callParam.put("params", cParams);
        JSONArray callParams = new JSONArray();
        callParams.add(callParam);
        destination.put("call_params", callParams);
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "ctrl_uuid");
        params.put("destination", destination);
        rpc.put("params", params);
        log.info("dial:{}", rpc);

        JSONObject response = new JSONObject();
        Message msg = nc.request(service, rpc.toString().getBytes(StandardCharsets.UTF_8), Duration.ofSeconds(20));
        if (msg != null) {
            response = JSONObject.parseObject(new String(msg.getData(), StandardCharsets.UTF_8));
            log.info("外呼结果：{}", response.toJSONString());
        } else {
            JSONObject result = new JSONObject();
            result.put("code", 600);
            response.put("result", result);
        }

        return response;
    }

    /**
     * tts播报
     *
     * @param uuid
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public JSONObject play(Connection nc, String id, String uuid, String text) throws IOException, InterruptedException,
            ExecutionException, TimeoutException {
        JSONObject rpc = RequestUtil.getJsonRpc("Play", id);
        JSONObject params = new JSONObject();
        params.put("uuid", uuid);
        params.put("ctrl_uuid", "ctrl_uuid");
        params.put("media", this.getPlayMedia(XCCConstants.PLAY_TTS, text));
        rpc.put("params", params);
        StringWriter request = new StringWriter();
        rpc.writeJSONString(request);
        log.info(request.toString());

        Future<Message> incoming = nc.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
        Message msg = incoming.get(5000, TimeUnit.MILLISECONDS);
        String callResponse = new String(msg.getData(), StandardCharsets.UTF_8);
        log.info(callResponse);

        return JSONObject.parseObject(callResponse);
    }

    /**
     * 播放一个语音，并进行asr识别、收集按键信息
     *
     * @param id
     * @param uuid
     * @param text
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public String detectSpeech(Connection nc, String id, String uuid, String text) throws ExecutionException,
            InterruptedException, TimeoutException {
        JSONObject rpc = RequestUtil.getJsonRpc("DetectSpeech", id);
        JSONObject params = new JSONObject();
        params.put("uuid", uuid);
        params.put("ctrl_uuid", "ctrl_uuid");
        params.put("dtmf", this.getDtmf18());
        params.put("speech", this.getSpeech());
        params.put("media", this.getPlayMedia(XCCConstants.PLAY_TTS, text));
        rpc.put("params", params);

        Future<Message> asr = nc.request(service, rpc.toString().getBytes(StandardCharsets.UTF_8));
        Message asrMsg = asr.get(50000, TimeUnit.MILLISECONDS);
        String callResponse = new String(asrMsg.getData(), StandardCharsets.UTF_8);
        log.info(callResponse);

        JSONObject result = JSONObject.parseObject(callResponse).getJSONObject("result");
        String asrText = "";
        if (result.getInteger("code") == 200) {
            asrText = result.getJSONObject("data").getString("text");
        } else if (result.getInteger("code") == 404) {
            asrText = "error";
        }
        return asrText;
    }

    /**
     * 桥接
     *
     * @param nc   nats连接
     * @param id   rpc的id
     * @param uuid 通话唯一标识
     * @throws ExecutionException   ExecutionException
     * @throws InterruptedException 中断异常
     * @throws TimeoutException     超时异常
     */
    public void bridge(Connection nc, String id, String uuid) throws ExecutionException, InterruptedException, TimeoutException {
        JSONObject rpc = RequestUtil.getJsonRpc("Bridge", id);
        JSONObject params = new JSONObject();
        params.put("uuid", uuid);
        params.put("ctrl_uuid", "ctrl_uuid");
        params.put("flow_control", "ANY");
        JSONObject destination = new JSONObject();
        JSONObject callParam = new JSONObject();
        JSONArray callArray = new JSONArray();
        callParam.put("uuid", IdGenerator.snowflakeId());
        callParam.put("dial_string", new StringBuilder("sofia/public/sip40").append("15553551792").append("@xyt" +
                ".xswitch.cn:18880;transport=tcp"));
        JSONObject cParams = new JSONObject();
        cParams.put("absolute_codec_string", "PCMA");
        callParam.put("params", cParams);
        callArray.add(callParam);
        destination.put("call_params", callArray);
        destination.put("global_params", new JSONObject());
        params.put("destination", destination);
        rpc.put("params", params);
        log.info("Bridge入参：{}", rpc);

        Future<Message> incoming = nc.request(service, rpc.toString().getBytes(StandardCharsets.UTF_8));
        Message msg = incoming.get(50000, TimeUnit.MILLISECONDS);
        String callResponse = new String(msg.getData(), StandardCharsets.UTF_8);
        JSONObject jsonObject = JSONObject.parseObject(callResponse);
        log.info("Bridge：{}", jsonObject);
    }

    /**
     * 挂机
     *
     * @param nc
     * @param id
     * @param uuid
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void hangup(Connection nc, String id, String uuid) throws ExecutionException, InterruptedException, TimeoutException {
        JSONObject rpc = RequestUtil.getJsonRpc("Hangup", id);
        JSONObject params = new JSONObject();
        params.put("uuid", uuid);
        rpc.put("params", params);

        Future<Message> incoming = nc.request(service, rpc.toString().getBytes(StandardCharsets.UTF_8));
        Message msg = incoming.get(5000, TimeUnit.MILLISECONDS);
        String callResponse = new String(msg.getData(), StandardCharsets.UTF_8);
        JSONObject jsonObject = JSONObject.parseObject(callResponse);
        log.info("HangUp：{}", jsonObject);
    }


    //=====================================================================================================
    static String subject_prefix = "cn.xswitch.";
    static String engine = "ali";
    static String voice = "default";
    //    static String media_path = "/usr/local/freeswitch/storage/5g";
//    static String png_file = "{png_ms=3000,tts_engine=" + engine + ",tts_voice=" + voice + ",text=\"欢迎进入语音识别服务，请说出你想办理的业务\"}" + media_path + "/5g-1.png";


    /**
     * 获取媒体对象
     *
     * @param playType play类型
     * @param content  内容,可为text,file
     * @return JSONObject
     */
    public JSONObject getPlayMedia(String playType, String content) {
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
        media.put("engine", "ali");
        //嗓音，由TTS引擎决定，默认为default。
        media.put("voice", "default");
        return media;
    }

    /**
     * 获取按键对象,一位按键
     *
     * @return JSONObject
     */
    public JSONObject getDtmfOne() {
        JSONObject dtmf = new JSONObject();
        dtmf.put("min_digits", 1);
        dtmf.put("max_digits", 1);
        dtmf.put("timeout", 1500);
        dtmf.put("digit_timeout", 2000);
        dtmf.put("terminators", "#");
        return dtmf;
    }

    /**
     * 获取按键对象,多位按键
     *
     * @return JSONObject
     */
    public JSONObject getDtmf18() {
        JSONObject dtmf = new JSONObject();
        dtmf.put("min_digits", 1);
        dtmf.put("max_digits", 18);
        dtmf.put("timeout", 1500);
        dtmf.put("digit_timeout", 2000);
        dtmf.put("terminators", "#");
        return dtmf;
    }

    /**
     * 获取语音识别对象
     *
     * @return JSONObject
     */
    public JSONObject getSpeech() {
        JSONObject speech = new JSONObject();
//        speech.put("grammar", "default");
        //ASR引擎
        speech.put("engine", engine);
        //禁止打断。用户讲话不会打断放音。
        speech.put("nobreak", XCCConstants.NO_BREAK);
        //正整数，未检测到语音超时，默认为5000ms
        speech.put("no_input_timeout", 5 * 1000);
        //语音超时，即如果对方讲话一直不停超时，最大只能设置成6000ms，默认为6000ms。
        speech.put("speech_timeout", 8 * 1000);
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
        params.put("ctrl_uuid", "ivvr");
        params.put("uuid", event.getUuid());
        params.put("data", data);
        request.natsRequest(subject_prefix + "node." + event.getNodeUuid(), "Xnode.SetVar", params, nc);
    }


    public void getState(Connection nc, ChannelEvent event) {
        RequestUtil request = new RequestUtil();
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "ivvr");
        params.put("uuid", event.getUuid());
        request.natsRequest(subject_prefix + "node." + event.getNodeUuid(), "Xnode.GetState", params, nc);
    }

    //TODO 应答
    public void answer(Connection nc, ChannelEvent event) {
        RequestUtil request = new RequestUtil();
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "ivvr");
        params.put("uuid", event.getUuid());
        request.natsRequest(subject_prefix + "node." + event.getNodeUuid(), "Xnode.Answer", params, nc);
    }

    /**
     * 播放text
     *
     * @param nc
     * @param event
     * @param ttsContent 内容
     */
    public void playTTS(Connection nc, ChannelEvent event, String ttsContent) {
        RequestUtil request = new RequestUtil();
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "ivvr");
        params.put("uuid", event.getUuid());
        JSONObject media = this.getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
        params.put("media", media);
        RequestUtil.natsRequest(subject_prefix + "node." + event.getNodeUuid(), "Xnode.Play", params, nc);
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
        params.put("ctrl_uuid", "ivvr");
        params.put("uuid", event.getUuid());
        JSONObject media = this.getPlayMedia(XCCConstants.PLAY_FILE, file);
        params.put("media", media);
        RequestUtil.natsRequest(subject_prefix + "node." + event.getNodeUuid(), "Xnode.Play", params, nc);
    }

    /**
     * 语音识别,播放语音,收集语音(不收集按键)
     *
     * @param nc
     * @param event
     * @return
     */
    public String detectSpeechPlayTTSNoDTMF(Connection nc, ChannelEvent event, String ttsContent) {
        JSONObject params = new JSONObject();
        //ctrl_uuid:ctrl_uuid
        params.put("ctrl_uuid", "ivvr");
        //uuid：如果请求中有uuid，则结果中也有uuid，代表当前的Channel UUID。
        //Channel API：作用于一个Channel，必须有一个uuid参数，uuid即为当前Channel的uuid。
        params.put("uuid", event.getUuid());
        JSONObject media = this.getPlayMedia(XCCConstants.PLAY_TTS, ttsContent);
        params.put("media", media);
        //如果不需要同时检测DTMF，可以不传该参数。
//        params.put("dtmf", null);
        JSONObject speech = this.getSpeech();
        params.put("speech", speech);
        Message msg = RequestUtil.natsRequestTimeOut(subject_prefix + "node." + event.getNodeUuid(), "Xnode.DetectSpeech", params, nc, 10);
        String str = new String(msg.getData(), StandardCharsets.UTF_8);
        return str;
    }

    /**
     * 多位按键并播放
     *
     * @param nc
     * @param event
     * @param ttsContent 播报内容
     */
    public String readDTMF(Connection nc, ChannelEvent event, String ttsContent) {
//        播放一个语音并获取用户按键信息，将在收到满足条件的按键后返回。
//
//        min_digits：最小位长。
//        max_digits：最大位长。
//        timeout：超时，默认5000ms。
//        digit_timeout：位间超时，默认2000ms。
//        terminators：结束符，如#。
//        data：播放的媒体，可以是语音文件或TTS。
//        返回结果：
//
//        dtmf：收到的按键。
//        terminator：结束符，如果有的话。
//        本接口将在收到第一个DTMF按键后打断当前的播放。
//
        JSONObject params = new JSONObject();
        params.put("ctrl_uuid", "ivvr");
        params.put("uuid", event.getUuid());
        params.put("dtmf", this.getDtmf18());
        Message msg = RequestUtil.natsRequestTimeOut(subject_prefix + "node." + event.getNodeUuid(), "Xnode.ReadDTMF", params, nc, 10);
        String str = new String(msg.getData(), StandardCharsets.UTF_8);
        return str;
    }

}
