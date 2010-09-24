package org.trianacode.velocity.form;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 20, 2010
 */
public abstract class FormComponent {

    private List<String> classes = new ArrayList<String>();

    public abstract String render();

    

    public String getClassesAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classes.size(); i++) {
            sb.append(classes.get(i));
            if (i < classes.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();

    }

    public boolean hasClasses() {
        return classes.size() > 0;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void addClass(String clazz) {
        classes.add(clazz);
    }
}
