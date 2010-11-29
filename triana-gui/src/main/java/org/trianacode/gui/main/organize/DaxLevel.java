package org.trianacode.gui.main.organize;

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

    public int getLevelNumber(){
        return levelNumber;
    }

    public void setLevelNumber(int number){
        levelNumber = number;
    }

    private int levelSize(){
        return duos.size();
    }

    public void addUnit(DaxUnitObject duo) {
        duo.setLevel(this);
        duo.setRow(getFreeRow());

        duos.add(duo);

        System.out.println("Added unit " + duo + " to level " + getLevelNumber() + ". " +
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
        System.out.println("Last used row in level : " + getLevelNumber() + " is : " + freeRow + ". (" + duos.size() + " objects in level)");

        return (freeRow +1);
    }

    public void removeDUO(DaxUnitObject duo) {
        duos.remove(duo);
    }
}
