package com.haiyisoft.anno;

import java.lang.annotation.*;

/**
 * Created By Chryl on 2023-03-10.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    String value() default "";

}
