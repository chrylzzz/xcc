package com.haiyisoft.xcc;

import com.haiyisoft.entry.NGDEvent;
import com.haiyisoft.util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class XccApplicationTests {

    @Test
    void contextLoads() {


        System.out.println("0-----");
        System.out.println(IdGenerator.nextId());
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void contextLoads2() {
        NGDEvent ngdEvent = new NGDEvent();
        System.out.println(ngdEvent.isUserOk());

    }

}
