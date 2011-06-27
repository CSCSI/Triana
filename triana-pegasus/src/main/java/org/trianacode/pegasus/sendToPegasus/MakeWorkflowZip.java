package org.trianacode.pegasus.sendToPegasus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 07/03/2011
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class MakeWorkflowZip {
    public static File makeZip(String dax, String properties, String rc, String sites, String tc) throws IOException {
        Properties zipProperties = new Properties();
        zipProperties.put("dax", new File(dax).getName());
        zipProperties.put("properties", new File(properties).getName());
        zipProperties.put("rc", new File(rc).getName());
        zipProperties.put("sites", new File(sites).getName());
        zipProperties.put("tc", new File(tc).getName());
        zipProperties.put("runTool", "RunPegasus");

        File zipPropertiesTemp = File.createTempFile("zipProperties", "xml");
        zipProperties.storeToXML(new FileOutputStream(zipPropertiesTemp), "zipProperties");

        String[] source = new String[]{
                dax,
                properties,
                rc,
                sites,
                tc};
        byte[] buf = new byte[1024];

        File tempZip = File.createTempFile("target", ".zip");
        try {
            JarOutputStream out = new JarOutputStream(new FileOutputStream(tempZip));

            FileInputStream fin = new FileInputStream(zipPropertiesTemp);
            out.putNextEntry(new ZipEntry("zipProperties"));
            int length;
            while ((length = fin.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
            out.closeEntry();
            fin.close();

            for (int i = 0; i < source.length; i++) {
                System.out.println("Adding file " + source[i]);

                if (new File(source[i]).exists()) {

                    FileInputStream in = new FileInputStream(source[i]);
                    File entryFile = new File(source[i]);
                    out.putNextEntry(new ZipEntry(entryFile.getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Failed to write zip file.");
        }
        return tempZip;
    }
}
