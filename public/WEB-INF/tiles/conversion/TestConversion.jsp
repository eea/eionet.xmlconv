<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>


	<div id="tabbedmenu">
		<ul>
			<li id="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.tab.converters"/>"><bean:message key="label.conversion.tab.converters"/></span></li>
			<li><a style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.tab.crconversion"/>" href="<bean:write name="webRoot" />/do/crConversionForm"><bean:message key="label.conversion.tab.crconversion"/></a></li>
			<li><a style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.tab.excel2xml"/>" href="<bean:write name="webRoot" />/do/excel2XmlConversionForm"><bean:message key="label.conversion.tab.excel2xml"/></a></li>
			<li><a style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.tab.validate"/>" href="<bean:write name="webRoot" />/do/crConversionForm"><bean:message key="label.conversion.tab.validate"/></a></li>
		</ul>
	</div>


	<ed:breadcrumbs-push label="Test conversion" level="1" />
	<h1><bean:message key="label.conversion.testconversion"/></h1>

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

		<logic:iterate id="schema" name="conversion.schemas" scope="session" type="Schema">
			<html:form action="/testConversion" method="post" >
				  <table cellpadding="0" cellspacing="0" border="0" class="datatable">
				    <tr>
					 <th scope="col" class="scope-col">
						<bean:message key="label.conversion.url"/>
				      </th>
				    </tr>
				    <tr>
				      <td><bean:message key="label.conversion.selectSource"/></td>
				    </tr>
				    <tr>
				      <td>
					        <html:text property="url" style="width: 30em;"/>
				      </td>
				    </tr>
				    <tr>
			      		<bean:size name="schema" id="countfiles" property="cdrfiles"/>
				      	<logic:greaterThan name="countfiles" value="0">
					      <td>
					        <select name="cdrFile"  size="10" >
								<option selected="selected" value="">
									--
								</option>
								<logic:iterate id="cdrfile" name="schema" scope="page"  property="cdrfiles" type="CdrFileDto">
										<option value="<bean:write name="cdrfile" property="url" />">
											<bean:write name="cdrfile" property="country" />&nbsp;-&nbsp;
											<bean:write name="cdrfile" property="title" />
											<logic:notEqual name="cdrfile" property="year" value="0">
												&nbsp;-&nbsp;(<bean:write name="cdrfile" property="year" />
												<logic:notEqual name="cdrfile" property="endyear" value="0">
													&nbsp;-&nbsp;<bean:write name="cdrfile" property="endyear" />
												</logic:notEqual>
												<logic:equal name="cdrfile" property="endyear" value="0">
													<logic:notEqual name="cdrfile" property="partofyear" value="">
														&nbsp;-&nbsp;<bean:write name="cdrfile" property="partofyear" />
													</logic:notEqual>
												</logic:equal>)
											</logic:notEqual>
										</option>
								</logic:iterate>
						    </select>
						  </td>
						</logic:greaterThan>
				      	<logic:equal name="countfiles" value="0">
					      <td>
						        <bean:message key="label.conversion.noCdrFiles"/>
					      </td>
						</logic:equal>
					</tr>
				    <tr>
					 <th scope="col" class="scope-col">
					        <bean:message key="label.conversion.xmlSchema"/>
				      </th>
					</tr>
					<tr>
				      <td>
							<a  href="<bean:write name="schema" property="schema" />" title="<bean:write name="schema" property="schema" />">
									<bean:write name="schema" property="schema" />
							</a>
							<html:hidden name="ConversionForm" property="schemaUrl"/>
				      </td>
				    </tr>
				    <tr>
					 <th scope="col" class="scope-col">
						<bean:message key="label.conversion.selectConversion"/>
				      </th>
				    </tr>
					
				    <bean:define id="idConv" name="ConversionForm" property="conversionId"  type="java.lang.String" scope="request"/>

					    <tr>
					      <td align="left">
				    <logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">
								<logic:equal name="stylesheet" property="convId" value="<%=idConv%>">
									<input type="radio" checked="checked" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />" value="<bean:write name="stylesheet" property="convId" />" />
								</logic:equal>
								<logic:notEqual name="stylesheet" property="convId" value="<%=idConv%>">
									<input type="radio" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />"  value="<bean:write name="stylesheet" property="convId" />" />
								</logic:notEqual>
								<label for="r_<bean:write name="stylesheet" property="convId" />"><bean:write name="stylesheet" property="type" />
								&nbsp;-&nbsp;<bean:write name="stylesheet" property="xsl_descr" /></label><br/>
					</logic:iterate>
					      </td>
					    </tr>
				    <tr>
				      <td>&nbsp;</td>
				    </tr>
				    <tr>
				      <td align="center">
				      		<bean:size name="schema" id="count" property="stylesheets"/>
					      	<logic:greaterThan name="count" value="0">
						        <html:submit styleClass="button">
						        	<bean:message key="label.conversion.convert"/>
			        			</html:submit>		        
					        </logic:greaterThan>
					        <logic:equal name="count" value="0">
					        <p style="color: red; font-weight: bold;"><bean:message key="label.conversion.noconversion"/></p>
					        </logic:equal>

				      </td>
				    </tr>
				    <tr>
				      <td>&nbsp;</td>
				    </tr>
				  </table>
				</html:form>
				</logic:iterate>
				<div class="visualClear">&nbsp;</div>
				<logic:present name="conversion.valid" scope="request">
					<div style="width: 97%">
						<table class="datatable" align="center" width="100%">
							<col style="width:7%"/>
							<col style="width:7%"/>
							<col style="width:7%"/>
							<col style="width:79%"/>
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
					</div>
				</logic:present>

