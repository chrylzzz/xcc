package com.haiyisoft.util;

import java.nio.charset.Charset;

/**
 * Created By Chryl on 2023-10-25.
 */
public class CharsetUtil {

    /**
     * 获取GBK编码字符集
     *
     * @return
     */
    public static Charset charsetGBK() {
        return Charset.forName("GBK");
    }

    /**
     * 获取GBK编码字符集
     *
     * @return
     */
    public static Charset getCharsetGBK() {
        return cn.hutool.core.util.CharsetUtil.CHARSET_GBK;
    }
}
