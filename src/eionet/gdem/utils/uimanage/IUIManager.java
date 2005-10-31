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

import java.util.Map;
import org.w3c.dom.Node;


public interface IUIManager {
	
	/**
	 * Replaces selected cell with new cell whose type attribute is set to blank  
	 * @param row Row that contains cell being removed  
	 * @param col Cols attribute of cell being removed
	 * @throws Exception 
	 */
	public void removeCell(int row, int col) throws Exception;
	/**
	 * Creates cell node that consists of content node which is image filename or text, 
	 * link node with URL, and style node that contains children nodes describing style  
	 * @param col Cell's cols attribute value. 
	 * @param type Type of content stored in cell.  
	 * @param link Value of link node. 
	 * @param content Value of content node.
	 * @param styleMap Map which is used for creating style node. Key is style child node name and value is child node value.
	 * @return Cell node with cols and type attributes and children nodes content, link and style
	 * @throws Exception
	 */
	public Node createCell(int col, String type, String link, String content, Map styleMap) throws Exception;
	/**
	 * Creates cell node that consists of content node which is image filename or text, 
	 * and style node that contains children nodes describing style 
	 * @param col Cell's cols attribute value.
	 * @param type Type of content stored in cell.
	 * @param content Value of content node.
	 * @param styleMap Map which is used for creating style node. Key is style child node name and value is child node value.
	 * @return Cell node with cols and type attributes and children nodes content and style.
	 * @throws Exception
	 */
	public Node createCell(int col, String type, String content, Map styleMap) throws Exception;	
}
