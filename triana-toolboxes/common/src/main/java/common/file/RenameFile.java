package common.file;

import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 18/04/2012
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class RenameFile {

    @TextFieldParameter
    private String filePath = "";

    @org.trianacode.annotation.Process
    public File process(File inputFile) {
        File outputFile = new File(inputFile.getParent(), filePath);
        inputFile.renameTo(outputFile);
        return outputFile;
    }
}
