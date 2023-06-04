package com.haiyisoft.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.ivr.IVRService;
import com.haiyisoft.util.HttpClientUtil;
import com.haiyisoft.util.IdGenerator;
import com.haiyisoft.util.NGDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Chr.yl on 2023/3/6.
 *
 * @author Chr.yl
 */
@Slf4j
@RestController
@RequestMapping("xcc")
public class ChrylController {

    @GetMapping("chryl")
    public void chryl(HttpServletResponse response) throws InterruptedException, IOException {
        String loadYml = JSON.toJSONString(IVRInit.XCC_CONFIG_PROPERTY, true);
        log.info("loadYml : {}", loadYml);
        response.getWriter().write(loadYml);
        response.flushBuffer();
        response.getWriter().flush();
    }

    @GetMapping("yml")
    public String yml(HttpServletResponse response) throws InterruptedException, IOException {
        String loadYml = JSON.toJSONString(IVRInit.XCC_CONFIG_PROPERTY, true);
        return loadYml;
    }

    @GetMapping("/test/{queryText}")
    public String xcc(@PathVariable String queryText) {
        JSONObject a = new JSONObject();
        a.put("code", 200);
        String s = NGDUtil.testNGD(queryText, IdGenerator.randomUUID());
        return s;

    }

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private IVRService ivrService;
    @Autowired
    private IVRService ivrService2;

    @GetMapping("c")
    public void show() {
        System.out.println(applicationContext);
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(IVRService.class);
        IVRService person = ac.getBean(IVRService.class);
        System.out.println("p:" + person);
        System.out.println(ivrService);
        System.out.println(ivrService2);
    }

    //----------------------------测试http client
    @GetMapping("/timeout")
    public void testSocketTimeOut() {
        JSONObject a = new JSONObject();
        a.put("code", 200);
        long start = System.currentTimeMillis();
        String s = HttpClientUtil.doGetTest("http://127.0.0.1:8088/xcc/testSocketTimeOutMethod", null);
        long end = System.currentTimeMillis();
        System.out.println("所需时间 ms : " + (end - start));
        System.out.println(s);
    }

    @GetMapping("testSocketTimeOutMethod")
    public String testSocketTimeOutMethod() throws InterruptedException {
        String json = JSON.toJSONString(IVRInit.XCC_CONFIG_PROPERTY, true);
        Thread.sleep(10000);
        return json;
    }
    //----------------------------测试
}
