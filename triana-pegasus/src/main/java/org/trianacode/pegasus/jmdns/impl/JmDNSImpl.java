// /Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl;

import org.trianacode.pegasus.jmdns.ServiceEvent;
import org.trianacode.pegasus.jmdns.ServiceInfo;
import org.trianacode.pegasus.jmdns.ServiceInfo.Fields;
import org.trianacode.pegasus.jmdns.ServiceListener;
import org.trianacode.pegasus.jmdns.ServiceTypeListener;
import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType;
import org.trianacode.pegasus.jmdns.impl.constants.DNSState;
import org.trianacode.pegasus.jmdns.impl.tasks.DNSTask;
import org.trianacode.pegasus.jmdns.impl.tasks.RecordReaper;
import org.trianacode.pegasus.jmdns.impl.tasks.Responder;
import org.trianacode.pegasus.jmdns.impl.tasks.resolver.ServiceInfoResolver;
import org.trianacode.pegasus.jmdns.impl.tasks.resolver.ServiceResolver;
import org.trianacode.pegasus.jmdns.impl.tasks.resolver.TypeResolver;
import org.trianacode.pegasus.jmdns.impl.tasks.state.Announcer;
import org.trianacode.pegasus.jmdns.impl.tasks.state.Canceler;
import org.trianacode.pegasus.jmdns.impl.tasks.state.Prober;
import org.trianacode.pegasus.jmdns.impl.tasks.state.Renewer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

// REMIND: multiple IP addresses

/**
 * mDNS implementation in Java.
 *
 * @author Arthur van Hoff, Rick Blair, Jeff Sonstein, Werner Randelshofer, Pierre Frisch, Scott Lewis
 */
public class JmDNSImpl extends org.trianacode.pegasus.jmdns.JmDNS implements DNSStatefulObject {
    private static Logger logger = Logger.getLogger(JmDNSImpl.class.getName());

    public enum Operation {
        Remove, Update, Add, RegisterServiceType, Noop
    }

    /**
     * This is the multicast group, we are listening to for multicast DNS messages.
     */
    private volatile InetAddress                                     _group;
    /**
     * This is our multicast socket.
     */
    private volatile MulticastSocket                                 _socket;

    /**
     * Used to fix live lock problem on unregister.
     */
    private volatile boolean                                         _closed = false;

    /**
     * Holds instances of JmDNS.DNSListener. Must by a synchronized collection, because it is updated from concurrent threads.
     */
    private final List<org.trianacode.pegasus.jmdns.impl.DNSListener>                                  _listeners;

    /**
     * Holds instances of ServiceListener's. Keys are Strings holding a fully qualified service type. Values are LinkedList's of ServiceListener's.
     */
    private final ConcurrentMap<String, List<ListenerStatus.ServiceListenerStatus>> _serviceListeners;

    /**
     * Holds instances of ServiceTypeListener's.
     */
    private final Set<ListenerStatus.ServiceTypeListenerStatus>                     _typeListeners;

    /**
     * Cache for DNSEntry's.
     */
    private final org.trianacode.pegasus.jmdns.impl.DNSCache _cache;

    /**
     * This hashtable holds the services that have been registered. Keys are instances of String which hold an all lower-case version of the fully qualified service name. Values are instances of ServiceInfo.
     */
    private final ConcurrentMap<String, org.trianacode.pegasus.jmdns.ServiceInfo>                 _services;

    /**
     * This hashtable holds the service types that have been registered or that have been received in an incoming datagram.<br/>
     * Keys are instances of String which hold an all lower-case version of the fully qualified service type.<br/>
     * Values hold the fully qualified service type.
     */
    private final ConcurrentMap<String, ServiceTypeEntry>            _serviceTypes;

    /**
     * This is used to store type entries. The type is stored as a call variable and the map support the subtypes.
     * <p>
     * The key is the lowercase version as the value is the case preserved version.
     * </p>
     */
    public static class ServiceTypeEntry extends AbstractMap<String, String> implements Cloneable {

        private final Set<Map.Entry<String, String>> _entrySet;

        private final String                         _type;

        private static class SubTypeEntry implements Entry<String, String>, java.io.Serializable, Cloneable {

            private static final long serialVersionUID = 9188503522395855322L;

            private final String      _key;
            private final String      _value;

            public SubTypeEntry(String subtype) {
                super();
                _value = (subtype != null ? subtype : "");
                _key = _value.toLowerCase();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getKey() {
                return _key;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getValue() {
                return _value;
            }

            /**
             * Replaces the value corresponding to this entry with the specified value (optional operation). This implementation simply throws <tt>UnsupportedOperationException</tt>, as this class implements an <i>immutable</i> map entry.
             *
             * @param value
             *            new value to be stored in this entry
             * @return (Does not return)
             * @throws UnsupportedOperationException
             *             always
             */
            @Override
            public String setValue(String value) {
                throw new UnsupportedOperationException();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean equals(Object entry) {
                if (!(entry instanceof Map.Entry)) {
                    return false;
                }
                return this.getKey().equals(((Map.Entry<?, ?>) entry).getKey()) && this.getValue().equals(((Map.Entry<?, ?>) entry).getValue());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int hashCode() {
                return (_key == null ? 0 : _key.hashCode()) ^ (_value == null ? 0 : _value.hashCode());
            }

            /*
             * (non-Javadoc)
             * @see java.lang.Object#clone()
             */
            @Override
            public SubTypeEntry clone() {
                // Immutable object
                return this;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return _key + "=" + _value;
            }

        }

        public ServiceTypeEntry(String type) {
            super();
            this._type = type;
            this._entrySet = new HashSet<Map.Entry<String, String>>();
        }

        /**
         * The type associated with this entry.
         *
         * @return the type
         */
        public String getType() {
            return _type;
        }

        /*
         * (non-Javadoc)
         * @see java.util.AbstractMap#entrySet()
         */
        @Override
        public Set<Map.Entry<String, String>> entrySet() {
            return _entrySet;
        }

        /**
         * Returns <code>true</code> if this set contains the specified element. More formally, returns <code>true</code> if and only if this set contains an element <code>e</code> such that
         * <code>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</code>.
         *
         * @param subtype
         *            element whose presence in this set is to be tested
         * @return <code>true</code> if this set contains the specified element
         */
        public boolean contains(String subtype) {
            return subtype != null && this.containsKey(subtype.toLowerCase());
        }

        /**
         * Adds the specified element to this set if it is not already present. More formally, adds the specified element <code>e</code> to this set if this set contains no element <code>e2</code> such that
         * <code>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</code>. If this set already contains the element, the call leaves the set unchanged and returns <code>false</code>.
         *
         * @param subtype
         *            element to be added to this set
         * @return <code>true</code> if this set did not already contain the specified element
         */
        public boolean add(String subtype) {
            if (subtype == null || this.contains(subtype)) {
                return false;
            }
            _entrySet.add(new SubTypeEntry(subtype));
            return true;
        }

        /**
         * Returns an iterator over the elements in this set. The elements are returned in no particular order (unless this set is an instance of some class that provides a guarantee).
         *
         * @return an iterator over the elements in this set
         */
        public Iterator<String> iterator() {
            return this.keySet().iterator();
        }

        /*
         * (non-Javadoc)
         * @see java.util.AbstractMap#clone()
         */
        @Override
        public ServiceTypeEntry clone() {
            ServiceTypeEntry entry = new ServiceTypeEntry(this.getType());
            for (Map.Entry<String, String> subTypeEntry : this.entrySet()) {
                entry.add(subTypeEntry.getValue());
            }
            return entry;
        }

        /*
         * (non-Javadoc)
         * @see java.util.AbstractMap#toString()
         */
        @Override
        public String toString() {
            final StringBuilder aLog = new StringBuilder(200);
            if (this.isEmpty()) {
                aLog.append("empty");
            } else {
                for (String value : this.values()) {
                    aLog.append(value);
                    aLog.append(", ");
                }
                aLog.setLength(aLog.length() - 2);
            }
            return aLog.toString();
        }

    }

    /**
     * This is the shutdown hook, we registered with the java runtime.
     */
    protected Thread                                      _shutdown;

    /**
     * Handle on the local host
     */
    private org.trianacode.pegasus.jmdns.impl.HostInfo _localHost;

    private Thread                                        _incomingListener;

    /**
     * Throttle count. This is used to count the overall number of probes sent by JmDNS. When the last throttle increment happened .
     */
    private int                                           _throttle;

    /**
     * Last throttle increment.
     */
    private long                                          _lastThrottleIncrement;

    private final ExecutorService                         _executor = Executors.newSingleThreadExecutor();

    //
    // 2009-09-16 ldeck: adding docbug patch with slight ammendments
    // 'Fixes two deadlock conditions involving JmDNS.close() - ID: 1473279'
    //
    // ---------------------------------------------------
    /**
     * The timer that triggers our announcements. We can't use the main timer object, because that could cause a deadlock where Prober waits on JmDNS.this lock held by close(), close() waits for us to finish, and we wait for Prober to give us back
     * the timer thread so we can announce. (Patch from docbug in 2006-04-19 still wasn't patched .. so I'm doing it!)
     */
    // private final Timer _cancelerTimer;
    // ---------------------------------------------------

    /**
     * The timer is used to dispatch all outgoing messages of JmDNS. It is also used to dispatch maintenance tasks for the DNS cache.
     */
    private final Timer                                   _timer;

    /**
     * The timer is used to dispatch maintenance tasks for the DNS cache.
     */
    private final Timer                                   _stateTimer;

    /**
     * The source for random values. This is used to introduce random delays in responses. This reduces the potential for collisions on the network.
     */
    private final static Random                           _random   = new Random();

    /**
     * This lock is used to coordinate processing of incoming and outgoing messages. This is needed, because the Rendezvous Conformance Test does not forgive race conditions.
     */
    private final ReentrantLock                           _ioLock   = new ReentrantLock();

    /**
     * If an incoming package which needs an answer is truncated, we store it here. We add more incoming DNSRecords to it, until the JmDNS.Responder timer picks it up.<br/>
     * FIXME [PJYF June 8 2010]: This does not work well with multiple planned answers for packages that came in from different clients.
     */
    private org.trianacode.pegasus.jmdns.impl.DNSIncoming _plannedAnswer;

    // State machine

    /**
     * This hashtable is used to maintain a list of service types being collected by this JmDNS instance. The key of the hashtable is a service type name, the value is an instance of JmDNS.ServiceCollector.
     *
     * @see #list
     */
    private final ConcurrentMap<String, ServiceCollector> _serviceCollectors;

    private final String                                  _name;

    /**
     * Main method to display API information if run from java -jar
     *
     * @param argv
     *            the command line arguments
     */
    public static void main(String[] argv) {
        String version = null;
        try {
            final Properties pomProperties = new Properties();
            pomProperties.load(JmDNSImpl.class.getResourceAsStream("/META-INF/maven/javax.jmdns/jmdns/pom.properties"));
            version = pomProperties.getProperty("version");
        } catch (Exception e) {
            version = "RUNNING.IN.IDE.FULL";
        }
        System.out.println("JmDNS version \"" + version + "\"");
        System.out.println(" ");

        System.out.println("Running on java version \"" + System.getProperty("java.version") + "\"" + " (build " + System.getProperty("java.runtime.version") + ")" + " from " + System.getProperty("java.vendor"));

        System.out.println("Operating environment \"" + System.getProperty("os.name") + "\"" + " version " + System.getProperty("os.version") + " on " + System.getProperty("os.arch"));

        System.out.println("For more information on JmDNS please visit https://sourceforge.net/projects/jmdns/");
    }

    /**
     * Create an instance of JmDNS and bind it to a specific network interface given its IP-address.
     *
     * @param address
     *            IP address to bind to.
     * @param name
     *            name of the newly created JmDNS
     * @throws IOException
     */
    public JmDNSImpl(InetAddress address, String name) throws IOException {
        super();
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("JmDNS instance created");
        }
        _cache = new org.trianacode.pegasus.jmdns.impl.DNSCache(100);

        _listeners = Collections.synchronizedList(new ArrayList<org.trianacode.pegasus.jmdns.impl.DNSListener>());
        _serviceListeners = new ConcurrentHashMap<String, List<ListenerStatus.ServiceListenerStatus>>();
        _typeListeners = Collections.synchronizedSet(new HashSet<ListenerStatus.ServiceTypeListenerStatus>());
        _serviceCollectors = new ConcurrentHashMap<String, ServiceCollector>();

        _services = new ConcurrentHashMap<String, ServiceInfo>(20);
        _serviceTypes = new ConcurrentHashMap<String, ServiceTypeEntry>(20);

        _localHost = org.trianacode.pegasus.jmdns.impl.HostInfo.newHostInfo(address, this, name);
        _name = (name != null ? name : _localHost.getName());

        _timer = new Timer("JmDNS(" + _name + ").Timer", true);
        _stateTimer = new Timer("JmDNS(" + _name + ").State.Timer", false);
        // _cancelerTimer = new Timer("JmDNS.cancelerTimer");

        // (ldeck 2.1.1) preventing shutdown blocking thread
        // -------------------------------------------------
        // _shutdown = new Thread(new Shutdown(), "JmDNS.Shutdown");
        // Runtime.getRuntime().addShutdownHook(_shutdown);

        // -------------------------------------------------

        // Bind to multicast socket
        this.openMulticastSocket(this.getLocalHost());
        this.start(this.getServices().values());

        new RecordReaper(this).start(_timer);
    }

    private void start(Collection<? extends org.trianacode.pegasus.jmdns.ServiceInfo> serviceInfos) {
        if (_incomingListener == null) {
            _incomingListener = new SocketListener(this);
            _incomingListener.start();
        }
        this.startProber();
        for (org.trianacode.pegasus.jmdns.ServiceInfo info : serviceInfos) {
            try {
                this.registerService(new ServiceInfoImpl(info));
            } catch (final Exception exception) {
                logger.log(Level.WARNING, "start() Registration exception ", exception);
            }
        }
    }

    private void openMulticastSocket(org.trianacode.pegasus.jmdns.impl.HostInfo hostInfo) throws IOException {
        if (_group == null) {
            _group = InetAddress.getByName(DNSConstants.MDNS_GROUP);
        }
        if (_socket != null) {
            this.closeMulticastSocket();
        }
        _socket = new MulticastSocket(DNSConstants.MDNS_PORT);
        if ((hostInfo != null) && (hostInfo.getInterface() != null)) {
            try {
                _socket.setNetworkInterface(hostInfo.getInterface());
            } catch (SocketException e) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("openMulticastSocket() Set network interface exception: " + e.getMessage());
                }
            }
        }
        _socket.setTimeToLive(255);
        _socket.joinGroup(_group);
    }

    private void closeMulticastSocket() {
        // jP: 20010-01-18. See below. We'll need this monitor...
        // assert (Thread.holdsLock(this));
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("closeMulticastSocket()");
        }
        if (_socket != null) {
            // close socket
            try {
                try {
                    _socket.leaveGroup(_group);
                } catch (SocketException exception) {
                    //
                }
                _socket.close();
                // jP: 20010-01-18. It isn't safe to join() on the listener
                // thread - it attempts to lock the IoLock object, and deadlock
                // ensues. Per issue #2933183, changed this to wait on the JmDNS
                // monitor, checking on each notify (or timeout) that the
                // listener thread has stopped.
                //
                while (_incomingListener != null && _incomingListener.isAlive()) {
                    synchronized (this) {
                        try {
                            if (_incomingListener != null && _incomingListener.isAlive()) {
                                // wait time is arbitrary, we're really expecting notification.
                                if (logger.isLoggable(Level.FINER)) {
                                    logger.finer("closeMulticastSocket(): waiting for jmDNS monitor");
                                }
                                this.wait(1000);
                            }
                        } catch (InterruptedException ignored) {
                            // Ignored
                        }
                    }
                }
                _incomingListener = null;
            } catch (final Exception exception) {
                logger.log(Level.WARNING, "closeMulticastSocket() Close socket exception ", exception);
            }
            _socket = null;
        }
    }

    // State machine
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean advanceState(org.trianacode.pegasus.jmdns.impl.tasks.DNSTask task) {
        return this._localHost.advanceState(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean revertState() {
        return this._localHost.revertState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cancelState() {
        return this._localHost.cancelState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean recoverState() {
        return this._localHost.recoverState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JmDNSImpl getDns() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associateWithTask(DNSTask task, DNSState state) {
        this._localHost.associateWithTask(task, state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAssociationWithTask(org.trianacode.pegasus.jmdns.impl.tasks.DNSTask task) {
        this._localHost.removeAssociationWithTask(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAssociatedWithTask(DNSTask task, DNSState state) {
        return this._localHost.isAssociatedWithTask(task, state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProbing() {
        return this._localHost.isProbing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnouncing() {
        return this._localHost.isAnnouncing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnounced() {
        return this._localHost.isAnnounced();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCanceling() {
        return this._localHost.isCanceling();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCanceled() {
        return this._localHost.isCanceled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitForAnnounced(long timeout) {
        return this._localHost.waitForAnnounced(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitForCanceled(long timeout) {
        return this._localHost.waitForCanceled(timeout);
    }

    /**
     * Return the DNSCache associated with the cache variable
     *
     * @return DNS cache
     */
    public org.trianacode.pegasus.jmdns.impl.DNSCache getCache() {
        return _cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHostName() {
        return _localHost.getName();
    }

    /**
     * Returns the local host info
     *
     * @return local host info
     */
    public org.trianacode.pegasus.jmdns.impl.HostInfo getLocalHost() {
        return _localHost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getInterface() throws IOException {
        return _socket.getInterface();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo getServiceInfo(String type, String name) {
        return this.getServiceInfo(type, name, false, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo getServiceInfo(String type, String name, long timeout) {
        return this.getServiceInfo(type, name, false, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo getServiceInfo(String type, String name, boolean persistent) {
        return this.getServiceInfo(type, name, persistent, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo getServiceInfo(String type, String name, boolean persistent, long timeout) {
        final ServiceInfoImpl info = this.resolveServiceInfo(type, name, "", persistent);
        this.waitForInfoData(info, timeout);
        return (info.hasData() ? info : null);
    }

    ServiceInfoImpl resolveServiceInfo(String type, String name, String subtype, boolean persistent) {
        this.cleanCache();
        String loType = type.toLowerCase();
        this.registerServiceType(type);
        if (_serviceCollectors.putIfAbsent(loType, new ServiceCollector(type)) == null) {
            this.addServiceListener(loType, _serviceCollectors.get(loType), ListenerStatus.SYNCHONEOUS);
        }

        // Check if the answer is in the cache.
        final ServiceInfoImpl info = this.getServiceInfoFromCache(type, name, subtype, persistent);
        // We still run the resolver to do the dispatch but if the info is already there it will quit immediately
        new ServiceInfoResolver(this, info).start(_timer);

        return info;
    }

    ServiceInfoImpl getServiceInfoFromCache(String type, String name, String subtype, boolean persistent) {
        // Check if the answer is in the cache.
        ServiceInfoImpl info = new ServiceInfoImpl(type, name, subtype, 0, 0, 0, persistent, (byte[]) null);
        org.trianacode.pegasus.jmdns.impl.DNSEntry pointerEntry = this.getCache().getDNSEntry(new org.trianacode.pegasus.jmdns.impl.DNSRecord.Pointer(type, DNSRecordClass.CLASS_ANY, false, 0, info.getQualifiedName()));
        if (pointerEntry instanceof org.trianacode.pegasus.jmdns.impl.DNSRecord) {
            ServiceInfoImpl cachedInfo = (ServiceInfoImpl) ((org.trianacode.pegasus.jmdns.impl.DNSRecord) pointerEntry).getServiceInfo(persistent);
            if (cachedInfo != null) {
                // To get a complete info record we need to retrieve the service, address and the text bytes.

                Map<Fields, String> map = cachedInfo.getQualifiedNameMap();
                byte[] srvBytes = null;
                String server = "";
                org.trianacode.pegasus.jmdns.impl.DNSEntry serviceEntry = this.getCache().getDNSEntry(info.getQualifiedName(), DNSRecordType.TYPE_SRV, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_ANY);
                if (serviceEntry instanceof org.trianacode.pegasus.jmdns.impl.DNSRecord) {
                    org.trianacode.pegasus.jmdns.ServiceInfo cachedServiceEntryInfo = ((org.trianacode.pegasus.jmdns.impl.DNSRecord) serviceEntry).getServiceInfo(persistent);
                    if (cachedServiceEntryInfo != null) {
                        cachedInfo = new ServiceInfoImpl(map, cachedServiceEntryInfo.getPort(), cachedServiceEntryInfo.getWeight(), cachedServiceEntryInfo.getPriority(), persistent, (byte[]) null);
                        srvBytes = cachedServiceEntryInfo.getTextBytes();
                        server = cachedServiceEntryInfo.getServer();
                    }
                }
                org.trianacode.pegasus.jmdns.impl.DNSEntry addressEntry = this.getCache().getDNSEntry(server, DNSRecordType.TYPE_A, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_ANY);
                if (addressEntry instanceof org.trianacode.pegasus.jmdns.impl.DNSRecord) {
                    org.trianacode.pegasus.jmdns.ServiceInfo cachedAddressInfo = ((org.trianacode.pegasus.jmdns.impl.DNSRecord) addressEntry).getServiceInfo(persistent);
                    if (cachedAddressInfo != null) {
                        cachedInfo.setAddress(cachedAddressInfo.getInet4Address());
                        cachedInfo._setText(cachedAddressInfo.getTextBytes());
                    }
                }
                addressEntry = this.getCache().getDNSEntry(server, DNSRecordType.TYPE_AAAA, DNSRecordClass.CLASS_ANY);
                if (addressEntry instanceof org.trianacode.pegasus.jmdns.impl.DNSRecord) {
                    org.trianacode.pegasus.jmdns.ServiceInfo cachedAddressInfo = ((org.trianacode.pegasus.jmdns.impl.DNSRecord) addressEntry).getServiceInfo(persistent);
                    if (cachedAddressInfo != null) {
                        cachedInfo.setAddress(cachedAddressInfo.getInet6Address());
                        cachedInfo._setText(cachedAddressInfo.getTextBytes());
                    }
                }
                org.trianacode.pegasus.jmdns.impl.DNSEntry textEntry = this.getCache().getDNSEntry(cachedInfo.getQualifiedName(), DNSRecordType.TYPE_TXT, DNSRecordClass.CLASS_ANY);
                if (textEntry instanceof org.trianacode.pegasus.jmdns.impl.DNSRecord) {
                    org.trianacode.pegasus.jmdns.ServiceInfo cachedTextInfo = ((org.trianacode.pegasus.jmdns.impl.DNSRecord) textEntry).getServiceInfo(persistent);
                    if (cachedTextInfo != null) {
                        cachedInfo._setText(cachedTextInfo.getTextBytes());
                    }
                }
                if (cachedInfo.getTextBytes().length == 0) {
                    cachedInfo._setText(srvBytes);
                }
                if (cachedInfo.hasData()) {
                    info = cachedInfo;
                }
            }
        }
        return info;
    }

    private void waitForInfoData(org.trianacode.pegasus.jmdns.ServiceInfo info, long timeout) {
        synchronized (info) {
            long loops = (timeout / 200L);
            if (loops < 1) {
                loops = 1;
            }
            for (int i = 0; i < loops; i++) {
                if (info.hasData()) {
                    break;
                }
                try {
                    info.wait(200);
                } catch (final InterruptedException e) {
                    /* Stub */
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestServiceInfo(String type, String name) {
        this.requestServiceInfo(type, name, false, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestServiceInfo(String type, String name, boolean persistent) {
        this.requestServiceInfo(type, name, persistent, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestServiceInfo(String type, String name, long timeout) {
        this.requestServiceInfo(type, name, false, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestServiceInfo(String type, String name, boolean persistent, long timeout) {
        final ServiceInfoImpl info = this.resolveServiceInfo(type, name, "", persistent);
        this.waitForInfoData(info, timeout);
    }

    void handleServiceResolved(ServiceEvent event) {
        List<ListenerStatus.ServiceListenerStatus> list = _serviceListeners.get(event.getType().toLowerCase());
        final List<ListenerStatus.ServiceListenerStatus> listCopy;
        if ((list != null) && (!list.isEmpty())) {
            if ((event.getInfo() != null) && event.getInfo().hasData()) {
                final org.trianacode.pegasus.jmdns.ServiceEvent localEvent = event;
                synchronized (list) {
                    listCopy = new ArrayList<ListenerStatus.ServiceListenerStatus>(list);
                }
                for (final ListenerStatus.ServiceListenerStatus listener : listCopy) {
                    _executor.submit(new Runnable() {
                        /** {@inheritDoc} */
                        @Override
                        public void run() {
                            listener.serviceResolved(localEvent);
                        }
                    });
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addServiceTypeListener(org.trianacode.pegasus.jmdns.ServiceTypeListener listener) throws IOException {
        ListenerStatus.ServiceTypeListenerStatus status = new ListenerStatus.ServiceTypeListenerStatus(listener, ListenerStatus.ASYNCHONEOUS);
        _typeListeners.add(status);

        // report cached service types
        for (String type : _serviceTypes.keySet()) {
            status.serviceTypeAdded(new ServiceEventImpl(this, type, "", null));
        }

        new TypeResolver(this).start(_timer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeServiceTypeListener(ServiceTypeListener listener) {
        ListenerStatus.ServiceTypeListenerStatus status = new ListenerStatus.ServiceTypeListenerStatus(listener, ListenerStatus.ASYNCHONEOUS);
        _typeListeners.remove(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addServiceListener(String type, ServiceListener listener) {
        this.addServiceListener(type, listener, ListenerStatus.ASYNCHONEOUS);
    }

    private void addServiceListener(String type, org.trianacode.pegasus.jmdns.ServiceListener listener, boolean synch) {
        ListenerStatus.ServiceListenerStatus status = new ListenerStatus.ServiceListenerStatus(listener, synch);
        final String loType = type.toLowerCase();
        List<ListenerStatus.ServiceListenerStatus> list = _serviceListeners.get(loType);
        if (list == null) {
            if (_serviceListeners.putIfAbsent(loType, new LinkedList<ListenerStatus.ServiceListenerStatus>()) == null) {
                if (_serviceCollectors.putIfAbsent(loType, new ServiceCollector(type)) == null) {
                    // We have a problem here. The service collectors must be called synchronously so that their cache get cleaned up immediately or we will report .
                    this.addServiceListener(loType, _serviceCollectors.get(loType), ListenerStatus.SYNCHONEOUS);
                }
            }
            list = _serviceListeners.get(loType);
        }
        if (list != null) {
            synchronized (list) {
                if (!list.contains(listener)) {
                    list.add(status);
                }
            }
        }
        // report cached service types
        final List<ServiceEvent> serviceEvents = new ArrayList<org.trianacode.pegasus.jmdns.ServiceEvent>();
        Collection<org.trianacode.pegasus.jmdns.impl.DNSEntry> dnsEntryLits = this.getCache().allValues();
        for (org.trianacode.pegasus.jmdns.impl.DNSEntry entry : dnsEntryLits) {
            final org.trianacode.pegasus.jmdns.impl.DNSRecord record = (org.trianacode.pegasus.jmdns.impl.DNSRecord) entry;
            if (record.getRecordType() == DNSRecordType.TYPE_SRV) {
                if (record.getKey().endsWith(loType)) {
                    // Do not used the record embedded method for generating event this will not work.
                    // serviceEvents.add(record.getServiceEvent(this));
                    serviceEvents.add(new ServiceEventImpl(this, record.getType(), toUnqualifiedName(record.getType(), record.getName()), record.getServiceInfo()));
                }
            }
        }
        // Actually call listener with all service events added above
        for (org.trianacode.pegasus.jmdns.ServiceEvent serviceEvent : serviceEvents) {
            status.serviceAdded(serviceEvent);
        }
        // Create/start ServiceResolver
        new ServiceResolver(this, type).start(_timer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeServiceListener(String type, org.trianacode.pegasus.jmdns.ServiceListener listener) {
        String loType = type.toLowerCase();
        List<ListenerStatus.ServiceListenerStatus> list = _serviceListeners.get(loType);
        if (list != null) {
            synchronized (list) {
                ListenerStatus.ServiceListenerStatus status = new ListenerStatus.ServiceListenerStatus(listener, ListenerStatus.ASYNCHONEOUS);
                list.remove(status);
                if (list.isEmpty()) {
                    _serviceListeners.remove(loType, list);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerService(org.trianacode.pegasus.jmdns.ServiceInfo infoAbstract) throws IOException {
        final ServiceInfoImpl info = (ServiceInfoImpl) infoAbstract;

        if ((info.getDns() != null) && (info.getDns() != this)) {
            throw new IllegalStateException("This service information is already registered with another DNS.");
        }
        info.setDns(this);

        this.registerServiceType(info.getTypeWithSubtype());

        // bind the service to this address
        info.setServer(_localHost.getName());
        info.setAddress(_localHost.getInet4Address());
        info.setAddress(_localHost.getInet6Address());

        this.waitForAnnounced(0);

        this.makeServiceNameUnique(info);
        while (_services.putIfAbsent(info.getKey(), info) != null) {
            this.makeServiceNameUnique(info);
        }

        this.startProber();
        info.waitForAnnounced(0);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("registerService() JmDNS registered service as " + info);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterService(org.trianacode.pegasus.jmdns.ServiceInfo infoAbstract) {
        final ServiceInfoImpl info = (ServiceInfoImpl) _services.get(infoAbstract.getKey());

        if (info != null) {
            info.cancelState();
            this.startCanceler();

            // Remind: We get a deadlock here, if the Canceler does not run!
            info.waitForCanceled(0);

            _services.remove(info.getKey(), info);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("unregisterService() JmDNS unregistered service as " + info);
            }
        } else {
            logger.warning("Removing unregistered service info: " + infoAbstract.getKey());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterAllServices() {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("unregisterAllServices()");
        }

        for (String name : _services.keySet()) {
            ServiceInfoImpl info = (ServiceInfoImpl) _services.get(name);
            if (info != null) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("Cancelling service info: " + info);
                }
                info.cancelState();
            }
        }
        this.startCanceler();

        for (String name : _services.keySet()) {
            ServiceInfoImpl info = (ServiceInfoImpl) _services.get(name);
            if (info != null) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("Wait for service info cancel: " + info);
                }
                info.waitForCanceled(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.CLOSE_TIMEOUT);
                _services.remove(name, info);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean registerServiceType(String type) {
        boolean typeAdded = false;
        Map<Fields, String> map = ServiceInfoImpl.decodeQualifiedNameMapForType(type);
        String domain = map.get(Fields.Domain);
        String protocol = map.get(Fields.Protocol);
        String application = map.get(Fields.Application);
        String subtype = map.get(Fields.Subtype);

        final String name = (application.length() > 0 ? "_" + application + "." : "") + (protocol.length() > 0 ? "_" + protocol + "." : "") + domain + ".";
        final String loname = name.toLowerCase();
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(this.getName() + ".registering service type: " + type + " as: " + name + (subtype.length() > 0 ? " subtype: " + subtype : ""));
        }
        if (!_serviceTypes.containsKey(loname) && !application.toLowerCase().equals("dns-sd") && !domain.toLowerCase().endsWith("in-addr.arpa") && !domain.toLowerCase().endsWith("ip6.arpa")) {
            typeAdded = _serviceTypes.putIfAbsent(loname, new ServiceTypeEntry(name)) == null;
            if (typeAdded) {
                final ListenerStatus.ServiceTypeListenerStatus[] list = _typeListeners.toArray(new ListenerStatus.ServiceTypeListenerStatus[_typeListeners.size()]);
                final org.trianacode.pegasus.jmdns.ServiceEvent event = new ServiceEventImpl(this, name, "", null);
                for (final ListenerStatus.ServiceTypeListenerStatus status : list) {
                    _executor.submit(new Runnable() {
                        /** {@inheritDoc} */
                        @Override
                        public void run() {
                            status.serviceTypeAdded(event);
                        }
                    });
                }
            }
        }
        if (subtype.length() > 0) {
            ServiceTypeEntry subtypes = _serviceTypes.get(loname);
            if ((subtypes != null) && (!subtypes.contains(subtype))) {
                synchronized (subtypes) {
                    if (!subtypes.contains(subtype)) {
                        typeAdded = true;
                        subtypes.add(subtype);
                        final ListenerStatus.ServiceTypeListenerStatus[] list = _typeListeners.toArray(new ListenerStatus.ServiceTypeListenerStatus[_typeListeners.size()]);
                        final org.trianacode.pegasus.jmdns.ServiceEvent event = new ServiceEventImpl(this, "_" + subtype + "._sub." + name, "", null);
                        for (final ListenerStatus.ServiceTypeListenerStatus status : list) {
                            _executor.submit(new Runnable() {
                                /** {@inheritDoc} */
                                @Override
                                public void run() {
                                    status.subTypeForServiceTypeAdded(event);
                                }
                            });
                        }
                    }
                }
            }
        }
        return typeAdded;
    }

    /**
     * Generate a possibly unique name for a service using the information we have in the cache.
     *
     * @return returns true, if the name of the service info had to be changed.
     */
    private boolean makeServiceNameUnique(ServiceInfoImpl info) {
        final String originalQualifiedName = info.getKey();
        final long now = System.currentTimeMillis();

        boolean collision;
        do {
            collision = false;

            // Check for collision in cache
            Collection<? extends org.trianacode.pegasus.jmdns.impl.DNSEntry> entryList = this.getCache().getDNSEntryList(info.getKey());
            if (entryList != null) {
                for (org.trianacode.pegasus.jmdns.impl.DNSEntry dnsEntry : entryList) {
                    if (DNSRecordType.TYPE_SRV.equals(dnsEntry.getRecordType()) && !dnsEntry.isExpired(now)) {
                        final org.trianacode.pegasus.jmdns.impl.DNSRecord.Service s = (org.trianacode.pegasus.jmdns.impl.DNSRecord.Service) dnsEntry;
                        if (s.getPort() != info.getPort() || !s.getServer().equals(_localHost.getName())) {
                            if (logger.isLoggable(Level.FINER)) {
                                logger.finer("makeServiceNameUnique() JmDNS.makeServiceNameUnique srv collision:" + dnsEntry + " s.server=" + s.getServer() + " " + _localHost.getName() + " equals:" + (s.getServer().equals(_localHost.getName())));
                            }
                            info.setName(incrementName(info.getName()));
                            collision = true;
                            break;
                        }
                    }
                }
            }

            // Check for collision with other service infos published by JmDNS
            final ServiceInfo selfService = _services.get(info.getKey());
            if (selfService != null && selfService != info) {
                info.setName(incrementName(info.getName()));
                collision = true;
            }
        }
        while (collision);

        return !(originalQualifiedName.equals(info.getKey()));
    }

    String incrementName(String name) {
        String aName = name;
        try {
            final int l = aName.lastIndexOf('(');
            final int r = aName.lastIndexOf(')');
            if ((l >= 0) && (l < r)) {
                aName = aName.substring(0, l) + "(" + (Integer.parseInt(aName.substring(l + 1, r)) + 1) + ")";
            } else {
                aName += " (2)";
            }
        } catch (final NumberFormatException e) {
            aName += " (2)";
        }
        return aName;
    }

    /**
     * Add a listener for a question. The listener will receive updates of answers to the question as they arrive, or from the cache if they are already available.
     *
     * @param listener
     *            DSN listener
     * @param question
     *            DNS query
     */
    public void addListener(DNSListener listener, org.trianacode.pegasus.jmdns.impl.DNSQuestion question) {
        final long now = System.currentTimeMillis();

        // add the new listener
        _listeners.add(listener);

        // report existing matched records

        if (question != null) {
            Collection<? extends org.trianacode.pegasus.jmdns.impl.DNSEntry> entryList = this.getCache().getDNSEntryList(question.getName().toLowerCase());
            if (entryList != null) {
                synchronized (entryList) {
                    for (org.trianacode.pegasus.jmdns.impl.DNSEntry dnsEntry : entryList) {
                        if (question.answeredBy(dnsEntry) && !dnsEntry.isExpired(now)) {
                            listener.updateRecord(this.getCache(), now, dnsEntry);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove a listener from all outstanding questions. The listener will no longer receive any updates.
     *
     * @param listener
     *            DSN listener
     */
    public void removeListener(DNSListener listener) {
        _listeners.remove(listener);
    }

    /**
     * Renew a service when the record become stale. If there is no service collector for the type this method does nothing.
     *
     * @param record
     *            DNS record
     */
    public void renewServiceCollector(org.trianacode.pegasus.jmdns.impl.DNSRecord record) {
        org.trianacode.pegasus.jmdns.ServiceInfo info = record.getServiceInfo();
        if (_serviceCollectors.containsKey(info.getType().toLowerCase())) {
            // Create/start ServiceResolver
            new ServiceResolver(this, info.getType()).start(_timer);
        }
    }

    // Remind: Method updateRecord should receive a better name.
    /**
     * Notify all listeners that a record was updated.
     *
     * @param now
     *            update date
     * @param rec
     *            DNS record
     * @param operation
     *            DNS cache operation
     */
    public void updateRecord(long now, org.trianacode.pegasus.jmdns.impl.DNSRecord rec, Operation operation) {
        // We do not want to block the entire DNS while we are updating the record for each listener (service info)
        {
            List<DNSListener> listenerList = null;
            synchronized (_listeners) {
                listenerList = new ArrayList<DNSListener>(_listeners);
            }
            for (DNSListener listener : listenerList) {
                listener.updateRecord(this.getCache(), now, rec);
            }
        }
        if (DNSRecordType.TYPE_PTR.equals(rec.getRecordType()))
        // if (DNSRecordType.TYPE_PTR.equals(rec.getRecordType()) || DNSRecordType.TYPE_SRV.equals(rec.getRecordType()))
        {
            ServiceEvent event = rec.getServiceEvent(this);
            if ((event.getInfo() == null) || !event.getInfo().hasData()) {
                // We do not care about the subtype because the info is only used if complete and the subtype will then be included.
                org.trianacode.pegasus.jmdns.ServiceInfo info = this.getServiceInfoFromCache(event.getType(), event.getName(), "", false);
                if (info.hasData()) {
                    event = new ServiceEventImpl(this, event.getType(), event.getName(), info);
                }
            }

            List<ListenerStatus.ServiceListenerStatus> list = _serviceListeners.get(event.getType().toLowerCase());
            final List<ListenerStatus.ServiceListenerStatus> serviceListenerList;
            if (list != null) {
                synchronized (list) {
                    serviceListenerList = new ArrayList<ListenerStatus.ServiceListenerStatus>(list);
                }
            } else {
                serviceListenerList = Collections.emptyList();
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.getName() + ".updating record for event: " + event + " list " + serviceListenerList + " operation: " + operation);
            }
            if (!serviceListenerList.isEmpty()) {
                final org.trianacode.pegasus.jmdns.ServiceEvent localEvent = event;

                switch (operation) {
                    case Add:
                        for (final ListenerStatus.ServiceListenerStatus listener : serviceListenerList) {
                            if (listener.isSynchronous()) {
                                listener.serviceAdded(localEvent);
                            } else {
                                _executor.submit(new Runnable() {
                                    /** {@inheritDoc} */
                                    @Override
                                    public void run() {
                                        listener.serviceAdded(localEvent);
                                    }
                                });
                            }
                        }
                        break;
                    case Remove:
                        for (final ListenerStatus.ServiceListenerStatus listener : serviceListenerList) {
                            if (listener.isSynchronous()) {
                                listener.serviceRemoved(localEvent);
                            } else {
                                _executor.submit(new Runnable() {
                                    /** {@inheritDoc} */
                                    @Override
                                    public void run() {
                                        listener.serviceRemoved(localEvent);
                                    }
                                });
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    void handleRecord(org.trianacode.pegasus.jmdns.impl.DNSRecord record, long now) {
        org.trianacode.pegasus.jmdns.impl.DNSRecord newRecord = record;

        Operation cacheOperation = Operation.Noop;
        final boolean expired = newRecord.isExpired(now);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(this.getName() + " handle response: " + newRecord);
        }

        // update the cache
        if (!newRecord.isServicesDiscoveryMetaQuery() && !newRecord.isDomainDiscoveryQuery()) {
            final boolean unique = newRecord.isUnique();
            final org.trianacode.pegasus.jmdns.impl.DNSRecord cachedRecord = (org.trianacode.pegasus.jmdns.impl.DNSRecord) this.getCache().getDNSEntry(newRecord);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(this.getName() + " handle response cached record: " + cachedRecord);
            }
            if (unique) {
                Collection<? extends org.trianacode.pegasus.jmdns.impl.DNSEntry> entries = this.getCache().getDNSEntryList(newRecord.getKey());
                if (entries != null) {
                    for (org.trianacode.pegasus.jmdns.impl.DNSEntry entry : entries) {
                        if (newRecord.getRecordType().equals(entry.getRecordType()) && newRecord.getRecordClass().equals(entry.getRecordClass()) && (entry != cachedRecord)) {
                            ((org.trianacode.pegasus.jmdns.impl.DNSRecord) entry).setWillExpireSoon(now);
                        }
                    }
                }
            }
            if (cachedRecord != null) {
                if (expired) {
                    // if the record has a 0 ttl that means we have a cancel record we need to delay the removal by 1s
                    if (newRecord.getTTL() == 0) {
                        cacheOperation = Operation.Noop;
                        cachedRecord.setWillExpireSoon(now);
                        // the actual record will be disposed of by the record reaper.
                    } else {
                        cacheOperation = Operation.Remove;
                        this.getCache().removeDNSEntry(cachedRecord);
                    }
                } else {
                    // If the record content has changed we need to inform our listeners.
                    if (!newRecord.sameValue(cachedRecord) || (!newRecord.sameSubtype(cachedRecord) && (newRecord.getSubtype().length() > 0))) {
                        cacheOperation = Operation.Update;
                        this.getCache().replaceDNSEntry(newRecord, cachedRecord);
                    } else {
                        cachedRecord.resetTTL(newRecord);
                        newRecord = cachedRecord;
                    }
                }
            } else {
                if (!expired) {
                    cacheOperation = Operation.Add;
                    this.getCache().addDNSEntry(newRecord);
                }
            }
        }

        // Register new service types
        if (newRecord.getRecordType() == DNSRecordType.TYPE_PTR) {
            // handle DNSConstants.DNS_META_QUERY records
            boolean typeAdded = false;
            if (newRecord.isServicesDiscoveryMetaQuery()) {
                // The service names are in the alias.
                if (!expired) {
                    typeAdded = this.registerServiceType(((org.trianacode.pegasus.jmdns.impl.DNSRecord.Pointer) newRecord).getAlias());
                }
                return;
            }
            typeAdded |= this.registerServiceType(newRecord.getName());
            if (typeAdded && (cacheOperation == Operation.Noop)) {
                cacheOperation = Operation.RegisterServiceType;
            }
        }

        // notify the listeners
        if (cacheOperation != Operation.Noop) {
            this.updateRecord(now, newRecord, cacheOperation);
        }

    }

    /**
     * Handle an incoming response. Cache answers, and pass them on to the appropriate questions.
     *
     * @throws IOException
     */
    void handleResponse(org.trianacode.pegasus.jmdns.impl.DNSIncoming msg) throws IOException {
        final long now = System.currentTimeMillis();

        boolean hostConflictDetected = false;
        boolean serviceConflictDetected = false;

        for (org.trianacode.pegasus.jmdns.impl.DNSRecord newRecord : msg.getAllAnswers()) {
            this.handleRecord(newRecord, now);

            if (DNSRecordType.TYPE_A.equals(newRecord.getRecordType()) || DNSRecordType.TYPE_AAAA.equals(newRecord.getRecordType())) {
                hostConflictDetected |= newRecord.handleResponse(this);
            } else {
                serviceConflictDetected |= newRecord.handleResponse(this);
            }

        }

        if (hostConflictDetected || serviceConflictDetected) {
            this.startProber();
        }
    }

    /**
     * Handle an incoming query. See if we can answer any part of it given our service infos.
     *
     * @param in
     * @param addr
     * @param port
     * @throws IOException
     */
    void handleQuery(org.trianacode.pegasus.jmdns.impl.DNSIncoming in, InetAddress addr, int port) throws IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(this.getName() + ".handle query: " + in);
        }
        // Track known answers
        boolean conflictDetected = false;
        final long expirationTime = System.currentTimeMillis() + org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.KNOWN_ANSWER_TTL;
        for (org.trianacode.pegasus.jmdns.impl.DNSRecord answer : in.getAllAnswers()) {
            conflictDetected |= answer.handleQuery(this, expirationTime);
        }

        _ioLock.lock();
        try {

            if (_plannedAnswer != null) {
                _plannedAnswer.append(in);
            } else {
                if (in.isTruncated()) {
                    _plannedAnswer = in;
                }
                new Responder(this, in, port).start(_timer);
            }

        } finally {
            _ioLock.unlock();
        }

        final long now = System.currentTimeMillis();
        for (org.trianacode.pegasus.jmdns.impl.DNSRecord answer : in.getAnswers()) {
            this.handleRecord(answer, now);
        }

        if (conflictDetected) {
            this.startProber();
        }
    }

    public void respondToQuery(org.trianacode.pegasus.jmdns.impl.DNSIncoming in) {
        _ioLock.lock();
        try {
            if (_plannedAnswer == in) {
                _plannedAnswer = null;
            }
        } finally {
            _ioLock.unlock();
        }
    }

    /**
     * Add an answer to a question. Deal with the case when the outgoing packet overflows
     *
     * @param in
     * @param addr
     * @param port
     * @param out
     * @param rec
     * @return outgoing answer
     * @throws IOException
     */
    public org.trianacode.pegasus.jmdns.impl.DNSOutgoing addAnswer(org.trianacode.pegasus.jmdns.impl.DNSIncoming in, InetAddress addr, int port, org.trianacode.pegasus.jmdns.impl.DNSOutgoing out, org.trianacode.pegasus.jmdns.impl.DNSRecord rec) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        if (newOut == null) {
            newOut = new org.trianacode.pegasus.jmdns.impl.DNSOutgoing(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_QR_RESPONSE | org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_AA, false, in.getSenderUDPPayload());
        }
        try {
            newOut.addAnswer(in, rec);
        } catch (final IOException e) {
            newOut.setFlags(newOut.getFlags() | org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_TC);
            newOut.setId(in.getId());
            send(newOut);

            newOut = new org.trianacode.pegasus.jmdns.impl.DNSOutgoing(DNSConstants.FLAGS_QR_RESPONSE | org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_AA, false, in.getSenderUDPPayload());
            newOut.addAnswer(in, rec);
        }
        return newOut;
    }

    /**
     * Send an outgoing multicast DNS message.
     *
     * @param out
     * @throws IOException
     */
    public void send(org.trianacode.pegasus.jmdns.impl.DNSOutgoing out) throws IOException {
        if (!out.isEmpty()) {
            byte[] message = out.data();
            final DatagramPacket packet = new DatagramPacket(message, message.length, _group, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.MDNS_PORT);

            if (logger.isLoggable(Level.FINEST)) {
                try {
                    final org.trianacode.pegasus.jmdns.impl.DNSIncoming msg = new org.trianacode.pegasus.jmdns.impl.DNSIncoming(packet);
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest("send(" + this.getName() + ") JmDNS out:" + msg.print(true));
                    }
                } catch (final IOException e) {
                    logger.throwing(getClass().toString(), "send(" + this.getName() + ") - JmDNS can not parse what it sends!!!", e);
                }
            }
            final MulticastSocket ms = _socket;
            if (ms != null && !ms.isClosed()) {
                ms.send(packet);
            }
        }
    }

    public void startProber() {
        new Prober(this).start(_stateTimer);
    }

    public void startAnnouncer() {
        new Announcer(this).start(_stateTimer);
    }

    public void startRenewer() {
        new Renewer(this).start(_stateTimer);
    }

    public void startCanceler() {
        new Canceler(this).start(_stateTimer);
    }

    // REMIND: Why is this not an anonymous inner class?
    /**
     * Shutdown operations.
     */
    protected class Shutdown implements Runnable {
        /** {@inheritDoc} */
        @Override
        public void run() {
            try {
                _shutdown = null;
                close();
            } catch (Throwable exception) {
                System.err.println("Error while shuting down. " + exception);
            }
        }
    }

    /**
     * Recover jmdns when there is an error.
     */
    public void recover() {
        logger.finer(this.getName() + "recover()");
        // We have an IO error so lets try to recover if anything happens lets close it.
        // This should cover the case of the IP address changing under our feet
        if (this.isCanceling() || this.isCanceled()) {
            return;
        }

        // Stop JmDNS
        // This protects against recursive calls
        if (this.cancelState()) {
            // Synchronize only if we are not already in process to prevent dead locks
            //
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.getName() + "recover() Cleanning up");
            }

            // Purge the timer
            _timer.purge();

            // We need to keep a copy for reregistration
            final Collection<org.trianacode.pegasus.jmdns.ServiceInfo> oldServiceInfos = new ArrayList<ServiceInfo>(getServices().values());

            // Cancel all services
            this.unregisterAllServices();
            this.disposeServiceCollectors();

            this.waitForCanceled(0);

            // Purge the canceler timer
            _stateTimer.purge();

            //
            // close multicast socket
            this.closeMulticastSocket();
            //
            this.getCache().clear();
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.getName() + "recover() All is clean");
            }
            //
            // All is clear now start the services
            //
            for (org.trianacode.pegasus.jmdns.ServiceInfo info : oldServiceInfos) {
                ((ServiceInfoImpl) info).recoverState();
            }
            this.recoverState();

            try {
                this.openMulticastSocket(this.getLocalHost());
                this.start(oldServiceInfos);
            } catch (final Exception exception) {
                logger.log(Level.WARNING, this.getName() + "recover() Start services exception ", exception);
            }
            logger.log(Level.WARNING, this.getName() + "recover() We are back!");
        }
    }

    public void cleanCache() {
        long now = System.currentTimeMillis();
        for (org.trianacode.pegasus.jmdns.impl.DNSEntry entry : this.getCache().allValues()) {
            try {
                org.trianacode.pegasus.jmdns.impl.DNSRecord record = (org.trianacode.pegasus.jmdns.impl.DNSRecord) entry;
                if (record.isExpired(now)) {
                    this.updateRecord(now, record, Operation.Remove);
                    this.getCache().removeDNSEntry(record);
                } else if (record.isStale(now)) {
                    // we should query for the record we care about i.e. those in the service collectors
                    this.renewServiceCollector(record);
                }
            } catch (Exception exception) {
                logger.log(Level.SEVERE, this.getName() + ".Error while reaping records: " + entry, exception);
                logger.severe(this.toString());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (this.isCanceling() || this.isCanceled()) {
            return;
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Cancelling JmDNS: " + this);
        }
        // Stop JmDNS
        // This protects against recursive calls
        if (this.cancelState()) {
            // We got the tie break now clean up

            // Stop the timer
            _timer.cancel();

            // Cancel all services
            this.unregisterAllServices();
            this.disposeServiceCollectors();

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Wait for JmDNS cancel: " + this);
            }
            this.waitForCanceled(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.CLOSE_TIMEOUT);

            // Stop the canceler timer
            _stateTimer.cancel();

            // Stop the executor
            _executor.shutdown();

            // close socket
            this.closeMulticastSocket();

            // remove the shutdown hook
            if (_shutdown != null) {
                Runtime.getRuntime().removeShutdownHook(_shutdown);
            }

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("JmDNS closed.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void printServices() {
        System.err.println(toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder aLog = new StringBuilder(2048);
        aLog.append("\t---- Local Host -----");
        aLog.append("\n\t");
        aLog.append(_localHost);
        aLog.append("\n\t---- Services -----");
        for (String key : _services.keySet()) {
            aLog.append("\n\t\tService: ");
            aLog.append(key);
            aLog.append(": ");
            aLog.append(_services.get(key));
        }
        aLog.append("\n");
        aLog.append("\t---- Types ----");
        for (String key : _serviceTypes.keySet()) {
            ServiceTypeEntry subtypes = _serviceTypes.get(key);
            aLog.append("\n\t\tType: ");
            aLog.append(subtypes.getType());
            aLog.append(": ");
            aLog.append(subtypes.isEmpty() ? "no subtypes" : subtypes);
        }
        aLog.append("\n");
        aLog.append(_cache.toString());
        aLog.append("\n");
        aLog.append("\t---- Service Collectors ----");
        for (String key : _serviceCollectors.keySet()) {
            aLog.append("\n\t\tService Collector: ");
            aLog.append(key);
            aLog.append(": ");
            aLog.append(_serviceCollectors.get(key));
        }
        aLog.append("\n");
        aLog.append("\t---- Service Listeners ----");
        for (String key : _serviceListeners.keySet()) {
            aLog.append("\n\t\tService Listener: ");
            aLog.append(key);
            aLog.append(": ");
            aLog.append(_serviceListeners.get(key));
        }
        return aLog.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo[] list(String type) {
        return this.list(type, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo[] list(String type, long timeout) {
        this.cleanCache();
        // Implementation note: The first time a list for a given type is
        // requested, a ServiceCollector is created which collects service
        // infos. This greatly speeds up the performance of subsequent calls
        // to this method. The caveats are, that 1) the first call to this
        // method for a given type is slow, and 2) we spawn a ServiceCollector
        // instance for each service type which increases network traffic a
        // little.

        String loType = type.toLowerCase();

        boolean newCollectorCreated = false;
        if (this.isCanceling() || this.isCanceled()) {
            return new org.trianacode.pegasus.jmdns.ServiceInfo[0];
        }

        ServiceCollector collector = _serviceCollectors.get(loType);
        if (collector == null) {
            newCollectorCreated = _serviceCollectors.putIfAbsent(loType, new ServiceCollector(type)) == null;
            collector = _serviceCollectors.get(loType);
            if (newCollectorCreated) {
                this.addServiceListener(type, collector, ListenerStatus.SYNCHONEOUS);
            }
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.getName() + ".collector: " + collector);
        }
        // At this stage the collector should never be null but it keeps findbugs happy.
        return (collector != null ? collector.list(timeout) : new org.trianacode.pegasus.jmdns.ServiceInfo[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ServiceInfo[]> listBySubtype(String type) {
        return this.listBySubtype(type, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, org.trianacode.pegasus.jmdns.ServiceInfo[]> listBySubtype(String type, long timeout) {
        Map<String, List<org.trianacode.pegasus.jmdns.ServiceInfo>> map = new HashMap<String, List<org.trianacode.pegasus.jmdns.ServiceInfo>>(5);
        for (org.trianacode.pegasus.jmdns.ServiceInfo info : this.list(type, timeout)) {
            String subtype = info.getSubtype().toLowerCase();
            if (!map.containsKey(subtype)) {
                map.put(subtype, new ArrayList<org.trianacode.pegasus.jmdns.ServiceInfo>(10));
            }
            map.get(subtype).add(info);
        }

        Map<String, org.trianacode.pegasus.jmdns.ServiceInfo[]> result = new HashMap<String, org.trianacode.pegasus.jmdns.ServiceInfo[]>(map.size());
        for (String subtype : map.keySet()) {
            List<org.trianacode.pegasus.jmdns.ServiceInfo> infoForSubType = map.get(subtype);
            result.put(subtype, infoForSubType.toArray(new org.trianacode.pegasus.jmdns.ServiceInfo[infoForSubType.size()]));
        }

        return result;
    }

    /**
     * This method disposes all ServiceCollector instances which have been created by calls to method <code>list(type)</code>.
     *
     * @see #list
     */
    private void disposeServiceCollectors() {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("disposeServiceCollectors()");
        }
        for (String type : _serviceCollectors.keySet()) {
            ServiceCollector collector = _serviceCollectors.get(type);
            if (collector != null) {
                this.removeServiceListener(type, collector);
                _serviceCollectors.remove(type, collector);
            }
        }
    }

    /**
     * Instances of ServiceCollector are used internally to speed up the performance of method <code>list(type)</code>.
     *
     * @see #list
     */
    private static class ServiceCollector implements ServiceListener {
        // private static Logger logger = Logger.getLogger(ServiceCollector.class.getName());

        /**
         * A set of collected service instance names.
         */
        private final ConcurrentMap<String, org.trianacode.pegasus.jmdns.ServiceInfo>  _infos;

        /**
         * A set of collected service event waiting to be resolved.
         */
        private final ConcurrentMap<String, ServiceEvent> _events;

        /**
         * This is the type we are listening for (only used for debugging).
         */
        private final String                              _type;

        /**
         * This is used to force a wait on the first invocation of list.
         */
        private volatile boolean                          _needToWaitForInfos;

        public ServiceCollector(String type) {
            super();
            _infos = new ConcurrentHashMap<String, org.trianacode.pegasus.jmdns.ServiceInfo>();
            _events = new ConcurrentHashMap<String, org.trianacode.pegasus.jmdns.ServiceEvent>();
            _type = type;
            _needToWaitForInfos = true;
        }

        /**
         * A service has been added.
         *
         * @param event
         *            service event
         */
        @Override
        public void serviceAdded(org.trianacode.pegasus.jmdns.ServiceEvent event) {
            synchronized (this) {
                org.trianacode.pegasus.jmdns.ServiceInfo info = event.getInfo();
                if ((info != null) && (info.hasData())) {
                    _infos.put(event.getName(), info);
                } else {
                    String subtype = (info != null ? info.getSubtype() : "");
                    info = ((JmDNSImpl) event.getDNS()).resolveServiceInfo(event.getType(), event.getName(), subtype, true);
                    if (info != null) {
                        _infos.put(event.getName(), info);
                    } else {
                        _events.put(event.getName(), event);
                    }
                }
            }
        }

        /**
         * A service has been removed.
         *
         * @param event
         *            service event
         */
        @Override
        public void serviceRemoved(org.trianacode.pegasus.jmdns.ServiceEvent event) {
            synchronized (this) {
                _infos.remove(event.getName());
                _events.remove(event.getName());
            }
        }

        /**
         * A service has been resolved. Its details are now available in the ServiceInfo record.
         *
         * @param event
         *            service event
         */
        @Override
        public void serviceResolved(org.trianacode.pegasus.jmdns.ServiceEvent event) {
            synchronized (this) {
                _infos.put(event.getName(), event.getInfo());
                _events.remove(event.getName());
            }
        }

        /**
         * Returns an array of all service infos which have been collected by this ServiceCollector.
         *
         * @param timeout
         *            timeout if the info list is empty.
         * @return Service Info array
         */
        public org.trianacode.pegasus.jmdns.ServiceInfo[] list(long timeout) {
            if (_infos.isEmpty() || !_events.isEmpty() || _needToWaitForInfos) {
                long loops = (timeout / 200L);
                if (loops < 1) {
                    loops = 1;
                }
                for (int i = 0; i < loops; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (final InterruptedException e) {
                        /* Stub */
                    }
                    if (_events.isEmpty() && !_infos.isEmpty() && !_needToWaitForInfos) {
                        break;
                    }
                }
            }
            _needToWaitForInfos = false;
            return _infos.values().toArray(new org.trianacode.pegasus.jmdns.ServiceInfo[_infos.size()]);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuffer aLog = new StringBuffer();
            aLog.append("\n\tType: ");
            aLog.append(_type);
            if (_infos.isEmpty()) {
                aLog.append("\n\tNo services collected.");
            } else {
                aLog.append("\n\tServices");
                for (String key : _infos.keySet()) {
                    aLog.append("\n\t\tService: ");
                    aLog.append(key);
                    aLog.append(": ");
                    aLog.append(_infos.get(key));
                }
            }
            if (_events.isEmpty()) {
                aLog.append("\n\tNo event queued.");
            } else {
                aLog.append("\n\tEvents");
                for (String key : _events.keySet()) {
                    aLog.append("\n\t\tEvent: ");
                    aLog.append(key);
                    aLog.append(": ");
                    aLog.append(_events.get(key));
                }
            }
            return aLog.toString();
        }
    }

    static String toUnqualifiedName(String type, String qualifiedName) {
        String loType = type.toLowerCase();
        String loQualifiedName = qualifiedName.toLowerCase();
        if (loQualifiedName.endsWith(loType) && !(loQualifiedName.equals(loType))) {
            return qualifiedName.substring(0, qualifiedName.length() - type.length() - 1);
        }
        return qualifiedName;
    }

    public Map<String, org.trianacode.pegasus.jmdns.ServiceInfo> getServices() {
        return _services;
    }

    public void setLastThrottleIncrement(long lastThrottleIncrement) {
        this._lastThrottleIncrement = lastThrottleIncrement;
    }

    public long getLastThrottleIncrement() {
        return _lastThrottleIncrement;
    }

    public void setThrottle(int throttle) {
        this._throttle = throttle;
    }

    public int getThrottle() {
        return _throttle;
    }

    public static Random getRandom() {
        return _random;
    }

    public void ioLock() {
        _ioLock.lock();
    }

    public void ioUnlock() {
        _ioLock.unlock();
    }

    public void setPlannedAnswer(org.trianacode.pegasus.jmdns.impl.DNSIncoming plannedAnswer) {
        this._plannedAnswer = plannedAnswer;
    }

    public org.trianacode.pegasus.jmdns.impl.DNSIncoming getPlannedAnswer() {
        return _plannedAnswer;
    }

    void setLocalHost(org.trianacode.pegasus.jmdns.impl.HostInfo localHost) {
        this._localHost = localHost;
    }

    public Map<String, ServiceTypeEntry> getServiceTypes() {
        return _serviceTypes;
    }

    public void setClosed(boolean closed) {
        this._closed = closed;
    }

    public boolean isClosed() {
        return _closed;
    }

    public MulticastSocket getSocket() {
        return _socket;
    }

    public InetAddress getGroup() {
        return _group;
    }

}
