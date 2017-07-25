package apidoc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiObject {

	public String name() default "";
	public String description() default "";

	public boolean show() default true;
	public String author() default "";

	public String category() default "";
	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
	public String sinceVersion() default "";
	public String untilVersion() default "";
	public ApiDocResource[] docResources() default {};
	public String snippet() default "";
}