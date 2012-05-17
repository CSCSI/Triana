package org.trianacode.shiwa.workflowCreation.dart;

import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 14/05/2012
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class DartScript {

    public static String freqpoints_max = "freqpoints_max";
    public static String harmonics_max = "harmonics_max";
    public static String audio_files_csv = "audio_file";

    private HashMap<String, String> defaults;

    @Process()
    public String[] process(String input) {

        setDefaults();

        getInputs(input);

        int freqpoints_max_int = Integer.parseInt(defaults.get(freqpoints_max));
        int harmonics_max_int = Integer.parseInt(defaults.get(harmonics_max));
        String[] audios = defaults.get(audio_files_csv).split("'");


        ArrayList<String> runs = new ArrayList<String>();
        for (int a = 0; a < audios.length; a++) {
            for (int i = 1; i <= freqpoints_max_int; i += 10) {
                for (int j = 1; j <= harmonics_max_int; j++) {
                    String command_line = "java -jar Dart.jar -infile "
                            + audios[a]
                            + " -outfile DART-" + (a + 1)
                            + "-" + i + "-" + j + "-1.txt"
                            + " -nofreqpoints " + i
                            + " -noharmonics " + j
                            + " -fft_window Rectangle";

                    runs.add(command_line);
                }
            }
        }
        String[] runArray = new String[runs.size()];
        return runs.toArray(runArray);
    }

    private void setDefaults() {
        defaults = new HashMap<String, String>();

        defaults.put(freqpoints_max, "501");
        defaults.put(harmonics_max, "32");
        defaults.put(audio_files_csv, "DARTAcousticG.wav'DARTOboe.wav'DARTViolin.wav'DARTPiano.wav'DARTTubBells.wav'DARTDistortG.wav");


    }


    private void getInputs(String all) {
        String[] inputs = (all).split(";");

        for (String input : inputs) {
            String[] inputString = (input).split(",");

            if (inputString.length == 4) {
                if (inputString[0].equals("input")) {
                    System.out.println(Arrays.toString(inputString));
//                    addSource(doc, root, inputString[1], inputString[2], inputString[3]);

                    defaults.put(inputString[1], inputString[3]);
                }
            }
        }
    }
}
