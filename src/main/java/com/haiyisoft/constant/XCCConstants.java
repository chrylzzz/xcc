package com.haiyisoft.constant;

/**
 * XCC常量
 * Created By Chryl on 2023-02-08.
 */
public class XCCConstants {
    //XSwitch 服务地址
    public static final String XSWITCH_SERVICE = "xswitchService";
    //Nats 地址
    public static final String NATS_URL = "NATS_URL";
    //Ctrl 订阅主题
    public static final String XCTRL_SUBJECT = "cn.xswitch.ctrl.subject";
    //    public static final String dialString = "";
    public static final String subject_prefix = "";
    //
    public static final String XCTRL_UUID = "";


    /**
     * 来话Channel状态:
     * START：来话第一个事件，XCtrl应该从该事件开始处理，第一个指令必须是Accept或Answer
     * RINGING：振铃
     * ANSWERED：应答
     * BRIDGE：桥接
     * UNBRIDGE： 断开桥接
     * DESTROY：挂机
     * <p>
     * 去话Channel状态:
     * CALLING：去话第一个事件
     * RINGING：振铃
     * ANSWERED：应答
     * MEDIA：媒体建立
     * BRIDGE：桥接
     * READY：就绪
     * UNBRIDGE： 断开桥接
     * DESTROY： 挂机
     */
    public static final String Channel_START = "START";
    //CALLING：去话第一个事件
    public static final String Channel_CALLING = "CALLING";
    //RINGING：振铃
    public static final String Channel_RINGING = "RINGING";
    //BRIDGE：桥接
    public static final String Channel_BRIDGE = "BRIDGE";
    //READY：就绪
    public static final String Channel_READY = "READY";
    //MEDIA：媒体建立
    public static final String Channel_MEDIA = "MEDIA";
    //Channel事件
    public static final String Event_Channel = "Event.Channel";

    //不可打断
    public static final boolean NO_BREAK = false;
    //TTS，即语音合成
    public static final String PLAY_TTS = "TEXT";
    //文件
    public static final String PLAY_FILE = "FILE";
}
