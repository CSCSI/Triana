package org.trianacode.enactment.logging;

import org.trianacode.config.Locations;
import org.trianacode.config.TrianaProperties;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 18/10/2011
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public class LoggingUtils {

    public static String getDefaultLocation() {
        String loggingLocation = Locations.getHomeProper() + File.separator + "logging";
        File loggingFolder = LoggingUtils.createFolders(loggingLocation);
        return loggingFolder.getAbsolutePath();
    }

    public static File createFolders(String locationString) {
        File loggingFolder = new File(locationString);
        if (!loggingFolder.exists()) {
            loggingFolder.mkdirs();
        }
        return loggingFolder;
    }

    public static void setLogInputValues(TrianaProperties trianaProperties, boolean selected) {
        trianaProperties.put(TrianaProperties.LOGGING_INPUT_VALUES, String.valueOf(selected));
        try {
            trianaProperties.saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean loggingInputs(TrianaProperties trianaProperties) {
        String shouldLogInputs = (String) trianaProperties.get(TrianaProperties.LOGGING_INPUT_VALUES);
        if (shouldLogInputs == null) {
            setLogInputValues(trianaProperties, false);
            return false;
        } else {
            return (new Boolean(shouldLogInputs)).booleanValue();
        }
    }

    public static void setLogToRabbitMQ(TrianaProperties trianaProperties, boolean selected) {
        trianaProperties.put(TrianaProperties.LOG_TO_RABBITMQ, String.valueOf(selected));
        try {
            trianaProperties.saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean loggingToRabbitMQ(TrianaProperties trianaProperties) {
        String shouldLogToRabitMQ = (String) trianaProperties.get(TrianaProperties.LOG_TO_RABBITMQ);
        if (shouldLogToRabitMQ == null) {
            setLogToRabbitMQ(trianaProperties, false);
            return false;
        } else {
            return (new Boolean(shouldLogToRabitMQ)).booleanValue();
        }
    }
}
