package org.trianacode.shiwaall.bundle;

import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.transfer.ExecutionListener;

import java.io.File;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 28/02/2012
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 *
 * @see BundleUnitEvent
 */
public class BundleUnitListener implements ExecutionListener {

    /** The file. */
    private File file;
    
    /** The transfer signature. */
    private TransferSignature transferSignature;

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.ExecutionListener#digestWorkflow(java.io.File, java.io.File, org.shiwa.desktop.data.description.handler.TransferSignature)
     */
    @Override
    public void digestWorkflow(File file, File file1, TransferSignature transferSignature) {

        this.file = file;
        this.transferSignature = transferSignature;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.ExecutionListener#ignoreBundle()
     */
    @Override
    public boolean ignoreBundle() {
        return false;
    }

    /**
     * Gets the transfer signature.
     *
     * @return the transfer signature
     */
    public TransferSignature getTransferSignature() {
        return transferSignature;
    }

    /**
     * Gets the file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }
}
