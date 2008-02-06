<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

		<ed:breadcrumbs-push label="Edit Stylesheet" level="3" />
		<h1><bean:message key="label.stylesheet.edit"/></h1> 

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

		<html:form action="/stylesheetEdit" method="post" enctype="multipart/form-data">
		  <table width="100%" class="datatable">
			<col style="width:16%"/>
			<col style="width:84%"/>
		    <tr>
				<th scope="row" class="scope-row">
			        <bean:message key="label.stylesheet.schema"/>:
				</th>
		      <td>
		      	<logic:present name="user">
		        	<html:text property="schema"  maxlength="255" style="width:400px" />		        
		        </logic:present>
		        <logic:notPresent name="user">
	        		<html:hidden name="stylesheetForm" property="schema" />
					<a  href="<bean:write name="stylesheetForm" property="schema" />" title="<bean:write name="stylesheetForm" property="schema" />">						
						<bean:write name="stylesheetForm" property="schema" />
					</a>&#160;		        	  
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
					<bean:define id="oType" name="stylesheet.outputtypeSel" scope="session" type="java.lang.String" />	
			   <logic:present name="user">		
		        	<select name="outputtype" style="width:100px">
		       </logic:present>
				<logic:iterate id="opt" name="stylesheet.outputtype" scope="session"  property="convTypes" type="ConvType">
				         <logic:equal name="opt" property="convType" value="<%=oType%>">
					         <logic:present name="user">						         
								<option selected="selected" value="<bean:write name="opt" property="convType" />">
									<bean:write name="opt" property="convType" />
								</option>
							 </logic:present>
					         <logic:notPresent name="user">									 								 
     						         <bean:write name="opt" property="convType" />
					         </logic:notPresent>
						 </logic:equal>
				         <logic:notEqual name="opt" property="convType" value="<%=oType%>">
					         <logic:present name="user">						         
								<option value="<bean:write name="opt" property="convType" />">
									<bean:write name="opt" property="convType" />
							 	</option>
							 </logic:present>
						 </logic:notEqual>							 						
				</logic:iterate>
				<logic:present name="user">			
			        </select>
		       </logic:present>
		      </td>
		    </tr>
		    <tr>
				<th scope="row" class="scope-row">
			      	<bean:message key="label.stylesheet.description"/>:
				</th>
		      <td>
			      <logic:present name="user">
				        <html:textarea property="description"  rows="3" cols="30" style="width:400px"/>
			      </logic:present>
			      <logic:notPresent name="user">
					    <bean:write name="stylesheetForm" property="description"/>
			      </logic:notPresent>
		        <html:hidden property="stylesheetId" />
		      </td>
		    </tr>
		    <tr>
				<th scope="row" class="scope-row">
			        <bean:message key="label.stylesheet.xslfile"/>:
				</th>
		      <td>
					<a  href="<bean:write name="webRoot"/>/<bean:write property="xsl" name="stylesheetForm"/>" title="<bean:write property="xsl" name="stylesheetForm"/>">						
								<bean:write property="xsl" name="stylesheetForm"/>
					</a>
					<logic:present name="stylesheetForm" property="modified">
						&#160;&#160;&#160;&#160;&#160;&#160;(<bean:message key="label.lastmodified"/>: <bean:write property="modified" name="stylesheetForm"/>)
					</logic:present>
		      </td>
		    </tr>
		    <logic:present name="user">
		      	<logic:present name="stylesheetForm" property="xslFileName">
				    <tr>
				      <td colspan="2" align="center">
				        <html:textarea property="xslContent" style="width: 98%;" rows="20" cols="55"/>
				      </td>
				    </tr>
			    <tr>
			      <td colspan="2" align="center">
			        <html:submit styleClass="button" property="action">
			        	<bean:message key="label.stylesheet.save"/>
				    </html:submit>
			        <html:hidden property="xslFileName" />
			        <html:hidden property="checksum" name="stylesheetForm" />
			      </td>
			    </tr>
			</logic:present>
		    <tr>
		      <td colspan="2" align="center">
		        <html:file property="xslfile" style="width:400px" size="64" />
		      </td>
		    </tr>
		    </logic:present>
		    <tr>
		      <td colspan="2" align="center">
		      	<logic:present name="user">
			        <html:submit styleClass="button" property="action">
			        	<bean:message key="label.stylesheet.upload"/>
			        </html:submit>
			        <html:cancel styleClass="button" property="action">
			        	<bean:message key="label.stylesheet.cancel"/>
			        </html:cancel>
		        </logic:present>
		        <logic:notPresent name="user">
			        <html:cancel styleClass="button" property="action">
			        	<bean:message key="label.ok"/>
			        </html:cancel>		        
		        </logic:notPresent>
		      </td>
		    </tr>
		  </table>
		</html:form>
		

