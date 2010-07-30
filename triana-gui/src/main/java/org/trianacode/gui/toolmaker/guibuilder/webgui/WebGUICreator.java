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

            htmloutput = "<form>\n";

            String output = "";
            int stringlength = input.length();
            int nooflines;

            // Create a string array with each line in the string as an element in the array
            String[] lines = input.split("\n");
            //System.out.println("no of lines = " + lines.length);

            for (int i = 0; i < lines.length; i++) {

                String choice = "Choice";
                String checkbox = "Checkbox";
                String scroller = "Scroller";
                String textfield = "TextField";
                String filechooser = "FileChooser";
                String textlabel = "Label";

                String currentline = lines[i];

                // Drop down menu - Done
                int index1 = currentline.indexOf(choice);
                if (index1 != -1) {
                    htmloutput += choice(currentline);
                }
                // Checkbox - Done
                int index2 = currentline.indexOf(checkbox);
                if (index2 != -1) {
                    htmloutput += checkbox(currentline);
                }
                // Scroller -
                int index3 = currentline.indexOf(scroller);
                if (index3 != -1) {
                    htmloutput += scroller(currentline);
                }
                // Text Box - Done
                int index4 = currentline.indexOf(textfield);
                if (index4 != -1) {
                    htmloutput += textfield(currentline);
                }
                // File browser -
                int index5 = currentline.indexOf(filechooser);
                if (index5 != -1) {
                    htmloutput += filechooser(currentline);
                }
                // Text Label - Done
                int index6 = currentline.indexOf(textlabel);
                if (index6 != -1) {
                    htmloutput += textlabel(currentline);
                }
            }
            htmloutput += "<br>\n<INPUT TYPE=SUBMIT VALUE=\"submit\">\n" + "</form>";
        }
        System.out.println(htmloutput);
        return htmloutput;
    }

    // Text Label Method
    public String textlabel(String currentline) {
        //guilines += "This is the written label's message: $title label Label hello\n";

        String textlabel = currentline;
        String title = "";
        String output;
        String defaulttext = "";

        for (int i = 0; i < textlabel.length(); i++) {
            int index = textlabel.indexOf("$");
            title = textlabel.substring(0, (index - 1));
            int index2 = textlabel.indexOf("Label");
            defaulttext = textlabel.substring(index2+5);
        }

        output = title + defaulttext;
        return output;
    }

    // File Browser Method
    public String filechooser(String currentline) {
        // guilines += "Please choose a file $title browserparamname File null *.*\n";
        //<input type="file" name="datafile" size="40">

        String filechoosertext = currentline;
        String title = "";
        String stringbuffer = "";
        String outputtemp = "";
        String output = "";
        String name = "";

        // Title is everything from beginning of string til the $ sign -1
        for (int i = 0; i < filechoosertext.length(); i++) {
            int index = filechoosertext.indexOf("$");
            title = filechoosertext.substring(0, (index - 1));
            int index2 = filechoosertext.indexOf("File");
            stringbuffer = filechoosertext.substring(index2+7);
        }

        String[] words = stringbuffer.split(" ");
        //This code is to find the name of the element
        String[] allwords = filechoosertext.split(" ");
        for (int i=0; i<allwords.length;i++){
            if(allwords[i].startsWith("$")){
                name = allwords[i+1];
            }
        }



        return output;

    }

    // Text Box Method
    public String textfield(String currentline) {

        //Text Field Example! $title whatever TextField This is the default text\n
        //This is the title $title test2 TextField 22\n
        //guilines += "Try some text! $title textbox1 TextField Default Input\n";

        String textfieldbox = currentline;
        String title = "";
        String output;
        String defaulttext = "";
        String name = "";

        for (int i = 0; i < textfieldbox.length(); i++) {
            int index = textfieldbox.indexOf("$");
            title = textfieldbox.substring(0, (index - 1));
            int index2 = textfieldbox.indexOf("TextField");
            defaulttext = textfieldbox.substring(index2);
        }

        // Split all the words up then find the one after the one with the dollar sign in it - that's the 'name'
        String[] allwords = textfieldbox.split(" ");
        for (int i=0; i<allwords.length;i++){
            if(allwords[i].startsWith("$")){
                name = allwords[i+1];
            }
        }

        //String[] stri = textfieldbox.split(" ");
        output = title + " <input type = \"text\" name = " + name + "\"" + "value=\"" + defaulttext + "\"/>";
        return output;
    }

    // CheckBox Method
    public String checkbox(String currentline) {

        String checkboxline = currentline;
        String title = "";
        String output;
        String checked = "";
        String value = "";
        String name= "";

        for (int i = 0; i < checkboxline.length(); i++) {
            int index = checkboxline.indexOf("$");
            title = checkboxline.substring(0, (index - 1));
            String lastWord = checkboxline.substring(checkboxline.lastIndexOf(' ') + 1);

            if (lastWord == "true"){
                checked = "checked=\"yes\"";
            }

            // Find last word...
            String[] words = checkboxline.split(" ");

            for (int j=0; j<words.length;j++){
                int index2 = checkboxline.indexOf("$");
                if (j == index2){
                    value = words[j+1];
                }
            }
        }

        String[] allwords = checkboxline.split(" ");
        for (int i=0; i<allwords.length;i++){
            if(allwords[i].startsWith("$")){
                name = allwords[i+1];
            }
        }

        output = title + " <input type =\"checkbox\" name =\"" + name + "\"" + checked + " " + value + "/>";
        return output;
    }

    // Drop Down Menu Method
    public String choice(String input) {

        //LFO Type $title LFOType Choice [sinusoidal] [triangular]\n";
        //guilines += "Please choose a vehicle $title choicebox Choice [Bike] [Car] [Train] [Bus] [Plane]\n";
        String choicebox = input;
        String title = "";
        String stringbuffer = "";
        String outputtemp = "";
        String output = "";
        String name = "";

        // Title is everything from beginning of string til the $ sign -1
        for (int i = 0; i < choicebox.length(); i++) {
            int index = choicebox.indexOf("$");
            title = choicebox.substring(0, (index - 1));
            int index2 = choicebox.indexOf("Choice");
            stringbuffer = choicebox.substring(index2+7);
        }

        String[] words = stringbuffer.split(" ");
        //This code is to find the name of the element
        String[] allwords = choicebox.split(" ");
        for (int i=0; i<allwords.length;i++){
            if(allwords[i].startsWith("$")){
                name = allwords[i+1];
            }
        }

        // For each word in the 'choices' array
        for (int j=0; j<words.length;j++){
            words[j] = words[j].replaceAll("\\[", "");
            words[j] = words[j].replaceAll("]", "");
                outputtemp += "<option value = \"" + j + "\">" +  words[j] + "</option>\n";
        }

        outputtemp += "</select>";
        output = title + "\n<select name = " + "\"" + name + "\"" + "</>" + "<br> \n" + outputtemp + " <br>";
        return output;
    }

    public String scroller(String input) {
        String output = "";
        return output;
    }


    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }
}
