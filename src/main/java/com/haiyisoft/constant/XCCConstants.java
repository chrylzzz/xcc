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
    //Channel事件 , XCC-BINDINGS
    //
    public static final String Event_Channel = "Event.Channel";
    //
    public static final String Event_NativeEvent = "Event.NativeEvent";
    //
    public static final String Event_DetectedFace = "Event.DetectedFace";
    //HEARTBEAT
    public static final String Event_NodeUpdate = "Event.NodeUpdate";

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

    //--------------------xcc识别返回的 code
    //100：临时响应，实际的响应消息将在后续以异步的方式返回。
    public static final int JSONRPC_TEMP = 100;
    //200：成功。
    public static final int OK = 200;
    //202：请求已成功收到，但尚不知道结果，后续的结果将以事件（NOTIFY）的形式发出。
    public static final int JSONRPC_NOTIFY = 202;
    //400：客户端错误，多发生在参数不全或不合法的情况。
    public static final int JSONRPC_CLIENT_ERROR = 400;
    //404: uuid错误或者用户挂机时调用xcc (cannot locate session by uuid)
    public static final int JSONRPC_CANNOT_LOCATE_SESSION_BY_UUID = 404;
    //410：Gone。发生在放音或ASR检测过程中用户侧挂机的情况。
    public static final int JSONRPC_USER_HANGUP = 410;
    //500：内部错误。
    public static final int JSONRPC_SERVER_ERROR = 500;
    //555: 自定义错误码
    public static final int CODE_CHRYL_ERROR = 555;
    //6xx: 系统错误，如发生在关机或即将关机的情况下，拒绝呼叫。
    public static final int JSONRPC_CODE_SYSTEM_ERROR = 6;
    //--------------------xcc识别返回 code

    //--------------------xcc识别返回的type error
    //xcc返回type
    //语音识别完成 : 当 type = Speech.End 时无error字段
    public static final String RECOGNITION_TYPE_SPEECH_END = "Speech.End";
    //识别错误 : 当 type = ERROR 时 ,error=no_input,speech_timeout
    public static final String RECOGNITION_TYPE_ERROR = "ERROR";
    public static final String RECOGNITION_ERROR_SPEECH_TIMEOUT = "speech_timeout";
    public static final String RECOGNITION_ERROR_NO_INPUT = "no_input";
    //--------------------xcc识别返回的type error


    //外呼(Dial)/挂断(Hangup)的结果中cause为成功或失败原因，列表如下：
    //SUCCESS：成功，可以进行下一步操作。
    public static final String SUCCESS = "SUCCESS";
    //USER_BUDY：被叫忙。
    public static final String USER_BUDY = "USER_BUDY";
    //CALL_REJECTED：被叫拒接。
    public static final String CALL_REJECTED = "CALL_REJECTED";
    //NO_ROUTE_DESTINATION：找不到路由。
    public static final String NO_ROUTE_DESTINATION = "NO_ROUTE_DESTINATION";

    /********************************************xswitch相关********************************************/

    /********************************************ngd相关********************************************/

    //百度知识库测试环境地址
    public static final String NGD_QUERY_URL = "https://api-ngd.baidu.com/api/v2/core/query";
    //百度NGD auth
    public static final String NGD_QUERY_AUTHORIZATION = "NGD 43b6f0be-4894-466f-a346-08046d935035";
    //欢迎语
    public static final String WELCOME_TEXT = "我是智能美美, 您要咨询什么问题, 您请说";
    //转人工话术
    public static final String ARTIFICIAL_TEXT = "正在为您转接人工坐席, 请稍后";
    //XCC返回失败话术
    public static final String XCC_MISSING_MSG = "YYSR#您的问题我不理解，请换个问法。如需人工服务，请讲 转人工";
    //XCC返回失败话术
    public static final String XCC_MISSING_ANSWER = "您的问题我不理解，请换个问法。如需人工服务，请讲 转人工";
    //语音输入
    public static final String YYSR = "YYSR";
    //按键输入
    public static final String AJSR = "AJSR";
    //一位按键
    public static final String YWAJ = "YWAJ";
    //人工意图
    public static final String RGYT = "RGYT";
    //指令集合数组
    public static final String[] RET_KEY_STR_ARR = {"YYSR", "AJSR", "RGYT", "YWAJ"};
    //智能ivr渠道
    public static final String CHANNEL_IVR = "智能IVR";
    //ngd话术分隔符
    public static final String NGD_SEPARATOR = "#";
    //unMatch : 百度知识库接口未匹配: 返回抱歉,我不太理解您的意思
    public static final String NGD_QUERY_UNMATCH = "unMatch";

    /**
     * "solved": true
     * source:为知识库返回问题来源
     * faq : faq回复
     * task_based :流程恢复
     * clarify: 澄清
     * <p>
     * "solved": false
     * system: 机器回复
     * none: 无
     */
    public static final String SOURCE_FAQ = "faq";
    public static final String SOURCE_TASK_BASED = "task_based";
    public static final String SOURCE_CLARIFY = "clarify";
    public static final String SOURCE_SYSTEM = "system";
    public static final String SOURCE_NONE = "none";
    //ngd未触发答案
    //NGD识别失败话术 "source":"task_based"
    public static final String NGD_MISSING_MSG = "这个家伙很懒,没留下答案就跑了";
    //"source": "system"
    public static final String NGD_UNDERSTAND_MSG = "抱歉,我不太理解您的意思";
    //ngd 用户请求过于频繁，请稍后再试
    public static final int NGD_REQUEST_TO_MUCH = 4000019;
    //bot token错误
    public static final int NGD_BOT_TOKEN_ERREO = 4002409;
    /********************************************ngd相关********************************************/

    /********************************************IVR相关********************************************/
    //多节点配置
    public static final String NODE = "node";
    public static final String NATS = "nats";
    //换行符
    public static final String NL = "\\n";
    //转义符
    public static final String ESCAPE_CHARACTER = "\\";
    //input
    public static final String INPUT = "input";
    //IVR失败转人工次数
    public static final int DEFAULT_TRANSFER_TIME = 1;
    public static final int TRANSFER_ARTIFICIAL_TIME = 4;


    /********************************************IVR相关********************************************/

}
