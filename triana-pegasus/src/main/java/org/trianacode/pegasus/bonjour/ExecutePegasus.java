package org.trianacode.pegasus.bonjour;

import edu.isi.pegasus.common.logging.LogManager;
import edu.isi.pegasus.common.util.FactoryException;
import edu.isi.pegasus.planner.client.CPlanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;


/**

 Executes pegasus by taking in the various config files, writing out the flat files and running
 pegasus using those.

 $ pegasus-plan -Dpegasus.user.properties=`pwd`/config/properties \
               --dax `pwd`/dax/diamond.dax --force\
               --dir dags -s local -o local --nocleanup

 In Properties, we have:

pegasus.catalog.replica.file = ${user.home}/pegasus-wms/config/rc.data
pegasus.catalog.site.file = ${user.home}/pegasus-wms/config/sites.xml
pegasus.catalog.transformation.file = ${user.home}/pegasus-wms/config/tc.data

 Need 5 things:

 DAX, replica, transform, site and properties;

 * User: scmijt
 * Date: Jul 28, 2010
 * Time: 5:25:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutePegasus {
    CPlanner planner;

    static String userHome;
    static String cacheDirectory;
    static String propertyFile;
    static String propertyDir;
    static String dax;
    static String outputDir;
    static String replicaFile;
    static String transformFile;
    static String siteFile;

    static {
        userHome = System.getProperty("user.home");
        cacheDirectory= userHome + File.separator + "pegasus-wms";
        File tmpDir = new File (cacheDirectory);
        if (!tmpDir.exists()) tmpDir.mkdir();
        cacheDirectory += File.separator + "config";
        tmpDir = new File (cacheDirectory);
        if (!tmpDir.exists()) tmpDir.mkdir();

        propertyDir = cacheDirectory;
        propertyFile = propertyDir + File.separator + "properties";
        System.setProperty("pegasus.user.properties", propertyFile);

        dax = cacheDirectory + File.separator + "tmp.dax";
        outputDir=cacheDirectory + File.separator + "dags";

        String pegasusHome = System.getenv("PEGASUS_HOME");

        System.setProperty("pegasus.home", pegasusHome);
    }

    PegasusWorkflowData workflowData;

    public ExecutePegasus(PegasusWorkflowData workflowData) {
        this.workflowData = workflowData;
    }

    public String runIt() {

        Properties properties = workflowData.getProperties();

        replicaFile = properties.getProperty("pegasus.catalog.replica.file");
        transformFile = properties.getProperty("pegasus.catalog.transformation.file");
        siteFile = properties.getProperty("pegasus.catalog.site.file");

        String userHome = System.getProperty("user.home");
        replicaFile = replicaFile.replace("${user.home}", userHome);
        transformFile = transformFile.replace("${user.home}", userHome);
        siteFile = siteFile.replace("${user.home}", userHome);

        System.out.println("Replica file location should be " + replicaFile);
        System.out.println("Transform File file location should be " + transformFile);
        System.out.println("Site File file location should be " + siteFile);

        try {
            File file = new File(propertyDir);
            if (!file.exists()) file.mkdir();
            properties.store(new FileOutputStream(propertyFile), "Generated by Pegasus Bonjour!!!");

            new FileOutputStream(replicaFile).write(workflowData.getReplicaCatalog().getBytes());
            new FileOutputStream(transformFile).write(workflowData.getTransformationCatalog().getBytes());
            new FileOutputStream(siteFile).write(workflowData.getSiteFile().getBytes());
            new FileOutputStream(dax).write(workflowData.getDax().getBytes());

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        String[] args = {"-d",
                            dax,
                        "--force",
                        "--dir",
                        "dags",
                        "-s",
                        "local",
                        "--nocleanup"};

        executePegasus(args);

        return "Run Pegasus ok!!!";
    }

    /**
     * $ pegasus-plan -Dpegasus.user.properties=`pwd`/config/properties \
               --dax `pwd`/dax/diamond.dax --force\
               --dir dags -s local -o local --nocleanup
     * @param args
     */
    public void executePegasus(String[] args) {
               CPlanner cPlanner = new CPlanner();

        for (String arg: args) {
            System.out.print(arg + " ");
        }
        System.out.println("");

        int result = 0;
        double starttime = new Date().getTime();
        double execTime  = -1;


        try{
            cPlanner.executeCommand( args );
        }
        catch ( FactoryException fe){
            cPlanner.log( fe.convertException() , LogManager.FATAL_MESSAGE_LEVEL);
            result = 2;
        }
        catch ( RuntimeException rte ) {
            //catch all runtime exceptions including our own that
            //are thrown that may have chained causes
            result = 1;
        }
        catch ( Exception e ) {
            //unaccounted for exceptions
            cPlanner.log(e.getMessage(),
                         LogManager.FATAL_MESSAGE_LEVEL );
            e.printStackTrace();
            result = 3;
        } finally {
            double endtime = new Date().getTime();
            execTime = (endtime - starttime)/1000;
        }

        // warn about non zero exit code
        if ( result != 0 ) {
            cPlanner.log("Non-zero exit-code " + result,
                         LogManager.WARNING_MESSAGE_LEVEL );
        }
        else{
            //log the time taken to execute
            cPlanner.log("Time taken to execute is " + execTime + " seconds",
                         LogManager.INFO_MESSAGE_LEVEL);
        }

        System.exit(result);
    }


}