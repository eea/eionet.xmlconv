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
	<div style="width:100%;">
		<div id="tabbedmenu">
			<ul>
				<li class="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.converters"/>"><bean:message key="label.conversion.converters"/></span></li>
				<li><a onclick="return submitTab(this);" style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.excel2xml"/>" href="<bean:write name="webRoot" />/excel2xml_conversion.jsp"><bean:message key="label.conversion.excel2xml"/></a></li>
			</ul>
		</div>
		<p>&nbsp;</p>
				
<div id="stylesheet" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div>
		</div>
	</div> 

			<ed:breadcrumbs-push label="Find conversion" level="1" />
			<h4><bean:message key="label.conversion.find"/></h4> 

		<div class="boxcontent">

		<html:form action="/listConv.do" method="post" >
		  <table cellpadding="0" cellspacing="0" border="0" align="center">
		    <tr>
		      <td colspan=3>
		        <bean:message key="label.conversion.insertURL"/>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td align=right> 
		       <B> <bean:message key="label.conversion.url"/>: </B>
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:text property="xmlUrl"  style="width: 30em;" />		        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan=3>
		        <bean:message key="label.conversion.selectSchema"/>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>		    
		    <tr>
		      <td align=right>
		        <B><bean:message key="label.conversion.xmlSchema"/>:</B>
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <select name="xmlSchema" style="width:400px;">
									<option selected value="">
										--
									</option>		        
					<logic:iterate id="schema" name="converson.schemas" scope="session"  type="Schema">
									<option value="<bean:write name="schema" property="schema" />">
										<bean:write name="schema" property="schema" />
									</option>
					</logic:iterate>
		        </select>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan=3>
		        <bean:message key="label.conversion.validateSchema"/>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>		    
		    <tr>
		      <td align=right>
		      	<B><bean:message key="label.conversion.validate"/>:</B>
		      </td>
		      <td>&nbsp;</td>
		      <td>
					<input type="checkbox" name=validate id="validatefield"/>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan="3" align="center">
		        <html:submit styleClass="button">
		        	<bean:message key="label.conversion.list"/>
		        </html:submit>		        
		      </td>
		    </tr>
		  </table>
		</html:form>
		
		</div>
		<div class="boxbottom"><div></div></div> 
	</div>
</div>

</div>