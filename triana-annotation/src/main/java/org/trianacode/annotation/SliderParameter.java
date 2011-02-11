package org.trianacode.annotation;

/**
 * GENERAL NOTE: parameters that are displayed in auto generated GUIs should only be primitive or string types
 * automatically defines a field as a parameter and defines the gui for it The gui will be a slider with optional
 * minimum, maximum and current settings. Default max is 100 Default min and current are 0 NOTE: the field should still
 * be a string - for the time being...
 * <p/>
 * integer set the slider to have an integer, rather than double, scale
 *
 * @author Andrew Harrison
 * @version 1.0.0 Aug 2, 2010
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SliderParameter {

    String title() default "";

    int min() default 0;

    int max() default 100;

    int current() default 0;

    boolean integer() default false;

}
