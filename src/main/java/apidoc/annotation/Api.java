package apidoc.annotation;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {
	public String description() default "";
	public String name() default "";
	public String category() default "";
	public String author() default "";
	public String[] hints() default {};
	public String[] warnings() default {};
	public String sinceVersion() default "";
	public String untilVersion() default "";
	public String snippet() default "";
	public ApiDocResource[] docResources() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
}