/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.gui.hci;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.FontUIResource;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ActionTable;
import org.trianacode.gui.action.MainTrianaKeyMapFactory;
import org.trianacode.gui.action.SelectionManager;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.action.ToolSelectionListener;
import org.trianacode.gui.action.clipboard.CopyAction;
import org.trianacode.gui.action.clipboard.CutAction;
import org.trianacode.gui.action.clipboard.PasteAction;
import org.trianacode.gui.action.files.CloseAction;
import org.trianacode.gui.action.files.ExportAction;
import org.trianacode.gui.action.files.FindAction;
import org.trianacode.gui.action.files.HelpAction;
import org.trianacode.gui.action.files.ImportAction;
import org.trianacode.gui.action.files.NewAction;
import org.trianacode.gui.action.files.OpenAction;
import org.trianacode.gui.action.files.PrintAction;
import org.trianacode.gui.action.files.RenderAction;
import org.trianacode.gui.action.files.SaveAction;
import org.trianacode.gui.action.files.SaveAsAction;
import org.trianacode.gui.action.files.TaskGraphFileHandler;
import org.trianacode.gui.action.taskgraph.ClearAction;
import org.trianacode.gui.action.taskgraph.GroupAction;
import org.trianacode.gui.action.taskgraph.OrganizeAction;
import org.trianacode.gui.action.taskgraph.PauseAction;
import org.trianacode.gui.action.taskgraph.ResetAction;
import org.trianacode.gui.action.taskgraph.RunAction;
import org.trianacode.gui.action.taskgraph.SelectAllAction;
import org.trianacode.gui.action.taskgraph.TrianaWorkflowVerifier;
import org.trianacode.gui.action.taskgraph.UnGroupAction;
import org.trianacode.gui.action.taskgraph.ZoomAction;
import org.trianacode.gui.action.taskgraph.ZoomOutAction;
import org.trianacode.gui.action.tools.AddTriggerAction;
import org.trianacode.gui.action.tools.CompileAction;
import org.trianacode.gui.action.tools.ControlPropertiesAction;
import org.trianacode.gui.action.tools.DecInNodeAction;
import org.trianacode.gui.action.tools.DecOutNodeAction;
import org.trianacode.gui.action.tools.DeleteAction;
import org.trianacode.gui.action.tools.DeleteRefsAction;
import org.trianacode.gui.action.tools.HistoryTrackingAction;
import org.trianacode.gui.action.tools.IncInNodeAction;
import org.trianacode.gui.action.tools.IncOutNodeAction;
import org.trianacode.gui.action.tools.NodeEditorAction;
import org.trianacode.gui.action.tools.PasteIntoAction;
import org.trianacode.gui.action.tools.PropertiesAction;
import org.trianacode.gui.action.tools.RemoveTriggerAction;
import org.trianacode.gui.action.tools.RenameAction;
import org.trianacode.gui.action.tools.RunScriptAction;
import org.trianacode.gui.action.tools.ToggerErrorAction;
import org.trianacode.gui.components.hidden.HiddenComponentModel;
import org.trianacode.gui.components.map.MapComponentModel;
import org.trianacode.gui.components.map.MapLocationComponentModel;
import org.trianacode.gui.components.script.ScriptColorModel;
import org.trianacode.gui.components.script.ScriptComponentModel;
import org.trianacode.gui.components.text.TextToolComponentModel;
import org.trianacode.gui.components.triana.TrianaColorModel;
import org.trianacode.gui.components.triana.TrianaComponentModel;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.gui.extensions.ExtensionFinder;
import org.trianacode.gui.extensions.ExtensionManager;
import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.hci.tools.BrokenToolMonitor;
import org.trianacode.gui.hci.tools.TaskGraphView;
import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.hci.tools.ToolTreeModel;
import org.trianacode.gui.hci.tools.TrianaTreeRenderer;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.panels.ParameterPanelManager;
import org.trianacode.gui.service.LocalServer;
import org.trianacode.gui.service.WorkflowActionManager;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.SplashScreen;
import org.trianacode.gui.windows.TrianaWindow;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.constants.HiddenToolConstants;
import org.trianacode.taskgraph.constants.MapConstants;
import org.trianacode.taskgraph.constants.ScriptConstants;
import org.trianacode.taskgraph.constants.TextToolConstants;
import org.trianacode.taskgraph.event.ControlTaskStateEvent;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskGraphCableEvent;
import org.trianacode.taskgraph.event.TaskGraphListener;
import org.trianacode.taskgraph.event.TaskGraphTaskEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.interceptor.InterceptorChain;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.ser.Base64ObjectDeserializer;
import org.trianacode.taskgraph.ser.ObjectDeserializationManager;
import org.trianacode.taskgraph.service.LocalDeployAssistant;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.ToolTableImp;
import org.trianacode.util.Env;


/**
 * @author Mathew Shields & Ian Taylor
 * @version $Revision: 4051 $
 */
public class ApplicationFrame extends TrianaWindow
        implements TaskListener, TaskGraphListener, InternalFrameListener,
        ToolSelectionHandler, SelectionManager, TreeModelListener,
        ComponentListener, LocalDeployAssistant, FocusListener {

    static Logger logger = Logger.getLogger("org.trianacode.gui.hci.ApplicationFrame");

    /**
     * tool tip delays
     */
    private static int TOOL_TIP_SHOW_DELAY = ToolTipManager.sharedInstance().getInitialDelay();
    private static int TOOL_TIP_HIDE_DELAY = Integer.MAX_VALUE;


    /**
     * the current loaded tools
     */
    private ToolTable tools;


    /**
     * The manager responsible for instantiating parameter panels
     */
    private ParameterPanelManager panelmanager = new ParameterPanelManager();

    /**
     * a hashtable of the Triana Clients for each taskgraph
     */
    private Hashtable clienttable = new Hashtable();


    /**
     * an array list of all the top-level main trianas
     */
    private ArrayList parents = new ArrayList();

    /**
     * the main workspace containing the tools panel and the main trianas
     */
    private JScrollableDesktopPane workspace = null;

    /**
     * The leaf listener that handles all mouse events on desktop panes.
     */
    private LeafListener leaflistener;


    // a lookup table for MainTriana object and their containers
    private static Hashtable taskGraphConts = new Hashtable();

    private TaskGraphFileHandler taskGraphFileHandler = null;
    private JTree toolboxTree;

    /**
     * a thread that monitors which tools are broken
     */
    private BrokenToolMonitor toolmonitor;

    /**
     * the currently selected component (tool tree/main triana)
     */
    private Object selected;


    /**
     * Initialise the application
     */
    public static ApplicationFrame initTriana() {
        // todo: this is crap, use andrew's UI stuff
        UIDefaults uiDefaults = UIManager.getDefaults();
        Object font = ((FontUIResource) uiDefaults.get("TextArea.font")).deriveFont((float) 11);

        Enumeration enumeration = uiDefaults.keys();
        while (enumeration.hasMoreElements()) {
            Object key = enumeration.nextElement();

            if (key.toString().endsWith("font")) {
                uiDefaults.put(key, font);
            }
        }

        ApplicationFrame app = new ApplicationFrame("Triana");
        app.init();

        return app;
    }


    /**
     * Constructor for the ApplicationFrame object
     */
    private ApplicationFrame(String title) {
        super(title);
    }

    private void init() {
        try {
            logger.info("Initialising");
            SplashScreen splash = new SplashScreen();
            splash.showSplashScreen(5);

            initObjectDeserializers();


            splash.setSplashProgress(Env.getString("toolsInitLabel"));
            ProxyFactory.initProxyFactory();
            TaskGraphManager.initTaskGraphManager();
            initTools();
            initActionTable();
            initWorkflowVerifiers();
            initMonitors();
            initExtensions();

            splash.setSplashProgress(Env.getString("prefsLabel"));
            Env.initConfig(tools, true);

            splash.setSplashProgress(Env.getString("uiLabel"));
            initLayout();

            splash.setSplashProgress(Env.getString("stateLabel"));
            Env.readStateFiles();

            splash.setSplashProgress(Env.getString("toolLoadLabel"));
            initWindow(super.getTitle());

            addParentTaskGraphPanel();

            splash.setSplashProgress("");
            splash.hideSplashScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObjectDeserializers() {
        ObjectDeserializationManager.registerObjectDeserializer(Base64ObjectDeserializer.BASE64_OBJECT_DESERIALIZER,
                new Base64ObjectDeserializer());
    }

    /**
     * @return the cuurently loaded tool table
     */
    ToolTable getTools() {
        return tools;
    }

    /**
     * @return the MenuBar for the main application
     */
    public TrianaMainMenu getTrianaMenuBar() {
        return (TrianaMainMenu) trianaMenuBar;
    }

    /**
     * @return the handler for opening/saving taskgraphs
     */
    public TaskGraphFileHandler getTaskGraphFileHandler() {
        return taskGraphFileHandler;
    }

    /**
     * Initialises the main window
     */
    private void initWindow(String title) {
        setName(title);

        setSize(Env.getWindowSize());
        setLocation(Env.getWindowPosition());

        addComponentListener(this);

        setVisible(true);
        getTrianaMenuBar().updateRecentMenu();
    }


    /**
     * Initialise the actions in the ActionTable
     */
    private void initActionTable() {
        logger.fine("Init");
        ActionTable.putAction(ActionTable.NEW_ACTION, new NewAction());
        ActionTable.putAction(ActionTable.OPEN_ACTION, new OpenAction(this));
        ActionTable.putAction(ActionTable.OPEN_FILE_ACTION, new OpenAction(this, OpenAction.FILE_ONLY_MODE));
        ActionTable.putAction(ActionTable.SAVE_ACTION, new SaveAction(this, tools));
        ActionTable.putAction(ActionTable.SAVE_AS_ACTION, new SaveAsAction(this, tools));
        ActionTable.putAction(ActionTable.CLOSE_ACTION, new CloseAction());
        ActionTable.putAction(ActionTable.IMPORT_ACTION, new ImportAction());
        ActionTable.putAction(ActionTable.EXPORT_ACTION, new ExportAction());
        ActionTable.putAction(ActionTable.PRINT_ACTION, new PrintAction(this));
        ActionTable.putAction(ActionTable.FIND_ACTION, new FindAction(this));
        ActionTable.putAction(ActionTable.HELP_ACTION, new HelpAction(this));
        ActionTable.putAction(ActionTable.RENDER_ACTION, new RenderAction(this));

        ActionTable.putAction(ActionTable.CUT_ACTION, new CutAction(this));
        ActionTable.putAction(ActionTable.COPY_ACTION, new CopyAction(this));
        ActionTable.putAction(ActionTable.PASTE_ACTION, new PasteAction(this));
        ActionTable.putAction(ActionTable.PASTE_INTO_ACTION, new PasteIntoAction(this, tools));
        ActionTable.putAction(ActionTable.DELETE_ACTION, new DeleteAction(this));
        ActionTable.putAction(ActionTable.DELETE_REFERENCES_ACTION, new DeleteRefsAction(this));
        ActionTable.putAction(ActionTable.RENAME_ACTION, new RenameAction(this));

        ActionTable.putAction(ActionTable.PROERTIES_ACTION, new PropertiesAction(this));
        ActionTable.putAction(ActionTable.CONTROL_PROERTIES_ACTION, new ControlPropertiesAction(this));
        ActionTable.putAction(ActionTable.NODE_EDITOR_ACTION, new NodeEditorAction(this));
        ActionTable.putAction(ActionTable.HISTORY_TRACKING_ACTION, new HistoryTrackingAction(this));

        ActionTable.putAction(ActionTable.GROUP_ACTION, new GroupAction(this, tools));
        ActionTable.putAction(ActionTable.UNGROUP_ACTION, new UnGroupAction(this));
        ActionTable.putAction(ActionTable.SELECT_ALL_ACTION, new SelectAllAction(this));
        ActionTable.putAction(ActionTable.CLEAR_ACTION, new ClearAction(this));
        ActionTable.putAction(ActionTable.ORGANIZE_ACTION, new OrganizeAction(this));

        ActionTable.putAction(ActionTable.ZOOMIN_ACTION, new ZoomAction(this));
        ActionTable.putAction(ActionTable.ZOOMOUT_ACTION, new ZoomOutAction(this));

        ActionTable.putAction(ActionTable.RUN_ACTION, new RunAction(this));
        ActionTable.putAction(ActionTable.PAUSE_ACTION, new PauseAction(this));
        ActionTable.putAction(ActionTable.RESET_ACTION, new ResetAction(this));

        ActionTable.putAction(ActionTable.COMPILE_ACTION, new CompileAction(this, tools));

        ActionTable.putAction(ActionTable.RUN_SCRIPT_ACTION, new RunScriptAction(this, tools));

        ActionTable.putAction(ActionTable.DEC_INPUT_NODES_ACTION, new DecInNodeAction(this));
        ActionTable.putAction(ActionTable.DEC_OUTPUT_NODES_ACTION, new DecOutNodeAction(this));
        ActionTable.putAction(ActionTable.INC_INPUT_NODES_ACTION, new IncInNodeAction(this));
        ActionTable.putAction(ActionTable.INC_OUTPUT_NODES_ACTION, new IncOutNodeAction(this));
        ActionTable.putAction(ActionTable.ADD_TRIGGER_NODE_ACTION, new AddTriggerAction(this));
        ActionTable.putAction(ActionTable.REMOVE_TRIGGER_NODE_ACTION, new RemoveTriggerAction(this));
        ActionTable.putAction(ActionTable.TOGGLE_ERROR_NODE_ACTION, new ToggerErrorAction(this));
    }

    /**
     * Initialise the actions in the ActionTable
     */
    private void initWorkflowVerifiers() {
        logger.fine("Init");
        WorkflowActionManager.registerWorkflowAction(new TrianaWorkflowVerifier());
        //WorkflowActionManager.registerWorkflowAction(new ProtoServiceWorkflowVerifier());
    }

    /**
     * Initialise the application monitors
     */
    private void initMonitors() {
        logger.fine("Init");
        //new ServicesMonitor();
    }

    /**
     * Discover and initialize the extension classes and populate the extension manager
     */
    private void initExtensions() {
        List ext = new ArrayList<Class>();
        ext.add(Extension.class);
        ext.add(Interceptor.class);
        Map<Class, List<Object>> en = ExtensionFinder.services(ext);
        Set<Class> keys = en.keySet();
        for (Class key : keys) {
            if (key.equals(Extension.class)) {
                List<Object> exts = en.get(key);
                for (Object o : exts) {
                    Extension e = (Extension) o;
                    e.init(this);
                    ExtensionManager.registerExtension(e);
                }
            } else if (key.equals(Interceptor.class)) {
                List<Object> exts = en.get(key);
                for (Object o : exts) {
                    Interceptor e = (Interceptor) o;
                    InterceptorChain.register(e);
                }
            }
        }

    }


    /**
     * Initialises the panels in the main window
     */
    private void initLayout() {

        ColorManager.setDefaultColorModel(new TrianaColorModel());
        ColorManager.registerColorModel(ScriptConstants.SCRIPT_RENDERING_HINT, new ScriptColorModel());

        TaskGraphView defaultview = new TaskGraphView("Default View");
        TrianaComponentModel compmodel = new TrianaComponentModel(tools, this, this);

        defaultview.setDefaultToolModel(compmodel);
        defaultview.setDefaultOpenGroupModel(compmodel);
        defaultview.registerToolModel(ScriptConstants.SCRIPT_RENDERING_HINT, new ScriptComponentModel());
        defaultview.registerToolModel(TextToolConstants.TEXT_TOOL_RENDERING_HINT, new TextToolComponentModel());
        defaultview.registerToolModel(HiddenToolConstants.HIDDEN_RENDERING_HINT, new HiddenComponentModel());

        TaskGraphView mapview = new TaskGraphView("Map View", defaultview);
        mapview.registerOpenGroupModel(MapConstants.MAP_RENDERING_HINT, new MapComponentModel());
        mapview.registerToolModel(MapConstants.MAP_LOCATION_RENDERING_HINT, new MapLocationComponentModel());

        TaskGraphViewManager.setDefaultTaskGraphView(defaultview);
        TaskGraphViewManager.registerTaskGraphView(MapConstants.MAP_RENDERING_HINT, mapview);

        taskGraphFileHandler = new TaskGraphFileHandler();

        trianaMenuBar = new TrianaMainMenu(this, tools);
        this.setJMenuBar(trianaMenuBar);

        TrianaShutdownHook shutDownHook = new TrianaShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutDownHook);

        workspace = new JScrollableDesktopPane(trianaMenuBar);

        ((TrianaMainMenu) trianaMenuBar).addHelp();
        GUIEnv.setApplicationFrame(this);

        ToolTreeModel treemodel = new ToolTreeModel(tools);
        toolboxTree = new JTree(treemodel);
        toolboxTree.addFocusListener(this);
        toolboxTree.setCellRenderer(new TrianaTreeRenderer());
        toolmonitor.setTree(toolboxTree);

        treemodel.addTreeModelListener(this);

        ToolTipManager.sharedInstance().registerComponent(toolboxTree);
        ToolTipManager.sharedInstance().setInitialDelay(TOOL_TIP_SHOW_DELAY);
        ToolTipManager.sharedInstance().setDismissDelay(TOOL_TIP_HIDE_DELAY);

        //set up key maps
        MainTrianaKeyMapFactory keymaps = new MainTrianaKeyMapFactory(this, ActionDisplayOptions.DISPLAY_NAME);
        InputMap inputMap = keymaps.getInputMap();
        inputMap.setParent(this.getRootPane().getInputMap());
        this.getRootPane().setInputMap(JComponent.WHEN_FOCUSED, inputMap);
        ActionMap actMap = keymaps.getActionMap();
        actMap.setParent(this.getRootPane().getActionMap());
        this.getRootPane().setActionMap(actMap);

        leaflistener = new LeafListener(toolboxTree, workspace, tools);
//        keymaps = new MainTrianaKeyMapFactory(leaflistener, ActionDisplayOptions.DISPLAY_NAME);
//        inputMap = keymaps.getInputMap();
//        inputMap.setParent(toolboxTree.getInputMap());
//        toolboxTree.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
//        actMap = keymaps.getActionMap();
//        actMap.setParent(toolboxTree.getActionMap());
//        toolboxTree.setActionMap(actMap);

        toolboxTree.addMouseListener(leaflistener);
        toolboxTree.addMouseMotionListener(leaflistener);
        JPanel toolPanel = new JPanel(new BorderLayout());

        SearchToolBar searchtoolbar = new SearchToolBar("Search", toolboxTree, treemodel);
        searchtoolbar.setFloatable(false);

        toolPanel.add(searchtoolbar, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(toolboxTree);
        toolPanel.add(scroll, BorderLayout.CENTER);

        JScrollPane scrollWork = new JScrollPane(workspace);
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                toolPanel,
                scrollWork);

        TrianaToolBar toolbar = new TrianaToolBar("Main ToolBar", this);
        TrianaUnitToolBar unitToolbar = new TrianaUnitToolBar("Unit ToolBar");

        toolbar.setRollover(true);
        unitToolbar.setRollover(true);

        JPanel innerpanel = new JPanel(new BorderLayout());
        innerpanel.add(unitToolbar, BorderLayout.PAGE_START);
        innerpanel.add(verticalSplit, BorderLayout.CENTER);

        JPanel outerpanel = new JPanel(new BorderLayout());
        outerpanel.add(toolbar, BorderLayout.PAGE_START);
        outerpanel.add(innerpanel, BorderLayout.CENTER);

        getContentPane().add(outerpanel);
    }

    /**
     * Initialises the tool table
     */
    public void initTools() {
        logger.fine("Init");
        tools = new ToolTableImp();
        TaskGraphManager.initToolTable(tools);

        tools.init();

        toolmonitor = new BrokenToolMonitor(tools);
        toolmonitor.start();

        /*
        TODO
        ServiceManager.registerImporter(new WSServiceImporter(tools));
        ServiceManager.registerDeployer(new GAPServiceDeployer(GAPPeerTypes.WEB_SERVICES, tools, new WSDeployAssistant(), this));
        ServiceManager.registerImporter(new P2PSServiceImporter(tools));
        ServiceManager.registerDeployer(new GAPServiceDeployer(GAPPeerTypes.P2PS, tools, new P2PSDeployAssistant(), this));
    */
    }


    /**
     * Adds a listener to be notified when the tool selection changes
     */
    public void addToolSelectionListener(ToolSelectionListener listener) {
    }

    /**
     * Removes a listener from being notified when the tool selection changes
     */
    public void removeToolSelectionListener(ToolSelectionListener listener) {
    }


    /**
     * Close the selected MainTriana window.
     * <p/>
     * TODO add checks for saved or not, finalise and tidy up graph etc.
     */
    void closeSelectedWindow() {
        closeTaskGraphPanel(getSelectedTaskGraphPanel());
    }

    /**
     * Closes the specified main triana and cleans-up the taskgraph if required
     */
    public void closeTaskGraphPanel(TaskGraphPanel cont) {
        disposeTaskGraphPanel(cont);
    }

    /**
     * @return an array all the taskgraph panels that are open within the application
     */
    public static TaskGraphPanel[] getTaskGraphPanels() {
        return (TaskGraphPanel[]) taskGraphConts.values().toArray(new TaskGraphPanel[taskGraphConts.values().size()]);
    }

    /**
     * @return an array of all taskgraph panels with no parents
     */
    public TaskGraphPanel[] getRootTaskGraphPanels() {
        Vector result = new Vector();
        for (Iterator iterator = taskGraphConts.values().iterator(); iterator.hasNext();) {
            TaskGraphPanel comp = (TaskGraphPanel) iterator.next();
            if (comp.getTaskGraph().getParent() == null) {
                result.add(comp);
            }
        }
        return (TaskGraphPanel[]) result.toArray(new TaskGraphPanel[result.size()]);
    }


    /**
     * @param parent the taskgraph panel to find children for
     * @return the child taskgraph panels
     */
    public TaskGraphPanel[] getChildTaskGraphPanels(TaskGraphPanel parent) {
        Vector result = new Vector();
        for (Iterator iterator = taskGraphConts.values().iterator(); iterator.hasNext();) {
            TaskGraphPanel cont = (TaskGraphPanel) iterator.next();
            if (cont.getTaskGraph().getParent() == parent.getTaskGraph()) {
                result.add(cont);
            }
        }
        return (TaskGraphPanel[]) result.toArray(new TaskGraphPanel[result.size()]);
    }

    /**
     * @return the taskgraph panel which is representing the specified task graph, or null if the task isn't
     *         represented
     */
    public static TaskGraphPanel getTaskGraphPanelFor(TaskGraph group) {
        Enumeration enumeration = taskGraphConts.elements();
        TaskGraphPanel element = null;

        while (enumeration.hasMoreElements() && ((element == null) || (element.getTaskGraph() != group))) {
            element = (TaskGraphPanel) enumeration.nextElement();
        }

        if ((element != null) && (element.getTaskGraph() == group)) {
            return element;
        } else {
            return null;
        }
    }

    /**
     * Gets the taskgraph panel which is conatined within the given JInternalFrame
     */
    public static TaskGraphPanel getTaskGraphPanelFor(JInternalFrame fr) {
        return (TaskGraphPanel) taskGraphConts.get(fr);
    }


    /**
     * Add a blank taskgraph panel
     */
    public TaskGraph addParentTaskGraphPanel() {
        try {
            return addParentTaskGraphPanel(TaskGraphManager.createTaskGraph());
        } catch (TaskException except) {
            ErrorDialog.show(this, "Error Creating Parent TaskGraph", except);
            return null;
        }
    }

    /**
     * Add a taskgraph panel for the specified taskgraph. This method creates a new instance of the specified taskgraph
     * using the current taskgraph factory
     *
     * @return the instance of taskgraph created using the current taskgraph factory
     */
    public TaskGraph addParentTaskGraphPanel(TaskGraph initgraph) {
        String factoryType = TaskGraphManager.DEFAULT_FACTORY_TYPE;
        try {
            return addParentTaskGraphPanel(initgraph, factoryType);
        }
        catch (TaskException e) {
            ErrorDialog.show(this, "Error Rendering TaskGraph: " + initgraph.getToolName(), e);
            return null;
        }
    }

    public TaskGraph addNoExecParentTaskGraphPanel(TaskGraph initgraph) {
        String factoryType = TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE;
        try {
            return addParentTaskGraphPanel(initgraph, factoryType);
        }
        catch (TaskException e) {
            ErrorDialog.show(this, "Error Rendering TaskGraph: " + initgraph.getToolName(), e);
            return null;
        }
    }

    private TaskGraph addParentTaskGraphPanel(TaskGraph initgraph, String factoryType) throws TaskException {
        TaskGraph taskgraph = (TaskGraph) TaskGraphManager.createTask(initgraph, factoryType, false);

        LocalServer server = new LocalServer(taskgraph, tools);
        TaskGraphManager.setTrianaServer(taskgraph, server);

        if ((taskgraph.getToolName() == null) || taskgraph.getToolName().equals("")) {
            String name = getNextUntitledName();
            taskgraph.setToolName(name);
        }

        TaskGraphPanel parent = addChildTaskGraphPanel(taskgraph, server);
        parents.add(parent);

        return taskgraph;
    }

    /**
     * Add an taskgraph panel for a child taskgraph to the workspace.
     */
    public TaskGraphPanel addChildTaskGraphPanel(final TaskGraph taskgraph, TrianaClient client) {
        registerTrianaClient(taskgraph, client);

        final TaskGraphPanel panel = TaskGraphViewManager.getTaskGraphPanel(taskgraph, client);
        panelmanager.monitorTaskGraph(taskgraph);
        panel.getContainer().addFocusListener(this);
        new ToolMouseHandler(panel);

        final JPanel fixForScrollBug = new JPanel();
        JScrollPane scrollerForMainTriana;
        scrollerForMainTriana = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollerForMainTriana.setViewportView(panel.getContainer());
        Border b = new EtchedBorder(EtchedBorder.RAISED, Color.blue, Color.gray);
        scrollerForMainTriana.setViewportBorder(b);
        scrollerForMainTriana.doLayout();

        fixForScrollBug.setLayout(new BorderLayout());
        fixForScrollBug.add(scrollerForMainTriana, BorderLayout.CENTER);

        panel.init();
        panel.getTaskGraph().addTaskGraphListener(this);
        panel.getTaskGraph().addTaskListener(this);

        final InternalFrameListener listener = this;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BaseInternalFrame internalframe;
                internalframe = (BaseInternalFrame) workspace.add(fixForScrollBug);
                internalframe.setTitle(panel.getTaskGraph().getToolName());
                internalframe.getAssociatedButton().setText(panel.getTaskGraph().getToolName());
                internalframe.getAssociatedMenuButton().setText(panel.getTaskGraph().getToolName());
                internalframe.addInternalFrameListener(listener);
                internalframe.selectFrameAndAssociatedButtons();
                internalframe.pack();

                selected = panel;
                taskGraphConts.put(internalframe, panel);

                if (taskgraph.getTasks(false).length == 0) {
                    Dimension size = Env.getWindowSize();
                    setTaskGraphPanelSize(panel, (int) (size.width * 0.6), (int) (size.height * 0.6));
                }
            }
        });

        return panel;
    }

    /**
     * @return A string for the new window/taskgraph, Untitled1, Untitled2...
     */
    private String getNextUntitledName() {
        int untitledCount = 1;
        Enumeration enumeration = taskGraphConts.keys();
        while (enumeration.hasMoreElements()) {
            JInternalFrame frame = (JInternalFrame) enumeration.nextElement();
            if (frame.getTitle().indexOf("Untitled") >= 0) {
                untitledCount++;
            }
        }
        return "Untitled" + untitledCount;
    }


    /**
     * Registers the specified TrianaClient to handle the specified taskgraph. This is generally called when
     * addTaskGraphPanel/addParentTaskGraphPanel is called and does not need to be called explicitally.
     */
    public void registerTrianaClient(TaskGraph taskgraph, TrianaClient client) {
        clienttable.put(taskgraph, client);
    }

    /**
     * Unregisters the TrianaClient for the specified task
     */
    public void unregisterTrianaClient(TaskGraph taskgraph) {
        clienttable.remove(taskgraph);
    }

    /**
     * @return the TrianaClient for the specified taskgraph (null if none registered)
     */
    public TrianaClient getTrianaClient(TaskGraph taskgraph) {
        if (clienttable.containsKey(taskgraph)) {
            return (TrianaClient) clienttable.get(taskgraph);
        } else {
            return null;
        }
    }

    /**
     * Sets the size of the internal frame for a main triana
     */
    public void setTaskGraphPanelSize(TaskGraphPanel cont, int width, int height) {
        BaseInternalFrame internalframe = (BaseInternalFrame) getInternalFrameFor(cont);
        Insets scrollinsets = cont.getContainer().getParent().getParent().getInsets();

        internalframe.setSize(
                width + scrollinsets.left + scrollinsets.right + internalframe.getInsets().left + internalframe
                        .getInsets().right,
                height + scrollinsets.top + scrollinsets.bottom + internalframe.getInsets().top + internalframe
                        .getInsets().bottom);
    }


    /**
     * Handle the local publish (and view if required) of the specified taskgraph
     */
    public void localDeploy(TaskGraph taskgraph, TrianaClient client) {
        addChildTaskGraphPanel(taskgraph, client);
    }

    /**
     * Handle the local retract of the specified taskgraph
     */
    public void localRetract(TaskGraph taskgraph) {
        TaskGraphPanel[] panels = getTaskGraphPanels();
        String id = taskgraph.getInstanceID();

        for (int count = 0; count < panels.length; count++) {
            if (id.equals(panels[count].getTaskGraph().getInstanceID())) {
                cleanUpWindows(panels[count].getTaskGraph());
            }
        }
    }

    /**
     * Gets the helpFile attribute of the ApplicationFrame object
     *
     * @return The helpFile value
     */
    public String getHelpFile() {
        return Env.home() + "help" + File.separator + "index.html";
    }


    /**
     * Hides of a main triana window and all its sub windows. If the main triana is a parent this also disposes of the
     * whole taskgraph.
     */
    private void disposeTaskGraphPanel(TaskGraphPanel panel) {
        TaskGraph taskgraph = panel.getTaskGraph();
        cleanUpWindows(taskgraph);
    }


    private void cleanUpWindows(TaskGraph taskgraph) {
        Task[] tasks = taskgraph.getTasks(false);
        TaskGraphPanel cont;

        cont = getTaskGraphPanelFor(taskgraph);

        if (cont != null) {
            disposeWindow(cont);
        }

        for (int count = 0; count < tasks.length; count++) {
            if (tasks[count] instanceof TaskGraph) {
                cleanUpWindows((TaskGraph) tasks[count]);
            }
        }

        unregisterTrianaClient(taskgraph);
    }

    /**
     * Cleans up a main triana window
     */
    private void disposeWindow(final TaskGraphPanel comp) {
        final InternalFrameListener framelist = this;
        final TaskListener tasklist = this;
        final TaskGraphListener tgraphlist = this;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JInternalFrame frame = getInternalFrameFor(comp);

                if (frame != null) {
                    frame.removeInternalFrameListener(framelist);
                    taskGraphConts.remove(frame);
                    workspace.remove(frame);
                }

                comp.getTaskGraph().removeTaskGraphListener(tgraphlist);
                comp.getTaskGraph().removeTaskListener(tasklist);
                comp.dispose();

                if (parents.contains(comp)) {
                    parents.remove(comp);
                    comp.getTaskGraph().dispose();
                }
            }
        });
    }


    /**
     * Called when the user wants to close the window. If the window is in another application then the window is just
     * made invisible, but if it is a stand alone application then a really Quit ? window is given to ask the user if
     * he/she really wants to quit or not.
     */
    public void cleanUp() {
        Env.stopConfigWriters();

        TaskGraphPanel[] cont = (TaskGraphPanel[]) parents.toArray(new TaskGraphPanel[parents.size()]);

        for (int count = 0; count < cont.length; count++) {
            closeTaskGraphPanel(cont[count]);
        }
    }

    /**
     * @return the current selected MainTriana panel within the main Triana application within e.g. the reference to the
     *         frame within the workspace which has the focus
     */
    public TaskGraphPanel getSelectedTaskGraphPanel() {
        if (selected instanceof TaskGraphPanel) {
            return (TaskGraphPanel) selected;
        } else {
            return null;
        }
    }

    /**
     * Sets the current selected main triana
     */
    public void setSelectedTaskGraphContainer(TaskGraphPanel cont) {
        BaseInternalFrame frame = (BaseInternalFrame) getInternalFrameFor(cont);

        if (frame != null) {
            if (frame.getAssociatedButton().getParent() == null) {
                workspace.add(frame);
            }

            frame.selectFrameAndAssociatedButtons();
        }
    }

    /**
     * @return the MainTriana panel which is conatined within the given JInternalFrame
     */
    public JInternalFrame getInternalFrameFor(TaskGraphPanel comp) {
        Enumeration enumeration = taskGraphConts.keys();
        JInternalFrame key = null;
        TaskGraphPanel element = null;

        while (enumeration.hasMoreElements() && (comp != element)) {
            key = (JInternalFrame) enumeration.nextElement();
            element = (TaskGraphPanel) taskGraphConts.get(key);
        }

        if (comp == element) {
            return key;
        } else {
            return null;
        }
    }


    /**
     * @return true if only a single tool is selected
     */
    public boolean isSingleSelectedTool() {
        if ((getSelectionHandler() != this) && (getSelectionHandler() instanceof ToolSelectionHandler)) {
            return ((ToolSelectionHandler) getSelectionHandler()).isSingleSelectedTool();
        } else {
            return false;
        }
    }

    /**
     * @return the currently selected tool (null if none selected)
     */
    public Tool getSelectedTool() {
        if ((getSelectionHandler() != this) && (getSelectionHandler() instanceof ToolSelectionHandler)) {
            return ((ToolSelectionHandler) getSelectionHandler()).getSelectedTool();
        } else {
            return null;
        }
    }

    /**
     * @return an array of the currently selected tools
     */
    public Tool[] getSelectedTools() {
        if ((getSelectionHandler() != this) && (getSelectionHandler() instanceof ToolSelectionHandler)) {
            return ((ToolSelectionHandler) getSelectionHandler()).getSelectedTools();
        } else {
            return new Tool[0];
        }
    }

    /**
     * @return the triana client responsible for the selected tools (null if none)
     */
    public TrianaClient getSelectedTrianaClient() {
        if ((getSelectionHandler() != this) && (getSelectionHandler() instanceof ToolSelectionHandler)) {
            return ((ToolSelectionHandler) getSelectionHandler()).getSelectedTrianaClient();
        } else {
            return null;
        }
    }

    /**
     * @return the currently selected taskgraph (usually parent of selected tool)
     */
    public TaskGraph getSelectedTaskgraph() {
        if ((getSelectionHandler() != this) && (getSelectionHandler() instanceof ToolSelectionHandler)) {
            return ((ToolSelectionHandler) getSelectionHandler()).getSelectedTaskgraph();
        } else {
            return null;
        }
    }


    /**
     * @return The object that is selected for this handler.
     */
    public Object getSelectionHandler() {
        return selected;
    }


    /**
     * Called when the core options of a task change.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
        Task task = event.getTask();

        TaskGraphPanel[] comps = getTaskGraphPanels();
        int count = 0;

        while ((count < comps.length) && (comps[count].getTaskGraph() != task)) {
            count++;
        }

        if (comps[count].getTaskGraph() == task) {
            BaseInternalFrame internalframe = (BaseInternalFrame) getInternalFrameFor(comps[count]);

            internalframe.setTitle(comps[count].getTaskGraph().getToolName());
            internalframe.getAssociatedButton().setText(comps[count].getTaskGraph().getToolName());
            internalframe.getAssociatedMenuButton().setText(comps[count].getTaskGraph().getToolName());
        }
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }


    /**
     * Called when a new task is created in a taskgraph.
     */
    public void taskCreated(TaskGraphTaskEvent event) {
    }

    /**
     * Called when a task is removed from a taskgraph. Note that this method is called when tasks are removed from a
     * taskgraph due to being grouped (they are placed in the new groups taskgraph).
     */
    public void taskRemoved(TaskGraphTaskEvent event) {
        if (event.getTask() instanceof TaskGraph) {
            TaskGraphPanel panel = getTaskGraphPanelFor((TaskGraph) event.getTask());

            if (panel != null) {
                closeTaskGraphPanel(panel);
            }
        }
    }

    /**
     * Called when a new connection is made between two tasks.
     */
    public void cableConnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when a connection is reconnected to a different task.
     */
    public void cableReconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called before a connection between two tasks is removed.
     */
    public void cableDisconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when the control task is connected/disconnected or unstable
     */
    public void controlTaskStateChanged(ControlTaskStateEvent event) {
    }


    public void treeNodesChanged(TreeModelEvent event) {
    }

    public void treeNodesInserted(TreeModelEvent event) {
        if (!toolboxTree.isExpanded(0)) {
            toolboxTree.expandRow(0);
        }
    }

    public void treeNodesRemoved(TreeModelEvent event) {
    }

    public void treeStructureChanged(TreeModelEvent event) {
    }


    public void componentHidden(ComponentEvent event) {
    }

    public void componentMoved(ComponentEvent event) {
        if (event.getSource() == this) {
            Env.setWindowPosition(getLocation());
        }
    }

    public void componentResized(ComponentEvent event) {
        if (event.getSource() == this) {
            Env.setWindowSize(getSize());
        }
    }

    public void componentShown(ComponentEvent event) {
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent event) {
        if (event.getSource() == toolboxTree) {
            selected = leaflistener;
        } else {
            selected = event.getSource();
        }
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent event) {
        // required to fix internal frame focus/selection bug
        if (event.getComponent() instanceof TaskGraphPanel) {
            try {
                JInternalFrame frame = getInternalFrameFor((TaskGraphPanel) event.getComponent());

                if (frame != null) {
                    frame.setSelected(false);
                }
            } catch (PropertyVetoException except) {
            }
        }
    }

    /**
     * Clean-up when a internal window is closed
     */
    public void internalFrameClosing(InternalFrameEvent event) {
        TaskGraphPanel comp = getTaskGraphPanelFor(event.getInternalFrame());
        event.getInternalFrame().removeInternalFrameListener(this);
        disposeTaskGraphPanel(comp);
    }

    public void internalFrameOpened(InternalFrameEvent event) {
    }

    public void internalFrameActivated(InternalFrameEvent event) {
        selected = getTaskGraphPanelFor(event.getInternalFrame());
    }

    public void internalFrameClosed(InternalFrameEvent event) {
        TaskGraphPanel comp = getTaskGraphPanelFor(event.getInternalFrame());
        event.getInternalFrame().removeInternalFrameListener(this);
        disposeTaskGraphPanel(comp);
    }

    public void internalFrameDeactivated(InternalFrameEvent event) {
    }

    public void internalFrameDeiconified(InternalFrameEvent event) {
    }

    public void internalFrameIconified(InternalFrameEvent event) {
    }


    private class TrianaShutdownHook extends Thread {
        /**
         * If this thread was constructed using a separate <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called; otherwise, this method does nothing and
         * returns.
         * <p/>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see Thread#start()
         * @see Thread#stop()
         * @see Thread#Thread(ThreadGroup, Runnable, String)
         * @see Runnable#run()
         */
        public void run() {
            cleanUp();
        }

    }

}
