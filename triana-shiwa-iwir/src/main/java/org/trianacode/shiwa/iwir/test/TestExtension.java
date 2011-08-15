//package org.trianacode.shiwa.iwir.test;
//
//import org.trianacode.gui.action.ToolSelectionHandler;
//import org.trianacode.gui.extensions.Extension;
//import org.trianacode.taskgraph.Task;
//import org.trianacode.taskgraph.tool.Tool;
//
//import javax.swing.*;
//
///**
// * Created by IntelliJ IDEA.
// * User: ian
// * Date: 18/07/2011
// * Time: 11:38
// * To change this template use File | Settings | File Templates.
// */
//public class TestExtension implements Extension {
//    @Override
//    public void init(ToolSelectionHandler selhandler) {
//    }
//
//    @Override
//    public Action getTreeAction(Tool tool) {
//        return null;
//    }
//
//    @Override
//    public Action getWorkspaceAction(Task tool) {
//        return null;
//    }
//
//    @Override
//    public Action getWorkflowAction(int type) {
//        if (type == Extension.TOOL_TYPE) {
//            return (Action) new TestAction();
//        } else return null;
//    }
//}
