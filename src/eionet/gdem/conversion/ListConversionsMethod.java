/*
 * Created on 19.02.2008
 */
package eionet.gdem.conversion;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IConvTypeDao;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ListConversionsMethod
 */

public class ListConversionsMethod {

	private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

	private static LoggerIF _logger = GDEMServices.getLogger();

	public Vector listConversions(String schema) throws GDEMException {
		Vector v = new Vector();
		List ddTables = DDServiceClient.getDDTables();
		List convs = Conversion.getConversions();

		if (schema != null && schema.startsWith(Properties.ddURL) && ddTables!=null) {
			// schema is from DD
			// parse tbl id
			// check tbl id
			String tblId = schema.substring(schema.indexOf("id=TBL") + 6,
					schema.length());
			boolean existsInDD = false;
			for (Iterator iter = ddTables.iterator(); iter.hasNext();) {
				Hashtable element = (Hashtable) iter.next();
				if (((String) element.get("tblId")).equalsIgnoreCase(tblId)) {
					existsInDD = true;
					break;
				}
			}

			if (existsInDD) {
				for (int i = 0; i < convs.size(); i++) {
					Hashtable h = new Hashtable();
					h.put("convert_id", "DD_TBL" + tblId + "_CONV"
							+ ((ConversionDto) convs.get(i)).getConvId());
					h.put("xsl", Properties.gdemURL + "/do/getStylesheet?id="
							+ tblId + "&conv="
							+ ((ConversionDto) convs.get(i)).getConvId());
					h.put("description", ((ConversionDto) convs.get(i))
							.getDescription());
					h.put("content_type_out", ((ConversionDto) convs.get(i))
							.getContentType());
					h.put("result_type", ((ConversionDto) convs.get(i))
							.getResultType());
					h.put("xml_schema", schema);
					v.add(h);
				}
			}

		}
		//
		if (schema == null && ddTables!=null) {

			for (int i = 0; i < ddTables.size(); i++) {
				Hashtable schemaDD = (Hashtable) ddTables.get(i);
				String tblId = (String) schemaDD.get("tblId");
				String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL"
				+ tblId;

				for (int j = 0; j < convs.size(); j++) {
					Hashtable h = new Hashtable();
					h.put("convert_id", "DD_TBL" + tblId + "_CONV"
							+ ((ConversionDto) convs.get(j)).getConvId());
					h.put("xsl", Properties.gdemURL + "/do/getStylesheet?id="
							+ tblId + "&conv="
							+ ((ConversionDto) convs.get(j)).getConvId());
					h.put("description", ((ConversionDto) convs.get(j))
							.getDescription());
					h.put("content_type_out", ((ConversionDto) convs.get(j))
							.getContentType());
					h.put("result_type", ((ConversionDto) convs.get(j))
							.getResultType());
					h.put("xml_schema", schemaUrl);
					v.add(h);
				}

			}
		}
		// retriving handocoded transformations
		try {
			Vector vDb = convTypeDao.listConversions(schema);
			for (int i = 0; i < vDb.size(); i++) {
				v.add(vDb.get(i));
			}

		} catch (Exception e) {
			_logger.error("Error getting data from the DB", e);
			throw new GDEMException("Error getting data from the DB "
					+ e.toString(), e);
		}
		return v;		
	}
}
