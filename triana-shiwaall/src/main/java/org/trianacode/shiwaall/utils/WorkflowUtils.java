package org.trianacode.shiwaall.utils;

import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.trianacode.enactment.AddonUtils;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 13/12/2011
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowUtils {

    public static String getWorkflowType(File file, TransferSignature signature) {
        String workflowType = null;
        if (signature != null && signature.getLanguage() != null) {
            String language = signature.getLanguage();
            System.out.println("Language is : " + language);
            if (language.equals("Triana Taskgraph")) {
                workflowType = AddonUtils.TASKGRAPH_FORMAT;
            } else if (language.equals("IWIR")) {
                workflowType = AddonUtils.IWIR_FORMAT;
            }
        } else {
            workflowType = AddonUtils.getWorkflowType(file);
        }
        return workflowType;
    }

    public static void main(String[] args) {
        String a = "a";
        String b = "a";

        String c = new String("a");
        String d = new String("a");

        System.out.println(a.equals(b));

        System.out.println(a == b);

        System.out.println(compare());

        System.out.println(c == d);

        System.out.println(c.equals(d));

        System.out.println(c.compareTo("a"));
    }

    private static boolean compare() {
        return "a" == "a";
    }
}
