package org.trianacode.enactment.logging.rabbit;

import com.rabbitmq.client.*;
import org.trianacode.enactment.logging.Loggers;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 18/08/2011
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class RabbitHandler extends SwingWorker {

    private Status status = Status.NOT_READY;
    private static Channel channel = null;
    private static Connection connection = null;

    ArrayBlockingQueue<String> sendingQueue = new ArrayBlockingQueue<String>(1000);
    private static RabbitHandler rabbitHandler = new RabbitHandler();

    //    private String queueName = null;
    private String exchangeName = null;
    private boolean end = false;
    private String host;
    private int port;
    private String username;
    private String password;
    private long connectionWait = 250;
    private CreateConnection connectRunnable;

    public static RabbitHandler getRabbitHandler() {
        return rabbitHandler;
    }

    private RabbitHandler() {
        status = Status.NOT_READY;
        this.execute();
    }

    public enum Status {
        READY,
        INIT,
        NOT_READY,
        CLOSED,
        INFO_SET
    }

    public Status getStatus() {
        return status;
    }

    @Override
    protected Object doInBackground() throws Exception {
        while (!end) {
            if (dataToSend() && isReady()) {
                try {
                    sendString(sendingQueue.take());
//                    System.out.println("-" + sendingQueue.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
        return null;
    }

    private void sendString(String string) throws IOException {
//        channel.basicPublish("", queueName, null, string.getBytes());
//        System.out.println(string);
        channel.basicPublish(exchangeName, "", null, string.getBytes());
    }

    private boolean dataToSend() {
        return sendingQueue.size() > 0;
    }

    public void setConnectionInfo(String host, int port, String username, String password, String exchangeName) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.exchangeName = exchangeName;
        if (connectRunnable != null) {
            connectRunnable.kill();
        }
        status = Status.INFO_SET;
    }

    public void initConnection() {
//        System.out.println(host + " "
//                + port + " "
//                + username + " "
//                + password + " "
//                + queueName
//        );

        connectRunnable = new CreateConnection();
        Thread thread = new Thread(connectRunnable);
        status = Status.INIT;
        thread.start();
    }

    private void closeAll() throws IOException {
        System.out.println("Connection lost, closing");
        end = true;
        channel.close();
        connection.close();
        channel = null;
        connection = null;
        status = Status.CLOSED;
    }

    public void sendLog(String string) throws IOException, InterruptedException {
        sendingQueue.put(string);
//        System.out.println("+" + sendingQueue.size());
    }

    public boolean isReady() {
        return !(channel == null || !channel.isOpen());//queueName == null);
    }

    class CreateConnection implements Runnable {

        public boolean go = true;

        public void kill() {
            go = false;
        }

        @Override
        public void run() {
            while (status == Status.INIT && go) {
                try {
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost(host);
                    if (username != null && password != null) {
                        factory.setUsername(username);
                        factory.setPassword(password);
                    }
                    factory.setPort(port);
                    connection = factory.newConnection();
                    channel = connection.createChannel();
                    channel.addShutdownListener(new ShutdownListener() {
                        @Override
                        public void shutdownCompleted(ShutdownSignalException e) {
                            e.printStackTrace();
                            System.out.println(e.getReason().toString());
                            try {
                                Loggers.DEV_LOGGER.debug("Channel shutdown, tidying up");
                                closeAll();
                            } catch (IOException e1) {
                                Loggers.DEV_LOGGER.debug("Error shutting down Rabbit channel/connection");
                            }
                        }
                    });
//                    channel.queueDeclare("", false, false, false, null);
                    System.out.println("Connection made " + channel.toString());
                    status = Status.READY;
                } catch (Exception e) {
                    System.out.println("Connection fail");
                    try {
                        Thread.sleep(connectionWait);
                        connectionWait = connectionWait * 2;
                        connectionWait = (connectionWait < 60000 ? connectionWait : 60000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            connectionWait = 10;
        }
    }
}
