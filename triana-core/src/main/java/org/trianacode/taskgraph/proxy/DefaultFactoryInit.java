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

import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskGraphFactoryImp;
import org.trianacode.taskgraph.proxy.java.JavaConstants;
import org.trianacode.taskgraph.proxy.java.JavaProxyInstantiator;
import org.trianacode.taskgraph.service.RunnableTaskFactory;

/**
 * The default ProxyFactory, TaskGraphManager and ServiceManager setup.
 */


public class DefaultFactoryInit {

    private static boolean proxyinit = false;
    private static boolean taskgraphinit = false;

    /**
     * Initialises the proxy factory with the default instantiators. Repeat calls to this method have no effect.
     */
    public static void initProxyFactory() {
        if (!proxyinit) {
            proxyinit = true;
            ProxyFactory.registerInstantiator(JavaConstants.JAVA_PROXY_TYPE, new JavaProxyInstantiator());

/*
            FileProxyInstantiator fileinstant = new FileProxyInstantiator();

            ProxyFactory.registerInstantiator(WSConstants.WEB_SERVICE_PROXY_TYPE, new WSProxyInstantiator());
            ProxyFactory.registerInstantiator(WSRFPropertyConstants.WSRF_PROPERTY_PROXY_TYPE, new WSRFPropertyProxyInstantiator());
            ProxyFactory.registerInstantiator(WSNotificationConstants.WS_NOTIFICATION_PROXY_TYPE, new WSNotificationProxyInstantiator());
            ProxyFactory.registerInstantiator(P2PSConstants.P2PS_PROXY_TYPE, new P2PSProxyInstantiator());
            ProxyFactory.registerInstantiator(JobConstants.JOB_PROXY_TYPE, new JobProxyInstantiator());
            ProxyFactory.registerInstantiator(TransientConstants.TRANSIENT_PROXY_TYPE, new TransientProxyInstantiator());
            ProxyFactory.registerInstantiator(ProtoServiceConstants.PROTO_SERVICE_PROXY_TYPE, new ProtoServiceProxyInstantiator());
            ProxyFactory.registerInstantiator(TemplateConstants.TEMPLATE_PROXY_TYPE, new TemplateProxyInstantiator());
            ProxyFactory.registerInstantiator(FileConstants.FILE_PROXY_TYPE, fileinstant);
            ProxyFactory.registerInstantiator(FileConstants.FILE_LIST_PROXY_TYPE, fileinstant);
            ProxyFactory.registerInstantiator(FileConstants.LIST_FILES_PROXY_TYPE, fileinstant);
            ProxyFactory.registerInstantiator(FileConstants.SPLIT_FILES_PROXY_TYPE, fileinstant);
            ProxyFactory.registerInstantiator(ContextConstants.CONTEXT_PROXY_TYPE, new ContextProxyInstantiator());*/
        }
    }

    /**
     * Initialises the default and non-runnable factories in the taskgraph manager. Repeat calls to this method have no
     * effect.
     */
    public static void initTaskGraphManager(TrianaProperties props) {
        if (!taskgraphinit) {
            taskgraphinit = true;

            TaskGraphFactoryImp defaultfactory = new TaskGraphFactoryImp(props);
            RunnableTaskFactory javafactory = new RunnableTaskFactory();

            defaultfactory.registerTaskGraphFactory(JavaConstants.JAVA_PROXY_TYPE, javafactory);

            TaskGraphFactoryImp nonrunfactory = new TaskGraphFactoryImp(props);
            nonrunfactory.registerTaskGraphFactory(JavaConstants.JAVA_PROXY_TYPE, new TaskFactoryImp(javafactory));
            TaskGraphManager.registerTaskGraphFactory(TaskGraphManager.DEFAULT_FACTORY_TYPE, defaultfactory);
            TaskGraphManager.registerTaskGraphFactory(TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE, nonrunfactory);
            //TaskGraphManager.registerTaskGraphFactory(TaskGraphManager.TOOL_DEF_FACTORY_TYPE, new ToolDefFactory());
/*

            GAPTaskFactory p2psfactory = new GAPTaskFactory(GAPPeerTypes.P2PS);
            GAPTaskFactory wsfactory = new GAPTaskFactory(GAPPeerTypes.WEB_SERVICES);
            WSRFPropertyTaskFactory wsrffactory = new WSRFPropertyTaskFactory();
            WSNotificationTaskFactory wsnotification = new WSNotificationTaskFactory();
            GATTaskFactory gatfactory = new GATTaskFactory();
            GMSTaskFactory gmsfactory = new GMSTaskFactory();
            FileTaskFactory filefactory = new FileTaskFactory();
            ContextTaskFactory contextfactory = new ContextTaskFactory();



            defaultfactory.registerTaskGraphFactory(TransientConstants.TRANSIENT_PROXY_TYPE, nonrunnablefactory);
            defaultfactory.registerTaskGraphFactory(ProtoServiceConstants.PROTO_SERVICE_PROXY_TYPE, nonrunnablefactory);
            defaultfactory.registerTaskGraphFactory(TemplateConstants.TEMPLATE_PROXY_TYPE, nonrunnablefactory);
            defaultfactory.registerTaskGraphFactory(P2PSConstants.P2PS_PROXY_TYPE, p2psfactory);
            defaultfactory.registerTaskGraphFactory(WSConstants.WEB_SERVICE_PROXY_TYPE, wsfactory);
            defaultfactory.registerTaskGraphFactory(WSRFPropertyConstants.WSRF_PROPERTY_PROXY_TYPE, wsrffactory);
            defaultfactory.registerTaskGraphFactory(WSNotificationConstants.WS_NOTIFICATION_PROXY_TYPE, wsnotification);
            defaultfactory.registerTaskGraphFactory(JobConstants.JOB_PROXY_TYPE, gatfactory);
            defaultfactory.registerTaskGraphFactory(JobConstants.JOB_PROXY_TYPE, gmsfactory);
            defaultfactory.registerTaskGraphFactory(FileConstants.FILE_PROXY_TYPE, filefactory);
            defaultfactory.registerTaskGraphFactory(FileConstants.FILE_LIST_PROXY_TYPE, deffactory);
            defaultfactory.registerTaskGraphFactory(FileConstants.LIST_FILES_PROXY_TYPE, deffactory);
            defaultfactory.registerTaskGraphFactory(FileConstants.SPLIT_FILES_PROXY_TYPE, deffactory);
            defaultfactory.registerTaskGraphFactory(ContextConstants.CONTEXT_PROXY_TYPE, contextfactory);


            nonrunfactory.registerTaskGraphFactory(TransientConstants.TRANSIENT_PROXY_TYPE, new TaskFactoryImp(nonrunnablefactory));
            nonrunfactory.registerTaskGraphFactory(ProtoServiceConstants.PROTO_SERVICE_PROXY_TYPE, new TaskFactoryImp(nonrunnablefactory));
            nonrunfactory.registerTaskGraphFactory(TemplateConstants.TEMPLATE_PROXY_TYPE, new TaskFactoryImp(nonrunnablefactory));
            nonrunfactory.registerTaskGraphFactory(WSConstants.WEB_SERVICE_PROXY_TYPE, new TaskFactoryImp(wsfactory));
            nonrunfactory.registerTaskGraphFactory(WSRFPropertyConstants.WSRF_PROPERTY_PROXY_TYPE, new TaskFactoryImp(wsrffactory));
            nonrunfactory.registerTaskGraphFactory(WSNotificationConstants.WS_NOTIFICATION_PROXY_TYPE, new TaskFactoryImp(wsnotification));
            nonrunfactory.registerTaskGraphFactory(P2PSConstants.P2PS_PROXY_TYPE, new TaskFactoryImp(p2psfactory));
            nonrunfactory.registerTaskGraphFactory(JobConstants.JOB_PROXY_TYPE, new TaskFactoryImp(gatfactory));
            nonrunfactory.registerTaskGraphFactory(JobConstants.JOB_PROXY_TYPE, new TaskFactoryImp(gmsfactory));
            nonrunfactory.registerTaskGraphFactory(FileConstants.FILE_PROXY_TYPE, new TaskFactoryImp(filefactory));
            nonrunfactory.registerTaskGraphFactory(FileConstants.FILE_LIST_PROXY_TYPE, deffactory);
            nonrunfactory.registerTaskGraphFactory(FileConstants.LIST_FILES_PROXY_TYPE, deffactory);
            nonrunfactory.registerTaskGraphFactory(FileConstants.SPLIT_FILES_PROXY_TYPE, deffactory);
            nonrunfactory.registerTaskGraphFactory(ContextConstants.CONTEXT_PROXY_TYPE, contextfactory);

            */
        }
    }

}
