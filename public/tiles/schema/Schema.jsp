<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>

<html:xhtml/>


<div id="schema" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div>
		</div>
	</div> 

			<ed:breadcrumbs-push label="XML Schema or DTD" level="2" />
			<h4><bean:message key="label.schema.edit"/></h4> 

		<div class="boxcontent" align="center">

		<html:form action="/schemaUpdate" method="post" >
		  <table cellpadding="0" cellspacing="0" border="0" align="center">
		    <tr>
		      <td align="right">
    		    <html:hidden property="schemaId" />
		        <bean:message key="label.schema.location"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td align="left">
	    		    <logic:present name="user">
			        	<html:text property="schema" maxlength="255" size="50" />		        
    		        </logic:present>
    		        <logic:notPresent name="user">
    		        	<html:hidden property="schema" />
						<a target="blank" href="<bean:write name="schemaForm" property="schema" />" title="<bean:write name="schemaForm" property="schema" />">						
							<bean:write name="schemaForm" property="schema" />
						</a>&#160;		        	      		        	
    		        </logic:notPresent>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td align="right">
		        <bean:message key="label.schema.description"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td align="left">
	    		    <logic:present name="user">		      	
				        <html:text property="description" maxlength="255" size="50" />		        
				    </logic:present>
    		        <logic:notPresent name="user">
							<bean:write name="schemaForm" property="description" />    		        
    		        </logic:notPresent>				    
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <bean:define id="schemaProp" name="schema.rootElemets" scope="session" property="schema" />
		    <logic:equal  value="true"  name="schemaProp" scope="page" property="isDTD" >
		    <tr>
		      <td align="right">
		        <bean:message key="label.elem.dtdid"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td align="left">
		        <html:text property="dtdId" maxlength="50" size="50" />		        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		    </tr>
		    </logic:equal>	
		  </table>
		  
		   <p>&nbsp;</p>
		  
			<logic:equal name="xsduPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >				
			<div class="boxbottombuttons">
   			    <input type="button"  class="button" value="<bean:message key="label.schema.save"/>" onclick="return submitAction('schemaUpdate');" />
		        &nbsp;
		        <html:cancel styleClass="button">
			      	<bean:message key="label.stylesheet.cancel"/>
  			    </html:cancel>
			</div>
		    </logic:equal>
		    <logic:notEqual name="xsduPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >
				<div class="boxbottombuttons">
			        <html:cancel styleClass="button">
				      	<bean:message key="label.ok"/>
	  			    </html:cancel>
				</div>
		    </logic:notEqual>	
		    
		  <p>&nbsp;</p>
		  
		 

			<logic:present name="user">

			  <table class="sortable" align="center" >
				<tr>
					<th scope="col"><span title="Element name"><bean:message key="label.schema.table.element"/></span></th>
					<th scope="col"><span title="Namespace"><bean:message key="label.schema.table.namespace"/></span></th>
					<th scope="col"><span title="Action"><bean:message key="label.schema.table.action"/></span></th>				
				</tr>
				
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
							<input type="button"  class="button" style="width:50px" value="<bean:message key="label.element.add"/>" onclick="return submitAction('elementAdd');" />
						</td>
					</tr>
					<tr>
						<td valign="top" colspan="3">
						</td>
					</tr>
					</logic:equal>				   	
			</table>

		</logic:present>


			  <logic:notPresent name="user">
			  <logic:present name="rootElem"  name="schema.rootElemets" scope="session" property="rootElem" >							  
			  <table class="sortable" align="center" width="80%">
				<tr>
					<th scope="col"><span title="Element name"><bean:message key="label.schema.table.element"/></span></th>
					<th scope="col"><span title="Namespace"><bean:message key="label.schema.table.namespace"/></span></th>
				</tr>
				
				
					<logic:iterate indexId="index" id="elem" name="schema.rootElemets" scope="session" property="rootElem" type="RootElem">						
	
					<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
						<td width="45%">
							<bean:write name="elem" property="name" />
						</td>
						<td width="45%">					
								<bean:write name="elem" property="namespace" />					
						</td>
					</tr>
					</logic:iterate>				
			</table>
		</logic:present>
		</logic:notPresent>

		</html:form>
		
		</div>
		<div class="boxbottom"><div></div></div> 
	</div>
</div>

