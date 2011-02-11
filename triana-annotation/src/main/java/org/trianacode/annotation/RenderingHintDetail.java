package org.trianacode.annotation;

/**
 * The hint value must match a rendering hint given in the Tool annotation
 *
 * @author Andrew Harrison
 * @version 1.0.0 Aug 2, 2010
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface RenderingHintDetail {

    String hint();

    String detail();
}
