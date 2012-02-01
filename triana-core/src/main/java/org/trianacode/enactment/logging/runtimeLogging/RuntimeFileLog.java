package org.trianacode.enactment.logging.runtimeLogging;

import gov.lbl.netlogger.LogMessage;
import org.apache.commons.logging.Log;
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

    public String getLogFilePath() {
        return logFile.getAbsolutePath();
    }

    public RuntimeFileLog(String property, UUID runUUID) {
        File rootLogFolder = new File(property);
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
