package com.haiyisoft.entry;

import lombok.Data;

/**
 * xcc channel model
 * Created by Chr.yl on 2023/3/7.
 *
 * @author Chr.yl
 */
@Data
public class ChannelEvent {

    //xswitch node id
    private String nodeUuid;
    //channel id
    private String uuid;
    //Channel state
    private String state;
    //随路数据 sip_h_User-to-User：calld、手机号、来话手机所对应的后缀码
    private String sipHeaderU2U;

}


