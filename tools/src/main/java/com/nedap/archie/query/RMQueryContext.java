package com.nedap.archie.query;

import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * It's done by converting the RM objects into an XML-DOM using JAXB's Binder. XPATH is then evaluated against the DOM.
 * The binder enables us to return the original objects.
 *
 * The APATH-shorthand notation is converted to its equivalent XPATH-notation before evaluation
 *
 * Created by pieter.bos on 03/05/16.
 */
public class RMQueryContext {

    private final XPathFactory xPathFactory;
    private final ModelInfoLookup modelInfoLooup;
    private Binder<Node> binder;
    private Document domForQueries;
    private Object rootNode;

    /**
     * TODO: for now we will add /firstXPathNode, because otherwise there will be something like '/composition' missing
     * However that is rather annoying, because apath does not specify this. So find a way of fixing this.
     */
    private String firstXPathNode;
    private static Logger logger = LoggerFactory.getLogger(RMQueryContext.class);



    /**
     * Construct a query object for a given root node with a given RM implementation. Requires a JaxbContext.
     * @param rootNode
     */
    public RMQueryContext(ModelInfoLookup lookup, Object rootNode, JAXBContext jaxbContext) {
        try {
            this.rootNode = rootNode;
            this.modelInfoLooup = lookup;
            this.binder = jaxbContext.createBinder();
            domForQueries = createBlankDOMDocument(true);

            binder.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Marshall Query object to a blank DOM document.
            //Binder will maintains association between two views.
            binder.marshal( rootNode/*new JAXBElement<Query>(qname, Query.class, query)*/  , domForQueries);

            firstXPathNode = domForQueries.getFirstChild().getNodeName();

            //print to stdout
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.marshal(rootNode, new StreamResult(System.out));

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        xPathFactory = XPathFactory.newInstance();
    }

    public Document createBlankDOMDocument(boolean namespaceAware) {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        fact.setNamespaceAware(namespaceAware);
        DocumentBuilder builder;
        try {
            builder = fact.newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        return builder.newDocument();
    }

    public <T> List<T> findList(String query) throws XPathExpressionException {
        List<T> result = new ArrayList<T>();

        if (query.equals("/")) {
            result.add((T) rootNode);
        } else {

            String convertedQuery = APathToXPathConverter.convertQueryToXPath(query, firstXPathNode);
            XPath xpath = xPathFactory.newXPath();


            xpath.setNamespaceContext(new ArchieNamespaceResolver(domForQueries));
            NodeList foundNodes = (NodeList) xpath.evaluate(convertedQuery, domForQueries, XPathConstants.NODESET);

            //Perform decoration
            for (int i = 0; i < foundNodes.getLength(); i++) {
                Node node = foundNodes.item(i);
                result.add(getJAXBNode(node));
            }
        }

        return result;
    }

    private <T> T getJAXBNode(Node node) {
        T object = (T) binder.getJAXBNode(node);
        if(object != null) {
            return object;
        } else{
            //JAXB sometimes has trouble binding primitive values. Looks like a bug in Xerces
            //very annoying. Here's our workaround: lookup the parent node and manually get the correct attribute
            String nodeName = node.getNodeName();
            //the parent usually can be found easily
            Object parent = binder.getJAXBNode(node.getParentNode());
            if(parent == null) {
                logger.error("trying to get a node without a parent");
                return null;
            }
            RMAttributeInfo attributeInfo = modelInfoLooup.getAttributeInfo(parent.getClass(), nodeName);
            try {
                return (T) attributeInfo.getGetMethod().invoke(parent);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Node getNode(Object object) {
        return binder.getXMLNode(object);
    }

    public String getUniquePath(Object object) {
        if(object == null) {
            return null;
        }
        Node node = getNode(object);
        if(node == null) {
            return null;
        } else {
            return UniqueNodePathBuilder.constructPath(node);
        }
    }


    public List<RMObjectWithPath> findListWithPaths(String query) throws XPathExpressionException {
        String convertedQuery = APathToXPathConverter.convertQueryToXPath(query, firstXPathNode);
        XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext( new ArchieNamespaceResolver(domForQueries));
        NodeList foundNodes = (NodeList)xpath.evaluate(convertedQuery, domForQueries, XPathConstants.NODESET);
        List<RMObjectWithPath> result = new ArrayList<>();
        for(int i=0; i<foundNodes.getLength(); i++){
            Node node = foundNodes.item(i);
            String path = UniqueNodePathBuilder.constructPath(node);
            result.add(new RMObjectWithPath(getJAXBNode(node), path));
        }
        return result;
    }

    public <T> T find(String query) throws XPathExpressionException {
        List<T> result = findList(query);
        if(result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            throw new RuntimeException("query returned more than one element: " + result.size());
        }
    }

    /**
     * Call this to mark the RMObject value as updated in the XML context
     * @param parent
     */
    public void updateValue(Object parent) throws JAXBException {
        this.binder.updateXML(parent);

    }
}
