/**
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
 * The Original Code is " GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 27.04.2006
 */
package eionet.gdem.conversion.odf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.catcode.odf.ODFMetaFileAnalyzer;
import com.catcode.odf.OpenDocumentMetadata;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.DDXMLConverter;
import eionet.gdem.conversion.SourceReaderIF;
import eionet.gdem.conversion.excel.reader.DDXmlElement;
import eionet.gdem.conversion.excel.reader.DD_XMLInstance;
import eionet.gdem.utils.Streams;
import eionet.gdem.utils.Utils;

/**
 * The class is responsible for reading OpenDocument Spreadsheets
 *
 * @author Enriko Käsper
 */

public class OdsReader implements SourceReaderIF {
	private OpenDocumentMetadata metadata = null;

	private OpenDocumentSpreadsheet spreadsheet = null;

	public final static String SCHEMA_ATTR_NAME = "schema-url";

	public final static String TBL_SCHEMAS_ATTR_NAME = "table-schema-urls";

	private final static String TBL_SEPARATOR = ";";

	private final static String TBL_PROPERTIES_SEPARATOR = ",";

	public final static String TABLE_NAME = "tableName=";

	public final static String TABLE_SCHEMA_URL = "tableSchemaURL=";

	public String getXMLSchema() {
		String ret = null;
		Hashtable usermetadata = metadata.getUserDefined();
		if (usermetadata.containsKey(SCHEMA_ATTR_NAME)) {
			ret = (String) usermetadata.get(SCHEMA_ATTR_NAME);
		}
		return ret;
	}

	/*
	 * Intializes OdsReader. Reades ODS file into Java objects
	 *
	 * @see eionet.gdem.conversion.SourceReaderIF#initReader(java.io.InputStream)
	 *      @param InputStream input: Source ods file
	 */
	public void initReader(InputStream input) throws GDEMException {

		ByteArrayOutputStream out_stream = new ByteArrayOutputStream();
		try {
			Streams.drain(input, out_stream);

			// ODF analyzer closes the stream after parsing content. We need to
			// keep the stream availabl in Outputstream.
			ODFSpreadsheetAnalyzer odfSpreadsheetAnalyzer = new ODFSpreadsheetAnalyzer();
			spreadsheet = odfSpreadsheetAnalyzer
					.analyzeZip(new ByteArrayInputStream(out_stream
							.toByteArray()));

			ODFMetaFileAnalyzer odfMetaAnalyzer = new ODFMetaFileAnalyzer();
			metadata = odfMetaAnalyzer.analyzeZip(new ByteArrayInputStream(
					out_stream.toByteArray()));

		} catch (IOException e) {
			// throw e;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
				}
			}
			if (out_stream != null) {
				try {
					out_stream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see eionet.gdem.conversion.SourceReaderIF#writeContentToInstance(eionet.gdem.conversion.excel.DD_XMLInstance)
	 */
	public void writeContentToInstance(DD_XMLInstance instance)
			throws GDEMException {
		List<DDXmlElement> tables = instance.getTables();
		if (tables == null)
			throw new GDEMException(
					"could not find tables from DD instance file");
		if (spreadsheet == null)
			return;

		for (int i = 0; i < tables.size(); i++) {
			DDXmlElement table = tables.get(i);
			String tblLocalName = table.getLocalName();
			String tblName = table.getName();
			String tblAttrs = table.getAttributes();

			List list_tabledata = spreadsheet.getTableData(tblLocalName);
			List list_metatabledata = getMetaTableData(tblLocalName);

			if (list_tabledata == null)
				continue;
			List<DDXmlElement> elements = instance.getTblElements(tblName);

			setColumnMappings(spreadsheet.getTableHeader(tblLocalName),
					elements, true);

			if (list_metatabledata != null) {
				setColumnMappings(getMetaTableHeader(tblLocalName), elements,
						false);
			}

			instance.writeTableStart(tblName, tblAttrs);
			instance.setCurRow(tblName);

			// read data
			// there are no data rows in the Excel file. We create empty table
			// first_row = (first_row == last_row) ? last_row : first_row+1;
			boolean emptySheet = spreadsheet.isEmptySheet(tblLocalName);

			for (int j = 0; j < list_tabledata.size() || emptySheet; j++) {
				ArrayList list_row = (ArrayList) list_tabledata.get(j);
				ArrayList list_metarow = (list_metatabledata != null && list_metatabledata
						.size() > j) ? (ArrayList) list_metatabledata.get(j)
						: null;

				// don't convert empty rows.
				if (Utils.isEmptyArrayList(list_row) && !emptySheet)
					continue;

				instance.writeRowStart();
				for (int k = 0; k < elements.size(); k++) {
					DDXmlElement elem = elements.get(k);
					String elem_name = elem.getName();
					String elem_attributes = elem.getAttributes();
					int colIndex = elem.getColIndex();
					boolean isMainTable = elem.isMainTable();

					String data = "";
					if (colIndex > -1 && !emptySheet) {
						data = (isMainTable) ? getListStringValue(
								list_row, colIndex)
								: getListStringValue(list_metarow, colIndex);
					}
					instance.writeElement(elem_name, elem_attributes, data);

				}
				instance.writeRowEnd();
				if (emptySheet)
					break;
			}
			instance.writeTableEnd(tblName);
		}

	}

	/*
	 * Returns the name of the first table
	 *
	 */
	public String getFirstSheetName() {

		if (spreadsheet == null)
			return null;

		return spreadsheet.getTableName(0);
	}

	public Hashtable getSheetSchemas() {
		Hashtable hash = null;
		Hashtable usermetadata = metadata.getUserDefined();
		if (usermetadata.containsKey(TBL_SCHEMAS_ATTR_NAME)) {
			String ret = (String) usermetadata.get(TBL_SCHEMAS_ATTR_NAME);
			if (Utils.isNullStr(ret))
				return hash;

			StringTokenizer st_tbl = new StringTokenizer(ret, TBL_SEPARATOR);
			if (st_tbl.countTokens() == 0)
				return hash;
			hash = new Hashtable();
			while (st_tbl.hasMoreTokens()) {
				String tbl = st_tbl.nextToken();
				StringTokenizer st_tbl_props = new StringTokenizer(tbl,
						TBL_PROPERTIES_SEPARATOR);
				if (st_tbl_props.countTokens() < 2)
					continue;

				String tbl_name = null;
				String tbl_schema = null;

				while (st_tbl_props.hasMoreTokens()) {
					String token = st_tbl_props.nextToken();
					if (token.startsWith(TABLE_NAME))
						tbl_name = token.substring(TABLE_NAME.length());
					if (token.startsWith(TABLE_SCHEMA_URL))
						tbl_schema = token.substring(TABLE_SCHEMA_URL.length());
				}
				if (Utils.isNullStr(tbl_name) || Utils.isNullStr(tbl_schema))
					continue;

				// check if table exists
				if (spreadsheet != null) {
					if (!spreadsheet.tableExists(tbl_name))
						continue;
				}
				if (!hash.containsKey(tbl_name)) {
					hash.put(tbl_name, tbl_schema);
				}
			}

		}
		return hash;
	}

	public boolean isEmptySheet(String sheet_name) {
		if (spreadsheet == null)
			return true;

		return spreadsheet.isEmptySheet(sheet_name);
	}

	/*
	 * DD can generate additional "-meta" sheets with GIS elements for one DD
	 * table. In XML these should be handled as 1 table. This is method for
	 * finding these kind of sheets and parsing these in parallel with the main
	 * sheet
	 */
	private List getMetaTableData(String mainSheetName) {
		return spreadsheet.getTableData(mainSheetName
				+ DDXMLConverter.META_SHEET_NAME_ODS);
	}

	private List getMetaTableHeader(String mainSheetName) {
		return spreadsheet.getTableHeader(mainSheetName
				+ DDXMLConverter.META_SHEET_NAME_ODS);
	}

	/*
	 * Set mappings in case user has changed columns ordering
	 */
	private void setColumnMappings(List<String> listHeaderRow, List<DDXmlElement> elements,
			boolean isMainTable) {
		// read column header

		for (int j = 0; j < elements.size(); j++) {
			DDXmlElement elem = elements.get(j);
			String elemLocalName = elem.getLocalName();
			int k = listHeaderRow.indexOf(elemLocalName);

			if (k > -1) {
				elem.setColIndex(k);
				elem.setMainTable(isMainTable);
			}

		}

	}

	private String getListStringValue(List<String> list, Integer colIdx) {

		if (list == null)
			return "";
		if (list.size() < colIdx)
			return "";
		String data = list.get(colIdx);
		if (data == null)
			return "";

		return data;
	}

}