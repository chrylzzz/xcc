package com.haiyisoft.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
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
     * @return
     */
    public static String doGxZsk(String queryText, String sessionId) {
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
        JSONObject param = new JSONObject();
        JSONObject context = new JSONObject();
        context.put("channel", "智能IVR");
        param.put("queryText", queryText);//客户问题
        param.put("sessionId", sessionId);//会话id
        param.put("context", context);//渠道标识，智能IVR为广西智能ivr标识
        log.info("开始调用百度知识库接口:{}", LocalDateTime.now());
        String jsonStrResult = HttpClientUtil.doPostJsonForGx(XCCConstants.NGD_URL, param.toJSONString());
//        String jsonStrResult = "{\"time\":1669600747863,\"data\":{\"suggestAnswer\":\"YYSR#非智能IVR渠道标识 ： 您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！\",\"appendAnswers\":null,\"source\":\"task_based\",\"solved\":true,\"confidence\":0.9597217440605164,\"queryId\":\"2657c291-fd7d-4cf7-8f3f-87af8ae99ff8\",\"queryTime\":\"2022-11-28 09:59:07:687\",\"answerTime\":\"2022-11-28 09:59:07:863\",\"sessionId\":\"2d32b5b3-a98f-4e59-8ea1-6c5969771878\",\"actions\":[\"PhoneN\"],\"answer\":{\"intentMap\":{\"id\":\"5b6f21df-1203-4c08-ad35-ee5b16b435d0\",\"name\":\"q_elec_charge\",\"description\":\"\",\"agentId\":null,\"created\":null,\"updated\":null,\"confidence\":0.9597217440605164,\"source\":\"knn\",\"threshold\":0.0,\"system\":false,\"alias\":\"\",\"nameZh\":\"查询电量电费\",\"examples\":null,\"hasActiveCopy\":false,\"templateStr\":null,\"createdUserName\":null,\"createdUserId\":null,\"lastEditUserName\":null,\"lastEditUserId\":null},\"enterTopNodeName\":\"查电费\",\"lastEnterTopNodeName\":null,\"context\":{\"channel\":\"\",\"sys_counter\":{\"01查电费_muszm2dx_upf550gl\":1,\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\":1,\"查电费\":1,\"渠道标识判断_phokrvuf\":1},\"commonMonth\":\"\",\"authRefer\":1},\"collectInfo\":{},\"enterTopNodeIndex\":1,\"lastNodeId\":null,\"intent\":\"q_elec_charge\",\"entity\":{},\"dialogs\":[{\"dialogNodeName\":\"查电费\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"85eb8820-c869-4905-8982-28902b3d1a94\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":null},{\"dialogNodeName\":\"渠道标识判断_phokrvuf\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"a53e76a2-90fd-4d71-bd36-3bff3472ce97\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":2,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":\"非智能IVR渠道标识 ： \"},{\"dialogNodeName\":\"01查电费_muszm2dx_upf550gl\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"729ad4f1-26e0-4c97-a9f1-e765bfaa7e2d\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":\"您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！\"},{\"dialogNodeName\":\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"9cf1abf0-ddf6-4829-a86f-bf5329b64147\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"019e98ca-0fe8-49ed-a3cf-dcbbcc8252f5\",\"processName\":\"自动获取手机号验证身份_p1vibepk\",\"action\":\"PhoneN\",\"processType\":0,\"isResult\":false,\"value\":null}]},\"context\":{\"channel\":\"\",\"sys_counter\":{\"01查电费_muszm2dx_upf550gl\":1,\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\":1,\"查电费\":1,\"渠道标识判断_phokrvuf\":1},\"commonMonth\":\"\",\"authRefer\":1},\"botName\":\"测试\",\"botDesc\":null,\"botVersion\":9,\"agentType\":1,\"webhook\":false},\"code\":200,\"msg\":\"OK\"}";
        //百度知识库返回的数据信息
        JSONObject parse = JSON.parseObject(jsonStrResult);
        log.info("return 调用百度多轮接口:{}", parse);
        JSONObject dataJson = parse.getJSONObject("data");
//        String sessionId = dataJson.getString("sessionId");
        log.info("结束调用百度知识库接口:{}", LocalDateTime.now());
        int code = parse.getIntValue("code");
        String res = "";
        if (200 == code) {
            res = dataJson.getString("suggestAnswer");
        } else {
            res = XCCConstants.NGD_MISSING_MSG;
        }
        return res;
    }


    /**
     * 将百度返回的文本字段转为 指令和播报的内容
     *
     * @param glResText
     * @return
     */
    public static Map<String, String> convertResText(String glResText) {
        String retKey = "";//指令
        String retValue = "";//播报内容
        log.info("convertResText glResText:{}", glResText);
        if (StringUtils.isBlank(glResText)) {//话术为空
            retKey = XCCConstants.YYSR;//subZhiling
            retValue = XCCConstants.NGD_MISSING_MSG;//subContent
        } else {
            if (!glResText.contains("#")) {//不带#的话术
                retKey = XCCConstants.YYSR;//subZhiling
                retValue = glResText;//subContent
            } else {//带#的话术
                String[] split = glResText.split("#");
                retKey = split[0];//指令
                if (XCCConstants.ZHILING_STR.contains(retKey)) {//有指令
                    retValue = split[1];//内容
                } else {//无指令
                    retKey = XCCConstants.YYSR;
                    retValue = XCCConstants.NGD_MISSING_MSG;
                }
            }
        }
        Map<String, String> resMap = new HashMap<>();
        resMap.put("retKey", retKey);
        resMap.put("retValue", retValue);
        log.info("convertResText resMap:{}", resMap);
        return resMap;
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
