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
                        if (option.isRequiresValue()) {
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
                if (option.isRequiresValue()) {
                    if (!v.startsWith("<") && !v.endsWith(">")) {
                        v = "<" + v + ">";
                    }
                } else {
                    if (!v.startsWith("[") && !v.endsWith("]")) {
                        v = "[" + v + "]";
                    }
                }
            }
            sb.append("-")
                    .append(option.getShortOpt())
                    .append(" ");
            if (option.getLongOpt() != null && option.getLongOpt().length() > 0) {
                sb.append(" --").append(option.getLongOpt());
            }
            sb.append(" ")
                    .append(v);
            if (option.getDescription() != null && option.getDescription().length() > 0) {
                sb.append("\n\t\t");
                List<String> segs = split(option.getDescription(), 120);
                for (int i = 0; i < segs.size() - 1; i++) {
                    String seg = segs.get(i);
                    sb.append(seg).append("\n\t\t");
                }
                sb.append(segs.get(segs.size() - 1)).append("\n\t");
            } else {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static List<String> split(String text, int size) {
        List<String> ret = new ArrayList<String>();
        int start = 0;
        while (start < text.length()) {
            int nextSeg = start + size;
            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(start, Math.min(text.length(), nextSeg)).trim());
            while (nextSeg < text.length()) {
                char c = text.charAt(nextSeg);
                if (c != ' ' && c != '\n' && c != '\t') {
                    sb.append(c);
                    nextSeg++;
                } else {
                    break;
                }
            }
            start = nextSeg;
            ret.add(sb.toString());
        }
        return ret;
    }


}
