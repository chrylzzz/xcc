package com.haiyisoft.handler;

import com.alibaba.fastjson2.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.enumerate.EnumXCC;
import com.haiyisoft.util.NGDUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * ngd业务处理
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
     * @param icdCallerId          华为cid
     * @param phoneAdsCode         来电后缀码
     * @return
     */
    public static NGDEvent handlerNlu(String xccRecognitionResult, String channelId,
                                      String callNumber, String icdCallerId, String phoneAdsCode) {
        //调用百度知识库,获取answer
        NGDEvent ngdEvent = NGDUtil.coreQuery(xccRecognitionResult, channelId, callNumber, icdCallerId, phoneAdsCode);
        //处理指令和话术,处理成retKey/retValue
        ngdEvent = NGDUtil.convertText(ngdEvent);
        log.info("handlerNlu ngdEvent :{}", ngdEvent);
        return ngdEvent;
    }

    /**
     * NGD handler
     *
     * @param xccRecognitionResult xcc识别数据
     * @param channelId            call id
     * @param callNumber           来电号码
     * @param icdCallerId          华为cid
     * @param phoneAdsCode         来电后缀码
     * @param reqNgdEvent          上一环节的ngdEvent
     * @return
     */
    public static NGDEvent handler(String xccRecognitionResult, String channelId, String callNumber, String icdCallerId, String phoneAdsCode, NGDEvent reqNgdEvent) {
        //调用百度知识库
        JSONObject result = NGDUtil.coreQueryJson(xccRecognitionResult, channelId, callNumber, icdCallerId, phoneAdsCode);

        int code;
        String msg;
        //NGD 存在SocketTimeoutException
        if (result == null) {
            code = EnumXCC.NGD_BOT_RES_ERROR.keyParseIntValue();
            msg = EnumXCC.NGD_BOT_RES_ERROR.getValue();
        } else {
            code = result.getIntValue("code");//统一返回
            msg = result.getString("msg");//统一返回
        }

        NGDEvent resNgdEvent;
        String answer;
        if (XCCConstants.OK == code) {
            JSONObject jsonData = result.getJSONObject("data");
            //答复来源
            String source = jsonData.getString("source");
            //是否解决
            boolean solved = jsonData.getBooleanValue("solved");
            //处理回复
            answer = NGDUtil.convertAnswer(jsonData, IVRInit.CHRYL_CONFIG_PROPERTY.isConvertSolved());
            //处理ngd api数据
            resNgdEvent = ngdEventSetVar(channelId, code, msg, answer, source, solved);
            log.info("百度知识库返回正常 code: {} , msg: {} , answer: {}", code, msg, answer);
        } else {
            answer = XCCConstants.XCC_MISSING_TEXT;
            resNgdEvent = ngdEventSetErrorVar(channelId, code, msg, answer);
            log.error("百度知识调用异常 code: {} , msg: {} , answer: {}", code, msg, answer);
        }
        //context全局交互实体
        JSONObject context = getContext(result);
        //测试发现闲聊时,无context
        if (context != null && context.size() > 0) {
            log.info("context:{}", context);
            //处理用户校验
            NGDUtil.checkUser(context, resNgdEvent);
            //处理客户意图
            NGDUtil.handlerIntent(context, resNgdEvent);
            //处理满意度
            NGDUtil.handlerRate(context, resNgdEvent);
            //处理转人工队列编码
            NGDUtil.handlerTransferQueue(context, resNgdEvent);
        } else {
            //处理全局参数
            convertNgdEvent(reqNgdEvent, resNgdEvent);
        }

        //处理记录会话
        NGDUtil.convertNgdNodeMateData(xccRecognitionResult, answer,code, result, resNgdEvent);

        //处理指令和话术,处理成retKey/retValue
        NGDUtil.convertText(resNgdEvent);
        log.info("handler ngdEvent :{}", resNgdEvent);
        return resNgdEvent;
    }

    /**
     * 处理N G D 流程业务交互变量(全局)
     * uid
     * userOK
     * intent
     *
     * @param reqNgdEvent
     * @param resNgdEvent
     * @return
     */
    public static NGDEvent convertNgdEvent(NGDEvent reqNgdEvent, NGDEvent resNgdEvent) {
        String uid = reqNgdEvent.getUid();
        String intent = reqNgdEvent.getIntent();
        boolean userOk = reqNgdEvent.isUserOk();
        String transferCode = reqNgdEvent.getTransferCode();

        resNgdEvent.setUid(uid);
        resNgdEvent.setIntent(intent);
        resNgdEvent.setUserOk(userOk);
        resNgdEvent.setTransferCode(transferCode);
        return resNgdEvent;
    }

    /**
     * 处理知识库回复
     * 校验solved: true/false
     *
     * @param ngdEvent
     * @return false 知识库错误回复, true 知识库正确回复
     */
    public static boolean handleSolved(NGDEvent ngdEvent) {
        return ngdEvent.isSolved();
    }

    /**
     * 校验source
     * system/none/
     * task_based/faq/clarify/
     *
     * @param ngdEvent
     * @return
     */
    public static boolean handleSource(NGDEvent ngdEvent) {
        boolean handleSource = false;
        String source = ngdEvent.getSource();
        if (XCCConstants.SOURCE_TASK_BASED.equals(source)) {//task_based

        } else if (XCCConstants.SOURCE_FAQ.equals(source)) {//faq

        } else if (XCCConstants.SOURCE_CLARIFY.equals(source)) {//clarify

        } else if (XCCConstants.CHITCHAT.equals(source)) {//chitchat

        } else if (XCCConstants.SOURCE_SYSTEM.equals(source)) {//system

        } else if (XCCConstants.SOURCE_NONE.equals(source)) {//none

        } else {

        }
        return handleSource;
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
        log.info("ngdEventSetVar 入参 sessionId : [{}] , code : [{}] , msg : [{}] , answer : [{}] , source : [{}] , solved : {}",
                sessionId, code, msg, answer, source, solved);
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
        return ngdEventSetVar(sessionId, code, msg, answer, "ngd error source", false);
    }

    /**
     * 获取context全局交互实体
     *
     * @param result
     * @return
     */
    public static JSONObject getContext(JSONObject result) {
        JSONObject context;
        if (result != null) {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                context = data.getJSONObject("context");
            } else {
                context = new JSONObject(0);
            }
        } else {
            context = new JSONObject(0);
        }

        return context;
    }

}
