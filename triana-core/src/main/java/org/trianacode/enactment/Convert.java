package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.config.Locations;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.OptionValues;
import org.trianacode.config.cl.OptionsHandler;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 03/11/2011
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
public class Convert {

    public static final String TASKGRAPH_FORMAT = "taskgraph";
    public static final String IWIR_FORMAT = "iwir";
    public static final String DAX_FORMAT = "dax";

    public static void convert(String[] args) throws Exception {
        String os = Locations.os();
        String usage = "./triana.sh";
        if (os.equals("windows")) {
            usage = "triana.bat";
        }
        OptionsHandler parser = new OptionsHandler(usage, TrianaOptions.TRIANA_OPTIONS);
        OptionValues vals = null;
        try {
            vals = parser.parse(args);
        } catch (ArgumentParsingException e) {
            System.out.println(e.getMessage());
            System.out.println(parser.usage());
            System.exit(0);
        }

        TrianaInstance engine = new TrianaInstance(args);
        engine.addExtensionClass(ExecutionService.class);
        engine.init();

        Tool tool = null;

        List<String> bundleInput = vals.getOptionValues(TrianaOptions.EXECUTE_BUNDLE.getShortOpt());
        if (bundleInput != null) {
            ExecutionService executionService = ExecutionUtils.getService(engine, "bundle");
            tool = executionService.getTool(engine, bundleInput.get(0));
        } else {
            List<String> workflowInput = vals.getOptionValues(TrianaOptions.WORKFLOW_OPTION.getShortOpt());
            if (workflowInput != null) {
                XMLReader reader = new XMLReader(new FileReader(workflowInput.get(0)));
                tool = reader.readComponent(engine.getProperties());
            }
        }
        if (tool == null) {
            System.out.println("No input specified");
            System.exit(1);
        }

        List<String> conversion = vals.getOptionValues("c");
        if (conversion != null) {
            String conversionString = conversion.get(0).toLowerCase();

            if (conversionString.equals(IWIR_FORMAT)) {

            } else if (conversionString.equals(DAX_FORMAT)) {
                ExecutionService executionService = ExecutionUtils.getService(engine, "taskgraph-to-daxJobs");
                Tool daxifiedTaskgraph = (Tool) executionService.getWorkflow(tool);
            } else {
                conversionString = TASKGRAPH_FORMAT;

                File temp = File.createTempFile("publishedTaskgraphTemp", ".xml");
                temp.deleteOnExit();

                XMLWriter outWriter = new XMLWriter(new PrintWriter(System.out));
                outWriter.writeComponent(tool);

                XMLWriter fileWriter = new XMLWriter(new PrintWriter(temp));
                fileWriter.writeComponent(tool);
                System.out.println("File created : " + temp.getAbsolutePath());
            }
            engine.shutdown(0);
        }


    }

}
