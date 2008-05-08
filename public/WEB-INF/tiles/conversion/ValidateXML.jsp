<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ page import="eionet.gdem.utils.Utils,java.util.Date" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
	<div style="width:100%;">
				
		<ed:breadcrumbs-push label="Validate XML" level="1" />
		<h1><bean:message key="label.conversion.validate.title"/></h1> 
	
		<%-- include Error display --%>
		<tiles:insert definition="Error" />
				<logic:present name="conversion.valid" scope="request">
					<bean:size id="countErrors" name="conversion.valid" />

						<logic:equal name="countErrors" value="0">
							<div class="system-msg">
								The file is valid XML (<%=Utils.getDateTime(new Date())%>)
								<logic:notEmpty name="conversion.validatedSchema">
									<p><bean:message key="label.conversion.validatedSchema"/>&#160;
										<a href="<bean:write name="conversion.validatedSchema"/>"><bean:write  name="conversion.validatedSchema"/></a></p> 
								</logic:notEmpty>
							</div>
						</logic:equal>
					<logic:notEqual name="countErrors" value="0">
					<div class="error-msg">
						The file is not valid XML
						<logic:notEmpty name="conversion.validatedSchema">
							<p><bean:message key="label.conversion.validatedSchema"/>&#160;
								<a href="<bean:write name="conversion.validatedSchema"/>"><bean:write  name="conversion.validatedSchema"/></a></p> 
						</logic:notEmpty>
					</div>
					</logic:notEqual>
				</logic:present>


			<html:form action="/validateXML" method="post" >
			<table class="datatable">
			    <tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.url"/>
			      </th>
				 </tr>
			    <tr>
			      <td>
			        <bean:message key="label.conversion.validate.note"/>
			      </td>
			    </tr>
				 <tr>
			      <td>
			        <html:text property="url"  style="width: 40em;" />		        
			      </td>
			    </tr>
				<logic:equal name="ValidationForm" property="showSchemaSelection" value="true">
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
			      	
			        <html:select name="showSchemaSelection" property="schemaUrl"  size="10">
						<html:option value="">--</html:option>
						<html:options collection="conversion.schemas" property="schema" labelProperty="label" />
			        </html:select>
			      </td>
			    </tr>
			   </logic:equal>
			    <tr>
			      <td align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.conversion.validate"/>
			        </html:submit>		        
			      </td>
			</tr>
		</table>
	</html:form>
				<logic:present name="conversion.valid" scope="request">
					<bean:size id="countErrors" name="conversion.valid" />
					<logic:notEqual name="countErrors" value="0">
						<table class="datatable" align="center" width="100%">
							<col style="width:8%"/>
							<col style="width:8%"/>
							<col style="width:8%"/>
							<col/>
							<thead>
								<tr>
									<th scope="col"><span title="Error"><bean:message key="label.table.conversion.type"/></span></th>
									<th scope="col"><span title="PositionLine"><bean:message key="label.table.conversion.line"/></span></th>
									<th scope="col"><span title="PositionCol"><bean:message key="label.table.conversion.col"/></span></th>
									<th scope="col"><span title="Message"><bean:message key="label.table.conversion.message"/></span></th>
								</tr>
							</thead>
							<tbody>
								<logic:iterate indexId="index" id="valid" name="conversion.valid" scope="request" type="ValidateDto">
									<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
										<td>
												<bean:write name="valid" property="type" />
										</td>
										<td>
												<bean:write name="valid" property="line" />
										</td>
										<td>
												<bean:write name="valid" property="column" />
										</td>
										<td>
												<bean:write name="valid" property="description" />
										</td>
									</tr>
								</logic:iterate>
							</tbody>
						</table>
					</logic:notEqual>
				</logic:present>
	</div>