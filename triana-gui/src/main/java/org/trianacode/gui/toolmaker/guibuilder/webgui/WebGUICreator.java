package org.trianacode.gui.toolmaker.guibuilder.webgui;

/**
 * Created by IntelliJ IDEA. User: eddie Date: Jul 27, 2010 Time: 10:53:42 AM To change this template use File |
 * Settings | File Templates.
 */

public class WebGUICreator {

    public WebGUICreator(String input) {
        guiInit(input);
    }

    public String guiInit(String input) {

        String htmloutput = null;
        if (input != null) {

            htmloutput = "<form>";

            String output = "";
            int stringlength = input.length();
            int nooflines;

            // Create a string array with each line in the string as an element in the array
            String[] lines = input.split("\n");
            System.out.println("no of lines = " + lines.length);

            for (int i = 0; i < lines.length; i++) {

                String choice = "Choice";
                String checkbox = "Checkbox";
                String scroller = "Scroller";
                String textfield = "TextField";

                String currentline = lines[i];

                int index1 = currentline.indexOf(choice);

                if (index1 != -1) {
                    choice(currentline);
                }

                int index2 = currentline.indexOf(checkbox);

                if (index2 != -1) {
                    checkbox(currentline);
                }

                int index3 = currentline.indexOf(scroller);

                if (index3 != -1) {
                    scroller(currentline);
                }

                int index4 = currentline.indexOf(textfield);

                if (index4 != -1) {
                    textfield(currentline);
                }
            }
        }
        return htmloutput;
    }

    public String textfield(String currentline) {

        //Text Field Example! $title whatever TextField This is the default text\n
        //This is the title $title test2 TextField 22\n

        String textfieldbox = currentline;
        String title = "";
        String output;
        String defaulttext = "";

        for (int i = 0; i < textfieldbox.length(); i++) {
            int index = textfieldbox.indexOf("$");
            title = textfieldbox.substring(0, (index - 1));
            int index2 = textfieldbox.indexOf("TextField");
            defaulttext = textfieldbox.substring(index2 + 1);
        }

        String[] stri = textfieldbox.split(" ");
        //System.out.println(Arrays.toString(stri));

        // Setting the 
        for (int i = 0; i < stri.length; i++) {

        }

        output = title + " <br>" + "<input type=\"text\" value=\"" + defaulttext + "\" />";

        return output;
    }

    public String checkbox(String input) {
        //<input type="checkbox" name="vehicle" value="Bike" /> I have a bike<br />
    }

    public String choice(String input) {

        //LFO Type $title LFOType Choice [sinusoidal] [triangular]\n";
        String choicebox = input;
        String title;
        String output;

        for (int i = 0; i < choicebox.length(); i++) {
            int index = choicebox.indexOf("$");
            title = choicebox.substring(0, (index - 1));
        }

        int index2 = choicebox.indexOf("$title");
        <select name = "mydropdown" > // whatever is after $title

        for
        each thing
        in square
        brackets
        {
            <option value = "Milk" > Fresh
            Milk</option >
            <option value = "Cheese" > Old
            Cheese</option >
            <option value = "Bread" > Hot
            Bread</option >
        }


        output = title + " <br>";
        return output;
    }


    public String scroller(String input) {
    }


    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }
}
