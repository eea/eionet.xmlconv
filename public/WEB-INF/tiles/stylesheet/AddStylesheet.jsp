<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>


<html:xhtml/>
		<ed:breadcrumbs-push label="Add Stylesheet" level="3" />
		<h1><bean:message key="label.stylesheet.add"/></h1> 

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

		<html:form action="/stylesheetAdd" method="post" enctype="multipart/form-data">
		  <table width="100%" class="datatable">
			<col style="width:16%"/>
			<col style="width:84%"/>
		    <tr>
				<th scope="row" class="scope-row">
			        <bean:message key="label.stylesheet.schema"/>:
				</th>
				<td>
							<logic:present name="schema" scope="request">
					          <input type="text" name="schema" value="<bean:write name="schema" scope="request"/>" style="width:400px" />
					        </logic:present>
					        <logic:notPresent name="schema" scope="request">
					          <input type="text" name="schema" maxlength="255"  style="width:400px" />
					        </logic:notPresent>
				</td>
			</tr>
			    <logic:present name="user">
				    <tr>
	 				  <th scope="row" class="scope-row">
				        <bean:message key="label.stylesheet.selectDDSchema"/>:
				      </th>
				      <td>		    
						      		<select name="xmlSchema"  size="10" onchange="setSchema()" style="width:98%">
													<option selected="selected" value="">
														--
													</option>		        
									<logic:iterate id="schema" name="stylesheet.DDSchemas" scope="session"  type="Schema">
													<option value="<bean:write name="schema" property="schema" />">
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
					    </logic:present>		    
					    <tr>
		 				  <th scope="row" class="scope-row">
					        <bean:message key="label.stylesheet.outputtype"/>:
					      </th>
					      <td>	                         
					        <select name="outputtype" style="width:100px;">
								<logic:iterate id="opt" name="stylesheet.outputtype" scope="session"  property="convTypes" type="ConvType">
									<option value="<bean:write name="opt" property="convType" />">
										<bean:write name="opt" property="convType" />
									</option>
								</logic:iterate>
					        </select>
					      </td>
					    </tr>
					    
					    <logic:present name="schemaInfo" scope="request">
					    <logic:equal name="schemaInfo" property="schemaLang" value="EXCEL">
					    <tr>
		 				  <th scope="row" class="scope-row">
					        <bean:message key="label.stylesheet.dependsOn"/>:
					      </th>
					      <td>	                         
					        <select name="dependsOn">
					        	<option value="" selected="selected">--</option>
					        	<logic:iterate id="st" scope="request" name="existingStylesheets">
					        		<option value="<bean:write name="st" property="convId" />">
					        			<bean:write name="st" property="xslFileName" />
					        		</option>
					        	</logic:iterate>
					        </select>
					      </td>
					    </tr>
					    </logic:equal>
					    </logic:present>
					    
					    <tr>
		 				  <th scope="row" class="scope-row">
					      	<bean:message key="label.stylesheet.description"/>:
					      </th>
					      <td>
					        <html:textarea property="description"  rows="3" cols="30" style="width:400px"/>
					      </td>
					    </tr>
					    <tr>
		 				  <th scope="row" class="scope-row">
					        <bean:message key="label.stylesheet.xslfile"/>:
					      </th>
					      <td>
					        <html:file property="xslfile"  style="width:400px" size="64"/>
					      </td>
					    </tr>
					    <tr>
					      <td colspan="2" align="center">
					        <html:submit styleClass="button">
					        	<bean:message key="label.xsl.save"/>
					        </html:submit>
					        <html:cancel styleClass="button">
					        	<bean:message key="label.stylesheet.cancel"/>
					        </html:cancel>
					      </td>
					    </tr>
					  </table>
			</html:form>		

