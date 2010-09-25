package org.trianacode;

/**
 * Created by IntelliJ IDEA.
 * User: scmijt
 * Date: Sep 25, 2010
 * Time: 11:03:28 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TrianaInstanceProgressListener {

    /**
     * Sets the number of steps in the initialization to give the application
     * a queue of the progress of the initialization
     * 
     * @param stepsInInitialization
     */
    public void setProgressSteps(int stepsInInitialization);

    /**
     * Allows applications that intantiate a TrianaInstance to get feedback
     * upon the advancement of the initialization of the various services
     * during a TrianaInstance
     * 
     * @param progress String describing the current progress state
     */
    public void showCurrentProgress(String progress);

}
