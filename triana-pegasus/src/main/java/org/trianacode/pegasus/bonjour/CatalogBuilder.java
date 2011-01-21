package org.trianacode.pegasus.bonjour;

import org.trianacode.pegasus.extras.FileBuilder;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 21, 2011
 * Time: 11:52:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class CatalogBuilder {

     public static void buildPropertiesFile(String topDir) {
        String propertiesFileContents = "pegasus.catalog.site=XML3\n" +
                "pegasus.catalog.site.file=" + topDir + File.separator + "sites.xml\n" +
                "\n" +
                "pegasus.dir.useTimestamp=true\n" +
                "pegasus.dir.storage.deep=false";

        new FileBuilder(topDir + File.separator + "properties", propertiesFileContents);

    }

    public static void buildSitesFile(String topDir) {
        String pegasusDir = System.getenv("PEGASUS_HOME");

        String sitesContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<sitecatalog xmlns=\"http://pegasus.isi.edu/schema/sitecatalog\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                " xsi:schemaLocation=\"http://pegasus.isi.edu/schema/sitecatalog http://pegasus.isi.edu/schema/sc-3.0.xsd\" version=\"3.0\">\n" +
                "    <site  handle=\"local\" arch=\"x86\" os=\"LINUX\">\n" +
                "        <grid  type=\"gt2\" contact=\"localhost/jobmanager-fork\" scheduler=\"Fork\" jobtype=\"auxillary\"/>\n" +
                "        <grid  type=\"gt2\" contact=\"localhost/jobmanager-fork\" scheduler=\"unknown\" jobtype=\"compute\"/>\n" +
                "        <head-fs>\n" +
                "            <scratch>\n" +
                "                <shared>\n" +
                "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"" + topDir +"/outputs\"/>\n" +
                "                    <internal-mount-point mount-point=\"" + topDir + "/work/outputs\" free-size=\"100G\" total-size=\"30G\"/>\n" +
                "                </shared>\n" +
                "            </scratch>\n" +
                "            <storage>\n" +
                "                <shared>\n" +
                "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"" + topDir + "/outputs\"/>\n" +
                "                    <internal-mount-point mount-point=\"" + topDir + "/work/outputs\" free-size=\"100G\" total-size=\"30G\"/>\n" +
                "                </shared>\n" +
                "            </storage>\n" +
                "        </head-fs>\n" +
                "        <replica-catalog  type=\"LRC\" url=\"rlsn://dummyValue.url.edu\" />\n" +
                "        <profile namespace=\"env\" key=\"PEGASUS_HOME\" >" + pegasusDir + "</profile>\n" +
                "    </site>\n" +
                "    <site  handle=\"condorpool\" arch=\"x86\" os=\"LINUX\">\n" +
                "        <grid  type=\"gt2\" contact=\"localhost/jobmanager-fork\" scheduler=\"Fork\" jobtype=\"auxillary\"/>\n" +
                "        <grid  type=\"gt2\" contact=\"localhost/jobmanager-fork\" scheduler=\"unknown\" jobtype=\"compute\"/>\n" +
                "        <head-fs>\n" +
                "            <scratch>\n" +
                "                <shared>\n" +
                "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"" + topDir + "/outputs\"/>\n" +
                "                    <internal-mount-point mount-point=\"" + topDir + "/work/outputs\" free-size=\"100G\" total-size=\"30G\"/>\n" +
                "                </shared>\n" +
                "            </scratch>\n" +
                "            <storage>\n" +
                "                <shared>\n" +
                "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"" + topDir + "/outputs\"/>\n" +
                "                    <internal-mount-point mount-point=\"" + topDir + "/work/outputs\" free-size=\"100G\" total-size=\"30G\"/>\n" +
                "                </shared>\n" +
                "            </storage>\n" +
                "        </head-fs>\n" +
                "        <replica-catalog  type=\"LRC\" url=\"rlsn://dummyValue.url.edu\" />\n" +
                "        <profile namespace=\"pegasus\" key=\"style\" >condor</profile>\n" +
                "        <profile namespace=\"condor\" key=\"universe\" >vanilla</profile>\n" +
                "        <profile namespace=\"env\" key=\"PEGASUS_HOME\" >" + pegasusDir + "</profile>\n" +
                "    </site>\n" +
                "</sitecatalog>";

        new FileBuilder(topDir + File.separator  + "sites.xml", sitesContent);
    }

}
