package org.trianacode.shiwa;

import org.shiwa.fgi.iwir.IWIR;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 22/02/2011
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class IwirWriter {
    public IwirWriter(){
        IWIR iwir = new IWIR();
        iwir.setWfname("test");

        System.out.println("");
        org.shiwa.fgi.iwir.examples.CrossProduct.main(new String[0]);

    }

    public static void main(String[] args) {
        new IwirWriter();
    }
}
