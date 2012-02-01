package org.trianacode.cloud;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.io.Payloads;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;

import java.io.File;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 11/01/2012
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class CloudTest {

    private ComputeServiceContext context;
    private int tabs = 0;

    private String publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEApgg+hrCqP3uaCJO2Vp+yMQwVvKmHWJ+79jiEVXQu0vdetVxsW1XuUhLAbx8ds28SLGpRJ7ocz/LxhwfH0hg2X3i7PSUWlibSyCFk3aK5Sk+IJmhxaP6nLjlzBb6typKhD8mW+kYovg+t/L+h/WYgz017p1OxgGNvUWd5+ZwjQKm3v8TC9yhPOMGSu7fF6505ivV9IokllD5UOz5tomTVvVUE2oinEGxpnLis1yCnH/Od0bfrMWyC57mi3MYA/89zOxE+ncljGZWRFRATl7J6WzEqKXau+hrNsypyFoFHhTC4lQQOjzRvZhPcx7zJBO2JwRqZAqIKuU+M+7mf/z1Yrw==";
    private String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEowIBAAKCAQEA76ciAiYV5xbQKA85CG3wo0AI7xhgYH91t3RbSm4i5ecisi1acGxv8PbOQREr" +
            "h2zg0DT+wEW4dEvojkxdGhH5uFJDRwX5iKPIo52H/QnlTnuxtnv38KwCL40bByDmDgxKDV9RK0rE" +
            "+7vVffkWejAdmQe/uI4E67bl4ThC4VpPhd5OjOprDvP6mOlLwJNsijahQEZcTR34yF9rdET5o40m" +
            "psoJFHlsWVhtwyJLZrr+8OKTVXUtOjrlBZ5/ePQ5z/oPr5urIuXDn9Ia+aXVv4HbueusoJNlUOFV" +
            "2shC/TjZ21epzmBbbWEOxPGIn+6jVViE82ChUWIrCMkTA2KfZ9FDFQIDAQABAoIBAHzNK7+0t7lF" +
            "PxdtfgTuw98wDLb+mcoG9nWYCaaEHnZdXMsvJSbgwZbZ4GUwHNwEjjq/Ll9Qr5MYqL3Z9K4L6GDd" +
            "rh2BdgZ7TJQwCE505c5uSgi/HEpgOS8sK4QV1NhB/Bpkpe+Gm3iAw9g7bEIrZm54A5aHlvZto98a" +
            "nnGex0TtkyHdHR7Qdq+UKTV8y8hXhIwfAPrj7UNIs+IVMpFn/xb3oL9jcTZrmMJPCgXy+3HW5jZ5" +
            "vfWrClHfjoyBBxcN0ib1cr2w7Va4o9p2IPUhE1SARXQBDCP04wq/n8WzfDwrVq96DjuTJPJYSRIr" +
            "fKxmE/c+ZXadJ2nHBk2R7Hxyz60CgYEA/v+vVM3y3iOteMoPj8//qE+a99NoEY/WMYbtgM/ulDvl" +
            "Gd7kIlA9y2IgfsMZR5JEgadcAJfMYxd7NBD4jpTZdUbCdWZ5x1jg8TNkfKbu674YBBP0nolkvq0T" +
            "E81nOet6O1PxAUb8VrwQYI9GipEJe7SUgUEviGHLY7/VItbzaTsCgYEA8JgF2GTkWzP91nndPdVt" +
            "CkUgR3CmbXa79DcCCAASxeHBqNmqQeIQIbF8e4JMJHjn3PAlAfGJZgb/OCSuInNTPZRVoWVpy4Ix" +
            "NaH3PjQBNJsHrYciHfhkhwoLEPpfZKrDC1btalMmFusGI/0XB42RkwcjOJOI6yoei7KI+mQ9v+8C" +
            "gYAf6Mz+9rqik6JckCR8YZHjdPq2cmz+bZpnHjRQPzTitdAIebzgklv5PHFGi5F5RFNwSgoYROad" +
            "q82OCWWhKf1AULd6y3UHvhZ/+GdltdoGBAarU/fzcsv5lFzjyHtXYyErlWh+OWzQSb5e5u7z3gxq" +
            "v4Ep34dcMKir9dBtCKrzCwKBgE1gRvj6MarXRLq2hJ2/Rws3ghMXup6XwgjfrAqQo3j6iwLXxfbc" +
            "Ul3Tq/o4xG4yaDdZED3YxVyHwYr32P0BY/L5ArUeXXDy1QqvpUlfBkg517VKMYG8AfCgHUD/lLBW" +
            "btX1xfMc/LIAMgBfBAoM0JWdhQlMAYvIBvzclUsZ8/YHAoGBAM8IEL9HZNNP1WzqkfruSgWEik2l" +
            "5hSZbimdTTa9NYr0MGpPyGz1D8H6JvyYxAzziXfQxwhnvBJ/JY8iOlQ884qliXhiWjYMbz4xdPI9" +
            "L8DDH9FHB5DsO0y/N3SVj9toGe82eQbwI095nOM4UsKlyGUId/0OSOZtWDNQC77ZfIj3\n" +
            "-----END RSA PRIVATE KEY-----";

    public CloudTest(String accesskeyid, String secretkey) {

        ImmutableSet<AbstractModule> modules = ImmutableSet.of(new Log4JLoggingModule(), new SshjSshClientModule());
//        ImmutableSet<AbstractModule> modules = ImmutableSet.of(new Log4JLoggingModule(), new JschSshClientModule());

        context = new
                ComputeServiceContextFactory().createContext(
                "aws-ec2",
                accesskeyid,
                secretkey,
                modules);

        print("Getting computeService");

        ComputeService service = context.getComputeService();

//        Set<? extends Location> locations =
//        service.listAssignableLocations();
//        for (Location next : locations) {
//            print(next.getDescription());
//        }

        listNodes(service);

        String keyPair = "defaultKeypair";

        print("Building template..");
        Template template = service.templateBuilder().locationId("eu-west-1").hardwareId(InstanceType.T1_MICRO).osFamily(OsFamily.AMZN_LINUX).build();
        print("Template Description : " + template.getImage().getDescription());
        String group1 = "quicklaunch-1";

        AWSEC2TemplateOptions templateOptions = template.getOptions().as(AWSEC2TemplateOptions.class);
        templateOptions.securityGroups(group1);
//        templateOptions.keyPair(keyPair);
        templateOptions.overrideLoginUser("user");
        templateOptions.overrideLoginPassword("lamepassword");

//        templateOptions.overrideLoginUserWith("ec2-user");
//        templateOptions.authorizePublicKey(publicKey);
//        templateOptions.overrideCredentialsWith(new Credentials("ec2-user", "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEApgg+hrCqP3uaCJO2Vp+yMQwVvKmHWJ+79jiEVXQu0vdetVxsW1XuUhLAbx8ds28SLGpRJ7ocz/LxhwfH0hg2X3i7PSUWlibSyCFk3aK5Sk+IJmhxaP6nLjlzBb6typKhD8mW+kYovg+t/L+h/WYgz017p1OxgGNvUWd5+ZwjQKm3v8TC9yhPOMGSu7fF6505ivV9IokllD5UOz5tomTVvVUE2oinEGxpnLis1yCnH/Od0bfrMWyC57mi3MYA/89zOxE+ncljGZWRFRATl7J6WzEqKXau+hrNsypyFoFHhTC4lQQOjzRvZhPcx7zJBO2JwRqZAqIKuU+M+7mf/z1Yrw=="));
//        templateOptions.installPrivateKey();

        print("Template credential : "
                + template.getImage().getDefaultCredentials().credential
                + " Identity : " + template.getImage().getDefaultCredentials().identity);

        int[] ports = template.getOptions().getInboundPorts();
        tabs++;
        for (int i = 0; i < ports.length; i++) {
            int port = ports[i];
            print("Has port : " + port);
        }
        tabs--;

        print("Private key : " + template.getOptions().getPrivateKey());
        print("Public key : " + template.getOptions().getPublicKey());

        print("Created template\n");


        try {
            Set<? extends NodeMetadata> nodes = service.createNodesInGroup("default", 1, template);

            tabs++;
            for (NodeMetadata metadata : nodes) {
                print("Created node : " + metadata.getHostname());
                print("Credential : " + metadata.getCredentials().credential +
                        " " + metadata.getCredentials().identity);
//                sendFile(context, metadata, "/newFolder", "a string");
            }
            tabs--;

        } catch (RunNodesException e) {
            e.printStackTrace();
        }


        tabs++;
        listHardware(template.getHardware());
        tabs--;
//        listImages(service);

//        listHardware(service);

        tabs++;
        listNodes(service);
        tabs--;
        context.close();
    }

    private void listImages(ComputeService service) {
        print("Images");
        for (Image image : service.listImages()) {

            String description = image.getDescription();
            String full = image.getOperatingSystem().toString();
            String version = image.getVersion();

            print(description + " " + full + " " + version);
        }
    }

    private void listHardware(ComputeService service) {
        for (Hardware hardware : service.listHardwareProfiles()) {
            listHardware(hardware);
        }
    }

    private void listHardware(Hardware hardware) {
        print("Hardware : " + hardware.getName());
        String id = hardware.getId();
        print("ID : " + id);

        tabs++;
        for (Processor proc : hardware.getProcessors()) {
            String full = proc.toString();
            double cores = proc.getCores();
            double speed = proc.getSpeed();

            print("Full Description : " + full);
            print("Cores : " + cores);
            print("Speed : " + speed);
        }
        tabs--;
        int ram = hardware.getRam();
        print("RAM : " + ram);

        tabs++;
        print("Volumes : ");
        for (Volume vol : hardware.getVolumes()) {
            String device = vol.getDevice();
            String volId = vol.getId();
            Float size = vol.getSize();
            String type = vol.getType().toString();
            boolean boot = vol.isBootDevice();
            boolean durable = vol.isDurable();

            print("Device : " + device);
            print("Volume ID : " + volId);
            print("Size : " + size);
            print("Type : " + type);
            print("Boot : " + boot);
            print("Durable : " + durable);
        }
        tabs--;
    }

    private void listNodes(ComputeService service) {
        print("\n");
        print("Nodes");
        tabs++;
        for (ComputeMetadata node : service.listNodes()) {
            String id = node.getId();
            String name = node.getName();
            String providerID = node.getProviderId();
            String locationDescrition = node.getLocation().getDescription();
            print("Node name : " + name);
            print("ID : " + id);
            print("Provider ID : " + providerID);
            print("Location Description : " + locationDescrition);

            NodeMetadata nodeData = service.getNodeMetadata(id);

            if (nodeData != null) {

                print("Admin password : " + nodeData.getAdminPassword());
                print("Hostname : " + nodeData.getHostname());
                Credentials creds = nodeData.getCredentials();
                if (creds != null) {
                    print("Creds : " + creds.credential);
                    print("Ident : " + creds.identity);
                }
                print("Port : " + String.valueOf(nodeData.getLoginPort()));
                print("OS : " + nodeData.getOperatingSystem().toString());
                print("Group : " + nodeData.getGroup());

                for (String publicIP : nodeData.getPublicAddresses()) {
                    print("Public ip : " + publicIP);
                }
                tabs++;
                listHardware(nodeData.getHardware());
                tabs--;
                NodeState state = nodeData.getState();

                print("State : " + state.name());
                if (state == NodeState.RUNNING) {
                    sendFile(context, nodeData, "/home/ec2-user", new File("/Users/ian/HauntedHouse"));
                }

            } else {
                print(id + " not found");
            }
            print("\n");
        }
        tabs--;
        print("Done listing nodes\n");
    }

    public void sendFile(ComputeServiceContext context, NodeMetadata node, String path, File toSend) {

        SshClient ssh = context.utils().sshForNode().apply(
                NodeMetadataBuilder.fromNodeMetadata(node).credentials(
                        new LoginCredentials(node.getCredentials().identity, null, privateKey, false)
                ).build());
//                .credentials( new Credentials("ec2-user", publicKey)).build());
        try {
            CloudSsh.doSSH(node.getHostname(), node.getCredentials().identity, privateKey);
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }

        try {
            print("Connecting via ssh to : " + ssh.getHostAddress()
                    + " Username " + ssh.getUsername());
            ssh.connect();

            String output = ssh.exec("pwd").getOutput();
            print("SSH output : " + output);
            ssh.put(path,
                    Payloads.newPayload(toSend));
        } finally {
            if (ssh != null)
                ssh.disconnect();
        }

//        try {
//            SSHClient sshClient = new SSHClient();
//            sshClient.loadKnownHosts();
//            sshClient.connect(node.getHostname());
//            sshClient.authPublickey(node.getCredentials().identity);
//            sshClient.startSession().exec("true");
//            sshClient.getConnection().join();
//
//            sshClient.newSCPFileTransfer().upload("/Users/ian/HauntedHouse", "/home/ec2-user/");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void print(String string) {
        System.out.println(tabbing() + string);

    }

    private String tabbing() {
        String tabString = "";
        for (int i = 0; i < tabs; i++) {
            tabString += "   ";
        }
        return tabString;
    }

    public static void main(String[] args) {

        new CloudTest("AKIAINTQNTX4BGC2BNKQ", "IqaxUaC9EstFMVVCIGL8x38hXbOHeOp8H9jxKuC9");
    }


}
