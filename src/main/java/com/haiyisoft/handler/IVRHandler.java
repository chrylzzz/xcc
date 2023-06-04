package com.haiyisoft.handler;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.util.XCCUtil;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Chr.yl on 2023/3/30.
 *
 * @author Chr.yl
 */
@Slf4j
public class IVRHandler {

    /**
     * 处理xcc code
     * 200 成功
     * 202
     * 400
     * 404
     * 410
     * 500
     * 555
     * 6xx
     *
     * @param xccEvent
     * @return true xcc返回异常,false xcc返回正常
     */
    public static boolean handleXccAgent(XCCEvent xccEvent, IVREvent ivrEvent, ChannelEvent channelEvent, Connection nc) {
        Integer code = xccEvent.getCode();
        String type = xccEvent.getType();
        String error = xccEvent.getError();
        String xccRecognitionResult = xccEvent.getXccRecognitionResult();
        boolean handleXcc;
        log.info("handleXccAgent code : {}  , xccRecognitionResult : {} , type : {} , error : {}", code, xccRecognitionResult, type, error);
        if (StringUtils.isBlank(xccRecognitionResult)) {
            if (XCCConstants.OK == code) {//200
                //Speech.End,continue;
//                if (XCCConstants.RECOGNITION_TYPE_SPEECH_END.equals(type)) {
//                    handleXcc = false;
//                }
                //当 type = ERROR 时 , error = no_input
                if (XCCConstants.RECOGNITION_TYPE_ERROR.equals(type)) {//ERROR
                    //没说话,话术需要修改

                }
                handleXcc = false;
            } else if (XCCConstants.JSONRPC_TEMP == code) {//100
                handleXcc = true;
            } else if (XCCConstants.JSONRPC_NOTIFY == code) {//202
                //没按键

                handleXcc = false;
            } else if (XCCConstants.JSONRPC_CLIENT_ERROR == code) {//400
                handleXcc = true;
            } else if (XCCConstants.JSONRPC_CANNOT_LOCATE_SESSION_BY_UUID == code) {//404
                //uuid参数错误或者用户主动挂机时调用xswitch
                //挂断双方
                log.info("hangup this call channelId: {} , because 410 ", ivrEvent.getChannelId());
                handleXcc = true;
            } else if (XCCConstants.JSONRPC_CANNOT_USER_HUGUP == code) {//410
                //发生在放音或ASR检测过程中用户侧挂机的情况
                XCCUtil.hangup(nc, channelEvent);
                handleXcc = true;
            } else if (XCCConstants.JSONRPC_SERVER_ERROR == code) {//500
                //xswitch出错
                handleXcc = true;
            } else if (XCCConstants.CODE_CHRYL_ERROR == code) {//555
                //自定义错误码
                handleXcc = true;
            } else if (XCCConstants.JSONRPC_CODE_SYSTEM_ERROR == code) {//6xx
                //系统错误
                handleXcc = true;
            } else {
                handleXcc = true;
            }
        } else {
            handleXcc = false;
        }
        return handleXcc;
    }


    //连续两次xcc no_input 转换人工
    public static void handleArtificial() {

    }

    /**
     * 两次识别失败转人工
     *
     * @param ivrEvent
     * @return
     */
    public static IVREvent handleNgdAgent(IVREvent ivrEvent) {
//        String retValue = ivrEvent.getRetValue();
//        if (retValue.contains(XCCConstants.NGD_MISSING_MSG)) {
//            int agentTime = ivrEvent.getAgentTime();
//            ivrEvent.setAgentTime(agentTime + 1);
//            if (agentTime >= 2) {
//                log.info("转人工:{}", ivrEvent);
//                ivrEvent.setAgent(true);
//            }
//        }
        return ivrEvent;

    }

    /**
     * 赋值xcc返回数据
     *
     * @param code
     * @param message
     * @param xccRecognitionResult
     * @return
     */
    public static XCCEvent xccEventSetVar(Integer code, String message, String xccRecognitionResult, String type, String error, String xccMethod) {
        log.info("xccEventSetVar code : {} , message : {} , xccRecognitionResult : {} , type : {} , error : {} , xccMethod : {}", code, message, xccRecognitionResult, type, error, xccMethod);
        XCCEvent xccEvent = new XCCEvent();
        xccEvent.setCode(code);
        xccEvent.setMessage(message);
        xccEvent.setXccRecognitionResult(xccRecognitionResult);
        xccEvent.setType(type);
        xccEvent.setError(error);
        xccEvent.setXccMethod(xccMethod);

        return xccEvent;
    }

}
