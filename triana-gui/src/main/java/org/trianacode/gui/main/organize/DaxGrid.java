package org.trianacode.gui.main.organize;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 28, 2010
 * Time: 7:31:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxGrid {
    private ArrayList<DaxLevel> levels = new ArrayList();

    private void log(String text){
        Log log = Loggers.DEV_LOGGER;
        log.debug(text);
  //      System.out.println(text);
    }

    public DaxLevel getLevel(int level) {
        for(DaxLevel thisLevel : levels){
            if (thisLevel.getLevelNumber() == level){
                return thisLevel;
            }
        }
        log("Level " + level + " doesn't exist. Creating new level.");

        return addLevel(level);
    }

    private DaxLevel addLevel (int level){
        DaxLevel newLevel = new DaxLevel();
        newLevel.setLevelNumber(level);
        addLevel(newLevel);
        return newLevel;
    }

    public void addLevel (DaxLevel newLevel){
        newLevel.setLevelNumber(newLevel.getLevelNumber());
        levels.add(newLevel);
        log("Added level " + newLevel.getLevelNumber() + ". " +
                "There are now " + numberOfLevels() + " levels in the grid.");
    }

    public int numberOfLevels(){
        return levels.size();
    }

    public int numberOfRows(){
        int rowMax = 0;
        for(DaxLevel thisLevel : levels){
            int levelRows = thisLevel.getFreeRow();
            if( levelRows > rowMax){
                rowMax = levelRows;
            }
        }
        return rowMax;
    }
}
