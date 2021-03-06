/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */

package org.trianacode.taskgraph.proxy.java;


import org.trianacode.taskgraph.BeanUnit;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.annotation.AnnotatedUnitWrapper;
import org.trianacode.taskgraph.annotation.AnnotationProcessor;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.ClassLoaders;

import java.util.HashMap;
import java.util.Map;

/**
 * The proxy for java units.
 * <p/>
 * Ian T changed so that errors are thrown not consumed and ignored ....
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */


public class JavaProxy implements Proxy {

    private String unitname;
    private String unitpackage;
    private Unit unit;

    public JavaProxy(Object unit, String unitname, String unitpackage) throws ProxyInstantiationException {

        this.unitname = unitname;
        this.unitpackage = unitpackage;
        try {
            this.unit = createUnit(unit);
        } catch (ProxyInstantiationException e) {
            createUnit();
        }
    }


    public JavaProxy(String unitname) throws ProxyInstantiationException {
        this.unitname = unitname;
        this.unitpackage = "";
        createUnit();
    }

    public JavaProxy(String unitname, String unitpackage) throws ProxyInstantiationException {
        this.unitname = hackUnitName(unitname);
        this.unitpackage = unitpackage;
        createUnit();
    }

    public JavaProxy(Map instdetails) throws ProxyInstantiationException {
        if (!instdetails.containsKey(JavaConstants.UNIT_NAME)) {
            throw (new ProxyInstantiationException("Invalid Java Unit Instance Details: Missing unit name"));
        }

        if (!instdetails.containsKey(JavaConstants.UNIT_PACKAGE)) {
            throw (new ProxyInstantiationException("Invalid Java Unit Instance Details: Missing unit package"));
        }

        this.unitname = (String) instdetails.get(JavaConstants.UNIT_NAME);
        this.unitname = hackUnitName(unitname);
        this.unitpackage = (String) instdetails.get(JavaConstants.UNIT_PACKAGE);
    }

    private String hackUnitName(String unitname) {
        return unitname;
        /*if (unitname.indexOf(".") > -1) {
            unitname = unitname.substring(unitname.lastIndexOf(".") + 1, unitname.length());
        }
        String num = unitname.substring(unitname.length() - 1, unitname.length());
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return unitname;
        }
        return unitname.substring(0, unitname.length() - 1);*/
    }


    /**
     * @return the type of the proxy
     */
    public String getType() {
        return JavaConstants.JAVA_PROXY_TYPE;
    }

    private void createUnit() throws ProxyInstantiationException {
        try {
            Class cls = ClassLoaders.forName(getFullUnitName());
            Object o = cls.newInstance();
            if (Unit.class.isAssignableFrom(cls)) {
                unit = (Unit) o;
            } else {
                unit = AnnotationProcessor.createUnit(o);
                if (unit == null) {
                    unit = new BeanUnit(cls);
                }
            }
        } catch (Exception e) {
            throw new ProxyInstantiationException(e);
        }
    }

    private Unit createUnit(Object o) throws ProxyInstantiationException {
//        System.out.println("JavaProxy.createUnit ENTER with object " + o.getClass());
        if (o instanceof Unit && !(o instanceof AnnotatedUnitWrapper)) {
            return (Unit) o;
        } else {
            return AnnotationProcessor.createUnit(o);
        }
    }


    /**
     * @return the unit name
     */
    public String getUnitName() {
        return unitname;
    }

    /**
     * @return the unit package
     */
    public String getUnitPackage() {
        return unitpackage;
    }

    public String getFullUnitName() {
        if (unitpackage != null && unitpackage.length() > 0) {
            return unitpackage + "." + unitname;
        }
        return unitname;
    }

    public boolean hasUnit() {
        return unit != null;
    }

    public Unit getUnit() {
        return unit;
    }

    /**
     * @return a map of the instance details for this proxy
     */
    public Map<String, Object> getInstanceDetails() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(JavaConstants.UNIT_NAME, unitname);
        map.put(JavaConstants.UNIT_PACKAGE, unitpackage);
        return map;

    }


    public String toString() {
        String str = unitname;

        if (!unitpackage.equals("")) {
            str += " (" + unitpackage + ")";
        }

        return str;
    }

}
