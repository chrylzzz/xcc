package com.haiyisoft.handler;

import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.model.IVRModel;

/**
 * 营销系统
 * Created by Chr.yl on 2023/7/11.
 *
 * @author Chr.yl
 */
public class PMSHandler {

    /**
     * 保存来话意图信息
     *
     * @param ivrEvent
     */
    public static void saveIntent(IVREvent ivrEvent) {
        String ivrStartTime = ivrEvent.getIvrStartTime();
        String cidPhoneNumber = ivrEvent.getCidPhoneNumber();
        String fsCallerId = ivrEvent.getFsCallerId();
        String icdCallerId = ivrEvent.getIcdCallerId();
        boolean transferFlag = ivrEvent.isTransferFlag();
        String zl = "";//指令
//        (String cidPhoneNumber, String fsCallerId, String icdCallerId,
//        String ivrStartTime, String ivrEndTime,
//        String artificialType, String ivrValidCallType, String ivrFinishType)
        String artificialType;//是否转人工
        if (transferFlag) {
            artificialType = "1";
        } else {
            artificialType = "0";
        }
//        new IVRModel()

    }

    /**
     * 保存通话数据信息
     *
     * @param ivrEvent
     */
    public static void saveCallData(IVREvent ivrEvent) {

    }

}
