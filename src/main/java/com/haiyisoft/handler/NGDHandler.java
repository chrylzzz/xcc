package com.haiyisoft.handler;

import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.util.NGDUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Chr.yl on 2023/3/30.
 *
 * @author Chr.yl
 */
@Slf4j
public class NGDHandler {

    /**
     * xcc识别的数据送到ngd处理
     *
     * @param xccRecognitionResult xcc识别数据
     * @param channelId            call id
     * @return
     */
    public static NGDEvent handlerNlu(String xccRecognitionResult, String channelId) {
        //调用百度知识库
        NGDEvent ngdEvent = NGDUtil.coreQueryNGD(xccRecognitionResult, channelId);
        //处理指令和话术
        ngdEvent = NGDUtil.convertText(ngdEvent);
        log.info("ngdHandler ngdEvent :{}", ngdEvent);
        return ngdEvent;
    }


    /**
     * 赋值ngd返回数据
     *
     * @param code
     * @param msg
     * @param answer
     * @return
     */
    public static NGDEvent ngdEventSetVar(Integer code, String msg, String answer) {
        log.info("ngdEventSetVar code : {} , msg : {} , answer : {} ", code, msg, answer);
        NGDEvent ngdEvent = new NGDEvent();
        ngdEvent.setCode(code);
        ngdEvent.setMsg(msg);
        ngdEvent.setAnswer(answer);
        return ngdEvent;
    }


}
