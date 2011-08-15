package org.trianacode.enactment.logging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import gov.lbl.netlogger.LogMessage;
import org.trianacode.config.Locations;
import org.trianacode.taskgraph.service.ExecutionEvent;
import org.trianacode.taskgraph.service.ExecutionListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 15/08/2011
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class StampedeLogger implements ExecutionListener {

    private static File logFileDir = new File(Locations.runHome(), "logs");
    private static File currentLogFile = null;
    private static Channel channel;
    private static String host = "colette.cs.cf.ac.uk";

    public StampedeLogger() {
        if (!logFileDir.exists()) {
            logFileDir.mkdirs();
        }
        if (currentLogFile == null) {
            currentLogFile = new File(logFileDir, "Run" + new Timestamp(new Date().getTime()).toString() + ".log");
        }
    }

    public static void log(ExecutionEvent event) {
        log(event, null, true);
    }

    public static void log(ExecutionEvent event, boolean writeFile) {
        log(event, null, writeFile);
    }

    public static void log(ExecutionEvent event, ArrayList<ErrorDetail> details, boolean store) {
        LogMessage logMessage = new LogMessage(event.getState().name());
        logMessage.add("Task", event.getTask().getQualifiedTaskName());

        if (details != null && details.size() > 0) {
            for (ErrorDetail errorDetail : details) {
                logMessage.add(errorDetail.name, errorDetail.detail);
            }
        }
        if (store) {
            logToRabbit(logMessage);
            writeFile(logMessage);
        }
        System.out.println(logMessage.toString());
    }

    private static void writeFile(LogMessage logMessage) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(currentLogFile, true));
            out.write(logMessage.toString() + "\n");
            out.close();
        } catch (IOException e) {
        }
    }

    private static void logToRabbit(LogMessage logMessage) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(7008);
        Connection connection = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("TestQueue", false, false, false, null);
            channel.basicPublish("", "TestQueue", null, logMessage.toString().getBytes());
            channel.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Rabbit error");
        }
    }

    @Override
    public void executionStateChanged(ExecutionEvent event) {
        log(event);
    }

    @Override
    public void executionRequested(ExecutionEvent event) {
        log(event);
    }

    @Override
    public void executionStarting(ExecutionEvent event) {
        log(event);
    }

    @Override
    public void executionFinished(ExecutionEvent event) {
        log(event);
    }

    @Override
    public void executionReset(ExecutionEvent event) {
        log(event);
    }
}
