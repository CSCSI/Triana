package org.trianacode.shiwaall.utils;

import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 16/05/2012
 * Time: 02:39
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class FUBAR {


    @Process
    public void process() {
        throw new RuntimeException("Mwahahaha");
    }
}
