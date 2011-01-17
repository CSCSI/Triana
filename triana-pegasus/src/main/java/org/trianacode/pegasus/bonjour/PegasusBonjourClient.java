package org.trianacode.pegasus.bonjour;

import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Response;
import org.thinginitself.streamable.StreamableString;

import java.io.*;
import java.util.Properties;

/**
 * Command line tool that takes in the locations of the various input files, loads them in
 * as Strings or properties and creates a PegasusWorkflowData object for sending to the server.
 * It then posts that object to the "remotecontrol" URL on the server to invoke the remote
 * pegasus installation
 * 
 * User: scmijt
 * Date: Jul 29, 2010
 * Time: 10:34:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class PegasusBonjourClient {
    String host;

    private String dax, replicaCatalog, transformationCatalog, siteFile;
    Properties properties;

    static String commandLineArgs =
            "pegasus-bonjour \" + \"http://hostname:8080/remotecontrol\" + \" " +
                    "<propertyFile> <daxfile.xml> <rc.data> <tc.data> <siteFile.xml>" +
                    " \nwhere:\n" +

                    "\n    - Address/Port is the address and port number you need to type in to reach this service" +
                    "\n    - propertyFile is the Pegasus property file" +
                    "\n    - daxfile.xml is the DAX workflow graph" +
                    "\n    - rc.data is the replica catalog information (mapping from logical to physical filenames" +
                    "\n    - tc.data is the transformation catalog (mapping from the logical executable names to physical locations" +
                    "\n    - siteFile.xml is contains information about the layout of your grid where you want " +
                    "to run your workflows. For each site information like workdirectory, jobmanagers to use, gridftp servers to " +
                    "use and other site wide information like environment variables to be set is maintained ";

    private static void showUsageInstructionsAndQuit() {
        System.out.println("USAGE:");
        System.out.println(commandLineArgs);
    }

    public static String getContents(String filen) throws IOException {
        File file = new File(filen);
        FileInputStream filein = new FileInputStream (file);
        DataInputStream in = new DataInputStream(filein);
        byte[] b = new byte[in.available ()];
        in.readFully (b);
        in.close ();
        System.out.println("Adding contents for file: " + file.getName());
        return new String(b);
    }

    public void loadProperties(String propertyLoc) throws Exception {
        FileInputStream is = new FileInputStream(propertyLoc);
        properties=new Properties();
        properties.load(is);
    }

    public String loadDAX(String daxLoc) throws Exception {
        return getContents(daxLoc);
    }

    public String loadReplica(String rcloc) throws Exception {
        return getContents(rcloc);
    }

    public String loadTransform(String trloc) throws Exception {
        return getContents(trloc);
    }

    public String loadSite(String siteLoc) throws Exception {
        return getContents(siteLoc); 
    }

    public Response parse(String[] commandline) {
        if (commandline.length<6) PegasusBonjourClient.showUsageInstructionsAndQuit();


        String httpAddress = commandline[0];
        System.out.println("Service Address is : " +  httpAddress);
        String propertyfile = commandline[1];
        System.out.println("Properties file is : " +  propertyfile);
        String daxfile = commandline[2];
        System.out.println("Properties file is : " +  daxfile);
        String replicafile = commandline[3];
        System.out.println("Properties file is : " +  replicafile);
        String transformfile = commandline[4];
        System.out.println("Properties file is : " +  transformfile);
        String sitefile = commandline[5];
        System.out.println("Properties file is : " +  sitefile);

        try {
            loadProperties(propertyfile);
            this.dax=loadDAX(daxfile);
            this.replicaCatalog=loadReplica(replicafile);
            this.transformationCatalog = loadTransform(transformfile);
            this.siteFile=loadSite(sitefile);
        } catch (Exception e) {
            e.printStackTrace();
            PegasusBonjourClient.showUsageInstructionsAndQuit();
        }

        PegasusWorkflowData wf = new PegasusWorkflowData(dax,replicaCatalog,transformationCatalog,siteFile,properties);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream ou = new ObjectOutputStream(out);
            ou.writeObject(wf);
            ou.flush();

            RequestContext c = new RequestContext(httpAddress);
//            c.setResource(new Resource(new StreamableString(out.toString())));
            c.setResource(new Resource(new StreamableString( new String (out.toByteArray() , "UTF-8" ))));

            out.close();

            HttpPeer peer = new HttpPeer();
            Response ret = peer.post(c);
 //               System.out.println("Tried to send :" + out.toString() + " to : *" + httpAddress + "*");            
            System.out.println("Received reply :" + ret.toString());
            return ret;
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        return null;
    }
    
    public static void main(String [] args) {
        new PegasusBonjourClient().parse(args);
    }
}
