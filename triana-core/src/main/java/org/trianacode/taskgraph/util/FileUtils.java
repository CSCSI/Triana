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

package org.trianacode.taskgraph.util;

import org.trianacode.taskgraph.tool.ClassLoaders;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 24, 2009: 9:31:17 PM
 * @date $Date:$ modified by $Author:$
 */

public class FileUtils {

    static Logger logger = Logger.getLogger("org.trianacode.taskgraph.util.FileUtils");


    public static final int ASCII = 0;
    public static final int BINARY = 0;


    /**
     * deletes files recursively. can optionally delete the parent file as well. So, if the parent file
     * is not a directory, and incParent is false, then nothing will be deleted.
     *
     * @param parent    file to delete. If this is a directory then any children are deleted.
     * @param incParent boolean that determines if the parent file is also deleted.
     * @throws java.io.FileNotFoundException
     */
    public static void deleteFiles(File parent, boolean incParent) throws FileNotFoundException {
        if (!parent.exists()) {
            throw new FileNotFoundException("File does not exist.");
        }
        if (parent.isDirectory() && !(parent.listFiles() == null)) {
            File[] files = parent.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFiles(files[i], true);
            }
        }
        if (incParent) {
            parent.delete();
        }
    }


    public static String formatThrowable(Throwable t) {
        StringBuilder sb = new StringBuilder(t.getClass().getName());
        t.fillInStackTrace();
        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement stackTraceElement : trace) {
            sb.append("\t\n").append(stackTraceElement);
        }
        return sb.toString();
    }

    public static boolean rename(File src, File dest) {
        if (src.getAbsolutePath().equals(dest.getAbsolutePath())) {
            return true;
        }
        boolean rename = src.renameTo(dest);
        if (!rename) {
            try {
                copyFilesRecursive(src, dest);
            } catch (IOException e) {
                logger.warning("Exception thrown while renaming:" + FileUtils.formatThrowable(e));
                return false;
            }
        }
        return true;
    }


    /**
     * Copies files and directories recursively. The destination does not
     * have to exist yet.
     *
     * @param src  the source file to copy. Can be a file or a directory.
     * @param dest the destination to copy to. Can be a file or directory.
     * @return a List of the newly created Files
     * @throws java.io.FileNotFoundException if src does not exist
     * @throws java.io.IOException           if src is a directory and dest exists
     *                                       and is not a directory, or an IO error occurs.
     */
    public static List<File> copyFilesRecursive(File src, File dest) throws IOException {
        ArrayList<File> list = new ArrayList<File>();
        if (!src.exists()) {
            throw new FileNotFoundException("Input file does not exist.");
        }
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            } else if (!dest.isDirectory()) {
                throw new IOException("cannot write a directory to a file.");
            }
            list.add(dest);
            File[] srcFiles = src.listFiles();
            for (int i = 0; i < srcFiles.length; i++) {
                list.addAll(copyFilesRecursive(srcFiles[i], new File(dest, srcFiles[i].getName())));
            }
        } else {
            if (dest.exists() && dest.isDirectory()) {
                dest = new File(dest, src.getName());
            }
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] bytes = new byte[8192];
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }
            out.flush();
            out.close();
            in.close();
            list.add(dest);
        }
        return list;
    }


    /**
     * Deletes the file at the given file location. If the file
     * is a directory then the directory must be empty for the
     * operation to be successful.
     */
    public static void deleteFile(String loc) {
        File f1 = new File(loc);
        f1.delete();
    }

    /**
     * Determines whether the given path or file is an internet
     * address or not
     */
    public static boolean isOnInternet(String fileOrPath) {
        if (fileOrPath.indexOf("://") != -1)
            return true;
        else
            return false;
    }

    /**
     * Determines whether the file specified
     * is a directory. Files specified as string could be internet
     * paths or local paths.  This function will determine which
     * one it is.
     */
    public static boolean isDirectory(String loc) {
        if (loc.indexOf("://") == -1) {
            return new File(loc).isDirectory();
        } else
            try {
                return isDirectory(new URL(correctURL(loc)));
            } catch (MalformedURLException url) {
            }

        return false;
    }

    /**
     * Checks whether the <code>child</child> is contained by the <code>parent</code>
     * directory.
     *
     * @param parent String representing the parent directory
     * @param child  String representing the child file or directory
     * @return <b>False</b> if <code>parent</code> is not a directory, or <code>parent</code>
     *         equals <code>child</code>, <b>True</b> if and only if <code>child</code> is contained
     *         in <code>parent</code>.
     */
    public static boolean isParent(String parent, String child) {
        File parentFile = new File(parent);
        if (parentFile.isDirectory()) {
            File childFile = new File(child);
            if (!parentFile.equals(childFile)) {
                String s = childFile.getParent();
                while (s != null) {
                    childFile = new File(s);
                    if (parentFile.equals(childFile))
                        return true;
                    s = childFile.getParent();
                }
            }
        }
        return false;
    }

    /**
     * Converts a URL address which may have machine dependant
     * path separators contained within it to an appropraite
     * URL address e.g. can have http://www.comp.com/\file\on\d\
     * for example which should be http://www.comp.com/file/on/d/
     */
    public static String correctURL(String file) {
        if (file.indexOf("http://") == -1) return file;

        String fn = file.replace('\\', '/');
        // System.out.println("Changed to " + fn);
        return fn;
    }


    /**
     * Determines whether the internet location and file specified
     * is in fact a directory. This is determined by checking whether
     * there exists a <pre> <title>Index Of ..... </title></pre> exists
     * on the first line of the html file which is the typical
     * thing included on the first line if a directory is given to
     * a server
     */
    public static boolean isDirectory(URL loc) {
        // if the file has a . in it i.e. tmp.java then dismiss it

        String s = correctURL(loc.toString());

        if (s.indexOf(".", s.lastIndexOf("/")) != -1)
            return false;

        if (!s.endsWith("/"))
            s = s + "/";

        try {
            Vector<String> str = FileUtils.readAndSplitFile(FileUtils.createReader(s));

            if (str.size() == 0)
                return false;

            if (str.get(0).toLowerCase().indexOf("<title>index of") != -1)
                return true;

            // before returning false we need to check that the server
            // hasn't added a /index.html file to the end and passed this
            // file back instead which it will do. All we need to do
            // is test that the string s + index.html exists or not.
            // if it does exist then s is still a directory but it contains
            // the index.html file :-

            try {
                new URL(s + "index.html");
            } catch (MalformedURLException murl) {
                return false;
            }

            return true;
        } catch (IOException except) {
            return false;
        }
    }


    /**
     * Lists the filenames and directories within a URL if
     * that URL is a directory
     */

    public static Vector<String> listInternetDirectory(URL loc) {
        Vector<String> str = FileUtils.readAndSplitFile(FileUtils.createReader(loc));
        String cfile;

        Vector<String> files = new Vector<String>();
        String st;

        for (int i = 0; i < str.size(); ++i) {
            st = str.get(i).toLowerCase();

            if (st.indexOf("href=") != -1) {
                int pos = st.indexOf("href=") + 6;
                int pos2 = st.indexOf("\"", pos);
                int pos3 = st.indexOf("/", pos);
                if (pos3 > 0) // i.e. a / was found
                    pos2 = Math.min(pos2, pos3);
                String fn;
                if (pos2 > 0)
                    fn = str.get(i).substring(pos, pos2);
                else
                    fn = (str.get(i).substring(pos).trim());

                fn = fn.replace("\\", "/");
                fn = fn.trim();

                cfile = loc.toString();
                if (!cfile.endsWith("/")) cfile += "/";
                cfile += fn;

                System.out.println("File extracted = " + cfile);

                if ((!fn.startsWith("/")) && (fn.indexOf("%") == -1)
                        && (fn.indexOf("?") == -1)
                        && (fn.trim().length() > 0) && (!fn.equals(".."))
                        && (!fn.equals("")) && (isDirectory(cfile))) {
                    // miss out root directory and backup files
                    // and empty ones and ../ directories
                    files.addElement(fn);
                    System.out.println("Added " + fn + " to listing..");
                } else
                    System.out.println("Rejected " + fn);
            }
        }
        return files;
    }

    /**
     * As copyFile except that it doesn't confirm on replacing a file.
     *
     * @see #copyFile
     */
    public static boolean copyFile(String loc1, String loc2, int mode) {
        return copyFile(loc1, loc2, mode, false);
    }

    /**
     * Copies the file from the first location to the second.  If the
     * second location exists then the user is prompted to ask
     * if he/she wants to overwrite the exisiting file.  They are given
     * dates of the files also.  The full Bill Gates effect!!!!!!
     * The mode can be either ASCII or BINARY.
     *
     * @return false if the file could not be copied, true if successful
     */
    public static boolean copyFile(String loc1, String loc2, int mode, boolean confirmOnReplace) {
        BufferedReader br;
        DataInputStream ds;
        PrintWriter bw;
        DataOutputStream dout;
        long fmod1, fmod2;
        String text;
        byte[] bytes;

        File f1;
        File f2;
        if ((loc1 == null) || (loc2 == null))
            return false;

        f1 = new File(loc1);
        f2 = new File(loc2);

        if (!f1.exists())
            return false;

        if (f2.isDirectory()) // add source file name if none chosen
            loc2 = loc2 + File.separator +
                    loc1.substring(loc1.lastIndexOf(File.separator) + 1);

        f2 = new File(loc2);

        if (confirmOnReplace) {
            if ((f1.exists()) && (f2.exists())) { // does the time check with user
                fmod1 = f1.lastModified();
                fmod2 = f2.lastModified();

                /*
                TODO
                QuestionWindow con = new QuestionWindow(null,
                        "Do you want to replace :\n" +
                                loc2 + " modified : " + Str.niceDateAndTime(new Date(fmod2)) +
                                "\nwith\n" +
                                loc1 + " modified : " + Str.niceDateAndTime(new Date(fmod1)) + " ?\n");
                if (con.reply != con.YES)
                    return false;*/
            }
        }

        try {
            if (mode == ASCII) {
                br = createReader(loc1);
                text = readFile(br);
                bw = FileUtils.createWriter(loc2);
                bw.print(text);
                closeWriter(bw);
                closeReader(br);
            } else {
                ds = new DataInputStream(new FileInputStream(f1));
                bytes = new byte[(int) f1.length()];
                ds.readFully(bytes);
                dout = createOutputStream(loc2);
                dout.write(bytes);
                dout.close();
                ds.close();
            }
        } catch (Exception ee) {
            logger.warning("\"Error Copying from \\n\" +\n" +
                    "                    loc1 + \"  to \\n\" + loc2" + ":" + formatThrowable(ee));


            return false;
        }

        return true;
    }

    /**
     * creates a buffered writer for a specified local or networked
     * text file. Just give the protocol and path if its a networked file
     * e.g. :-
     * http:///www.astro.cf.ac.uk/pub/Ian.Taylor/Triana/Help/Wave.html
     * or the absolute file name if its a local file. </p><p>
     *
     * @param name the name of the file you wish to make a writer for.
     */
    public static PrintWriter createWriter(String name) {
        PrintWriter prn = null;

        try {
            if (name.indexOf("://") != -1) {  // a network path
                URL url = new URL(correctURL(name));
                return createWriter(url);
            } else {
                new File(name).getParentFile().mkdirs();
                FileWriter fwrite = new FileWriter((name));


                prn = new PrintWriter(new BufferedWriter(fwrite));
            }
        } catch (IOException io) {
            logger.warning("Couldn't open a writer " + name + ":" + formatThrowable(io));
        }
        return prn;
    }

    /**
     * Creates a buffered writer to a specific URL. </p><p>
     *
     * @param url the name of the file you wish to make a writer for
     */
    public static PrintWriter createWriter(URL url) {
        PrintWriter prn = null;

        try {
            prn = new PrintWriter(new BufferedWriter(new OutputStreamWriter(url.openConnection().getOutputStream())));
        } catch (IOException io) {
            // new ErrorDialog(null,"Couldn't open a network writer " + url.getFile());
        }
        return prn;
    }

    /**
     * creates a buffered writer for a specified local file, with an
     * option to append.  Just give the absolute file name.</p><p>
     *
     * @param name the name of the file you wish to make a writer for.
     */
    public static PrintWriter createWriter(String name, boolean append) {
        PrintWriter prn = null;

        try {
            new File(name).getParentFile().mkdirs();
            FileWriter fwrite = new FileWriter(name, append);
            prn = new PrintWriter(new BufferedWriter(fwrite));
        } catch (IOException io) {
            logger.warning("Couldn't open a writer " + name + ":" + formatThrowable(io));

        }
        return prn;
    }

    /**
     * creates a buffered reader for a specified local or networked
     * text file. Just give the protocol and path if its a networked file
     * e.g. :-
     * http:///www.astro.cf.ac.uk/pub/Ian.Taylor/Triana/Help/Wave.html
     * or the absolute file name if its a local file. </p><p>
     *
     * @param name the name of the file you wish to make a reader for.
     */
    public static BufferedReader createReader(String name) throws IOException {
        BufferedReader br = null;

        if (name.indexOf("://") != -1) {  // a network path
            URL url = new URL(correctURL(name));
            return createReader(url);
        } else {
            br = new BufferedReader(new FileReader(name));
        }

        return br;
    }


    /**
     * creates a buffered reader for a URL
     *
     * @param url the name of the file you wish to make a reader for.
     */
    public static BufferedReader createReader(URL url) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException io) {
            // new ErrorDialog(null,"Couldn't open a network reader for " + url.getFile());
        }
        return br;
    }

    /**
     * return true if the specified file (or Directory) exists either on a network or a local
     * server.  The function automatically detects where the file is.
     *
     * @param name the name of the file.
     */
    public static boolean fileExists(String name) {
        try {
            if (name.indexOf("://") != -1) {  // a network path
                return true;

                /* URL ur = new URL(correctURL(name));
                URLConnection url = ur.openConnection();

                String typ = url.getContentType();

                StringVector sv = FileUtils.readAndSplitFile(
                        FileUtils.createReader(ur));

                System.out.println(sv.at(0));
                System.out.println(typ);

                if (typ.equals("text/plain"))
                    return true;

                if (typ.equals("text/html"))
                    if (GUIEnv.isAnApplet())
                        return true;
                    else if (isDirectory(sv.at(0)))
                        return true;
                return false;*/
            } else {
                File f = new File(name);
                return f.exists();
            }
        } catch (Exception io) {
            System.out.println("Exception for " + name);
            return false;
        }
    }

    /**
     * closes the given reader
     *
     * @param br the reader to close.
     */
    public static void closeReader(BufferedReader br) {
        try {
            if (br != null)
                br.close();
        } catch (IOException io) {
            System.out.println("Couldn't close Writer " + br);
        }
    }

    /**
     * closes the given writer
     *
     * @param pw the writer to close.
     */
    public static void closeWriter(PrintWriter pw) {
        if (pw != null)
            pw.close();
    }

    /**
     * Write the specified String to the file
     */
    public static void writeToFile(String name, String contents) {
        PrintWriter pw = createWriter(name);
        pw.println(contents);
        closeWriter(pw);
    }

    /**
     * Write the specified String to the file with option to append
     */
    public static void writeToFile(String name, String contents, boolean append) {
        PrintWriter pw = createWriter(name, append);
        pw.println(contents);
        closeWriter(pw);
    }

    /**
     * Write the specified StringVector to the file, writing
     * each element on a separate line
     */
    public static void writeToFile(String name, Vector<String> contents) {
        PrintWriter pw = createWriter(name);

        for (int i = 0; i < contents.size(); ++i)
            pw.println(contents.get(i));

        closeWriter(pw);
    }


    /**
     * Creates a reader for a specified local or networked
     * binary file. Just give the protocol and path if its a networked file
     * e.g. :-
     * http:///www.astro.cf.ac.uk/pub/Ian.Taylor/Triana/Help/Wave.html
     * or the absolute file name if its a local file. </p><p>
     *
     * @param name the name of the file you wish to make a reader for.
     */
    public static DataInputStream createInputStream(String name) throws IOException {
        DataInputStream ds = null;

        if (name.indexOf("://") != -1) {  // a network path
            URL url = new URL(correctURL(name));
            ds = new DataInputStream(new BufferedInputStream(url.openStream()));
        } else {
            ds = new DataInputStream(new BufferedInputStream(new FileInputStream(name)));
        }

        return ds;
    }

    /**
     * Creates an output stream for a specified local or networked
     * binary file. Just give the protocol and path if its a networked file
     * e.g. :-
     * http:///www.astro.cf.ac.uk/Triana/data.bin
     * or the absolute file name if its a local file. </p><p>
     *
     * @param name the name of the file you wish to make an output stream for.
     */
    public static DataOutputStream createOutputStream(String name) {
        DataOutputStream ds = null;

        try {
            if (name.indexOf("://") != -1) {  // a network path
                URL url = new URL(correctURL(name));
                ds = new DataOutputStream(new BufferedOutputStream(url.openConnection().getOutputStream()));
            } else {
                ds = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(name)));
            }
        } catch (IOException io) {
            logger.warning("Couldn't open an Output Stream for " + name + ":" + formatThrowable(io));
        }
        return ds;
    }

    /**
     * Closes the given output stream.
     */
    public static void closeOutputStream(DataOutputStream dos) {
        try {
            if (dos != null)
                dos.close();
        } catch (IOException io) {
            System.out.println("Couldn't close Output Stream " + dos);
        }
    }

    /**
     * Closes the given input stream.
     */
    public static void closeInputStream(DataInputStream di) {
        try {
            if (di != null)
                di.close();
        } catch (IOException io) {
            System.out.println("Couldn't close Input Stream " + di);
        }
    }

    /**
     * Loads in a sound file from the specified internet location.
     */
    public static AudioClip getAudioClip(URL audioLoc) {
        System.out.println("Loading .... " + audioLoc);
        try {
            return Applet.newAudioClip(audioLoc);
        } catch (Exception e) {
            System.out.println("Couldn't load sound file " + audioLoc);
        }
        return null;
    }

    /**
     * Loads in a sound file from the internet or local disk.  It auto-detects
     * what protocol it should use.
     */
    public static AudioClip getAudioClip(String audioFile) {
        AudioClip audio = null;

        try {
            if (FileUtils.isOnInternet(audioFile))
                audio = getAudioClip(new URL(audioFile));
            else {
                URL url = FileUtils.class.getResource(audioFile);
                if (url != null) {
                    audio = getAudioClip(url);
                }
            }
        } catch (Exception e) {
            audio = null;
            e.printStackTrace();
        }
        return audio;
    }

    /**
     * Loads in a Triana system sound file via the internet (if it is an applet)
     * or local disk.  It auto-detects what protocol it should use.
     * It looks in the triana/system/sounds directory for
     * the sound file called "audioFile"
     */
    public static AudioClip getSystemAudioClip(String audioFile) {
        return getAudioClip("system" + "/" +
                "sounds" + "/" + audioFile);
    }

    /**
     * Plays a Triana system sound file by loading it across the internet
     * (if Triana is an applet)or local disk.  It auto-detects what protocol
     * it should use.
     * It looks in the %TRIANA%/system/sounds directory for
     * the sound file called "audioFile"
     */
    public static void playSystemAudio(String audioFile) {
        final AudioClip audio = getSystemAudioClip(audioFile);

        audio.play();

/*        Thread t = new Thread( new Runnable() {   // set off in a different thread
            public void run() {
                audio.play();
                }
            });

        t.start(); */
    }

    /**
     * Loads in an image from the internet or local disk.  It auto-detects
     * what protocol it should use.
     * <p/>
     * This is legacy and only used by some Units - core Triana classes should use
     * {@link #getSystemImage}
     */
    public static Image getImage(String imageName) {
        Image image;

        try {
            if (FileUtils.isOnInternet(imageName))
                image = Toolkit.getDefaultToolkit().getImage(new URL(imageName));
            else
                image = Toolkit.getDefaultToolkit().getImage(imageName);
        } catch (Exception e) {
            System.out.println("Couldn't load image " + imageName);
            image = null;
        }
        return image;
    }


    /**
     * Loads in a triana system image icon from the internet or local disk.  It auto-detects
     * what protocol it should use and looks in the triana/system/icons directory for
     * the image called "imageName"
     */
    public static ImageIcon getSystemImageIcon(String imageName) {
        Image image = getSystemImage(imageName);

        if (image == null)
            return null;
        else
            return new ImageIcon(image);
    }


    /**
     * Loads in a triana system image from the internet or local disk.  It auto-detects
     * what protocol it should use and looks in the triana/system/icons directory for
     * the image called "imageName"
     */
    public static Image getSystemImage(String imageName) {
        imageName = imageName.replace(File.separatorChar, '/');
        String dir = "system/icons/crystalicons/";
        if (imageName.startsWith("/")) {
            dir = "system/icons/crystalicons";
        }
        String imageURL = dir + imageName;
        logger.finest("loading icon: " + imageURL);
        URL imageResource = ClassLoaders.getResource(imageURL);
        if (imageResource == null) {
            logger.warning("error loading icon: " + imageURL);
            return null;
        } else
            return Toolkit.getDefaultToolkit().getImage(imageResource);
    }


    /**
     * @return the file name without suffix or path details.
     */
    public static String getFileNameNoSuffix(String fullName) {
        String name = (new File(fullName)).getName();

        if (name.lastIndexOf('.') > -1)
            return name.substring(0, name.lastIndexOf('.'));
        else
            return name;
    }

    /**
     * This function splits up the CLASSPATH and the TOOLBOXES environment
     * variables into the various specified paths. A Vector is returned
     * containing the paths found.  We also take into account that paths
     * can be internet address also by including an internet address in the
     * following form in your environment variables :- </p>
     * <p/>
     * <center>
     * protocol://server/directory<br>
     * for example<br>
     * http://www.astro.cf.ac.uk/pub/Ian.Taylor/Triana/Release/classes<br>
     * </center>
     */
    public static Vector splitPath(String path) {
        if (path == null)
            return null;

        Vector paths = new Vector(10);
        // 10's quite a lot, should be OK

        String separator = System.getProperty("path.separator");

        String newpath;
        URL urlPath;

        // get rid of colons as they are confusing as they act
        // as path separators as well. Put the http and ftp etc in just
        // in case some accidentally put // in as a local directory after
        // a deliminator e.g. path://usr/local/path

        path.replace("http://", "http+//");
        path.replace("ftp://", "ftp+//");
        path.replace("file://", "file+//");

        StringTokenizer st = new StringTokenizer(correctURL(path), separator);

        while (st.hasMoreTokens()) {
            newpath = st.nextToken();

            int aNetwork = newpath.indexOf("+");

            if (aNetwork != -1) { // an internet directory
                if ((!newpath.endsWith("/")) && (!newpath.endsWith(".tbx"))
                        && (!newpath.endsWith(".html")))
                    newpath = newpath + "/";
                path.replace("+", ":");  // swap +'s with :'s
                try {
                    urlPath = new URL(newpath);
                    paths.addElement(urlPath);
                } catch (MalformedURLException me) {
                    System.err.println("Network Path " + newpath +
                            " NOT FOUND!");
                }
            } else { // local directory or zipfile
                ZipFile z = null;
                if (newpath.indexOf(".zip") != -1) { // a zip file
                    try {
                        if (newpath != null)
                            z = new ZipFile(newpath);
                        if (z != null)
                            if (!paths.contains(z))
                                paths.addElement(z);
                    } catch (IOException io) {
                        // System.out.println(newpath + " not a zipfile!");
                    }
                } else {
                    if ((!newpath.endsWith(File.separator)) &&
                            (!newpath.endsWith(".tbx")) && (!newpath.endsWith(".html")))
                        newpath = newpath + File.separator;
                    paths.addElement(newpath);
                }
            }
        }

        return paths;
    }

    /**
     * This function splits the next line read from the specified
     * BufferReader into a vector of Strings contained within the line.
     * Each string is split up if they are separated by a single white
     * space. The function returns a StringVector which is basically,
     * a Vector which only stores strings, so we don't have type-cast
     * all the time.
     */
    public static Vector<String> readAndSplitLine(BufferedReader br) {
        if (br == null)
            return null;

        String line;

        try {
            line = br.readLine();
        } catch (IOException ee) {
            System.out.println("Couldn't input from " + br.toString());
            return null;
        }

        if (line == null)
            return null;

        return splitLine(line);
    }

    /**
     * Skips the empty lines in a file and returns the next non-empty line
     * or null if the end of the file is reached. The line returned
     * is a vector of string with one string representing each word
     * or whatever in the file, like readAndSplitLine
     *
     * @see #readAndSplitLine
     */
    public static Vector<String> nextNonEmptySplitLine(BufferedReader br) {
        Vector<String> line;

        do {
            line = FileUtils.readAndSplitLine(br);
            if (line == null)
                return null;
        } while ((line.size() < 1) || ((line.size() == 1) &&
                (line.get(0).equals(""))));
        return line;
    }

    /**
     * Skips the empty lines in a file and returns the next non-empty line
     * or null if the end of the file is reached.
     *
     * @see #readAndSplitLine
     */
    public static String nextNonEmptyLine(BufferedReader br)
            throws IOException {
        String line;
        do {
            line = br.readLine();
            if (line == null)
                return null;
            line = line.trim();
        } while (line.length() < 1);

        return line;
    }

    /**
     * This function splits the line into a vector of Strings
     * separated by spaces.
     * Each string is split up if they are separated by a single white
     * space. The function returns a StringVector which is basically,
     * a Vector which only stores strings, so we don't have type-cast
     * all the time.
     */
    public static Vector<String> splitLine(String line) {
        Vector<String> items = new Vector<String>(10);  // 10 should be OK

        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreTokens()) {
            items.addElement(st.nextToken());
        }

        return items;
    }


    /**
     * Reads all the file pointed to by the given reader
     * into the returned string.  This function also closes
     * the file (since we have read it all!). </p>
     * <p> For example, to open, read all, and close a file you
     * type :- </p>
     * <pre>
     * String template = FileUtils.readFile(FileUtils.createReader(fileName));
     * </pre>
     */
    public static String readFile(BufferedReader br) {
        String line;
        String str = "";

        if (br == null)
            return str;

        try {
            while ((line = br.readLine()) != null)
                str = str + line + "\n";
        } catch (IOException ee) {
            System.out.println("Couldn't input from " + br.toString());
            return null;
        }

        closeReader(br);

        return str;
    }


    /**
     * Reads the file specified by the given Filename and
     * puts it into a string. This uses DataInputStream to
     * read the file fully and then create a string
     * which is a lot faster than reading a line at a time.
     * Use this function for speed.
     */
    public static String readFile(String filename) {
        String st = null;

        try {
            if (filename.indexOf("://") != -1) {  // a network path
                BufferedReader br = createReader(correctURL(filename));
                String contents = readFile(br);
                closeReader(br);
                return contents;
            }

            File fi = new File(filename);
            long llen = fi.length();
            int len = (int) (llen & 0x7FFFFFFF);
            if (len != llen) {
                System.out.println("File too big to read into a single object");
                return null;
            }

            byte[] b = new byte[len];

            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(fi)));
            dis.readFully(b);
            st = new String(b);
            dis.close();
        } catch (FileNotFoundException ee) {
            logger.warning("Couldn't find file " + filename + ":" + formatThrowable(ee));
            return null;
        } catch (IOException eee) {
            logger.warning("IO exception on file " + filename + ":" + formatThrowable(eee));
            return null;
        }

        return st;
    }

    /**
     * This function splits the file into a vector of Strings,
     * each item in the vector representing one line. Used for
     * loading in the TrianaType file and useful for splitting up
     * file into a vector of easily accessable lines.
     * This function also closes
     * the file (since we have read it all!)
     */
    public static Vector<String> readAndSplitFile(BufferedReader br) {
        Vector<String> lines = new Vector<String>(10);  // 10 should be OK


        if (br == null)
            return null;

        String line;

        try {
            while ((line = br.readLine()) != null)
                lines.addElement(line);
        } catch (IOException ee) {
            System.out.println("Couldn't input from " + br.toString());
            return null;
        }

        closeReader(br);

        return lines;
    }

    /**
     * This function splits the file into a vector of Strings,
     * each item in the vector representing one line. Used for
     * loading in the TrianaType file and useful for splitting up
     * file into a vector of easily accessable lines.
     * This function also closes
     * the file (since we have read it all!)
     */
    public static Vector<String> readAndSplitFile(String fileName) {
        try {
            return readAndSplitFile(createReader(fileName));
        } catch (IOException except) {
            return new Vector<String>();
        }
    }

    /**
     * Convert the given fileName into a machine independant form
     * by making use of the environment variables to set up
     * relative paths etc. for example, instead of using
     * /home/specta/ian/grodcl/dir/file.name we would use
     * $TRIANA/dir/file.name so that we could convert the
     * file to the relevant directory on any machine.
     */
    public static String convertToVirtualName(String fileName) {
        // start with the largest path and then all sub paths
        // to try an extract as much information as possible.

        String newFile = new String(correctURL(fileName));

        if (fileName.trim().equals(""))
            return fileName;

/*        System.out.println("File = " + newFile);
        if (Env.home().length() <= newFile.length())
            System.out.println("File = " + newFile.substring(0, Env.home().length()));
        System.out.println("File = " + Env.home());
  */
        String userHome = System.getProperty("user.home");
        if (userHome.length() <= newFile.length())
            if (newFile.substring(0, userHome.length()).equalsIgnoreCase(userHome))
                newFile = "$USER_HOME" + newFile.substring(userHome.length());

        return newFile;
    }

    /**
     * Convert machine independant form to actual fileName
     */
    public static String convertFromVirtualName(String fileName) {
        // start with the largest path and then all sub paths
        // to try an extract as much information as possible.

        String newFile = new String(fileName);

        if (fileName.trim().equals(""))
            return fileName;


        if (newFile.indexOf("$USER_HOME") != -1)
            newFile = System.getProperty("user.home") + newFile.substring("$USER_HOME".length());

        if (newFile.indexOf("://") == -1) {
            if (newFile.indexOf("/") != -1) { // created on Unix
                if (!File.separator.equals("/"))
                    newFile = newFile.replace('/', File.separator.charAt(0));
            } else { // created on a DOS machine
                if (File.separator.equals("/"))
                    newFile = newFile.replace('\\', '/');
            }
        } else {
            if ((newFile.indexOf('\\')) != -1) {
                newFile = newFile.replace('\\', '+');
                newFile = newFile.replace("+", "");
            }
        }

        return newFile;
    }

    /**
     * Gets a Listing Object for the given directory, the given
     * searchstring (i.e. a wildcard based matcher) and a flag
     * indicating whether the FileList should recurse into
     * other directories or not.  </p>
     * <p> The searchFilter can be any unix style (or DOS style) search
     * expression when using <i>ls</i> or <i>dir</i>. This can also
     * be a file, directory or null.  If its a file then this class
     * can be used to determine whether the file exists or not (i.e.
     * if fileFound() == 0 or exists() == false then the file
     * does not exist. If searchFilter is a  directory then the
     * directory is listed (if it exists) and if it is set to null
     * then the search is based on the first argument (i.e. the searchdir).
     * This is useful for just lsiting the contents of the directory.</p
     * <p> The searchdir can contain "./" or "../" (unix) or ".\" "..\"
     * in dos.
     */
    public static Listing listFileNames(String searchdir, String searchFilter,
                                        boolean recurseDirs) {
        String absDir = absolutePath(searchdir);

        Listing allNames;

        allNames = listDir(absDir, recurseDirs); // contains high structure

        return (allNames == null) ? null : allNames.justFileList(searchFilter).pickOutMatching(searchFilter);
    }

    /**
     * Gets a Listing Object for the given directory, the given
     * searchstring (i.e. a wildcard based matcher) and a flag
     * indicating whether the FileList should recurse into
     * other directories or not.  </p>
     * <p> The searchFilter can be any unix style (or DOS style) search
     * expression when using <i>ls</i> or <i>dir</i>. This can also
     * be a file, directory or null.  If its a file then this class
     * can be used to determine whether the file exists or not (i.e.
     * if fileFound() == 0 or exists() == false then the file
     * does not exist. If searchFilter is a  directory then the
     * directory is listed (if it exists) and if it is set to null
     * then the search is based on the first argument (i.e. the searchdir).
     * This is useful for just lsiting the contents of the directory.</p
     * <p> The searchdir can contain "./" or "../" (unix) or ".\" "..\"
     * in dos.
     */
    public static Listing listDirNames(String searchdir, String searchFilter,
                                       boolean recurseDirs) {
        if (searchdir.indexOf("://") != -1) {
            if (!searchdir.endsWith("/"))
                searchdir = searchdir + "/";
        } else {
            if (!searchdir.endsWith(File.separator))
                searchdir = searchdir + File.separator;
        }

        String absDir = absolutePath(searchdir);

        Listing allNames;

        allNames = listDir(absDir, recurseDirs); // contains high structure

        return (allNames == null) ? null : allNames.justDirStructure(searchFilter).pickOutMatching(searchFilter);
    }


    /**
     * Gets a Listing Object for the given directory, the given
     * searchstring (i.e. a wildcard based matcher) and a flag
     * indicating whether the FileList should recurse into
     * other directories or not.  </p>
     * <p> The searchFilter can be any unix style (or DOS style) search
     * expression when using <i>ls</i> or <i>dir</i>. This can also
     * be a file, directory or null.  If its a file then this class
     * can be used to determine whether the file exists or not (i.e.
     * if fileFound() == 0 or exists() == false then the file
     * does not exist. If searchFilter is a  directory then the
     * directory is listed (if it exists) and if it is set to null
     * then the search is based on the first argument (i.e. the searchdir).
     * This is useful for just lsiting the contents of the directory.</p
     * <p> The searchdir can contain "./" or "../" (unix) or ".\" "..\"
     * in dos.
     */
    public static Listing listAllFiles(String searchdir,
                                       String searchFilter, boolean recurseDirs) {
        String absDir = absolutePath(searchdir);

        logger.fine("Listing directory: " + absDir);

        Listing allNames = listDir(absDir, recurseDirs);
        // contains high structure

        return (allNames == null) ? null : allNames.pickOutMatching(searchFilter);
    }


    /**
     * @return the absolute path of the given directory i.e. the
     *         path may contain many ../'s or ./'s in its path. This
     *         works out the correct path
     */
    public static String absolutePath(String path) {
        if (path.indexOf("://") != -1)
            return path;

        File f = new File(path);
        String s = null;
        try {
            s = f.getCanonicalPath();
        }
        catch (IOException ee) {
            logger.severe("Couldn't get absolue Path for " + path + ":" + formatThrowable(ee));
        }

        return s;
    }

    /**
     * Finds all file names in the specified directory and in
     * all directories lower down in the tree. This list includes
     * directories also and is highly structured. It contains
     * a Vector of File Object (representing file and directory)
     * names and Vectors containing listings of other directories.
     * These other such directories also have the same format.
     */
    public static Listing listDir(String dir, boolean recurse) {
        if (dir == null)
            return null;

        try {
            if (dir.indexOf("://") != -1)
                return listDir(new URL(dir), recurse);
        }
        catch (MalformedURLException murl) {

            logger.warning("Internet Address " + dir + " does not exist!");
            return null;
        }

        Listing filelist = new Listing(10);
        File file;

        String[] dirlist = listDir(dir);

        if (dirlist == null)
            return null;

        for (int i = 0; i < dirlist.length; ++i) {
            file = new File(dir + File.separator + dirlist[i]);
            filelist.addElement(file); // add it whether its a
            // directory or a normal file

            if ((file.isDirectory()) && (recurse)) {
                Listing f = listDir(file.getPath(), recurse);
                if (f != null) // don't add it if its empty
                    filelist.addElement(f);
            }
        }

        if (filelist.size() > 0)
            return filelist;
        else
            return null;
    }

    /**
     * returns a list of file names in the given directory
     * or returns null if the directory is empty or if it
     * does not exist.
     */
    public static String[] listDir(String dir) {
        if (dir == null)
            return null;

        try {
            if (dir.indexOf("://") != -1) {
                Vector<String> sv = listInternetDirectory(new URL(dir));
                String[] s = new String[sv.size()];
                for (int i = 0; i < sv.size(); ++i)
                    s[i] = sv.get(i);
                return s;
            }
        }
        catch (MalformedURLException murl) {
            logger.warning("Internet Address " + dir +
                    " does not exist!");
            return null;
        }

        File file = new File(dir);
        if ((!file.exists()) || (!file.isDirectory()))
            return null;

        String[] files = file.list();

        if ((files == null) || (files.length == 0))
            return null;
        else
            return files;

    }


    /**
     * Finds all file names in the specified directory and in
     * all directories lower down in the tree. This list includes
     * directories also and is highly structured. It contains
     * a Vector of File Object (representing file and directory)
     * names and Vectors containing listings of other directories.
     * These other such directories also have the same format.
     */
    public static Listing listDir(URL dir, boolean recurse) {
        try {
            dir.openConnection();
        }
        catch (Exception ee) {
            logger.warning("Internet Address " + dir + " does not exist!");
            return null;
        }

        if (!dir.toString().endsWith("/")) {
            try {
                dir = new URL(dir.toString() + "/");
            }
            catch (MalformedURLException murl) {
                // not possible, same directory!
            }
        }

        Listing filelist = new Listing(10);
        String file;
        URL furl;
        String sdir = dir.toString();

        Vector<String> dirlist = listInternetDirectory(dir);


        if (dirlist == null)
            return null;

        for (int i = 0; i < dirlist.size(); ++i) {
            if (!sdir.endsWith("/"))
                file = dir + "/" + dirlist.get(i);
            else
                file = dir + dirlist.get(i);

            filelist.addElement(file); // add it whether its a
            // directory or a normal file
            try {
                furl = new URL(file);
            }
            catch (MalformedURLException murl) {
                // not possible but :-
                logger.severe(file + "is not valid :" + formatThrowable(murl));
                return null;
            }

            if (FileUtils.isDirectory(furl) && (recurse)) {
                Listing f = listDir(furl, recurse);
                if (f != null) // don't add it if its empty
                    filelist.addElement(f);
            }
        }

        if (filelist.size() > 0)
            return filelist;
        else
            return null;
    }


    public static File[] listEndsWith(String baseDir, String endsWith) {
        return listEndsWith(baseDir, endsWith, new String[0]);
    }

    public static File[] listEndsWith(String baseDir, String endsWith, String[] excludedir) {
        ArrayList list = new ArrayList();

        list(baseDir, excludedir, endsWith, list);

        return (File[]) list.toArray(new File[list.size()]);
    }

    private static void list(String baseDir, String[] excludedir, String endsWith, ArrayList list) {
        File base = new File(baseDir);
        ExcludeFilter exfilter = new ExcludeFilter(excludedir);

        if (base.exists()) {
            File[] baseList = base.listFiles(exfilter);

            if (baseList != null)
                list(baseList, exfilter, new EndsWithFilter(endsWith), list);
        }
    }

    private static void list(File[] files, ExcludeFilter exfilter, Filter filter, ArrayList list) {
        File[] dirfiles;

        for (int count = 0; count < files.length; count++) {
            if (filter.matches(files[count]))
                list.add(files[count]);

            dirfiles = files[count].listFiles(exfilter);

            if (dirfiles != null)
                list(dirfiles, exfilter, filter, list);
        }
    }

    private static class ExcludeFilter implements FileFilter {

        private String[] exclude;

        public ExcludeFilter(String[] excludedir) {
            this.exclude = excludedir;
        }

        public boolean accept(File pathname) {
            boolean accept = true;

            for (int count = 0; (count < exclude.length) && (accept); count++)
                accept = (!pathname.getName().equalsIgnoreCase(exclude[count])) && accept;

            return accept;
        }
    }


    private static interface Filter {

        /**
         * @return true if the specified file matches the filter
         */
        public boolean matches(File file);

    }

    private static class EndsWithFilter implements Filter {

        private String endsWith;


        public EndsWithFilter(String endsWith) {
            this.endsWith = endsWith;
        }

        /**
         * @return true if the specified file matches the filter
         */
        public boolean matches(File file) {
            return file.getAbsolutePath().endsWith(endsWith);
        }

    }
}
