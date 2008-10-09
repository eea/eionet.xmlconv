package eionet.gdem.qa;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.remote.RemoteServiceMethod;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.utils.Utils;

/**
 * Implementation of listQueries and listQAScripts methods
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 */
public class ListQueriesMethod extends RemoteServiceMethod {

	private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();;
	private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
	
	/**
	 * List all possible QA scripts (XQueries, XML Schemas, DTD, XSLT?) for this XML Schema.
	 * If schema is null, then all possible QA scripts are returned
	 * 
	 * @param schema	URL of XML schema	
	 * @return			returns array of Hastables with the following keys:	qyery_id, short_name,
	 * description, query, schema_id, xml_schema, content_type_out, type
	 * 
	 * @throws GDEMException
	 */
	public Vector listQueries(String schema) throws GDEMException {

		Vector v = new Vector();
		try {
			//Get schemas that has to be validated
			Vector schemas=schemaDao.getSchemas(schema,false);
			if (schemas!=null){
				for (int i=0;i<schemas.size();i++){
					HashMap h = (HashMap)schemas.get(i);
					String validate = (String)h.get("validate");
					if (!Utils.isNullStr(validate)){
						if (validate.equals("1")){
							Hashtable ht = new Hashtable();
							ht.put("query_id", Constants.JOB_VALIDATION);
							ht.put("short_name", h.get("description"));
							ht.put("query", schema);
							ht.put("description", "XML Schema Validation");
							ht.put("schema_id", h.get("schema_id"));
							ht.put("xml_schema", schema);
							ht.put("content_type_out", "HTML");
							ht.put("type", ((String)h.get("schema_lang")).toLowerCase());
							v.add(ht);
						}
					}
				}
			}
			// Get XQueries
			Vector queries=queryDao.listQueries(schema);
			if (queries!=null){
				for (int i=0;i<queries.size();i++){
					Hashtable ht = (Hashtable)queries.get(i);
					ht.put("type", Constants.QA_TYPE_XQUERY);
					v.add(ht);					
				}
			}
		} catch (Exception e ) {
			throw new GDEMException("Error getting data from the DB " + e.toString(), e);
		}
		return v;
	}
	/**
	 * List all  XQueries and their modification times for this namespace
	 * returns also XML Schema validation
	 * 
	 * @param schema
	 * @return result is an Array of Arrays that contains 3 fields (script_id, description, last modification)
	 * @throws GDEMException
	 */
	public Vector listQAScripts(String schema) throws GDEMException {
		Vector vec = new Vector();
		Vector v1 = null;
		try {
			Vector v=schemaDao.getSchemas(schema);

			if (Utils.isNullVector(v)) return vec;

			HashMap h = (HashMap)v.get(0);
			String validate = (String)h.get("validate");
			if (!Utils.isNullStr(validate)){
				if (validate.equals("1")){
					v1 = new Vector();
					v1.add(String.valueOf(Constants.JOB_VALIDATION));
					v1.add("XML Schema Validation");
					v1.add("");
					vec.add(v1);
				}
			}
			Vector queries = (Vector)h.get("queries");
			if (Utils.isNullVector(queries)) return vec;

			for (int i = 0; i <queries.size();i++){
				HashMap hQueries = (HashMap)queries.get(i);
				String q_id = (String)hQueries.get("query_id");
				String q_file = (String)hQueries.get("query");
				String q_desc = (String)hQueries.get("descripton");
				String q_name = (String)hQueries.get("short_name");
				if (Utils.isNullStr(q_desc)){
					if (Utils.isNullStr(q_name)){
						q_desc = "Quality Assurance script";
					}
					else {
						q_desc=q_name;
					}
				}
				v1 = new Vector();
				v1.add(q_id);
				v1.add(q_desc);
				File f=new File(Properties.queriesFolder + q_file);
				String last_modified="";

				if (f!=null)
					last_modified = Utils.getDateTime(new Date(f.lastModified()));;
					//DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(new Date(f.lastModified()));

					v1.add(last_modified);
					vec.add(v1);
			}


		} catch (Exception e ) {
			throw new GDEMException("Error getting data from the DB " + e.toString(), e);
		}

		return vec;
	}
}
