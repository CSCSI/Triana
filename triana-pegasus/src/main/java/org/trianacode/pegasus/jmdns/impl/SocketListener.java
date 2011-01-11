// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;

/**
 * Listen for multicast packets.
 */
class SocketListener extends Thread {
    static Logger           logger = Logger.getLogger(SocketListener.class.getName());

    /**
     *
     */
    private final org.trianacode.pegasus.jmdns.impl.JmDNSImpl _jmDNSImpl;

    /**
     * @param jmDNSImpl
     */
    SocketListener(org.trianacode.pegasus.jmdns.impl.JmDNSImpl jmDNSImpl) {
        super("SocketListener(" + (jmDNSImpl != null ? jmDNSImpl.getName() : "") + ")");
        this.setDaemon(true);
        this._jmDNSImpl = jmDNSImpl;
    }

    @Override
    public void run() {
        try {
            byte buf[] = new byte[DNSConstants.MAX_MSG_ABSOLUTE];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (!this._jmDNSImpl.isCanceling() && !this._jmDNSImpl.isCanceled()) {
                packet.setLength(buf.length);
                this._jmDNSImpl.getSocket().receive(packet);
                if (this._jmDNSImpl.isCanceling() || this._jmDNSImpl.isCanceled()) {
                    break;
                }
                try {
                    if (this._jmDNSImpl.getLocalHost().shouldIgnorePacket(packet)) {
                        continue;
                    }

                    org.trianacode.pegasus.jmdns.impl.DNSIncoming msg = new org.trianacode.pegasus.jmdns.impl.DNSIncoming(packet);
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(this.getName() + ".run() JmDNS in:" + msg.print(true));
                    }
                    if (msg.isQuery()) {
                        if (packet.getPort() != DNSConstants.MDNS_PORT) {
                            this._jmDNSImpl.handleQuery(msg, packet.getAddress(), packet.getPort());
                        }
                        this._jmDNSImpl.handleQuery(msg, this._jmDNSImpl.getGroup(), DNSConstants.MDNS_PORT);
                    } else {
                        this._jmDNSImpl.handleResponse(msg);
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING, this.getName() + ".run() exception ", e);
                }
            }
        } catch (IOException e) {
            if (!this._jmDNSImpl.isCanceling() && !this._jmDNSImpl.isCanceled()) {
                logger.log(Level.WARNING, this.getName() + ".run() exception ", e);
                this._jmDNSImpl.recover();
            }
        }
        // jP: 20010-01-18. Per issue #2933183. If this thread was stopped
        // by closeMulticastSocket, we need to signal the other party via
        // the jmDNS monitor. The other guy will then check to see if this
        // thread has died.
        // Note: This is placed here to avoid locking the IoLock object and
        // 'this' instance together.
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.getName() + ".run() exiting.");
        }
        synchronized (this._jmDNSImpl) {
            this._jmDNSImpl.notifyAll();
        }
    }

    public org.trianacode.pegasus.jmdns.impl.JmDNSImpl getDns() {
        return _jmDNSImpl;
    }

}