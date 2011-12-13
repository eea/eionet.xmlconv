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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.access;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

import eionet.gdem.conversion.datadict.DDElement;
import eionet.gdem.conversion.datadict.DDTable;
import eionet.gdem.utils.Utils;

/**
 * The class tests, if it is possible to convert CDDA MS access file into XML format using Jackcess library
 * 
 * @author Enriko Käsper, Tieto Estonia MdbConversionTest
 */

public class MdbConversionTest {

    Database db = null;

    public static void main(String[] args) {
        MdbConversionTest test = new MdbConversionTest();
        // test.readData();
        test.readData2();
    }

    public void readData() {
        try {
            db = Database.open(new File("C:\\Projects\\xmlconv\\test\\resources\\CDDA-test.mdb"), true);
            System.out.println(db.getTableNames());
            convertDataToXml();
        } catch (Exception e) {
            // FIXME Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    // FIXME Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public void readData2() {
        try {
            db = Database.open(new File("C:\\Projects\\xmlconv\\tmp\\test.mdb"), true);
            System.out.println(db.getTableNames());
            Table mTable = db.getTable("tblTest");
            Map<String, Object> rowMap = null;
            while ((rowMap = mTable.getNextRow()) != null) {
                for (Column col : mTable.getColumns()) {
                    String strValue = "";
                    Object value = rowMap.get(col.getName());
                    if (value != null) {
                        strValue = correctValueFormating(value, col);// correct
                                                                     // formatting
                    }
                    System.out.println(col.getName() + "-" + strValue);
                }
            }
        } catch (Exception e) {
            // FIXME Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    // FIXME Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public void convertDataToXml() throws Exception {
        ConversionTableMetadata metadata = getConversionMetadata();
        System.out.println(metadata);
        writeXML(metadata);
    }

    public void writeXML(ConversionTableMetadata metadata) throws Exception {

        String folder = "C:\\Projects\\xmlconv\\tmp";

        for (DDTable tbl : metadata.getTbl()) {

            if (tbl.getElms() == null || tbl.getElms().size() == 0) {
                throw new Exception("No elements in validation metadata of table " + tbl.getTblIdf());
            }
            if (db.getTable(tbl.getTblIdf()) == null) {
                throw new Exception("Table " + tbl.getTblIdf() + " is missing!");
            }
            BufferedWriter writer = null;
            try {

                writer =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folder, tbl.getTblIdf()
                                + "-java.xml")), "UTF8"));
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");

                // print table start tag
                String tblStartTag =
                        "<" + metadata.getDstNsID() + ":" + tbl.getTblIdf()
                                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
                tblStartTag =
                        tblStartTag + "xmlns:" + metadata.getDstNsID() + "=\"" + metadata.getDstNsURL() + "\" xmlns:"
                                + tbl.getTblNsID() + "=\"" + tbl.getTblNsURL() + "\" xsi:schemaLocation=\""
                                + metadata.getDstNsURL() + " " + tbl.getTblSchemaURL() + "\">\r\n";

                // printLine tblStartTag
                writer.write(tblStartTag);

                // prepare Row tag
                String rowTag = metadata.getDstNsID() + ":Row";

                // loop through the data rows of this table
                Table mdbTable = db.getTable(tbl.getTblIdf());
                System.out.println("write table " + mdbTable.getName() + "; records: " + mdbTable.getRowCount());

                Map<String, Object> rowMap = null;
                while ((rowMap = mdbTable.getNextRow()) != null) {
                    // print start row
                    writer.write("\t" + "<" + rowTag + " status=\"new\">" + "\r\n");

                    // for each element in this table's validation metadata, get
                    // its value in the data table
                    for (DDElement elem : tbl.getElms()) {
                        Column col = mdbTable.getColumn(elem.getElmIdf());

                        if (col == null) {
                            throw new Exception("Table " + tbl.getTblIdf() + " is missing column " + elem.getElmIdf());
                        }
                        String strValue = "";
                        Object value = rowMap.get(elem.getElmIdf());
                        if (value != null) {
                            strValue = correctValueFormating(value, col);// correct
                                                                         // formatting
                        }
                        String elmTag = tbl.getTblNsID() + ":" + elem.getElmIdf();
                        writer.write("\t\t" + "<" + elmTag + ">" + Utils.escapeXML(strValue) + "</" + elmTag + ">\r\n");
                    }
                    writer.write("\t" + "</" + rowTag + ">" + "\r\n");
                }
                // print table end tag
                writer.write("</" + metadata.getDstNsID() + ":" + tbl.getTblIdf() + ">\r\n");

            } catch (IOException e) {
                throw e;
            } finally {
                writer.close();
            }
            // next table
        }
    }

    public ConversionTableMetadata getConversionMetadata() throws IOException {

        ConversionTableMetadata result = new ConversionTableMetadata();
        DDTable tbl = null;

        Table mTable = db.getTable("VALIDATION_METADATA_DO_NOT_MODIFY");

        Map<String, Object> rowMap = null;
        while ((rowMap = mTable.getNextRow()) != null) {

            if (Utils.isNullStr(result.getDstIdf())) {
                result.setDstIdf((String) rowMap.get("DstIdf"));
                result.setDstNr((String) rowMap.get("DstNr"));
                result.setDstNsID((String) rowMap.get("DstNsID"));
                result.setDstNsURL((String) rowMap.get("DstNsURL"));
                result.setDstSchemaURL((String) rowMap.get("DstSchemaURL"));
                result.setDstSchemaLocation((String) rowMap.get("DstSchemaLocation"));
                result.setDstsNsID((String) rowMap.get("DstsNsID"));
                result.setDstsNsURL((String) rowMap.get("DstsNsURL"));
            }
            if (!(Boolean) rowMap.get("SkipTbl")) {
                String tblId = (String) rowMap.get("TblIdf");

                if (tbl == null) {
                    tbl = new DDTable();
                } else {
                    if (!tbl.getTblIdf().equals(tblId)) {
                        result.getTbl().add(tbl);
                        tbl = new DDTable();
                    }
                }

                if (Utils.isNullStr(tbl.getTblIdf())) {
                    tbl.setTblIdf(tblId);
                    tbl.setTblNr((String) rowMap.get("TblNr"));
                    tbl.setTblNsID((String) rowMap.get("TblNsID"));
                    tbl.setTblNsURL((String) rowMap.get("TblNsURL"));
                    tbl.setTblSchemaURL((String) rowMap.get("TblSchemaURL"));
                    tbl.setElms(new ArrayList<DDElement>());
                    result.setTblsNamespaces(result.getTblsNamespaces().concat(
                            " xmlns:" + tbl.getTblNsID() + "=\"" + tbl.getTblNsURL() + "\""));
                }
                tbl.getElms().add(new DDElement((String) rowMap.get("ElmIdf")));
            }
        }
        result.getTbl().add(tbl);

        return result;
    }

    public String correctValueFormating(Object value, Column col) {

        String result = "";
        if (value == null)
            return result;
        try {
            switch (col.getType()) {
                case DOUBLE:
                    return (value.toString()).endsWith(".0") ? Integer.toString(((Double) value).intValue()) : value.toString();
                case BOOLEAN:
                    return ((Boolean) value) ? "True" : "False"; // true or false -
                                                                 // CDDA specific
                case SHORT_DATE_TIME:
                    // System.out.println("TIMESTAMP " + (Date)value);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    return formatter.format((Date) value);
                default:
                    return value.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        // Select Case intDataType
        // Case adBoolean '11
        // If varValue = True Then
        // CorrectValueFormating = "True"
        // ElseIf varValue = False Then
        // CorrectValueFormating = "False"
        // Else
        // CorrectValueFormating = ""
        // End If
        // Case adDate '7
        // CorrectValueFormating = CStr(Format(varValue, "YYYY-MM-DD"))
        // Case adDBDate '133
        // CorrectValueFormating = CStr(Format(varValue, "YYYY-MM-DD"))
        // Case adDBTime '134
        // CorrectValueFormating = CStr(Format(varValue, "hh:mm:ss"))
        // Case adDBTimeStamp '135
        // CorrectValueFormating = CStr(Format(varValue, "YYYY-MM-DD hh:mm:ss"))
        // Case adDecimal, adDouble, adSingle, adNumeric, adVarNumeric '14, 5,
        // 4, 131, 139
        // CorrectValueFormating = CStr(varValue)
        // Do While CorrectValueFormating Like "*,*"
        // CorrectValueFormating = Left(CorrectValueFormating, InStr(1,
        // CorrectValueFormating, ",") - 1) & "." & Mid(CorrectValueFormating,
        // InStr(1, CorrectValueFormating, ",") + 1)
        // Loop
        // Case Else
        // CorrectValueFormating = CStr(varValue)
        // End Select
        //
        // }
        return result;
    }
}
