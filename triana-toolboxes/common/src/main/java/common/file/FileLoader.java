package common.file;

import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 25, 2010
 * Time: 12:36:36 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass="common.file.FileLoaderPanel")
public class FileLoader {

    @TextFieldParameter
    private String filePath = "";

    @Process
    public File process(){

        if (!filePath.equals("")){
            File file = new File(filePath);
            if(file.exists()){
                return file;
            }
            return null;
        }
        return new File ("./diamond.dax");
    }
}
