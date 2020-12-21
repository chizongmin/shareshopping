package base

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Log {
    String value() default  "SUD";  //R  读，SUD 增改删
}
