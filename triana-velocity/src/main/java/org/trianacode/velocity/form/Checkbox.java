package org.trianacode.velocity.form;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 20, 2010
 */
public class Checkbox extends FormComponent {

    private String name;
    private String label;
    private boolean checked;

    public Checkbox(String name, String label, boolean checked, String... classes) {
        this.name = name;
        this.label = label;
        this.checked = checked;
        for (String aClass : classes) {
            addClass(aClass);
        }
    }

    public Checkbox(String name, String label, String... classes) {
        this(name, label, false, classes);
    }

    public Checkbox(String name, boolean checked, String... classes) {
        this(name, name, checked, classes);
    }

    public Checkbox(String name, String label) {
        this(name, label, false);
    }

    public Checkbox(String name, boolean checked) {
        this(name, name, checked);
    }


    public Checkbox(String name) {
        this(name, name, false);
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public boolean isChecked() {
        return checked;
    }

    public String render() {
        String checked = this.checked ? "checked=\"checked\"" : "";
        String classes = hasClasses() ? "class=\"" + getClassesAsString() + "\"" : "";
        return "<p>" + label + "<input type=\"checkbox\" " + classes + "name=\"" + name + "\" value=\"" + name + "\"" + checked + " /></p>";
    }
}
