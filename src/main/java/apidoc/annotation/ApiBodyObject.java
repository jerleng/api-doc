package apidoc.annotation;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiBodyObject {

	public String objectType() default "";
	public ApiObjectField[] formFields() default {};
	public String wrapperObjectType() default "";
	public String[] subObjectTypes() default {};
	public boolean isList() default false;
	public boolean isMap() default false;

	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};

}
