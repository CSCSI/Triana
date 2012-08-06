//package org.trianacode.shiwaall.dax;
//
///**
// * @author Andrew Harrison
// * @version 1.0.0 Jul 16, 2010
// */
//
//public class FileArg implements Arg {
//
//    private String option = "";
//    private FileHolder value;
//
//    public FileArg(String option, FileHolder value) {
//        this.option = option;
//        this.value = value;
//    }
//
//    public FileArg(FileHolder value) {
//        this("", value);
//    }
//
//    public String getOption() {
//        return option;
//    }
//
//    public FileHolder getValue() {
//        return value;
//    }
//
//    public void setValue(FileHolder value) {
//        this.value = value;
//    }
//
//    public String asString() {
//
//        FileHolder holder = value;
//        if (holder == null) {
//            throw new RuntimeException("holder is null.");
//        }
//        if (holder.getRealName() == null) {
//            throw new RuntimeException("holder has not been filled with real value.");
//        }
//        String opt = "";
//        if(option != null) {
//            opt = option;
//        }
//        return opt + " <filename file=\"" + holder.getRealName() + "\" />";
//    }
//}
