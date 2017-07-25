package apidoc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiUpdate {
	public String value() default "";
    public String messageCode() default "";
	public String version() default "";

}