package org.trianacode.taskgraph.annotation;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 11, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Parameter {

    String name() default "";
}
