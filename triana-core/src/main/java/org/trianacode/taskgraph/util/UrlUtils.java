package org.trianacode.taskgraph.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 6, 2010
 */

public class UrlUtils {

    private UrlUtils() {
    }

    public static File getFile(URL url) {
        if (isFile(url)) {
            try {
                return new File(url.toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getExistingFile(String path) {
        return getExistingFile(toURL(path));
    }

    public static File getExistingFile(URL url) {
        if (isFile(url)) {
            try {
                File f = new File(url.toURI());
                if (f.exists() && f.length() > 0) {
                    return f;
                }
                return null;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isFile(Tool tool) {
        return tool.getDefinitionPath() != null && isFile(tool.getDefinitionPath());
    }


    public static boolean isFile(URL url) {
        if (url == null) {
            return false;
        }
        if (url.getProtocol() != null && url.getProtocol().length() > 0) {
            return url.getProtocol().equalsIgnoreCase("file");
        }
        return url.getAuthority() == null
                && url.getPath() != null;
    }

    public static boolean isJar(URL url) {
        if (url.getProtocol() != null && url.getProtocol().length() > 0) {
            if (url.getProtocol().equalsIgnoreCase("jar")) {
                return true;
            }
        }
        return url.getPath().endsWith(".jar");
    }

    public static boolean isHttp(URL url) {
        if (url.getProtocol() != null && url.getProtocol().length() > 0) {
            return url.getProtocol().equalsIgnoreCase("http");
        }
        return false;
    }

    public static URL fromFile(File f) {
        try {
            return f.toURI().toURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL toURL(String filePath) {
        try {
            return new File(filePath).toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
