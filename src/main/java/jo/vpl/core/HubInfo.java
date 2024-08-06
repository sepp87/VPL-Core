package jo.vpl.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //On class and method level
@Retention(RetentionPolicy.RUNTIME)
public @interface HubInfo {

    String identifier() default "";

    String category();

    String description();

    String[] tags() default {};
    
    String name() default "";
    
    IconType icon() default IconType.NULL;

//    Method declarations must not have any parameters or a throws clause. 
//    Return types are restricted to primitives, String, Class, enums, 
//    annotations, and arrays of the preceding types.
}
