package common.string;

import org.trianacode.taskgraph.annotation.Parameter;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextAreaParameter;
import org.trianacode.taskgraph.annotation.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 18, 2010
 */

@Tool
public class AnnotatedStringGen {

    @TextAreaParameter
    private String str = "";

    @Parameter
    private String other = "other";

    @Process
    public String process() {
        return str;
    }
}
