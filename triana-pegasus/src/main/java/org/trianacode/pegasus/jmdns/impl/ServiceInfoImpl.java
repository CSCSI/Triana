// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl;

import org.trianacode.pegasus.jmdns.ServiceInfo;
import org.trianacode.pegasus.jmdns.impl.DNSRecord.Pointer;
import org.trianacode.pegasus.jmdns.impl.DNSRecord.Service;
import org.trianacode.pegasus.jmdns.impl.DNSRecord.Text;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType;
import org.trianacode.pegasus.jmdns.impl.constants.DNSState;
import org.trianacode.pegasus.jmdns.impl.tasks.DNSTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JmDNS service information.
 *
 * @author Arthur van Hoff, Jeff Sonstein, Werner Randelshofer
 */
public class ServiceInfoImpl extends ServiceInfo implements DNSListener, DNSStatefulObject {
    private static Logger          logger = Logger.getLogger(ServiceInfoImpl.class.getName());

    private String                 _domain;
    private String                 _protocol;
    private String                 _application;
    private String                 _name;
    private String                 _subtype;
    private String                 _server;
    private int                    _port;
    private int                    _weight;
    private int                    _priority;
    private byte                   _text[];
    private Map<String, byte[]>    _props;
    private Inet4Address           _ipv4Addr;
    private Inet6Address           _ipv6Addr;
    private final Set<InetAddress> _addresses;

    private transient String       _key;

    private boolean                _persistent;
    private boolean                _needTextAnnouncing;

    private final ServiceInfoState _state;

    private Delegate               _delegate;

    public static interface Delegate {

        public void textValueUpdated(ServiceInfo target, byte[] value);

    }

    private final static class ServiceInfoState extends DNSStatefulObject.DefaultImplementation {

        private static final long     serialVersionUID = 1104131034952196820L;

        private final ServiceInfoImpl _info;

        /**
         * @param info
         */
        public ServiceInfoState(ServiceInfoImpl info) {
            super();
            _info = info;
        }

        @Override
        protected void setTask(org.trianacode.pegasus.jmdns.impl.tasks.DNSTask task) {
            super.setTask(task);
            if ((this._task == null) && _info.needTextAnnouncing()) {
                this.lock();
                try {
                    if ((this._task == null) && _info.needTextAnnouncing()) {
                        if (this._state.isAnnounced()) {
                            this._state = DNSState.ANNOUNCING_1;
                            if (this.getDns() != null) {
                                this.getDns().startAnnouncer();
                            }
                        }
                        _info.setNeedTextAnnouncing(false);
                    }
                } finally {
                    this.unlock();
                }
            }
        }

        @Override
        public void setDns(org.trianacode.pegasus.jmdns.impl.JmDNSImpl dns) {
            super.setDns(dns);
        }

    }

    /**
     * @param type
     * @param name
     * @param subtype
     * @param port
     * @param weight
     * @param priority
     * @param persistent
     * @param text
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#create(String, String, int, int, int, String)
     */
    public ServiceInfoImpl(String type, String name, String subtype, int port, int weight, int priority, boolean persistent, String text) {
        this(ServiceInfoImpl.decodeQualifiedNameMap(type, name, subtype), port, weight, priority, persistent, (byte[]) null);
        _server = text;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(text.length());
            writeUTF(out, text);
            this._text = out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("unexpected exception: " + e);
        }
    }

    /**
     * @param type
     * @param name
     * @param subtype
     * @param port
     * @param weight
     * @param priority
     * @param persistent
     * @param props
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#create(String, String, int, int, int, Map)
     */
    public ServiceInfoImpl(String type, String name, String subtype, int port, int weight, int priority, boolean persistent, Map<String, ?> props) {
        this(ServiceInfoImpl.decodeQualifiedNameMap(type, name, subtype), port, weight, priority, persistent, textFromProperties(props));
    }

    /**
     * @param type
     * @param name
     * @param subtype
     * @param port
     * @param weight
     * @param priority
     * @param persistent
     * @param text
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#create(String, String, int, int, int, byte[])
     */
    public ServiceInfoImpl(String type, String name, String subtype, int port, int weight, int priority, boolean persistent, byte text[]) {
        this(ServiceInfoImpl.decodeQualifiedNameMap(type, name, subtype), port, weight, priority, persistent, text);
    }

    public ServiceInfoImpl(Map<Fields, String> qualifiedNameMap, int port, int weight, int priority, boolean persistent, Map<String, ?> props) {
        this(qualifiedNameMap, port, weight, priority, persistent, textFromProperties(props));
    }

    ServiceInfoImpl(Map<Fields, String> qualifiedNameMap, int port, int weight, int priority, boolean persistent, String text) {
        this(qualifiedNameMap, port, weight, priority, persistent, (byte[]) null);
        _server = text;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(text.length());
            writeUTF(out, text);
            this._text = out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("unexpected exception: " + e);
        }
    }

    ServiceInfoImpl(Map<Fields, String> qualifiedNameMap, int port, int weight, int priority, boolean persistent, byte text[]) {
        Map<Fields, String> map = ServiceInfoImpl.checkQualifiedNameMap(qualifiedNameMap);

        this._domain = map.get(Fields.Domain);
        this._protocol = map.get(Fields.Protocol);
        this._application = map.get(Fields.Application);
        this._name = map.get(Fields.Instance);
        this._subtype = map.get(Fields.Subtype);

        this._port = port;
        this._weight = weight;
        this._priority = priority;
        this._text = text;
        this.setNeedTextAnnouncing(false);
        this._state = new ServiceInfoState(this);
        this._persistent = persistent;
        this._addresses = Collections.synchronizedSet(new LinkedHashSet<InetAddress>());
    }

    /**
     * During recovery we need to duplicate service info to reregister them
     *
     * @param info
     */
    ServiceInfoImpl(org.trianacode.pegasus.jmdns.ServiceInfo info) {
        this._addresses = Collections.synchronizedSet(new LinkedHashSet<InetAddress>());
        if (info != null) {
            this._domain = info.getDomain();
            this._protocol = info.getProtocol();
            this._application = info.getApplication();
            this._name = info.getName();
            this._subtype = info.getSubtype();
            this._port = info.getPort();
            this._weight = info.getWeight();
            this._priority = info.getPriority();
            this._text = info.getTextBytes();
            this._persistent = info.isPersistent();
            this._ipv4Addr = info.getInet4Address();
            this._ipv6Addr = info.getInet6Address();
            InetAddress[] addresses = info.getInetAddresses();
            for (InetAddress address : addresses) {
                this._addresses.add(address);
            }
        }
        this._state = new ServiceInfoState(this);
    }

    public static Map<Fields, String> decodeQualifiedNameMap(String type, String name, String subtype) {
        Map<Fields, String> qualifiedNameMap = decodeQualifiedNameMapForType(type);

        qualifiedNameMap.put(Fields.Instance, name);
        qualifiedNameMap.put(Fields.Subtype, subtype);

        return checkQualifiedNameMap(qualifiedNameMap);
    }

    public static Map<Fields, String> decodeQualifiedNameMapForType(String type) {
        int index;

        String casePreservedType = type;

        String aType = type.toLowerCase();
        String application = aType;
        String protocol = "";
        String subtype = "";
        String name = "";
        String domain = "";

        if (aType.contains("in-addr.arpa") || aType.contains("ip6.arpa")) {
            index = (aType.contains("in-addr.arpa") ? aType.indexOf("in-addr.arpa") : aType.indexOf("ip6.arpa"));
            name = removeSeparators(casePreservedType.substring(0, index));
            domain = casePreservedType.substring(index);
            application = "";
        } else if ((!aType.contains("_")) && aType.contains(".")) {
            index = aType.indexOf('.');
            name = removeSeparators(casePreservedType.substring(0, index));
            domain = removeSeparators(casePreservedType.substring(index));
            application = "";
        } else {
            // First remove the name if it there.
            if (!aType.startsWith("_") || aType.startsWith("_services")) {
                index = aType.indexOf('.');
                if (index > 0) {
                    // We need to preserve the case for the user readable name.
                    name = casePreservedType.substring(0, index);
                    if (index + 1 < aType.length()) {
                        aType = aType.substring(index + 1);
                        casePreservedType = casePreservedType.substring(index + 1);
                    }
                }
            }

            index = aType.lastIndexOf("._");
            if (index > 0) {
                int start = index + 2;
                int end = aType.indexOf('.', start);
                protocol = casePreservedType.substring(start, end);
            }
            if (protocol.length() > 0) {
                index = aType.indexOf("_" + protocol.toLowerCase() + ".");
                int start = index + protocol.length() + 2;
                int end = aType.length() - (aType.endsWith(".") ? 1 : 0);
                domain = casePreservedType.substring(start, end);
                application = casePreservedType.substring(0, index - 1);
            }
            index = application.toLowerCase().indexOf("._sub");
            if (index > 0) {
                int start = index + 5;
                subtype = removeSeparators(application.substring(0, index));
                application = application.substring(start);
            }
        }

        final Map<Fields, String> qualifiedNameMap = new HashMap<Fields, String>(5);
        qualifiedNameMap.put(Fields.Domain, removeSeparators(domain));
        qualifiedNameMap.put(Fields.Protocol, protocol);
        qualifiedNameMap.put(Fields.Application, removeSeparators(application));
        qualifiedNameMap.put(Fields.Instance, name);
        qualifiedNameMap.put(Fields.Subtype, subtype);

        return qualifiedNameMap;
    }

    protected static Map<Fields, String> checkQualifiedNameMap(Map<Fields, String> qualifiedNameMap) {
        Map<Fields, String> checkedQualifiedNameMap = new HashMap<Fields, String>(5);

        // Optional domain
        String domain = (qualifiedNameMap.containsKey(Fields.Domain) ? qualifiedNameMap.get(Fields.Domain) : "local");
        if ((domain == null) || (domain.length() == 0)) {
            domain = "local";
        }
        domain = removeSeparators(domain);
        checkedQualifiedNameMap.put(Fields.Domain, domain);
        // Optional protocol
        String protocol = (qualifiedNameMap.containsKey(Fields.Protocol) ? qualifiedNameMap.get(Fields.Protocol) : "tcp");
        if ((protocol == null) || (protocol.length() == 0)) {
            protocol = "tcp";
        }
        protocol = removeSeparators(protocol);
        checkedQualifiedNameMap.put(Fields.Protocol, protocol);
        // Application
        String application = (qualifiedNameMap.containsKey(Fields.Application) ? qualifiedNameMap.get(Fields.Application) : "");
        if ((application == null) || (application.length() == 0)) {
            application = "";
        }
        application = removeSeparators(application);
        checkedQualifiedNameMap.put(Fields.Application, application);
        // Instance
        String instance = (qualifiedNameMap.containsKey(Fields.Instance) ? qualifiedNameMap.get(Fields.Instance) : "");
        if ((instance == null) || (instance.length() == 0)) {
            instance = "";
            // throw new IllegalArgumentException("The instance name component of a fully qualified service cannot be empty.");
        }
        instance = removeSeparators(instance);
        checkedQualifiedNameMap.put(Fields.Instance, instance);
        // Optional Subtype
        String subtype = (qualifiedNameMap.containsKey(Fields.Subtype) ? qualifiedNameMap.get(Fields.Subtype) : "");
        if ((subtype == null) || (subtype.length() == 0)) {
            subtype = "";
        }
        subtype = removeSeparators(subtype);
        checkedQualifiedNameMap.put(Fields.Subtype, subtype);

        return checkedQualifiedNameMap;
    }

    private static String removeSeparators(String name) {
        if (name == null) {
            return "";
        }
        String newName = name.trim();
        if (newName.startsWith(".")) {
            newName = newName.substring(1);
        }
        if (newName.startsWith("_")) {
            newName = newName.substring(1);
        }
        if (newName.endsWith(".")) {
            newName = newName.substring(0, newName.length() - 1);
        }
        return newName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        String domain = this.getDomain();
        String protocol = this.getProtocol();
        String application = this.getApplication();
        return (application.length() > 0 ? "_" + application + "." : "") + (protocol.length() > 0 ? "_" + protocol + "." : "") + domain + ".";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeWithSubtype() {
        String subtype = this.getSubtype();
        return (subtype.length() > 0 ? "_" + subtype.toLowerCase() + "._sub." : "") + this.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return (_name != null ? _name : "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        if (this._key == null) {
            this._key = this.getQualifiedName().toLowerCase();
        }
        return this._key;
    }

    /**
     * Sets the service instance name.
     *
     * @param name
     *            unqualified service instance name, such as <code>foobar</code>
     */
    void setName(String name) {
        this._name = name;
        this._key = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQualifiedName() {
        String domain = this.getDomain();
        String protocol = this.getProtocol();
        String application = this.getApplication();
        String instance = this.getName();
        // String subtype = this.getSubtype();
        // return (instance.length() > 0 ? instance + "." : "") + (application.length() > 0 ? "_" + application + "." : "") + (protocol.length() > 0 ? "_" + protocol + (subtype.length() > 0 ? ",_" + subtype.toLowerCase() + "." : ".") : "") + domain
        // + ".";
        return (instance.length() > 0 ? instance + "." : "") + (application.length() > 0 ? "_" + application + "." : "") + (protocol.length() > 0 ? "_" + protocol + "." : "") + domain + ".";
    }

    /**
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#getServer()
     */
    @Override
    public String getServer() {
        return (_server != null ? _server : "");
    }

    /**
     * @param server
     *            the server to set
     */
    void setServer(String server) {
        this._server = server;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHostAddress() {
        return (this.getInetAddress() != null ? this.getInetAddress().getHostAddress() : "");
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public InetAddress getAddress() {
        return this.getInetAddress();
    }

    /**
     * @param addr
     *            the addr to set
     */
    void setAddress(Inet4Address addr) {
        this._ipv4Addr = addr;
        addAddress(addr);
    }

    /**
     * @param addr
     *            the addr to set
     */
    void setAddress(Inet6Address addr) {
        this._ipv6Addr = addr;
        addAddress(addr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getInetAddress() {
        return (_ipv4Addr != null ? _ipv4Addr : _ipv6Addr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inet4Address getInet4Address() {
        return _ipv4Addr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inet6Address getInet6Address() {
        return _ipv6Addr;
    }

    /**
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#getPort()
     */
    @Override
    public int getPort() {
        return _port;
    }

    /**
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#getPriority()
     */
    @Override
    public int getPriority() {
        return _priority;
    }

    /**
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#getWeight()
     */
    @Override
    public int getWeight() {
        return _weight;
    }

    /**
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#getTextBytes()
     */
    @Override
    public byte[] getTextBytes() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String getTextString() {
        Map<String, byte[]> properties = this.getProperties();
        for (String key : properties.keySet()) {
            byte[] value = properties.get(key);
            if ((value != null) && (value.length > 0)) {
                return key + "=" + new String(value);
            }
            return key;
        }
        return "";
    }

    /**
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#getURL()
     */
    @Override
    public String getURL() {
        return getURL("http");
    }

    /**
     * @see org.trianacode.pegasus.jmdns.ServiceInfo#getURL(java.lang.String)
     */
    @Override
    public String getURL(String protocol) {
        String url = protocol + "://" + getHostAddress() + ":" + getPort();
        String path = getPropertyString("path");
        if (path != null) {
            if (path.indexOf("://") >= 0) {
                url = path;
            } else {
                url += path.startsWith("/") ? path : "/" + path;
            }
        }
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized byte[] getPropertyBytes(String name) {
        return this.getProperties().get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getPropertyString(String name) {
        byte data[] = this.getProperties().get(name);
        if (data == null) {
            return null;
        }
        if (data == NO_VALUE) {
            return "true";
        }
        return readUTF(data, 0, data.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getPropertyNames() {
        Map<String, byte[]> properties = this.getProperties();
        Collection<String> names = (properties != null ? properties.keySet() : Collections.<String> emptySet());
        return new Vector<String>(names).elements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getApplication() {
        return (_application != null ? _application : "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDomain() {
        return (_domain != null ? _domain : "local");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocol() {
        return (_protocol != null ? _protocol : "tcp");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubtype() {
        return (_subtype != null ? _subtype : "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Fields, String> getQualifiedNameMap() {
        Map<Fields, String> map = new HashMap<Fields, String>(5);

        map.put(Fields.Domain, this.getDomain());
        map.put(Fields.Protocol, this.getProtocol());
        map.put(Fields.Application, this.getApplication());
        map.put(Fields.Instance, this.getName());
        map.put(Fields.Subtype, this.getSubtype());
        return map;
    }

    @Override
    public InetAddress[] getInetAddresses() {
        return _addresses.toArray(new InetAddress[_addresses.size()]);
    }

    private void addAddress(InetAddress address) {
        _addresses.add(address);
    }

    /**
     * Write a UTF string with a length to a stream.
     */
    static void writeUTF(OutputStream out, String str) throws IOException {
        for (int i = 0, len = str.length(); i < len; i++) {
            int c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                out.write(c);
            } else {
                if (c > 0x07FF) {
                    out.write(0xE0 | ((c >> 12) & 0x0F));
                    out.write(0x80 | ((c >> 6) & 0x3F));
                    out.write(0x80 | ((c >> 0) & 0x3F));
                } else {
                    out.write(0xC0 | ((c >> 6) & 0x1F));
                    out.write(0x80 | ((c >> 0) & 0x3F));
                }
            }
        }
    }

    /**
     * Read data bytes as a UTF stream.
     */
    String readUTF(byte data[], int off, int len) {
        int offset = off;
        StringBuffer buf = new StringBuffer();
        for (int end = offset + len; offset < end;) {
            int ch = data[offset++] & 0xFF;
            switch (ch >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    // 0xxxxxxx
                    break;
                case 12:
                case 13:
                    if (offset >= len) {
                        return null;
                    }
                    // 110x xxxx 10xx xxxx
                    ch = ((ch & 0x1F) << 6) | (data[offset++] & 0x3F);
                    break;
                case 14:
                    if (offset + 2 >= len) {
                        return null;
                    }
                    // 1110 xxxx 10xx xxxx 10xx xxxx
                    ch = ((ch & 0x0f) << 12) | ((data[offset++] & 0x3F) << 6) | (data[offset++] & 0x3F);
                    break;
                default:
                    if (offset + 1 >= len) {
                        return null;
                    }
                    // 10xx xxxx, 1111 xxxx
                    ch = ((ch & 0x3F) << 4) | (data[offset++] & 0x0f);
                    break;
            }
            buf.append((char) ch);
        }
        return buf.toString();
    }

    synchronized Map<String, byte[]> getProperties() {
        if ((_props == null) && (this.getText() != null)) {
            Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();
            try {
                int off = 0;
                while (off < getText().length) {
                    // length of the next key value pair
                    int len = getText()[off++] & 0xFF;
                    if ((len == 0) || (off + len > getText().length)) {
                        properties.clear();
                        break;
                    }
                    // look for the '='
                    int i = 0;
                    for (; (i < len) && (getText()[off + i] != '='); i++) {
                        /* Stub */
                    }

                    // get the property name
                    String name = readUTF(getText(), off, i);
                    if (name == null) {
                        properties.clear();
                        break;
                    }
                    if (i == len) {
                        properties.put(name, NO_VALUE);
                    } else {
                        byte value[] = new byte[len - ++i];
                        System.arraycopy(getText(), off + i, value, 0, len - i);
                        properties.put(name, value);
                        off += len;
                    }
                }
            } catch (Exception exception) {
                // We should get better logging.
                logger.log(Level.WARNING, "Malformed TXT Field ", exception);
            }
            this._props = properties;
        }
        return (_props != null ? _props : Collections.<String, byte[]> emptyMap());
    }

    /**
     * JmDNS callback to update a DNS record.
     *
     * @param dnsCache
     * @param now
     * @param rec
     */
    @Override
    public void updateRecord(org.trianacode.pegasus.jmdns.impl.DNSCache dnsCache, long now, org.trianacode.pegasus.jmdns.impl.DNSEntry rec) {
        if ((rec instanceof org.trianacode.pegasus.jmdns.impl.DNSRecord) && !rec.isExpired(now)) {
            boolean serviceUpdated = false;
            switch (rec.getRecordType()) {
                case TYPE_A: // IPv4
                    if (rec.getName().equalsIgnoreCase(this.getServer())) {
                        _ipv4Addr = (Inet4Address) ((org.trianacode.pegasus.jmdns.impl.DNSRecord.Address) rec).getAddress();
                        addAddress(_ipv4Addr);
                        serviceUpdated = true;
                    }
                    break;
                case TYPE_AAAA: // IPv6
                    if (rec.getName().equalsIgnoreCase(this.getServer())) {
                        _ipv6Addr = (Inet6Address) ((org.trianacode.pegasus.jmdns.impl.DNSRecord.Address) rec).getAddress();
                        addAddress(_ipv6Addr);
                        serviceUpdated = true;
                    }
                    break;
                case TYPE_SRV:
                    if (rec.getName().equalsIgnoreCase(this.getQualifiedName())) {
                        org.trianacode.pegasus.jmdns.impl.DNSRecord.Service srv = (org.trianacode.pegasus.jmdns.impl.DNSRecord.Service) rec;
                        boolean serverChanged = (_server == null) || !_server.equalsIgnoreCase(srv.getServer());
                        _server = srv.getServer();
                        _port = srv.getPort();
                        _weight = srv.getWeight();
                        _priority = srv.getPriority();
                        if (serverChanged) {
                            _ipv4Addr = null;
                            _ipv6Addr = null;
                            this.updateRecord(dnsCache, now, dnsCache.getDNSEntry(_server, DNSRecordType.TYPE_A, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN));
                            this.updateRecord(dnsCache, now, dnsCache.getDNSEntry(_server, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_AAAA, DNSRecordClass.CLASS_IN));
                            // We do not want to trigger the listener in this case as it will be triggered if the address resolves.
                        } else {
                            serviceUpdated = true;
                        }
                    }
                    break;
                case TYPE_TXT:
                    if (rec.getName().equalsIgnoreCase(this.getQualifiedName())) {
                        org.trianacode.pegasus.jmdns.impl.DNSRecord.Text txt = (org.trianacode.pegasus.jmdns.impl.DNSRecord.Text) rec;
                        _text = txt.getText();
                        serviceUpdated = true;
                    }
                    break;
                case TYPE_PTR:
                    if ((this.getSubtype().length() == 0) && (rec.getSubtype().length() != 0)) {
                        _subtype = rec.getSubtype();
                        serviceUpdated = true;
                    }
                    break;
                default:
                    break;
            }
            if (serviceUpdated && this.hasData()) {
                org.trianacode.pegasus.jmdns.impl.JmDNSImpl dns = this.getDns();
                if (dns != null) {
                    org.trianacode.pegasus.jmdns.ServiceEvent event = ((org.trianacode.pegasus.jmdns.impl.DNSRecord) rec).getServiceEvent(dns);
                    event = new org.trianacode.pegasus.jmdns.impl.ServiceEventImpl(dns, event.getType(), event.getName(), this);
                    dns.handleServiceResolved(event);
                }
            }
            // This is done, to notify the wait loop in method JmDNS.waitForInfoData(ServiceInfo info, int timeout);
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    /**
     * Returns true if the service info is filled with data.
     *
     * @return <code>true</code> if the service info has data, <code>false</code> otherwise.
     */
    @Override
    public synchronized boolean hasData() {
        return this.getServer() != null && this.getInetAddress() != null && this.getTextBytes() != null && this.getTextBytes().length > 0;
        // return this.getServer() != null && (this.getAddress() != null || (this.getTextBytes() != null && this.getTextBytes().length > 0));
    }

    // State machine

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean advanceState(DNSTask task) {
        return _state.advanceState(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean revertState() {
        return _state.revertState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cancelState() {
        return _state.cancelState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean recoverState() {
        return this._state.recoverState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAssociationWithTask(DNSTask task) {
        _state.removeAssociationWithTask(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associateWithTask(org.trianacode.pegasus.jmdns.impl.tasks.DNSTask task, org.trianacode.pegasus.jmdns.impl.constants.DNSState state) {
        _state.associateWithTask(task, state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAssociatedWithTask(DNSTask task, DNSState state) {
        return _state.isAssociatedWithTask(task, state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProbing() {
        return _state.isProbing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnouncing() {
        return _state.isAnnouncing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnounced() {
        return _state.isAnnounced();
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
        return _state.isCanceled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitForAnnounced(long timeout) {
        return _state.waitForAnnounced(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitForCanceled(long timeout) {
        return _state.waitForCanceled(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getQualifiedName().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ServiceInfoImpl) && getQualifiedName().equals(((ServiceInfoImpl) obj).getQualifiedName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNiceTextString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0, len = this.getText().length; i < len; i++) {
            if (i >= 200) {
                buf.append("...");
                break;
            }
            int ch = getText()[i] & 0xFF;
            if ((ch < ' ') || (ch > 127)) {
                buf.append("\\0");
                buf.append(Integer.toString(ch, 8));
            } else {
                buf.append((char) ch);
            }
        }
        return buf.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceInfoImpl clone() {
        ServiceInfoImpl serviceInfo = new ServiceInfoImpl(this.getQualifiedNameMap(), _port, _weight, _priority, _persistent, _text);
        InetAddress[] addresses = this.getInetAddresses();
        for (InetAddress address : addresses) {
            serviceInfo.addAddress(address);
        }
        serviceInfo._ipv4Addr = this.getInet4Address();
        serviceInfo._ipv6Addr = this.getInet6Address();
        return serviceInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[" + this.getClass().getSimpleName() + "@" + System.identityHashCode(this) + " ");
        buf.append("name: '");
        buf.append((this.getName().length() > 0 ? this.getName() + "." : "") + this.getTypeWithSubtype());
        buf.append("' address: '");
        InetAddress[] addresses = this.getInetAddresses();
        if (addresses.length > 0) {
            for (InetAddress address : addresses) {
                buf.append(address);
                buf.append(':');
                buf.append(this.getPort());
                buf.append(' ');
            }
        } else {
            buf.append("(null):");
            buf.append(this.getPort());
        }
        buf.append("' status: '");
        buf.append(_state.toString());
        buf.append(this.isPersistent() ? "' is persistent," : "',");
        buf.append(" has ");
        buf.append(this.hasData() ? "" : "NO ");
        buf.append("data");
        if (this.getText().length > 0) {
            // buf.append("\n");
            // buf.append(this.getNiceTextString());
            Map<String, byte[]> properties = this.getProperties();
            if (!properties.isEmpty()) {
                buf.append("\n");
                for (String key : properties.keySet()) {
                    buf.append("\t" + key + ": " + new String(properties.get(key)) + "\n");
                }
            } else {
                buf.append(" empty");
            }
        }
        buf.append(']');
        return buf.toString();
    }

    public Collection<org.trianacode.pegasus.jmdns.impl.DNSRecord> answers(boolean unique, int ttl, org.trianacode.pegasus.jmdns.impl.HostInfo localHost) {
        List<org.trianacode.pegasus.jmdns.impl.DNSRecord> list = new ArrayList<org.trianacode.pegasus.jmdns.impl.DNSRecord>();
        if (this.getSubtype().length() > 0) {
            list.add(new Pointer(this.getTypeWithSubtype(), DNSRecordClass.CLASS_IN, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE, ttl, this.getQualifiedName()));
        }
        list.add(new Pointer(this.getType(), DNSRecordClass.CLASS_IN, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE, ttl, this.getQualifiedName()));
        list.add(new Service(this.getQualifiedName(), DNSRecordClass.CLASS_IN, unique, ttl, _priority, _weight, _port, localHost.getName()));
        list.add(new Text(this.getQualifiedName(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN, unique, ttl, this.getText()));
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText(byte[] text) throws IllegalStateException {
        synchronized (this) {
            this._text = text;
            this._props = null;
            this.setNeedTextAnnouncing(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText(Map<String, ?> props) throws IllegalStateException {
        this.setText(textFromProperties(props));
    }

    /**
     * This is used internally by the framework
     *
     * @param text
     */
    void _setText(byte[] text) {
        this._text = text;
        this._props = null;
    }

    private static byte[] textFromProperties(Map<String, ?> props) {
        byte[] text = null;
        if (props != null) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream(256);
                for (String key : props.keySet()) {
                    Object val = props.get(key);
                    ByteArrayOutputStream out2 = new ByteArrayOutputStream(100);
                    writeUTF(out2, key);
                    if (val == null) {
                        // Skip
                    } else if (val instanceof String) {
                        out2.write('=');
                        writeUTF(out2, (String) val);
                    } else if (val instanceof byte[]) {
                        byte[] bval = (byte[]) val;
                        if (bval.length > 0) {
                            out2.write('=');
                            out2.write(bval, 0, bval.length);
                        } else {
                            val = null;
                        }
                    } else {
                        throw new IllegalArgumentException("invalid property value: " + val);
                    }
                    byte data[] = out2.toByteArray();
                    if (data.length > 255) {
                        throw new IOException("Cannot have individual values larger that 255 chars. Offending value: " + key + (val != null ? "" : "=" + val));
                    }
                    out.write((byte) data.length);
                    out.write(data, 0, data.length);
                }
                text = out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("unexpected exception: " + e);
            }
        }
        return (text != null && text.length > 0 ? text : org.trianacode.pegasus.jmdns.impl.DNSRecord.EMPTY_TXT);
    }

    public byte[] getText() {
        return (this._text != null && this._text.length > 0 ? this._text : org.trianacode.pegasus.jmdns.impl.DNSRecord.EMPTY_TXT);
    }

    public void setDns(org.trianacode.pegasus.jmdns.impl.JmDNSImpl dns) {
        this._state.setDns(dns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.trianacode.pegasus.jmdns.impl.JmDNSImpl getDns() {
        return this._state.getDns();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPersistent() {
        return _persistent;
    }

    /**
     * @param needTextAnnouncing
     *            the needTextAnnouncing to set
     */
    public void setNeedTextAnnouncing(boolean needTextAnnouncing) {
        this._needTextAnnouncing = needTextAnnouncing;
        if (this._needTextAnnouncing) {
            _state.setTask(null);
        }
    }

    /**
     * @return the needTextAnnouncing
     */
    public boolean needTextAnnouncing() {
        return _needTextAnnouncing;
    }

    /**
     * @return the delegate
     */
    Delegate getDelegate() {
        return this._delegate;
    }

    /**
     * @param delegate
     *            the delegate to set
     */
    void setDelegate(Delegate delegate) {
        this._delegate = delegate;
    }

}
