package apidoc.annotation;

import java.lang.annotation.*;

@Target(value = ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiError {
	public String code();
	public String description();
	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
	public String sinceVersion() default "";
	public String untilVersion() default "";

}