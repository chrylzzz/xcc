package com.haiyisoft.ivr;

import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.util.IdGenerator;
import com.haiyisoft.util.XCCUtil;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created By Chryl on 2023-02-08.
 */
@Slf4j
@Component
public class IvrHandler {

    @Async
    public void ivrDomain(Connection nc, JSONObject params) {
        log.info(Thread.currentThread().getName());
        final long call_id = IdGenerator.snowflakeId();//呼叫call_id
    }

    public void HandlerChannelEvent(Connection nc, ChannelEvent event) {
        /*
        Xcc xcc = new Xcc();
        switch (event.getState()) {
            case CallConst.Channel_START:
                org.json.simple.JSONObject params = new org.json.simple.JSONObject();
                params.put("ctrl_uuid", "ivvr");


                //开始接管
                xcc.Answer(nc, event);

                xcc.SetVar(nc, event);
                //播放一段音频
//                xcc.Play(nc, event);
                //获取当前通道状态
                xcc.GetState(nc, event);
                //执行原生APP
                xcc.NativeApp(nc, event);
                //语音进入IVR
                IvrHandler ivr = new IvrHandler();
                ivr.Ivr(nc, event);

            case "CALLING":
            case "RINGING":
            case "BRIDGE":
            case "READY":
                xcc.Bridge(nc, event);
            case "MEDIA":
        }
        */
//=================//=================//=================//=================
        XCCUtil xcc = new XCCUtil();
        switch (event.getState()) {
            case XCCConstants.Channel_START:
                //开始接管
                xcc.answer(nc, event);
                //
                xcc.detectSpeechPlayTTSNoDTMF(nc, event, "");

//                xcc.SetVar(nc, event);
                //播放一段音频
//                xcc.Play(nc, event);
                //获取当前通道状态
//                xcc.GetState(nc, event);
                //执行原生APP
//                xcc.NativeApp(nc, event);
                //语音进入IVR
//                IvrHandler ivr = new IvrHandler();
//                ivr.Ivr(nc, event);

            case "CALLING":
            case "RINGING":
            case "BRIDGE":
            case "READY":
            case "MEDIA":
        }

    }

}
