<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>

<html:xhtml/>


	
		<logic:notPresent name="schemaForm" property="backToConv">
			<ed:breadcrumbs-push label="XML Schema or DTD" level="3" />
		</logic:notPresent>
		<logic:present name="schemaForm" property="backToConv">
			<ed:breadcrumbs-push label="XML Schema or DTD" level="2" />
		</logic:present>
		<h1><bean:message key="label.schema.edit"/></h1> 

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

		<html:form action="/schemaUpdate" method="post">
		  <table width="100%" class="datatable">
			<col style="width:25%"/>
			<col style="width:75%"/>
		    <tr>
				<th scope="row" class="scope-row">
	    		    <html:hidden property="schemaId" />
			        <bean:message key="label.schema.url"/>:
			      </th>
			      <td align="left">
			        <html:hidden property="backToConv" />
		    		<logic:present name="user">
				       	<html:text property="schema" maxlength="255" style="width:400px"/>    
	    		    </logic:present>
	    		    <logic:notPresent name="user">
		    		    <html:hidden property="schema" />       		        	 		        	
						<a href="<bean:write name="schemaForm" property="schema" />" title="<bean:write name="schemaForm" property="schema" />">						
							<bean:write name="schemaForm" property="schema" />
						</a>&#160;		        	      		        	
	   		        </logic:notPresent>
			      </td>
			    </tr>
				<logic:notEmpty name="schemaForm" property="uplSchemaFileName">
					<tr>
  					  <th scope="row" class="scope-row">
				        <bean:message key="label.uplSchema.schemaFile"/>:
				      </th>
			    	  <td>
						<a href="<bean:write name="schemaForm" property="uplSchemaFileUrl" />">
							<bean:write name="schemaForm" property="uplSchemaFileName" />
						</a>
				      </td>
				    </tr>
				</logic:notEmpty>
			    <tr>
				  <th scope="row" class="scope-row">
			        <bean:message key="label.schema.description"/>:
			      </th>
			      <td align="left">
	    		    <logic:present name="user">		      	
				        <html:textarea property="description"  rows="3" cols="30" style="width:400px"/>	        
				    </logic:present>
	   		        <logic:notPresent name="user">
						<bean:write name="schemaForm" property="description" />    		        
	   		        </logic:notPresent>				    
			      </td>
			    </tr>
			    <tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.schema.language"/>:
			      </th>
			      <td>
		    		<logic:present name="user">
				        <html:select property="schemaLang" >
							<html:options property="schemaLanguages" />
			    	    </html:select>
					</logic:present>	    	   			      	
		    		<logic:notPresent name="user">
						<bean:write name="schemaForm" property="schemaLang" />
		    		</logic:notPresent>
			      </td>
			    </tr>
			    <tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.schema.dovalidation"/>:
			      </th>
			      <td>
		    		<logic:present name="user">
				        <html:checkbox property="doValidation" />
					</logic:present>	    	   			      	
		    		<logic:notPresent name="user">
						<bean:write name="schemaForm" property="doValidation" />
		    		</logic:notPresent>
			      </td>
			    </tr>
			    <logic:equal value="true" name="schemaForm" property="dtd" >
				    <tr>
					  <th scope="row" class="scope-row">
				        <bean:message key="label.elem.dtdid"/>:
				      </th>
				      <td align="left">
			    		<logic:present name="user">
					        <html:text property="dtdId" maxlength="50" size="50" />		        
						</logic:present>	    	   			      	
		    			<logic:notPresent name="user">
							<bean:write name="schemaForm" property="dtdId" />
		    			</logic:notPresent>
				      </td>
				    </tr>
			    </logic:equal>	
			<tr>			  
			   <td colspan="2" align="center">
			  <logic:equal name="xsduPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >				
	   			    <input type="button"  class="button" value="<bean:message key="label.schema.save"/>" onclick="return submitAction(1,'schemaUpdate');" />
			        &nbsp;
   		      </logic:equal>
			  <logic:notEqual name="xsduPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >
			        <logic:notPresent name="schemaForm" property="backToConv">		        					
		   			    <input type="button"  class="button" value="<bean:message key="label.ok"/>" onclick="return submitAction(1,'schemaStylesheets');" />
		  			</logic:notPresent>
			   </logic:notEqual>	
			  </td>
			 </tr>
		  </table>
			    
			<logic:present name="user">
			  <h3>Root elements</h3>
			  <table class="datatable">
				  <thead>
					<tr>
						<th scope="col"><span title="Element name"><bean:message key="label.schema.table.element"/></span></th>
						<th scope="col"><span title="Namespace"><bean:message key="label.schema.table.namespace"/></span></th>
						<th scope="col"><span title="Action"><bean:message key="label.schema.table.action"/></span></th>				
					</tr>			
				   </thead>
				   <tbody>
						<logic:present name="rootElem"  name="schema.rootElemets" scope="session" property="rootElem" >						
							<logic:iterate indexId="index" id="elem" name="schema.rootElemets" scope="session" property="rootElem" type="RootElem">								
								<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
									<td>
										<bean:write name="elem" property="name" />
									</td>
									<td>					
											<bean:write name="elem" property="namespace" />					
									</td>
									<td align="center">
										<logic:equal name="ssdPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >
										<a href="deleteElem?elemId=<bean:write name="elem" property="elemId" />"
										onclick='return elementDelete("<bean:write name="elem" property="name" />");'>
										<html:img page="/images/delete.gif" altKey="label.delete" title="delete root element"/>
										</a>	
										</logic:equal>		
									</td>
								</tr>
							</logic:iterate>				
							</logic:present>
							<logic:equal name="xsduPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >
								<tr>
									<td  align="left">
										 <html:text property="elemName" maxlength="255"  style="width:250px"/>
									</td>
									<td  align="left">					
										  <html:text property="namespace" maxlength="255"   style="width:250px"/>
									</td>
									<td >
										<input type="button"  class="button" style="width:50px" value="<bean:message key="label.element.add"/>" onclick="return submitAction(1,'elementAdd');" />
									</td>
								</tr>
								<tr>
									<td valign="top" colspan="3">
									</td>
								</tr>
							</logic:equal>	
					</tbody>			   	
				</table>	
			</logic:present>
				
   		     <logic:notPresent name="user">
			   <logic:present name="rootElem"  name="schema.rootElemets" scope="session" property="rootElem" >							  
				  <table class="datatable" width="80%">
					<col style="width:50%"/>
					<col style="width:50%"/>
				  	<thead>
						<tr>
							<th scope="col"><span title="Element name"><bean:message key="label.schema.table.element"/></span></th>
							<th scope="col"><span title="Namespace"><bean:message key="label.schema.table.namespace"/></span></th>
						</tr>
					</thead>
					<tbody>
						<logic:iterate indexId="index" id="elem" name="schema.rootElemets" scope="session" property="rootElem" type="RootElem">								
							<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
								<td>
									<bean:write name="elem" property="name" />
								</td>
								<td>					
									<bean:write name="elem" property="namespace" />
								</td>
							</tr>
						</logic:iterate>				
					</tbody>
				</table>
			   </logic:present>
			</logic:notPresent>
	
		</html:form>
