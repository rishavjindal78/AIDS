package org.shunya.shared.annotation;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@XmlTransient
public @interface InputParam{
    String bind() default "";
    String type() default "input";
    String description() default "";
    String displayName() default "";
    boolean required() default false;
    boolean substitute() default true;
}