package com.haiyisoft.handler;

import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.XCCEvent;

/**
 * Created by Chr.yl on 2023/6/30.
 *
 * @author Chr.yl
 */
public class WebHookHandler {

    /**
     * 发送短信
     */
    public static XCCEvent sendMessage(IVREvent ivrEvent, String retValue) {
        //来电号码
        String cidNumber = ivrEvent.getCidNumber();
        //华为会话标识
        String icdCallerId = ivrEvent.getIcdCallerId();
        //号码归属地
        String phoneAdsCode = ivrEvent.getPhoneAdsCode();
        
        return null;
    }

    /**
     * 对话记录
     */
    public static XCCEvent writeDhnr() {

        return null;
    }
}
