<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
<div id="stylesheet" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div>&nbsp;</div>
	</div> 

			<ed:breadcrumbs-push label="Edit Stylesheet" level="3" />
			<h4><bean:message key="label.stylesheet.edit"/></h4> 

		<div class="boxcontent" >

		<html:form action="/stylesheetEdit" method="post" enctype="multipart/form-data">
		  <div style="padding-left:50px">
		  <table cellpadding="0" cellspacing="0" border="0" >
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.schema"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		      	<logic:present name="user">
		        	<html:text property="schema"  maxlength="255" style="width:400px" />		        
		        </logic:present>
		        <logic:notPresent name="user">
	        		<html:hidden name="stylesheetForm" property="schema" />
					<a target="blank" href="<bean:write name="stylesheetForm" property="schema" />" title="<bean:write name="stylesheetForm" property="schema" />">						
						<bean:write name="stylesheetForm" property="schema" />
					</a>&#160;		        	  
		        </logic:notPresent>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <logic:present name="user">
			    <tr>
			      <td colspan="3">
			        <bean:message key="label.stylesheet.selectDDSchema"/>:
			      </td>
			    </tr>		    
			    <tr>
			      <td>&nbsp;</td>
			    </tr>		    
			    <tr>
			      <td colspan="3">		    
			          <select name="xmlSchema"  size="6" onchange="setSchema()" >
						<option selected="selected" value="">
							--
						</option>		        
						<logic:iterate id="schema" name="stylesheet.DDSchemas" scope="session"  type="Schema">
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
			      <td>&nbsp;</td>
			    </tr>		    
		    </logic:present>		        
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.outputtype"/>:
		      </td>
		      <td>&nbsp;</td>
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
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		      	<bean:message key="label.stylesheet.description"/>:
		      </td>
		      <td>&nbsp;</td>
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
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.xslfile"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
					<a target="blank" href="<bean:write property="xsl" name="stylesheetForm"/>" title="<bean:write property="xsl" name="stylesheetForm"/>">						
								<bean:write property="xsl" name="stylesheetForm"/>
					</a>&#160;		        	  		        
		      </td>
		    </tr>
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <logic:present name="user">
		    <tr>
		      <td>		        
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:file property="xslfile" style="width:400px" size="64" />
		      </td>
		    </tr>
		    </logic:present>
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan="3" align="center">
		      	<logic:present name="user">
			        <html:submit styleClass="button">
			        	<bean:message key="label.stylesheet.upload"/>
			        </html:submit>
			        <html:cancel styleClass="button">
			        	<bean:message key="label.stylesheet.cancel"/>
			        </html:cancel>
		        </logic:present>
		        <logic:notPresent name="user">
			        <html:cancel styleClass="button">
			        	<bean:message key="label.ok"/>
			        </html:cancel>		        
		        </logic:notPresent>
		      </td>
		    </tr>
		  </table>
		</div>  
		</html:form>
		
		</div>
		<div class="boxbottom"><div>&nbsp;</div></div> 
	</div>
</div>

