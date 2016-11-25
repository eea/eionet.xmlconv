/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Nenad Popovic (ED)
 */
package eionet.gdem.utils.uimanage;

import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * UI Manager class.
 * @author Unknown
 * @author George Sofianos
 *
 */
public class UIManager implements IUIManager {

    Document doc = null;

    /**
     * Constructor
     * @param doc Document
     */
    public UIManager(Document doc) {
        this.doc = doc;
    }

    /**
     * Default constructor
     */
    public UIManager() {

    }

    /**
     * Removes cell
     * @param row
     *            Row that contains cell being removed
     * @param col
     *            Cols attribute of cell being removed
     * @throws Exception If an error occurs.
     */
    public void removeCell(int row, int col) throws Exception {

        try {
            Node cell =
                    XPathAPI.selectSingleNode(doc, "ui-templates/template[@id='temp']/row[@id='" + row + "']/cell[@cols='" + col
                            + "']");
            Node parent = cell.getParentNode();
            Element emptyCell = doc.createElement("cell");
            emptyCell.setAttribute("cols", String.valueOf(col));
            emptyCell.setAttribute("type", "blank");
            parent.replaceChild(emptyCell, cell);

        } catch (TransformerException Te) {
            throw new Exception(Te.getMessage());

        }
    }

    /**
     * Creates cell
     * @param col
     *            Cell's cols attribute value.
     * @param type
     *            Type of content stored in cell.
     * @param link
     *            Value of link node.
     * @param content
     *            Value of content node.
     * @param styleMap
     *            Map which is used for creating style node. Key is style child node name and value is child node value.
     * @return Node
     * @throws Exception If an error occurs.
     */
    public Node createCell(int col, String type, String link, String content, Map styleMap) throws Exception {
        try {
            Element cell = doc.createElement("cell");
            cell.setAttribute("cols", Integer.toString(col));
            cell.setAttribute("type", type);
            Node contentNode = doc.createElement("content");
            Text newT = doc.createTextNode(content);
            contentNode.appendChild(newT);
            cell.appendChild(contentNode);
            if ((link != null) && (!link.equalsIgnoreCase(""))) {
                Node linkNode = doc.createElement("link");
                Text newL = doc.createTextNode(link);
                cell.appendChild(linkNode);
                linkNode.appendChild(newL);
            }
            Node styleNode = doc.createElement("style");
            Iterator styleKeys = styleMap.keySet().iterator();
            while (styleKeys.hasNext()) {
                String key = (String) styleKeys.next();
                Element tempStyle = doc.createElement(key);
                Text tempStyleText = doc.createTextNode((String) styleMap.get(key));
                tempStyle.appendChild(tempStyleText);
                styleNode.appendChild(tempStyle);
            }
            cell.appendChild(styleNode);
            return cell;
        } catch (DOMException domex) {
            throw new Exception(domex.getMessage());
        }
    }

    /**
     * Creates cell
     * @param col
     *            Cell's cols attribute value.
     * @param type
     *            Type of content stored in cell.
     * @param content
     *            Value of content node.
     * @param styleMap
     *            Map which is used for creating style node. Key is style child node name and value is child node value.
     * @return Node
     * @throws Exception If an error occurs.
     */
    public Node createCell(int col, String type, String content, Map styleMap) throws Exception {
        Node cell = createCell(col, type, null, content, styleMap);
        return cell;

    }
}
