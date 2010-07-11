package org.trianacode.taskgraph.annotation;

/**
 * Annotation to be applied to a method in a class that is annotated with the @Tool annotation This annotation
 * identifies the method to be called during execution.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 10, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Process {

}