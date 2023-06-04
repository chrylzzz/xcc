package com.haiyisoft.handler;

import com.haiyisoft.constant.XCCConstants;
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
        log.info("handlerNlu ngdEvent :{}", ngdEvent);
        return ngdEvent;
    }

    /**
     * 校验数据来源
     * 是否为system/task_based/faq/clarify
     *
     * @param ngdEvent
     * @return
     */
    public static boolean handleSource(NGDEvent ngdEvent) {
        boolean systemAnswer = ngdEvent.isSystemAnswer();
        if (systemAnswer) {
            return true;
        }
        return false;
    }


    /**
     * 赋值ngd返回数据
     *
     * @param code
     * @param msg
     * @param answer
     * @return
     */
    public static NGDEvent ngdEventSetVar(Integer code, String msg, String answer, String source) {
        log.info("ngdEventSetVar 入参 code : [{}] , msg : [{}] , answer : [{}] , source : [{}]", code, msg, answer, source);
        NGDEvent ngdEvent = new NGDEvent();
        ngdEvent.setCode(code);
        ngdEvent.setMsg(msg);
        ngdEvent.setAnswer(answer);
        ngdEvent.setSource(source);
        if (XCCConstants.SOURCE_SYSTEM.equals(source)) {
            ngdEvent.setSystemAnswer(true);
        } else {
            ngdEvent.setSystemAnswer(false);
        }
        log.info("ngdEventSetVar 出参 ngdEvent : {}", ngdEvent);
        return ngdEvent;
    }


}
