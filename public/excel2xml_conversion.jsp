<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.HashMap, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, java.util.Vector" %>



<ed:breadcrumbs-push label="MS Excel to XML" level="1" />
<tiles:insert definition="TmpHeader"/>



<%@ include file="menu.jsp" %>

<div id="workarea">


		<div id="tabbedmenu">
			<ul>


				<li><a onclick="return submitTab('do/listConvForm');" style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.converters"/>" href="<bean:write name="webRoot" />/do/listConvForm"><bean:message key="label.conversion.converters"/></a></li>
				<li class="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.excel2xml"/>" href="<bean:write name="webRoot" />/excel2xml_conversion.jsp"><bean:message key="label.conversion.excel2xml"/></span></li>



				
			</ul>
		</div>
	
			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
			%>
  	  
	  <h1>MS Excel to XML conversion</h1>

	  <br/>	

		<form name="Excel2XML"	action="<%=Names.TEST_CONVERSION_SERVLET%>"	method="post">
				
			<input type="radio" name="split" value="all" onclick="sheet_name.disabled=true" checked="checked">Convert all Excel sheets</input>
			<br/>
			<input type="radio" name="split" value="split" onclick="sheet_name.disabled=false">Convert only one Excel sheet. Insert the sheet name:</input>
			<input type="text" id="sheet" name="sheet_name" onfocus="split[1].checked=true"/>
			<br/><br/>
			<table cellspacing="0">
				<tr><td colspan ="2">Insert the url of source MS Excel file</td></tr>
				<tr>
					<td align="right" style="padding-right:5">
						<label for="excelurlfield">URL of Excel File</label>
					</td>
					<td align="left">
						<input type="text" class="textfield" name="url" size="53" id="excelurlfield" />
					</td>
				</tr>

				<tr><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="Convert" type="submit" class="button" value="Convert" />&#160;&#160;
					</td>
				</tr>
			</table>
			<br/>
			<div>NB! The MS Excel file should be generated from Data Dictionary and DO_NOT_DELETE_THIS_SHEET should be available with original data.</div>
			<input type="hidden" name="format" value="<%=Names.EXCEL2XML_CONV_PARAM%>"/>
			<input type="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</form>	

		 
		</div>
<tiles:insert definition="TmpFooter"/>