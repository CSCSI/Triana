// Licensed under Apache License version 2.0
package org.trianacode.pegasus.jmdns.impl.tasks.resolver;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;
import org.trianacode.pegasus.jmdns.impl.JmDNSImpl;
import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;
import org.trianacode.pegasus.jmdns.impl.tasks.DNSTask;

/**
 * This is the root class for all resolver tasks.
 *
 * @author Pierre Frisch
 */
public abstract class DNSResolverTask extends DNSTask {
    private static Logger logger = Logger.getLogger(DNSResolverTask.class.getName());

    /**
     * Counts the number of queries being sent.
     */
    protected int         _count = 0;

    /**
     * @param jmDNSImpl
     */
    public DNSResolverTask(JmDNSImpl jmDNSImpl) {
        super(jmDNSImpl);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + " count: " + _count;
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#start(java.util.Timer)
     */
    @Override
    public void start(Timer timer) {
        if (!this.getDns().isCanceling() && !this.getDns().isCanceled()) {
            timer.schedule(this, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.QUERY_WAIT_INTERVAL, DNSConstants.QUERY_WAIT_INTERVAL);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        try {
            if (this.getDns().isCanceling() || this.getDns().isCanceled()) {
                this.cancel();
            } else {
                if (_count++ < 3) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this.getName() + ".run() JmDNS " + this.description());
                    }
                    org.trianacode.pegasus.jmdns.impl.DNSOutgoing out = new org.trianacode.pegasus.jmdns.impl.DNSOutgoing(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_QR_QUERY);
                    out = this.addQuestions(out);
                    if (this.getDns().isAnnounced()) {
                        out = this.addAnswers(out);
                    }
                    if (!out.isEmpty()) {
                        this.getDns().send(out);
                    }
                } else {
                    // After three queries, we can quit.
                    this.cancel();
                }
            }
        } catch (Throwable e) {
            logger.log(Level.WARNING, this.getName() + ".run() exception ", e);
            this.getDns().recover();
        }
    }

    /**
     * Overridden by subclasses to add questions to the message.<br/>
     * <b>Note:</b> Because of message size limitation the returned message may be different than the message parameter.
     *
     * @param out
     *            outgoing message
     * @return the outgoing message.
     * @throws IOException
     */
    protected abstract org.trianacode.pegasus.jmdns.impl.DNSOutgoing addQuestions(DNSOutgoing out) throws IOException;

    /**
     * Overridden by subclasses to add questions to the message.<br/>
     * <b>Note:</b> Because of message size limitation the returned message may be different than the message parameter.
     *
     * @param out
     *            outgoing message
     * @return the outgoing message.
     * @throws IOException
     */
    protected abstract DNSOutgoing addAnswers(DNSOutgoing out) throws IOException;

    /**
     * Returns a description of the resolver for debugging
     *
     * @return resolver description
     */
    protected abstract String description();

}
