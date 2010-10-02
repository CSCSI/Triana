package common.string;

import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import java.util.Collection;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */

@Tool
public class Concat {

    @TextFieldParameter
    private String boundary = " ";

    @Process(gather = true)
    public String concat(Collection vals) {
        StringBuilder sb = new StringBuilder();

        for (Object val : vals) {
            sb.append(val.toString()).append(boundary);
        }
        String ret = sb.toString();
        return ret.substring(0, ret.length() - boundary.length());
    }
}
