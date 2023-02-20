package com.haiyisoft.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.constant.XCCConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chr.yl on 2023/2/15.
 *
 * @author Chr.yl
 */
public class NGDUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String gxZskUrl = "http://10.100.104.20:8304/api/v2/core/query";//广西知识库测试环境地址

    public static String ZHILING_STR = "TDYT;YYSR;AJSR;RGYT;YWAJ;";

    /**
     * 广西知识库接口
     *
     * @param queryText
     * @return
     */
    public static String doGxZsk(String queryText) {
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
        param.put("context", context);//渠道标识，智能IVR为广西智能ivr标识
//        String jsonStrResult = HttpClientUtil.doPostJsonForGx(gxZskUrl, param.toJSONString());
        String jsonStrResult = "{\"time\":1669600747863,\"data\":{\"suggestAnswer\":\"YYSR#非智能IVR渠道标识 ： 您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！\",\"appendAnswers\":null,\"source\":\"task_based\",\"solved\":true,\"confidence\":0.9597217440605164,\"queryId\":\"2657c291-fd7d-4cf7-8f3f-87af8ae99ff8\",\"queryTime\":\"2022-11-28 09:59:07:687\",\"answerTime\":\"2022-11-28 09:59:07:863\",\"sessionId\":\"2d32b5b3-a98f-4e59-8ea1-6c5969771878\",\"actions\":[\"PhoneN\"],\"answer\":{\"intentMap\":{\"id\":\"5b6f21df-1203-4c08-ad35-ee5b16b435d0\",\"name\":\"q_elec_charge\",\"description\":\"\",\"agentId\":null,\"created\":null,\"updated\":null,\"confidence\":0.9597217440605164,\"source\":\"knn\",\"threshold\":0.0,\"system\":false,\"alias\":\"\",\"nameZh\":\"查询电量电费\",\"examples\":null,\"hasActiveCopy\":false,\"templateStr\":null,\"createdUserName\":null,\"createdUserId\":null,\"lastEditUserName\":null,\"lastEditUserId\":null},\"enterTopNodeName\":\"查电费\",\"lastEnterTopNodeName\":null,\"context\":{\"channel\":\"\",\"sys_counter\":{\"01查电费_muszm2dx_upf550gl\":1,\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\":1,\"查电费\":1,\"渠道标识判断_phokrvuf\":1},\"commonMonth\":\"\",\"authRefer\":1},\"collectInfo\":{},\"enterTopNodeIndex\":1,\"lastNodeId\":null,\"intent\":\"q_elec_charge\",\"entity\":{},\"dialogs\":[{\"dialogNodeName\":\"查电费\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"85eb8820-c869-4905-8982-28902b3d1a94\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":null},{\"dialogNodeName\":\"渠道标识判断_phokrvuf\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"a53e76a2-90fd-4d71-bd36-3bff3472ce97\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":2,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":\"非智能IVR渠道标识 ： \"},{\"dialogNodeName\":\"01查电费_muszm2dx_upf550gl\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"729ad4f1-26e0-4c97-a9f1-e765bfaa7e2d\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"c3a3e12e-73a9-4650-8686-839f156011e6\",\"processName\":\"查电费\",\"action\":\"\",\"processType\":0,\"isResult\":false,\"value\":\"您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！\"},{\"dialogNodeName\":\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\",\"webhook\":false,\"description\":null,\"dialogNodeId\":\"9cf1abf0-ddf6-4829-a86f-bf5329b64147\",\"processVersion\":1,\"isBackTrack\":false,\"endNode\":false,\"jumpBackValue\":null,\"outputIndex\":1,\"processId\":\"019e98ca-0fe8-49ed-a3cf-dcbbcc8252f5\",\"processName\":\"自动获取手机号验证身份_p1vibepk\",\"action\":\"PhoneN\",\"processType\":0,\"isResult\":false,\"value\":null}]},\"context\":{\"channel\":\"\",\"sys_counter\":{\"01查电费_muszm2dx_upf550gl\":1,\"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2\":1,\"查电费\":1,\"渠道标识判断_phokrvuf\":1},\"commonMonth\":\"\",\"authRefer\":1},\"botName\":\"测试\",\"botDesc\":null,\"botVersion\":9,\"agentType\":1,\"webhook\":false},\"code\":200,\"msg\":\"OK\"}";
        System.out.println("开始调用百度知识库接口:" + LocalDateTime.now());
        //百度知识库返回的数据信息
        JSONObject parse = JSON.parseObject(jsonStrResult);
        LOGGER.info("return 调用百度多轮接口:{}", parse);
        System.out.println("结束调用百度知识库接口:" + LocalDateTime.now());

        int code = parse.getIntValue("code");
//        System.out.println(code);
//        String msg = parse.getString("msg");
//        System.out.println(msg);
//        String string = parse.getJSONObject("data").getString("suggestAnswer");
//        System.out.println(string);
        String res = "";
        if (200 == code) {
            res = parse.getJSONObject("data").getString("suggestAnswer");
        } else {
            res = XCCConstants.NGD_ERROR;
        }

        return res;
    }


    /**
     * 广西用户编号和地区编码转换
     *
     * @param znivr_uid
     */
//    public static void convertYhbh2Dqbm(String znivr_uid) {
//        String dqbm4 = znivr_uid.substring(0, 4);
//        //地区编码
//        String znivr_dqbm = "";
//        /**
//         * 就是0432开头的用户，哪个地市都有可能
//         * 这批次的用户是从新电力公司迁移进来用户户，户号不变，根据地理行政单位迁移进不同的供电局
//         * 那除了0432开头的用户,都可以取用户编号前四位+00 这样吗
//         * 是的
//         */
//        if ("0432".equals(dqbm4)) {
//            znivr_dqbm = YhxxMap.get("DQBM").toString();
//        } else {
//            znivr_dqbm = dqbm4 + "00";
//        }
//        return znivr_dqbm;
//    }


    /**
     * 将百度返回的文本字段转为 指令和播报的内容
     *
     * @param glResText
     * @return
     */
    public static Map<String, String> convertResText(String glResText) {
        String retKey = "";//指令
        String retValue = "";//播报内容
        System.out.println("glResText:" + glResText);
//        if (StringUtils.isBlank(glResText)) {//话术为空
//            retKey = "YYSR";//subZhiling
//            retValue = "您的问题我不理解，请换个问法。如需人工服务，请讲 转人工";//subContent
//        } else {
        if (!glResText.contains("#")) {//不带#的话术
            retKey = "YYSR";//subZhiling
            retValue = glResText;//subContent
        } else {//带#的话术
            String[] split = glResText.split("#");
            retKey = split[0];//指令
            if (ZHILING_STR.contains(retKey)) {//有指令
                retValue = split[1];//内容
            } else {//无指令
                retKey = "YYSR";
                retValue = "您的问题我不理解，请换个问法。如需人工服务，请讲 转人工";
            }
        }
//        }
        Map<String, String> resMap = new HashMap<>();
        resMap.put("retKey", retKey);
        resMap.put("retValue", retValue);
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

    public static void main(String[] args) {
//        Map<String, String> map = convertResText("AJSR#我就是是是是十四十四师");
//        System.out.println(map);
        doGxZsk("查电费");

    }
}
