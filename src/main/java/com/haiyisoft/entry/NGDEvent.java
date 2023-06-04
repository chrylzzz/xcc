package com.haiyisoft.entry;

import lombok.Data;

/**
 * 每次调用NGD接收业务实体
 * Created by Chr.yl on 2023/6/3.
 *
 * @author Chr.yl
 */
@Data
public class NGDEvent {

    //ngd返回code
    private Integer code;
    //ngd返回msg
    private String msg;
    // answer 来源
    private String source;
    // 是否系统答复 : true 系统答复
    //连续两次系统回复转人工
    private boolean systemAnswer;
    //ngd返回的数据, 此字段根据百度api获取的最优answer
    private String answer;


    //----------------
    //播报指令
    private String retKey;
    //播报话术
    private String retValue;
    //----------------


}
