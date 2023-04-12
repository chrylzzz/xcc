package com.haiyisoft.constant;

/**
 * 不可配置
 * XCC和NGD常量
 * Created By Chryl on 2023-02-08.
 */
public class XCCConstants {

    /********************************************xswitch相关********************************************/
    //Nats 地址
    public static final String NATS_URL = "nats://hy:h8klu6bRwW@nats.xswitch.cn:4222";
    //Ctrl 订阅主题
    public static final String XCTRL_SUBJECT = "cn.xswitch.ctrl";

    public static final String XCTRL_SUBJECT_DESTROY = "cn.xswitch.ctrl.event";
    //TTS引擎
    public static final String TTS_ENGINE = "ali";
    //ASR引擎
    public static final String ASR_ENGINE = "ali";
    //XSwitch 服务地址
    public static final String XSWITCH_SERVICE = "xswitchService";
    //uuid
    public static final String XCTRL_UUID = "";
    //Node
    public static final String NODE_SERVICE_PREFIX = "cn.xswitch.node.";
    //XNode侧的Subject
    public static final String XNODE_SUBJECT_PREFIX = "cn.xswitch.node.";
    //Ctrl
    public static final String XCTRL_SERVICE_PREFIX = "cn.xswitch.ctrl.";
    //XCtrl侧的Subject
    public static final String XCTRL_SUBJECT_PREFIX = "cn.xswitch.ctrl.";


    /**
     * 来话Channel状态:
     * START：来话第一个事件，XCtrl应该从该事件开始处理，第一个指令必须是Accept或Answer
     * RINGING：振铃
     * ANSWERED：应答
     * BRIDGE：桥接
     * UNBRIDGE： 断开桥接
     * CHANNEL_DESTROY：挂机
     * <p>
     * 去话Channel状态:
     * CALLING：去话第一个事件
     * RINGING：振铃
     * ANSWERED：应答
     * MEDIA：媒体建立
     * BRIDGE：桥接
     * READY：就绪
     * UNBRIDGE： 断开桥接
     * CHANNEL_DESTROY： 挂机
     */
    //START：来话第一个事件
    public static final String Channel_START = "START";
    //小会
    public static final String CHANNEL_DESTROY = "CHANNEL_DESTROY";
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
    public static final String Event_NativeEvent = "Event.NativeEvent";
    public static final String Event_DetectedFace = "Event.DetectedFace";

    //不可打断
    public static final boolean NO_BREAK = true;
    //TTS，即语音合成
    public static final String PLAY_TTS = "TEXT";
    //文件
    public static final String PLAY_FILE = "FILE";
    //dtmf结束符
    public static final String DTMF_TERMINATORS = "#";
    //
    public static final String ACCEPT = "Xnode.Accept";
    //
    public static final String SET_VAR = "Xnode.SetVar";
    //当前通道状态
    public static final String GET_STATE = "Xnode.GetState";
    //应答
    public static final String ANSWER = "Xnode.Answer";
    //播报
    public static final String PLAY = "Xnode.Play";
    //放音收音
    public static final String DETECT_SPEECH = "Xnode.DetectSpeech";
    //放音收号
    public static final String READ_DTMF = "Xnode.ReadDTMF";
    //转接
    public static final String BRIDGE = "Xnode.Bridge";
    //挂断
    public static final String HANGUP = "Xnode.Hangup";

    //flow_control：呼叫控制，跟程控交换机中的控制方式类似，略有不同。
    //NONE：无控制，任意方挂机不影响其它一方
    //CALLER：主叫控制，a-leg挂机后b-leg自动挂机
    //CALLEE：被叫控制，b-leg挂机后a-leg自动挂机
    //ANY：互不控制，任一方挂机后另一方也挂机
    public static final String ANY = "ANY";

    //success
    public static final int JSONRPC_OK = 200;
    //NOTIFY
    public static final int JSONRPC_NOTIFY = 202;
    //
    public static final int JSONRPC_CLIENT_ERROR = 400;
    //cannot locate session by uuid
    public static final int JSONRPC_CANNOT_LOCATE_SESSION_BY_UUID = 404;
    //error
    public static final int JSONRPC_SERVER_ERROR = 500;


    /********************************************xswitch相关********************************************/

    /********************************************ngd相关********************************************/

    //百度知识库测试环境地址
    public static final String NGD_QUERY_URL = "https://api-ngd.baidu.com/api/v2/core/query";
    //百度NGD auth
    public static final String NGD_QUERY_AUTHORIZATION = "NGD 43b6f0be-4894-466f-a346-08046d935035";
    //欢迎语
    public static final String WELCOME_TEXT = "我是智能美美, 您要咨询什么问题, 您请说";
    //XCC返回失败话术
    public static final String XCC_MISSING_MSG = "YYSR#您的问题我不理解，请换个问法。如需人工服务，请讲 转人工";
    //XCC返回失败话术
    public static final String XCC_MISSING_ANSWER = "您的问题我不理解，请换个问法。如需人工服务，请讲 转人工";
    //NGD识别失败话术
    public static final String NGD_MISSING_MSG = "我不太理解您的意思";
    //语音输入
    public static final String YYSR = "YYSR";
    //按键输入
    public static final String AJSR = "AJSR";
    //一位按键
    public static final String YWAJ = "YWAJ";
    //人工意图
    public static final String RGYT = "RGYT";
    //指令集合
    public static final String RET_KEY_STR = "YYSR;AJSR;RGYT;YWAJ;";
    //智能ivr渠道
    public static final String CHANNEL_IVR = "智能IVR";
    //ngd话术分隔符
    public static final String NGD_SEPARATOR = "#";


    /********************************************ngd相关********************************************/

}
