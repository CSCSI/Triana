package org.trianacode.http;

import org.thinginitself.http.Http;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.streamable.StreamableData;
import org.thinginitself.streamable.StreamableObject;

import java.io.Serializable;

/**
 * Make a Java object available via restless.
 *
 * User: scmijt
 * Date: Jul 24, 2010
 * Time: 1:39:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataResource extends Resource {

    Serializable object;

    public DataResource(String urlName, Serializable object, HttpPeer webPeer) {
        super(urlName, Http.Method.GET);

        System.out.println("Deploying an object with identifier " + urlName);
        
        webPeer.addTarget(this);
        this.object=object;
    }

    public void onGet(RequestContext context) {
        StringBuffer item= new StringBuffer();
            context.setResponseEntity(new StreamableObject(object, "text/plain"));
    }
    
}
