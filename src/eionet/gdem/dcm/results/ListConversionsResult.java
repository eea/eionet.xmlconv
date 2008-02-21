/*
 * Created on 12.02.2008
 */
package eionet.gdem.dcm.results;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Converts the listConversions method result as XML.
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class ListConversionsResult extends XMLResultStrategy{

	private static final String CONVERSION_TAG="conversion";
	
	private Vector conversions = null;
	
	public ListConversionsResult() {
	}
	/**
	 * Set the data
	 * 
	 * @param list
	 */
	public void setResult(Vector list){
		conversions = list;
	}
		/**
	 * write data into XML
	 */
	protected void writeElements() throws Exception{
		if(conversions==null)return;
		for (int i = 0; i<conversions.size();i++){
			Hashtable h = (Hashtable)conversions.get(i);
			Enumeration keys = h.keys();
			
			hd.startElement("","",CONVERSION_TAG,atts);
			while (keys.hasMoreElements()){
				String key = (String)keys.nextElement();
				writeSimpleElement(key,(String)h.get(key));			
			}
			hd.endElement("","",CONVERSION_TAG);

		}
	}



}
