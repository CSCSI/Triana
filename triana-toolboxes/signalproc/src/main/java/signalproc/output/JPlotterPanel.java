package signalproc.output;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JFrame;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import jplot.JPlot;
import triana.types.GraphType;
import triana.types.Histogram;


/**
 * Used by JPlotter to plot graphs on the client side.
 *
 * @author David Churches
 * @version $Revision $
 */

public class JPlotterPanel extends ParameterPanel
        implements TaskListener, ItemListener, ActionListener {

    private static JPlot jp;
    public static int WIDTH = 520;
    public static int HEIGHT = 400;
    private dataHolder dataSets = new dataHolder();


    /**
     * A flag indicating whether this is the first set of data or not
     */
    private boolean first = true;


    /**
     * Creates a new JPlotterPanel.
     */
    public JPlotterPanel() {
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
    }

    /**
     * Disposes of the graph window and removes this panel as a task listener.
     */
    public void dispose() {
        getTask().removeTaskListener(this);
    }


    private void initPanel() {

        JFrame frame = new JFrame("JPlot");
        setLayout(new BorderLayout());
        setSize(WIDTH, HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - WIDTH / 2, screenSize.height / 2 - HEIGHT / 2);
        jp = new JPlot(frame);
        add(jp, BorderLayout.CENTER);

    }


    public void graphData(GraphType graph, int port) {
        double[] xData, yData;
        System.out.println("inside graphData");
        String filename = "node" + port;
        System.out.println("String is " + filename);

        if (dataSets.getData(port) == null) {

            dataSets.addDataSet(port);
            if (graph instanceof Histogram) {
                xData = ((Histogram) graph).getDelimiterMidpoints(0);
            } else {
                xData = graph.getIndependentScaleReal(0);
            }
            yData = (double[]) graph.getGraphArrayReal(0);
            jp.chooseDatafile(filename, xData, yData);

            //jp.showGraph(true);
        } else {
            xData = graph.getIndependentScaleReal(0);
            yData = (double[]) graph.getGraphArrayReal(0);
            jp.updateDatafile(filename, xData, yData);
            //jp.showGraph(true);
        }
    }


    public void actionPerformed(ActionEvent ev) {
    }


    public void itemStateChanged(ItemEvent ev) {
    }

    /**
     * Updates the graph when the HistogrammerData parameter is changed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        String paramname = event.getParameterName();
        //System.out.println("Panel recieved parameter "+ paramname);

        if (paramname.startsWith("JPlotterData") && (getTask().getParameter(paramname) != null)) {
            try {
                GraphType graphdata = (GraphType) getTask().getParameter(paramname);

                if (graphdata != null) {
                    int port = Integer.parseInt(paramname.substring(paramname.lastIndexOf('_') + 1));
                    graphData(graphdata, port);
                }
            }
            catch (ClassCastException except) {
                System.out.println("JPlotterPanel: Invalid parameter format");
            }
        }
        if (paramname.equals("GraphData")) {
            System.out.println("Panel revieved parameter GraphData");
            jp.showGraph(true);
        }
    }

    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    public void nodeAdded(TaskNodeEvent event) {
    }

    public void nodeRemoved(TaskNodeEvent event) {
        System.out.println("remvoing node " + event.getNode().getNodeIndex());
        jp.removeDatafile(event.getNode().getNodeIndex());
        dataSets.removeDataSet(event.getNode().getNodeIndex());
    }


    class dataHolder {

        Vector dataSetsVector = new Vector(20, 10);

        void addDataSet(int i) {
            Integer number = new Integer(i);
            dataSetsVector.addElement(number);
        }


        void removeDataSet(int i) {
            dataSetsVector.removeElementAt(i);
        }

        Integer getData(int port) {
            System.out.println("there are " + getTask().getDataInputNodeCount() + " nodes connected");
            System.out.println("dataSetsVector.size()= " + dataSetsVector.size());
            try {
                if (dataSetsVector.elementAt(port) == null) {
                    return null;
                } else {
                    return (Integer) dataSetsVector.elementAt(port);
                }
            }
            catch (ArrayIndexOutOfBoundsException except) {
                System.out.println("JPlotterPanel: index " + port + " in dataSetsVector >= dataSetsVector.length");
                return null;
            }
        }

    }


}








