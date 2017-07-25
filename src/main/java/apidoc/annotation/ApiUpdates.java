package apidoc.annotation;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiUpdates {

	public ApiUpdate[] value() default {};

}