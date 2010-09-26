package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.config.ArgumentParser;
import org.trianacode.config.Locations;
import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.service.ExecutionEvent;
import org.trianacode.taskgraph.service.ExecutionListener;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 24, 2010
 */
public class Exec implements ExecutionListener {


    public static final String PID_DIR = "pids";
    public static String tmpdir = System.getProperty("user.home");

    private String pid = "1";//UUID.randomUUID().toString();

    private TrianaRun runner;

    public Exec(String pid) {
        if (pid != null) {
            this.pid = pid;
        }
    }

    public static void main(String[] args) {
        try {
            ArgumentParser parser = new ArgumentParser(args);
            parser.parse();
            String pid = parser.getArgumentValue("-p");
            String wf = parser.getArgumentValue("-w");
            if (wf != null) {
                System.out.println(new Exec(pid).executeFile(wf));
                System.exit(0);
            }
            boolean stat = parser.isOption("-s");
            if (stat && pid != null) {
                int val = readFile(pid);
                System.out.println(statusToString(val));
                System.exit(0);
            }
            String com = parser.getArgumentValue("-c");
            if (com != null && pid != null) {
                int i = commandToInt(com);
                writeFile(i, pid, false);
                System.exit(0);
            }
            String execute = parser.getArgumentValue("-e");
            if (execute != null) {
                new Exec(pid).executeWorkflow(execute);
            } else {
                System.out.println("Unknown options:" + parser.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String executeFile(String wf) throws IOException {
        File f = new File(wf);
        if (!f.exists() || f.length() == 0) {
            return "Workflow File cannot be found:" + wf;
        }
        createFile(pid);
        String home = Locations.getHomeProper();
        File bin;
        if (home.endsWith(".jar")) {
            bin = new File(new File(home).getParent());
        } else {
            bin = new File(Locations.getHomeProper(), "bin");
        }
        String script = "triana-exec";
        if (Locations.os().equals("windows")) {
            script += ".bat";
        } else {
            script = "./" + script + ".sh";
        }
        List<String> args = new ArrayList<String>();
        args.add(script);
        args.add("-e");
        args.add(wf);
        args.add("-p");
        args.add(pid);
        runProcess(args, bin);
        return pid;
    }

    private void executeWorkflow(String wf) throws Exception {
        File f = new File(wf);
        if (!f.exists() || f.length() == 0) {
            log("Cannot find workflow file:" + wf);
            System.exit(1);
        }
        TrianaInstance engine = new TrianaInstance(new String[0], null);
        XMLReader reader = new XMLReader(new FileReader(f));
        Tool tool = reader.readComponent();
        execute(tool);
        System.exit(0);

    }

    public void execute(Tool tool) throws Exception {
        runner = new TrianaRun(tool);
        runner.getScheduler().addExecutionListener(this);
        runner.runTaskGraph();
//        while (!runner.isFinished()) {
//            synchronized (this) {
//                try {
//                    wait(100);
//                } catch (InterruptedException e) {
//
//                }
//            }
//        }
//        runner.dispose();
//        try {
//            writeFile(FINISHED, pid, false);
//        } catch (IOException e) {
//            log(e.getMessage());
//            e.printStackTrace();
//        }
    }


    private void runProcess(List<String> args, File runDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(args);
        /*Map<String, String> env = pb.environment();
        for (String s : env.keySet()) {
            log.info("Receiver.processBundle env key=" + s);
            log.info("Receiver.processBundle env val=" + env.get(s));
        }*/

        pb = pb.directory(runDir);
        Process process = pb.start();

//        InputStream in = process.getInputStream();
//        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
//        byte[] bytes1 = new byte[512];
//        int c1;
//        while ((c1 = in.read(bytes1)) > -1) {
//            stdout.write(bytes1, 0, c1);
//        }
//        String msg = new String(stdout.toByteArray());
//        if (msg.length() > 0) {
//            System.out.println(msg);
//        }
//
//        InputStream err = process.getErrorStream();
//        ByteArrayOutputStream errlog = new ByteArrayOutputStream();
//        byte[] bytes = new byte[512];
//        int c;
//        while ((c = err.read(bytes)) > -1) {
//            errlog.write(bytes, 0, c);
//        }
//        msg = new String(errlog.toByteArray());
//        if (msg.length() > 0) {
//            System.out.println("Exec.runProcess result from error stream:" + msg );
//        }
    }


    private void createFile(String pid) throws IOException {
        writeFile(ExecutionState.NOT_INITIALIZED.ordinal(), pid, true);
    }

    private static int readFile(String pid) throws IOException {
        File lockfile = new File(tmpdir, pid);
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
        return -1;
    }

    private static void writeFile(int command, String pid, boolean create) throws IOException {

        File lockfile = new File(tmpdir, pid);
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
        File lockfile = new File(tmpdir, pid);
        RandomAccessFile file = new RandomAccessFile(lockfile, "rw");
        FileChannel f = file.getChannel();
        FileLock fl = f.lock();
        lockfile.delete();
        fl.release();
        file.close();
    }


    @Override
    public void executionStateChanged(ExecutionEvent event) {
        try {
            log(event.getTask().getToolName() + ":" + event.getState().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        runner.dispose();
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

    private void log(String log) throws Exception {
        File file = new File(System.getProperty("user.home"), pid + ".log");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(file.length());
        FileChannel f = raf.getChannel();
        FileLock fl = f.lock();
        ByteBuffer bb = ByteBuffer.allocate((log + "\n").getBytes().length);
        bb.put((log + "\n").getBytes()).flip();
        f.write(bb);
        f.force(false);
        fl.release();
        raf.close();
    }
}
