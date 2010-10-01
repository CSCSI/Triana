package signalproc.output;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JTabbedPane;
import javax.swing.JTree;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotter;
import hep.aida.IPlotterRegion;
import jas.aida.gui.JASGUIAnalysisFactory;
import triana.types.GraphType;

/**
 * Used by Histogrammer to plot graphs on the client side.
 *
 * @author David Churches
 * @version $Revision $
 */

public class HistogrammerPanel extends ParameterPanel
        implements TaskListener, ItemListener, ActionListener {


    private JTree jTree;
    private IAnalysisFactory af;
    private IPlotter plotter;
    private IPlotterRegion plotterRegion;
    private IHistogramFactory hf;
    private JTabbedPane tabs;
    private ICloud1D h1d;
    Vector dh = new Vector();
    Vector histograms = new Vector();


    /**
     * A flag indicating whether this is the first set of data or not
     */
    private boolean first = true;


    /**
     * Creates a new HistogrammerPanel.
     */
    public HistogrammerPanel() {
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


        setLayout(new BorderLayout());
        setSize(400, 400);

        jTree = new JTree(new javax.swing.tree.DefaultMutableTreeNode("/"));
        tabs = new JTabbedPane();

        add(tabs, BorderLayout.CENTER);

    }


    public void graphData(GraphType graph, int port) {
        double[] data, range;
        Component parent = this;

        while ((parent != null) && (!(parent instanceof Window))) {
            parent = parent.getParent();
        }

        if ((parent != null) && (!parent.isVisible())) {
            parent.setVisible(true);
        }

        if (first) {

            System.out.println("first=true");

            af = new JASGUIAnalysisFactory(jTree, tabs);
            hf = af.createHistogramFactory(af.createTreeFactory().create());
            // plotter is an object of type JASGUIPlotter, which itself extends JASPlotter
            plotter = af.createPlotterFactory().create(graph.getTitle());
            //plotterRegion is an object of type JASPlotterRegion
            plotterRegion = plotter.createRegion(0, 0, 1, 1);
            h1d = hf.createCloud1D("hist1", "Histogram");


            data = (double[]) graph.getGraphArrayReal(0);
            dh.addElement(data);
            range = calculateRange(data);

            for (int i = 0; i < graph.getDimensionLengths(0); i++) {
                h1d.fill(data[i]);
            }


            histograms.addElement(h1d);
            plotterRegion.plot((ICloud1D) histograms.elementAt(port));
            plotter.show();

            first = false;
        } else {
            System.out.println("first=false");

            try {
                dh.elementAt(port);
                System.out.println("retrieving dh.elementAt(" + port + ")");
                dh.removeElementAt(port);
                data = (double[]) graph.getGraphArrayReal(0);
                dh.insertElementAt(data, port);
                ICloud1D temp = (ICloud1D) histograms.elementAt(port);
                temp.reset();
                for (int i = 0; i < graph.getDimensionLengths(0); i++) {
                    temp.fill(data[i]);
                }
                range = calculateRange(data);
                plotter.refresh();
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("no data at this point in the vector!!");
                data = (double[]) graph.getGraphArrayReal(0);
                dh.insertElementAt(data, port);
                ICloud1D hist = hf.createCloud1D("hist1", "Histogram");
                for (int i = 0; i < graph.getDimensionLengths(0); i++) {
                    hist.fill(data[i]);
                }
                histograms.insertElementAt(hist, port);
                range = calculateRange(data);
                plotterRegion.plot((ICloud1D) histograms.elementAt(port));
            }
        }
    }

    public double[] calculateRange(double[] data) {
        double xMin, xMax;
        double[] range = new double[2];

        xMin = data[0];
        xMax = data[0];

        int index = 0;

        for (int count = 0; count < data.length; count++) {
            if (xMin > data[count]) {
                xMin = data[count];
            }
            if (xMax < data[count]) {
                xMax = data[count];
            }
        }

        range[0] = xMin - Math.abs((xMin / 10));
        range[1] = xMax + Math.abs((xMax / 10));

        return range;
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

        if (paramname.startsWith("HistogrammerData") && (getTask().getParameter(paramname) != null)) {
            try {
                GraphType graphdata = (GraphType) getTask().getParameter(paramname);

                if (graphdata != null) {
                    int port = Integer.parseInt(paramname.substring(paramname.lastIndexOf('_') + 1));
                    graphData(graphdata, port);
                }
            }
            catch (ClassCastException except) {
                System.out.println("HistogrammerPanel: Invalid parameter format");
            }
        }
    }

    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    public void nodeAdded(TaskNodeEvent event) {
    }

    public void nodeRemoved(TaskNodeEvent event) {
        //dh.removeElementAt(node.getNodeIndex());
        //ICloud1D temp = (ICloud1D)histograms.elementAt(node.getNodeIndex());
        //temp.reset();
        //histograms.removeElementAt(node.getNodeIndex());
        //plotter.refresh();
    }


    class dataHolder {

        Vector dataSets = new Vector();

        void addData(double[] data) {
            dataSets.addElement(data);
        }

        double[] getData(int port) {
            if (dataSets.elementAt(port) == null) {
                return null;
            } else {
                return (double[]) dataSets.elementAt(port);
            }

        }


    }


}








