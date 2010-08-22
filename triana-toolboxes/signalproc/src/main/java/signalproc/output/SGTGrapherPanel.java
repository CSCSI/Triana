/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.
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
 */
package signalproc.output;


import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.ParameterPanelImp;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import gov.noaa.pmel.sgt.AbstractPane;
import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.LineCartesianRenderer;
import gov.noaa.pmel.sgt.SGLabel;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JClassTree;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.sgt.swing.prop.LineAttributeDialog;
import gov.noaa.pmel.util.Domain;
import gov.noaa.pmel.util.Point2D;
import gov.noaa.pmel.util.Range2D;
import gov.noaa.pmel.util.SoTRange;
import gov.noaa.pmel.util.SoTValue;
import triana.types.GraphType;
import triana.types.Histogram;
import triana.types.util.TrianaSort;


/**
 * Used by SGTGrapher to plot graphs on the client side.
 *
 * @author Rob Davies
 * @version $Revision: 2921 $
 */
public class SGTGrapherPanel extends ParameterPanel
        implements TaskListener, FocusListener, ItemListener, ActionListener {

    public static final String GRAPH_DATA = "SGTGraphData";
    public static Color[] COLOR_MAP = {Color.blue, Color.red,
            Color.green, Color.yellow,
            Color.magenta, Color.cyan,
            Color.black, Color.pink,
            Color.orange, Color.gray};


    private Checkbox xAutoScaleCheckbox, yAutoScaleCheckbox;
    private JButton printButton, resetZoomButton, snapshotButton;
    private JCheckBoxMenuItem takeOutFactorsOfTenMenu, applyOffsetMenu;
    private JCheckBoxMenuItem forceEqualRangesMenu;
    LineAttributeDialog lad_;
    MyMouse myMouse_;
    boolean linearXPrevious = true;
    boolean linearYPrevious = true;
    boolean logXPrevious = false;
    boolean logYPrevious = false;
    boolean equalXAndYRanges = false;
    String clipInExists = "false";
    private JPlotLayout layout;

    private JTextField keytitleField;
    private int numberOfInputs;
    private String nodeNumber;
    private JComboBox inputnodeCombo;
    private int lineStyle = LineAttribute.SOLID; // Sets the line style to lines by default
    private int markType = 1;  // Sets the mark to a cross by default. See gov.noaa.pmel.sgt.PlotMark for defeinitions.
    private double markSize = 0.1;  // Sets the mark size
    private String lineKeyTitle;
    private String mainTitle = "";
    private String xtitle = "";
    private String ytitle = "";

    /**
     * Creates a new SGTGrapherPanel.
     */
    public SGTGrapherPanel() {
        super();
    }


    /**
     * @return false so that the auto commit box is not shown
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * Overridden to return WindowButtonConstants.OK_BUTTON only.
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_BUTTON;
    }

    /**
     * Overridden to return false, suggesting that the panel prefers to be allowed to be hidden behind the main Triana
     * window.
     */
    public boolean isAlwaysOnTopPreferred() {
        return false;
    }

    /**
     * Initialises the panel.
     */
    public void init() {
        initPanel();
        initControlPanel();
        getTask().addTaskListener(this);
    }

    /**
     * A empty method as the graph is updated through its task listener interface.
     */
    public void run() {
    }

    /**
     * A empty method as the graph is updated through its task listener interface.
     */
    public void reset() {
        //layout.setXTitle((String)getParameter("xtitle"));
        //layout.setYTitle((String)getParameter("ytitle"));
    }

    /**
     * Disposes of the graph window and removes this panel as a task listener.
     */
    public void dispose() {
        getTask().removeTaskListener(this);
    }


    private void showLineKeyTitleWindow() {
        ParameterPanelImp login = new ParameterPanelImp();
        login.setLayout(new BorderLayout());

        JPanel labelpanel = new JPanel(new GridLayout(2, 1));
        labelpanel.add(new JLabel("Input Node"));
        labelpanel.add(new JLabel("Key Title"));
        labelpanel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel inputnodecont = new JPanel(new BorderLayout());
        inputnodeCombo = new JComboBox();
        for (int nodeCounter = 0; nodeCounter < numberOfInputs; ++nodeCounter) {
            inputnodeCombo.addItem(Integer.toString(nodeCounter));
        }
        inputnodeCombo.setSelectedIndex(0);
        inputnodeCombo.addActionListener(this);
        inputnodecont.add(inputnodeCombo, BorderLayout.WEST);
        inputnodecont.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel keytitlecont = new JPanel(new BorderLayout());
        keytitleField = new JTextField(18);
        nodeNumber = (String) inputnodeCombo.getSelectedItem();
        SimpleLine data = (SimpleLine) layout.getData(nodeNumber);
        keytitleField.setText(data.getKeyTitle().getText());
        keytitleField.addFocusListener(this);
        keytitlecont.add(keytitleField, BorderLayout.WEST);
        keytitlecont.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel fieldpanel = new JPanel(new GridLayout(2, 1));
        fieldpanel.add(inputnodecont);
        fieldpanel.add(keytitlecont);

        login.add(labelpanel, BorderLayout.WEST);
        login.add(fieldpanel, BorderLayout.CENTER);

        ParameterWindow window;

        if (getWindowInterface().getWindow() instanceof Frame) {
            window = new ParameterWindow((Frame) getWindowInterface().getWindow(),
                    WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS, true);
        } else {
            window = new ParameterWindow((Dialog) getWindowInterface().getWindow(),
                    WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS, true);
        }

        Point loc = getLocationOnScreen();
        window.setTitle("Set Key Title...");
        window.setParameterPanel(login);
        window.setLocation(loc.x + 100, loc.y + 100);
        window.setVisible(true);

    }


    private void initControlPanel() {

        printButton = new JButton("Print");
        printButton.addActionListener(this);

        resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.addActionListener(this);

        snapshotButton = new JButton("Snapshot");
        snapshotButton.addActionListener(this);

        xAutoScaleCheckbox = new Checkbox("x Auto Scale", true);
        yAutoScaleCheckbox = new Checkbox("y Auto Scale", true);

        JPanel subPanel1 = new JPanel(new GridLayout(1, 4));
        subPanel1.add(resetZoomButton);
        subPanel1.add(snapshotButton);
        subPanel1.add(xAutoScaleCheckbox);
        subPanel1.add(yAutoScaleCheckbox);

        add(subPanel1, BorderLayout.SOUTH);


        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenu plotMenu = new JMenu("Plot");
        plotMenu.setMnemonic(KeyEvent.VK_P);
        JMenu xSubMenu = new JMenu("X-axis");
        xSubMenu.setMnemonic(KeyEvent.VK_X);
        JMenu ySubMenu = new JMenu("Y-axis");
        ySubMenu.setMnemonic(KeyEvent.VK_Y);
        JMenuItem item;

        item = new JMenuItem("Print...", KeyEvent.VK_P); // Env.getString(...)
        item.addActionListener(this);
        item.setActionCommand("Print");    // not change with locale
        fileMenu.add(item);

        item = new JMenuItem("Plot Properties", KeyEvent.VK_P);
        item.addActionListener(this);
        plotMenu.add(item);

        item = new JMenuItem("Line Key Title", KeyEvent.VK_L);
        item.addActionListener(this);
        plotMenu.add(item);
        plotMenu.addSeparator();

        ButtonGroup xScale = new ButtonGroup();
        item = new JRadioButtonMenuItem("Linear-X");
        item.setMnemonic(KeyEvent.VK_L);
        item.addActionListener(this);
        item.setSelected(true);
        xScale.add(item);
        xSubMenu.add(item);

        item = new JRadioButtonMenuItem("Log-X");
        item.setMnemonic(KeyEvent.VK_O);
        item.addActionListener(this);
        xScale.add(item);
        xSubMenu.add(item);

        ButtonGroup yScale = new ButtonGroup();
        item = new JRadioButtonMenuItem("Linear-Y");
        item.setMnemonic(KeyEvent.VK_L);
        item.addActionListener(this);
        item.setSelected(true);
        yScale.add(item);
        ySubMenu.add(item);

        item = new JRadioButtonMenuItem("Log-Y");
        item.setMnemonic(KeyEvent.VK_O);
        item.addActionListener(this);
        yScale.add(item);
        ySubMenu.add(item);

        takeOutFactorsOfTenMenu = new JCheckBoxMenuItem("take out common factors of 10 from data", false);
        takeOutFactorsOfTenMenu.addActionListener(this);
        plotMenu.add(takeOutFactorsOfTenMenu);

        applyOffsetMenu = new JCheckBoxMenuItem("apply offset to data", false);
        applyOffsetMenu.addActionListener(this);
        plotMenu.add(applyOffsetMenu);

        forceEqualRangesMenu = new JCheckBoxMenuItem("force equal ranges on both axes", false);
        forceEqualRangesMenu.addActionListener(this);
        plotMenu.add(forceEqualRangesMenu);

        plotMenu.add(xSubMenu);
        plotMenu.add(ySubMenu);


        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(plotMenu);
        add(menuBar, BorderLayout.NORTH);
    }


    private void initPanel() {
        layout = new JPlotLayout(false, false, false, "f", null, false);
        layout.setTitles("", "", "");
        //layout.setKeyLocationP(new Point2D.Double(3.5, 5.0));

        myMouse_ = new MyMouse();
        layout.addMouseListener(myMouse_);
        layout.setXAutoIntervals(10);
        //layout.setXAutoRange(true);
        layout.setYAutoIntervals(8);
        //layout.setYAutoRange(true);

        setLayout(new BorderLayout());
        add(layout, BorderLayout.CENTER);
    }


    public boolean isXAutoScale() {
        if (xAutoScaleCheckbox.getState()) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isYAutoScale() {
        if (yAutoScaleCheckbox.getState()) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isXLinear() {
        if (linearXPrevious) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isYLinear() {
        if (linearYPrevious) {
            return true;
        } else {
            return false;
        }
    }


    public boolean getSnapshotStatus() {
        if (snapshotButton.getText().equals("Snapshot")) {
            return false;
        } else {
            return true;
        }
    }


    public static boolean checkForNegativeNumbers(double[] d) {
        for (int i = 0; i < d.length; i++) {
            if (d[i] < 0) {
                return true;
            }
        }

        return false;
    }

    public SimpleLine getSGTData(GraphType graph, int port) {
        double[] xData, yData, zData;
        ArrayList xlist, ylist;

        if (clipInExists.equals("false") && port == 0) {
            xtitle = graph.getIndependentLabels(0);
            ytitle = graph.getDependentLabels(0);
        }

        if (isXLinear()) {
            if (graph instanceof Histogram) {
                xData = ((Histogram) graph).getDelimiterMidpoints(0);
            } else {
                xData = graph.getIndependentScaleReal(0);
                if (applyOffsetMenu.getState()) {
                    xlist = applyOffset(xData, true);
                    xData = (double[]) xlist.get(0);
                    Double offset = (Double) xlist.get(1);
                    if (offset.doubleValue() != 0.0 && port == 0) {
                        xtitle += "  - " + offset.toString();
                    }
                }
                if (takeOutFactorsOfTenMenu.getState()) {
                    xlist = takeOutFactorsOfTen(xData);
                    xData = (double[]) xlist.get(0);
                    Double scale = (Double) xlist.get(1);
                    if (scale.doubleValue() != 0.0 && port == 0) {
                        xtitle += "  x " + scale.toString();
                    }
                }
            }
        } else {
            if (graph instanceof Histogram) {
                xData = ((Histogram) graph).getDelimiterMidpointsLog10(0);
            } else {
                xData = graph.getIndependentScaleRealLog10(0);
            }
            for (int j = 0; j < xData.length; j++) {
                if (xData[j] == Double.NEGATIVE_INFINITY) {
                    xData[j] = 0.0;
                }
            }
        }

        if (isYLinear()) {
            yData = (double[]) graph.getGraphArrayReal(0);
            if (applyOffsetMenu.getState()) {
                ylist = applyOffset(yData, false);
                yData = (double[]) ylist.get(0);
                Double offset = (Double) ylist.get(1);
                if (offset.doubleValue() != 0.0 && port == 0) {
                    ytitle += "  - " + offset.toString();
                }
            }
            if (takeOutFactorsOfTenMenu.getState()) {
                ylist = takeOutFactorsOfTen(yData);
                yData = (double[]) ylist.get(0);
                Double scale = (Double) ylist.get(1);
                if (scale.doubleValue() != 0.0 && port == 0) {
                    ytitle += "  x " + scale.toString();
                }
            }
        } else {
            yData = (double[]) graph.getGraphArrayRealLog10(0);
            for (int j = 0; j < yData.length; j++) {
                if (yData[j] == Double.NEGATIVE_INFINITY) {
                    yData[j] = 0.0;
                }
            }
        }

        if (graph.getTitle() == null) {
            graph.setTitle(getTask().getToolName());
        }


        SimpleLine sgtdata = new SimpleLine(xData, yData, graph.getTitle());
        sgtdata.setId(String.valueOf(port));
        sgtdata.setXMetaData(new SGTMetaData(graph.getIndependentLabels(0), ""));
        sgtdata.setYMetaData(new SGTMetaData(graph.getDependentLabels(0), ""));


        return sgtdata;
    }

    public void updateSGTData(SimpleLine sgtdata, GraphType graph) {
        double[] xData, yData;
        ArrayList xlist, ylist;

        if (clipInExists.equals("false") && sgtdata.getId().equals("0")) {
            xtitle = graph.getIndependentLabels(0);
            ytitle = graph.getDependentLabels(0);
        }

        if (isXLinear()) {
            if (graph instanceof Histogram) {
                xData = ((Histogram) graph).getDelimiterMidpoints(0);
            } else {
                xData = graph.getIndependentScaleReal(0);
                if (applyOffsetMenu.getState()) {
                    xlist = applyOffset(xData, true);
                    xData = (double[]) xlist.get(0);
                    Double offset = (Double) xlist.get(1);
                    if (offset.doubleValue() != 0.0 && sgtdata.getId().equals("0")) {
                        xtitle += "  - " + offset.toString();
                    }
                }
                if (takeOutFactorsOfTenMenu.getState()) {
                    xlist = takeOutFactorsOfTen(xData);
                    xData = (double[]) xlist.get(0);
                    Double scale = (Double) xlist.get(1);
                    if (scale.doubleValue() != 0.0 && sgtdata.getId().equals("0")) {
                        xtitle += "  x " + scale.toString();
                    }
                }
            }
        } else {
            if (graph instanceof Histogram) {
                xData = ((Histogram) graph).getDelimiterMidpointsLog10(0);
            } else {
                xData = graph.getIndependentScaleRealLog10(0);
            }
            for (int j = 0; j < xData.length; j++) {
                if (xData[j] == Double.NEGATIVE_INFINITY) {
                    xData[j] = 0.0;
                }
            }
        }

        if (isYLinear()) {
            yData = (double[]) graph.getGraphArrayReal(0);
            if (applyOffsetMenu.getState()) {
                ylist = applyOffset(yData, false);
                yData = (double[]) ylist.get(0);
                Double offset = (Double) ylist.get(1);
                if (offset.doubleValue() != 0.0 && sgtdata.getId().equals("0")) {
                    ytitle += "  - " + offset.toString();
                }
            }
            if (takeOutFactorsOfTenMenu.getState()) {
                ylist = takeOutFactorsOfTen(yData);
                yData = (double[]) ylist.get(0);
                Double scale = (Double) ylist.get(1);
                if (scale.doubleValue() != 0.0 && sgtdata.getId().equals("0")) {
                    ytitle += "  x " + scale.toString();
                }
            }
        } else {
            yData = (double[]) graph.getGraphArrayRealLog10(0);
            for (int j = 0; j < yData.length; j++) {
                if (yData[j] == Double.NEGATIVE_INFINITY) {
                    yData[j] = 0.0;
                }
            }
        }

        sgtdata.setXArray(xData);
        sgtdata.setYArray(yData);
        if (graph.getTitle() == null) {
            graph.setTitle(getTask().getToolName());
        }
        sgtdata.setTitle(graph.getTitle());
    }


    public SimpleGrid getSGTGrid(GraphType graph, int port) {
        double[] xData, yData, zData;
        ArrayList xlist, ylist;

        if (clipInExists.equals("false")) {
            xtitle = graph.getIndependentLabels(0);
            ytitle = graph.getDependentLabels(0);
        }

        xData = graph.getIndependentScaleReal(0);
        yData = graph.getIndependentScaleReal(1);
        double[][] data2d = (double[][]) graph.getGraphArrayReal(0);
        zData = convertArray(data2d);

        if (graph.getTitle() == null) {
            graph.setTitle(getTask().getToolName());
        }


        SimpleGrid sgtdata = new SimpleGrid(xData, yData, zData, graph.getTitle());
        sgtdata.setId(String.valueOf(port));
        sgtdata.setXMetaData(new SGTMetaData(graph.getIndependentLabels(0), ""));
        sgtdata.setYMetaData(new SGTMetaData(graph.getDependentLabels(0), ""));


        return sgtdata;
    }


    public SimpleLine logXData(int port) {

        double[] xData;
        SimpleLine sgtdata;

        sgtdata = (SimpleLine) layout.getData(String.valueOf(port));
        xData = sgtdata.getXArray();
        sgtdata.setXMetaData(sgtdata.getXMetaData());

        for (int j = 0; j < xData.length; j++) {
            xData[j] = Math.log(xData[j]) * 0.434294481;
            if (xData[j] == Double.NEGATIVE_INFINITY) {
                xData[j] = 0.0;
            }
        }

        sgtdata.setXArray(xData);

        return sgtdata;
    }

    public SimpleLine logYData(int port) {

        double[] yData;
        SimpleLine sgtdata;

        sgtdata = (SimpleLine) layout.getData(String.valueOf(port));
        yData = sgtdata.getYArray();
        sgtdata.setYMetaData(sgtdata.getYMetaData());

        for (int j = 0; j < yData.length; j++) {
            yData[j] = Math.log(yData[j]) * 0.434294481;
            if (yData[j] == Double.NEGATIVE_INFINITY) {
                yData[j] = 0.0;
            }
        }

        sgtdata.setYArray(yData);

        return sgtdata;
    }


    public SimpleLine linXData(int port) {

        double[] xData;
        SimpleLine sgtdata;

        sgtdata = (SimpleLine) layout.getData(String.valueOf(port));
        xData = sgtdata.getXArray();
        sgtdata.setXMetaData(sgtdata.getXMetaData());

        for (int j = 0; j < xData.length; j++) {
            xData[j] = Math.pow(10, xData[j]);
        }

        sgtdata.setXArray(xData);

        return sgtdata;
    }

    public SimpleLine linYData(int port) {

        double[] yData;
        SimpleLine sgtdata;

        sgtdata = (SimpleLine) layout.getData(String.valueOf(port));
        yData = sgtdata.getYArray();
        sgtdata.setYMetaData(sgtdata.getYMetaData());

        for (int j = 0; j < yData.length; j++) {
            yData[j] = Math.pow(10, yData[j]);
        }

        sgtdata.setYArray(yData);

        return sgtdata;
    }


    public void graphData(GraphType graph, int port) {
        SimpleLine data;
        SimpleGrid grid;
        Component parent = this;

        while ((parent != null) && (!(parent instanceof Window))) {
            parent = parent.getParent();
        }

        if ((parent != null) && (!parent.isVisible())) {
            parent.setVisible(true);
        }

        layout.setBatch(true);

        if (layout.getData(String.valueOf(port)) == null) {

            System.out.println("In SGTGrapherPanel, adding data from node " + port);


            data = getSGTData(graph, port);
            SGLabel keyTitle = data.getKeyTitle();

            if (keyTitle == null) {
                keyTitle = new SGLabel(Integer.toString(port), data.getTitle(), new Point2D.Double(0.0, 0.0));
                data.setKeyTitle(keyTitle);
                if (clipInExists.equals("true")) {
                    data.getKeyTitle().setText(lineKeyTitle);
                }
            } else {
                keyTitle.setText(data.getTitle());
            }

            LineAttribute lineAtt;

            if (lineStyle == LineAttribute.MARK) {
                System.out.println("MARK line in SGTGrapher");
                lineAtt = new LineAttribute(LineAttribute.MARK, COLOR_MAP[Math.min(port, COLOR_MAP.length - 1)]);
                lineAtt.setMark(markType);
                lineAtt.setMarkHeightP(markSize);
            } else if (lineStyle == LineAttribute.SOLID) {
                System.out.println("SOLID line in SGTGrapher");
                lineAtt = new LineAttribute(LineAttribute.SOLID, COLOR_MAP[Math.min(port, COLOR_MAP.length - 1)]);
            } else {
                System.out.println("OTHER line in SGTGrapher");
                lineAtt = new LineAttribute(LineAttribute.SOLID, COLOR_MAP[Math.min(port, COLOR_MAP.length - 1)]);
            }

            layout.addData(data, lineAtt);
            calculateNewRange(data);
        } else {
            data = (SimpleLine) layout.getData(String.valueOf(port));
            updateSGTData(data, graph);
            if (clipInExists.equals("true")) {
                data.getKeyTitle().setText(lineKeyTitle);
            } else {
                data.getKeyTitle().setText(data.getTitle());
            }

            calculateNewRange(data);
        }

        //layout.setBatch(false);

    }


    public void calculateNewRange(SimpleLine data) {
        Range2D xRange;
        Range2D yRange;
        SoTRange.Double xSoTRange, ySoTRange;
        SoTValue.Double xStart, yStart;
        SoTValue.Double xEnd, yEnd;
        double xMin, xMax, yMin, yMax;

        if (!getSnapshotStatus()) {

            xRange = layout.getRange().getXRange();
            yRange = layout.getRange().getYRange();


            if (isXAutoScale()) {
                xSoTRange = (SoTRange.Double) data.getXRange();
                xStart = (SoTValue.Double) xSoTRange.getStart();
                xEnd = (SoTValue.Double) xSoTRange.getEnd();

                if (data.getId().equals("0")) {
                    xMin = xStart.getValue();
                    xMax = xEnd.getValue();
                } else {
                    xMin = (xStart.getValue() < xRange.start) ? xStart.getValue() : xRange.start;
                    xMax = (xEnd.getValue() > xRange.end) ? xEnd.getValue() : xRange.end;
                }
                xMin -= Math.abs((xMax - xMin) / 15.0);
                xMax += Math.abs((xMax - xMin) / 15.0);
                System.out.println("xMin= " + xMin);
                System.out.println("xMax= " + xMax);
                xRange = new Range2D(xMin, xMax);
            }

            if (isYAutoScale()) {
                ySoTRange = (SoTRange.Double) data.getYRange();
                yStart = (SoTValue.Double) ySoTRange.getStart();
                yEnd = (SoTValue.Double) ySoTRange.getEnd();

                if (data.getId().equals("0")) {
                    yMin = yStart.getValue();
                    yMax = yEnd.getValue();
                } else {
                    yMin = (yStart.getValue() < yRange.start) ? yStart.getValue() : yRange.start;
                    yMax = (yEnd.getValue() > yRange.end) ? yEnd.getValue() : yRange.end;
                }
                yMin -= Math.abs((yMax - yMin) / 15.0);
                yMax += Math.abs((yMax - yMin) / 15.0);
                System.out.println("yMin= " + yMin);
                System.out.println("yMax= " + yMax);
                yRange = new Range2D(yMin, yMax);
            }

            if (forceEqualRangesMenu.getState()) {
                double xSize = xRange.end - xRange.start;
                double ySize = yRange.end - yRange.start;
                if (xSize > ySize) {
                    yRange = xRange;
                }
                if (ySize > xSize) {
                    xRange = yRange;
                }
            }

            try {
                layout.daveSetRange(new Domain(xRange, yRange));
            } catch (java.beans.PropertyVetoException e) {
            }

        }

    }


/*
* Method to rescale and re-zero data to provide SGTGrapher with 
* better axis displays.
* 
* The data are rescaled by powers of 10 if the median of their 
* absolute values lies outside the range 0.001 to 100. In 
* addition, if the relative difference between the smallest 
* and largest values is less than 0.1% then a value 
* is subtracted (this is called the offset) to make the resulting 
* values have a larger relative spread. 
* 
* The data are returned in an ArrayList which contains the 
* re-worked data values, the scale factor, and the offset. 
* To reproduce the original data from the returned values 
* you add the offset to all data values and then multiply 
* all of them by the scale.
*
* @param double[] in The original data
* @return ArrayList contains the reprocessed data, scale and offset
*/

    private ArrayList takeOutFactorsOfTen(double[] in) {


        int len = in.length;
        int j;
        double scale = 0.0, offset = 0.0;
        double[] out;
        double[] sorted = new double[len];


        System.arraycopy(in, 0, sorted, 0, len); // do not change array in
        sorted = TrianaSort.mergeSort(sorted);   // sort to find min, max
        double min = sorted[0];
        double max = sorted[len - 1];
        /*
            If array elements are not all one sign, then take absolute
            values and re-sort to find median. Want the median of absolute
            values as a test for scaling, not interested in signs. Median
            is more reliable than min or max, either of which could be
            near zero and hence give false idea of scale. Median of the
            absolute values should give a better idea of the scale of
            the data.
        */
        if (min * max < 0.0) {
            for (j = 0; j < len; j++) {
                sorted[j] = Math.abs(sorted[j]);
            }
            sorted = TrianaSort.mergeSort(sorted);
        }
        double median = Math.abs(sorted[len / 2]);


        out = in;  //rename and re-use array for output
        /*
            If median is smaller than 0.001 then rescale data by the power of 10
            puts it between 1 and 10. If median is larger than 100, rescale by
            the power of 10 that reduces it to a number between 1 and 10.
        */
        if (median < 0.001) {
            scale = Math.pow(10, -Math.ceil(Math.log(median) / Math.log(10.0)));
            for (j = 0; j < len; j++) {
                out[j] = scale * in[j];
            }
            System.out.println("in takeOutFactorsOfTen, scale= " + scale);
        } else if (median > 100) {
            scale = Math.pow(10, -Math.floor(Math.log(median) / Math.log(10.0)));
            for (j = 0; j < len; j++) {
                out[j] = scale * in[j];
            }
            System.out.println("in takeOutFactorsOfTen, scale= " + scale);
        }

        /*
            Now pack the data into an ArrayList for output.
        */

        ArrayList assemble = new ArrayList(2);
        assemble.add(out);
        assemble.add(new Double(scale));
        return assemble;
    }


/*
        In addition to rescaling, it might happen that the numbers are 
        so close to one another than one cannot see the differences in 
        the displayed scale. Test if the total range of the numbers 
        divided by the median is smaller than 0.001. This would mean 
        that the numbers differ typically in their fourth significant 
        digit. To correct this, define an offset equal to the value of 
        the median out to the number of significant digits that all 
        the values have in common, and subtract it (with the appropriate
        sign) from all the data values.
*/

    private ArrayList applyOffset(double[] in, boolean isXData) {


        int len = in.length;
        int j;
        double offset = 0.0;
        double[] out;
        double[] sorted = new double[len];


        System.arraycopy(in, 0, sorted, 0, len); // do not change array in
        sorted = TrianaSort.mergeSort(sorted);   // sort to find min, max
        double min = sorted[0];
        double max = sorted[len - 1];
        if (min * max < 0.0) {
            for (j = 0; j < len; j++) {
                sorted[j] = Math.abs(sorted[j]);
            }
            sorted = TrianaSort.mergeSort(sorted);
        }
        double median = Math.abs(sorted[len / 2]);

        out = in;  //rename and re-use array for output


        System.out.println("in applyOffset, median = " + median);
        System.out.println("in applyOffset, max = " + max);
        System.out.println("in applyOffset, min = " + min);

        //double test = ( max - min ) / median;
        double test = Math.abs((max - min) / (max + min) * 2);


        System.out.println("in applyOffset, test = " + test);

        if (test < 0.001) {
            double places = -Math.floor(Math.log(test) / Math.log(10.0));
            System.out.println("in applyOffset, places = " + places);
            if (isXData) {
                offset = Math.floor(min * Math.pow(10.0, places)) / Math.pow(10.0, places);
            } else {
                offset = Math.floor(median * Math.pow(10.0, places)) / Math.pow(10.0, places);
            }
            if (max * min < 0.0) {
                offset = 0.0;
            }
            for (j = 0; j < len; j++) {
                out[j] -= offset;
            }
        }

        /*
            Now pack the data into an ArrayList for output.
        */
        System.out.println("in applyOffset, offset= " + offset);

        ArrayList assemble = new ArrayList(2);
        assemble.add(out);
        assemble.add(new Double(offset));
        return assemble;
    }


    private void printGraph() {
        Color saveColor = layout.getBackground();
        RepaintManager currentManager = RepaintManager.currentManager(layout);

        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(layout);
        printJob.setJobName("Vector Demo");

        if (printJob.getPrintService() != null) {
            if (printJob.printDialog()) {
                try {
                    if (!saveColor.equals(Color.white)) {
                        layout.setBackground(Color.white);
                    }
                    layout.setPageAlign(AbstractPane.TOP, AbstractPane.CENTER);
                    currentManager.setDoubleBufferingEnabled(false);
                    printJob.print();
                } catch (PrinterException pe) {
                    System.out.println("Error printing: " + pe);
                } finally {
                    currentManager.setDoubleBufferingEnabled(true);
                    layout.setBackground(saveColor);
                }
            }
        } else {        // no printing service, e.g. no printcap file (j2sdk1.4)
            ByteArrayOutputStream outstream;
            BufferedOutputStream psOutput;
            StreamPrintService psPrinter;
            String psMimeType = "application/postscript";

            StreamPrintServiceFactory[] factories = PrinterJob.lookupStreamPrintServices(psMimeType);
            if (factories.length > 0) {
                try {
                    outstream = new ByteArrayOutputStream(65536); // so we can ask filename later!
                    psPrinter = factories[0].getPrintService(outstream);
                    printJob.setPrintService(psPrinter);
                    if (printJob.printDialog()) {
                        try {
                            if (!saveColor.equals(Color.white)) {
                                layout.setBackground(Color.white);
                            }
                            layout.setPageAlign(AbstractPane.TOP, AbstractPane.CENTER);
                            currentManager.setDoubleBufferingEnabled(false);
                            printJob.print();

                            JFileChooser fc = new JFileChooser();
                            fc.setDialogTitle("Choose A File For Postscript Output");
                            int val = fc.showSaveDialog(this);
                            if (val == JFileChooser.APPROVE_OPTION) {
                                File fn = fc.getSelectedFile();
                                if (fn.exists() && JOptionPane.showConfirmDialog(fc,
                                        "This file already exists.  Would you like to overwrite the existing file?",
                                        "Choose A File For Postscript Output", JOptionPane.YES_NO_OPTION)
                                        == JOptionPane.YES_OPTION || !fn.exists()) {
                                    psOutput = new BufferedOutputStream(new FileOutputStream(fn));
                                    outstream.writeTo(psOutput);
                                }
                            }
                        } catch (PrinterException pe) {
                            System.out.println("Error printing: " + pe);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            currentManager.setDoubleBufferingEnabled(true);
                            layout.setBackground(saveColor);
                        }
                    }
                } catch (PrinterException e) {
                    System.out.println("Error setting print service");
                    e.printStackTrace();
                }
            }
        }
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == inputnodeCombo) {
            SimpleLine data;

            JComboBox source = (JComboBox) ev.getSource();
            nodeNumber = (String) source.getSelectedItem();
            data = (SimpleLine) layout.getData(nodeNumber);
            keytitleField.setText(data.getKeyTitle().getText());
        }
        if (ev.getActionCommand().equals("Print")) {
            printGraph();
        }
        if (ev.getActionCommand().equals("Plot Properties")) {
            JClassTree ct = new JClassTree();
            ct.setModal(false);
            ct.setJPane(layout);
            ct.show();
        }
        if (ev.getActionCommand().equals("Line Key Title")) {
            showLineKeyTitleWindow();
        }
        if (ev.getActionCommand().equals("take out common factors of 10 from data")) {
            System.out.println("rescale axes to have nice numbers is " + takeOutFactorsOfTenMenu.getState());
        }
        if (ev.getActionCommand().equals("Linear-X")) {
            if (linearXPrevious) {
//                System.out.println("x already linear!!");
            } else {
                for (int port = 0; port < getTask().getDataInputNodeCount(); port++) {
                    layout.setBatch(true);
                    SimpleLine data = linXData(port);
                    linearXPrevious = true;
                    logXPrevious = false;
                    calculateNewRange(data);
                    layout.setBatch(false);
                }
            }
        }
        if (ev.getActionCommand().equals("Log-X")) {
            if (logXPrevious) {
//                System.out.println("x already log!!");
            } else {
                for (int port = 0; port < getTask().getDataInputNodeCount(); port++) {
                    layout.setBatch(true);
                    SimpleLine data = logXData(port);
                    linearXPrevious = false;
                    logXPrevious = true;
                    calculateNewRange(data);
                    layout.setBatch(false);
                }
            }
        }
        if (ev.getActionCommand().equals("Linear-Y")) {
            if (linearYPrevious) {
//                System.out.println("y already linear!!");
            } else {
                for (int port = 0; port < getTask().getDataInputNodeCount(); port++) {
                    layout.setBatch(true);
                    SimpleLine data = linYData(port);
                    linearYPrevious = true;
                    logYPrevious = false;
                    calculateNewRange(data);
                    layout.setBatch(false);
                }
            }
        }
        if (ev.getActionCommand().equals("Log-Y")) {
            if (logYPrevious) {
//                System.out.println("y already log!!");
            } else {
                for (int port = 0; port < getTask().getDataInputNodeCount(); port++) {
                    layout.setBatch(true);
                    SimpleLine data = logYData(port);
                    linearYPrevious = false;
                    logYPrevious = true;
                    calculateNewRange(data);
                    layout.setBatch(false);
                }
            }
        }
        if (ev.getSource() == snapshotButton) {
            if (snapshotButton.getText().equals("Snapshot")) {
                snapshotButton.setText("Continue");
            } else {
                snapshotButton.setText("Snapshot");
            }
        }

        if (ev.getSource() == resetZoomButton) {
            layout.resetZoom();
        }
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        try {
            if ((event.getSource() == keytitleField) && (!keytitleField.getText().equals(""))) {
                SimpleLine data;
                data = (SimpleLine) layout.getData(nodeNumber);
                data.getKeyTitle().setText(keytitleField.getText());
            }
        } catch (NumberFormatException except) {
            JOptionPane.showMessageDialog(null, "Invalid number format");
        }
    }


    public void itemStateChanged(ItemEvent ev) {
    }

    /**
     * Updates the graph when the SGTGraphData parameter is changed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        String paramname = event.getParameterName();
        if (paramname.equals("numberOfInputs")) {
            numberOfInputs = Integer.parseInt((String) getTask().getParameter(paramname));
        }

        if (paramname.equals("clipInExists")) {
            clipInExists = (String) getTask().getParameter(paramname);
            System.out.println("SGTGrapherPanel recieved clipInExists= " + clipInExists);
        }

        if (paramname.startsWith(GRAPH_DATA) && (getParameter(paramname) != null)
                && !(getParameter(paramname).equals("notConnected"))) {
            try {
                GraphType graphdata = (GraphType) getParameter(paramname);
                if (graphdata != null) {
                    int port = Integer.parseInt(paramname.substring(paramname.lastIndexOf('_') + 1));
                    graphData(graphdata, port);
                }
            } catch (ClassCastException except) {
                System.out.println("SGTGrapherPanel: Invalid parameter format");
            }
        }

        if (paramname.startsWith(GRAPH_DATA) && getParameter(paramname).equals("notConnected")) {
            int port = Integer.parseInt(paramname.substring(paramname.lastIndexOf('_') + 1));
            if (layout.getData(String.valueOf(port)) != null) {
                layout.clear(String.valueOf(port));
            }
        }

        if (paramname.equals("finished")) {
            System.out.println("Panel recieved finished");
            layout.setBatch(false);
            if (!xtitle.equals("")) {
                layout.setXTitle(xtitle);
            }
            if (!ytitle.equals("")) {
                layout.setYTitle(ytitle);
            }
            layout.setTitles(mainTitle, "", "");
        }

        if (paramname.equals("mainTitle")) {
            System.out.println("Panel recieved mainTitle");
            if (mainTitle.equals("")) {
                mainTitle = (String) getTask().getParameter(paramname);
            }
        }

        if (paramname.equals("xtitle")) {
            System.out.println("Panel recieved xtitle");
            if (xtitle.equals("")) {
                xtitle = (String) getTask().getParameter(paramname);
            }
        }

        if (paramname.equals("ytitle")) {
            System.out.println("Panel recieved ytitle");
            if (ytitle.equals("")) {
                ytitle = (String) getTask().getParameter(paramname);
            }
        }

        if (paramname.equals("lineKeyTitle")) {
            System.out.println("Panel recieved lineKeyTitle");
            lineKeyTitle = (String) getTask().getParameter(paramname);
        }

        if (paramname.equals("lineStyle")) {
            System.out.println("Panel recieved lineStyle");
            lineStyle = Integer.parseInt((String) getTask().getParameter(paramname));
        }

        if (paramname.equals("markType")) {
            System.out.println("Panel recieved markType");
            markType = Integer.parseInt((String) getTask().getParameter(paramname));
        }

        if (paramname.equals("markSize")) {
            System.out.println("Panel recieved markSize");
            markSize = Double.parseDouble((String) getTask().getParameter(paramname));
        }
    }

    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }


    public void nodeAdded(TaskNodeEvent event) {
    }

    public void nodeRemoved(TaskNodeEvent event) {
        if (event.isInputNode() && event.isDataNode() && (layout.getData(String.valueOf(event.getNodeIndex()))
                != null)) {
            layout.clear(String.valueOf(event.getNodeIndex()));
        }
    }


    class MyMouse extends MouseAdapter {

        public void mouseReleased(MouseEvent event) {
            Object object = event.getSource();

            if (object == layout) {
                maybeShowLineAttributeDialog(event);
            }
        }

        void maybeShowLineAttributeDialog(MouseEvent e) {
            if (e.isPopupTrigger() || e.getClickCount() == 2) {
                Object obj = layout.getObjectAt(e.getX(), e.getY());
                layout.setSelectedObject(obj);

                if (obj instanceof LineCartesianRenderer) {
                    LineAttribute attr = ((LineCartesianRenderer) obj).getLineAttribute();
                    if (lad_ == null) {
                        lad_ = new LineAttributeDialog();
                    }
                    lad_.setLineAttribute(attr);
                    if (!lad_.isShowing()) {
                        lad_.setVisible(true);
                    }
                }
            }
        }
    }


    /**
     * convert a 2d double array into a 1d double array by concatenating the rows together as one long row
     */
    public double[] convertArray(double[][] array) {

        int l = array.length;
        int w = array[0].length;

        System.out.println("Length : " + l);
        System.out.println("Width : " + w);

        int length = l * w;

        System.out.println("Total : " + length);

        double[] data = new double[length];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < w; j++) {
                int index = (i * w) + j;
                //System.out.print(index+" : ");
                data[index] = array[i][j];
            }
        }

        return data;
    }


}
