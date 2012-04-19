package org.trianacode.enactment.plugins;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 17/04/2012
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */

import org.trianacode.TrianaInstance;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.OptionValues;
import org.trianacode.config.cl.OptionsHandler;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.enactment.addon.ExecutionAddon;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A class to run Triana with a specified plugin, discovered and run with the command line.
 * <p/>
 * The plugins must implement TrianaPlugin. args are run in order they are listed.
 * <p/>
 * -p [name-of-plugin] {task1, task2}
 * <p/>
 * eg triana.sh -n -p bundleplugin bundle.zip unbundle run bundleoutputs
 * <p/>
 * returns the exit code of the execution.
 */

public class Plugins {
    private OptionsHandler parser;
    private List<String> pluginArguments;
    private String[] args;

    public static int exec(String[] args) throws ArgumentParsingException, IOException, InterruptedException {

        Plugins plugins = new Plugins(args);
        return plugins.executeWithPlugins();
    }

    private Plugins(String[] args) throws ArgumentParsingException {
        this.args = args;
        parser = new OptionsHandler("Exec", TrianaOptions.TRIANA_OPTIONS);
        OptionValues vals = parser.parse(args);

        List<String> pluginArgs = vals.getOptionValues("p");
        if (pluginArgs != null) {
            pluginArguments = pluginArgs;
        }
    }

    private int executeWithPlugins() throws IOException, InterruptedException {
        TrianaInstance engine = new TrianaInstance(args);
        engine.addExtensionClass(CLIaddon.class);
        engine.init();
        System.out.println("Triana init complete...");

//        Thread.sleep(3000);

        Set<Object> addons = AddonUtils.getCLIaddons(engine);
        System.out.println(Arrays.toString(addons.toArray()));

        CLIaddon service = AddonUtils.getService(engine, pluginArguments.get(0), CLIaddon.class);
        if (service != null) {

            if (service instanceof ExecutionAddon) {
                System.out.println("Execution addon");

                ExecutionAddon executionAddon = (ExecutionAddon) service;

                try {
                    executionAddon.execute(engine, pluginArguments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (service instanceof ConversionAddon) {
                System.out.println("Conversion addon");
            }

        } else {
            System.out.println("No service plugin found matching " + pluginArguments.get(0));
        }

        //error
        return 1;
    }


//    private void executeBundle(String bundlePath, String data, String[] args) throws Exception {
//        System.out.println("Running a bundled workflow");
//        File f = new File(bundlePath);
//        if (!f.exists()) {
//            System.out.println("Cannot find bundle file:" + bundlePath);
//            System.exit(1);
//        }
////        TrianaInstance engine = new TrianaInstance(args);
////        engine.addExtensionClass(CLIaddon.class);
////        engine.init();
////        System.out.println("Triana init complete...");
//
//        Thread.sleep(3000);
//
//        ExecutionAddon executionAddon = (ExecutionAddon) AddonUtils.getService(engine, "unbundle", ExecutionAddon.class);
//
//        if (executionAddon != null) {
//            System.out.println("Running with " + executionAddon.getServiceName());
//            executionAddon.execute(this, engine, bundlePath, null, data, args);
//        } else {
//            System.out.println("Bundle executing service not found");
//        }
//        engine.shutdown(0);
//    }
}
