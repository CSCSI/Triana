package org.trianacode.discovery;


/**
 * Created by IntelliJ IDEA. User: scmijt Date: Jul 30, 2010 Time: 2:37:49 PM To change this template use File |
 * Settings | File Templates.
 */
public class DiscoverTools {

//    private static Log log = Loggers.TOOL_LOGGER;
//
//
//    private static WebBootstrap bonjourServer;
//    private ServiceTypesAndProtocols tdpProtocols;
//    private DiscoveredTools discoveredServices;
//    private ToolResolver toolResolver;
//
//    private HttpPeer httpEngine;
//    private TrianaProperties properties;
//
//    public DiscoverTools(ToolResolver resolver, HttpPeer httpEngine, TrianaProperties properties) {
//        this.tdpProtocols = new ServiceTypesAndProtocols();
//        this.httpEngine = httpEngine;
//        this.properties = properties;
//        this.toolResolver = resolver;
//        Thread discoverThread = new Thread(this);
//        discoverThread.setPriority(Thread.MIN_PRIORITY);
//        discoverThread.start();
//    }
//
//    public void startServices(ToolResolver resolver) {
//        new LocalTrawler(httpEngine, resolver);
//    }
//
//    public ToolResolver getToolResolver() {
//        return toolResolver;
//    }
//
//    public void run() {
//        startServices(toolResolver);
//        ServiceTypes st = new ServiceTypes();
//
//        // you would use this to provide custom icons for Triana etc
//        WebDefines webDefines = new WebDefines(null, null, null, null, null);
//
//        discoveredServices = new DiscoveredTools(this);
//
//        try {
//            bonjourServer = new WebBootstrap(discoveredServices, httpEngine,
//                    "TrianaServer", "triana-web", "Triana Bonjour Service!",
//                    "Published Services", webDefines, st);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//
//        //tick(null);
//        //  scan every 60 seconds....
//        //   timer = new Timer(this, 10000);
//        // timer.cont();
//
//        tick(null);
//
//    }
//
//    public void tick(sun.misc.Timer timer) {
//        log.debug("Looking for bonjour services !!");
//        Object[] protocols = discoveredServices.getProtocols().toArray();
//
//        for (Object obj : protocols) {
//            ServiceInfoEndpoint protocol = (ServiceInfoEndpoint) obj;
//            String endpoint = "http://" + protocol.getServiceAddress() + ":" + protocol.getPort() + "/"
//                    + TDPServer.command;
//
//            TDPRequest request = new TDPRequest(TDPRequest.Request.GET_TOOLS_LIST);
//
//            try {
//                TDPResponse data = sendMessageToServer(request, endpoint);
//
//                List<ToolMetadata> tools = data.getTools();
//
//                log.debug("Here's the list of tools found from the bonjour service  !!");
//
//                for (ToolMetadata toolmd : tools) {
//                    log.debug(toolmd.toString());
//                    discoveredServices.addTool(toolmd, protocol);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//    }
//
//    private TDPResponse sendMessageToServer(TDPRequest request, String endpoint)
//            throws IOException, ClassNotFoundException {
//        TDPResponse data;
//        ObjectInputStream r = null;
//
//        RequestContext c = new RequestContext(endpoint);
//
//        Response response = sendRequest(c, request);
//
//        RequestContext rc = response.getContext();
//        Streamable stream = rc.getResource().getStreamable();
//
//
//        r = new ObjectInputStream(stream.getInputStream());
//        data = (TDPResponse) r.readObject();
//        return data;
//    }
//
//    private Response sendRequest(RequestContext c, TDPRequest request) throws IOException {
//        c.setResource(new Resource(new StreamableObject(request)));
//        return httpEngine.post(c);
//
//    }
//
//    public static WebBootstrap getBonjourServer() {
//        return bonjourServer;
//    }
//
//    public void shutdown() {
//        if (bonjourServer != null) {
//            bonjourServer.getDiscovery().shutdown();
//        }
//    }
}
