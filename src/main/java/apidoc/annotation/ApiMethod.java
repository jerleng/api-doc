package apidoc.annotation;

import apidoc.*;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMethod {

	public String path() default ""; 
	public String id() default "";
	public String name() default ""; 
	public String description() default "";
	public String responseCode() default  "200";
	public String[] verbs() default {};
	public String[] produces() default {}; // [MediaType.APPLICATION_JSON_VALUE]
	public String[] consumes() default {};
	public String author() default "";

	//is it a listing action? => put max/offset
	public boolean listing() default false;
	public boolean noApiKey() default false;
	public boolean noToken() default false;
	public boolean extraQueryString() default false;
	public boolean enablePlayground() default true;

	public String[] hints() default {};
	public String[] warnings() default {};
	public String[] hintCodes() default {};
	public String[] warningCodes() default {};
	public String sinceVersion() default "";
	public String untilVersion() default "";
	public ApiDocResource[] docResources() default {};
	public String snippet() default "";

}
