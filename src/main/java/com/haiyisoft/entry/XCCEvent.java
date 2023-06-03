package com.haiyisoft.entry;

import lombok.Data;

/**
 * 每一次XCC接收业务实体
 * Created by Chr.yl on 2023/6/3.
 *
 * @author Chr.yl
 */
@Data
public class XCCEvent {

    //xcc返回code
    private Integer code;
    //xcc返回message
    private String message;
    //返回正常则无次字段ERROR/Speech.End/
    private String type;
    //返回正常无此字段
    private String error;
    //xcc识别返回结果(语音和按键),包括 utterance/dtmf
    private String xccRecognitionResult;

}
