package org.trianacode.pegasus.gui.guiUnits;

import org.trianacode.annotation.Tool;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/05/2011
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class TestUnit {

    @org.trianacode.annotation.Process(gather = true)
    public void testProcess(List list) {
        System.out.println("Test process running");
        for (Object object : list) {
            System.out.println("Object : " + object.toString());
        }
    }
}
