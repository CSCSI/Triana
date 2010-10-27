package org.trianacode.taskgraph.util;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 26, 2010
 */
public class ToolUtils {


    public static String getPackageName(String fullname) {
        if (fullname.endsWith(".class")) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(0, fullname.lastIndexOf("."));
        }
        return "unknown";
    }

    public static String getClassName(String fullname) {
        if (fullname.endsWith(".class")) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(fullname.lastIndexOf(".") + 1, fullname.length());
        }
        return fullname;
    }
}
