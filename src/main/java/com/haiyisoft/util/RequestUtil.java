package com.haiyisoft.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haiyisoft.anno.SysLog;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.IVREvent;
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
            log.error("handleException 发生异常：{} , {}", method, e);
        }
    }


    /**
     * 无数据返回
     *
     * @param ivrEvent     IVR业务实体
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static void natsRequestTimeOut(IVREvent ivrEvent, Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("{} 执行开始", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        log.info("{} request service:{}, Serializer json:{}", method, service, json);
        try {
            con.request(service, request.toString().getBytes(StandardCharsets.UTF_8), Duration.ofMillis(milliSeconds));
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("handleException 发生异常：{} , {}", method, e);
            ivrEvent = ExceptionAdvice.handleException(method, e, ivrEvent);
        }
        log.info("{} 执行结束", method);
    }

    /**
     * 无数据返回 Future
     *
     * @param ivrEvent     IVR业务实体
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static void natsRequestFuture(IVREvent ivrEvent, Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("{} 执行开始", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
//        jsonRpc.toString().getBytes()
        log.info(" service:{}, jsonRpc:{}", service, jsonRpc);
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("handleException 发生异常：{} , {}", method, e);
        }
        log.info("{} 执行结束", method);

    }


    /**
     * DetectSpeech
     *
     * @param ivrEvent     IVR业务实体
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    @SysLog("DetectSpeech")
    public static IVREvent natsRequestFutureByDetectSpeech(IVREvent ivrEvent, Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("{} 执行开始", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        log.info("{} request service:{}, Serializer json:{}", method, service, json);
        //识别返回数据,调用失败默认为""
        String utterance = "";
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get();
//            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);
            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
//            log.info("DetectSpeech 返回信息:{}", result);
            Integer code = result.getInteger("code");
            if (code == XCCConstants.JSONRPC_OK) {
                utterance = result.getJSONObject("data").getString("text");
            } else {//调用失败
            }
            ivrEvent.setCode(code);
            ivrEvent.setXccMsg(utterance);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("handleException 发生异常：{} , {}", method, e);
            ivrEvent = ExceptionAdvice.handleException(method, e, ivrEvent);
        }
        log.info("{} 识别返回数据 utterance: {}", method, utterance);
        log.info("{} ivrEvent: {}", method, ivrEvent);
        log.info("{} 执行结束", method);
        return ivrEvent;
    }


    /**
     * ReadDTMF
     *
     * @param ivrEvent     IVR业务实体
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static IVREvent natsRequestFutureByReadDTMF(IVREvent ivrEvent, Connection con, String service, String method, JSONObject params, long milliSeconds) {
        log.info("{} 执行开始时间为", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        log.info("{} request service:{}, Serializer json:{}", method, service, json);
        //识别返回数据,调用失败默认为""
        String dtmf = "";
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get();
//            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);

            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");
            if (code == XCCConstants.JSONRPC_OK) {
                dtmf = result.getString("dtmf");
            } else if (code == XCCConstants.JSONRPC_NOTIFY) {
                dtmf = result.getString("dtmf");
            } else {//调用失败
            }
            ivrEvent.setCode(code);
            ivrEvent.setXccMsg(dtmf);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("handleException 发生异常：{} , {}", method, e);
            ivrEvent = ExceptionAdvice.handleException(method, e, ivrEvent);
        }
        log.info("{} 识别返回数据 dtmf: {}", method, dtmf);
        log.info("{} ivrEvent: {}", method, ivrEvent);
        log.info("{} 执行结束", method);
        return ivrEvent;
    }

    /**
     * Bridge
     *
     * @param ivrEvent     IVR业务实体
     * @param con          connection
     * @param service      node uuid
     * @param method       xcc method
     * @param params       rpc-json params
     * @param milliSeconds 毫秒
     * @return
     */
    public static IVREvent natsRequestFutureByBridge(IVREvent ivrEvent, Connection con, String service, String method, JSONObject params, int milliSeconds) {
        log.info("{} 执行开始时间为", method);
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        String json = JSON.toJSONString(jsonRpc, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        log.info("{} request service:{}, Serializer json:{}", method, service, json);
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
//            Message msg = incoming.get(milliSeconds, TimeUnit.MILLISECONDS);
            Message msg = incoming.get();
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("{} 返回信息:{}", method, response);


            JSONObject result = JSONObject.parseObject(response).getJSONObject("result");
            Integer code = result.getInteger("code");
            if (code == XCCConstants.JSONRPC_OK) {
            } else if (code == XCCConstants.JSONRPC_NOTIFY) {
            } else {//调用失败
            }
            ivrEvent.setCode(code);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("handleException 发生异常：{} , {}", method, e);
            ivrEvent = ExceptionAdvice.handleException(method, e, ivrEvent);
        }
        log.info("{} ivrEvent: {}", method, ivrEvent);
        log.info("{} 识别返回数据: {}", method, "===================");
        log.info("{} 执行结束", method);
        return ivrEvent;
    }
}
