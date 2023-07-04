package com.haiyisoft.entry;

import com.alibaba.fastjson2.JSONArray;
import com.haiyisoft.constant.XCCConstants;
import lombok.Data;

/**
 * IVR业务实体
 * Created by Chr.yl on 2023/3/7.
 *
 * @author Chr.yl
 */
@Data
public class IVREvent {

    //xcc call id
    private String channelId;
    //转人工次数 : 连续2次无法处理则转人工 ,默认值为1,transferTime == 3时transferFlag=true,转人工
    private Integer transferTime;
    //是否转人工 : true 转人工 , time=3时赋值true
    private boolean transferFlag;
    //一通电话的json对话记录
    private JSONArray ngdNodeMetadataArray;

    //----------------华为平台sip解析数据
    //华为caller id
    private String icdCallerId;
    //手机归属地编码
    private String phoneAdsCode;
    //来电手机号码
    private String cidNumber;
    //----------------华为平台sip解析数据

    public IVREvent(String channelId) {
        this.channelId = channelId;
        this.transferTime = XCCConstants.DEFAULT_TRANSFER_TIME;
        this.transferFlag = false;
        this.ngdNodeMetadataArray = new JSONArray();
    }


}
