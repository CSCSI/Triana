package org.trianacode.taskgraph.interceptor.execution;

import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.tool.Toolbox;

import java.net.URL;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 21, 2010
 */

public class PoisonTask implements Task {
    TrianaProperties properties;


    public PoisonTask(TrianaProperties properties) {
        this.properties = properties;
    }

    /**
     * Not sure what this does - how is this created etc ?
     *
     * @return
     */
    public TrianaProperties getProperties() {
        return properties;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setProperties(TrianaProperties properties) {
    }

    @Override
    public String getQualifiedTaskName() {
        return "Poison";
    }

    @Override
    public void updateProxy() {
    }

    @Override
    public TaskGraph getParent() {
        return null;
    }

    @Override
    public TaskGraph getUltimateParent() {
        return null;
    }

    @Override
    public String getInstanceID() {
        return null;
    }

    @Override
    public void addTaskListener(TaskListener listener) {
    }

    @Override
    public void removeTaskListener(TaskListener listener) {
    }

    @Override
    public Node addDataInputNode() throws NodeException {
        return null;
    }

    @Override
    public void removeDataInputNode(Node node) {
    }

    @Override
    public Node getDataInputNode(int index) {
        return null;
    }

    @Override
    public Node[] getDataInputNodes() {
        return new Node[0];
    }

    @Override
    public Node addDataOutputNode() throws NodeException {
        return null;
    }

    @Override
    public void removeDataOutputNode(Node node) {
    }

    @Override
    public Node getDataOutputNode(int index) {
        return null;
    }

    @Override
    public Node[] getDataOutputNodes() {
        return new Node[0];
    }

    @Override
    public ParameterNode addParameterInputNode(String paramname) throws NodeException {
        return null;
    }

    @Override
    public void removeParameterInputNode(ParameterNode node) {
    }

    @Override
    public ParameterNode getParameterInputNode(int index) {
        return null;
    }

    @Override
    public ParameterNode[] getParameterInputNodes() {
        return new ParameterNode[0];
    }

    @Override
    public ParameterNode addParameterOutputNode(String paramname) throws NodeException {
        return null;
    }

    @Override
    public void removeParameterOutputNode(ParameterNode node) {
    }

    @Override
    public ParameterNode getParameterOutputNode(int index) {
        return null;
    }

    @Override
    public ParameterNode[] getParameterOutputNodes() {
        return new ParameterNode[0];
    }

    @Override
    public Node[] getInputNodes() {
        return new Node[0];
    }

    @Override
    public Node getInputNode(int absoluteindex) {
        return null;
    }

    @Override
    public Node[] getOutputNodes() {
        return new Node[0];
    }

    @Override
    public Node getOutputNode(int absoluteindex) {
        return null;
    }

    @Override
    public void removeNode(Node node) {
    }

    @Override
    public boolean isRunContinuously() {
        return false;
    }

    @Override
    public void setRunContinuously(boolean state) {
    }

    @Override
    public void setDefaultNodeRequirement(String requirement) {
    }

    @Override
    public String getDefaultNodeRequirement() {
        return null;
    }

    @Override
    public void setNodeRequirement(int index, String requirement) {
    }

    @Override
    public String getNodeRequirement(int index) {
        return null;
    }

    @Override
    public int getExecutionRequestCount() {
        return 0;
    }

    @Override
    public int getExecutionCount() {
        return 0;
    }

    @Override
    public ExecutionState getExecutionState() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public void init() throws TaskException {
    }

    @Override
    public void setParent(TaskGraph taskgraph) {
    }

    @Override
    public int getNodeIndex(Node node) {
        return 0;
    }

    @Override
    public int getAbsoluteNodeIndex(Node node) {
        return 0;
    }

    @Override
    public boolean isDataInputNode(Node node) {
        return false;
    }

    @Override
    public boolean isDataOutputNode(Node node) {
        return false;
    }

    @Override
    public boolean isParameterInputNode(Node node) {
        return false;
    }

    @Override
    public boolean isParameterOutputNode(Node node) {
        return false;
    }

    @Override
    public void dispose() {
    }

    @Override
    public TaskGraphContext getContext() {
        return null;
    }

    @Override
    public Object getContextProperty(String name) {
        return null;
    }

    @Override
    public void setContextProperty(String name, Object value) {
    }


    @Override
    public String getSubTitle() {
        return null;
    }

    @Override
    public void setSubTitle(String title) {
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getToolName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getDisplayPackage() {
        return null;
    }

    @Override
    public String getDefinitionType() {
        return null;
    }

    @Override
    public String getToolPackage() {
        return null;
    }

    @Override
    public String getQualifiedToolName() {
        return null;
    }

    @Override
    public URL getDefinitionPath() {
        return null;
    }

    @Override
    public Toolbox getToolBox() {
        return null;
    }

    @Override
    public Proxy getProxy() {
        return null;
    }

    @Override
    public RenderingHint[] getRenderingHints() {
        return new RenderingHint[0];
    }

    @Override
    public RenderingHint getRenderingHint(String hint) {
        return null;
    }

    @Override
    public boolean isRenderingHint(String hint) {
        return false;
    }

    @Override
    public Object getParameter(String name) {
        return null;
    }

    @Override
    public String getParameterType(String name) {
        return null;
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    public boolean isParameterName(String name) {
        return false;
    }

    @Override
    public String getPopUpDescription() {
        return null;
    }

    @Override
    public String getHelpFile() {
        return null;
    }

    @Override
    public String[] getDataInputTypes() {
        return new String[0];
    }

    @Override
    public String[] getDataInputTypes(int node) {
        return new String[0];
    }

    @Override
    public String[] getDataOutputTypes() {
        return new String[0];
    }

    @Override
    public String[] getDataOutputTypes(int node) {
        return new String[0];
    }

    @Override
    public int getDataInputNodeCount() {
        return 0;
    }

    @Override
    public int getDataOutputNodeCount() {
        return 0;
    }

    @Override
    public int getParameterInputNodeCount() {
        return 0;
    }

    @Override
    public String getParameterInputName(int index) {
        return null;
    }

    @Override
    public boolean isParameterTriggerNode(int index) {
        return false;
    }

    @Override
    public int getParameterOutputNodeCount() {
        return 0;
    }

    @Override
    public String getParameterOutputName(int index) {
        return null;
    }

    @Override
    public int getInputNodeCount() {
        return 0;
    }

    @Override
    public int getOutputNodeCount() {
        return 0;
    }

    @Override
    public int getMinDataInputNodes() {
        return 0;
    }

    @Override
    public int getMaxDataInputNodes() {
        return 0;
    }

    @Override
    public int getDefaultDataInputNodes() {
        return 0;
    }

    @Override
    public int getMinDataOutputNodes() {
        return 0;
    }

    @Override
    public int getMaxDataOutputNodes() {
        return 0;
    }

    @Override
    public int getDefaultDataOutputNodes() {
        return 0;
    }

    @Override
    public String getExtension(String name) {
        return null;
    }

    @Override
    public String[] getExtensionNames() {
        return new String[0];
    }

    @Override
    public boolean isExtensionName(String name) {
        return false;
    }

    @Override
    public void setVersion(String version) {
    }

    @Override
    public void setToolName(String toolName) {
    }

    @Override
    public void setToolPackage(String pakageName) {
    }

    @Override
    public void setDefinitionPath(URL filepath) {
    }

    @Override
    public void setToolBox(Toolbox toolboxpath) {
    }

    @Override
    public void setDefinitionType(String type) {
    }

    @Override
    public void setProxy(Proxy proxy) throws TaskException {
    }

    @Override
    public void removeProxy() throws TaskException {
    }

    @Override
    public void addRenderingHint(RenderingHint hints) {
    }

    @Override
    public void removeRenderingHint(String hint) {
    }

    @Override
    public void setParameter(String name, Object value) {
    }

    @Override
    public void setParameterType(String name, String type) {
    }

    @Override
    public void removeParameter(String name) {
    }

    @Override
    public void setPopUpDescription(String name) {
    }

    @Override
    public void setHelpFile(String url) {
    }

    @Override
    public void setDataInputTypes(String[] types) {
    }

    @Override
    public void setDataOutputTypes(String[] types) {
    }

    @Override
    public void setParameterInputs(String[] names, boolean[] trigger) throws NodeException {
    }

    @Override
    public void setParameterOutputs(String[] names) throws NodeException {
    }

    @Override
    public void addExtension(String name, String extension) {
    }

    @Override
    public void removeExtension(String name) {
    }
}
