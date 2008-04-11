<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
	<div style="width:100%;">
		<tiles:insert definition="ConverterTabs">
			<tiles:put name="selectedTab" value="searchXML" />
		</tiles:insert>
				
		<ed:breadcrumbs-push label="Search CR for XML files" level="1" />
		<h1><bean:message key="label.conversion.crconversion.title"/></h1> 
	
		<%-- include Error display --%>
		<tiles:insert definition="Error" />

			<html:form action="/searchCR" method="get">
			<table class="datatable">
				<tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.xmlSchema"/>
			      </th>
			    </tr>
			    <tr>
			      <td>
			      	<c:set var="selSchema"><bean:write name="ConversionForm" property="schemaUrl" scope="session" /></c:set>
			      	
			        <select name="schemaUrl"  size="10">
						<option value="">
							--
						</option>		        
						<logic:iterate id="schema" name="conversion.schemas" scope="session"  type="Schema">
							
					         <logic:equal name="schema" property="schema" value="${selSchema}" >
					         	<c:set var="selected" value="selected=\"selected\""/>
					         </logic:equal>
					         <logic:notEqual name="schema" property="schema" value="${selSchema}" >
			         			<c:set var="selected" value=""/>
					        </logic:notEqual>

							<option value="<bean:write name="schema" property="schema" />" ${selected}>
								<bean:write name="schema" property="schema" />
								<logic:notEqual name="schema" property="table" value="">
									&nbsp;-&nbsp;
									<bean:write name="schema" property="table" />&nbsp;(
									<bean:write name="schema" property="dataset" /> - 
									<bean:write name="schema" property="datasetReleased" format="<%= Properties.dateFormatPattern%>" />)
								</logic:notEqual>										
							</option>
						</logic:iterate>
			        </select>
			      </td>
			    </tr>
			    <tr>
			      <td align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.conversion.searchXML"/>
			        </html:submit>
			        <html:hidden property="conversionId"/>
			      </td>
			    </tr>
			   </table>
			</html:form>
			
			<!--  Show XML files -->
			<logic:present name="ConversionForm" property="schema">
			<bean:define id="schema"  name="ConversionForm" property="schema" />
			<html:form action="/testConversion" method="post" >
			<table class="datatable">
				<tr>
				 <th scope="col" class="scope-col">
			        <bean:message key="label.conversion.CRxmlfiles"/>
			      </th>
			    </tr>
			      		<bean:size name="schema" id="countfiles" property="crfiles"/>
				      	<bean:define id="selUrl" name="converted.url" scope="session" type="String" />
				      	<logic:greaterThan name="countfiles" value="0">
				      	<tr>
					      <td>

					        <select name="url"  size="10" >
								<option value="">
									--
								</option>
								<logic:iterate id="crfile" name="schema" scope="page"  property="crfiles" type="CdrFileDto">
							         <logic:equal name="crfile" property="url" value="<%=selUrl %>" >
					         			<c:set var="selected" value="selected=\"selected\""/>
					         		</logic:equal>
					         		<logic:notEqual name="crfile" property="url" value="<%=selUrl %>" >
			         					<c:set var="selected" value=""/>
					        		</logic:notEqual>

										<option value="<bean:write name="crfile" property="url" />" ${selected } >
											<bean:write name="crfile" property="country" />&nbsp;-&nbsp;
											<bean:write name="crfile" property="title" />
											<logic:notEqual name="crfile" property="year" value="0">
												&nbsp;-&nbsp;(<bean:write name="crfile" property="year" />
												<logic:notEqual name="crfile" property="endyear" value="0">
													&nbsp;-&nbsp;<bean:write name="crfile" property="endyear" />
												</logic:notEqual>
												<logic:equal name="crfile" property="endyear" value="0">
													<logic:notEqual name="crfile" property="partofyear" value="">
														&nbsp;-&nbsp;<bean:write name="crfile" property="partofyear" />
													</logic:notEqual>
												</logic:equal>)
											</logic:notEqual>
										</option>
								</logic:iterate>
						    </select>
						  </td>
						 </tr>
						</logic:greaterThan>
				     <logic:equal name="countfiles" value="0">
				     	<tr> 	
					      <td>
						        <bean:message key="label.conversion.noCRFiles"/>
					      </td>
					     </tr>
			    		<tr>
				      		<td>
						        <input type="text" name="url" style="width: 30em;" value="<%=selUrl %>" ></input>
				      		</td>
			    		</tr>
					</logic:equal>
					<tr>
				      <td>
							<html:hidden name="ConversionForm" property="schemaUrl"/>
				      </td>
				    </tr>
				    <tr>
					 <th scope="col" class="scope-col">
						<bean:message key="label.conversion.selectConversion"/>
				      </th>
				    </tr>
					
			      	<bean:define id="idConv" name="converted.conversionId" scope="session" type="String" />
			      	<logic:empty name="idConv">
			      		<bean:define id="idConv" name="ConversionForm" property="conversionId" scope="session" type="String" />
			      	</logic:empty>
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
			</table>
		</html:form>
	</logic:present>
</div>