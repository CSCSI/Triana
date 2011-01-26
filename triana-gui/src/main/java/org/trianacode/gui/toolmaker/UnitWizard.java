/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.gui.toolmaker;


import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.WizardListener;
import org.trianacode.gui.windows.WizardWindow;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.taskgraph.util.UrlUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * <p> A class for creating the template for an Triana tool This class is a stand alone application which can be run
 * independantly of the Triana GUI, but can also be used from within the GUI. </p>
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class UnitWizard implements WizardListener {

    /**
     * Template file names
     */
    public final static String DEFAULT_TEMPLATE = "DefaultUnit";
    public final static String CUSTOM_PANEL_TEMPLATE = "CustomPanel";

    /**
     * Default template tags
     */
    public final static String TOOLNAME = "$TOOLNAME";
    public final static String AUTHOR = "$AUTHOR";
    public final static String POPUP = "$POPUP_DESCRIPTION";
    public final static String DATE = "$DATE";
    public final static String HELPFILE = "$HELPFILE";
    public final static String COPYRIGHT = "$COPYRIGHT";

    public final static String NODE_INPUT_TYPES = "$NODE_INPUT_TYPES";
    public final static String INPUT_TYPES = "$INPUT_TYPES";
    public final static String NODE_OUTPUT_TYPES = "$NODE_OUTPUT_TYPES";
    public final static String OUTPUT_TYPES = "$OUTPUT_TYPES";

    public final static String INPUT_TYPES_HTML = "$HTML_INPUT_TYPES";
    public final static String OUTPUT_TYPES_HTML = "$HTML_OUTPUT_TYPES";

    public final static String DEFAULT_INPUT_NODES = "$DEFAULT_INPUT_NODES";
    public final static String MAX_INPUT_NODE = "$MAX_INPUT_NODES";
    public final static String MIN_INPUT_NODES = "$MIN_INPUT_NODES";

    public final static String DEFAULT_OUTPUT_NODES = "$DEFAULT_OUTPUT_NODES";
    public final static String MAX_OUTPUT_NODE = "$MAX_OUTPUT_NODES";
    public final static String MIN_OUTPUT_NODES = "$MIN_OUTPUT_NODES";

    public final static String PARAM_UPDATE_POLICY = "$PARAM_UPDATE_POLICY";
    public final static String OUTPUT_POLICY = "$OUTPUT_POLICY";

    public final static String DEFINE_PARAMS = "$DEFINE_PARAMS";
    public final static String INIT_PARAMS = "$INIT_PARAMS";
    public final static String RESET_PARAMS = "$RESET_PARAMS";
    public final static String UPDATE_PARAMS = "$UPDATE_PARAMS";

    public final static String INIT_GUI = "$INIT_GUI";
    public final static String PANELNAME = "$PANELNAME";

    public final static String PROCESS_CODE = "$PROCESS_CODE";

    /**
     * valid data types
     */
    public static String[] DATA_TYPES = new String[]{"String", "boolean", "byte", "double",
            "int", "float", "long", "short"};

    /**
     * valid parameter types
     */
    public static String USER_ACCESSIBLE = "User Accessible";
    public static String INTERNAL = "Internal";
    public static String TRANSIENT = "Transient";

    public static String[] PARAM_TYPES = new String[]{USER_ACCESSIBLE, INTERNAL, TRANSIENT};


    /**
     * the panels in the tool wizard
     */
    private UnitPanel toolpanel;
    private TypesPanel typespanel;
    private ParamsPanel paramspanel;
    private GUIPanel guipanel;
    private FinalPanel finalpanel;
    private ToolTable toolTable;

    /**
     * the wizard window
     */
    protected WizardWindow window;


    public UnitWizard(ToolTable tools, Frame owner) {
        this.toolTable = tools;
        toolpanel = new UnitPanel(tools);
        typespanel = new TypesPanel(getTypes(), toolpanel);
        paramspanel = new ParamsPanel(DATA_TYPES, PARAM_TYPES);
        guipanel = new GUIPanel(paramspanel);
        finalpanel = new FinalPanel(toolpanel, guipanel);

        window = new WizardWindow(owner, new JPanel[]{toolpanel, typespanel, paramspanel,
                guipanel, finalpanel}, "Unit Wizard");
        window.addWizardListener(this);
    }

    /**
     * @return an array of the triana types
     */
    private static String[] getTypes() {
        //StringVector sv = Env.getAllTrianaTypes();
        String[] types = new String[2];

        types[0] = "java.lang.Object";
        types[1] = "java.lang.String";

        //for (int count = 0; count < sv.size(); ++count)
        //    types[count + 2] = FileUtils.splitLine(sv.at(count)).at(0);

        return types;
    }

    /**
     * @return the source file path for the tool
     */
    public String getToolSourcePath() {
        return finalpanel.getSrcFileName();
    }

    public String getToolHelpFilePath() {
        return finalpanel.getHelpFileName();
    }

    /**
     * @return the source file path for the custom panel
     */
    public String getCustomPanelSourcePath() {
        return finalpanel.getGuiFileName();
    }


    /**
     * Generate tool code if finish is selected
     */
    public void finishSelected(WizardWindow window) {
        createDirs();
        if (!generateSourceCode()) {
            window.setVisible(true);
            return;
        }
        if (!generateHelpFile()) {
            window.setVisible(true);
            return;
        }

        if (guipanel.isGenerateCustomPanel()) {
            if (!generateCustomPanel()) {
                window.setVisible(true);
                return;
            }
        }

        if (finalpanel.isPlaceholderChecked()) {
            if (!generatePlaceHolder()) {
                window.setVisible(true);
                return;
            }
        }
        window.dispose();
    }

    public void cancelSelected(WizardWindow window) {
    }

    public void panelSelected(WizardWindow window, int index) {
    }


    /**
     * Create src and help directories if they do not exist
     */
    private void createDirs() {
        String[] dirList = finalpanel.getDirectoriesToCreate();
        for (int i = 0; i < dirList.length; i++) {
            String dirStr = dirList[i];
            if (!FileUtils.fileExists(dirStr)) {
                File dir = new File(dirStr);
                dir.mkdir();
            }

        }
    }

    /**
     * Generate the help file template
     */
    private boolean generateHelpFile() {
        String template = Env.getTemplate("helpTemplate.html");
        String htmlFile = getToolHelpFilePath();

        template = changeToolTags(template);
        template = changeHTMLTags(template);
        if (FileUtils.fileExists(htmlFile)) {
            int reply = JOptionPane
                    .showConfirmDialog(null, "Really Overwrite " + htmlFile + " ?", "File Exists Warning",
                            JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        PrintWriter pw = FileUtils.createWriter(htmlFile);
        pw.print(template);
        FileUtils.closeWriter(pw);
        return true;
    }

    /**
     * Generate the source code file
     */
    private boolean generateSourceCode() {
        String template = Env.getTemplate(DEFAULT_TEMPLATE);

        template = changeToolTags(template);

        String saveFile = getToolSourcePath();
        if (FileUtils.fileExists(saveFile)) {
            int reply = JOptionPane
                    .showConfirmDialog(null, "Really Overwrite " + saveFile + " ?", "File Exists Warning",
                            JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        PrintWriter pwriter = FileUtils.createWriter(saveFile);
        pwriter.print(template);
        FileUtils.closeWriter(pwriter);
        return true;
    }


    /**
     * Changes the default tags, such as tool name, author etc.
     */
    private String changeToolTags(String template) {
        String packageName = toolpanel.getSourcePackage();
        if (!packageName.equals("")) {
            template = "package " + packageName + ";\n\n" + template;
        }
        template = template.replace(TOOLNAME, toolpanel.getSourceClass());
        template = template.replace(POPUP, toolpanel.getPopUpDescription());
        template = template.replace(DATE, toolpanel.getDate());
        template = template.replace(AUTHOR, toolpanel.getAuthor());
        template = template.replace(HELPFILE, toolpanel.getHelpFile());

        if (toolpanel.isIncludeCopyright()) {
            template = template.replace(COPYRIGHT, Env.getCopyright());
        } else {
            template = template.replace(COPYRIGHT, "");
        }

        template = template.replace(DEFAULT_INPUT_NODES, String.valueOf(toolpanel.getDefaultInputNodes()));
        template = template.replace(MIN_INPUT_NODES, String.valueOf(toolpanel.getMinimumInputNodes()));

        if (toolpanel.getMaximumInputNodes() == Integer.MAX_VALUE) {
            template = template.replace(MAX_INPUT_NODE, "Integer.MAX_VALUE");
        } else {
            template = template.replace(MAX_INPUT_NODE, String.valueOf(toolpanel.getMaximumInputNodes()));
        }

        template = template.replace(DEFAULT_OUTPUT_NODES, String.valueOf(toolpanel.getDefaultOutputNodes()));
        template = template.replace(MIN_OUTPUT_NODES, String.valueOf(toolpanel.getMinimumOutputNodes()));

        if (toolpanel.getMaximumOutputNodes() == Integer.MAX_VALUE) {
            template = template.replace(MAX_OUTPUT_NODE, "Integer.MAX_VALUE");
        } else {
            template = template.replace(MAX_OUTPUT_NODE, String.valueOf(toolpanel.getMaximumOutputNodes()));
        }

        if (paramspanel.getUpdatePolicy() == ParamsPanelInterface.UPDATE_AT_START_OF_PROCESS) {
            template = template.replace(PARAM_UPDATE_POLICY, "PROCESS_UPDATE");
        } else if (paramspanel.getUpdatePolicy() == ParamsPanelInterface.UPDATE_IMMEDIATELY) {
            template = template.replace(PARAM_UPDATE_POLICY, "IMMEDIATE_UPDATE");
        } else {
            template = template.replace(PARAM_UPDATE_POLICY, "NO_UPDATE");
        }

        if (typespanel.getOutputPolicy() == TypesPanel.COPY_OUTPUT) {
            template = template.replace(OUTPUT_POLICY, "COPY_OUTPUT");
        } else if (typespanel.getOutputPolicy() == TypesPanel.CLONE_MULTIPLE_OUTPUT) {
            template = template.replace(OUTPUT_POLICY, "CLONE_MULTIPLE_OUTPUT");
        } else {
            template = template.replace(OUTPUT_POLICY, "CLONE_ALL_OUTPUT");
        }

        template = template.replace(NODE_INPUT_TYPES, getNodeInputTypesString());
        template = template.replace(INPUT_TYPES, getInputTypesString());
        template = template.replace(NODE_OUTPUT_TYPES, getNodeOutputTypesString());
        template = template.replace(OUTPUT_TYPES, getOutputTypesString());

        template = template.replace(DEFINE_PARAMS, getDefineParamsString());
        template = template.replace(INIT_PARAMS, getInitParamsString());
        template = template.replace(RESET_PARAMS, getResetParamsString());
        template = template.replace(UPDATE_PARAMS, getUpdateParamsString());

        template = template.replace(INIT_GUI, getInitGUIString());

        template = template.replace(PROCESS_CODE, getProcessCodeString());

        return template;
    }

    /**
     * construct an html set of input and output types and replace the tags in the template
     */
    public String changeHTMLTags(String template) {
        String incode = "";
        String outcode = "";

        String http1 = "<A HREF=\"" + Env.home() + "help/JavaDoc/triana/types/";

// web page
//        String http1 = "<A HREF=\"http://triana.co.uk/documentation/Triana/JavaDoc/triana/types/";
        String http2 = "\">";
        String http3 = "</A>";

        String[] strArr = typespanel.getInputTypes();
        for (int i = 0; i < strArr.length; ++i) {
            incode += http1 + strArr[i] + ".html" +
                    http2 + strArr[i] + http3;
            if (i < strArr.length - 1) {
                incode += ", ";
            }
        }

        strArr = typespanel.getOutputTypes();
        for (int i = 0; i < strArr.length; ++i) {
            outcode += http1 + strArr[i] + ".html" +
                    http2 + strArr[i] + http3;
            if (i < strArr.length - 1) {
                outcode += ", ";
            }
        }

        template = template.replace(INPUT_TYPES_HTML, incode);
        template = template.replace(OUTPUT_TYPES_HTML, outcode);

        return template;
    }

    /**
     * @return a String representing an array of the input types for each node
     */
    private String getNodeInputTypesString() {
        String[][] intypes = typespanel.getNodeInputTypes();

        if (intypes.length == 0) {
            return "[0][0]";
        } else {
            String instr = "[][] {\n";

            for (int ncount = 0; ncount < intypes.length; ncount++) {
                instr += "            {";

                for (int tcount = 0; tcount < intypes[ncount].length; tcount++) {
                    if (tcount > 0) {
                        instr += ", ";
                    }

                    instr += "\"" + intypes[ncount][tcount] + "\"";
                }

                if (ncount < intypes.length - 1) {
                    instr += "},\n";
                } else {
                    instr += "}\n";
                }
            }

            instr += "            }";

            return instr;
        }
    }

    /**
     * @return a String representing an array of the input types
     */
    private String getInputTypesString() {
        String instr = "{";
        String[] intypes = typespanel.getInputTypes();

        for (int count = 0; count < intypes.length; count++) {
            if (count > 0) {
                instr = instr + ", ";
            }

            instr = instr + "\"" + intypes[count] + "\"";
        }

        return instr + "}";
    }


    /**
     * @return a String representing an array of the output types for each node
     */
    private String getNodeOutputTypesString() {
        String[][] outtypes = typespanel.getNodeOutputTypes();

        if (outtypes.length == 0) {
            return "[0][0]";
        } else {
            String outstr = "[][] {\n";

            for (int ncount = 0; ncount < outtypes.length; ncount++) {
                outstr += "            {";

                for (int tcount = 0; tcount < outtypes[ncount].length; tcount++) {
                    if (tcount > 0) {
                        outstr += ", ";
                    }

                    outstr += "\"" + outtypes[ncount][tcount] + "\"";
                }

                if (ncount < outtypes.length - 1) {
                    outstr += "},\n";
                } else {
                    outstr += "}\n";
                }
            }

            outstr += "            }";

            return outstr;
        }
    }

    /**
     * @return a String representing an array of the output types
     */
    public String getOutputTypesString() {
        String outstr = "{";
        String[] outtypes = typespanel.getOutputTypes();

        for (int count = 0; count < outtypes.length; count++) {
            if (count > 0) {
                outstr = outstr + ", ";
            }

            outstr = outstr + "\"" + outtypes[count] + "\"";
        }

        return outstr + "}";
    }

    /**
     * @return a String that defines the parameter data types
     */
    private String getDefineParamsString() {
        String[] paramnames = paramspanel.getParameterNames();

        if (paramnames.length == 0) {
            return "";
        }

        String str = "// parameter data type definitions";

        for (int count = 0; count < paramnames.length; count++) {
            str += "\n    private " + paramspanel.getDataType(paramnames[count]) + " " + paramnames[count] + ";";
        }

        str += "\n\n";

        return str;
    }

    /**
     * @return a String that initialises the parameters to their default values
     */
    private String getInitParamsString() {
        String[] paramnames = paramspanel.getParameterNames();

        if (paramnames.length == 0) {
            return "";
        }

        String str = "\n\n        // Define initial value and type of parameters";
        for (int count = 0; count < paramnames.length; count++) {
            str += "\n        defineParameter(\"" + paramnames[count] + "\", ";

            if (paramspanel.getDefaultValue(paramnames[count]) != null) {
                str += "\"" + paramspanel.getDefaultValue(paramnames[count]) + "\", ";
            } else if (paramspanel.getDataType(paramnames[count]).equals("String")) {
                str += "\"\", ";
            } else if (paramspanel.getDataType(paramnames[count]).equals("boolean")) {
                str += "\"false\", ";
            } else {
                str += "\"0\", ";
            }

            if (paramspanel.getParameterType(paramnames[count]).equals(INTERNAL)) {
                str += "INTERNAL";
            } else if (paramspanel.getParameterType(paramnames[count]).equals(TRANSIENT)) {
                str += "TRANSIENT";
            } else {
                str += "USER_ACCESSIBLE";
            }

            str += ");";
        }

        return str;
    }


    /**
     * @return a String that sets parameters to their default values
     */
    private String getResetParamsString() {
        String[] paramnames = paramspanel.getParameterNames();

        if (paramnames.length == 0) {
            return "";
        }

        String str = "\n        // Set unit variables to the values specified by the parameters";

        for (int count = 0; count < paramnames.length; count++) {
            str += "\n        " + getParamUpdateString(paramnames[count],
                    "getParameter(\"" + paramnames[count] + "\")");
        }

        return str;
    }

    /**
     * @return a String for immediate parameter update
     */
    private String getUpdateParamsString() {
        String[] paramnames = paramspanel.getParameterNames();
        String str = "";

        for (int count = 0; count < paramnames.length; count++) {
            str += "\n        ";

            if (count != 0) {
                str += "\n        ";
            }

            str += "if (paramname.equals(\"" + paramnames[count] + "\"))";
            str += "\n            " + getParamUpdateString(paramnames[count], "value");
        }

        return str;
    }

    /**
     * @return a String for the skeleton process method
     */
    private String getProcessCodeString() {
        String str = "";

        if ((typespanel.getInputTypes().length > 0) && (toolpanel.getDefaultInputNodes() > 0)) {
            str += typespanel.getInputTypes()[0] + " input = (" + typespanel.getInputTypes()[0]
                    + ") getInputAtNode(0);\n\n        ";
        }

        return str;
    }


    /**
     * @return a String for updating the specfied parameter
     */
    public String getParamUpdateString(String paramname, String updatestr) {
        String datatype = paramspanel.getDataType(paramname);
        String str = "";

        if (datatype.equals("String")) {
            str += paramname + " = (String) " + updatestr + ";";
        } else if (datatype.equals("boolean")) {
            str += paramname + " = new Boolean((String) " + updatestr + ").booleanValue();";
        } else if (datatype.equals("byte")) {
            str += paramname + " = new Byte((String) " + updatestr + ").byteValue();";
        } else if (datatype.equals("double")) {
            str += paramname + " = new Double((String) " + updatestr + ").doubleValue();";
        } else if (datatype.equals("int")) {
            str += paramname + " = new Integer((String) " + updatestr + ").intValue();";
        } else if (datatype.equals("float")) {
            str += paramname + " = new Float((String) " + updatestr + ").floatValue();";
        } else if (datatype.equals("long")) {
            str += paramname + " = new Long((String) " + updatestr + ").longValue();";
        } else if (datatype.equals("short")) {
            str += paramname + " = new Short((String) " + updatestr + ").shortValue();";
        }

        return str;
    }


    /**
     * @return a String that initialises the parameters to their default values
     */
    private String getInitGUIString() {
        if (guipanel.isUsingGUIBuilder() && (guipanel.getGUIBuilderLines().length > 0)) {
            String[] lines = guipanel.getGUIBuilderLines();
            String str = "\n\n        // Initialise GUI builder interface";
            str += "\n        String guilines = \"\";";

            for (int count = 0; count < lines.length; count++) {
                str += "\n        guilines += \"" + lines[count] + "\\n\";";
            }

            str += "\n        setGUIBuilderV2Info(guilines);";

            return str;
        } else if (guipanel.isUsingCustomPanel() && (!guipanel.getQualifiedCustomPanelName().equals(""))) {
            String str = "\n\n        // Initialise custom panel interface";
            str += "\n        setParameterPanelClass(\"" + guipanel.getQualifiedCustomPanelName() + "\");";

            return str;
        } else {
            return "";
        }
    }

    /**
     * Generate the custom parameter panel
     */
    private boolean generateCustomPanel() {
        String template = Env.getTemplate(CUSTOM_PANEL_TEMPLATE);

        template = changeCustomPanelTags(template);

        String saveFile = getCustomPanelSourcePath();
        if (FileUtils.fileExists(saveFile)) {
            int reply = JOptionPane
                    .showConfirmDialog(null, "Really Overwrite " + saveFile + " ?", "File Exists Warning",
                            JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        PrintWriter pwriter = FileUtils.createWriter(saveFile);
        pwriter.print(template);
        FileUtils.closeWriter(pwriter);
        return true;
    }

    /**
     * Generate an XML placeholder file for the unit.
     */
    private boolean generatePlaceHolder() {
        try {
            ToolImp placeholder = new ToolImp();
            String placename = finalpanel.getPlaceHolderToolName();
            int index = placename.lastIndexOf('.');
            if (index == -1) {
                placeholder.setToolName(placename);
            } else {
                placeholder.setToolName(placename.substring(index + 1));
                placeholder.setToolPackage(placename.substring(0, index));
            }

            if (!toolpanel.getSourcePackage().equals("")) {
                placeholder.setProxy(new JavaProxy(toolpanel.getSourcePackage() + '.' + toolpanel.getSourceClass(),
                        toolpanel.getUnitPackage()));
            } else {
                placeholder.setProxy(new JavaProxy(toolpanel.getSourceClass(), toolpanel.getUnitPackage()));
            }

            placeholder.setPopUpDescription(toolpanel.getPopUpDescription());
            placeholder.setHelpFile(getToolHelpFilePath());
            placeholder.setToolBox(toolTable.getToolResolver().getToolbox(toolpanel.getToolBox()));

            String saveFile = finalpanel.getPlaceHolderFile();
            if (FileUtils.fileExists(saveFile)) {
                int reply = JOptionPane
                        .showConfirmDialog(null, "Really Overwrite " + saveFile + " ?", "File Exists Warning",
                                JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
            try {
                XMLWriter writer = new XMLWriter(FileUtils.createWriter(saveFile));

                writer.writeComponent(placeholder);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
            toolTable.refreshLocation(UrlUtils.toURL(saveFile), placeholder.getToolBox().getPath());
            return true;
        } catch (TaskException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
    }

    /**
     * Changes the custom panel tags.
     */
    private String changeCustomPanelTags(String template) {
        template = template.replace(PANELNAME, guipanel.getCustomPanelName());

        String packagename = guipanel.getCustomPanelPackage();
        if (!packagename.equals("")) {
            template = "package " + packagename + ";\n\n" + template;
        }

        return template;
    }

}
