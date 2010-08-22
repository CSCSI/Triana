package signalproc.output;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Exports data in row/column format to an ASCII file
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */


public class TableExporter extends Export {

    private PrintWriter writer;

    /**
     * This function is called when the unit is first created. It should be over-ridden to initialise the tool
     * properties (e.g. default number of nodes) and tool parameters.
     */
    public void init() {
        super.init();

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "File Name $title filename File null *.*\n";
        guilines += "Export $title export Choice [Columns] [Rows]\n";
        guilines += "Number of Columns/Rows $title colrownum TextField 1\n";
        guilines += "Input from multiple nodes $title multi Checkbox true\n";
        guilines += "Append sequence number (file1, file2...) $title seqnum Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Opens a writer to wrap the specified output stream
     *
     * @return the opened writer.
     */
    protected boolean openOutputStream(OutputStream outstream) {
        this.writer = new PrintWriter(outstream);
        return true;
    }

    /**
     * Closes and disposes the output stream
     */
    protected void closeOutputStream() {
        writer.close();
        writer = null;
    }


    /**
     * Write a row to the output stream
     */
    protected void writeRow(double[] data) {
        for (int count = 0; count < data.length; count++) {
            if (data[count] == Double.NaN) {
                writer.print("NaN");
            } else {
                writer.print(data[count]);
            }

            if (count < data.length - 1) {
                writer.print(' ');
            }
        }

        writer.println();
    }

}



