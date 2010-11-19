package org.trianacode.taskgraph.annotation;

/**
 * Annotation that should be applied to a class wanting to be wrapped as a Tool This annotation should be applied to the
 * class definition. In addition, a Process annotation should be applied to the method that should be invoked during
 * workflow execution.
 * <p/>
 * Currently display name and package are ignored. This is because too little is known about a tool when it is first
 * rendered.
 * <p/>
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 10, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Tool {

    String displayName() default "";

    String displayPackage() default "";

    String panelClass() default "";

    int minimumInputs() default 0;

    int minimumOutputs() default 0;

    String[] renderingHints() default {};
}
