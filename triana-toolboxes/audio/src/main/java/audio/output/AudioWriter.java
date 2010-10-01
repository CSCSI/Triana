package audio.output;

import java.io.IOException;
import java.util.Vector;

import org.tritonus.share.sampled.file.AudioOutputStream;

/**
 * A class to support the threaded writing/saving of chunked audio. A thread is used in order to boost the CPU
 * efficiency, while the Write class converts the audio from a short to a byte array in a seperate thread. Contact
 * e.alshakarchi@cs.cf.ac.uk
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2921 $
 * @see WriteAiff_old
 */

public class AudioWriter extends Thread {

    public boolean stop;
    private boolean finished = true;
    private Vector bytes = new Vector();
    byte[] byteArray;
    AudioOutputStream audioOutputStream;

    public AudioWriter(AudioOutputStream dataOutputStream) {
        audioOutputStream = dataOutputStream;
    }

    public boolean isReady() {
        return finished;
    }

    public void addChunk(byte[] byteArray) {
        bytes.add(byteArray);
    }

    public void startWriter() {
        start();
    }

    public void stopWriter() {
        stop = true;
    }

    public void run() {
        int i = 0;
        stop = false;
        while (true) {
            if (bytes.size() > 0) {
                byteArray = (byte[]) bytes.remove(0);
                //System.out.println("i = " + i);
                i++;
                try {
                    audioOutputStream.write(byteArray, 0, byteArray.length);
                }
                catch (IOException e) {
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
