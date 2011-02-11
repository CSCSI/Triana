package org.trianacode.gui.util.organize;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 20, 2010
 */
public class OrganizeThread implements Runnable {


    protected DagOrganizer process;
    protected boolean stop;

    /**
     * how long the relaxer thread pauses between iteration loops.
     */
    protected long sleepTime = 50L;


    public OrganizeThread(DagOrganizer process) {
        this(process, 10L);
    }

    public OrganizeThread(DagOrganizer process, long sleepTime) {
        this.process = process;
        this.sleepTime = sleepTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public synchronized void stop() {
        stop = true;
    }

    public void run() {
        while (!process.done() && !stop) {

            process.step();

            if (stop)
                return;

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ie) {
            }
        }
    }
}
