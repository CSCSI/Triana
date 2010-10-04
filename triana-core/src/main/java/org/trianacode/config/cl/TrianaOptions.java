package org.trianacode.config.cl;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class TrianaOptions {

    public static final Option NO_GUI_OPTION = new Option("n", "no-gui", "run with no user interface");
    public static final Option SERVER_OPTION = new Option("s", "server", "run triana server services (HTTP and mDNS)");
    public static final Option WORKFLOW_OPTION = new Option("w", "workflow", "workflows", "supply one or more workflows. only a single workflow is allowed in non-gui mode (-n)", true);
    public static final Option DATA_OPTION = new Option("d", "data-config", "data-config", "supply a data configuration file to a non-gui workflow");
    public static final Option LOG_LEVEL_OPTION = new Option("l", "log-level", "log-level", "0 (off) to 7 (all)");
    public static final Option ISOLATE_LOG_OPTION = new Option("i", "isolate-logger", "logger", "isolate a particular logger and shut down all others");
    public static final Option EXECUTE_OPTION = new Option("e", "execute", "workflow", "execute workflow (in non-gui mode)");
    public static final Option UUID_OPTION = new Option("u", "uuid", "uuid", "get status of running workflow (in non-gui mode). The -w option in non-gui mode returns a unique id for a workflow");
    public static final Option RESOLVE_THREAD_OPTION = new Option("t", "tool-thread", "periodically re-resolve tools");
    public static final Option HELP_OPTION = new Option("h", "help", "prints this message");

    public static Option[] TRIANA_OPTIONS = {
            NO_GUI_OPTION,
            SERVER_OPTION,
            WORKFLOW_OPTION,
            DATA_OPTION,
            LOG_LEVEL_OPTION,
            ISOLATE_LOG_OPTION,
            EXECUTE_OPTION,
            UUID_OPTION,
            RESOLVE_THREAD_OPTION,
            HELP_OPTION
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
