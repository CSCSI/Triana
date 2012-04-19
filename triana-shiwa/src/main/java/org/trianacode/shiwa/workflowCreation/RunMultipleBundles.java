package org.trianacode.shiwa.workflowCreation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 19/04/2012
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class RunMultipleBundles {

    @TextFieldParameter
    public String address = "http://s-vmc.cs.cf.ac.uk:7025/Broker/broker";

    @TextFieldParameter
    private String routingKey = "*.triana";

    @org.trianacode.annotation.Process()
    public void process(List list) {

        System.out.println("Submitting " + list.size() + " bundles.");
        for (Object object : list) {
            if (object instanceof File) {

//                ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(new SHIWABundle((File) object));
                postBundle(address, (File) object);

            }
        }
    }

    private String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SS_z");
        return dateFormat.format(new Date());
    }

    private void postBundle(String hostAddress, File tempBundleFile) {

        try {
            FileBody fileBody = new FileBody(tempBundleFile);
            StringBody routing = new StringBody(routingKey);
            StringBody numtasks = new StringBody("1");
//            String execBundleName = shiwaBundleHelper.getWorkflowImplementation().getTitle()
//                    + "-" + getTimeStamp();
            StringBody name = new StringBody(tempBundleFile.getName() + "-" + getTimeStamp());

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

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.printf("\n%s", line);
            }
            client.getConnectionManager().shutdown();
        } catch (Exception ignored) {
        }
    }
}
