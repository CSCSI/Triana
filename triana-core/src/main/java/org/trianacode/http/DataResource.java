package org.trianacode.http;

import org.thinginitself.http.Http;
import org.thinginitself.http.Resource;

import java.io.Serializable;

/**
 * Make a Java object available via restless.
 * <p/>
 * User: scmijt Date: Jul 24, 2010 Time: 1:39:46 PM
 */
public class DataResource extends Resource {

    private Serializable object;

    public DataResource(String urlName, Serializable object) {
        super(urlName, Http.Method.GET);
        System.out.println("Deploying an object with identifier " + urlName);
        this.object = object;
    }

    public Serializable getObject() {
        return object;
    }


}
