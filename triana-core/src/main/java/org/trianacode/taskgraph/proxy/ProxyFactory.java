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

package org.trianacode.taskgraph.proxy;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.util.Hashtable;
import java.util.Map;


/**
 * A factory class for instantiating proxies from a map of its instance details.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */


public class ProxyFactory {

    private static final Hashtable insts = new Hashtable();
    static Log logger = Loggers.CONFIG_LOGGER;

    /**
     * Initialises the proxy factory
     */
    public static void initProxyFactory() {
        logger.debug("Init Proxy Factory");
        DefaultFactoryInit.initProxyFactory();

        /*Object[] plugins = PluginLoader.getInstance().getInstances(PluginInit.class);

        for (int count = 0; count < plugins.length; count++)
            ((PluginInit) plugins[count]).initProxyFactory();*/
    }


    /**
     * Registers a ProxyInstantiator for the specified proxy type
     */
    public static void registerInstantiator(String type, ProxyInstantiator inst) {
        insts.put(type, inst);
    }

    /**
     * Unregisters the ProxyInstantiator for the specified proxy type
     */
    public static void unregisterInstantiator(String type) {
        insts.remove(type);
    }

    /**
     * @return the instantiator for the specified type
     */
    public static ProxyInstantiator getProxyInstantiator(String type) {
        return (ProxyInstantiator) insts.get(type);
    }


    /**
     * @return an array of the registered proxy types
     */
    public static String[] getProxyTypes() {
        return (String[]) insts.keySet().toArray(new String[insts.keySet().size()]);
    }

    /**
     * @return true if the specified type is a registered proxy type
     */
    public static boolean isProxyType(String type) {
        return insts.containsKey(type);
    }


    /**
     * @return an instantiated proxy from a map of its instance details
     */
    public static Proxy createProxy(String type, Map instdetails) throws ProxyInstantiationException {
        if (!isProxyType(type)) {
            throw (new RuntimeException("No Proxy Instantiator registered for type " + type));
        }

        return getProxyInstantiator(type).createProxy(type, instdetails);
    }

    /**
     * @return an instantiated proxy from a map of its instance details
     */
    public static Proxy cloneProxy(Proxy proxy) throws ProxyInstantiationException {
        if (proxy == null) {
            return null;
        }

        if (!isProxyType(proxy.getType())) {
            throw (new RuntimeException("No Proxy Instantiator registered for type " + proxy.getType()));
        }

        return getProxyInstantiator(proxy.getType()).cloneProxy(proxy);
    }

}
