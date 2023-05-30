package com.haiyisoft.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haiyisoft.anno.SysLog;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConfigProperty;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.ivr.IVRHandler;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Created by Chr.yl on 2023/3/6.
 *
 * @author Chr.yl
 */
@Slf4j
@RestController
@RequestMapping("xcc")
public class ChrylController {


    /*
    @Autowired
    private IVRHandler ivrHandler;


    public static void main(String[] args) {
        new TestController().domain();
    }

    public void domain() {
        try {
            log.info(" Ivr Controller started ");
            //获取nats连接
            Connection des_nc = Nats.connect(XCCConstants.NATS_URL);

            //从nats获取订阅主题
            Subscription des_sub = des_nc.subscribe(XCCConstants.XCTRL_SUBJECT_DESTROY);
            while (true) {
                try {
                    //订阅接收的消息
                    Message des_subMsg = des_sub.nextMessage(Duration.ofMillis(50000));
//                log.info("订阅接收的消息：{}", subMsg);
                    if (des_subMsg == null) {
                        log.info("this subMsg is null ,{}", des_subMsg);
                    } else {
                        log.info(" subMsg ,{}", des_subMsg);
                        log.info(" des_subMsg ,{}", des_subMsg);
                        //订阅事件
                        String des_eventStr = new String(des_subMsg.getData(), StandardCharsets.UTF_8);
//                        log.info(" eventStr data:{}", eventStr);

                        JSONObject des_eventJson = JSONObject.parseObject(des_eventStr);
//                log.info("订阅事件 json data:{}", eventJson);
                        //event状态,Event.Channel（state=START）
                        String des_method = des_eventJson.getString("method");
//                log.info("event method :{}", method);
                        //XNode收到呼叫后，向NATS广播来话消息（Event.Channel（state = START）），Ctrl收到后进行处理。
                        switch (des_method) {
                            case XCCConstants.Event_Channel:
                                JSONObject params = des_eventJson.getJSONObject("params");
                                //convert param
                                ChannelEvent event = IVRHandler.convertParams(params);
                                //asr domain
//                                ivrHandler.handlerChannelEvent(nc, event);
                                new IVRHandler().handlerChannelEvent(des_nc, event);
                            case XCCConstants.Event_DetectedFace:
                                log.info("事件 event======:{}", "Event.DetectedFace");
                            case XCCConstants.Event_NativeEvent:
                                log.info("事件 event======:{}", "Event.NativeEvent");
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    */

    @GetMapping("chryl")
    public String show() {
        String json = JSON.toJSONString(IVRInit.XCC_CONFIG_PROPERTY, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
        return json;
    }

}
