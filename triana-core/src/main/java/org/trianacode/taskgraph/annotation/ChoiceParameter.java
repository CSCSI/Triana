package org.trianacode.taskgraph.annotation;

/**
 * GENERAL NOTE: parameters that are displayed in auto generated GUIs should only be primitive or string types
 * automatically defines a field as a parameter and defines the gui for it title is a title for the gui values is a list
 * of choices that will appear in a drop down menu If this list is empty, then the parameter nor GUI will be set.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Aug 2, 2010
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ChoiceParameter {

    String title() default "";

    String[] values() default {};
}
