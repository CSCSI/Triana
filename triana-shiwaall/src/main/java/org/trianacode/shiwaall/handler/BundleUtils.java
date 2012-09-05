package org.trianacode.shiwaall.handler;

import org.trianacode.gui.action.files.ImageAction;
import org.trianacode.gui.desktop.DesktopView;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.TaskGraph;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 20/08/2012
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class BundleUtils {

    /**
     * Gets the display stream.
     *
     * @param taskGraph the task graph
     * @return the display stream
     */
    public static InputStream getDisplayStream(TaskGraph taskGraph){
        DesktopView view = GUIEnv.getApplicationFrame().getDesktopViewFor(taskGraph);
        GUIEnv.getApplicationFrame().getDesktopViewManager().setSelected(view, true);

        InputStream displayStream = null;
        try {
            File imageFile = File.createTempFile("image", ".jpg");
            ImageAction.save(imageFile, 1, "jpg");
            if (imageFile.length() > 0) {
                displayStream = new FileInputStream(imageFile);
                System.out.println("Display image created : " + imageFile.toURI());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return displayStream;
    }

    /**
     * Read file.
     *
     * @param path the path
     * @return the string
     */
    public static String readFile(String path) {
        try{
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(new File(path));
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
                return Charset.defaultCharset().decode(bb).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
    }
}
