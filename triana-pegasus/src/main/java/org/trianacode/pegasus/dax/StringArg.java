//package org.trianacode.pegasus.dax;
//
///**
// * @author Andrew Harrison
// * @version 1.0.0 Jul 16, 2010
// */
//
//public class StringArg implements Arg {
//
//    private String option;
//    private String value;
//
//    public StringArg(String option, String value) {
//        this.option = option;
//        this.value = value;
//    }
//
//    public StringArg(String option) {
//        this.option = option;
//    }
//
//    public String asString() {
//        String opt = "";
//        if(option != null) {
//            opt = option ;
//        }
//        String val = "";
//        if(value != null && value.length() > 0) {
//            val = " " + value;
//        }
//        return opt + val;
//    }
//}
