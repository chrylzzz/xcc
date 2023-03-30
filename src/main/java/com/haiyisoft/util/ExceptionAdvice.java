package com.haiyisoft.util;

import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.IVRModel;
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
    public static IVRModel handleException(Exception e) {
        IVRModel ivrModel = new IVRModel();
        ivrModel.setCode(500);
        ivrModel.setXccMsg("");
        log.error("服务器发生异常：{}", e);
        return ivrModel;
    }

    /**
     * 404 挂机
     * 500 5000挂机
     *
     * @param ivrModel
     * @return
     */
    public static boolean handleXcc(IVRModel ivrModel) {
        Integer code = ivrModel.getCode();
        if (code == XCCConstants.JSONRPC_CANNOT_LOCATE_SESSION_BY_UUID
                || code == XCCConstants.JSONRPC_SERVER_ERROR) {
            //生产环境500改为转人工
            return true;
        } else {
            return false;
        }

    }

}
