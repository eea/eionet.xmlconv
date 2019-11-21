package eionet.gdem.security.acl;

import eionet.acl.SignOnException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class WriteXMLFile {

    public static void writeGroups(String fileFullPath, Map<String, Group> groups) throws SignOnException {
        TransformerFactory trf = TransformerFactory.newInstance();
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        try {
            Document dom = groupsToDOM(groups);
            fos = new FileOutputStream(new File(fileFullPath));
            osw = new OutputStreamWriter(fos, "UTF-8");
            StreamResult streamResult = new StreamResult(osw);

            Transformer tr = trf.newTransformer();
            tr.setOutputProperty("method", "xml");
            tr.setOutputProperty("indent", "yes");
            tr.setOutputProperty("encoding", "UTF-8");
            tr.setOutputProperty("omit-xml-declaration","no");
            tr.transform(new DOMSource(), streamResult);
            tr.setOutputProperty("omit-xml-declaration","yes");
            tr.transform(new DOMSource(dom), streamResult);
        } catch (TransformerConfigurationException var18) {
            var18.printStackTrace(System.out);
            throw new SignOnException(var18, "Failed to write groups to " + fileFullPath + ", TransformerConfigurationException");
        } catch (ParserConfigurationException var19) {
            var19.printStackTrace(System.out);
            throw new SignOnException(var19, "Failed to write groups to " + fileFullPath + ", ParserConfigurationException");
        } catch (FileNotFoundException var20) {
            var20.printStackTrace(System.out);
            throw new SignOnException(var20, "Failed to write groups to " + fileFullPath + ", problem with creating or opening the file");
        } catch (UnsupportedEncodingException var21) {
            var21.printStackTrace(System.out);
            throw new SignOnException(var21, "Failed to write groups to " + fileFullPath + ", UnsupportedEncodingException");
        } catch (TransformerException var22) {
            var22.printStackTrace(System.out);
            throw new SignOnException(var22, "Failed to write groups to " + fileFullPath + ", TransformerException");
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }

                if (fos != null) {
                    fos.close();
                }
            } catch (Exception var17) {
            }

        }

    }

    private static Document groupsToDOM(Map groups) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElm = document.createElement("localgroups");
        document.appendChild(rootElm);
        if (groups != null && groups.size() != 0) {
            Iterator i = groups.keySet().iterator();

            while(true) {
                String groupID;
                do {
                    if (!i.hasNext()) {
                        return document;
                    }

                    groupID = (String)i.next();
                } while(groupID == null);

                Element groupElm = document.createElement("group");
                groupElm.setAttribute("id", groupID);
                rootElm.appendChild(groupElm);
                Vector members = (Vector)groups.get(groupID);

                for(int j = 0; members != null && j < members.size(); ++j) {
                    String userID = (String)members.get(j);
                    if (userID != null) {
                        Element memberElm = document.createElement("member");
                        memberElm.setAttribute("userid", userID);
                        groupElm.appendChild(memberElm);
                    }
                }
            }
        } else {
            return document;
        }
    }
}

