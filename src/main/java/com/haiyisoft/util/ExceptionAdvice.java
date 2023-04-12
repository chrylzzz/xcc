package com.haiyisoft.util;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.IVREvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Chr.yl on 2023/3/30.
 *
 * @author Chr.yl
 */
@Slf4j
public class ExceptionAdvice {

    /**
     * handle invoke xcc exception:
     *
     * @param e
     * @return
     */
    public static IVREvent handleException(Exception e, IVREvent ivrEvent) {
        ivrEvent.setCode(500);
        ivrEvent.setXccMsg("服务器发生异常:" + e);
        log.error("服务器发生异常：{}", e);
        return ivrEvent;
    }

    /**
     * 404 挂机
     * 500 挂机
     *
     * @param ivrEvent
     * @return
     */
    public static boolean handleXccAgent(IVREvent ivrEvent) {
        Integer code = ivrEvent.getCode();
        if (code == XCCConstants.JSONRPC_CANNOT_LOCATE_SESSION_BY_UUID
                || code == XCCConstants.JSONRPC_SERVER_ERROR) {
            //生产环境500改为转人工,404挂断
            return true;
        } else {
            return false;
        }

    }

    /**
     * 处理 抱歉,我不太理解您的意思
     * 两次识别失败转人工
     *
     * @param ivrEvent
     * @return
     */
    public static IVREvent handleNgdAgent(IVREvent ivrEvent) {
        String retValue = ivrEvent.getRetValue();
        if (retValue.contains(XCCConstants.NGD_MISSING_MSG)) {
            int agentTime = ivrEvent.getAgentTime();
            ivrEvent.setAgentTime(agentTime + 1);
            if (agentTime >= 2) {
                log.info("转人工:{}", ivrEvent);
                ivrEvent.setAgent(true);
            }
        }
        return ivrEvent;

    }
}
