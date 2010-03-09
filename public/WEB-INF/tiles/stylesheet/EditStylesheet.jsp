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
		  <table class="formtable">
			<col class="labelcol"/>
			<col class="entrycol"/>
			<tr class="zebraeven">
	        	<td>
					<label class="question">
			        	<bean:message key="label.stylesheet.schema"/>
			    	</label>
				</td>
		   		<td>
		        	<html:text property="schema"  maxlength="255" style="width:400px" />
		        </td>		        
		    </tr>
		    <tr>
				<td>
					<label class="question" for="selDDSchema">
			        	<bean:message key="label.stylesheet.selectDDSchema"/>
			        </label>
		      </td>
		      <td>
		          <select name="xmlSchema"  size="10" onchange="setSchema()" style="width:98%" id="selDDSchema">
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
			<tr class="zebraeven">
				<td>
					<label class="question" for="selOutputType">
						<bean:message key="label.stylesheet.outputtype"/>
					</label>
				</td>
		      <td>
				<bean:define id="oType" name="stylesheet.outputtypeSel" scope="session" type="java.lang.String" />
	        	<select name="outputtype" style="width:100px" id="selOutputType">
					<logic:iterate id="opt" name="stylesheet.outputtype" scope="session"  property="convTypes" type="ConvType">
				         <logic:equal name="opt" property="convType" value="<%=oType%>">
							<option selected="selected" value="<bean:write name="opt" property="convType" />">
								<bean:write name="opt" property="convType" />
							</option>
						 </logic:equal>
				         <logic:notEqual name="opt" property="convType" value="<%=oType%>">
								<option value="<bean:write name="opt" property="convType" />">
									<bean:write name="opt" property="convType" />
							 	</option>
						 </logic:notEqual>							 						
					</logic:iterate>
		        </select>
		      </td>
		    </tr>
		    
		    
		    <logic:present name="schemaInfo" scope="request">
			<logic:equal name="schemaInfo" property="schemaLang" value="EXCEL">
			
			<bean:define id="depOn" name="stylesheetForm" property="dependsOn" scope="request" type="java.lang.String" />
			
				<tr>
		 			<td>
						<label class="question" for="selDependsOn">
							<bean:message key="label.stylesheet.dependsOn"/>
						</label>
					</td>
					<td>
						<select name="dependsOn" id="selDependsOn">
					        <logic:empty name="stylesheetForm" property="dependsOn">
					        	<option value="" selected="selected">--</option>
					        </logic:empty>
					        <logic:notEmpty name="stylesheetForm" property="dependsOn">
					        	<option value="">--</option>
					        </logic:notEmpty>
						        
						    <logic:iterate id="st" scope="request" name="existingStylesheets">
						        <logic:equal name="st" property="convId" value="<%=depOn %>">
							        <option value="<bean:write name="st" property="convId" />" selected="selected">
							        	<bean:write name="st" property="xslFileName"/>
							        </option>
						        </logic:equal>
						        <logic:notEqual name="st" property="convId" value="<%=depOn %>">
							        <option value="<bean:write name="st" property="convId" />">
							        	<bean:write name="st" property="xslFileName"/>
							        </option>
						        </logic:notEqual>
						     </logic:iterate>
					     </select>
					 </td>
				</tr>
				
			</logic:equal>
			</logic:present>
			
		    
		    <tr>
				<td>
					<label class="question" for="txtDescription">
			      		<bean:message key="label.stylesheet.description"/>
			      	</label>
				</td>
		      <td>
		        <html:textarea property="description"  rows="3" cols="30" style="width:400px" styleId="txtDescription"/>
		        <html:hidden property="stylesheetId" />
		      </td>
		    </tr>
			<tr class="zebraeven">
				<td>
					<label class="question" for="txtXsl">
				        <bean:message key="label.stylesheet.xslfile"/>
				     </label>
				</td>
		      <td>
					<a  href="<bean:write name="webRoot"/>/<bean:write property="xsl" name="stylesheetForm"/>" title="<bean:write property="xsl" name="stylesheetForm"/>">						
								<bean:write property="xslFileName" name="stylesheetForm"/>
					</a>
					&#160;&#160;&#160;&#160;&#160;&#160;(<bean:message key="label.lastmodified"/>: 
					<logic:present name="stylesheetForm" property="modified">
						<bean:write property="modified" name="stylesheetForm"/>
					</logic:present>
					<logic:notPresent name="stylesheetForm" property="modified">
						<span style="color:red"><bean:message key="label.fileNotFound"/></span>
					</logic:notPresent>
					)
		      </td>
		    </tr>
		      	<logic:present name="stylesheetForm" property="xslFileName">
				    <tr>
				      	<td colspan="2">
					        <html:textarea property="xslContent" style="width: 98%;" rows="20" cols="55" styleId="txtXsl"/>
				      	</td>
				    </tr>
			    <tr>
		    		<td>&#160;</td>
			      	<td>
				        <html:submit styleClass="button" property="action">
			        		<bean:message key="label.stylesheet.save"/>
				    	</html:submit>
			        	<html:hidden property="xslFileName" />
			        	<html:hidden property="checksum" name="stylesheetForm" />
			      	</td>
			    </tr>
		    <tr>
	    	  <td colspan="2">&#160;</td>
	    	 </tr>
		    <tr>
	    	  <td>&#160;</td>
		      <td>
		        <html:file property="xslfile" style="width:400px" size="68" />
		      </td>
		    </tr>
		    </logic:present>
		    <tr>
	    		<td>&#160;</td>
		      <td>
		        <html:submit styleClass="button" property="action">
		        	<bean:message key="label.stylesheet.upload"/>
		        </html:submit>
		      </td>
		    </tr>
		  </table>
		</html:form>
		

