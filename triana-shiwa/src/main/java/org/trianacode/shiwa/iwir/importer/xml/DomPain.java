package org.trianacode.shiwa.iwir.importer.xml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: scmabh
 * @date: Sep 10, 2010
 */
public class DomPain {

    public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    public static final String NS_XMLNS = "http://www.w3.org/2000/xmlns/";


    public static Document newDocument() throws IOException {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static Document newDocument(InputStream stream) throws IOException {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(stream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static StreamResult transform(Node doc, OutputStream out) throws IOException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperty(OutputKeys.STANDALONE, "yes");

        } catch (TransformerConfigurationException tce) {
            assert (false);
        }
        DOMSource doms = new DOMSource(doc);
        StreamResult sr = new StreamResult(out);
        try {
            t.transform(doms, sr);
        } catch (TransformerException te) {
            throw new IOException(te.getMessage());
        }
        return sr;
    }

    public static StreamResult transform(Node doc, Writer out) throws IOException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperty(OutputKeys.STANDALONE, "yes");
        } catch (TransformerConfigurationException tce) {
            assert (false);
        }
        DOMSource doms = new DOMSource(doc);
        StreamResult sr = new StreamResult(out);
        try {
            t.transform(doms, sr);
        } catch (TransformerException te) {
            throw new IOException(te.getMessage());
        }
        return sr;
    }

    public static List<Element> getChildElements(Element parent) {
        NodeList nl = parent.getChildNodes();
        List<Element> ret = new ArrayList<Element>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                ret.add((Element) n);
            }
        }
        return ret;
    }

    public static List<Element> getChildElements(Element parent, String tagName) {
        NodeList nl = parent.getChildNodes();
        List<Element> ret = new ArrayList<Element>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                if ((n).getNodeName().equals(tagName)) {
                    ret.add((Element) n);
                }
            }
        }
        return ret;
    }

    public static Element getFirstChildElement(Element parent, String tagName) {
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                if (((Element) n).getNodeName().equals(tagName)) {
                    return (Element) n;
                }
            }
        }
        return null;
    }

    public static String getFirstChildElementContent(Element parent, String tagName) {
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                if (n.getNodeName().equals(tagName)) {
                    return ((Element) n).getTextContent();
                }
            }
        }
        return null;
    }

    public static Element getChild(Element parent, String tagName, String ns) {
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                if (n.getNodeName().equals(tagName) && n.getNamespaceURI().equals(ns)) {
                    return (Element) n;
                }
            }
        }
        return null;
    }

    public static String getText(Element element) {
        if (element == null) {
            return null;
        }
        return element.getTextContent().trim();
    }

    public static Element element(Document doc, Element parent, QName qname) {
        String prefix = qname.getPrefix();
        String tag = prefix == null ? qname.getLocalPart() : prefix + ":" + qname.getLocalPart();
        Element e = doc.createElementNS(qname.getNamespaceURI(), qname.getLocalPart());
        parent.appendChild(e);
        return e;
    }

    public static Element element(Document doc, Element parent, String local, String namespace, String content) {
        Element e = element(doc, parent, new QName(namespace, local));
        e.setTextContent(content);
        return e;
    }

    public static Element element(Document doc, QName qname, String content) {
        Element e = element(doc, qname);
        e.setTextContent(content);
        return e;
    }

    public static Element element(Document doc, Element parent, QName qname, String content) {
        Element e = element(doc, parent, qname);
        e.setTextContent(content);
        return e;
    }

    public static Element element(Document doc, Element parent, String local, String namespace) {
        Element e = element(doc, parent, new QName(namespace, local));
        return e;
    }

    public static Element element(Document doc, QName qname) {
        String prefix = qname.getPrefix();
        if (prefix != null && prefix.trim().length() == 0) {
            prefix = null;
        }
        String tag = prefix == null ? qname.getLocalPart() : prefix + ":" + qname.getLocalPart();
        return doc.createElementNS(qname.getNamespaceURI(), tag);
    }

    public static Element element(Document doc, String local, String ns) {
        return doc.createElementNS(ns, local);
    }

    public static Element element(Element parent, QName qname) {
        Document ownerDoc = getOwnerDocument(parent);
        Element child = element(ownerDoc, qname);
        parent.appendChild(child);
        return child;
    }

    public static Element element(Element parent, String local, String ns) {
        Document ownerDoc = getOwnerDocument(parent);
        Element child = element(ownerDoc, new QName(ns, local));
        parent.appendChild(child);
        return child;
    }

    public static void attribute(Element element, String name, String value) {
        if (value == null) return;
        element.setAttributeNS(null, name, value);
    }

    public static void xmlAttribute(Element element, String name, String value) {
        if (value == null) return;
        Document doc = element.getOwnerDocument();
        Attr attr = doc.createAttributeNS(NS_XML, name);
        if (!value.startsWith("xml:")) {
            value = "xml:" + value;
        }
        attr.setValue(value);
        element.setAttributeNodeNS(attr);
    }

    public static void xmlnsAttribute(Element element, String pref, String uri) {
        if (uri == null) return;
        System.out.println("DomPain.xmlnsAttribute SETTING XMLSN FOR PREFIX:" + pref + " AND NAMESPACE " + uri);
        element.setAttributeNS(NS_XMLNS, "xmlns:" + pref, uri);
    }


    public static void xsiAttribute(Element element, String name, String value) {
        if (value == null) return;
        Document doc = element.getOwnerDocument();
        Attr attr = doc.createAttributeNS(NS_XSI, name);
        attr.setValue(value);
        element.setAttributeNodeNS(attr);
    }

    public static Document getOwnerDocument(Node node) {
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            return (Document) node;
        } else {
            return node.getOwnerDocument();
        }
    }


    public static void add(Node parent, Node child) {
        Document ownerDoc = getOwnerDocument(parent);
        if (child.getOwnerDocument() != ownerDoc) {
            parent.appendChild(ownerDoc.importNode(child, true));
        } else {
            parent.appendChild(child);
        }
    }


    public static Element firstChild(Node node) {
        Node child = node.getFirstChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            child = child.getNextSibling();
        }
        return (Element) child;
    }

    public static Element lastChild(Node node) {
        Node child = node.getLastChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            child = child.getPreviousSibling();
        }
        return (Element) child;
    }

    public static Element nextSibling(Node node) {
        Node sibling = node.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling;
    }

    public static String getAttribute(Element elem, String name) {
        Attr attr = elem.getAttributeNodeNS(null, name);
        return (attr == null) ? null : attr.getValue();
    }

    public static String getNamespace(Node node, String searchPrefix) {

        Element el;
        while (!(node instanceof Element)) {
            node = node.getParentNode();
        }
        el = (Element) node;

        NamedNodeMap atts = el.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            Node currentAttribute = atts.item(i);
            String currentLocalName = currentAttribute.getLocalName();
            String currentPrefix = currentAttribute.getPrefix();

            if (searchPrefix.equals(currentLocalName) && "xmlns".equals(currentPrefix)) {
                return currentAttribute.getNodeValue();
            } else if (isEmpty(searchPrefix) && "xmlns".equals(currentLocalName)
                    && isEmpty(currentPrefix)) {
                return currentAttribute.getNodeValue();
            }
        }

        Node parent = el.getParentNode();
        if (parent instanceof Element) {
            return getNamespace(parent, searchPrefix);
        }

        return null;
    }

    public static String getNamespace(Attr node) {
        String value = node.getValue();
        if (value.indexOf(":") == -1) {
            return null;
        }
        String pref = value.substring(0, value.indexOf(":"));
        return getNamespace(node, pref);
    }

    public static QName getQName(Attr node, Element root) {
        String value = node.getValue();
        if (value.indexOf(":") == -1) {
            return null;
        }
        String pref = value.substring(0, value.indexOf(":"));
        String ns = getNamespace(root, pref);
        if (ns != null) {
            return new QName(ns, value.substring(value.indexOf(":") + 1), pref);
        }
        return null;
    }


    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }


    private static final String prettyPrintStylesheet =
            "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0' " +
                    " xmlns:xalan='http://xml.apache.org/xslt' " +
                    " exclude-result-prefixes='xalan'>" +
                    "  <xsl:output method='xml' indent='yes' xalan:indent-amount='4'/>" +
                    "  <xsl:strip-space elements='*'/>" +
                    "  <xsl:template match='/'>" +
                    "    <xsl:apply-templates/>" +
                    "  </xsl:template>" +
                    "  <xsl:template match='node() | @*'>" +
                    "        <xsl:copy>" +
                    "          <xsl:apply-templates select='node() | @*'/>" +
                    "        </xsl:copy>" +
                    "  </xsl:template>" +
                    "</xsl:stylesheet>";

    public static void prettify(Node element, OutputStream out) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DomPain.transform(element, baos);

            Source stylesheetSource = new StreamSource(new ByteArrayInputStream(prettyPrintStylesheet.getBytes()));
            Source xmlSource = new StreamSource(new ByteArrayInputStream(baos.toByteArray()));

            TransformerFactory tf = TransformerFactory.newInstance();
            Templates templates = tf.newTemplates(stylesheetSource);
            Transformer transformer = templates.newTransformer();
            transformer.transform(xmlSource, new StreamResult(out));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static void prettify(Node element, Writer out) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DomPain.transform(element, baos);

            Source stylesheetSource = new StreamSource(new ByteArrayInputStream(prettyPrintStylesheet.getBytes()));
            Source xmlSource = new StreamSource(new ByteArrayInputStream(baos.toByteArray()));

            TransformerFactory tf = TransformerFactory.newInstance();
            Templates templates = tf.newTemplates(stylesheetSource);
            Transformer transformer = templates.newTransformer();
            transformer.transform(xmlSource, new StreamResult(out));
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

}
