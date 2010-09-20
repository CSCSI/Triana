package org.trianacode.velocity.form;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 20, 2010
 */
public class Checkbox implements FormComponent {

    private String name;
    private String label;
    private boolean checked;

    public Checkbox(String name, String label, boolean checked) {
        this.name = name;
        this.label = label;
        this.checked = checked;
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
        return "<p>" + label + "<input type=\"checkbox\" name=\"" + name + "\" value=\"" + name + "\"" + checked + " /></p>";
    }
}
