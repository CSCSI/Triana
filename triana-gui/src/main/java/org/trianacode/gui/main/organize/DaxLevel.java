package org.trianacode.gui.main.organize;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 28, 2010
 * Time: 7:21:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxLevel {
    ArrayList<DaxUnitObject> duos = new ArrayList();
    private int levelNumber = 0;
    private int width = 10;
    private int height = 10;

    private void log(String text){
                Log log = Loggers.DEV_LOGGER;
        log.debug(text);
        System.out.println(text);
    }

    public int getLevelNumber(){
        return levelNumber;
    }

    public void setLevelNumber(int number){
        levelNumber = number;
    }

    public int levelSize(){
        return duos.size();
    }

    public void addUnit(DaxUnitObject duo) {
        duo.setLevel(this);
        duo.setRow(getFreeRow());

        String name = duo.getTask().getToolName();
        if(name.length() > getWidth()){
            setWidth(name.length());
        }

        int maxNodes = (duo.getTask().getInputNodeCount() > duo.getTask().getOutputNodeCount()) ?
                duo.getTask().getInputNodeCount() : duo.getTask().getOutputNodeCount();
        setHeight( (maxNodes > height) ? maxNodes : height);

        duos.add(duo);

        log("Added unit " + duo + " to level " + getLevelNumber() + ". " +
                "Level now contains " + levelSize() + " units.");
    }

    public int getFreeRow(){
        int freeRow = 0;
        for(DaxUnitObject duo : duos){
            int thisRow = duo.getRow();
            if(thisRow >= freeRow){
                freeRow = thisRow;
            }

        }
        log("Last used row in level : " + getLevelNumber() + " is : " + freeRow + ". (" + duos.size() + " objects in level)");

        return (freeRow +1);
    }

    public void removeDUO(DaxUnitObject duo) {
        duos.remove(duo);
    }

    public void tidyupRows(){

        for( DaxUnitObject duo : duos){
            boolean changeOccured = true;
            while(changeOccured){
                int row = duo.getRow();
                boolean occupied = false;
                for( DaxUnitObject otherDUO : duos){
                    if (otherDUO.getRow() == (row -1) && occupied == false){
                        log("Task " + duo.toString() + " has task " + otherDUO.toString() + " above it.");
                        occupied = true;
                    }
                }
                if(occupied == false && (row -1 > 0)){
                    log("Able to move duo " + duo.toString() + " up one row");
                    duo.setRow(row -1);
                    duo.setParams();
                    changeOccured = true;
                }
                else{
                    changeOccured = false;
                }
            }
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
}
