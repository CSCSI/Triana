package org.trianacode.pool.broker;

import fr.insalyon.creatis.shiwapool.agent.engines.EnginePluginImpl;
import fr.insalyon.creatis.shiwapool.agent.engines.StatusHelper;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.WorkflowInstance;
import org.trianacode.pool.ShiwaBundleHelper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 31/03/2012
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
@PluginImplementation
public class PoolToBroker extends EnginePluginImpl {

    public static String languageName = "";
    private Properties properties;
    private static final String HOST_ADDRESS = "host.address";
    private static final String ROUTING_KEY = "routing.key";

    public PoolToBroker() {
        super(languageName);

        properties = new Properties();

        File propertiesFile = new File("pool.to.broker.properties");
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileReader(propertiesFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void submit(int instanceID, SHIWABundle shiwaBundle, String s) {
        System.out.println("Running " + instanceID);
        StatusHelper.setStatus(instanceID, WorkflowInstance.Status.RUNNING);


        File tempReturnFile = null;
        try {
            ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(shiwaBundle);
            File temp = File.createTempFile("bundle", "tmp");
            shiwaBundleHelper.saveBundle(temp);

            tempReturnFile = File.createTempFile("return", "tmp");


            String hostAddress = String.valueOf(properties.get(HOST_ADDRESS));
            String routingKey = String.valueOf(properties.get(ROUTING_KEY));
            String name = getInstanceName(shiwaBundleHelper);
            postBundle(hostAddress, routingKey, name, temp);


        } catch (IOException e) {
            e.printStackTrace();
        }


        if (tempReturnFile != null && tempReturnFile.exists()) {
            StatusHelper.setStatus(instanceID, WorkflowInstance.Status.FINISHED);
        } else {
            StatusHelper.setStatus(instanceID, WorkflowInstance.Status.FAILED);
        }
    }

    private String getInstanceName(ShiwaBundleHelper shiwaBundleHelper) {
        return shiwaBundleHelper.getWorkflowImplementation().getTitle()
                + "-" + getTimeStamp();
    }

    private String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SS_z");
        return dateFormat.format(new Date());
    }

    private String postBundle(String hostAddress, String routingKey, String bundleName, File tempBundleFile) {

        String line = null;
        try {
            FileBody fileBody = new FileBody(tempBundleFile);
            StringBody routing = new StringBody(routingKey);
            StringBody numtasks = new StringBody("1");

            StringBody name = new StringBody(bundleName);

            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("file", fileBody);
            multipartEntity.addPart("routingkey", routing);
            multipartEntity.addPart("numtasks", numtasks);
            multipartEntity.addPart("name", name);

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(hostAddress);
            httpPost.setEntity(multipartEntity);
            System.out.println("Sending " + httpPost.getEntity().getContentLength()
                    + " bytes to " + hostAddress);
            HttpResponse response = client.execute(httpPost);

            InputStream input = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            line = null;
            while ((line = br.readLine()) != null) {
                System.out.printf("\n%s", line);
            }
            client.getConnectionManager().shutdown();
        } catch (Exception ignored) {
        }
        return line;
    }
}
