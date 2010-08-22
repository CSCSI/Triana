package audio.processing.mir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Eddie Al-Shakarchi Date: Nov 28, 2008 Time: 7:25:30 PM To change this template use
 * File | Settings | File Templates.
 */
public class NoteWriter {

    Vector notesVector = new Vector();
    private int frequency;
    String note;
    String outfilename = "untitled";
    File file;
    BufferedWriter out;
    String[] notesArray;
    String[][] noteMapArray = {{"C0", "16.35"}, {"C#0/Db0", "17.32"}, {"D0", "18.35"}, {"D#0/Eb0", "19.45"},
            {"E0", "20.60"}, {"F0", "21.83"}, {"F#0/Gb0", "23.12"}, {"G0", "24.50"}, {"G#0/Ab0", "25.96"},
            {"A0", "27.50"}, {"A#0/Bb0", "29.14"}, {"B0", "30.87"}, {"C1", "32.70"}, {"C#1/Db1", "34.65"},
            {"D1", "36.71"}, {"D#1/Eb1", "38.89"}, {"E1", "41.20"}, {"F1", "43.65"}, {"F#1/Gb1", "46.25"},
            {"G1", "49.00"}, {"G#1/Ab1", "51.91"}, {"A1", "55.00"}, {"A#1/Bb1", "58.27"}, {"B1", "61.74"},
            {"C2", "65.41"}, {"C#2/Db2", "69.30"}, {"D2", "73.42"}, {"D#2/Eb2", "77.78"}, {"E2", "82.41"},
            {"F2", "87.31"}, {"F#2/Gb2", "92.50"}, {"G2", "98.00"}, {"G#2/Ab2", "103.83"}, {"A2", "110.00"},
            {"A#2/Bb2", "116.54"}, {"B2", "123.47"}, {"C3", "130.81"}, {"C#3/Db3", "138.59"}, {"D3", "146.83"},
            {"D#3/Eb3", "155.56"}, {"E3", "164.81"}, {"F3", "174.61"}, {"F#3/Gb3", "185.00"}, {"G3", "196.00"},
            {"G#3/Ab3", "207.65"}, {"A3", "220.00"}, {"A#3/Bb3", "233.08"}, {"B3", "246.94"}, {"C4", "261.63"},
            {"C#4/Db4", "277.18"}, {"D4", "293.66"}, {"D#4/Eb4", "311.13"}, {"E4", "329.63"}, {"F4", "349.23"},
            {"F#4/Gb4", "369.99"}, {"G4", "392.00"}, {"G#4/Ab4", "415.30"}, {"A4", "440.00"}, {"A#4/Bb4", "466.16"},
            {"B4", "493.88"}, {"C5", "523.25"}, {"C#5/Db5", "554.37"}, {"D5", "587.33"}, {"D#5/Eb5", "622.25"},
            {"E5", "659.26"}, {"F5", "698.46"}, {"F#5/Gb5", "739.99"}, {"G5", "783.99"}, {"G#5/Ab5", "830.61"},
            {"A5", "880.00"}, {"A#5/Bb5", "932.33"}, {"B5", "987.77"}, {"C6", "1046.50"}, {"C#6/Db6", "1108.73"},
            {"D6", "1174.66"}, {"D#6/Eb6", "1244.51"}, {"E6", "1318.51"}, {"F6", "1396.91"}, {"F#6/Gb6", "1479.98"},
            {"G6", "1567.98"}, {"G#6/Ab6", "1661.22"}, {"A6", "1760.00"}, {"A#6/Bb6", "1864.66"}, {"B6", "1975.53"},
            {"C7", "2093.00"}, {"C#7/Db7", "2217.46"}, {"D7", "2349.32"}, {"D#7/Eb7", "2489.02"}, {"E7", "2637.02"},
            {"F7", "2793.83"}, {"F#7/Gb7\t", "2959.96"}, {"G7", "3135.96"}, {"G#7/Ab7", "3322.44"}, {"A7", "3520.00"},
            {"A#7/Bb7", "3729.31"}, {"B7", "3951.07"}, {"C8", "4186.01"}, {"C#8/Db8", "4434.92"}, {"D8", "4698.64"},
            {"D#8/Eb8", "4978.03"}};

    public NoteWriter(int freq) {
        frequency = freq;
    }

    public void addNote(int f) {
        notesVector.add((Object) f);
    }

    // Need to continue writing so that vector is written into a text file, with the location/argument set
    // by the user and, i guess passed on from the command line

    public void writeFile() {
        try {
            file = new File("DARTResults.txt");
            out = new BufferedWriter(new FileWriter(file));
            out.write("DART RESULTS");
            out.newLine();
            out.newLine();
            System.out.println("notesVector.size() = " + notesVector.size());

            for (int i = 0; i < notesVector.size(); i++) {
                if (i < notesVector.size()) {
                    out.write(notesVector.elementAt(i).toString() + ", ");
                }
                if (i == notesVector.size()) {
                    out.write(notesVector.elementAt(i).toString());
                }
            }
            out.newLine();
            out.newLine();

            for (int i = 0; i < notesArray.length; i++) {
                if (i < notesArray.length) {
                    out.write(notesArray[i] + ", ");
                } else {
                    out.write(notesArray[i]);
                }
            }
            out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
    * This array will find the closest note to a given frequency in the object array.
    */

    public void findNoteMap() {

        Object[] criteria = new Object[notesVector.size()];
        notesArray = new String[notesVector.size()];
        notesVector.toArray(criteria);

        for (int i = 0; i < notesVector.size(); i++) { // go through each value in notesVector
            int arrayFreq;
            int high = 0;
            int low = 0;

            for (int j = 0; j < noteMapArray.length; j++) { // for each value in 2D note mape array
                arrayFreq = Integer.parseInt(criteria[i].toString()); // arrayFreq = freq to be comapred with 2D array

                if (j > 0) {
                    int highBound = (int) Double.parseDouble(noteMapArray[j][1]);
                    int lowBound = (int) Double.parseDouble(noteMapArray[j - 1][1]);

                    if ((lowBound < arrayFreq) && (arrayFreq < highBound)) {
                        high = (int) Double.parseDouble(noteMapArray[j][1].toString());
                        low = (int) Double.parseDouble(noteMapArray[j - 1][1].toString());

                        if ((arrayFreq - low) < (high - arrayFreq)) {
                            note = (noteMapArray[j - 1][0].toString()); // note = the lower note
                        } else {
                            note = (noteMapArray[j][0].toString()); // note = the higher note
                        }
                        notesArray[i] = note;
                    }
                    if (highBound == arrayFreq) {
                        note = (noteMapArray[j][0].toString()); // note = the highernote
                        //System.out.println("notes = " + note);
                        notesArray[i] = note;
                    }
                } else {
                }
            } // for loop ends
        } // for loop ends

//        for (int i = 0; i < notesArray.length; i++) {
//            System.out.println("notesArray = " + notesArray[i]);
//        }
    }
}
