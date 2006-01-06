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
 */

package eionet.gdem.conversion.excel;

import org.apache.poi.hssf.usermodel.*;


/**
* This class is mapping styles between xml (fo:style) and sytles defined in poi HSSF
* 
* @author Enriko Käsper
*/
  
public class ExcelStyle implements ExcelStyleIF {

  private String name=null;
  private String family=null;
  private boolean italic=false;
  private short font_weight=HSSFFont.BOLDWEIGHT_NORMAL;
  private short font_size=12;
  private String font_name=null;
  private short text_align=HSSFCellStyle.ALIGN_GENERAL;
  private short workbook_index=-1;
  
  public ExcelStyle() {
  }
  public void setExcelStyle(String name, String family) {
      //These are style unique id's 
      this.name=name;
      this.family=family;
  }

//get methods
  public String getName(){
    return name;
  }
  public String getFamily(){
    return family;
  }
  public boolean getItalic(){
    return italic;
  }
  public short getFontWeight(){
    return font_weight;
  }
  public short getFontSize(){
    return font_size;
  }
  public String getFontName(){
    return font_name;
  }
  public short getTextAlign(){
    return text_align;
  }
  public short getWorkbookIndex(){
    return workbook_index;
  }

// set methods
  public void setItalic(String str_italic){
    if (str_italic==null) return;
    if (str_italic.equalsIgnoreCase("italic")) italic=true;  
  }
  public void setFontWeight(String str_bold){
    if (str_bold==null) return;
    if (str_bold.equalsIgnoreCase("bold")) 
        font_weight=HSSFFont.BOLDWEIGHT_BOLD;
  }
  public void setFontSize(String str_size){
    Short short_size = null;
    if (str_size==null) return;
    if (str_size.endsWith("pt")){
      str_size=str_size.substring(0,str_size.indexOf("pt"));
    }
    try {
      short_size = new Short(str_size);
    }
    catch (Exception e){
      return;
    }
    if (short_size!=null)
      font_size=short_size.shortValue();
  }
  public void setFontName(String str_fontname){
    if (str_fontname==null) return;
    this.font_name=str_fontname;
  }
   public void setTextAlgin(String str_align){
    if (str_align==null) return;
    if (str_align.equalsIgnoreCase("center")) 
        text_align=HSSFCellStyle.ALIGN_CENTER;
    else if (str_align.equalsIgnoreCase("left")) 
        text_align=HSSFCellStyle.ALIGN_LEFT;
    else if (str_align.equalsIgnoreCase("right") || str_align.equalsIgnoreCase("end")) 
        text_align=HSSFCellStyle.ALIGN_RIGHT;
    else if (str_align.equalsIgnoreCase("justify")) 
        text_align=HSSFCellStyle.ALIGN_JUSTIFY;
    else if (str_align.equalsIgnoreCase("left")) 
        text_align=HSSFCellStyle.ALIGN_LEFT;
    else if (str_align.equalsIgnoreCase("start")) 
        text_align=HSSFCellStyle.ALIGN_GENERAL;
    else
        text_align=HSSFCellStyle.ALIGN_GENERAL;
  }
  public boolean equals(ExcelStyleIF style){
  
    String compare_name=  style.getName();
    String compare_family=  style.getFamily();

    if (compare_name==null || compare_family==null) return false;

    if (compare_name.equalsIgnoreCase(this.name) 
            && compare_family.equalsIgnoreCase(this.family))
            return true;

    return false;
  }
  public void setWorkbookIndex(short index){
    this.workbook_index=index;
  }
}
