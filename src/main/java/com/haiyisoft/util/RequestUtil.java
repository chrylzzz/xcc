package com.haiyisoft.util;

import com.alibaba.fastjson.JSONObject;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import io.nats.client.impl.AckType;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsJetStreamMetaData;
import io.nats.client.support.Status;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        //JSON-RPC id,每个请求一个，保证唯一.
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
     * @param con     connection
     * @param service node uuid
     * @param method  xcc method
     * @param params  rpc-json params
     * @param millis  毫秒
     * @return
     */
    public static Message natsRequestTimeOut(Connection con, String service, String method, JSONObject params, long millis) {
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
//        jsonRpc.toString().getBytes()
        log.info("===================== service:{}, jsonRpc:{}", service, jsonRpc);
        try {
            return con.request(service, request.toString().getBytes(StandardCharsets.UTF_8), Duration.ofMillis(millis));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Message() {

            @Override
            public String getSubject() {
                return null;
            }

            @Override
            public String getReplyTo() {
                return null;
            }

            @Override
            public boolean hasHeaders() {
                return false;
            }

            @Override
            public Headers getHeaders() {
                return null;
            }

            @Override
            public boolean isStatusMessage() {
                return false;
            }

            @Override
            public Status getStatus() {
                return null;
            }

            @Override
            public byte[] getData() {
                return new byte[0];
            }

            @Override
            public boolean isUtf8mode() {
                return false;
            }

            @Override
            public Subscription getSubscription() {
                return null;
            }

            @Override
            public String getSID() {
                return null;
            }

            @Override
            public Connection getConnection() {
                return null;
            }

            @Override
            public NatsJetStreamMetaData metaData() {
                return null;
            }

            @Override
            public AckType lastAck() {
                return null;
            }

            @Override
            public void ack() {

            }

            @Override
            public void ackSync(Duration duration) throws TimeoutException, InterruptedException {

            }

            @Override
            public void nak() {

            }

            @Override
            public void term() {

            }

            @Override
            public void inProgress() {

            }

            @Override
            public boolean isJetStream() {
                return false;
            }
        };
    }

    /**
     * @param con     connection
     * @param service node uuid
     * @param method  xcc method
     * @param params  rpc-json params
     * @param millis  毫秒
     * @return
     */
    public static String natsRequestFuture(Connection con, String service, String method, JSONObject params, long millis) {
        JSONObject jsonRpc = getJsonRpc(method, params);
        StringWriter request = new StringWriter();
        jsonRpc.writeJSONString(request);
        log.info("===================== service:{}, jsonRpc:{}", service, jsonRpc);
        try {
            Future<Message> incoming = con.request(service, request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(1000, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return "";

    }
}
