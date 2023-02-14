package com.haiyisoft.ivr;

import com.alibaba.fastjson.JSONObject;
import io.nats.client.Connection;
import io.nats.client.Message;
//import org.json.simple.JSONObject;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 多线程IVR流程
 * Created By Chryl on 2023-02-08.
 */
public class IvrThread extends Thread {
    private Connection nc;
    private JSONObject params;
    // private final Globals globals;

    public IvrThread(Connection nc, JSONObject params) {
        this.nc = nc;
        this.params = params;
    }

    private JSONObject make_rpc(String method) {
        JSONObject rpc = new JSONObject();
        rpc.put("jsonrpc", "2.0");
        rpc.put("id", UUID.randomUUID().toString());
        rpc.put("method", method);
        return rpc;
    }

    @Override
    public void run() {
        Connection nc = this.nc;
        System.out.println(params.get("uuid") + " call from " + params.get("cid_number" + " to " + params.get("dest_number")));
        String node_uuid = (String) params.get("node_uuid");
        System.out.println("ctrl_uuid " + Globals.ctrl_uuid);

        try {
            // accept the call
            JSONObject rpc = make_rpc("XNode.Accept");
            JSONObject p = new JSONObject();
            p.put("uuid", (String) params.get("uuid"));
            p.put("ctrl_uuid", Globals.ctrl_uuid.toString());
            rpc.put("params", p);
            StringWriter request = new StringWriter();
            rpc.writeJSONString(request);
            System.out.println(request);
            Future<Message> incoming = nc.request("cn.xswitch.node." + node_uuid,
                    request.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(1000, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);
            System.out.println(response);

            // answer
            rpc = make_rpc("XNode.Answer");
            p = new JSONObject();
            p.put("uuid", (String) params.get("uuid"));
            p.put("ctrl_uuid", Globals.ctrl_uuid.toString());
            rpc.put("params", p);
            request = new StringWriter();
            rpc.writeJSONString(request);
            System.out.println(request);
            incoming = nc.request("cn.xswitch.node." + node_uuid, request.toString().getBytes(StandardCharsets.UTF_8));
            msg = incoming.get(1000, TimeUnit.MILLISECONDS);
            response = new String(msg.getData(), StandardCharsets.UTF_8);
            System.out.println(response);

            Thread.sleep(500); // wait for media setup

            // play tts
            rpc = make_rpc("XNode.Play");
            p = new JSONObject();
            p.put("uuid", (String) params.get("uuid"));
            p.put("ctrl_uuid", Globals.ctrl_uuid.toString());
            JSONObject media = new JSONObject();
            media.put("data", "你好");
            media.put("type", "TEXT");
            media.put("engine", "ali");
            media.put("voice", "default");
            p.put("media", media);
            rpc.put("params", p);
            request = new StringWriter();
            rpc.writeJSONString(request);
            System.out.println(request);
            incoming = nc.request("cn.xswitch.node." + node_uuid, request.toString().getBytes(StandardCharsets.UTF_8));
            msg = incoming.get(5000, TimeUnit.MILLISECONDS);
            response = new String(msg.getData(), StandardCharsets.UTF_8);
            System.out.println(response);

            // hangup
            rpc = make_rpc("XNode.Hangup");
            p = new JSONObject();
            p.put("uuid", (String) params.get("uuid"));
            p.put("ctrl_uuid", Globals.ctrl_uuid.toString());
            p.put("cause", "NORMAL_CLEARING");
            rpc.put("params", p);
            request = new StringWriter();
            rpc.writeJSONString(request);
            System.out.println(request);
            incoming = nc.request("cn.xswitch.node." + node_uuid, request.toString().getBytes(StandardCharsets.UTF_8));
            msg = incoming.get(5000, TimeUnit.MILLISECONDS);
            response = new String(msg.getData(), StandardCharsets.UTF_8);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Ivr Threead Done for UUID=" + params.get("uuid"));
    }
}
