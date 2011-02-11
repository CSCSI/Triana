package org.trianacode.annotation;

/**
 * GENERAL NOTE: parameters that are displayed in auto generated GUIs should only be primitive or string types
 * automatically defines a field as a parameter and defines the gui for it This produces a checkbox for the parameter in
 * the gui
 *
 * @author Andrew Harrison
 * @version 1.0.0 Aug 2, 2010
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface CheckboxParameter {

    String title() default "";

    boolean value() default false;

}
