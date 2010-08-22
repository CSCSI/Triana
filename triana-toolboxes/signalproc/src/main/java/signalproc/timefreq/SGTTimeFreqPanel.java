package signalproc.timefreq;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;
import gov.noaa.pmel.sgt.ColorMap;
import gov.noaa.pmel.sgt.GridAttribute;
import gov.noaa.pmel.sgt.IndexedColorMap;
import gov.noaa.pmel.sgt.LinearTransform;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Range2D;
import triana.types.MatrixType;
import triana.types.TimeFrequency;
import triana.types.util.FlatArray;


public class SGTTimeFreqPanel extends ParameterPanel
        implements ActionListener {

    // Define GUI components here, e.g.
    //
    // private JTextField namelabel = new JTextField();

    //the graph layout panel
    JPlotLayout layout;
    GridAttribute attribute;

    public static final String GRAPH_DATA = "SGTTimeFreqData";
    private JButton resetZoomButton;
    private JCheckBox autoScaleCheck;
    private JCheckBox logLevelCheck;

    MatrixType graphData;

    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        // Insert code to layout GUI here, e.g.
        //
        // add(namelabel);
        layout = new JPlotLayout(true, false, false, "f", null, false);
        layout.setTitles("", "", "");

        setLayout(new BorderLayout());
        add(layout, BorderLayout.CENTER);
        getTask().addTaskListener(this);

        JPanel south = new JPanel(new BorderLayout());
        add(south, BorderLayout.SOUTH);
        JPanel bP = new JPanel(new GridLayout(1, 3));
        south.add(bP, BorderLayout.EAST);

        resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.addActionListener(this);
        bP.add(resetZoomButton);

        autoScaleCheck = new JCheckBox("Auto Scale");
        autoScaleCheck.setSelected(true);
        bP.add(autoScaleCheck);

        logLevelCheck = new JCheckBox("Level in Log Scale");
        logLevelCheck.setSelected(false);
        logLevelCheck.addActionListener(this);
        bP.add(logLevelCheck);

    }

    public void graph(MatrixType timeFreq) {

        SimpleGrid sgtData;
        Component parent = this;
        double[] time;
        double[] freq;
        double[][] data2d;
        double[] data;
        String title;

        layout.setBatch(true);

        if (timeFreq instanceof TimeFrequency) {
            time = timeFreq.getIndependentScaleReal(0);
            freq = timeFreq.getIndependentScaleReal(1);
            data2d = (double[][]) timeFreq.getGraphArrayReal(0);
            data = convertRowsToAnArray(data2d);
        } else if (timeFreq instanceof MatrixType) {
            time = timeFreq.getXorYArray(0);
            freq = timeFreq.getXorYArray(1);
            data2d = (double[][]) timeFreq.getData();
            //data = convertColsToAnArray(data2d);
            data = convertRowsToAnArray(data2d);
        } else {
            return;
        }
        title = timeFreq.getTitle();
        if (title == null) {
            title = "(n/a)";
        }

        if (logLevelCheck.isSelected()) {
            for (int i = 0; i < data.length; i++) {
                data[i] = Math.log(data[i]) * 0.43429448190325182765d;
            }
        }

        double r = FlatArray.minArray(data);
        double b = FlatArray.maxArray(data);
        double g = (r + b) / 2.0;
        ;

        Range2D datar = new Range2D(r, b);
        ColorMap cmap = createColorMap(datar);


        while ((parent != null) && (!(parent instanceof Window))) {
            parent = parent.getParent();
        }

        if ((parent != null) && (!parent.isVisible())) {
            parent.setVisible(true);
        }

        if ((layout.getData("data")) == null) {
            //add new data
            sgtData = new SimpleGrid(data, time, freq, "");
            sgtData.setId("data");
            sgtData.setXMetaData(new SGTMetaData()); // needed or you get a NullPointerException
            sgtData.setYMetaData(new SGTMetaData()); // needed or you get a NullPointerException

            attribute = new GridAttribute(GridAttribute.RASTER, cmap);
            layout.addData(sgtData, attribute, "amplitude");
            //layout.setKeyBoundsP(new Rectangle2D.Double());
            //layout.setKeyLocationP(new Point2D.Double());
            layout.setXTitle("xtitle");
            layout.setYTitle("ytitle");
            layout.setTitles(title, "", "");

        } else {
            //update data
            sgtData = (SimpleGrid) layout.getData("data");
            //layout.updateColorKey(cmap);
            sgtData.setXArray(time);
            sgtData.setYArray(freq);
            sgtData.setZArray(data);

            attribute = new GridAttribute(GridAttribute.RASTER, cmap);
            layout.updateData(sgtData, attribute);
        }


        if (autoScaleCheck.isSelected()) {
            layout.resetZoom();
        }

        layout.setBatch(false);

    }


    /**
     * convert a 2d double array into a 1d double array by concatenating the rows together as one long row
     */
    public double[] convertRowsToAnArray(double[][] array) {

        int l = array.length;
        int w = array[0].length;

        System.out.println("Length : " + l);
        System.out.println("Width : " + w);

        int length = l * w;

        System.out.println("Total : " + length);

        double[] data = new double[length];
        for (int j = 0; j < l; j++) {
            for (int k = 0; k < w; k++) {
                int index = (j * w) + k;
                data[index] = array[j][k];
            }
        }

        return data;
    }

    /**
     * convert a 2d double array into a 1d double array by concatenating the columns together as one long array
     */
    public double[] convertColsToAnArray(double[][] array) {

        int l = array.length;
        int w = array[0].length;

        System.out.println("Length : " + l);
        System.out.println("Width : " + w);

        int length = l * w;

        System.out.println("Total : " + length);

        double[] data = new double[length];
        for (int k = 0; k < w; k++) {
            for (int j = 0; j < l; j++) {
                int index = (k * l) + j;
                data[index] = array[j][k];
            }
        }

        return data;
    }


    ColorMap createColorMap(Range2D datar) {
        int[] red =
                {0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        0, 7, 23, 39, 55, 71, 87, 103,
                        119, 135, 151, 167, 183, 199, 215, 231,
                        247, 255, 255, 255, 255, 255, 255, 255,
                        255, 255, 255, 255, 255, 255, 255, 255,
                        255, 246, 228, 211, 193, 175, 158, 140};
        int[] green =
                {0, 0, 0, 0, 0, 0, 0, 0,
                        0, 11, 27, 43, 59, 75, 91, 107,
                        123, 139, 155, 171, 187, 203, 219, 235,
                        251, 255, 255, 255, 255, 255, 255, 255,
                        255, 255, 255, 255, 255, 255, 255, 255,
                        255, 247, 231, 215, 199, 183, 167, 151,
                        135, 119, 103, 87, 71, 55, 39, 23,
                        7, 0, 0, 0, 0, 0, 0, 0};
        int[] blue =
                {127, 143, 159, 175, 191, 207, 223, 239,
                        255, 255, 255, 255, 255, 255, 255, 255,
                        255, 255, 255, 255, 255, 255, 255, 255,
                        255, 247, 231, 215, 199, 183, 167, 151,
                        135, 119, 103, 87, 71, 55, 39, 23,
                        7, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0};

        IndexedColorMap cmap = new IndexedColorMap(red, green, blue);
        cmap.setTransform(new LinearTransform(0.0, (double) red.length,
                datar.start, datar.end));
        return cmap;
    }


    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == resetZoomButton) {
            layout.resetZoom();
        }
        if (ev.getSource() == logLevelCheck) {
            reGraph();
        }
    }

    private void reGraph() {
        graph(graphData);
    }

    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        // Insert code to synchronise the GUI with the task parameters here, e.g.
        //
        // namelabel.setText(getParameter("name"));
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        // Insert code to update GUI in response to parameter changes here, e.g.
        //
        // if (paramname.equals("name"))
        //     namelabel.setText(value);
        if (paramname.equals(GRAPH_DATA) && (value != null)) {
            System.out.println("Graphing Data");
            this.graphData = (MatrixType) value;
            graph((MatrixType) value);
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
        getTask().removeTaskListener(this);
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
}
