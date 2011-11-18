package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.config.Locations;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.OptionValues;
import org.trianacode.config.cl.OptionsHandler;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.enactment.addon.BundleAddon;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.enactment.addon.ExecutionAddon;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    private Tool tool;
    private File configFile;
    private String bundleInputPath;


    public Convert(String[] args, OptionValues vals) throws Exception {
        TrianaInstance engine = new TrianaInstance(args);
        engine.addExtensionClass(CLIaddon.class);
        engine.init();

        initTool(engine, vals);

        if (tool == null) {
            System.out.println("No Tool found or created.");
            System.exit(1);
        } else {

            List<String> conversion = vals.getOptionValues("c");
            File outputConvertedFile = null;
            if (conversion != null) {
                String conversionString = conversion.get(0).toLowerCase();

                if (conversionString.equals(IWIR_FORMAT)) {

                    ConversionAddon conversionAddon = AddonUtils.getConversionAddon(engine, "taskgraph-to-iwir");
                    if (conversionAddon != null) {
                        File iwirFile = conversionAddon.toolToWorkflowFile(tool, configFile, "tempFile.xml");
                        System.out.println("Created iwir file : " + iwirFile.getAbsolutePath());
                    }

                } else if (conversionString.equals(DAX_FORMAT)) {

                    System.out.println("Will create dax file");

                    ConversionAddon daxifyAddon = (ConversionAddon) AddonUtils.getService(engine, "taskgraph-to-daxJobs");
                    ConversionAddon daxAddon = (ConversionAddon) AddonUtils.getService(engine, "convert-dax");
                    if (daxifyAddon != null && daxAddon != null) {

                        Tool daxifiedTaskgraph = (Tool) daxifyAddon.processWorkflow(tool);
                        System.out.println("Daxified taskgraph");
                        outputConvertedFile = daxAddon.toolToWorkflowFile(daxifiedTaskgraph, configFile, "exampleDax.dax");
                        System.out.println("Created dax file " + outputConvertedFile.getAbsolutePath());
                    } else {
                        System.out.println("Couldn't find required addons to create dax");
                    }

                } else {

                    conversionString = TASKGRAPH_FORMAT;

                    outputConvertedFile = File.createTempFile("publishedTaskgraphTemp", ".xml");

                    XMLWriter outWriter = new XMLWriter(new PrintWriter(System.out));
                    outWriter.writeComponent(tool);
                    XMLWriter fileWriter = new XMLWriter(new PrintWriter(outputConvertedFile));
                    fileWriter.writeComponent(tool);
                    System.out.println("File created : " + outputConvertedFile.getAbsolutePath());

                }

                if (conversion.size() > 1) {
                    if (conversion.get(1).toLowerCase().equals("bundle")) {
                        bundleOutputs(engine, outputConvertedFile);
                    }
                }
                engine.shutdown(0);
            }
        }
    }

    private void bundleOutputs(TrianaInstance engine, File outputConvertedFile) throws IOException {
        System.out.println("Will bundle outputs");

        if (bundleInputPath == null) {
            System.out.println("No input bundle recorded.");

        } else {
            System.out.println("Producing bundle output");
            BundleAddon bundleAddon = (BundleAddon) AddonUtils.getExecutionAddon(engine, "unbundle");
            bundleAddon.setWorkflowFile(bundleInputPath, outputConvertedFile);
            bundleAddon.saveBundle("BundleOutput.zip");
            System.out.println("Bundled");
        }
    }

    private void initTool(TrianaInstance engine, OptionValues vals) throws Exception {
        List<String> bundleInputs = vals.getOptionValues(TrianaOptions.EXECUTE_BUNDLE.getShortOpt());
        if (bundleInputs != null) {
            ExecutionAddon executionAddon = AddonUtils.getExecutionAddon(engine, "unbundle");
            System.out.println("Unbundling with " + executionAddon.getServiceName());
            bundleInputPath = bundleInputs.get(0);
            tool = executionAddon.getTool(engine, bundleInputPath);
            configFile = executionAddon.getConfigFile();
        } else {
            List<String> workflowInput = vals.getOptionValues(TrianaOptions.WORKFLOW_OPTION.getShortOpt());
            if (workflowInput != null) {
                XMLReader reader = new XMLReader(new FileReader(workflowInput.get(0)));
                tool = reader.readComponent(engine.getProperties());
            }
        }
    }

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
        new Convert(args, vals);
    }
}
