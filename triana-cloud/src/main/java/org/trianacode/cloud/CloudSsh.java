package org.trianacode.cloud;

import com.jcraft.jsch.*;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 26/01/2012
 * Time: 18:54
 * To change this template use File | Settings | File Templates.
 */
public class CloudSsh {

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

    public static void main(String[] args) throws JSchException, SftpException {
        new CloudSsh();
    }

    public CloudSsh() throws JSchException, SftpException {
        doSSH("46.137.155.62", "ec2-user", privateKey);
    }

    public static void doSSH(String hostname, String identity, String privateKey) throws JSchException, SftpException {
        JSch jSch = new JSch();
        JSch.setLogger(new com.jcraft.jsch.Logger() {
            public boolean isEnabled(int enabled) {
                return true;
            }

            public void log(int level, String msg) {
                System.out.println(msg);
            }
        });
        final byte[] prvkey = privateKey.getBytes(); // Private key must be byte array
        final byte[] emptyPassPhrase = new byte[0]; // Empty passphrase for now, get real passphrase from MyUserInfo

//        jSch.addIdentity(
//                identity,    // String userName
//                prvkey,          // byte[] privateKey
//                null,            // byte[] publicKey
//                emptyPassPhrase  // byte[] passPhrase
//        );
        jSch.addIdentity("/Users/ian/.ssh/cloud/defaultKeypair.pem");
        Session session = jSch.getSession(identity, hostname, 22);
        UserInfo ui = new MyUserInfo(); // MyUserInfo implements UserInfo
        session.setUserInfo(ui);
        session.connect();
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftp = (ChannelSftp) channel;
        sftp.connect();


        System.out.println(sftp.pwd());
        final Vector files = sftp.ls(".");
        for (Object obj : files) {
            System.out.println(obj.toString());
        }


        sftp.disconnect();
        session.disconnect();
    }

    private static class MyUserInfo implements UserInfo {
        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassword(String s) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return false;
        }

        @Override
        public boolean promptYesNo(String s) {
            return true;
        }

        @Override
        public void showMessage(String s) {
        }
    }
}
