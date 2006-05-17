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
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 28.04.2006
 */
package eionet.gdem.conversion.odf;

import java.util.ArrayList;
import java.util.HashMap;

import eionet.gdem.utils.Utils;

public class OpenDocumentSpreadsheet {

	private String currentTable = null;

	private ArrayList tables = new ArrayList();

	private HashMap tables_data = new HashMap();

	private HashMap tables_headers = new HashMap();

	/*
	 *
	 */
	public void setCurrentTable(String currentTable) {
		this.currentTable = currentTable;
	}

	public String getCurrentTable() {
		return this.currentTable;
	}

	/*
	 * Adds table name to the tables vector
	 */
	public void addTable(String tbl_name) {
		tables.add(tbl_name);
		setCurrentTable(tbl_name);
	}

	/*
	 * Returns tables' list
	 */
	public ArrayList getTables() {
		return tables;
	}

	/*
	 * Returns table name in specified index
	 */
	public String getTableName(int idx) {
		if (idx <= tables.size())
			return (String) tables.get(idx);
		else
			return null;
	}

	/*
	 * Adds header list into headers Map
	 */
	public void addTableHeaderValue(String tbl_name, String value) {
		if (Utils.isNullStr(tbl_name))
			tbl_name = currentTable;
		ArrayList list = null;

		if (tables_headers.containsKey(tbl_name)) {
			list = (ArrayList) tables_headers.get(tbl_name);
			list.add(value);
		} else {
			list = new ArrayList();
			list.add(value);
			tables_headers.put(tbl_name, list);
		}

	}

	/*
	 * Adds data row into data Map
	 */
	public void addTableDataRow(String tbl_name, ArrayList row_list) {
		if (Utils.isNullStr(tbl_name))
			tbl_name = currentTable;
		ArrayList rows_list = null;

		if (tables_data.containsKey(tbl_name)) {
			rows_list = (ArrayList) tables_data.get(tbl_name);
			rows_list.add(row_list);
		} else {
			rows_list = new ArrayList();
			rows_list.add(row_list);
			tables_data.put(tbl_name, rows_list);
		}

	}

	/*
	 * Returns tables' data as ArrayList
	 */
	public ArrayList getTableData(String tbl_name) {
		if (Utils.isNullStr(tbl_name))
			tbl_name = currentTable;

		ArrayList list = null;
		if (tables_data.containsKey(tbl_name))
			list = (ArrayList) tables_data.get(tbl_name);

		return list;
	}

	/*
	 * Gets table header list
	 */
	public ArrayList getTableHeader(String tbl_name) {
		if (Utils.isNullStr(tbl_name))
			tbl_name = currentTable;

		ArrayList list = null;
		if (tables_headers.containsKey(tbl_name))
			list = (ArrayList) tables_headers.get(tbl_name);

		return list;
	}

	/*
	 * Gets table header list
	 */
	public int getTableColCount(String tbl_name) {
		if (Utils.isNullStr(tbl_name))
			tbl_name = currentTable;

		int i = 0;
		if (tables_headers == null)
			return i;

		if (tables_headers.containsKey(tbl_name)) {
			try {
				ArrayList list = (ArrayList) tables_headers.get(tbl_name);
				if (list != null)
					i = list.size();
			} catch (Exception e) {
				// do nothing return 0
			}
		}

		return i;
	}

	/*
	 * Gets table row count
	 */
	public int getTableRowCount(String tbl_name) {

		int i = 0;

		if (Utils.isNullStr(tbl_name))
			tbl_name = currentTable;
		if (tables_data == null)
			return i;

		if (tables_data.containsKey(tbl_name)) {
			try {
				ArrayList list = (ArrayList) tables_data.get(tbl_name);
				if (list != null)
					i = list.size();
			} catch (Exception e) {
				// do nothing return 0
			}
		}

		return i;
	}

	/*
	 * Checks if table exists
	 */
	public boolean tableExists(String tbl_name) {
		if (tables == null)
			return false;
		return tables.contains(tbl_name);

	}

	/*
	 * Checks if sheet contains any data.
	 */
	public boolean isEmptySheet(String tbl_name) {

		if (Utils.isNullStr(tbl_name))
			tbl_name = currentTable;
		// data does not exist
		if (tables_data == null)
			return true;

		// Table does not exist
		if (!tables_data.containsKey(tbl_name))
			return true;

		try {
			ArrayList rows = (ArrayList) tables_data.get(tbl_name);

			// no data rows
			if (rows == null || rows.size() == 0)
				return true;
			for (int n = 0; n < rows.size(); n++) {
				try {
					ArrayList row = (ArrayList) rows.get(n);

					// If row contains any String data, then it is not empty,
					// return false
					if (!Utils.isEmptyArrayList(row))
						return false;
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			return true;
		}

		return true;

	}
}
