package com.haiyisoft.model;

import lombok.Data;

/**
 * 记录完整的 ngd 流程
 * Created by Chr.yl on 2023/6/15.
 *
 * @author Chr.yl
 */
@Data
public class NGDNodeModel {

    private String nodeName;//lastNodeName
    private String answer;//suggestAnswer
    private String query;//queryText
    private String source;//source

}
