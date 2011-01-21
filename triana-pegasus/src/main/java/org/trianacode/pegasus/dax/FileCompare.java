package org.trianacode.pegasus.dax;

import org.trianacode.pegasus.bonjour.PegasusWorkflowData;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 20, 2011
 * Time: 7:25:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileCompare {
    public FileCompare(String s1, String s2) throws IOException {
        File f1 = new File(s1);
        File f2 = new File(s2);

         ObjectInputStream fois1 = new ObjectInputStream(new FileInputStream(f1));
            try {
                PegasusWorkflowData wf1 = (PegasusWorkflowData)fois1.readObject();
                System.out.println("Dax : " + wf1.getDax());
            } catch (ClassNotFoundException e) {
                System.out.println("PWD object not recognised after reading back in");
            }


         ObjectInputStream fois2 = new ObjectInputStream(new FileInputStream(f2));
            try {
                PegasusWorkflowData wf2 = (PegasusWorkflowData)fois2.readObject();
                System.out.println("Dax : " + wf2.getDax());
            } catch (ClassNotFoundException e) {
                System.out.println("PWD object not recognised after reading back in");
            }
    }

    public static void main(String[] args) {

        try {
            new FileCompare("../JmDNS_Restless_Pegasus/files/remotecontrol-12.txt", "temp_wfd.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Files not found");
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }
}
