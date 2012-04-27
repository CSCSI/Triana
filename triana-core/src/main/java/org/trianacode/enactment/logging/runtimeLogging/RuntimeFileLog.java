package org.trianacode.enactment.logging.runtimeLogging;

import gov.lbl.netlogger.LogMessage;
import org.apache.commons.logging.Log;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.stampede.StampedeEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 19/10/2011
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public class RuntimeFileLog implements Log {
    private File logFile;
    private TrianaProperties properties;

    public String getLogFilePath() {
        return logFile.getAbsolutePath();
    }

    public RuntimeFileLog(String log_location, UUID runUUID) {
//        this.properties = properties;
//        String log_location = properties.getProperty(TrianaProperties.LOG_LOCATION);

        File rootLogFolder = new File(log_location);
        if (!rootLogFolder.exists()) {
            rootLogFolder.mkdirs();
        }
        File thisLogFolder = new File(rootLogFolder,
                "Run_" + new Timestamp(
                        new Date().getTime()).toString()
                        .replace(":", "-")
                        .replace(".", "-")
                        .replace(" ", "_")
        );
        thisLogFolder.mkdirs();
        logFile = new File(thisLogFolder, runUUID + ".log");
        makeBrainDump(thisLogFolder, runUUID);
    }

    private void makeBrainDump(File thisLogFolder, UUID runUUID) {
        String dumpContents = "user stackops\n" +
                "grid_dn null\n" +
                "submit_hostname pegasussubmit1.novalocal\n" +
                "root_wf_uuid " + runUUID.toString() + "\n" +
                "wf_uuid " + runUUID.toString() + "\n" +
                "dax /home/stackops/examples/condor-blackdiamond-condorio/blackdiamond.dax\n" +
                "dax_label blackdiamond\n" +
                "dax_index 0\n" +
                "dax_version 3.3\n" +
                "pegasus_wf_name blackdiamond-0\n" +
                "timestamp 20120314T140946+0100\n" +
                "basedir /home/stackops/examples/condor-blackdiamond-condorio/work\n" +
                "submit_dir /Users/ian/Work/triana4/logging\n" +
                "properties properties\n" +
                "planner /usr/bin/pegasus-plan\n" +
                "planner_version 4.0.0\n" +
                "pegasus_build 20120301004113Z\n" +
                "planner_arguments \"--conf pegasusrc --sites condorpool --dir work --output local --dax blackdiamond.dax --submit \"\n" +
                "jsd jobstate.log\n" +
                "rundir 20120314T140946+0100\n" +
                "bindir /usr/bin\n" +
                "vogroup pegasus\n" +
                "condor_log blackdiamond-0.log\n" +
                "notify blackdiamond-0.notify\n" +
                "dag blackdiamond-0.dag\n" +
                "type dag";
        writeFile(new File(thisLogFolder, "braindump.txt"), dumpContents);
    }

    private void writeLogMessage(Object obj) {

        String string;
        if (obj instanceof StampedeEvent) {
            string = ((StampedeEvent) obj).toString();
        } else {
            string = "Malformed StampedeEvent " + obj.toString();
        }
        LogMessage logMessage = new LogMessage(string);
        string = logMessage.toString();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));
            out.write(string + "\n");
            out.close();
        } catch (Exception e) {
        }

    }

    private void writeFile(File file, String contents) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(contents + "\n");
            out.close();
        } catch (Exception e) {
        }
    }


    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isFatalEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void trace(Object o) {

    }

    @Override
    public void trace(Object o, Throwable throwable) {
    }

    @Override
    public void debug(Object o) {
    }

    @Override
    public void debug(Object o, Throwable throwable) {
    }

    @Override
    public void info(Object o) {
    }

    @Override
    public void info(Object o, Throwable throwable) {
    }

    @Override
    public void warn(Object o) {
    }

    @Override
    public void warn(Object o, Throwable throwable) {
    }

    @Override
    public void error(Object o) {
    }

    @Override
    public void error(Object o, Throwable throwable) {
    }

    @Override
    public void fatal(Object o) {
    }

    @Override
    public void fatal(Object o, Throwable throwable) {
    }

    public void trace(StampedeEvent o) {
        writeLogMessage(o);
    }

    public void debug(StampedeEvent o) {
        writeLogMessage(o);
    }

    public void info(StampedeEvent o) {
        writeLogMessage(o);
    }

    public void warn(StampedeEvent o) {
        writeLogMessage(o);
    }

    public void error(StampedeEvent o) {
        writeLogMessage(o);
    }

    public void fatal(StampedeEvent o) {
        writeLogMessage(o);
    }
}
