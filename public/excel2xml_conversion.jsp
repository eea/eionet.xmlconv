<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.HashMap, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, java.util.Vector" %>



<ed:breadcrumbs-push label="Spreadsheet to XML" level="1" />

<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="Spreadsheet to XML conversion"/>
</tiles:insert>


<%@ include file="menu.jsp" %>


		<div id="tabbedmenu">
			<ul>
				<li><a style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.converters"/>" href="<bean:write name="webRoot" />/do/listConvForm"><bean:message key="label.conversion.converters"/></a></li>
				<li id="currenttab"><a style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.excel2xml"/>" href="<bean:write name="webRoot" />/excel2xml_conversion.jsp"><bean:message key="label.conversion.excel2xml"/></a></li>
			</ul>
		</div>
	
	<h1>Spreadsheet to Data Dictionary XML conversion</h1>

	<% if (err!=null){%>
		<div class="error-msg"><%=err%></div>
	<%	} %>

	<form id="Excel2XML" action="<%=Names.TEST_CONVERSION_SERVLET%>" method="post">
		<table class="datatable">
			<tr>
				<th class="scope-col">URL of source file</th>
			</tr>
			<tr>
				<td>
					<label for="excelurlfield">Insert the url of source MS Excel or OpenDocument Spreadsheet file</label>
			    </td>
			</tr>
			<tr>
				<td>
					<input type="text" class="textfield" name="url" size="53" id="excelurlfield" />
			    </td>
			</tr>
			<tr>
				<th scope="col" class="scope-col">Sheets</th>
			</tr>
			<tr>
				<td>Convert MS Excel or OpenDocument Spreadsheets to Data Dictionary XML format.</td>
			</tr>
			<tr>
				<td>
					<input type="radio" name="split" id="split1" value="all" onclick="sheet_name.disabled=true" checked="checked"/><label for="split1">Convert all sheets</label>
			    </td>
			</tr>
			<tr>
				<td>
					<input type="radio" name="split" id="split2" value="split" onclick="sheet_name.disabled=false"/><label for="split2">Convert only one sheet. Insert the sheet name:</label>
					<input type="text" id="sheet" name="sheet_name" onfocus="split[1].checked=true"/>
			    </td>
			</tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			    <tr>
			      <td align="center">
						<input name="Convert" type="submit" class="button" value="Convert" />
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
				<tr>
					<td>NB! MS Excel and OpenDocument spreadsheet files should be generated from Data Dictionary. DO_NOT_DELETE_THIS_SHEET sheet should be available with original data in MS Excel file. OpenDocument should have DataDicitonary data (XML Schemas) in user defiend properties.
					</td>
				</tr>
		</table>
		<div>
			<input type="hidden" name="format" value="<%=Names.EXCEL2XML_CONV_PARAM%>"/>
			<input type="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</div>
	</form>	
 
<tiles:insert definition="TmpFooter"/>