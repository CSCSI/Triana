package org.trianacode.config.cl;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class TrianaOptions {

    public static final Option NO_GUI_OPTION = new Option("n", "no-gui", "run with no user interface");
    public static final Option SERVER_OPTION = new Option("s", "server", "run triana server services (HTTP)");
    public static final Option WORKFLOW_OPTION = new Option("w", "workflow", "workflows", "supply one or more workflows. only a single workflow is allowed in non-gui mode (-n)"
            + " In non-gui mode, the supplied workflow will be executed asychronously in a new JVM. The uuid of the executing workflow is returned immediately.", true, true);
    public static final Option DATA_OPTION = new Option("d", "data-config", "data-config", "supply a data configuration file to a non-gui workflow.");
    public static final Option LOG_LEVEL_OPTION = new Option("l", "log-level", "log-level", "0 (off) to 7 (all)");
    public static final Option ISOLATE_LOG_OPTION = new Option("i", "isolate-logger", "logger", "isolate a particular logger and shut down all others.");
    public static final Option EXECUTE_OPTION = new Option("e", "execute", "workflow", "execute workflow synchronously in non-gui mode");
    public static final Option OUTPUT_OPTION = new Option("o", "output", "output file", "output to file and optionally set the output directory (in non-gui mode)."
            + " If no output directory is specified then the default output directory is used (<Triana home directory>/runs/<uuid>).", false, false);
    public static final Option UUID_OPTION = new Option("u", "uuid", "uuid", "get status of running workflow (in non-gui mode). The -w option in non-gui mode returns a unique id for a workflow.");
    public static final Option RESOLVE_THREAD_OPTION = new Option("t", "tool-thread", "periodically re-resolve tools");
    public static final Option EXTRA_TOOLBOXES_OPTION = new Option("x", "extra-toolboxes", "add toolboxes to be resolved.");
    public static final Option EXTRA_MODULES_OPTION = new Option("m", "extra-modules", "add module paths.");
    public static final Option HELP_OPTION = new Option("h", "help", "prints this message.");

    public static final Option PLUGIN = new Option("p", "plugin", "plugin arguments", "runs plugin with iven arguments", true, false);
    public static final Option SUPPRESS_DEFAULT_TOOLBOXES = new Option("sdt", "suppress-default-toolboxes", "start triana with no default, only those given with -x");
    public static final Option CREATE_AND_SUBMIT_DAX = new Option("dax", "submit-to-pegasus", "Takes a workflow, creates a dax, and submits to Pegasus");
    public static final Option EXECUTE_BUNDLE = new Option("b", "bundle", "Input bundle");
    public static final Option OUTPUT_FORMAT = new Option("f", "output-format", "The format the workflow will be produced as after processing.");
    public static final Option RUN_UNIT = new Option("U", "execute-unit", "Execute a single unit, from the package name");
    public static final Option CONVERT_WORKFLOW = new Option("c", "convert-workflow", "languages", "No execution, converts between workflow languages", true, true);
    public static final Option INPUT_FILES = new Option("I", "input-files", "input files", "Serialized objects, one per file per input port", true, false);
    public static final Option OUTPUT_FILES = new Option("O", "output-files", "output-files", "The files the output objects will be serialized to, one file per output object", true, false);

    public static Option[] TRIANA_OPTIONS = {
            NO_GUI_OPTION,
            SERVER_OPTION,
            WORKFLOW_OPTION,
            DATA_OPTION,
            LOG_LEVEL_OPTION,
            ISOLATE_LOG_OPTION,
            EXECUTE_OPTION,
            OUTPUT_OPTION,
            UUID_OPTION,
            RESOLVE_THREAD_OPTION,
            EXTRA_TOOLBOXES_OPTION,
            EXTRA_MODULES_OPTION,
            HELP_OPTION,

            PLUGIN,
            SUPPRESS_DEFAULT_TOOLBOXES,
            CREATE_AND_SUBMIT_DAX,
            EXECUTE_BUNDLE,
            OUTPUT_FORMAT,
            CONVERT_WORKFLOW,
            RUN_UNIT,
            INPUT_FILES,
            OUTPUT_FILES
    };

    public static boolean hasOption(ArgumentParser parsed, Option o) {
        String shortOpt = o.getShortOpt();
        boolean has = parsed.isOption("-" + shortOpt);
        if (!has && o.getLongOpt() != null) {
            has = parsed.isOption("--" + o.getLongOpt());
        }
        return has;
    }

    public static List<String> getOptionValues(ArgumentParser parsed, Option o) {
        String shortOpt = o.getShortOpt();
        List<String> vals = parsed.getArgumentValues("-" + shortOpt);
        if (vals == null && o.getLongOpt() != null) {
            vals = parsed.getArgumentValues("--" + o.getLongOpt());
        }
        return vals;
    }

    public static String getOptionValue(ArgumentParser parsed, Option o) {
        String shortOpt = o.getShortOpt();
        String val = parsed.getArgumentValue("-" + shortOpt);
        if (val == null && o.getLongOpt() != null) {
            val = parsed.getArgumentValue("--" + o.getLongOpt());
        }
        return val;
    }
}
