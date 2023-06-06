package com.haiyisoft.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.handler.NGDHandler;
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
    public static NGDEvent coreQueryNGD(String queryText, String sessionId) {
        JSONObject param = new JSONObject();
        JSONObject context = new JSONObject();
        JSONObject ext = new JSONObject();
        context.put("channel", XCCConstants.CHANNEL_IVR);
        param.put("queryText", queryText);//客户问题
        param.put("sessionId", sessionId);//会话id
        ext.put("exact", "true");
        param.put("ext", ext);//ext
        param.put("context", context);//渠道标识，智能IVR为广西智能ivr标识
        log.info("开始调用,百度知识库接口入参:{}", JSON.toJSONString(param, true));
        //invoke
        String jsonStrResult = HttpClientUtil.doPostJsonForGxNgd(IVRInit.XCC_CONFIG_PROPERTY.getNgdQueryUrl(), param.toJSONString());
        //百度知识库返回的数据信息
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
            //是否解决:系统答复未解决
            boolean solved = jsonData.getBooleanValue("solved");
            answer = convertAnswer(jsonData);
            ngdEvent = NGDHandler.ngdEventSetVar(code, msg, answer, source, solved);
            log.info("百度知识库返回 code: {} , msg: {} , answer: {}", code, msg, answer);
        } else {
            log.error("百度知识调用异常 code: {} , msg: {}", code, msg);
            answer = XCCConstants.XCC_MISSING_MSG;
            ngdEvent = NGDHandler.ngdEventSetVar(code, msg, answer, "", false);
        }
        return ngdEvent;
    }


    /**
     * 根据百度知识库返回的数据取到合理的回复
     *
     * @param jsonData
     * @return
     */
    public static String convertAnswer(JSONObject jsonData) {
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
            }
        } else {
            answer = XCCConstants.XCC_MISSING_MSG;
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
     * 广西知识库接口
     *
     * @param queryText
     * @return
     */
    public static String invokeNGD(String queryText, String sessionId) {
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
        String jsonStrResult = HttpClientUtil.doPostJson(IVRInit.XCC_CONFIG_PROPERTY.getNgdQueryUrl(), param.toJSONString());
//        String jsonStrResult = "{\"time\":1669600747863,\"data\":{\"suggestAnswer\":\"YYSR#非智能IVR渠道标识 ： 您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！\",\"appendAnswers\":null,\"source\":\"task_based\",\"solved\":true,\"confidence\":0.9597217440605164,\"queryId\":\"2657c291-fd7d-4cf7-8f3f-87af8ae99ff8\",\"queryTime\":\"2022-11-28 09:59:07:687\",\"answerTime\":\"2022-11-28 09:59:07:863\",\"sessionId\":\"2d32b5b3-a98f-4e59-8ea1-6c5969771878\",\"actions\":[\"PhoneN\"],\"answer\":{\"intentMap\":{\"id\":\"5b6f21df-1203-4c08-ad35-ee5b16b435d0\",\"name\":\"q_elec_charge\",\"description\":\"\",\"agentId\":null,\"created\":null,\"updated\":null,\"confidence\":0.9597217440605164,\"source\":\"knn\",\"threshold\":0.0,\"system\":false,\"alias\":\"\",\"nameZh\":\"查询电量电费\",\"examples\":null,\"hasActiveCopy\":false,\"templateStr\":null,\"createdUserName\":null,\"createdUserId\":null,\"lastEditUserName\":null,\"lastEditUserId\":null},\"enterTopNodeName\":\"查电费\",\"lastEnterTopNodeName\":null,\"context\":{\"channel\":\"\",\"sys_counter\":{\"01查电费_muszm2dx_upf550gl\":1,\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\":1,\"查电费\":1,\"渠道标识判断_phokrvuf\":1},\"commonMonth\":\"\",\"authRefer\":1},\"collectInfo\":{},\"enterTopNodeIndex\":1,\"lastNodeId\":null,\"intent\":\"q_elec_charge\",\"entity\":{},\"dialogs\":[{\"dialogNodeName\":\"查电费\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"85eb8820-c869-4905-8982-28902b3d1a94\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":null},{\"dialogNodeName\":\"渠道标识判断_phokrvuf\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"a53e76a2-90fd-4d71-bd36-3bff3472ce97\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":2,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":\"非智能IVR渠道标识 ： \"},{\"dialogNodeName\":\"01查电费_muszm2dx_upf550gl\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"729ad4f1-26e0-4c97-a9f1-e765bfaa7e2d\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":\"您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！\"},{\"dialogNodeName\":\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"9cf1abf0-ddf6-4829-a86f-bf5329b64147\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"019e98ca-0fe8-49ed-a3cf-dcbbcc8252f5\",\"processName\":\"自动获取手机号验证身份_p1vibepk\",\"action\":\"PhoneN\",\"processType\":0,\"isResult\":false,\"value\":null}]},\"context\":{\"channel\":\"\",\"sys_counter\":{\"01查电费_muszm2dx_upf550gl\":1,\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\":1,\"查电费\":1,\"渠道标识判断_phokrvuf\":1},\"commonMonth\":\"\",\"authRefer\":1},\"botName\":\"测试\",\"botDesc\":null,\"botVersion\":9,\"agentType\":1,\"webhook\":false},\"code\":200,\"msg\":\"OK\"}";
        //百度知识库返回的数据信息
        JSONObject parse = JSON.parseObject(jsonStrResult);
        log.info("return 调用百度多轮接口: {}", parse);
        JSONObject dataJson = parse.getJSONObject("data");
//        String sessionId = dataJson.getString("sessionId");
        log.info("结束调用百度知识库接口");
        int code = parse.getIntValue("code");
        String res = "";
        if (200 == code) {
            res = dataJson.getString("suggestAnswer");
        } else {
            res = XCCConstants.XCC_MISSING_MSG;
        }
        log.info("百度知识库 code: {} , suggestAnswer: {}", code, res);
        return res;
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
        String jsonStrResult = HttpClientUtil.doPostJsonForGxNgd(IVRInit.XCC_CONFIG_PROPERTY.getNgdQueryUrl(), param.toJSONString());
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

}
