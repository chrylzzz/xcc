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
     * @param callNumber           来电号码
     * @return
     */
    public static NGDEvent handlerNlu(String xccRecognitionResult, String channelId, String callNumber) {
        //调用百度知识库,获取answer
        NGDEvent ngdEvent = NGDUtil.coreQueryNGD(xccRecognitionResult, channelId, callNumber);
        //处理指令和话术,处理成retKey/retValue
        ngdEvent = NGDUtil.convertText(ngdEvent);
        log.info("handlerNlu ngdEvent :{}", ngdEvent);
        return ngdEvent;
    }


    /**
     * 校验机器回复
     * system/none/
     * task_based/faq/clarify/
     *
     * @param ngdEvent
     * @return
     */
    public static boolean handleSolved(NGDEvent ngdEvent) {
        return ngdEvent.isSolved();

    }


    /**
     * 赋值 ngd 返回数据
     *
     * @param sessionId
     * @param code
     * @param msg
     * @param answer
     * @param source
     * @param solved
     * @return
     */
    public static NGDEvent ngdEventSetVar(String sessionId, Integer code, String msg, String answer, String source, boolean solved) {
        log.info("ngdEventSetVar 入参 sessionId : [{}] , code : [{}] , msg : [{}] , answer : [{}] , source : [{}] , solved : {}", code, msg, answer, source, solved);
        NGDEvent ngdEvent = new NGDEvent(sessionId, code, msg, source, answer, solved);
        log.info("ngdEventSetVar 出参 ngdEvent : {}", ngdEvent);
        return ngdEvent;
    }

    /**
     * 设置失败 ngdEvent
     *
     * @param code
     * @param msg
     * @param answer
     * @return
     */
    public static NGDEvent ngdEventSetErrorVar(String sessionId, Integer code, String msg, String answer) {
        return ngdEventSetVar(sessionId, code, msg, answer, "", false);
    }

}
