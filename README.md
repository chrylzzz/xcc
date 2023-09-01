# XSwitch / NGD 文档

### Documentation ###

* [智能知识库-官网](https://login.bce.baidu.com)
* [XSwitch-官网](https://docs.xswitch.cn)
* [xcc-example](https://git.xswitch.cn/xswitch/xcc-examples)
* [xcc-api](https://xswitch.cn/docs/xswitch-xcc.html)

### 知识库请求格式 ###

curl -X POST 'https://api-ngd.baidu.com/api/v2/core/query' \
-H 'Authorization: NGD 43b6f0be-4894-466f-a346-08046d935035' \
-H 'Content-Type: application/json' \
--data-raw '{
"channel" : "智能IVR",
"queryText" : "查电费",
"sessionId" : "a4b83e63-d722-41e2f-8946-551582fe1122b917111",
"context": {
"ivr_phone" : "13287983898"
}
}'

### 知识库返回数据格式 ###

{"time":1669600747863,"data":{"suggestAnswer":"非智能IVR渠道标识 ：
您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！","appendAnswers":null,"source":"task_based","solved":true,"
confidence":0.9597217440605164,"queryId":"2657c291-fd7d-4cf7-8f3f-87af8ae99ff8","queryTime":"2022-11-28 09:59:07:687","
answerTime":"2022-11-28 09:59:07:863","sessionId":"2d32b5b3-a98f-4e59-8ea1-6c5969771878","actions":["PhoneN"],"
answer":{"intentMap":{"id":"5b6f21df-1203-4c08-ad35-ee5b16b435d0","name":"q_elec_charge","description":"","agentId":
null,"created":null,"updated":null,"confidence":0.9597217440605164,"source":"knn","threshold":0.0,"system":false,"
alias":"","nameZh":"查询电量电费","examples":null,"hasActiveCopy":false,"templateStr":null,"createdUserName":null,"
createdUserId":null,"lastEditUserName":null,"lastEditUserId":null},"enterTopNodeName":"查电费","lastEnterTopNodeName":
null,"context":{"channel":"","sys_counter":{"01查电费_muszm2dx_upf550gl":1,"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2":
1,"查电费":1,"渠道标识判断_phokrvuf":1},"commonMonth":"","authRefer":1},"collectInfo":{},"enterTopNodeIndex":1,"
lastNodeId":null,"intent":"q_elec_charge","entity":{},"
dialogs":[{"dialogNodeName":"查电费","webhook":false,"description":null,"dialogNodeId":"85eb8820-c869-4905-8982-28902b3d1a94","processVersion":1,"isBackTrack":false,"endNode":false,"jumpBackValue":null,"outputIndex":1,"processId":"c3a3e12e-73a9-4650-8686-839f156011e6","processName":"查电费","action":"","processType":0,"isResult":false,"value":null},{"dialogNodeName":"渠道标识判断_phokrvuf","webhook":false,"description":null,"dialogNodeId":"a53e76a2-90fd-4d71-bd36-3bff3472ce97","processVersion":1,"isBackTrack":false,"endNode":false,"jumpBackValue":null,"outputIndex":2,"processId":"c3a3e12e-73a9-4650-8686-839f156011e6","processName":"查电费","action":"","processType":0,"isResult":false,"value":"非智能IVR渠道标识 ： "},{"dialogNodeName":"01查电费_muszm2dx_upf550gl","webhook":false,"description":null,"dialogNodeId":"729ad4f1-26e0-4c97-a9f1-e765bfaa7e2d","processVersion":1,"isBackTrack":false,"endNode":false,"jumpBackValue":null,"outputIndex":1,"processId":"c3a3e12e-73a9-4650-8686-839f156011e6","processName":"查电费","action":"","processType":0,"isResult":false,"value":"您好，查询电费需要三个步骤，身份查询-信息验证-电费信息播报！"},{"dialogNodeName":"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2","webhook":false,"description":null,"dialogNodeId":"9cf1abf0-ddf6-4829-a86f-bf5329b64147","processVersion":1,"isBackTrack":false,"endNode":false,"jumpBackValue":null,"outputIndex":1,"processId":"019e98ca-0fe8-49ed-a3cf-dcbbcc8252f5","processName":"自动获取手机号验证身份_p1vibepk","action":"PhoneN","processType":0,"isResult":false,"value":null}]},"
context":{"channel":"","sys_counter":{"01查电费_muszm2dx_upf550gl":1,"自动获取手机号_llmudwks_v4qfyfcu_pvh888y2":1,"
查电费":1,"渠道标识判断_phokrvuf":1},"commonMonth":"","authRefer":1},"botName":"测试","botDesc":null,"botVersion":9,"
agentType":1,"webhook":false},"code":200,"msg":"OK"}

### X C C 状态码 ###

JSON-RPC相当于一个信封。在信封内部，是XCC
API具体请求的内容，统一放到params中。信封中的出错信息通常是因为信封出错导致的，如必选参数不存在、方法不存在等。如果信封合法，则具体的返回结果都放到result中，返回结果中均有如下参数：

code：代码，参照HTTP代码规范，如2xx代表成功，4xx代表客户端错误，5xx代表服务端错误等。
message：对代码的解释。有一些message中会带有一些00开头的错误码，这些错误码没有任何含义，仅为方便追踪错误而设。
uuid：如果请求中有uuid，则结果中也有uuid，代表当前的Channel UUID。

通用code返回值说明
100：临时响应，实际的响应消息将在后续以异步的方式返回。
200：成功。
202：请求已成功收到，但尚不知道结果，后续的结果将以事件（NOTIFY）的形式发出。
206：成功，但是数据不全，如发生在放音过程中通过API暂停的情况。
400：客户端错误，多发生在参数不全或不合法的情况。
410：Gone。发生在放音或ASR检测过程中用户侧挂机的情况。
419：冲突。如一个电话被接管，另一个控制器又尝试接管的情况。
500：内部错误。
6xx：系统错误，如发生在关机或即将关机的情况下，拒绝呼叫。
XCC API即控制指令。控制指令分类两类：

### XSwitch基础知识 ###

Channel API：作用于一个Channel，必须有一个uuid参数，uuid即为当前Channel的uuid。
普通API：普通API没有uuid参数。如对FreeSWITCH的控制，发起外呼等。

XSwitch：XSwitch软交换平台。
XNode：XSwitch交换节点，负责SIP信令和媒体交换，可以有多个。有时也简称Node。
XCtrl：XController，即XSwitch控制器。用于控制呼叫流程，用户自己实现，可以用任何语言编写。有时也简称Ctrl。
XCC：XSwitch Call Control，即XSwitch呼叫控制。
NATS：一个消息队列。
Proxy：代理服务器。SIP Proxy用于SIP用户注册，通话负载分担等。HTTP Proxy用于HTTP以及Websocket负载分担。
IMS：IP Multimedia Subsystem，即IP多媒体子系统，运营商的系统。

### N G D ###

BOT设置:
错误优先级:(可以自主配置)
累计错误次数>连续错误次数

### 语音播报 ###

播报速度为1s大约4-5个字,较为合适
