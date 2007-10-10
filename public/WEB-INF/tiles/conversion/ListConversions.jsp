<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
<div style="width:100%;">
	<div id="tabbedmenu">
		<ul>
			<li id="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.converters"/>"><bean:message key="label.conversion.converters"/></span></li>
			<li><a style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.excel2xml"/>" href="<bean:write name="webRoot" />/excel2xml_conversion.jsp"><bean:message key="label.conversion.excel2xml"/></a></li>
		</ul>
	</div>
				
		<ed:breadcrumbs-push label="Find conversion" level="1" />
		<h1><bean:message key="label.conversion.find"/></h1> 
	
		<%-- include Error display --%>
		<tiles:insert definition="Error" />

			<html:form action="/testConversionForm" method="get" >
			<!--fieldset><legend><bean:message key="label.conversion.url"/></legend-->
			<table class="datatable">
			    <tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.url"/>
			      </th>
				 </tr>
			    <tr>
			      <td>
			        <bean:message key="label.conversion.insertURL"/>
			      </td>
			    </tr>
				 <tr>
			      <td>
			        <html:text property="url"  style="width: 30em;" />		        
			      </td>
			    </tr>
			    <tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.xmlSchema"/>
			      </th>
			    </tr>
			    <tr>
					<td>
				        <bean:message key="label.conversion.selectSchema"/>
			      </td>
			    </tr>
			    <tr>
			      <td>
			        <select name="schemaUrl"  size="10" >
										<option selected="selected" value="">
											--
										</option>		        
						<logic:iterate id="schema" name="conversion.schemas" scope="session"  type="Schema">
										<option value="<bean:write name="schema" property="schema" />">
											<bean:write name="schema" property="schema" />
											<logic:notEqual name="schema" property="table" value="">
												&nbsp;-&nbsp;
												<bean:write name="schema" property="table" />&nbsp;(
												<bean:write name="schema" property="dataset" />)
											</logic:notEqual>										
										</option>
						</logic:iterate>
			        </select>
			      </td>
			    </tr>
			    <tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.validate"/>
			      </th>
			    </tr>
			    <tr>
			        <td>
						<input type="checkbox" name="validate" id="validatefield"/><label for="validatefield"><bean:message key="label.conversion.validateSchema"/></label>
					</td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			    <tr>
			      <td align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.conversion.list"/>
			        </html:submit>		        
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			  </table>
			  <!--/fieldset-->
			</html:form>
			

</div>