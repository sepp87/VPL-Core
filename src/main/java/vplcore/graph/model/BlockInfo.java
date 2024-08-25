package vplcore.graph.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import vplcore.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //On class and method level
@Retention(RetentionPolicy.RUNTIME)
public @interface BlockInfo {

    String name() default "";

    String identifier();

    String category();

    String description() default "";

    String[] tags() default {};
    
    String[] alias() default {};

    IconType icon() default IconType.NULL;

//    Method declarations must not have any parameters or a throws clause. 
//    Return types are restricted to primitives, String, Class, enums, 
//    annotations, and arrays of the preceding types.
}
