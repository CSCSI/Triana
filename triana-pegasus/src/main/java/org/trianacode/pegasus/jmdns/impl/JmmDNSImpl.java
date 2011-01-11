/**
 *
 */
package org.trianacode.pegasus.jmdns.impl;

import org.trianacode.pegasus.jmdns.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.JmDNS;
import org.trianacode.pegasus.jmdns.JmmDNS;
import org.trianacode.pegasus.jmdns.NetworkTopologyDiscovery;
import org.trianacode.pegasus.jmdns.NetworkTopologyEvent;
import org.trianacode.pegasus.jmdns.NetworkTopologyListener;
import org.trianacode.pegasus.jmdns.ServiceInfo;

import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;

/**
 * This class enable multihomming mDNS. It will open a mDNS per IP address of the machine.
 *
 * @author C&eacute;drik Lime, Pierre Frisch
 */
public class JmmDNSImpl implements JmmDNS, NetworkTopologyListener, ServiceInfoImpl.Delegate {
    private static Logger                            logger = Logger.getLogger(JmmDNSImpl.class.getName());

    private final Set<NetworkTopologyListener>       _networkListeners;

    /**
     * Every JmDNS created.
     */
    private final ConcurrentMap<InetAddress, org.trianacode.pegasus.jmdns.JmDNS>  _knownMDNS;

    /**
     * This enable the service info text update.
     */
    private final ConcurrentMap<String, org.trianacode.pegasus.jmdns.ServiceInfo> _services;

    private final ExecutorService                    _ListenerExecutor;

    private final ExecutorService                    _jmDNSExecutor;

    private final Timer                              _timer;

    /**
     *
     */
    public JmmDNSImpl() {
        super();
        _networkListeners = Collections.synchronizedSet(new HashSet<NetworkTopologyListener>());
        _knownMDNS = new ConcurrentHashMap<InetAddress, org.trianacode.pegasus.jmdns.JmDNS>();
        _services = new ConcurrentHashMap<String, org.trianacode.pegasus.jmdns.ServiceInfo>(20);
        _ListenerExecutor = Executors.newSingleThreadExecutor();
        _jmDNSExecutor = Executors.newCachedThreadPool();
        _timer = new Timer("Multihommed mDNS.Timer", true);
        (new NetworkChecker(this, NetworkTopologyDiscovery.Factory.getInstance())).start(_timer);
    }

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Cancelling JmmDNS: " + this);
        }
        _timer.cancel();
        _ListenerExecutor.shutdown();
        // We need to cancel all the DNS
        ExecutorService executor = Executors.newCachedThreadPool();
        for (final org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            executor.submit(new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    try {
                        mDNS.close();
                    } catch (IOException exception) {
                        // JmDNS never throws this is only because of the closeable interface
                    }
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.CLOSE_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.WARNING, "Exception ", exception);
        }
        _knownMDNS.clear();
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#getNames()
     */
    @Override
    public String[] getNames() {
        Set<String> result = new HashSet<String>();
        for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            result.add(mDNS.getName());
        }
        return result.toArray(new String[result.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#getHostNames()
     */
    @Override
    public String[] getHostNames() {
        Set<String> result = new HashSet<String>();
        for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            result.add(mDNS.getHostName());
        }
        return result.toArray(new String[result.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#getInterfaces()
     */
    @Override
    public InetAddress[] getInterfaces() throws IOException {
        Set<InetAddress> result = new HashSet<InetAddress>();
        for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            result.add(mDNS.getInterface());
        }
        return result.toArray(new InetAddress[result.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#getServiceInfos(java.lang.String, java.lang.String)
     */
    @Override
    public ServiceInfo[] getServiceInfos(String type, String name) {
        return this.getServiceInfos(type, name, false, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#getServiceInfos(java.lang.String, java.lang.String, long)
     */
    @Override
    public ServiceInfo[] getServiceInfos(String type, String name, long timeout) {
        return this.getServiceInfos(type, name, false, timeout);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#getServiceInfos(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public ServiceInfo[] getServiceInfos(String type, String name, boolean persistent) {
        return this.getServiceInfos(type, name, persistent, DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#getServiceInfos(java.lang.String, java.lang.String, boolean, long)
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo[] getServiceInfos(final String type, final String name, final boolean persistent, final long timeout) {
        // We need to run this in parallel to respect the timeout.
        final Set<org.trianacode.pegasus.jmdns.ServiceInfo> result = Collections.synchronizedSet(new HashSet<ServiceInfo>(_knownMDNS.size()));
        ExecutorService executor = Executors.newCachedThreadPool();
        for (final org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            executor.submit(new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    result.add(mDNS.getServiceInfo(type, name, persistent, timeout));
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.WARNING, "Exception ", exception);
        }
        return result.toArray(new org.trianacode.pegasus.jmdns.ServiceInfo[result.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#requestServiceInfo(java.lang.String, java.lang.String)
     */
    @Override
    public void requestServiceInfo(String type, String name) {
        this.requestServiceInfo(type, name, false, DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#requestServiceInfo(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void requestServiceInfo(String type, String name, boolean persistent) {
        this.requestServiceInfo(type, name, persistent, DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#requestServiceInfo(java.lang.String, java.lang.String, long)
     */
    @Override
    public void requestServiceInfo(String type, String name, long timeout) {
        this.requestServiceInfo(type, name, false, timeout);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#requestServiceInfo(java.lang.String, java.lang.String, boolean, long)
     */
    @Override
    public void requestServiceInfo(final String type, final String name, final boolean persistent, final long timeout) {
        // We need to run this in parallel to respect the timeout.
        for (final org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            _jmDNSExecutor.submit(new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    mDNS.requestServiceInfo(type, name, persistent, timeout);
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#addServiceTypeListener(org.trianacode.pegasus.jmdns.ServiceTypeListener)
     */
    @Override
    public void addServiceTypeListener(ServiceTypeListener listener) throws IOException {
        for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            mDNS.addServiceTypeListener(listener);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#removeServiceTypeListener(org.trianacode.pegasus.jmdns.ServiceTypeListener)
     */
    @Override
    public void removeServiceTypeListener(org.trianacode.pegasus.jmdns.ServiceTypeListener listener) {
        for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            mDNS.removeServiceTypeListener(listener);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#addServiceListener(java.lang.String, org.trianacode.pegasus.jmdns.ServiceListener)
     */
    @Override
    public void addServiceListener(String type, org.trianacode.pegasus.jmdns.ServiceListener listener) {
        for (JmDNS mDNS : _knownMDNS.values()) {
            mDNS.addServiceListener(type, listener);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#removeServiceListener(java.lang.String, org.trianacode.pegasus.jmdns.ServiceListener)
     */
    @Override
    public void removeServiceListener(String type, org.trianacode.pegasus.jmdns.ServiceListener listener) {
        for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            mDNS.removeServiceListener(type, listener);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.ServiceInfoImpl.Delegate#textValueUpdated(org.trianacode.pegasus.jmdns.ServiceInfo, byte[])
     */
    @Override
    public void textValueUpdated(org.trianacode.pegasus.jmdns.ServiceInfo target, byte[] value) {
        synchronized (_services) {
            for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
                org.trianacode.pegasus.jmdns.ServiceInfo info = ((JmDNSImpl) mDNS).getServices().get(target.getQualifiedName());
                if (info != null) {
                    info.setText(value);
                } else {
                    logger.warning("We have a mDNS that does not know about the service info being updated.");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#registerService(org.trianacode.pegasus.jmdns.ServiceInfo)
     */
    @Override
    public void registerService(org.trianacode.pegasus.jmdns.ServiceInfo info) throws IOException {
        // This is really complex. We need to clone the service info for each DNS but then we loose the ability to update it.
        synchronized (_services) {
            for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
                mDNS.registerService(info.clone());
            }
            ((ServiceInfoImpl) info).setDelegate(this);
            _services.put(info.getQualifiedName(), info);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#unregisterService(org.trianacode.pegasus.jmdns.ServiceInfo)
     */
    @Override
    public void unregisterService(ServiceInfo info) {
        synchronized (_services) {
            for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
                mDNS.unregisterService(info);
            }
            ((ServiceInfoImpl) info).setDelegate(null);
            _services.remove(info.getQualifiedName());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#unregisterAllServices()
     */
    @Override
    public void unregisterAllServices() {
        synchronized (_services) {
            for (org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
                mDNS.unregisterAllServices();
            }
            _services.clear();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#registerServiceType(java.lang.String)
     */
    @Override
    public void registerServiceType(String type) {
        for (JmDNS mDNS : _knownMDNS.values()) {
            mDNS.registerServiceType(type);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#list(java.lang.String)
     */
    @Override
    public ServiceInfo[] list(String type) {
        return this.list(type, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#list(java.lang.String, long)
     */
    @Override
    public org.trianacode.pegasus.jmdns.ServiceInfo[] list(final String type, final long timeout) {
        // We need to run this in parallel to respect the timeout.
        final Set<org.trianacode.pegasus.jmdns.ServiceInfo> result = Collections.synchronizedSet(new HashSet<org.trianacode.pegasus.jmdns.ServiceInfo>(_knownMDNS.size() * 5));
        ExecutorService executor = Executors.newCachedThreadPool();
        for (final org.trianacode.pegasus.jmdns.JmDNS mDNS : _knownMDNS.values()) {
            executor.submit(new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    result.addAll(Arrays.asList(mDNS.list(type, timeout)));
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.WARNING, "Exception ", exception);
        }
        return result.toArray(new org.trianacode.pegasus.jmdns.ServiceInfo[result.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#listBySubtype(java.lang.String)
     */
    @Override
    public Map<String, org.trianacode.pegasus.jmdns.ServiceInfo[]> listBySubtype(String type) {
        return this.listBySubtype(type, DNSConstants.SERVICE_INFO_TIMEOUT);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#listBySubtype(java.lang.String, long)
     */
    @Override
    public Map<String, org.trianacode.pegasus.jmdns.ServiceInfo[]> listBySubtype(final String type, final long timeout) {
        Map<String, List<org.trianacode.pegasus.jmdns.ServiceInfo>> map = new HashMap<String, List<org.trianacode.pegasus.jmdns.ServiceInfo>>(5);
        for (org.trianacode.pegasus.jmdns.ServiceInfo info : this.list(type, timeout)) {
            String subtype = info.getSubtype();
            if (!map.containsKey(subtype)) {
                map.put(subtype, new ArrayList<org.trianacode.pegasus.jmdns.ServiceInfo>(10));
            }
            map.get(subtype).add(info);
        }

        Map<String, org.trianacode.pegasus.jmdns.ServiceInfo[]> result = new HashMap<String, ServiceInfo[]>(map.size());
        for (String subtype : map.keySet()) {
            List<org.trianacode.pegasus.jmdns.ServiceInfo> infoForSubType = map.get(subtype);
            result.put(subtype, infoForSubType.toArray(new org.trianacode.pegasus.jmdns.ServiceInfo[infoForSubType.size()]));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#addNetworkTopologyListener(org.trianacode.pegasus.jmdns.NetworkTopologyListener)
     */
    @Override
    public void addNetworkTopologyListener(NetworkTopologyListener listener) {
        _networkListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#removeNetworkTopologyListener(org.trianacode.pegasus.jmdns.NetworkTopologyListener)
     */
    @Override
    public void removeNetworkTopologyListener(NetworkTopologyListener listener) {
        _networkListeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.JmmDNS#networkListeners()
     */
    @Override
    public NetworkTopologyListener[] networkListeners() {
        return _networkListeners.toArray(new NetworkTopologyListener[_networkListeners.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.NetworkTopologyListener#inetAddressAdded(org.trianacode.pegasus.jmdns.NetworkTopologyEvent)
     */
    @Override
    public void inetAddressAdded(NetworkTopologyEvent event) {
        InetAddress address = event.getInetAddress();
        try {
            synchronized (this) {
                if (!_knownMDNS.containsKey(address)) {
                    org.trianacode.pegasus.jmdns.JmDNS mDNS = org.trianacode.pegasus.jmdns.JmDNS.create(address);
                    _knownMDNS.put(address, mDNS);
                    final NetworkTopologyEvent jmdnsEvent = new NetworkTopologyEventImpl(mDNS, address);
                    for (final NetworkTopologyListener listener : this.networkListeners()) {
                        _ListenerExecutor.submit(new Runnable() {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void run() {
                                listener.inetAddressAdded(jmdnsEvent);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Unexpected unhandled exception: " + e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.NetworkTopologyListener#inetAddressRemoved(org.trianacode.pegasus.jmdns.NetworkTopologyEvent)
     */
    @Override
    public void inetAddressRemoved(NetworkTopologyEvent event) {
        InetAddress address = event.getInetAddress();
        try {
            synchronized (this) {
                if (_knownMDNS.containsKey(address)) {
                    org.trianacode.pegasus.jmdns.JmDNS mDNS = org.trianacode.pegasus.jmdns.JmDNS.create(address);
                    _knownMDNS.remove(address);
                    mDNS.close();
                    final NetworkTopologyEvent jmdnsEvent = new NetworkTopologyEventImpl(mDNS, address);
                    for (final NetworkTopologyListener listener : this.networkListeners()) {
                        _ListenerExecutor.submit(new Runnable() {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void run() {
                                listener.inetAddressRemoved(jmdnsEvent);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Unexpected unhandled exception: " + e);
        }
    }

    /**
     * Checks the network state.<br/>
     * If the network change, this class will reconfigure the list of DNS do adapt to the new configuration.
     */
    static class NetworkChecker extends TimerTask {
        private static Logger                  logger1 = Logger.getLogger(NetworkChecker.class.getName());

        private final NetworkTopologyListener  _mmDNS;

        private final NetworkTopologyDiscovery _topology;

        private Set<InetAddress>               _knownAddresses;

        public NetworkChecker(NetworkTopologyListener mmDNS, NetworkTopologyDiscovery topology) {
            super();
            this._mmDNS = mmDNS;
            this._topology = topology;
            _knownAddresses = Collections.synchronizedSet(new HashSet<InetAddress>());
        }

        public void start(Timer timer) {
            timer.schedule(this, 0, DNSConstants.NETWORK_CHECK_INTERVAL);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                InetAddress[] curentAddresses = _topology.getInetAddresses();
                Set<InetAddress> current = new HashSet<InetAddress>(curentAddresses.length);
                for (InetAddress address : curentAddresses) {
                    current.add(address);
                    if (!_knownAddresses.contains(address)) {
                        final NetworkTopologyEvent event = new NetworkTopologyEventImpl(_mmDNS, address);
                        _mmDNS.inetAddressAdded(event);
                    }
                }
                for (InetAddress address : _knownAddresses) {
                    if (!current.contains(address)) {
                        final NetworkTopologyEvent event = new NetworkTopologyEventImpl(_mmDNS, address);
                        _mmDNS.inetAddressRemoved(event);
                    }
                }
                _knownAddresses = current;
            } catch (Exception e) {
                logger1.warning("Unexpected unhandled exception: " + e);
            }
        }

    }

}
