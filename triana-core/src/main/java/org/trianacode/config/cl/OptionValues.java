package org.trianacode.config.cl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 1, 2010
 */
public class OptionValues {

    private Map<String, OptionValue> values = new HashMap<String, OptionValue>();

    public OptionValues() {
    }

    public void addOptionValue(OptionValue value) {
        values.put(value.getOption(), value);
    }

    public boolean hasOption(String shortOpt) {
        return values.containsKey(shortOpt);
    }

    public boolean hasOptionValue(String shortOpt) {
        if (hasOption(shortOpt)) {
            OptionValue o = getOption(shortOpt);
            if (o.hasValue()) {
                return true;
            }
        }
        return false;
    }

    public String getOptionValue(String shortOpt) {
        if (hasOption(shortOpt)) {
            OptionValue o = getOption(shortOpt);
            if (o.hasValue()) {
                return o.getValue();
            }
        }
        return null;
    }

    public List<String> getOptionValues(String shortOpt) {
        if (hasOption(shortOpt)) {
            OptionValue o = getOption(shortOpt);
            if (o.hasValue()) {
                return o.getValues();
            }
        }
        return null;
    }

    public OptionValue getOption(String shortOpt) {
        return values.get(shortOpt);
    }
}
