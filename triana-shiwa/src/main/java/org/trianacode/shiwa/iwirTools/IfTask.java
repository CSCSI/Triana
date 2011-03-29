package org.trianacode.shiwa.iwirTools;

import org.trianacode.annotation.Process;
import org.trianacode.annotation.SliderParameter;
import org.trianacode.annotation.Tool;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class IfTask {

    @SliderParameter
    String slide = "0";

    @Process(gather = true)
    public Object process(List in) {
        for (Object object : in) {
            System.out.println("Incoming object");
        }
        return null;
    }
}
