package org.trianacode.shiwa.utils;

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
}
