package org.trianacode.shiwa.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.shiwa.desktop.data.description.core.WorkflowImplementation;
import org.shiwa.desktop.data.description.workflow.SHIWAProperty;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.service.Scheduler;
import org.trianacode.taskgraph.service.SchedulerInterface;
import org.trianacode.taskgraph.service.TrianaServer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 05/05/2012
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
 */
public class BrokerUtils {

    public static String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SS_z");
        return dateFormat.format(new Date());
    }

    public static File getResultBundle(String url, String key) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + "?action=file&key=" + key);
            System.out.println("Getting JSON from " + httpGet.getURI());

            HttpResponse response = client.execute(httpGet);
            InputStream input = response.getEntity().getContent();

            File bundle = File.createTempFile("received", ".zip");
            OutputStream out = new FileOutputStream(bundle);
            byte buf[] = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            input.close();

            System.out.println("Got bundle at : " + bundle.getAbsolutePath());

            client.getConnectionManager().shutdown();
            return bundle;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String waitForExec(String url, int i, String uuid, String execBundleName) {
        while (i > 0) {
            String key = getJSONReply(url, uuid, execBundleName);
            if (key != null) {
                return key;
            } else {
                try {
                    System.out.println("No key, sleeping. " + i + " remaining");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i -= 1000;
            }
        }
        return null;
    }


    public static String getJSONReply(String url, String uuid, String execBundleName) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + "?action=json");
//            HttpGet httpGet = new HttpGet(url + "?action=byid&&uuid=" + uuid);

            HttpResponse response = client.execute(httpGet);
            System.out.println(response.getEntity().getContentLength() + " from json");
            InputStream input = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                System.out.printf(line);
                stringBuilder.append(line);
            }
            client.getConnectionManager().shutdown();

            JSONTokener jsonTokener = new JSONTokener(stringBuilder.toString());
            JSONArray jsonArray = new JSONArray(jsonTokener);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");

                if (name.equals(execBundleName)) {
                    String key = jsonObject.getString("key");
                    System.out.println("Name = " + execBundleName + " key = " + key);
                    return key;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String postBundle(
            String hostAddress, String routingKey, String execBundleName, File tempBundleFile) {

        String line = null;
        try {
            FileBody fileBody = new FileBody(tempBundleFile);
            StringBody routing = new StringBody(routingKey);
            StringBody numtasks = new StringBody("1");

            StringBody name = new StringBody(execBundleName);

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

    public static void addParentDetailsToSubWorkflow(
            WorkflowImplementation impl, UUID runUUID, UUID parentID, String jobID, String jobInstID) {

        impl.addProperty(new SHIWAProperty(StampedeLog.PARENT_UUID_STRING,
                parentID.toString()));

        impl.addProperty(new SHIWAProperty(StampedeLog.RUN_UUID_STRING,
                runUUID.toString()));

        impl.addProperty(new SHIWAProperty(StampedeLog.JOB_ID, jobID));

        impl.addProperty(new SHIWAProperty(StampedeLog.JOB_INST_ID, jobInstID));
    }

    public static Scheduler getSchedulerForTaskGraph(TaskGraph taskGraph) {
        TrianaServer server = TaskGraphManager.getTrianaServer(taskGraph);
        SchedulerInterface sched = server.getSchedulerInterface();
        if (sched instanceof Scheduler) {
            return (Scheduler) sched;
        }
        return null;
    }

    public static void prepareSubworkflow(Task task, UUID runUUID, WorkflowImplementation workflowImplementation) {
        Scheduler scheduler = getSchedulerForTaskGraph(task.getParent());

        addParentDetailsToSubWorkflow(
                workflowImplementation,
                runUUID,
                scheduler.stampedeLog.getRunUUID(),
                "unit:" + task.getQualifiedToolName(),
                scheduler.stampedeLog.getTaskNumber(task).toString()
        );
    }
}
