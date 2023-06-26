package com.haiyisoft.model;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

/**
 * ngd node metadata
 * Created by Chr.yl on 2023/6/15.
 *
 * @author Chr.yl
 */
@Data
public class NGDNodeMetaData {
    //queryText
    private String query;
    //suggestAnswer 处理之后的
    private String answer;
    //source
    private String source;
    //solved
    private boolean solved;
    //节点内容
    private JSONArray dialogArray;

    public NGDNodeMetaData() {
        this.dialogArray = new JSONArray();
    }

    public NGDNodeMetaData(String query, String answer) {
        this.query = query;
        this.answer = answer;
    }
}
