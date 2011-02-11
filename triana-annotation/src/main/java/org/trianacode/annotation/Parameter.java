package org.trianacode.annotation;

/**
 * An annotation to allow parameters to be defined. This annotation should be applied to fields.
 * <p/>
 * no gui will be generated
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 11, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Parameter {

}
