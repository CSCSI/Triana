package org.trianacode.enactment;

import org.apache.commons.lang.ArrayUtils;
import org.trianacode.TrianaInstance;
import org.trianacode.config.Locations;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.OptionValues;
import org.trianacode.config.cl.OptionsHandler;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.enactment.addon.ExecutionAddon;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.enactment.io.NodeMappings;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.databus.DataBus;
import org.trianacode.taskgraph.databus.DataBusInterface;
import org.trianacode.taskgraph.databus.DataNotResolvableException;
import org.trianacode.taskgraph.databus.packet.WorkflowDataPacket;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.service.ExecutionEvent;
import org.trianacode.taskgraph.service.ExecutionListener;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 24, 2010
 */
public class Exec implements ExecutionListener {


    public static final String RUNS_DIR = "runs";
    public static File RUNS = new File(Locations.getApplicationDataDir(), RUNS_DIR);

    static {
        RUNS.mkdirs();
    }

    private String pid = UUID.randomUUID().toString();

    private TrianaRun runner;

    public Exec(String pid) {
        if (pid != null && pid.length() > 0) {
            this.pid = pid;
        }
    }

    public String getPid() {
        return pid;
    }

    public static void main(String[] args) {
        int exitNumber = exec(args);
        System.exit(exitNumber);
    }

    public static int exec(String[] args) {
        cleanPids();
        try {
            OptionsHandler parser = new OptionsHandler("Exec", TrianaOptions.TRIANA_OPTIONS);
            OptionValues vals = null;
            try {
                vals = parser.parse(args);
            } catch (ArgumentParsingException e) {
                System.out.println(e.getMessage());
                System.out.println(parser.usage());
                return 0;
//                System.exit(0);
            }
            String logLevel = vals.getOptionValue("l");

            String logger = vals.getOptionValue("i");
            if (logger != null) {
                Loggers.isolateLogger(logger, logLevel == null ? "INFO" : logLevel);
            } else {
                if (logLevel != null) {
                    Loggers.setLogLevel(logLevel);
                }
            }
            String pid = vals.getOptionValue("u");
            String data = vals.getOptionValue("d");

            List<String> bundles = vals.getOptionValues("b");
            if (bundles != null) {
                if (bundles.size() != 1) {
                    System.out.println("Only one bundle can be specified");
                    return 1;
                }
                new Exec(pid).executeBundle(bundles.get(0), data, args);
                return 0;
//                System.exit(0);
            }

            List<String> wfs = vals.getOptionValues("w");
            if (wfs != null) {
                if (wfs.size() != 1) {
                    System.out.println("Only one workflow can be specified.");
                    return 1;
//                    System.exit(1);
                }
                System.out.println("Executed : " + new Exec(pid).executeNewTriana(wfs.get(0), data));
                return 0;
//                System.exit(0);
            }
//            String com = vals.getOptionValue("c");
//            if (com != null && pid != null) {
//                int i = commandToInt(com);
//                writeFile(i, pid, false);
//                System.exit(0);
//            }

            wfs = vals.getOptionValues("e");
            if (wfs != null) {
                if (wfs.size() != 1) {
                    System.out.println("Only one workflow can be specified.");
                    return 1;
//                    System.exit(1);
                } else {
                    new Exec(pid).executeWorkflowFile(wfs.get(0), data, args, vals);
                    return 0;
                }
            } else {
                if (pid != null) {
                    int val = readFile(pid);
                    System.out.println(statusToString(val));
                    return 0;
//                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Fell off end of options, or major error occurred.");
        return 1;
    }

    private void executeBundle(String bundlePath, String data, String[] args) throws Exception {
        File f = new File(bundlePath);
        if (!f.exists()) {
            System.out.println("Cannot find bundle file:" + bundlePath);
            System.exit(1);
        }
        TrianaInstance engine = new TrianaInstance(args);
        engine.addExtensionClass(CLIaddon.class);
        engine.init();

        Thread.sleep(3000);

        ExecutionAddon executionAddon = AddonUtils.getExecutionAddon(engine, "bundle");

        if (executionAddon != null) {
            System.out.println("Running with " + executionAddon.getServiceName());
            executionAddon.execute(this, engine, bundlePath, null, data, args);
        } else {
            System.out.println("Bundle executing service not found");
        }
        engine.shutdown(0);
    }


    private static int commandToInt(String command) {
        if (command.equalsIgnoreCase("pause")) {
            return 100;
        } else if (command.equalsIgnoreCase("resume")) {
            return 101;
        } else if (command.equalsIgnoreCase("reset")) {
            return 102;
        } else if (command.equalsIgnoreCase("stop")) {
            return 103;
        }
        return -1;
    }

    private static String statusToString(int status) {
        ExecutionState state = ExecutionState.getState(status);
        if (state != null) {
            return state.toString();
        }
        switch (status) {
            case -1:
                return "unknown error";
            case 100:
                return "pause";
            case 101:
                return "resume";
            case 102:
                return "reset";
            case 103:
                return "stop";
            case 200:
                return "finished";
            default:
                return "unknown";
        }
    }

    private String executeNewTriana(String wf, String data) throws IOException {
        File f = new File(wf);
        if (!f.exists()) {
            return "Workflow File cannot be found:" + wf;
        }
        if (data != null) {
            File conf = new File(data);
            if (conf.exists() && conf.length() > 0) {
                data = conf.getAbsolutePath();
            }
        }
        createFile(pid);
        String home = Locations.getHomeProper();
        File bin;
        if (home.endsWith(".jar")) {
            bin = new File(new File(home).getParent());
        } else {
            bin = new File(Locations.getHomeProper(), "bin");
        }
        String script = "triana";
        if (Locations.os().equals("windows")) {
            script += ".bat";
        } else {
            script = "./" + script + ".sh";
        }
        System.out.println(bin.getCanonicalPath());
        List<String> args = new ArrayList<String>();
        args.add(script);
        args.add("-e");
        args.add(wf);
        args.add("-u");
        args.add(pid);
        if (data != null) {
            args.add("-d");
            args.add(data);
        }
        System.out.println(ArrayUtils.toString(args));
        execProcess(args, bin);
//        runProcess(args, bin);
        return pid;
    }

    private void executeWorkflowFile(String wf, String data, String[] args, OptionValues vals) throws Exception {

        File f = new File(wf);
        if (!f.exists()) {
            System.out.println("Cannot find workflow file:" + wf);
            System.exit(1);
        }
        TrianaInstance engine = new TrianaInstance(args);
        engine.addExtensionClass(CLIaddon.class);
        engine.init();

        Thread.sleep(3000);

        XMLReader reader = new XMLReader(new FileReader(f));
        Tool tool = reader.readComponent(engine.getProperties());

        ExecutionAddon executionAddon = (ExecutionAddon) AddonUtils.getService(engine, vals);
        if (executionAddon != null) {
            System.out.println("Running with " + executionAddon.getServiceName());
            executionAddon.execute(this, engine, wf, tool, data, args);
        } else {
            System.out.println("Running in vanilla mode : no ExecutionAddon");
            execute(tool, data);
        }
        engine.shutdown(0);
    }

    public void execute(final Tool tool, final String data) throws Exception {
        runner = new TrianaRun(tool);
        runner.getScheduler().addExecutionListener(this);
        NodeMappings mappings = null;
        if (data != null) {
            System.out.println("Config file : " + data);
            File conf = new File(data);
            if (!conf.exists()) {
                System.out.println("Cannot find data configuration file:" + data);
                System.exit(1);
            }
            IoHandler handler = new IoHandler();
            IoConfiguration ioc = handler.deserialize(new FileInputStream(conf));
            mappings = handler.map(ioc, runner.getTaskGraph());
            System.out.println("Data mappings size : " + mappings.getMap().size());
        }
        runner.runTaskGraph();
        if (mappings != null) {
            Iterator<Integer> it = mappings.iterator();
            while (it.hasNext()) {
                Integer integer = it.next();
                Object val = mappings.getValue(integer);
                System.out.println("Data : " + val.toString() + " will be sent to input number " + integer);
                runner.sendInputData(integer, val);
            }
        } else {
            System.out.println("Mappings was null");
            System.out.println(((TaskGraph) tool).getDataInputNodeCount() + " inputs.");
        }
        while (!runner.isFinished()) {
            synchronized (this) {
                try {
                    wait(100);
                } catch (InterruptedException e) {

                }
            }
        }

        Node[] nodes = runner.getTaskGraph().getDataOutputNodes();
        for (Node node : nodes) {
            Object out = runner.receiveOutputData(0);
            Object o = null;
            if (out instanceof WorkflowDataPacket) {
                try {
                    DataBusInterface db = DataBus.getDataBus(((WorkflowDataPacket) out).getProtocol());
                    o = db.get((WorkflowDataPacket) out);
                } catch (DataNotResolvableException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Exec.execute output node " + node.getName() + " data : " + o);
        }
        runner.dispose();
    }

    private int execProcess(List<String> optionsStrings, File bin) {
        String[] args = new String[optionsStrings.size()];

        for (int i = 0; i < optionsStrings.size(); i++) {
            args[i] = optionsStrings.get(i);
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(args, new String[]{}, bin);
            StreamToOutput err = new StreamToOutput(process.getInputStream(), "err");
            err.start();
            StreamToOutput out = new StreamToOutput(process.getInputStream(), "std.out");
            out.start();

            int returnCode = process.waitFor();
            System.out.println("Runtime process finished with code " + returnCode);
            return returnCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    class StreamToOutput implements Runnable {

        private InputStream inputStream;
        private String description;
        private Thread thread;
        private BufferedReader inreader;

        public StreamToOutput(InputStream inputStream, String description) {
            this.inputStream = inputStream;
            this.description = description;
            inreader = new BufferedReader(new InputStreamReader(inputStream));

        }

        public void start() {
            thread = new Thread(this);
            thread.run();
        }

        @Override
        public void run() {
            try {
                String str;
                while ((str = inreader.readLine()) != null) {
                    System.out.println(str);
                }
            } catch (IOException e) {
                System.out.println("Error with stream " + description + " closing");
            } finally {
                try {
                    inreader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Closed streamReader " + description);
            }
        }
    }

    public InputStream readData(String name) throws FileNotFoundException {
        File lockDir = new File(RUNS, pid);
        if (!lockDir.exists()) {
            return null;
        }
        File dataFile = new File(lockDir, name);
        if (dataFile.exists() && dataFile.length() > 0) {
            return new FileInputStream(dataFile);
        }
        return null;
    }

    private void writeData(Object o, int index) throws IOException, TaskGraphException {
        File lockDir = new File(RUNS, pid);
        if (!lockDir.exists()) {
            return;
        }
        File dataFile = new File(lockDir, "" + index);
        IoTypeHandler ioth = IoHandler.getHandler(o);
        if (ioth != null) {
            FileOutputStream fout = new FileOutputStream(dataFile);
            ioth.write(o, fout);
        }
    }

    public void createFile() throws IOException {
        writeFile(ExecutionState.NOT_INITIALIZED.ordinal(), pid, true);
    }

    private static void createFile(String pid) throws IOException {
        writeFile(ExecutionState.NOT_INITIALIZED.ordinal(), pid, true);
    }

    public String readUuidFile() throws IOException {
        int val = readFile(pid);
        return statusToString(val);
    }

    private static int readFile(String pid) throws IOException {
        File lockDir = new File(RUNS, pid);
        if (lockDir.exists()) {
            File lockfile = new File(lockDir, pid);
            if (lockfile.exists() && lockfile.length() > 0) {
                RandomAccessFile file = new RandomAccessFile(lockfile, "rw");
                FileChannel f = file.getChannel();
                FileLock fl = f.lock();
                ByteBuffer bb = ByteBuffer.allocate(4);
                f.read(bb);
                bb.flip();
                int command = bb.getInt();
                fl.release();
                file.close();
                return command;
            }
        }
        return -1;
    }

    private static void writeFile(int command, String pid, boolean create) throws IOException {

        File lockDir = new File(RUNS, pid);
        lockDir.mkdirs();
        File lockfile = new File(lockDir, pid);
        if (!create) {
            if (!lockfile.exists() || lockfile.length() == 0) {
                return;
            }
        }
        RandomAccessFile file = new RandomAccessFile(lockfile, "rw");
        FileChannel f = file.getChannel();
        FileLock fl = f.lock();
        if (lockfile.length() == 4) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            f.read(bb);
            bb.flip();
            int curr = bb.getInt();
            if (curr == ExecutionState.COMPLETE.ordinal()) {
                return;
            }
        }
        ByteBuffer bytes = ByteBuffer.allocate(4);
        bytes.putInt(command).flip();
        f.write(bytes);
        f.force(false);
        fl.release();
        file.close();
    }

    private static void deleteFile(String pid) throws IOException {
        File lockDir = new File(RUNS, pid);
        if (!lockDir.exists()) {
            return;
        }
        File lockfile = new File(lockDir, pid);
        if (!lockfile.exists()) {
            return;
        }
        RandomAccessFile file = new RandomAccessFile(lockfile, "rw");
        FileChannel f = file.getChannel();
        FileLock fl = f.lock();
        lockfile.delete();
        fl.release();
        file.close();
    }


    @Override
    public void executionStateChanged(ExecutionEvent event) {

    }

    @Override
    public void executionRequested(ExecutionEvent event) {
        try {
            writeFile(event.getState().ordinal(), pid, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executionStarting(ExecutionEvent event) {
        try {
            writeFile(event.getState().ordinal(), pid, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executionFinished(ExecutionEvent event) {
        try {
            writeFile(event.getState().ordinal(), pid, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //runner.dispose();
    }

    @Override
    public void executionReset(ExecutionEvent event) {
        try {
            writeFile(event.getState().ordinal(), pid, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Poller extends TimerTask {

        @Override
        public void run() {
            try {
                int val = readFile(pid);
                if (val >= 100) {
                    switch (val) {
                        case 100:
                            runner.getScheduler().pauseTaskGraph();
                            break;
                        case 101:
                            runner.getScheduler().resumeTaskGraph();
                            break;
                        case 102:
                            runner.getScheduler().resetTaskGraph();
                            break;
                        case 103:
                            runner.dispose();
                            writeFile(ExecutionState.COMPLETE.ordinal(), pid, false);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//
//    private void log(String log) throws Exception {
//        File file = new File(System.getProperty("user.home"), pid + ".log");
//        RandomAccessFile raf = new RandomAccessFile(file, "rw");
//        raf.seek(file.length());
//        FileChannel f = raf.getChannel();
//        FileLock fl = f.lock();
//        ByteBuffer bb = ByteBuffer.allocate((log + "\n").getBytes().length);
//        bb.put((log + "\n").getBytes()).flip();
//        f.write(bb);
//        f.force(false);
//        fl.release();
//        raf.close();
//    }

    private static void cleanPids() {
        File[] pids = RUNS.listFiles();
        long now = System.currentTimeMillis();
        for (File file : pids) {
            long mod = file.lastModified();
            if (mod + (3600000 * 24) < now && file.isDirectory()) {
                file.delete();
            }
        }
    }
}
