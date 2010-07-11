package org.trianacode.taskgraph.annotation;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 10, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Tool {

    String displayName() default "";

    String displayPackage() default "";
}
