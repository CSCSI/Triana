package audio.output;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.trianacode.gui.Display;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.UnitPanel;
import triana.types.OldUnit;
import triana.types.TrianaType;
import triana.types.audio.MultipleAudio;

/**
 * A WaveView unit to ..
 *
 * @author ian
 * @version 2.0 31 Dec 2000
 */
public class WaveView extends OldUnit implements ActionListener, AdjustmentListener {
    /**
     * The UnitPanel for WaveView
     */
    WaveViewParameters waveViewParameters;
    WaveViewPanel waveViewPanel;
    WaveViewToolBar waveViewToolBar;
    JScrollPane scrollerForWaveViewPanel;
    JFrame waveViewPanelWindow;

    JTextField info;
    String infoText;

    int winSizeX = 800;
    int winSizeY = 300;

    int max;
    int val;

    /**
     * ********************************************* ** USER CODE of WaveView goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        TrianaType in = getInputNode(0);

        if (in instanceof MultipleAudio) {
            MultipleAudio input = (MultipleAudio) in;

            javax.sound.sampled.AudioFormat audioFormat = input.getAudioFormat();

            int dataLength = input.getChannelLength(0);
            double duration = ((double) dataLength) / (double) input.getChannelFormat(0).getSamplingRate();

            infoText = audioFormat.toString() + ", duration : " + String.valueOf(duration) + " seconds";
            setInfo();

            waveViewPanel.initialize(input);

            for (int chan = 0; chan < input.getChannels(); ++chan) {
                waveViewPanel.addWave(input, chan);
            }
            waveViewPanel.drawGraph();
            waveViewPanelWindow.setVisible(true);
        }
    }

    /**
     * Initialses information specific to WaveView.
     */
    public void init() {
        super.init();

        waveViewPanel = new WaveViewPanel();
        waveViewPanelWindow = new JFrame(getName() + " Viewer");
        waveViewToolBar = new WaveViewToolBar(getName() + " toolbar", this);
        waveViewPanelWindow.setSize(winSizeX, winSizeY);
        waveViewPanelWindow.setLocation(20, Display.screenY - winSizeY - 50);

        scrollerForWaveViewPanel = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollerForWaveViewPanel.setViewportView(waveViewPanel);

        info = new JTextField();
        info.setEditable(false);

        waveViewPanelWindow.setIconImage(GUIEnv.getTrianaImage());
        waveViewPanelWindow.getContentPane().setLayout(new BorderLayout());
        waveViewPanelWindow.getContentPane().add(scrollerForWaveViewPanel, BorderLayout.CENTER);
        waveViewPanelWindow.getContentPane().add(waveViewToolBar, BorderLayout.EAST);
        waveViewPanelWindow.getContentPane().add(info, BorderLayout.SOUTH);

        setResizableInputs(false);
        setResizableOutputs(false);

        waveViewParameters = new WaveViewParameters();
        waveViewParameters.setObject(this);
    }

    public void setInfo() {
        String extrainfo;
        if (waveViewPanel.getDetail()) {
            extrainfo = " Reduced View";
        } else {
            extrainfo = " Enhanced View";
        }

        info.setText(infoText + extrainfo);
    }

    /**
     * Reset's WaveView
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
    public void starting() {
        super.starting();
    }

    /**
     * Saves WaveView's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of WaveView's parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to WaveView, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "triana.types.audio.MultipleAudio";
    }

    /**
     * @return a string containing the names of the types output from WaveView, each separated by a white space.
     */
    public String outputTypes() {
        return "none";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "A viewer for audio and time series data";
    }

    /**
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "WaveView.html";
    }

    /**
     * @return WaveView's parameter panel
     */
    public UnitPanel getParameterPanel() {
        return waveViewParameters;
    }


    public void cleanUp() {
        super.cleanUp();
        waveViewPanelWindow.setVisible(false);
    }

    /**
     * Captures the events thrown out by WaveViewParameters.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

        if (e.getSource() instanceof JButton) {
            JScrollBar scrollbar = scrollerForWaveViewPanel.getHorizontalScrollBar();
            boolean scrollbarAdjust = false;
            max = scrollbar.getMaximum();
            val = scrollbar.getValue();

            Dimension d = scrollerForWaveViewPanel.getSize();
            int w = d.width - scrollerForWaveViewPanel.getInsets().left
                    - scrollerForWaveViewPanel.getInsets().right;
            int h = d.height - scrollerForWaveViewPanel.getInsets().top
                    - scrollerForWaveViewPanel.getInsets().bottom -
                    scrollbar.getSize().height;
            JButton b = (JButton) e.getSource();
            if (b == waveViewToolBar.zoomIn) {
                waveViewPanel.zoomIn(w, h);
                scrollbarAdjust = true;
            } else if (b == waveViewToolBar.zoomOut) {
                waveViewPanel.zoomOut(w, h);
                scrollbarAdjust = true;
            } else if (b == waveViewToolBar.fullSize) {
                waveViewPanel.fullSize(w, h);
            } else if (b == waveViewToolBar.print) {
            } //
            else if (b == waveViewToolBar.detail) {
                waveViewPanel.detail();
                setInfo();
            } else if (b == waveViewToolBar.properties) {
                doubleClick();
            }
            if (scrollbarAdjust) {
                scrollbar.addAdjustmentListener(this);
            }
        }
    }


    public void adjustmentValueChanged(AdjustmentEvent e) {
        JScrollBar scrollbar = (JScrollBar) e.getAdjustable();
        scrollbar.removeAdjustmentListener(this);
        int newmax = scrollbar.getMaximum();

        System.out.println("Val =  " + val);

        System.out.println("Max =  " + max);
        System.out.println("New max = " + newmax);

        double ratio;

        if ((val == 0) && (max < winSizeX))  // first time zomming in : place in center
        {
            ratio = 0.5;
        } else {
            ratio = (double) val / (double) max;
        }

        int newPos = (int) ((double) newmax * ratio);
        scrollbar.setValue(newPos);
    }
}




