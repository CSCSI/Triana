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
 * User: Ian Harvey
 * Date: 03/11/2011
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
public class Convert {

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

                if (conversionString.equals(AddonUtils.IWIR_FORMAT)) {

                    ConversionAddon conversionAddon = (ConversionAddon) AddonUtils.getService(
                            engine, "iwir-converter", ConversionAddon.class
                    );

                    if (conversionAddon != null) {
                        File iwirFile = conversionAddon.toolToWorkflowFile(tool, configFile, "tempFile.xml");
                        System.out.println("Created iwir file : " + iwirFile.getAbsolutePath());
                    }

                } else if (conversionString.equals(AddonUtils.DAX_FORMAT)) {

                    System.out.println("Will create dax file");

                    ConversionAddon daxifyAddon = (ConversionAddon) AddonUtils.getService(engine, "taskgraph-to-daxJobs", ConversionAddon.class);
                    ConversionAddon daxAddon = (ConversionAddon) AddonUtils.getService(engine, "convert-dax", ConversionAddon.class);
                    if (daxifyAddon != null && daxAddon != null) {

                        Tool daxifiedTaskgraph = (Tool) daxifyAddon.processWorkflow(tool);
                        System.out.println("Daxified taskgraph");
                        outputConvertedFile = daxAddon.toolToWorkflowFile(daxifiedTaskgraph, configFile, "exampleDax.dax");
                        System.out.println("Created dax file " + outputConvertedFile.getAbsolutePath());
                    } else {
                        System.out.println("Couldn't find required addons to create dax");
                    }

                } else {

                    conversionString = AddonUtils.TASKGRAPH_FORMAT;

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
            BundleAddon bundleAddon = (BundleAddon) AddonUtils.getService(engine, "unbundle", BundleAddon.class);
            bundleAddon.setWorkflowFile(bundleInputPath, outputConvertedFile);
            bundleAddon.saveBundle("BundleOutput.zip");
            System.out.println("Bundled");
        }
    }

    private void initTool(TrianaInstance engine, OptionValues vals) throws Exception {
        List<String> bundleInputs = vals.getOptionValues(TrianaOptions.EXECUTE_BUNDLE.getShortOpt());
        if (bundleInputs != null) {
            ExecutionAddon executionAddon = (ExecutionAddon) AddonUtils.getService(engine, "unbundle", ExecutionAddon.class);
            System.out.println("Unbundling with " + executionAddon.getServiceName());
            bundleInputPath = bundleInputs.get(0);
            tool = executionAddon.getTool(engine, bundleInputPath);
            configFile = executionAddon.getConfigFile();
        } else {
            List<String> workflowInput = vals.getOptionValues(TrianaOptions.WORKFLOW_OPTION.getShortOpt());
            String type = null;
            if (workflowInput != null) {
                type = AddonUtils.getWorkflowType(new File(workflowInput.get(0)));
            }

            if (type == null) {
                return;
            } else if (type.equals(AddonUtils.TASKGRAPH_FORMAT)) {
                XMLReader reader = new XMLReader(new FileReader(workflowInput.get(0)));
                tool = reader.readComponent(engine.getProperties());
            } else if (type.equals(AddonUtils.IWIR_FORMAT)) {
                ConversionAddon conversionAddon = (ConversionAddon) AddonUtils.getService(engine, "iwir-converter", ConversionAddon.class);
                if (conversionAddon != null) {
                    tool = conversionAddon.workflowToTool(workflowInput.get(0));
                }
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
