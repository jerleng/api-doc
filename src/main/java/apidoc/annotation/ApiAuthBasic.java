package apidoc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = { ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAuthBasic {
	
	public String[] roles() default {};
	public ApiAuthBasicUser[] testusers() default {};
	public String[] hints() default {};
	public String[] warnings() default {};

}