package apidoc.annotation;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiDocResource {
	public String title() default "";
	public String description() default "";
	public String uri() default "";
	public String controller() default "";
	public String action() default "";
	public String resourceType() default "";	
}