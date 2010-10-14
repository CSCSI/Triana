package org.trianacode.config.cl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 1, 2010
 */
public class OptionsHandler {

    private List<Option> options = new ArrayList<Option>();

    private ArgumentParser parser;

    private String usage = "";

    public OptionsHandler(String usage, Option... options) {
        if (usage != null) {
            this.usage = usage;
        }
        for (Option option : options) {
            this.options.add(option);
        }
    }

    public OptionsHandler(Option... options) {
        this("", options);
    }

    public OptionsHandler() {
        this("", new Option[0]);
    }

    public OptionValues parse(String[] args) throws ArgumentParsingException {
        parser = new ArgumentParser(args);
        parser.parse();
        return parse(parser.getArguments());
    }

    private OptionValues parse(Map<String, List<String>> args) throws ArgumentParsingException {
        OptionValues ovs = new OptionValues();
        for (String s : args.keySet()) {
            boolean known = false;
            if (options.size() == 0) {
                String sub;
                if (s.startsWith("--")) {
                    sub = s.substring(2, s.length());
                    ovs.addOptionValue(new OptionValue(sub, args.get(s)));
                }
                if (s.startsWith("-")) {
                    sub = s.substring(1, s.length());
                    ovs.addOptionValue(new OptionValue(sub, args.get(s)));
                }
            } else {
                for (Option option : options) {
                    String sub;
                    if (s.startsWith("--")) {
                        sub = s.substring(2, s.length());
                        if (option.getLongOpt().equals(sub)) {
                            known = true;
                        }
                    }
                    if (s.startsWith("-")) {
                        sub = s.substring(1, s.length());
                        if (option.getShortOpt().equals(sub)) {
                            known = true;
                        }
                    }
                    if (known) {
                        if (option.getValue() != null && option.getValue().length() > 0) {
                            if (args.get(s) == null || args.get(s).size() == 0) {
                                throw new ArgumentParsingException("Option " + s + " requires argument '" + option.getValue() + "'");
                            }
                        }
                        if (args.get(s) != null && args.get(s).size() > 1) {
                            if (!option.isMultiple()) {
                                throw new ArgumentParsingException("Option " + s + " can only have one argument. I received: " + args.get(s));
                            }
                        }
                        ovs.addOptionValue(new OptionValue(option.getShortOpt(), args.get(s)));
                        break;
                    }
                }
                if (!known) {
                    throw new ArgumentParsingException("unknown option:" + s);
                }
            }
        }
        return ovs;

    }

    public String usage() {
        StringBuilder sb = new StringBuilder("\nUsage:\t" + usage + " [ options ]\nOptions:\n\t");
        for (Option option : options) {
            String v = "";
            if (option.getValue() != null && option.getValue().length() > 0) {
                v = option.getValue();
                if (!v.startsWith("<") && !v.endsWith(">")) {
                    v = "<" + v + ">";
                }
            }

            sb.append("-")
                    .append(option.getShortOpt())
                    .append(" ");
            if (option.getLongOpt() != null && option.getLongOpt().length() > 0) {
                sb.append(" --").append(option.getLongOpt());
            }
            sb.append(" ")
                    .append(v)
                    .append("\n\t\t")
                    .append(option.getDescription())
                    .append("\n\t");
        }
        return sb.toString();
    }

}