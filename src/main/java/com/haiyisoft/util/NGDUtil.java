package com.haiyisoft.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.enumerate.EnumXCC;
import com.haiyisoft.handler.NGDHandler;
import com.haiyisoft.model.NGDNodeModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 百度智能知识库
 * Created by Chr.yl on 2023/2/15.
 *
 * @author Chr.yl
 */
@Slf4j
public class NGDUtil {

    /**
     * 广西知识库接口
     *
     * @param queryText
     * @param sessionId call id
     * @return
     */
    public static NGDEvent coreQueryNGD(String queryText, String sessionId, String phone) {
        //package
        JSONObject param = coreQueryStruct(queryText, sessionId, phone);
        log.info("开始调用,百度知识库接口入参:{}", JSON.toJSONString(param, true));
        //invoke
        String jsonStrResult = HttpClientUtil.doPostJsonForGxNgd(IVRInit.XCC_CONFIG_PROPERTY.getNgdCoreQueryUrl(), param.toJSONString());
        //res
        JSONObject parse = JSON.parseObject(jsonStrResult);
        log.info("结束调用,百度知识库接口返回: {}", parse);

        Integer code = parse.getIntValue("code");//统一返回
        String msg = parse.getString("msg");//统一返回

        NGDEvent ngdEvent;
        String answer = "";
        if (XCCConstants.OK == code) {
            JSONObject jsonData = parse.getJSONObject("data");
            //答复来源
            String source = jsonData.getString("source");
            //是否解决
            boolean solved = jsonData.getBooleanValue("solved");
            answer = convertAnswer(jsonData, IVRInit.XCC_CONFIG_PROPERTY.isConvertSolved());
            ngdEvent = NGDHandler.ngdEventSetVar(code, msg, answer, source, solved);
            //保存流程信息
//            NGDNodeModel ngdNodeModel = saveNgdNode(queryText, answer, source, jsonData);
//            ngdEvent.setNgdNodeModel(ngdNodeModel);
            log.info("百度知识库返回正常 code: {} , msg: {} , answer: {}", code, msg, answer);
        } else {
            log.error("百度知识调用异常 code: {} , msg: {}", code, msg);
            answer = XCCConstants.XCC_MISSING_MSG;
            ngdEvent = NGDHandler.ngdEventSetErrorVar(code, msg, answer);
        }


        JSONObject resContext = parse.getJSONObject("data").getJSONObject("context");//context
        //处理用户校验是否完成
        NGDEvent resNgdEvent = convertUserOk(resContext, ngdEvent);

        log.info("coreQueryNGD ngdEvent: {}", resNgdEvent);
        return resNgdEvent;
    }


    /**
     * 根据百度知识库返回的数据取到合理的回复
     *
     * @param jsonData
     * @param convertSolved 是否处理 solved
     * @return
     */
    public static String convertAnswer(JSONObject jsonData, boolean convertSolved) {
        String answer = "";
        //根据 source 判断
        String source = jsonData.getString("source");
        //是否解决
        boolean solved = jsonData.getBooleanValue("solved");
        log.info("百度知识库命中 solved : {} source : {}", solved, source);
        if (solved) {
            if (XCCConstants.SOURCE_TASK_BASED.equals(source)) {//流程
                answer = jsonData.getString("suggestAnswer");
            } else if (XCCConstants.SOURCE_FAQ.equals(source)) {//faq
                answer = jsonData.getString("suggestAnswer");
            } else if (XCCConstants.SOURCE_CLARIFY.equals(source)) {//clarify
                answer = jsonData.getJSONObject("clarifyQuestions")
                        .getJSONObject("voice")
                        .getJSONArray("questions")
                        .getString(0);
            } else {//此处自定义,待发现新类型继续补充
                answer = XCCConstants.XCC_MISSING_MSG;
            }
        } else {
            if (convertSolved) {//处理,使用自定义话术
                answer = XCCConstants.XCC_MISSING_MSG;
            } else {//不处理
                answer = jsonData.getString("suggestAnswer");
            }
        }
/*
        if (XCCConstants.SOURCE_TASK_BASED.equals(source)) {//流程
            answer = jsonData.getString("suggestAnswer");
        } else if (XCCConstants.SOURCE_FAQ.equals(source)) {//faq
            answer = jsonData.getString("suggestAnswer");
        } else if (XCCConstants.SOURCE_CLARIFY.equals(source)) {//clarify
            answer = jsonData.getJSONObject("clarifyQuestions")
                    .getJSONObject("voice")
                    .getJSONArray("questions")
                    .getString(0);
        } else if (XCCConstants.SOURCE_SYSTEM.equals(source)) {//system
            answer = XCCConstants.XCC_MISSING_MSG;
        } else if (XCCConstants.SOURCE_NONE.equals(source)) {//none
            answer = jsonData.getString("suggestAnswer");
        } else {
            answer = XCCConstants.XCC_MISSING_MSG;
        }
*/
        log.info("百度知识库命中 answer: {}", answer);
        return answer;

    }


    /**
     * 获取ngd节点流程
     */
    public static NGDNodeModel saveNgdNode(String query, String answer, String source, JSONObject jsonData) {
        NGDNodeModel ngdNodeModel = new NGDNodeModel();
        ngdNodeModel.setAnswer(answer);

        String lastNodeName;
        //task_based才有lastNodeName,其他情况手机source
        if (XCCConstants.SOURCE_TASK_BASED.equals(source)) {//流程
            lastNodeName = jsonData.getJSONObject("answer").getString("lastNodeName");
        } else {
            lastNodeName = source;
        }
        ngdNodeModel.setNodeName(lastNodeName);
        ngdNodeModel.setSource(source);
        ngdNodeModel.setQuery(query);
        return ngdNodeModel;
    }

    /**
     * 将百度返回的文本字段处理为 指令和播报的内容
     *
     * @param ngdEvent
     * @return
     */

    public static NGDEvent convertText(NGDEvent ngdEvent) {
        String retKey = "";//指令
        String retValue = "";//播报内容
        String todoText = ngdEvent.getAnswer();
        log.info("convertText todoText: {}", todoText);
        if (StringUtils.isBlank(todoText)) {//话术为空
            retKey = XCCConstants.YYSR;
            retValue = XCCConstants.XCC_MISSING_ANSWER;
        } else {
            if (!todoText.contains(XCCConstants.NGD_SEPARATOR)) {//不带#的话术
                retKey = XCCConstants.YYSR;
                retValue = todoText;
            } else {//带#的话术
                String[] split = todoText.split(XCCConstants.NGD_SEPARATOR);
                retKey = split[0];//指令
                if (StringUtils.containsAny(retKey, XCCConstants.RET_KEY_STR_ARR)) {//有指令
                    retValue = split[1];//内容
                } else {//无指令
                    retKey = XCCConstants.YYSR;
                    retValue = XCCConstants.XCC_MISSING_ANSWER;
                }
            }
        }
        ngdEvent.setRetKey(retKey);
        ngdEvent.setRetValue(retValue);
        log.info("convertText retKey: {} , retValue: {}", retKey, retValue);
        return ngdEvent;
    }


    /**
     * 测试ngd接口
     *
     * @param queryText
     * @return
     */
    public static String testNGD(String queryText, String sessionId) {
        /*
        curl --location --request POST 'http://10.100.104.20:8304/api/v2/core/query' \
        --header 'Authorization: NGD b99612ed-8935-4215-98e2-dd96f05244b3' \
        --header 'Content-Type: application/json' \
        --data-raw '{
            "queryText": "查电费",
            "context": {
                "channel": "智能IVR"
            }
        }'

        */
//        String ch = "1";
//        String params = "{\n" +
//                "  \"sessionId\" : \"" + sessionId + "\",\n" +
//                "  \"channel\" : \"" + channel + "\",\n" +
//                "  \"queryText\" : \"" + queryText + "\",\n" +
//                "  \"context\" : {\"channel\":\"" + ch + "\"},\n" +
//                "  \"ext\" : {\"exact\":true}\n" +
//                "}";


        JSONObject param = new JSONObject();
        JSONObject context = new JSONObject();
        JSONObject ext = new JSONObject();
        context.put("channel", XCCConstants.CHANNEL_IVR);
        param.put("queryText", queryText);//客户问题
        param.put("sessionId", sessionId);//会话id
        ext.put("exact", "true");
        param.put("ext", ext);//ext
        param.put("context", context);//渠道标识，智能IVR为广西智能ivr标识
        log.info("开始调用百度知识库接口");
        String jsonStrResult = HttpClientUtil.doPostJsonForGxNgd(IVRInit.XCC_CONFIG_PROPERTY.getNgdCoreQueryUrl(), param.toJSONString());
        return jsonStrResult;
    }

    /**
     * 处理数字为中文汉字
     *
     * @param queryText
     * @return
     */
    public static String convertNumber2Ch(String queryText) {
        if ("0".equals(queryText)) {
            queryText = "零";
        } else if ("1".equals(queryText)) {
            queryText = "一";
        } else if ("2".equals(queryText)) {
            queryText = "二";
        } else if ("3".equals(queryText)) {
            queryText = "三";
        } else if ("4".equals(queryText)) {
            queryText = "四";
        } else if ("5".equals(queryText)) {
            queryText = "五";
        } else if ("6".equals(queryText)) {
            queryText = "六";
        } else if ("7".equals(queryText)) {
            queryText = "七";
        } else if ("8".equals(queryText)) {
            queryText = "八";
        } else if ("9".equals(queryText)) {
            queryText = "九";
        }
        return queryText;
    }

    /**
     * 组装core query
     *
     * @param queryText
     * @param sessionId
     * @param phone
     * @return
     */
    public static JSONObject coreQueryStruct(String queryText, String sessionId, String phone) {
        JSONObject param = new JSONObject();
        JSONObject context = new JSONObject();
        JSONObject ext = new JSONObject();
        context.put("channel", XCCConstants.CHANNEL_IVR);//渠道标识
        context.put(XCCConstants.IVR_PHONE, phone);
        param.put("queryText", queryText);//客户问题
        param.put("sessionId", sessionId);//会话id
        ext.put("exact", "true");
        param.put("ext", ext);//ext
        param.put("context", context);
        return param;
    }

    /**
     * 身份校验配套流程
     */
    public static NGDEvent convertUserOk(JSONObject context, NGDEvent ngdEvent) {
        if (context != null) {
            //判断用户校验是否完成
            String userOK = context.getString(EnumXCC.USER_OK.getProperty());
            if (EnumXCC.USER_OK.getValue().equals(userOK)) {
                String uid = context.getString(XCCConstants.IVR_YHBH);
                ngdEvent.setUserOk(true);
                ngdEvent.setUid(uid);
            } else {
                ngdEvent.setUserOk(false);
            }
            //获取 conversation
//            JSONArray conversation = context.getJSONArray("conversation");
//            ngdEvent.setConversation(conversation);
        } else {
            ngdEvent.setUserOk(false);
        }
        return ngdEvent;


    }

}
