<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
<div style="width:100%;">

		<tiles:insert definition="ConverterTabs">
			<tiles:put name="selectedTab" value="excel2xml" />
		</tiles:insert>
				
		<ed:breadcrumbs-push label="Spreadsheet to XML" level="1" />
		<h1><bean:message key="label.conversion.excel2xml.heading"/></h1> 
	
		<%-- include Error display --%>
		<tiles:insert definition="Error" />
		
		<html:form action="/excel2XmlConversion" method="post" >
		<table class="datatable">
			<tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.url"/>
			      </th>
			</tr>
			<tr>
				<td>
					<label for="inpUrl"><bean:message key="label.conversion.insertExcelUrl"/></label>
			    </td>
			</tr>
			<tr>
				<td>
			        <html:text property="url" style="width: 30em;" styleId="inpUrl" />
			    </td>
			</tr>
			<tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.sheets"/>
			      </th>
			</tr>
			<tr>
				<td>
					<bean:message key="label.conversion.excel.format"/>
			    </td>
			</tr>
			<tr>
				<td>
					<input type="radio" name="split" id="split1" value="all" onclick="sheet_name.disabled=true" checked="checked"/><label for="split1"><bean:message key="label.conversion.excel.allsheets"/></label>
			    </td>
			</tr>
			<tr>
				<td>
					<input type="radio" name="split" id="split2" value="split" onclick="sheet_name.disabled=false"/><label for="split2"><bean:message key="label.conversion.excel.sheetname"/></label>
			        <html:text property="sheet" onfocus="split[1].checked=true"/>
			    </td>
			</tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			    <tr>
			      <td align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.conversion.convert"/>
			        </html:submit>		        
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
				<tr>
					<td><bean:message key="label.conversion.excel.warning"/>
					</td>
				</tr>
		</table>
	  <!--/fieldset-->
	</html:form>
</div>