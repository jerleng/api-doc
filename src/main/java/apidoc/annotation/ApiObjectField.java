package apidoc.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiObjectField {

	public String description();
	public String format() default "";
	public String[] allowedValues() default {};
	public String allowedType() default "";
	public boolean isList() default false;
	public boolean isMap() default false;
	public String[] subObjectTypes() default {};

	public String name() default "";

	public boolean readOnly() default true;

	//only user if userForCreation = true
	public boolean required() default true;

	public String defaultValue() default "";

	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
	public String sinceVersion() default "";
	public String untilVersion() default "";


}
