package org.trianacode.enactment.logging.appender;

import gov.lbl.netlogger.LogMessage;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.enactment.logging.rabbit.RabbitHandler;
import org.trianacode.enactment.logging.stampede.LogDetail;
import org.trianacode.enactment.logging.stampede.StampedeEvent;
import org.trianacode.error.ErrorEvent;
import org.trianacode.error.ErrorTracker;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/08/2011
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
public class RabbitAppender extends AppenderSkeleton {

    public int PORT = 0000;
    public String HOST = "err";
    public String USERNAME = "name";
    public String PASSWORD = "password";
    public String QUEUENAME = "queuename";

    public RabbitAppender() {
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int port) {
        this.PORT = port;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String host) {
        this.HOST = host;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String username) {
        this.USERNAME = username;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String password) {
        this.PASSWORD = password;
    }

    public String getQUEUENAME() {
        return QUEUENAME;
    }

    public void setQUEUENAME(String queuename) {
        this.QUEUENAME = queuename;
    }

    private void ensureReady() {
        if (!RabbitHandler.getRabbitHandler().isReady()) {
            try {
                RabbitHandler.getRabbitHandler().initConnection(HOST, PORT, USERNAME, PASSWORD, QUEUENAME);
            } catch (IOException e) {
                Loggers.CONFIG_LOGGER.error("Rabbit init error");
                ErrorTracker.getErrorTracker().broadcastError(new ErrorEvent(null, e, "Rabbit init error"));
            }
        }
    }

    private LogMessage buildMessage(LoggingEvent loggingEvent) {
        LogMessage logMessage;
        Object eventMessage = loggingEvent.getMessage();
        if (eventMessage instanceof StampedeEvent) {
            StampedeEvent stampedeLog = (StampedeEvent) eventMessage;
            logMessage = new LogMessage(stampedeLog.getEventName());
            for (LogDetail logDetail : stampedeLog.getLogDetails()) {
                logMessage.add(logDetail.getName(), logDetail.getDetail());
            }
        } else {
            logMessage = new LogMessage("RABBIT_MSG").add("event", loggingEvent.getMessage().toString());
        }
        return logMessage;
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        String logString = buildMessage(loggingEvent).toString();

        ensureReady();

        try {
            RabbitHandler.getRabbitHandler().sendLog(logString);
        } catch (IOException e) {
            System.out.println("Remove appender");
        }

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
    }
}
