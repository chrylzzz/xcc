package com.haiyisoft.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.advice.IVRExceptionAdvice;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.ivr.IVRHandlerAdvice;
import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
        log.info(" service:{}, jsonRpc:{}", service, jsonRpc);
        try {
            con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("xcc handleException 发生异常：{} , {}", method, e);
        }
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
        log.info("{} 执行开始", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, true);
        log.info("{} 请求信息 service:[{}], Serializer json:{}", method, service, json);
        try {
            con.request(service, request.toString().getBytes(StandardCharsets.UTF_8), Duration.ofMillis(milliSeconds));
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);
            log.info("{} 执行结束", method);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("xcc handleException 发生异常：{} , {}", method, e);
        }

    }

    /**
     * Hangup
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static XCCEvent natsRequestFutureByHangup(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        return natsRequestFutureByAnswer(con, service, method, params, milliSeconds);
    }

    /**
     * PlayTTS
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static XCCEvent natsRequestFutureByPlayTTS(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        return natsRequestFutureByAnswer(con, service, method, params, milliSeconds);
    }

    /**
     * Answer
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static XCCEvent natsRequestFutureByAnswer(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("{} 执行开始", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, true);
        log.info("{} 请求信息 service:[{}], Serializer json:{}", method, service, json);
        XCCEvent xccEvent;
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get();
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);
            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");//统一返回
            String message = result.getString("message");//统一返回
            String type = "";//type=ERROR时才有
            String error = "";//type=ERROR时才有
            xccEvent = IVRHandlerAdvice.xccEventSetVar(code, message, "", type, error);
            log.info("{} 执行结束", method);
        } catch (Exception e) {
            log.error("xcc handleException 发生异常：{} , {}", method, e);
            xccEvent = IVRExceptionAdvice.handleException(method, e);
        }
        return xccEvent;
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
    public static XCCEvent natsRequestFutureByDetectSpeech(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("{} 执行开始", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
//        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        String json = JSON.toJSONString(jsonRpc, true);
        log.info("{} 请求信息 service:[{}], Serializer json:{}", method, service, json);
        //识别返回数据,调用失败默认为""
        String utterance = "";
        XCCEvent xccEvent;
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get();
//            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);
            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");//统一返回
            String message = result.getString("message");//统一返回
            String type = "";//type=ERROR时才有
            String error = "";//type=ERROR时才有
            if (code == XCCConstants.OK) {
                JSONObject jsonData = result.getJSONObject("data");
                if (IVRInit.XCC_CONFIG_PROPERTY.isHandleEngineData()) {//手动解析
                    String xmlStr = jsonData.getString("engine_data");
                    utterance = Dom4jUtil.parseAsrResXml(xmlStr);
                    if (utterance == null) {//未识别话术,参考深度解析返回event
                        type = XCCConstants.RECOGNITION_TYPE_ERROR;
                        error = XCCConstants.RECOGNITION_ERROR_NO_INPUT;
                    }
                } else {//已深度解析
                    utterance = jsonData.getString("text");
                    type = jsonData.getString("type");
                    error = jsonData.getString("error");
                }
            } else {//调用失败, 这里不作处理

            }
            xccEvent = IVRHandlerAdvice.xccEventSetVar(code, message, utterance, type, error);
            log.info("{} 识别返回数据 utterance: {}", method, utterance);
            log.info("{} xccEvent: {}", method, xccEvent);
            log.info("{} 执行结束", method);
        } catch (Exception e) {
            log.error("xcc handleException 发生异常：{} , {}", method, e);
            xccEvent = IVRExceptionAdvice.handleException(method, e);
        }
        return xccEvent;
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
    public static XCCEvent natsRequestFutureByReadDTMF(Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("{} 执行开始时间为", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, true);
        log.info("{} 请求信息 service:[{}], Serializer json:{}", method, service, json);
        //识别返回数据,调用失败默认为""
        String dtmf = "";
        XCCEvent xccEvent;
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
//            Message msg = incoming.get();
            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);
            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");//统一返回
            String message = result.getString("message");//统一返回
            String type = "";//type=ERROR时才有
            String error = "";//type=ERROR时才有
            if (code == XCCConstants.OK) {
                dtmf = result.getString("dtmf");
            } else if (code == XCCConstants.JSONRPC_NOTIFY) {
                dtmf = result.getString("dtmf");
                if (dtmf == null) {//未识别话术,参考深度解析返回event
                    type = XCCConstants.RECOGNITION_TYPE_ERROR;
                    error = XCCConstants.RECOGNITION_ERROR_NO_INPUT;
                }
            } else {//调用失败

            }
            xccEvent = IVRHandlerAdvice.xccEventSetVar(code, message, dtmf, type, error);
            log.info("{} 识别返回数据 dtmf: {}", method, dtmf);
            log.info("{} xccEvent: {}", method, xccEvent);
            log.info("{} 执行结束", method);
        } catch (Exception e) {
            log.error("xcc handleException 发生异常：{} , {}", method, e);
            xccEvent = IVRExceptionAdvice.handleException(method, e);
        }

        return xccEvent;
    }

    /**
     * Bridge
     *
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static XCCEvent natsRequestFutureByBridge(Connection con, String service, String method, JSONObject params, int milliSeconds) {
        log.info("{} 执行开始时间为", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, true);
        log.info("{} 请求信息 service:[{}], Serializer json:{}", method, service, json);
        XCCEvent xccEvent;
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
//            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            Message msg = incoming.get();
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);
            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");//统一返回
            String message = result.getString("message");//统一返回
            String type = "";//type=ERROR时才有
            String error = "";//type=ERROR时才有
            if (code == XCCConstants.OK) {
            } else if (code == XCCConstants.JSONRPC_NOTIFY) {
            } else {//调用失败
            }
            xccEvent = IVRHandlerAdvice.xccEventSetVar(code, message, "", type, error);
            log.info("{} xccEvent: {}", method, xccEvent);
            log.info("{} 识别返回数据: {}", method, "===================");
            log.info("{} 执行结束", method);
        } catch (Exception e) {
            log.error("xcc handleException 发生异常：{} , {}", method, e);
            xccEvent = IVRExceptionAdvice.handleException(method, e);
        }

        return xccEvent;
    }
}
