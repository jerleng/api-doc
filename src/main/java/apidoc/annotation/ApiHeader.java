package apidoc.annotation;

import java.lang.annotation.*;

@Target(value = ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiHeader {
	public String name();
	public String description();
	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
	public boolean required = false;
	public String sinceVersion() default "";
	public String untilVersion() default "";
	public String defaultValue() default "";
	public String format() default "";

}