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


import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class for handling temp files. This ensures that the user's temp directory gets cleaned of any WHIP
 * related files. Use this as you would the File method createTempFile(prefix, suffix);
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 11, 2007: 3:53:30 PM
 * @date $Date:$ modified by $Author:$
 * @todo Put your notes here...
 */
public class TempFileManager {

    /**
     * The prefix for the temp directory in the system temp directory
     */
    private final static String TEMP_DIR_PREFIX = "tmp-whip-";

    /**
     * The temp directory to generate all files in
     */
    private static File sTmpDir = null;

    public static File createTempFile(String prefix, String suffix)
            throws IOException {
        if (sTmpDir == null) {
            String tmpDirName = System.getProperty("java.io.tmpdir");
            File tmpDir = File.createTempFile(TEMP_DIR_PREFIX, ".tmp",
                    new File(tmpDirName));
            tmpDir.delete();
            File lockFile = new File(tmpDirName, tmpDir.getName() + ".lck");
            lockFile.createNewFile();
            lockFile.deleteOnExit();
            if (!tmpDir.mkdirs()) {
                throw new IOException("Unable to create temporary directory:"
                        + tmpDir.getAbsolutePath());
            }

            sTmpDir = tmpDir;
        }
        return File.createTempFile(prefix, suffix, sTmpDir);
    }


    static {
        FileFilter tmpDirFilter =
                new FileFilter() {
                    public boolean accept(File pathname) {
                        return (pathname.isDirectory() &&
                                pathname.getName().startsWith(TEMP_DIR_PREFIX));
                    }
                };

        String tmpDirName = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tmpDirName);
        File[] tmpFiles = tmpDir.listFiles(tmpDirFilter);
        for (int i = 0; i < tmpFiles.length; i++) {
            File tmpFile = tmpFiles[i];

            // Create a file to represent the lock and test.
            File lockFile = new File(tmpFile.getParent(), tmpFile.getName() + ".lck");
            if (!lockFile.exists()) {
                // Delete the contents of the directory since
                // it is no longer locked.

                try {
                    deleteFiles(tmpFile, true);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

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
}