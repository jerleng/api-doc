package apidoc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiWidget {
	public String name() default "";
	public String description() default "";
	public String category() default "";
	public String author() default "";
	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
	public String sinceVersion() default "";
	public String untilVersion() default "";
	public ApiDocResource[] docResources() default {};
	public String snippet() default "";
}