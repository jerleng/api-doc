package apidoc.annotation;

import java.lang.annotation.*;

@Target(value = ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParam {

	public String name();

	public String description() default "";

	public boolean required() default true;

	public String[] allowedValues() default {};
	
	public String defaultValue() default "";

	public String format() default "";

	public String paramType() default "";

	public String type() default "";

	public boolean allowMultiple() default false;

	public boolean show() default true;

	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
	public String sinceVersion() default "";
	public String untilVersion() default "";

}
