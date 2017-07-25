package apidoc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiFlow {
	
	public String name();
	
	public String description() default "";
	
	public String[] preconditions() default {};

	public ApiFlowStep[] steps() default {};
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