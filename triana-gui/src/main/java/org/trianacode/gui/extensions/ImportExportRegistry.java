package org.trianacode.gui.extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 31, 2010
 */

public class ImportExportRegistry {

    private static List<TaskGraphExporterInterface> exporters = new ArrayList<TaskGraphExporterInterface>();
    private static List<TaskGraphImporterInterface> importers = new ArrayList<TaskGraphImporterInterface>();
    private static List<ToolImporterInterface> toolImporters = new ArrayList<ToolImporterInterface>();

    private ImportExportRegistry() {
    }

    public static void addExporter(TaskGraphExporterInterface exporter) {
        exporters.add(exporter);
    }

    public static void addImporter(TaskGraphImporterInterface importer) {
        importers.add(importer);
    }

    public static void addToolImporter(ToolImporterInterface importer) {
        toolImporters.add(importer);
    }

    public static List<TaskGraphExporterInterface> getExporters() {
        return Collections.unmodifiableList(exporters);
    }

    public static List<TaskGraphImporterInterface> getImporters() {
        return Collections.unmodifiableList(importers);
    }

    public static List<ToolImporterInterface> getToolImporters() {
        return Collections.unmodifiableList(toolImporters);
    }
}
