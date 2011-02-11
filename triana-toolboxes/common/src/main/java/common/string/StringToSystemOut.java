package common.string;

import org.trianacode.annotation.Tool;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 10, 2011
 * Time: 12:44:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class StringToSystemOut {

    @org.trianacode.annotation.Process(gather = true)
    public void process(List list) {

        for (Object o : list) {
            if (o instanceof String) {
                System.out.println("" + (String) o);
            }
        }
    }
}
