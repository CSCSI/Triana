package audio.output;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;
import org.trianacode.gui.windows.ErrorDialog;
import triana.types.audio.MultipleAudio;

class WaveViewPanel extends JPanel implements Serializable {
    // for double buffering

    transient Dimension offDimension;
    transient Image offImage;
    transient Graphics offGraphics;

    private Font font10 = new Font("serif", Font.PLAIN, 10);
    private Font font12 = new Font("serif", Font.PLAIN, 12);
    Color jfcBlue = new Color(204, 204, 255);
    Color pink = new Color(255, 175, 175);

    int initialWidth;
    int initialHeight;
    double zoomFactor = 3.0;

    //data related 
    Vector audioData = new Vector();
    AudioFormat audioFormat;
    int dataLength;
    double duration, seconds = 0;
    int srate;
    int channels;
    double dataScale;

    /**
     * The space for the information line at the bottom of the image
     */
    int XLAB = 35; // on the right
    int YLAB = 15; // at the bottom

    int xLabspace = 60;

    // variables which can be altered by the user

    Color foreground = jfcBlue;
    Color background = new Color(20, 20, 20);
    Color axis = pink;
    Color axisText = Color.black;

    // for efficient drawing of the waveform (i.e. only draw points viewable)

    boolean efficientDraw = true;
    int efficientViewFactor = 1;

    boolean painting = false;

    public WaveViewPanel() {
        super();
        setBackground(background);
        setDoubleBuffered(false);
        setOpaque(true);
    }

    /**
     * resets the parameters ready for adduing new data
     */
    public void initialize(MultipleAudio au) {
        audioFormat = au.getAudioFormat();
        dataLength = au.getChannelLength(0);
        srate = au.getChannelFormat(0).getSamplingRate();
        duration = ((double) dataLength) / (double) srate;
        channels = au.getChannels();
        audioData.removeAllElements();
        dataScale = Math.pow(2.0, (double) (audioFormat.getSampleSizeInBits() - 1));
    }

    public void addWave(MultipleAudio au, int chan) {
        audioData.addElement(au.getChannel(chan));
    }

    public void drawGraph() {
        painting = true;
        repaint();
    }

    public void paintGraph(Graphics g) {
        Dimension d = getSize();
        int w = d.width;
        int h = d.height;

        int graphAreaX = w - XLAB;
        int graphAreaY = (h - YLAB) / channels;

        // Graph
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(background);
        g2.clearRect(0, 0, w, h);

        // axis border
        g2.setColor(axis);
        g2.fillRect(0, h - YLAB, w, h);
        g2.fillRect(w - XLAB, 0, w, h);

        // draw X axis labels *********************
        g2.setFont(font10);
        g2.setColor(axisText);

        double secs = (int) (dataLength / srate);

        int totalTicks = w / xLabspace;
        double[] labels = null;

        try {
            labels = WaveViewLabelling.label(0.0, secs, totalTicks);
        } catch (Exception e) {
            System.out.println("Exception in x labelling");
        }

        totalTicks = labels.length; // new length
        double newmax = labels[labels.length - 1];
        double ratio = newmax / secs;

        double scalar = graphAreaX / secs;
        double xl;
        int len = labels.length;
        if (newmax > secs) {
            --len;
        }

        for (int i = 1; i < len; ++i) {
            xl = labels[i] * scalar;
            g2.drawLine((int) xl, h - YLAB, (int) xl, h - YLAB + 4);
            g2.drawString(String.valueOf(labels[i]), (int) xl - 6, h - 2);
        }

        g2.setColor(foreground);

        // calculate graph area;

        double rec_srate = 1.0 / (double) srate;

        double xmove;

        if (efficientDraw) {
            xmove = 1.0;
        } else {
            xmove = (double) ((double) graphAreaX / (double) dataLength);
        }

        double samplesPerPixel = (double) dataLength / (double) graphAreaX;

        double xskip;

        if (efficientDraw) {
            xskip = samplesPerPixel;
        } else {
            xskip = 1.0;
        }

        double y_new;
        double x_new;

        double ystart = 0.0;
        double yoff;
        Object dataobj;
        int p;
        int max, min;
        short da;
        double y_zero = ((double) graphAreaY) / 2.0;
        double y_last = y_zero;
        double halfxskip = xskip / 2;
        double yscaler = (double) ((double) (graphAreaY / 2.0) / dataScale);

        // for y labels : for shorts
        double oneLabelStart = 30000.0 * yscaler;
        double oneLabelInc = 10000.0 * yscaler;
        int labs = 7;

        if (channels > 1) { // don't show 30,000 for stereo
            labs = 5;
            oneLabelStart = 20000.0 * yscaler;
        }

        int pos;
        for (int chan = 0; chan < channels; ++chan) { // for each channel
            // draw Y axis labels ********************* for shorts for now...
            g2.setColor(axisText);
            double start = ystart + y_zero + oneLabelStart;
            int text = -30000;
            if (channels > 1) {
                text = -20000;
            }
            for (int lab = 0; lab < labs; ++lab) {
                g2.drawLine(w - XLAB, (int) start, w - XLAB + 2, (int) start);
                g2.drawString(String.valueOf(text), w - XLAB + 5, (int) start + 4);
                start -= oneLabelInc;
                text += 10000;
            }

            g2.setColor(foreground);

            double x = 0.0;
            dataobj = audioData.get(chan);
            if (dataobj instanceof short[]) {
                short[] data = (short[]) dataobj;
                x_new = -xmove;
                yoff = ystart + y_zero;
                y_last = yoff;
                for (double i = 0.0; i < dataLength; i += xskip) {
                    da = data[(int) i];
                    y_new = yoff - (da * yscaler);
                    x_new += xmove;
                    min = da;
                    max = da;
                    for (p = (int) (i - halfxskip); p >= 0 && p < dataLength && p < (int) (i + halfxskip); ++p) {
                        max = Math.max(max, data[p]);
                        min = Math.min(min, data[p]);
                    }
                    if (xskip > 1) {
                        g2.drawLine((int) x, (int) (yoff - (min * yscaler)),
                                (int) x, (int) (yoff - (max * yscaler)));
                    } else {
                        g2.drawLine((int) x, (int) y_last, (int) x_new, (int) y_new);
                    }

                    y_last = y_new;
                    x = x_new;
                }
            } // end of short[]
            ystart += graphAreaY;
        } // end for each channel

        // .. draw current position ..
        if (seconds != 0) {
            double loc = seconds / duration * w;
            g2.setColor(pink);
            g2.setStroke(new BasicStroke(3));
            g2.draw(new Line2D.Double(loc, 0, loc, h - 2));
        }
        painting = false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension d = getSize();

        if ((d.height < 0) || (d.width < 0)) {
            return;
        }

        if (offGraphics == null) {
            offDimension = d;
            offImage = null;

            int to = 0;

            while (offImage == null) {
                try {
                    System.out.println("Creating buffer for graphics");
                    offImage = createImage(d.width, d.height);
                    System.runFinalization();
                    System.gc();
                }
                catch (OutOfMemoryError e) {
                    System.runFinalization();
                    System.gc();
                    ++to;
                    if (to > 10) {
                        ErrorDialog.show("Out Of Memory ! WaveView unable to Allocate Double Buffer");
                        setSize((int) (d.width / zoomFactor), (int) (d.height / zoomFactor));
                        return;
                    }
                }
            }

            offGraphics = offImage.getGraphics();
            paintGraph(offGraphics);
        } else if ((d.width != offDimension.width)
                || (d.height != offDimension.height)) {
            offDimension = d;
            offGraphics.dispose();
            offImage.flush();
            offImage = null;
            offGraphics = null;
            System.runFinalization();
            System.gc();

            int to = 0;
            while (offImage == null) {
                try {
                    System.out.println("Change in size, recreated buffer");
                    offImage = createImage(d.width, d.height);
                }
                catch (OutOfMemoryError e) {
                    offGraphics = null;
                    offImage = null;
                    System.runFinalization();
                    System.gc();
                    System.out.println("Out OF MEMORY ERROR!!!!, Trying again");
                    ++to;
                    if (to > 10) {
                        ErrorDialog.show("Out Of Memory ! WaveView unable to Zoom Further");
                        setSize((int) (d.width / zoomFactor), (int) (d.height / zoomFactor));
                        return;
                    }
                }
            }

            offGraphics = offImage.getGraphics();

            paintGraph(offGraphics);
        } else if (painting) {
            System.out.println("Forced Painting");
            paintGraph(offGraphics);
        }

        if ((g != null) && (offImage != null)) {
            g.drawImage(offImage, 0, 0, this);
        }
    }

    /**
     * Zooms in the image by making the canvas larger
     */
    public void zoomIn(int width, int height) {
        Dimension d = getSize();
        int w = d.width;
        setSize((int) ((double) w * zoomFactor), height);
        drawGraph();
    }

    /**
     * Zooms out the image by making the canvas smaller
     */
    public void zoomOut(int width, int height) {
        Dimension d = getSize();
        int w = d.width;
        int newX = (int) ((double) w * (1 / zoomFactor));
        if (newX < width) {
            newX = width;
        }
        setSize(newX, height);
        drawGraph();
    }

    /**
     * Zooms out fully
     */
    public void fullSize(int width, int height) {
        setSize(width, height);
        drawGraph();
    }

    public void detail() {
        efficientDraw = !efficientDraw;
        drawGraph();
    }

    public boolean getDetail() {
        return efficientDraw;
    }
}
