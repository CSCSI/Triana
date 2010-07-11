package org.trianacode.taskgraph.annotation;

/**
 * An annotation to allow parameters to be defined. This annotation should be aplied to fields. In addition, setter and
 * getter methods should be supplied for accessing and mutating the field. These methods should follow standard bean
 * conventions.
 * <p/>
 * A parameter can be given a name. If one is not given then the name of the field is used.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 11, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Parameter {

    String name() default "";
}
