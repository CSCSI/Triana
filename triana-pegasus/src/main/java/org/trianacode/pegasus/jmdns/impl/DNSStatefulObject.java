// Licensed under Apache License version 2.0
package org.trianacode.pegasus.jmdns.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;
import org.trianacode.pegasus.jmdns.impl.constants.DNSState;
import org.trianacode.pegasus.jmdns.impl.tasks.DNSTask;

/**
 * Sets of methods to manage the state machine.<br/>
 * <b>Implementation note:</b> This interface is accessed from multiple threads. The implementation must be thread safe.
 *
 * @author Pierre Frisch
 */
public interface DNSStatefulObject {

    public static class DefaultImplementation extends ReentrantLock implements DNSStatefulObject {
        private static Logger       logger           = Logger.getLogger(DefaultImplementation.class.getName());

        private static final long   serialVersionUID = -3264781576883412227L;

        private volatile JmDNSImpl  _dns;

        protected volatile org.trianacode.pegasus.jmdns.impl.tasks.DNSTask _task;

        protected volatile org.trianacode.pegasus.jmdns.impl.constants.DNSState _state;

        public DefaultImplementation() {
            super();
            _dns = null;
            _task = null;
            _state = DNSState.PROBING_1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JmDNSImpl getDns() {
            return this._dns;
        }

        protected void setDns(JmDNSImpl dns) {
            this._dns = dns;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void associateWithTask(org.trianacode.pegasus.jmdns.impl.tasks.DNSTask task, DNSState state) {
            if (this._task == null && this._state == state) {
                this.lock();
                try {
                    if (this._task == null && this._state == state) {
                        this.setTask(task);
                    }
                } finally {
                    this.unlock();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeAssociationWithTask(DNSTask task) {
            if (this._task == task) {
                this.lock();
                try {
                    if (this._task == task) {
                        this.setTask(null);
                    }
                } finally {
                    this.unlock();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isAssociatedWithTask(DNSTask task, DNSState state) {
            this.lock();
            try {
                return this._task == task && this._state == state;
            } finally {
                this.unlock();
            }
        }

        protected void setTask(org.trianacode.pegasus.jmdns.impl.tasks.DNSTask task) {
            this._task = task;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean advanceState(org.trianacode.pegasus.jmdns.impl.tasks.DNSTask task) {
            boolean result = true;
            if (this._task == task) {
                this.lock();
                try {
                    if (this._task == task) {
                        this._state = this._state.advance();
                    } else {
                        logger.warning("Trying to advance state whhen not the owner. owner: " + this._task + " perpetrator: " + task);
                    }
                } finally {
                    this.unlock();
                }
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean revertState() {
            boolean result = true;
            if (!this.willCancel()) {
                this.lock();
                try {
                    if (!this.willCancel()) {
                        this._state = this._state.revert();
                        this.setTask(null);
                    }
                } finally {
                    this.unlock();
                }
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean cancelState() {
            boolean result = false;
            if (!this.willCancel()) {
                this.lock();
                try {
                    if (!this.willCancel()) {
                        this._state = DNSState.CANCELING_1;
                        this.setTask(null);
                        result = true;
                    }
                } finally {
                    this.unlock();
                }
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean recoverState() {
            boolean result = false;
            this.lock();
            try {
                this._state = org.trianacode.pegasus.jmdns.impl.constants.DNSState.PROBING_1;
                this.setTask(null);
            } finally {
                this.unlock();
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isProbing() {
            return this._state.isProbing();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isAnnouncing() {
            return this._state.isAnnouncing();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isAnnounced() {
            return this._state.isAnnounced();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCanceling() {
            return this._state.isCanceling();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCanceled() {
            return this._state.isCanceled();
        }

        private boolean willCancel() {
            return this._state.isCanceled() || this._state.isCanceling();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean waitForAnnounced(long timeout) {
            if (!this.isAnnounced() && !this.willCancel()) {
                try {
                    boolean finished = false;
                    long end = (timeout > 0 ? System.currentTimeMillis() + timeout : Long.MAX_VALUE);
                    while (!finished) {
                        boolean lock = this.tryLock(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.ANNOUNCE_WAIT_INTERVAL, TimeUnit.MILLISECONDS);
                        try {
                            finished = (this.isAnnounced() || this.willCancel() ? true : end <= System.currentTimeMillis());
                        } finally {
                            if (lock) {
                                this.unlock();
                            }
                        }
                    }
                } catch (final InterruptedException e) {
                    // empty
                }
            }
            if (!this.isAnnounced()) {
                if (this.willCancel()) {
                    logger.warning("Wait for announced cancelled: " + this);
                } else {
                    logger.warning("Wait for announced timed out: " + this);
                }
            }
            return this.isAnnounced();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean waitForCanceled(long timeout) {
            if (!this.isCanceled()) {
                try {
                    boolean finished = false;
                    long end = (timeout > 0 ? System.currentTimeMillis() + timeout : Long.MAX_VALUE);
                    while (!finished) {
                        boolean lock = this.tryLock(DNSConstants.ANNOUNCE_WAIT_INTERVAL, TimeUnit.MILLISECONDS);
                        try {
                            finished = (this.isCanceled() ? true : end <= System.currentTimeMillis());
                        } finally {
                            if (lock) {
                                this.unlock();
                            }
                        }
                    }
                } catch (final InterruptedException e) {
                    // empty
                }
            }
            if (!this.isCanceled()) {
                logger.warning("Wait for canceled timed out: " + this);
            }
            return this.isCanceled();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return (_dns != null ? "DNS: " + _dns.getName() : "NO DNS") + " state: " + _state + " task: " + _task;
        }

    }

    /**
     * Returns the DNS associated with this object.
     *
     * @return DNS resolver
     */
    public JmDNSImpl getDns();

    /**
     * Sets the task associated with this Object.
     *
     * @param task
     *            associated task
     * @param state
     *            state of the task
     */
    public void associateWithTask(DNSTask task, org.trianacode.pegasus.jmdns.impl.constants.DNSState state);

    /**
     * Remove the association of the task with this Object.
     *
     * @param task
     *            associated task
     */
    public void removeAssociationWithTask(DNSTask task);

    /**
     * Checks if this object is associated with the task and in the same state.
     *
     * @param task
     *            associated task
     * @param state
     *            state of the task
     * @return <code>true</code> is the task is associated with this object, <code>false</code> otherwise.
     */
    public boolean isAssociatedWithTask(DNSTask task, org.trianacode.pegasus.jmdns.impl.constants.DNSState state);

    /**
     * Sets the state and notifies all objects that wait on the ServiceInfo.
     *
     * @param task
     *            associated task
     * @return <code>true</code if the state was changed by this thread, <code>false</code> otherwise.
     * @see org.trianacode.pegasus.jmdns.impl.constants.DNSState#advance()
     */
    public boolean advanceState(DNSTask task);

    /**
     * Sets the state and notifies all objects that wait on the ServiceInfo.
     *
     * @return <code>true</code if the state was changed by this thread, <code>false</code> otherwise.
     * @see DNSState#revert()
     */
    public boolean revertState();

    /**
     * Sets the state and notifies all objects that wait on the ServiceInfo.
     *
     * @return <code>true</code if the state was changed by this thread, <code>false</code> otherwise.
     */
    public boolean cancelState();

    /**
     * Sets the state and notifies all objects that wait on the ServiceInfo.
     *
     * @return <code>true</code if the state was changed by this thread, <code>false</code> otherwise.
     */
    public boolean recoverState();

    /**
     * Returns true, if this is a probing state.
     *
     * @return <code>true</code> if probing state, <code>false</code> otherwise
     */
    public boolean isProbing();

    /**
     * Returns true, if this is an announcing state.
     *
     * @return <code>true</code> if announcing state, <code>false</code> otherwise
     */
    public boolean isAnnouncing();

    /**
     * Returns true, if this is an announced state.
     *
     * @return <code>true</code> if announced state, <code>false</code> otherwise
     */
    public boolean isAnnounced();

    /**
     * Returns true, if this is a canceling state.
     *
     * @return <code>true</code> if canceling state, <code>false</code> otherwise
     */
    public boolean isCanceling();

    /**
     * Returns true, if this is a canceled state.
     *
     * @return <code>true</code> if canceled state, <code>false</code> otherwise
     */
    public boolean isCanceled();

    /**
     * Waits for the object to be announced.
     *
     * @param timeout
     *            the maximum time to wait in milliseconds.
     * @return <code>true</code> if the object is announced, <code>false</code> otherwise
     */
    public boolean waitForAnnounced(long timeout);

    /**
     * Waits for the object to be canceled.
     *
     * @param timeout
     *            the maximum time to wait in milliseconds.
     * @return <code>true</code> if the object is canceled, <code>false</code> otherwise
     */
    public boolean waitForCanceled(long timeout);

}
