package com.haiyisoft.xcc;

import com.haiyisoft.chryl.ChrylConfigProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created By Chr.yl on 2023-02-08.
 *
 * @author Chr.yl
 */
@EnableAsync(proxyTargetClass = true)//开启异步任务支持
@SpringBootApplication
@EnableConfigurationProperties({ChrylConfigProperty.class})
@ComponentScan("com.haiyisoft.**")
public class XccApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(XccApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }

}
