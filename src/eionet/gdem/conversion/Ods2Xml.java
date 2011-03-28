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

package eionet.gdem.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import eionet.gdem.conversion.odf.OpenDocumentUtils;


/**
 * This class is creating handlers for creating XML file from OpenDocument
 * Spreadsheet format called from ConversionService
 *
 * @author Enriko Käsper
 */

public class Ods2Xml extends DDXMLConverter {

    private final static String FORMAT_NAME = "OpenDocument Spreadsheet";

    public SourceReaderIF getSourceReader(){
        return OpenDocumentUtils.getSpreadhseetReader();
    }
    public String getSourceFormatName(){
        return FORMAT_NAME;
    }



    public static void main(String[] args){


        ConversionServiceIF conv = new ConversionService();
        try{
            //conv.convertDD_XML("http://localhost:8080/gdem/test.xls");
            Vector v = conv.convertDD_XML_split("http://cdrtest.eionet.europa.eu/copy_of_ee/eea/colqrajqw/envrorfcq/Rivers_testdata_EU_137.ods", null);
            System.out.println(v.toString());
        }
        catch(Exception e){
            System.out.println(e.toString());
        }

        if(true)
            return;

          FileInputStream fis = null;
          FileOutputStream fout = null;
        //String excelFile = "E:/Projects/gdem/public/test.xls";
          //String excelFile = "E:/Projects/gdem/tmp/Summer_ozone.xls";
        //String excelFile = E\\Projects\\gdem\\exelToXML\\Groundwater_GG_CCxxx.xls";
          String odsFile = "E:/Projects/xmlconv/tmp/Rivers_testdata_EU_137.ods";
        String outFile = "E:/Projects/xmlconv/tmp/Rivers_testdata_EU_137.ods.xml";
        try{
          //OdsReader reader= new OdsReader();
          Ods2Xml processor = new Ods2Xml();
          //processor.convertDD_XML_split(excelFile,outFile);
          File ods = new File(odsFile);
          fis = new FileInputStream(ods);

          File xml = new File(outFile);
          fout = new FileOutputStream(xml);

          //FileInputStream fis = new FileInputStream()
          processor.convertDD_XML_split(fis, fout, "Stations");
          //reader.initReader(fis);
          //String schema = reader.getXMLSchema();
          //Hashtable h = reader.getSheetSchemas();
          //System.out.println(schema);
          //System.out.println(h.toString());
          //reader.convertDD_XML_split(odsFile, null);
        }
        catch(Exception e){
          System.out.println(e.toString());
        }
        finally{
            if (fis!=null){
                try{ fis.close();}catch(Exception e){}
            }
            if (fout!=null){
                try{ fout.close();}catch(Exception e){}
            }
        }
      }
}
