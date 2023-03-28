package com.haiyisoft.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.IVRModel;
import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created By Chryl on 2023-02-08.
 */
@Slf4j
public class RequestUtil {


    /**
     * 构造JSON-RPC对象
     *
     * @param method 方法名
     * @return JSONObject
     */
    public static JSONObject getJsonRpc(String method, JSONObject params) {
        /*JSON-RPC 2.0格式定义
        {
            "jsonrpc": "2.0",
            "id": "0",
            "method": "XNode.NativeApp",
            "params": {
                "ctrl_uuid": "ctrl_uuid",
                "uuid": "channel_uuid",
                "cmd": "playback",
                "args": "/tmp/welcome.wav"
            }
        }*/
        JSONObject jsonRpc = new JSONObject();
        //JSON-RPC 2.0版本
        jsonRpc.put("jsonrpc", "2.0");
        //JSON-RPC id,每个请求一个，保证唯一. XSwitch要求该id必须是一个字符串类型
        jsonRpc.put("id", IdGenerator.simpleUUID());
        jsonRpc.put("method", method);
        jsonRpc.put("params", params);
        return jsonRpc;
    }

    /**
     * @param con     connection
     * @param service node uuid
     * @param method  xcc method
     * @param params  rpc-json params
     */
    public static void natsRequest(Connection con, String service, String method, JSONObject params) {
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        con.publish(service, request.toString().getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 无数据返回
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static void natsRequestTimeOut(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
//        jsonRpc.toString().getBytes()
        log.info(" service:{}, jsonRpc:{}", service, jsonRpc);
        try {
            con.request(service, request.toString().getBytes(StandardCharsets.UTF_8), Duration.ofMillis(milliSeconds));
//            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
//            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
//            String response = new String(msg.getData(), StandardCharsets.UTF_8);
//            log.info("DetectSpeech 返回信息:{}", response);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 无数据返回 Future
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static void natsRequestFuture(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
//        jsonRpc.toString().getBytes()
        log.info(" service:{}, jsonRpc:{}", service, jsonRpc);
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("返回信息:{}", response);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * DetectSpeech
     *
     * @param con     connection
     * @param service node uuid
     * @param method  xcc method
     * @param params  rpc-json params
     * @return
     */
    public static IVRModel natsRequestFutureByDetectSpeech(Connection con, String service, String method, JSONObject params) {
        long startTime = System.currentTimeMillis();
        log.info("DetectSpeech 执行开始时间为:{}", LocalDateTime.now());
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        log.info("service:{}, jsonRpc:{}", service, jsonRpc);
        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        log.info("request Serializer json:{}", json);
        //识别返回数据,调用失败默认为""
        String utterance = "";
        IVRModel ivrModel = new IVRModel();
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get();
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("DetectSpeech 返回信息:{}", response);
            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");
            if (code == XCCConstants.JSONRPC_OK) {
                utterance = result.getJSONObject("data").getString("text");
            } else {//调用失败
            }
            ivrModel.setCode(code);
            ivrModel.setXccMsg(utterance);
        } catch (Exception e) {
            e.printStackTrace();
            ivrModel = tryIvr();

        }
        log.info("执行结束时间为:{}", LocalDateTime.now());
        log.info("DetectSpeech 识别返回数据: {}", utterance);
        long endTime = System.currentTimeMillis();
        log.warn("{} ：调用业务结束，耗时：{} ms", Thread.currentThread().getName(), (endTime - startTime));
        return ivrModel;
    }

    /**
     * DetectSpeech
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static IVRModel natsRequestFutureByDetectSpeech(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("DetectSpeech 执行开始时间为:{}", LocalDateTime.now());
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        log.info("service:{}, jsonRpc:{}", service, jsonRpc);
        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        log.info("request Serializer json:{}", json);
        //识别返回数据,调用失败默认为""
        String utterance = "";
        IVRModel ivrModel = new IVRModel();
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("DetectSpeech 返回信息:{}", response);
            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");
            if (code == XCCConstants.JSONRPC_OK) {
                utterance = result.getJSONObject("data").getString("text");
            } else {//调用失败
            }
            ivrModel.setCode(code);
            ivrModel.setXccMsg(utterance);
        } catch (Exception e) {
            e.printStackTrace();
            ivrModel = tryIvr();

        }
        log.info("执行结束时间为:{}", LocalDateTime.now());
        log.info("DetectSpeech 识别返回数据: {}", utterance);
        return ivrModel;
    }


    /**
     * ReadDTMF
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static IVRModel natsRequestFutureByReadDTMF(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("ReadDTMF 执行开始时间为:{}", LocalDateTime.now());
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        log.info("service:{}, jsonRpc:{}", service, jsonRpc);
        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        log.info("request Serializer json:{}", json);
        //识别返回数据,调用失败默认为""
        String dtmf = "";
        IVRModel ivrModel = new IVRModel();
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
//            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            Message msg = incoming.get();
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("ReadDTMF 返回信息:{}", response);


            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");
            if (code == XCCConstants.JSONRPC_OK) {
                dtmf = result.getString("dtmf");
            } else if (code == XCCConstants.JSONRPC_NOTIFY) {
                dtmf = result.getString("dtmf");
            } else {//调用失败
            }
            ivrModel.setCode(code);
            ivrModel.setXccMsg(dtmf);
        } catch (Exception e) {
            e.printStackTrace();
            ivrModel = tryIvr();
        }
        log.info("ReadDTMF 执行结束时间为:{}", LocalDateTime.now());
        log.info("识别返回数据: {}", dtmf);
        return ivrModel;
    }


    public static IVRModel tryIvr() {
        IVRModel ivrModel = new IVRModel();
        ivrModel.setCode(500);
        ivrModel.setXccMsg("");
        return ivrModel;
    }
}
