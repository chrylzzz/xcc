package com.haiyisoft.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每次调用NGD接收业务实体
 * Created by Chr.yl on 2023/6/3.
 *
 * @author Chr.yl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NGDEvent {

    //ngd返回code
    private Integer code;
    //ngd返回msg
    private String msg;
    // answer 来源
    private String source;
    //ngd返回的数据, 此字段根据百度api获取的最优answer
    private String answer;
    //ngd返回solved 是否解决
    private boolean solved;


    //----------------
    //播报指令
    private String retKey;
    //播报话术
    private String retValue;
    //----------------


}
