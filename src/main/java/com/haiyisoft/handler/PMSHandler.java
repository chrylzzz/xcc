package com.haiyisoft.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.enumerate.EnumXCC;
import com.haiyisoft.model.IVRModel;
import com.haiyisoft.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 营销系统业务处理
 * Created by Chr.yl on 2023/7/11.
 *
 * @author Chr.yl
 */
@Slf4j
public class PMSHandler {

    /**
     * 保存来话意图信息
     *
     * @param ivrEvent
     * @param ngdEvent
     */
    public static void saveIntent(IVREvent ivrEvent, NGDEvent ngdEvent) {
        String ivrStartTime = ivrEvent.getIvrStartTime();
        String cidPhoneNumber = ivrEvent.getCidPhoneNumber();
        String fsCallerId = ivrEvent.getChannelId();
        String icdCallerId = ivrEvent.getIcdCallerId();
        String intent = ngdEvent.getIntent();
        if (StringUtils.isBlank(intent)) {
            //无意图默认为其他意图
            intent = EnumXCC.IVR_INTENT_QT.getValue();
        }
        IVRModel ivrModel = new IVRModel(cidPhoneNumber, fsCallerId, icdCallerId, ivrStartTime, intent, "", "", "");
        String jsonParam = JSON.toJSONString(ivrModel);
        log.info("SaveZnIVRLhytForGx,pms接口入参:{}", jsonParam);
        String postJson = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getPmsUrl() + XCCConstants.SAVE_INTENT_URL, jsonParam);
        log.info("SaveZnIVRLhytForGx,pms接口出参:{}", postJson);
    }

    /**
     * 保存通话数据信息
     *
     * @param ivrEvent
     */
    public static void saveCallInfo(IVREvent ivrEvent, NGDEvent ngdEvent) {
        String phoneAdsCode = ivrEvent.getPhoneAdsCode();
        String ivrStartTime = ivrEvent.getIvrStartTime();
        String cidPhoneNumber = ivrEvent.getCidPhoneNumber();
        String fsCallerId = ivrEvent.getChannelId();
        String icdCallerId = ivrEvent.getIcdCallerId();
        boolean transferFlag = ivrEvent.isTransferFlag();
        int artificialType, ivrValidCallType, ivrCallEndNormalType;//是否转人工,是否有效通话,是否正常结束: 0否1是

        if (transferFlag) {
            artificialType = EnumXCC.IVR_ARTIFICIAL_TRUE.valueParseIntValue();
        } else {
            artificialType = EnumXCC.IVR_ARTIFICIAL_FALSE.valueParseIntValue();
        }
//        ivrValidCallType = "1";
//        ivrCallEndNormalType = "1";
        ivrValidCallType = EnumXCC.IVR_VALID_CALL_TRUE.valueParseIntValue();
        ivrCallEndNormalType = EnumXCC.IVR_FINISH_TRUE.valueParseIntValue();
        IVRModel ivrModel = new IVRModel(cidPhoneNumber, fsCallerId, icdCallerId, ivrStartTime, artificialType, ivrValidCallType, ivrCallEndNormalType, phoneAdsCode);
        String jsonParam = JSON.toJSONString(ivrModel);
        log.info("SAVE_CALL_DATA, pms接口入参:{}", jsonParam);
        String postJson = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getPmsUrl() + XCCConstants.SAVE_CALL_DATA_URL, jsonParam);
        log.info("SAVE_CALL_DATA, pms接口出参:{}", postJson);
    }

    /**
     * 保存满意度
     *
     * @param ivrEvent
     * @param ngdEvent
     */
    public static void saveRate(IVREvent ivrEvent, NGDEvent ngdEvent) {
        String cidPhoneNumber = ivrEvent.getCidPhoneNumber();
        String fsCallerId = ivrEvent.getChannelId();
        String icdCallerId = ivrEvent.getIcdCallerId();
        String rate = ngdEvent.getRate();
        if (StringUtils.isBlank(rate)) {
            //无满意度默认为未评价
            rate = EnumXCC.IVR_RATE_NEUTRAL.getValue();
        }
        IVRModel ivrModel = new IVRModel(cidPhoneNumber, fsCallerId, icdCallerId, "", "", rate);
        String jsonParam = JSON.toJSONString(ivrModel);
        log.info("SAVE_RATE_DATA_URL, pms接口入参:{}", jsonParam);
        String postJson = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getPmsUrl() + XCCConstants.SAVE_RATE_DATA_URL, jsonParam);
        log.info("SAVE_RATE_DATA_URL, pms接口出参:{}", postJson);

    }

    /**
     * 查询欢迎语
     */
    public static String welcomeText() {
        IVRModel ivrModel = new IVRModel("19");
        String jsonParam = JSON.toJSONString(ivrModel);
        log.info("QUERY_BBHS__URL, pms接口入参:{}", jsonParam);
        String postJson = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getPmsUrl() + XCCConstants.QUERY_BBHS__URL, jsonParam);
        log.info("QUERY_BBHS__URL, pms接口出参:{}", postJson);
        JSONObject jsonObject = JSON.parseObject(postJson);
        JSONArray data = jsonObject.getJSONArray("data");
        String hsnr = "";
        if (data != null) {
            JSONObject dataJSONObject = data.getJSONObject(0);
            String hsbh = dataJSONObject.getString("hsbh");//话术编号
            if ("99".equals(hsbh)) {//欢迎语话术编号为99
                hsnr = dataJSONObject.getString("hsnr");//话术内容
            }
        }

        //欢迎语赋值,若失败,赋值默认
        if (StringUtils.isBlank(hsnr)) {
            hsnr = XCCConstants.DEFAULT_WELCOME_TEXT;
        }
        log.info("QUERY_BBHS__URL, welcomeText: {}", hsnr);
        return hsnr;
    }

}
