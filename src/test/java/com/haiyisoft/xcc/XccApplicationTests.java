package com.haiyisoft.xcc;

import com.haiyisoft.util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;

@SpringBootTest
class XccApplicationTests {

    @Test
    void contextLoads() {


        System.out.println("0-----");
        System.out.println(IdGenerator.snowflakeId());
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
