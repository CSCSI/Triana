package audio.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.trianacode.gui.Display;
import org.trianacode.gui.panels.ParameterPanel;
import triana.types.util.Str;

/**
 * $POPUP_DESCRIPTION
 *
 * @author $AUTHOR
 * @version $Revision: 2915 $
 */
public class LoadMP3Panel extends ParameterPanel {

    // Define GUI components here, e.g.
    public static String fileName;
    private long songSizeInSamples;
    private long bufSize = 16384;
    private long outputSizeInSamples;
    private int numberOfChunks;
    double duration, seconds;
    String lastDir = null;
    JFileChooser fc;

    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        try {
            File file;
            if (lastDir == null) {
                file = new File(System.getProperty("user.dir"));
            } else {
                file = new File(lastDir);
            }

            fc = new JFileChooser(file);
            fc.setFileFilter(new javax.swing.filechooser.FileFilter() {

                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName();

                    if (name.endsWith(".mp3") || name.endsWith(".MP3") || name.endsWith(".Mp3")) {
                        return true;
                    }
                    return false;
                }

                public String getDescription() {
                    return ".mp3, .MP3, .Mp3";
                }
            });

            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                // Pay attention to reference back to LoadMP3
                LoadMP3.createAudioInputStream(fc.getSelectedFile());

                String fn = fc.getSelectedFile().getAbsolutePath();
                userScreen(fc.getSelectedFile().getName());
                parameterUpdate("fileName", fn);

                setParameter((fileName), fn);
                setParameter((LoadMP3.fileName), fn);

                lastDir = fc.getSelectedFile().getPath();
            }
        } catch (SecurityException ex) {
            // JavaSound.showInfoDialog();
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void userScreen(String fileName) {

        long milliseconds = (long) ((LoadMP3.duration / 1000));// / LoadMP3.din.getFormat().getFrameRate());
        duration = milliseconds / 1000.0;

        System.out.println("DURATION = " + duration);

        long bufferLengthInFrames = (long) (duration * LoadMP3.decodedFormat.getSampleRate());//LoadMP3.din.getFrameLength();
        System.out.println("bufferLengthInFrames = " + bufferLengthInFrames);
        int samples = (int) bufferLengthInFrames;
        final double convertSampsToMSec = 1000.0 / (double) LoadMP3.decodedFormat.getSampleRate();
        int frameSizeInBytes = LoadMP3.decodedFormat.getFrameSize();
        System.out.println("frameSizeInBytes = " + frameSizeInBytes);

        bufSize = (bufferLengthInFrames) * frameSizeInBytes;
        System.out.println("bufSize = " + bufSize);

        songSizeInSamples = bufferLengthInFrames;
        System.out.println("songSizeInSamples = " + songSizeInSamples);

        final JSlider slider = new JSlider();
        final JTextField text = new JTextField(String.valueOf(milliseconds));
        final JTextField textSamp = new JTextField(String.valueOf(samples));
        int range = (int) milliseconds;

        slider.setMaximum(range);
        slider.setMinimum(0);
        slider.setValue(range);
        slider.setMinorTickSpacing(100);
        slider.setExtent(100);
        slider.setMajorTickSpacing(1000);
        slider.setEnabled(false);

        text.setEnabled(false);
        textSamp.setEnabled(false);
        final JPanel chunkit = new JPanel();
        chunkit.setLayout(new GridLayout(4, 1));
        chunkit.setBackground(Color.white);
        chunkit.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0));

        chunkit.setEnabled(false);

        final JCheckBox entireFile = new JCheckBox("Load the entire audio file ?", true);
        entireFile.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (entireFile.isSelected()) {
                    slider.setEnabled(false);
                    text.setEnabled(false);
                    textSamp.setEnabled(false);
                    chunkit.setEnabled(false);
                } else {
                    slider.setEnabled(true);
                    text.setEnabled(true);
                    textSamp.setEnabled(true);
                    chunkit.setEnabled(true);
                }
            }
        });
        JLabel fname = new JLabel(fileName + " : " + duration + " seconds in length (" + bufferLengthInFrames + " samples)");
        JLabel formatLab = new JLabel("Format : " + LoadMP3.decodedFormat.toString());

        JLabel stream = new JLabel("Or Stream Audio As Follows :", JLabel.CENTER);
        stream.setForeground(Color.black);
        stream.setBackground(Color.magenta);
        JLabel chunkLab = new JLabel("Specify Output Size (in milliseconds or samples) : ");
        JButton ok = new JButton("OK");
        final JDialog frame = new JDialog(new Frame(), "Configure Output Mode", true);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frame.setVisible(false);
            }
        });

        JPanel textRow = new JPanel();
        textRow.setLayout(new GridLayout(1, 4));
        textRow.add(new JLabel("Milliseconds :"));
        textRow.add(text);
        textRow.add(new JLabel("Samples :"));
        textRow.add(textSamp);
        textRow.setBackground(Color.white);

        JPanel desc = new JPanel();
        desc.setLayout(new GridLayout(3, 1));
        desc.setBackground(Color.lightGray);
        desc.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        desc.add(fname);
        desc.add(formatLab);
        desc.add(entireFile);

        chunkit.add(stream);
        chunkit.add(chunkLab);
        chunkit.add(slider);
        chunkit.add(textRow);
        chunkit.setBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        content.add(desc, BorderLayout.NORTH);
        content.add(chunkit, BorderLayout.CENTER);
        content.add(ok, BorderLayout.SOUTH);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int sampVal = (int) ((double) slider.getValue() / convertSampsToMSec);
                text.setText(String.valueOf(slider.getValue()));
                textSamp.setText(String.valueOf(sampVal));
            }
        });
        text.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slider.setValue(Str.strToInt(text.getText()));
            }
        });

        textSamp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int secVal = (int) ((double) Str.strToInt(textSamp.getText()) * convertSampsToMSec);
                slider.setValue(secVal);
            }
        });

        frame.pack();
        Display.centralise(frame);
        frame.setVisible(true);

        if (entireFile.isSelected()) {
            numberOfChunks = 1;
            outputSizeInSamples = songSizeInSamples;
            System.out.println("Single chunk");
        } else {
            outputSizeInSamples = (long) ((slider.getValue() / 1000.0) * LoadMP3.decodedFormat.getSampleRate());
            System.out.println("outputSizeInSamples = " + outputSizeInSamples);
            System.out.println("slider.getValue() = " + slider.getValue());

            numberOfChunks = (int) (songSizeInSamples / outputSizeInSamples);

            if ((songSizeInSamples % outputSizeInSamples) > 0) {
                ++numberOfChunks;
            }
        }

        long by = (long) (frameSizeInBytes * outputSizeInSamples);
        System.out.println("Number of chunks = " + numberOfChunks);

        LoadMP3.bufSize = (by);
        LoadMP3.songSizeInSamples = (songSizeInSamples);
        LoadMP3.outputSizeInSamples = (outputSizeInSamples);
        LoadMP3.numberOfChunks = (numberOfChunks);
        //LoadMP3.bytes = null;
        LoadMP3.ma = null;

        if (entireFile.isSelected()) {
            LoadMP3.gotEntireFile = true;
        } else {
            LoadMP3.gotEntireFile = false;
        }
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

        if (paramname.equals("fileName")) {
            fileName = (String) value;
            LoadMP3.fileName = (fileName);
            System.out.println("filename from paramupdate = " + fileName);
        }

        if (paramname.equals("bufSize")) {
            bufSize = new Long((String) value).longValue();
            LoadMP3.bufSize = (bufSize);
            System.out.println("bufSize from paramupdate = " + bufSize);
        }

        if (paramname.equals("songSizeInSamples")) {
            songSizeInSamples = new Long((String) value).longValue();
            System.out.println("songSizeInSamples from paramupdate = " + songSizeInSamples);
            LoadMP3.songSizeInSamples = (songSizeInSamples);
        }

        if (paramname.equals("outputSizeInSamples")) {
            outputSizeInSamples = new Long((String) value).longValue();
            System.out.println("outputSizeInSamples from paramupdate = " + outputSizeInSamples);
            LoadMP3.outputSizeInSamples = (outputSizeInSamples);
        }

        if (paramname.equals("numberOfChunksInSong")) {
            numberOfChunks = new Integer((String) value).intValue();
            LoadMP3.numberOfChunks = (numberOfChunks);

            System.out.println("numberOfChunks from paramupdate = " + numberOfChunks);
        }
    }

    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
    }
}
