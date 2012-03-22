package org.trianacode.annotation;

/**
 * Annotation to be applied to a method in a class that is annotated with the @Tool annotation This annotation
 * identifies the method to be called during execution.
 * <p/>
 * gather() set to true can be used for units that are happy to receive input from multiple nodes and deal with them. In
 * this case, the method must have a single parameter which is either an Array, or a java.util.Collection or a
 * java.util.List. Specifically, subclasses of List should not be used, but only the interface.
 * <p/>
 * If an array is used, then type checking can occur on the component type of the array. If a Collection or List is
 * used, then the unit will be passed raw Objects.
 * <p/>
 * If gather is not used, then input nodes are restricted to the number of method parameters and are mapped in order of
 * the input nodes.
 * <p/>
 * If gather is used, the inputs are aggregated into an array or List and passed to the method.
 * <p/>
 * Parameters that are changed during the execution of the method are updated via the setParameter() method.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 10, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Process {

    boolean gather() default false;

    boolean flatten() default false;

    boolean multipleOutputNodes() default false;


}
