/*
 * Copyright 2004 - 2005 University of Cardiff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.trianacode.taskgraph.tool.creators.type;

import org.trianacode.taskgraph.util.UrlUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassParser {

    private static final int MAGIC = 0xCAFEBABE;
    private static final int CONSTANT_CLASS = 7,
            CONSTANT_FIELDREF = 9,
            CONSTANT_METHODREF = 10,
            CONSTANT_INTERFACE_METHODREF = 11,
            CONSTANT_STRING = 8,
            CONSTANT_INTEGER = 3,
            CONSTANT_FLOAT = 4,
            CONSTANT_LONG = 5,
            CONSTANT_DOUBLE = 6,
            CONSTANT_NAME_AND_TYPE = 12,
            CONSTANT_UTF8 = 1;

    private static Object DUMMY = new Object();


    public Map<String, ClassHierarchy> readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return readFile(file);

    }

    public Map<String, ClassHierarchy> readFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("Input file does not exist");
        }
        if (file.isDirectory() || file.getName().endsWith(".class")) {
            return analyseClassFile(file);
        } else if (file.getName().endsWith(".zip") || file.getName().endsWith(".jar")) {
            return analyseClassFiles(file.toURI().toURL(), new ZipInputStream(new FileInputStream(file)));
        } else {
            throw new IOException(file + " is an invalid file.");
        }
    }

    public Map<String, ClassHierarchy> readURL(URL url) throws IOException {
        Map<String, ClassHierarchy> map = new HashMap<String, ClassHierarchy>();
        if (UrlUtils.isFile(url)) {
            try {
                return readFile(new File(url.toURI()));
            } catch (URISyntaxException e) {
                //e.printStackTrace();
                return map;
            }
        }
        String path = url.getPath();

        if (path.endsWith(".class")) {
            ClassHierarchy ch = getClasses(url.openStream(), url.toString());
            if (ch != null) {
                map.put(ch.getName(), ch);
            }
            return map;
        } else if (path.endsWith(".zip") || path.endsWith(".jar")) {
            return analyseClassFiles(url, new ZipInputStream(url.openStream()));
        } else {
            throw new IOException(url + " is an invalid URL.");
        }

    }

    public Map<String, ClassHierarchy> analyseClassFile(File file) throws IOException {
        Map<String, ClassHierarchy> map = new HashMap<String, ClassHierarchy>();
        if (file.getName().startsWith(".") || file.getName().equals("CVS")) {
            return map;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File file, String s) {
                    if (file.getName().equals("lib")) {
                        return false;
                    }
                    return true;
                }
            });
            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory() || files[i].getName().endsWith(".class")) {
                    map.putAll(analyseClassFile(files[i]));
                } else if (files[i].getName().endsWith(".jar") || files[i].getName().endsWith(".zip")) {
                    map.putAll(analyseClassFiles(files[i].toURI().toURL(),
                            new ZipInputStream(new FileInputStream(files[i]))));
                }
            }
        } else {
            ClassHierarchy ch = extractClasses(file);
            if (ch != null) {
                map.put(ch.getName(), ch);
            }
        }
        return map;
    }

    public Map<String, ClassHierarchy> analyseClassFiles(URL path, ZipInputStream zipFile) throws IOException {
        Map<String, ClassHierarchy> strings = new HashMap<String, ClassHierarchy>();

        ZipEntry entry;
        while ((entry = zipFile.getNextEntry()) != null) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                byte[] buff = new byte[2048];
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                int c;
                while ((c = zipFile.read(buff)) != -1) {
                    bout.write(buff, 0, c);
                }
                ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
                ClassHierarchy ch = getClasses(bin, path.toString());
                if (ch != null) {
                    String jarpath = path.toString();
                    ch.setFile("jar:" + jarpath + "!/" + entry.getName());
                    strings.put(ch.getName(), ch);
                }

            }
        }
        return strings;
    }

    public ClassHierarchy extractClasses(File file) throws IOException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return getClasses(stream, UrlUtils.fromFile(file).toString());
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public ClassHierarchy getClasses(InputStream stream, String path) throws IOException {
        DataInputStream dataStream = new DataInputStream(stream);
        return getPoolClasses(dataStream, path);
    }

    public static boolean isPrimitive(String name) {
        return name.equals("boolean") ||
                name.equals("byte") ||
                name.equals("short") ||
                name.equals("char") ||
                name.equals("int") ||
                name.equals("long") ||
                name.equals("double") ||
                name.equals("float");
    }


    private ClassHierarchy getPoolClasses(DataInputStream stream, String path) throws IOException {
        HashMap<Integer, Integer> classRefs = new HashMap<Integer, Integer>();
        HashMap<Integer, UTF8Constant> names = new HashMap<Integer, UTF8Constant>();

        if (MAGIC != stream.readInt()) {
            throw new IOException("Not a class file: Cafe Babe is missing.");
        }
        stream.readUnsignedShort(); //minor version
        stream.readUnsignedShort(); //major version
        int i = 1;
        Object[] constants = new Object[stream.readUnsignedShort()];
        constants[0] = DUMMY;
        while (i < constants.length) {
            Object c = DUMMY;
            boolean skipIndex = false;
            int type = stream.readUnsignedByte();
            switch (type) {
                case CONSTANT_CLASS:
                    classRefs.put(i, stream.readUnsignedShort());
                    break;
                case CONSTANT_METHODREF:
                    stream.readUnsignedShort();
                    stream.readUnsignedShort();
                    break;
                case CONSTANT_INTERFACE_METHODREF:
                    stream.readUnsignedShort();
                    stream.readUnsignedShort();
                    break;
                case CONSTANT_FIELDREF:
                    stream.readUnsignedShort();
                    stream.readUnsignedShort();
                    break;
                case CONSTANT_NAME_AND_TYPE:
                    stream.readUnsignedShort();
                    stream.readUnsignedShort();
                    break;
                case CONSTANT_STRING:
                    stream.readUnsignedShort();
                    break;
                case CONSTANT_INTEGER:
                    stream.readInt();
                    break;
                case CONSTANT_FLOAT:
                    stream.readFloat();
                    break;
                case CONSTANT_LONG:
                    stream.readLong();
                    skipIndex = true;
                    break;
                case CONSTANT_DOUBLE:
                    stream.readDouble();
                    skipIndex = true;
                    break;
                case CONSTANT_UTF8:
                    names.put(i, new UTF8Constant(i, stream.readUTF()));
                    break;
                default:
                    break;
            }
            constants[i] = c;
            i++;
            if (skipIndex) {
                constants[i++] = c;
            }
        }
        int access = stream.readUnsignedShort(); //acces flags

        int me = stream.readUnsignedShort();
        Integer index = classRefs.get(me);
        if (index == null) {
            return null;
        }
        UTF8Constant utf = names.get(index);
        if (utf == null) {
            return null;
        }
        String[] currStrings = utf.getClasses();
        if (currStrings == null || currStrings.length != 1) {
            return null;
        }
        ClassHierarchy hier = new ClassHierarchy(createClassName(currStrings[0]));
        hier.setAccess(access);
        hier.setFile(path);
        int parent = stream.readUnsignedShort();
        index = classRefs.get(parent);
        if (index == null) {
            return hier;
        }
        utf = names.get(index);
        if (utf == null) {
            return hier;
        }
        currStrings = utf.getClasses();
        if (currStrings != null && currStrings.length == 1) {
            hier.setSuperClass(createClassName(currStrings[0]));
        }
        int numInterfaces = stream.readUnsignedShort();
        for (int k = 0; k < numInterfaces; k++) {
            int intf = stream.readUnsignedShort();
            index = classRefs.get(intf);
            if (index == null) {
                continue;
            }
            utf = names.get(index);
            if (utf == null) {
                continue;
            }
            currStrings = utf.getClasses();
            if (currStrings == null || currStrings.length != 1) {
                continue;
            }
            hier.addInterface(createClassName(currStrings[0]));

        }
        drainFields(stream);
        drainMethods(stream);
        int clsAttr = stream.readUnsignedShort();
        for (int k = 0; k < clsAttr; k++) {
            int ref = stream.readUnsignedShort();
            UTF8Constant constant = names.get(ref);
            String[] classes = constant.getClasses();
            if (classes[0].equals("RuntimeVisibleAnnotations")) {
                stream.readInt();
                int annoslength = stream.readUnsignedShort();
                for (int an = 0; an < annoslength; an++) {
                    int annoName = stream.readUnsignedShort();
                    UTF8Constant anno = names.get(annoName);
                    if (anno.getClasses()[0].equals("org/trianacode/taskgraph/annotation/Tool")) {
                        hier.setAnnotated(true);
                    }
                    drainAnnotation(stream);
                }
            } else {
                int attrLength = stream.readInt();
                for (int z = 0; z < attrLength; z++) {
                    stream.readByte();
                }
            }
        }

        return hier;
    }

    private void drainFields(DataInputStream stream) throws IOException {
        int numFields = stream.readUnsignedShort();
        for (int j = 0; j < numFields; j++) {
            stream.readUnsignedShort();
            stream.readUnsignedShort();
            stream.readUnsignedShort();
            int fieldAttrs = stream.readUnsignedShort();
            for (int k = 0; k < fieldAttrs; k++) {
                stream.readUnsignedShort();
                int attrLength = stream.readInt();
                for (int z = 0; z < attrLength; z++) {
                    stream.readByte();
                }
            }
        }
    }

    private void drainMethods(DataInputStream stream) throws IOException {
        int numMethods = stream.readUnsignedShort();
        for (int x = 0; x < numMethods; x++) {
            stream.readUnsignedShort();
            stream.readUnsignedShort();
            stream.readUnsignedShort();
            int methodsAttr = stream.readUnsignedShort();
            for (int k = 0; k < methodsAttr; k++) {
                stream.readUnsignedShort();
                int attrLength = stream.readInt();
                for (int z = 0; z < attrLength; z++) {
                    stream.readByte();
                }
            }
        }
    }

    // start at index 2 - after checking type
    private void drainAnnotation(DataInputStream stream) throws IOException {
        int elements = stream.readUnsignedShort();
        for (int i = 0; i < elements; i++) {
            stream.readUnsignedShort(); // index into pool
            stream.readByte();
            stream.readUnsignedShort();
        }
    }

    private String createClassName(String slashed) {
        return slashed.replace("/", ".");
    }


    private String parseClassFile(String classFile) {
        int start = 0;
        int end = classFile.length();
        if (classFile.indexOf(File.separator) != -1) {
            start = classFile.lastIndexOf(File.separator) + 1;
        }
        if (classFile.indexOf(".") != -1 && (classFile.substring(classFile.indexOf(".")).equals(".class"))) {
            end = classFile.indexOf(".");
        }
        return classFile.substring(start, end);
    }

    public static void main(String[] args) {
        try {
            Map<String, ClassHierarchy> hiers = new ClassParser().readFile(
                    "/Users/scmabh/work/projects/triana/code/triana/triana-pegasus/target/classes/org/trianacode/pegasus/dax/FileUnit.class");
            for (String hier : hiers.keySet()) {
                System.out.println(hiers.get(hier));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
