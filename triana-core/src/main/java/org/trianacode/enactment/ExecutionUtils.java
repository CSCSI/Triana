package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.config.cl.OptionValues;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 03/11/2011
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionUtils {
    public static Set<Object> getExecutionServices(TrianaInstance engine) {
        Set<Object> executionServices = engine.getExtensions(ExecutionService.class);
        System.out.println("Found " + executionServices.size() + " ExecutionServices");
        return executionServices;
    }

    public static ExecutionService getService(TrianaInstance engine, OptionValues vals) {
        for (Object service : getExecutionServices(engine)) {
            if (service instanceof ExecutionService) {
                ExecutionService executionService = ((ExecutionService) service);
                if (vals.hasOption(executionService.getShortOption())) {
                    System.out.println("Returning service " + executionService.getShortOption());
                    return (ExecutionService) service;
                }
            }
        }
        System.out.println("No executionService requested");
        return null;
    }

    public static ExecutionService getService(TrianaInstance engine, String longOpt) {
        for (Object service : getExecutionServices(engine)) {
            if (service instanceof ExecutionService) {
                ExecutionService executionService = ((ExecutionService) service);
                if (executionService.getLongOption().equals(longOpt)) {
                    System.out.println("Returning service " + executionService.getShortOption());
                    return (ExecutionService) service;
                }
            }
        }
        return null;
    }
}
