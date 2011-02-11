package common.string;

import org.trianacode.annotation.Parameter;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.TextAreaParameter;
import org.trianacode.annotation.Tool;

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
