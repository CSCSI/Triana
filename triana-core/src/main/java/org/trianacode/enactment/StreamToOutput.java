package org.trianacode.enactment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/02/2012
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class StreamToOutput implements Runnable {

    private InputStream inputStream;
    private String description;
    private Thread thread;
    private BufferedReader inreader;

    public StreamToOutput(InputStream inputStream, String description) {
        this.inputStream = inputStream;
        this.description = description;
        inreader = new BufferedReader(new InputStreamReader(this.inputStream));

    }

    public void start() {
        thread = new Thread(this);
        thread.run();
    }

    @Override
    public void run() {
//        System.out.println("Reading stream : " + description);
        try {
            String str;
            while ((str = inreader.readLine()) != null) {
                System.out.println(" >> " + description + " : " + str);
            }
        } catch (IOException e) {
            System.out.println("Error with stream " + description + " closing");
        } finally {
            try {
                inreader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Closed streamReader " + description);
        }
    }
}
