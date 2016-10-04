package eionet.gdem.utils.xml.tiny;

import eionet.gdem.utils.xml.XPathQuery;
import eionet.gdem.utils.xml.XmlException;
import net.sf.saxon.s9api.*;
import net.sf.saxon.sxpath.IndependentContext;
import org.w3c.dom.Node;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author George Sofianos
 */
public class TinyTreeXpath implements XPathQuery {

    private XdmNode root = null;
    private XPathCompiler compiler;

    /**
     * Tiny Tree XPATH constructor.
     * @param processor - Saxon Processor
     * @param root - Document node
     */
    public TinyTreeXpath(Processor processor, XdmNode root) {
        this.root = root;
        compiler = processor.newXPathCompiler();
    }

    @Override
    public Node findElementByAttrs(String parentId, Map<String, String> attributes) throws XmlException {
        return null;
    }

    @Override
    public String getAttributeValue(String parentId, String attribute) throws XmlException {
        return null;
    }

    @Override
    public String getElementValue(String parentId, String name) throws XmlException {
        return null;
    }

    @Override
    public Node findElementById(String id) throws XmlException {
        return null;
    }

    @Override
    public List<String> getElementIdentifiers(String elementName) throws XmlException {
        return null;
    }

    @Override
    public List<Map<String, String>> getElementAttributes(String elementName) throws XmlException {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            XPathSelector elementSelector = compiler.compile("//" + elementName).load();
            elementSelector.setContextItem(root);
            Map<String, String> attributes = null;
            for (XdmItem item : elementSelector) {
                attributes = new HashMap<>();
                XdmSequenceIterator attributeSequence = ((XdmNode) item).axisIterator(Axis.ATTRIBUTE);
                while (attributeSequence.hasNext()) {
                    XdmNode attribute = (XdmNode) attributeSequence.next();
                    attributes.put(attribute.getNodeName().getLocalName(), attribute.getStringValue());
                }
                if (attributes != null) {
                    result.add(attributes);
                }
            }
        } catch (SaxonApiException e) {
            throw new XmlException(e);
        }
        return result;
    }

    @Override
    public List<String> getElementValues(String elementName) throws XmlException {
        List<String> result = new ArrayList<String>();
        try {
            XPathSelector selector = compiler.compile("//" + elementName).load();
            selector.setContextItem(root);
            for (XdmItem item : selector) {
                result.add(item.getStringValue());
            }
        } catch (SaxonApiException e) {
            throw new XmlException(e);
        }
        return result;
    }

    @Override
    public List<String> getSchemaElements() throws XmlException {
        return null;
    }

    @Override
    public String getSchemaElementType(String elementName) throws XmlException {
        return null;
    }

    @Override
    public List<String> getSchemaImports() throws XmlException {
        return null;
    }

    @Override
    public Map<String, String> getSchemaElementWithMultipleValues() throws XmlException {
        return null;
    }

    /**
     * Adds namespace to XPATH compiler.
     * @param prefix - Namespace prefix
     * @param uri - Namespace uri
     */
    public void declareNamespace(String prefix, String uri) {
        compiler.declareNamespace(prefix, uri);
    }
}
