package com.haiyisoft.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Chr.yl on 2023/6/30.
 *
 * @author Chr.yl
 */
@Slf4j
public class WebHookHandler {

    /**
     * 发送短信
     *
     * @param ivrEvent
     * @param dxnr     短信内容
     */
    public static void sendMessage(IVREvent ivrEvent, String dxnr) {
        //来电号码
        String cidNumber = ivrEvent.getCidNumber();
        JSONObject context = new JSONObject();
        context.put("jssjh", cidNumber);
        context.put("dxnr", dxnr);
        JSONObject params = convertWebHookReqBody(XCCConstants.SEND_MESSAGE, context);
        String resData = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getWebHookUrl(), params.toJSONString());
        log.info("sendMessage : {}", resData);
    }

    /**
     * 会话记录
     * Save Call Detail Recording
     *
     * @param ivrEvent
     */
    public static void writeCDR(IVREvent ivrEvent) {
        JSONArray metadataArray = ivrEvent.getNgdNodeMetadataArray();
        log.info("cdr array :{}", metadataArray);
        String cdr = "";
        if (metadataArray != null) {
//                metadataArray.forEach(metadata -> {
//                    JSONObject jsonObject = (JSONObject) JSON.toJSON(metadata);
//                    String query = jsonObject.getString("query");
//                    String queryTime = jsonObject.getString("queryTime");
//                    String answer = jsonObject.getString("answer");
//                    String answerTime = jsonObject.getString("answerTime");
//                    cdr = cdr + (XCCConstants.B + queryTime + query + XCCConstants.H + answerTime + answer);
//                });
            JSONObject welcomeJsonData = metadataArray.getJSONObject(0);
            String welcomeStr = welcomeJsonData.getString("answer");
            String welcomeTime = welcomeJsonData.getString("answerTime");
            cdr = XCCConstants.B + welcomeTime + welcomeStr;
            for (int i = 1; i < metadataArray.size(); i++) {
                JSONObject jsonObject = metadataArray.getJSONObject(i);
                String query = jsonObject.getString("query");
                String queryTime = jsonObject.getString("queryTime");
                String answer = jsonObject.getString("answer");
                String answerTime = jsonObject.getString("answerTime");
                cdr = cdr + (XCCConstants.B + queryTime + query + XCCConstants.H + answerTime + answer);
            }
        }

        //标准格式:[#B:2018-11-20 20:00:00欢迎致电95598.#H:2018-11-20 20:00:00你好我要查电费。#B:2018-11-20 20:00:00请A请按键输入您的用户编号。]
        log.info("================ CDR:{} ", cdr);

        //来电号码
        String cidNumber = ivrEvent.getCidNumber();
        //华为会话标识
        String icdCallerId = ivrEvent.getIcdCallerId();
        //号码归属地
        String phoneAdsCode = ivrEvent.getPhoneAdsCode();
        JSONObject context = new JSONObject();
        context.put("callid", icdCallerId);
        context.put("ldhm", cidNumber);
        context.put("cdr", cdr);
        //这里送后缀码
        context.put("dqbm", phoneAdsCode);
        context.put("gddwbm", phoneAdsCode);
        JSONObject params = convertWebHookReqBody(XCCConstants.I_HJZX_BCDHNR, context);
        String resData = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getWebHookUrl(), params.toJSONString());
        log.info("I_HJZX_BCDHNR : {}", resData);
    }

    /**
     * WebHook请求体
     *
     * @param action
     * @param context
     * @return
     */
    public static JSONObject convertWebHookReqBody(String action, JSONObject context) {
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("context", context);
        return params;
    }
}