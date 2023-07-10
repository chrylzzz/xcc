package com.haiyisoft.xcc;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by Chr.yl on 2023/7/10.
 *
 * @author Chr.yl
 */
public class TestLombok {

    public static void main(String[] args) {

        Customer customer = new Customer();
        customer.setAge(11);
        customer.setName("zy");

        Customer customer1 = new Customer();
        customer1.setAge(22);
        customer1.setName("zy");

        System.out.println(customer.equals(customer1));
    }


}

@Data
class User implements Serializable {
    private int age;
}

@Data
//子类使用:表示使用子类equal的时候会调用父类的equal方法和hashcode方法
//注意这里如果这个类没有任何的父类 的情况下使用了@ EuqalAndHashCode(callsuper=true) 会编译报错 提示
//Generating equals/hashCode with a supercall to java.lang.Object is pointless.
@EqualsAndHashCode(callSuper = true)//use super
class Customer extends User {
    private String name;
}



