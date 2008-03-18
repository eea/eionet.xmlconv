/*
 * Created on 12.02.2008
 */
package eionet.gdem.dcm.results;

import java.util.ArrayList;

/**
 * Converts the listConversions method result as XML.
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class GetXMLSchemasResult extends XMLResultStrategy{

	public static final String SCHEMA_TAG="schema";
	
	private ArrayList schemas = null;
	
	public GetXMLSchemasResult() {
	}
	/**
	 * Set the data
	 * 
	 * @param list
	 */
	public void setResult(ArrayList list){
		schemas = list;
	}
		/**
	 * write data into XML
	 */
	protected void writeElements() throws Exception{
		if(schemas==null)return;
		for (int i = 0; i<schemas.size();i++){
			String schema = (String)schemas.get(i);
			writeSimpleElement(SCHEMA_TAG,schema);			
		}
	}



}
