package common.string;

import org.trianacode.annotation.Tool;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 01/03/2012
 * Time: 22:57
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class ByteArrayToString {
    @org.trianacode.annotation.Process
    public String process(byte[] byteArray) {
        return new String(byteArray);
    }
}
