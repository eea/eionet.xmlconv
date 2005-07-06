<%@ page contentType="text/html; charset=UTF-8" 
  import="java.util.List"
  import="java.util.Iterator"
  import="eionet.gdem.dto.*"
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
<div id="stylesheet" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div>
		</div>
	</div> 

			<ed:breadcrumbs-push label="Edit Stylesheet" level="1" />
			<h4><bean:message key="label.stylesheet.edit"/></h4> 

		<div class="boxcontent">

		<html:form action="/stylesheetEdit" method="post" enctype="multipart/form-data">
		  <table cellpadding="0" cellspacing="0" border="0" align="center">
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.schema"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:text property="schema" maxlength="50" style="width: 30em;" />		        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.outputtype"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
					<bean:define id="oType" name="stylesheet.outputtypeSel" scope="session" type="java.lang.String" />	
		        <select name="outputtype" style="width:100px;">
					<logic:iterate id="opt" name="stylesheet.outputtype" scope="session"  property="convTypes" type="ConvType">
					         <logic:equal name="opt" property="convType" value="<%=oType%>">
									<option selected value="<bean:write name="opt" property="convType" />">
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
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		      	<bean:message key="label.stylesheet.description"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:textarea property="description" style="width: 33em;"/>
		        <html:hidden property="stylesheetId" />
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.xslfile"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <bean:write property="xsl" name="stylesheetForm"/>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td>		        
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:file property="xslfile" style="width: 30em;" />
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan="3" align="center">
		        <html:submit styleClass="button">
		        	<bean:message key="label.stylesheet.upload"/>
		        </html:submit>
		        <html:cancel styleClass="button">
		        	<bean:message key="label.stylesheet.cancel"/>
		        </html:cancel>
		        
		      </td>
		    </tr>
		  </table>
		</html:form>
		
		</div>
		<div class="boxbottom"><div></div></div> 
	</div>
</div>

