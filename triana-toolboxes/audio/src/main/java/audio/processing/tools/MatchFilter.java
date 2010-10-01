package audio.processing.tools;

import java.util.Iterator;
import java.util.TreeSet;

import org.trianacode.taskgraph.Unit;
import triana.types.Document;
import triana.types.audio.MultipleAudio;
import triana.types.util.Str;

public class MatchFilter extends Unit {
    StringBuffer output;
    int count = 0;

    int matchNum = 10;
    TreeSet matches = new TreeSet();

    public void process() throws Exception {
        ++count;
        matches.clear();
        MultipleAudio wave = (MultipleAudio) getInputAtNode(0);
        MultipleAudio filter = (MultipleAudio) getInputAtNode(1);

        Object waveAudio = wave.getChannel(0);

        Object filterAudio = filter.getChannel(0);

        if (waveAudio instanceof short[]) { // 16-bit audio
            double min = 0.0;
            double max = 0.0;

            if (!(filterAudio instanceof short[])) {
                throw new Exception(
                        "Incompatible types in " + getTask().getToolName() + "\nWave is 16-bit and filter is 8 or 24-bit");
            }
            short[] waveAud = ((short[]) waveAudio);
            short[] filtAud = ((short[]) filterAudio);

            int length = waveAud.length - filtAud.length;
            int filtLen = filtAud.length;
            int j;
            double sum;

            for (int i = 0; i < length; ++i) {
                sum = 0.0;
                for (j = 0; j < filtLen; ++j) {
                    sum += waveAud[i + j] * filtAud[j];
                }
                if (sum > min) { // add to the list
                    if (sum > max) {
                        max = sum;
                    }
                    matches.add(new Val(sum, i)); // index in in waveAudio
                    if (matches.size() > matchNum) {
                        // System.out.println(matches);
                        matches.remove(matches.first()); // lowest val
                        min = ((Val) matches.first()).val;
                    }
                }
            }

            StringBuffer b = new StringBuffer();
            Iterator i = matches.iterator();
            Val v;
            while (i.hasNext()) { // reSCale now for efficiency
                v = (Val) i.next();
                v.val /= (double) filtAud.length; // scale back
                b.append(v.toString());
                b.append("\n");
            }

            output.append("Run : " + String.valueOf(count));
            output.append("\n");
            output.append(b.toString());
            output.append("\n");

            Document out = new Document();
            out.setText(output.toString());
            output(out);
        }
    }

    /**
     * Initialses information specific to MatchFilter.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(2);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        output = new StringBuffer();
        count = 0;

        String guilines = "";
        guilines += "Number of matchNum (Best match output first) ? $title matchNum IntScroller 0 100 10";
        setGUIBuilderV2Info(guilines);
    }

//    public void setGUIInformation() {
//        addGUILine("Number of matchNum (Best match output first) ? $title matchNum IntScroller 0 100 10");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
        output = new StringBuffer();
        count = 0;
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    public void parameterUpdate(String name, String value) {
        // Code to update local variables
        if (name.equals("matchNum")) {
            matchNum = (int) Str.strToDouble(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to MatchFilter, each separated by a white
     *         space.
     */
    /**
     * @return an array of the input types for CombFilter
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }
    /**
     * @return a string containing the names of the types output from MatchFilter, each separated by a white space.
     */
    
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Document"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Creates a response from the correlation of the\n" +
                " second input with the first at each point";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "MatchFilter.html";
    }

    private class Val implements Comparable {
        double val;
        int index;

        public Val(double v, int ind) {
            val = v;
            index = ind;
        }

        public int compareTo(Object o) {
            Val v = (Val) o;
            if (val < v.val) {
                return -1;
            }
            if (val == v.val) {
                return 0;
            } else {
                return 1;
            }
        }

        public String toString() {
            return "Response at Index[" + index + "] = " + val;
        }
    }
}



