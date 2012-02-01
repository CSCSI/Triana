package org.trianacode.cloud;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 11/01/2012
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */

public class UserAuthPubKey {

    public static void main(String[] arg) {

        String pubkeyfile = "/Users/ian/.ssh/id_rsa";
        String passphrase = "";
        String host = "46.137.155.63", user = "ec2-user";

        try {
            JSch jsch = new JSch();
            jsch.addIdentity(pubkeyfile);
//   jsch.addIdentity(pubkeyfile, passphrase);
            jsch.setKnownHosts("/Users/ian/.ssh/known_hosts");

            Session session = jsch.getSession(user, host, 22);
            session.connect();

            Channel channel = session.openChannel("shell");

            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);

            channel.connect();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    } //end of main
} //end of class

