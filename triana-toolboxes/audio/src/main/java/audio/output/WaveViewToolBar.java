package audio.output;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;
import org.trianacode.taskgraph.util.FileUtils;

public class WaveViewToolBar extends JToolBar {
    ActionListener waveview;

    JButton fullSize;
    JButton zoomIn;
    JButton zoomOut;
    JButton print;
    JButton detail;
    JButton properties;

    public WaveViewToolBar(String title, ActionListener gl) {
        super(title, VERTICAL);
        setFloatable(true);
        waveview = gl;
        createWidgets();
    }

    public void createWidgets() {
        fullSize = new JButton(FileUtils.getSystemImageIcon("ZoomFullSize.gif"));
        fullSize.setToolTipText("Full Size");

        zoomIn = new JButton(FileUtils.getSystemImageIcon("ZoomIn.gif"));
        zoomIn.setToolTipText("Zoom In");

        zoomOut = new JButton(FileUtils.getSystemImageIcon("ZoomOut.gif"));
        zoomOut.setToolTipText("Zoom Out");

        print = new JButton(FileUtils.getSystemImageIcon("Print.gif"));
        print.setToolTipText("Print");

        detail = new JButton(FileUtils.getSystemImageIcon("Detail.gif"));
        detail.setToolTipText("Full Waveform Resolution");

        properties = new JButton(FileUtils.getSystemImageIcon("Properties.gif"));
        properties.setToolTipText("Properties");

        add(fullSize);
        add(zoomIn);
        add(zoomOut);
        add(print);
        add(detail);
        add(properties);

        fullSize.addActionListener(waveview);
        zoomIn.addActionListener(waveview);
        zoomOut.addActionListener(waveview);
        print.addActionListener(waveview);
        detail.addActionListener(waveview);
        properties.addActionListener(waveview);
    }
}
