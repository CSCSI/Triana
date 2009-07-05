/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.taskgraph.tool;

import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.taskgraph.util.TempFileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 3, 2009: 3:17:53 PM
 * @date $Date:$ modified by $Author:$
 */

public class JarHelper {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.tool.JarHelper");

    String[] ignores = {".", "CVS"};


    public static final String JAR_SEP = "/";
    public static final String URI_SEP = "/";
    public static final String FILE_SEP = File.separator;

    private File file;
    private JarFile jarFile;

    public JarHelper(File file) throws ToolException {
        this.file = file;
        try {
            this.jarFile = new JarFile(file);
        } catch (IOException e) {
            throw new ToolException(e.getMessage());
        }
    }

    public List<String> listEntries(String parentEntry) {
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> list = new ArrayList<String>();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (parentEntry != null) {
                if (jarEntry.getName().startsWith(parentEntry) && jarEntry.getName().length() > parentEntry.length()) {
                    list.add(jarEntry.getName());
                }
            } else {
                list.add(jarEntry.getName());
            }
        }
        return list;
    }

    public String getEntry(String path) {
        ZipEntry ze = jarFile.getEntry(path);
        if (ze != null) {
            return ze.getName();
        }
        return null;
    }

    public InputStream getStream(String path) throws IOException {
        ZipEntry ze = jarFile.getEntry(path);
        if (ze != null) {
            return jarFile.getInputStream(ze);
        }
        return null;
    }


    public List<String> listEntries() {
        return listEntries(null);
    }

    /**
     * returns the shortest path that ends with the matching string
     *
     * @param match
     * @return
     */
    public String getShortestEntry(String match) {
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> list = new ArrayList<String>();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith(match)) {
                list.add(jarEntry.getName());
            }
        }
        String ret = null;
        int num = 0;
        for (String s : list) {
            if (ret == null) {
                ret = s;
                num = s.split(JAR_SEP).length;
            } else {
                if (s.split(JAR_SEP).length < num) {
                    ret = s;
                }
            }
        }
        return ret;
    }

    public List<String> listDirectChildEntries(String parentEntry) {
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> list = new ArrayList<String>();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().startsWith(parentEntry) && jarEntry.getName().length() > parentEntry.length()) {
                String extra = jarEntry.getName().substring(parentEntry.length(), jarEntry.getName().length());
                if (extra.indexOf(JAR_SEP) == -1 || extra.indexOf(JAR_SEP) == extra.length() - 1) {
                    log.fine(" adding direct child of " + parentEntry + ": " + jarEntry.getName());
                    list.add(jarEntry.getName());
                }
            }
        }
        return list;
    }


    public File extract(String res, File destDir) throws IOException {
        log.fine(" jar path=" + res);
        File ret = extractEntry(res, destDir);
        if (ret == null) {
            throw new IOException("no resource found with name " + res);
        }
        if (ret.isDirectory()) {
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry je = en.nextElement();
                String name = je.getName();
                if (name.startsWith(res)) {
                    extractEntry(name, destDir);
                }
            }
        }
        return ret;
    }


    private File extractEntry(String path, File destDir) throws IOException {
        log.fine("Path=" + path);
        JarEntry entry = jarFile.getJarEntry(path);
        if (entry == null) {
            return null;
        }
        InputStream in = jarFile.getInputStream(entry);
        File f = createOutputFile(destDir, path);
        if (f.isDirectory()) {
            return f;
        }
        FileOutputStream out = new FileOutputStream(f);
        byte[] bytes = new byte[8192];
        int c;
        while ((c = in.read(bytes)) != -1) {
            out.write(bytes, 0, c);
        }
        out.flush();
        out.close();
        in.close();
        return f;
    }

    private File createOutputFile(File destDir, String jarEntry) {
        int sep = jarEntry.indexOf(JAR_SEP);
        if (sep == -1) {
            return new File(destDir, jarEntry);
        }
        String path = jarEntry.substring(0, jarEntry.lastIndexOf(JAR_SEP)).replace(JAR_SEP, FILE_SEP);
        File dir = new File(destDir, path);
        dir.mkdirs();
        String name = jarEntry.substring(jarEntry.lastIndexOf(JAR_SEP) + 1, jarEntry.length());
        if (name.length() == 0) {
            return dir;
        }
        return new File(dir, name);
    }


    private static void writeJarEntry(JarEntry entry, JarFile source, JarOutputStream jos, byte[] bytes) throws IOException {
        if (entry == null) {
            return;
        }
        InputStream is = source.getInputStream(entry);
        jos.putNextEntry(entry);
        int c;
        while ((c = is.read(bytes)) != -1) {
            jos.write(bytes, 0, c);
        }
    }

    public File update(File target, File[] add, String[] delete) throws IOException {
        if (add == null) {
            add = new File[0];
        }
        if (delete == null) {
            delete = new String[0];
        }
        File temp = TempFileManager.createTempFile("triana", ".tmp");
        JarFile jar = new JarFile(target);
        boolean success = false;
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(temp));
        byte[] bytes = new byte[4096];
        try {
            ArrayList<String> existing = new ArrayList<String>();

            JarEntry meta = jar.getJarEntry("META-INF/");
            writeJarEntry(meta, jar, jos, bytes);
            existing.add(meta.getName());

            JarEntry man = jar.getJarEntry("META-INF/MANIFEST.MF");
            writeJarEntry(man, jar, jos, bytes);
            existing.add(man.getName());

            for (int i = 0; i < add.length; i++) {
                File file = add[i];
                writeEntry(file, jos, file.getParentFile(), existing, false);
            }
            Enumeration entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                for (int i = 0; i < delete.length; i++) {
                    String s = delete[i];
                    if (s.startsWith(JAR_SEP)) {
                        s = s.substring(1, s.length());
                    }

                }
                if (!existing.contains(entry.getName())) {
                    writeJarEntry(entry, jar, jos, bytes);
                    existing.add(entry.getName());
                }
            }

            success = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                jos.close();
                jar.close();
            } catch (Exception ignored) {
            }
        }
        if (!success) {
            return null;
        } else {
            String name = target.getName();
            File parent = target.getParentFile();

            target.delete();
            target = new File(parent, name);
            FileUtils.copyFilesRecursive(temp, target);
            return target;
        }
    }

    private void writeEntry(File f, JarOutputStream jos, File build, List<String> entries, boolean recursive) throws IOException {
        if (f == null || shouldBeIgnored(f.getName())) {
            return;
        }
        String path = createPath(build, f);
        if (path == null) {
            return;
        }
        if (entries.contains(path)) {
            return;
        }
        entries.add(path);
        if (f.isDirectory()) {
            jos.putNextEntry(new ZipEntry(path));
            jos.closeEntry();
            if (recursive) {
                File[] childers = f.listFiles();
                for (File childer : childers) {
                    writeEntry(childer, jos, build, entries, true);
                }
            }
        } else {
            FileInputStream in = new FileInputStream(f);
            byte[] bytes = new byte[(int) f.length()];
            in.read(bytes);
            jos.putNextEntry(new ZipEntry(path));
            jos.write(bytes, 0, bytes.length);
            jos.closeEntry();
        }
    }


    private boolean shouldBeIgnored(String name) {
        for (String ignore : ignores) {
            if (name.startsWith(ignore)) {
                return true;
            }
        }
        return false;
    }

    private static String createPath(File build, File file) throws IOException {
        String root = build.getCanonicalPath();
        String entry = file.getCanonicalPath();
        String jarPath = entry.substring(root.length(), entry.length()).replace(File.separator, JAR_SEP);
        if (file.isDirectory() && !(jarPath.endsWith(JAR_SEP))) {
            jarPath += JAR_SEP;
        }
        if (jarPath.startsWith(JAR_SEP)) {
            jarPath = jarPath.substring(1, jarPath.length());
        }
        return jarPath;
    }

}
