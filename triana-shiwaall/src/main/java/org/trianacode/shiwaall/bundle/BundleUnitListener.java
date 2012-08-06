//package org.trianacode.shiwaall.bundle;
//
//import org.shiwa.desktop.data.description.handler.TransferSignature;
//import org.shiwa.desktop.data.transfer.ExecutionListener;
//
//import java.io.File;
//
///**
// * Created by IntelliJ IDEA.
// * User: Ian Harvey
// * Date: 28/02/2012
// * Time: 17:00
// * To change this template use File | Settings | File Templates.
// */
//public class BundleUnitListener implements ExecutionListener {
//
//    private File file;
//    private TransferSignature transferSignature;
//
//    @Override
//    public void digestWorkflow(File file, File file1, TransferSignature transferSignature) {
//
//        this.file = file;
//        this.transferSignature = transferSignature;
//    }
//
//    @Override
//    public boolean ignoreBundle() {
//        return false;
//    }
//
//    public TransferSignature getTransferSignature() {
//        return transferSignature;
//    }
//
//    public File getFile() {
//        return file;
//    }
//}
