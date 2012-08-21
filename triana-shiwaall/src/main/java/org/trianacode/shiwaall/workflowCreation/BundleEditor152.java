//package org.trianacode.shiwaall.workflowCreation;
//
//import org.shiwa.desktop.data.description.SHIWABundle;
//import org.shiwa.desktop.data.description.bundle.BundleFile;
//import org.shiwa.desktop.data.description.core.Configuration;
//import org.shiwa.desktop.data.description.core.WorkflowImplementation;
//import org.shiwa.desktop.data.description.resource.AggregatedResource;
//import org.shiwa.desktop.data.description.workflow.SHIWAProperty;
//import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
//import org.trianacode.annotation.TextFieldParameter;
//import org.trianacode.annotation.Tool;
//import org.trianacode.enactment.logging.stampede.StampedeLog;
//import org.trianacode.shiwaall.bundle.ShiwaBundleHelper152;
//import org.trianacode.shiwaall.utils.BrokerUtils;
//import org.trianacode.taskgraph.Task;
//import org.trianacode.taskgraph.TaskGraph;
//import org.trianacode.taskgraph.annotation.TaskConscious;
//import org.trianacode.taskgraph.ser.XMLWriter;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * Created by IntelliJ IDEA.
// * User: Ian Harvey
// * Date: 19/04/2012
// * Time: 14:43
// * To change this template use File | Settings | File Templates.
// */
//@Tool
//public class BundleEditor152 implements TaskConscious {
//
//    @TextFieldParameter
//    public String bundlePath = "/Users/ian/dartBundle.zip";
//    private ShiwaBundleHelper152 shiwaBundleHelper;
//    private Task task;
//
//    @org.trianacode.annotation.Process
//    public ArrayList<File> process(List list) {
//
//        System.out.println("Creating " + list.size() + " bundles");
//        ArrayList<File> bundles = new ArrayList<File>();
//
//        try {
//            File inputBundleFile = new File(bundlePath);
//            System.out.println("input exists : " + inputBundleFile.exists());
//
//            shiwaBundleHelper = new ShiwaBundleHelper152(new SHIWABundle(inputBundleFile));
//            clearConfigs(shiwaBundleHelper.getWorkflowImplementation());
//
//        } catch (SHIWADesktopIOException e) {
//            e.printStackTrace();
//        }
//
////        TrianaServer server = TaskGraphManager.getTrianaServer(task.getParent());
////        SchedulerInterface sched = server.getSchedulerInterface();
//
////        Scheduler scheduler = BrokerUtils.getSchedulerForTaskGraph(task.getParent());
//
//
////        if(scheduler != null) {
//
//
////            String jobID = "unit:" + task.getQualifiedToolName();
////        if (sched instanceof Scheduler) {
////            Scheduler scheduler = (Scheduler) sched;
////            UUID parentID = scheduler.stampedeLog.getRunUUID();
////            System.out.println("Parent execution " + parentID.toString());
////            String jobInstID = scheduler.stampedeLog.getTaskNumber(task).toString();
//
//
//        WorkflowImplementation impl = shiwaBundleHelper.getWorkflowImplementation();
//
//        for (Object object : list) {
//            if (object instanceof TaskGraph) {
//                TaskGraph taskGraph = (TaskGraph) object;
//
//                cleanProperties();
//                UUID runUUID = UUID.randomUUID();
//
//                BrokerUtils.prepareSubworkflow(
//                        task, runUUID, shiwaBundleHelper.getWorkflowImplementation()
//                );
////                    BrokerUtils.addParentDetailsToSubWorkflow(impl, runUUID, parentID, jobID, jobInstID);
//
////
////                    impl.addProperty(new SHIWAProperty(StampedeLog.PARENT_UUID_STRING,
////                            parentID.toString()));
////
////                    impl.addProperty(new SHIWAProperty(StampedeLog.RUN_UUID_STRING,
////                            runUUID.toString()));
////
////                    impl.addProperty(new SHIWAProperty(StampedeLog.JOB_ID, jobID));
////
////                    impl.addProperty(new SHIWAProperty(StampedeLog.JOB_INST_ID, jobInstID));
//
//                try {
//                    System.out.println("Adding imp " + taskGraph.getToolName());
//                    impl.setDefinition(
//                            new BundleFile(
//                                    getWorkflowDefinition(taskGraph), taskGraph.getToolName()));
//                } catch (SHIWADesktopIOException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    File temp = File.createTempFile(taskGraph.getToolName() + "-", "");
//                    File b = shiwaBundleHelper.saveBundle(temp);
//                    System.out.println("Made " + b.getAbsolutePath());
//                    bundles.add(b);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
////        }
//
//        return bundles;
//    }
//
//    private void cleanProperties() {
//        List<SHIWAProperty> props = shiwaBundleHelper.getWorkflowImplementation().getProperties();
//        ArrayList<SHIWAProperty> toRemove = new ArrayList<SHIWAProperty>();
//
//        for (SHIWAProperty p : props) {
//            if (p.getTitle().equals(StampedeLog.PARENT_UUID_STRING) ||
//                    p.getTitle().equals(StampedeLog.RUN_UUID_STRING) ||
//                    p.getTitle().equals(StampedeLog.JOB_ID) ||
//                    p.getTitle().equals(StampedeLog.JOB_INST_ID)) {
//                toRemove.add(p);
//            }
//        }
//
//        for (SHIWAProperty rem : toRemove) {
//            props.remove(rem);
//        }
//    }
//
//    private void clearConfigs(WorkflowImplementation workflowImplementation) {
//        ArrayList<Configuration> dataConfigs = new ArrayList<Configuration>();
//        for (AggregatedResource resource : workflowImplementation.getAggregatedResources()) {
//            if (resource instanceof Configuration) {
//                if (((Configuration) resource).getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
//                    dataConfigs.add((Configuration) resource);
//                }
//            }
//        }
//        for (Configuration configuration : dataConfigs) {
//            workflowImplementation.getAggregatedResources().remove(configuration);
//        }
//    }
//
//    public InputStream getWorkflowDefinition(Task task) {
//        try {
//            File temp = File.createTempFile("publishedTaskgraphTemp", ".xml");
//            temp.deleteOnExit();
//            XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(temp)));
//            writer.writeComponent(task);
//            writer.close();
//            return new FileInputStream(temp);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @Override
//    public void setTask(Task task) {
//        this.task = task;
//    }
//}