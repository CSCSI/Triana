package audio.output;

import java.util.Vector;

import javax.sound.sampled.SourceDataLine;

/**
 * A class to support the threaded playback of chunked audio. A thread is used in order to boost the CPU efficiency,
 * while the Play class converts the audio from a short to a byte array in a separate thread. Contact
 * e.alshakarchi@cs.cf.ac.uk
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see Play
 */

public class AudioPlayer extends Thread {

    public boolean stop;
    private boolean finished = true;
    private Vector bytes = new Vector();
    private final SourceDataLine outputChannel;
    byte[] bytedata;
    byte[] byteArray;
    int chan;
    int samples = 0;
    int pos;

    public AudioPlayer(SourceDataLine outputChannel) {
        this.outputChannel = outputChannel;
    }

    public boolean isReady() {
        return finished;
    }

    public void addChunk(byte[] byteArray) {
        bytes.add(byteArray);
    }

    public void startPlayer() {
        start();
    }

    public void stopPlayer() {
        stop = true;
    }

    public void run() {
        stop = false;

        //System.out.println("bytes.size() = " + bytes.size());
        while (true) {
            if (bytes.size() > 0) {
                byteArray = (byte[]) bytes.remove(0);
                try {
                    outputChannel.write(byteArray, 0, byteArray.length);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            yield();
            if (stop) {
                break;
            }
            byteArray = null;
        }// End of while
    }
}
