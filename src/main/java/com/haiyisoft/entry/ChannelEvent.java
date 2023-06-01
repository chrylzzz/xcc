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
    private String nodeUuid = "";
    //channel id
    private String uuid = "";
    //Channel state
    private String state = "";


}


