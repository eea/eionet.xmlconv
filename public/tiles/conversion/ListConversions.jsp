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
			<div class="boxtop"><div></div>
			</div> 
		<ed:breadcrumbs-push label="Find conversion" level="1" />
		<h4><bean:message key="label.conversion.find"/></h4> 
	
		<div class="boxcontent">
			<html:form action="/listConv" method="post" >
			  <table cellpadding="0" cellspacing="0" border="0" align="center">
			    <tr>
			      <td>
			        <bean:message key="label.conversion.insertURL"/>
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			    <tr>
			      <td class="label"> 
			        <bean:message key="label.conversion.url"/>:<br/>
			      </td>
			     </tr>
			     <tr>
			      <td>
			        <html:text property="xmlUrl"  style="width: 30em;" />		        
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			    <tr>
			      <td>
			        <bean:message key="label.conversion.selectSchema"/>
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>		    
			    <tr>
			      <td class="label">
			        <bean:message key="label.conversion.xmlSchema"/>:<br/>
			      </td>
			    </tr>
			    <tr>
			      <td>
			        <select name="xmlSchema"  size="6" >
										<option selected="selected" value="">
											--
										</option>		        
						<logic:iterate id="schema" name="conversion.schemas" scope="session"  type="Schema">
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
			    <tr>
			      <td>
			        <bean:message key="label.conversion.validateSchema"/>
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>		    
			    <tr>
			      <td>
			      	<table>
			      		<tr>
			      			<td class="label">
					      		<bean:message key="label.conversion.validate"/>:
					        </td>
					        <td>
								<input type="checkbox" name="validate" id="validatefield"/>
							</td>
					     </tr>
				    </table>
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			    <tr>
			      <td align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.conversion.list"/>
			        </html:submit>		        
			      </td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			    </tr>
			  </table>
			</html:form>
			
		</div>
		<div class="boxbottom"><div></div></div> 
		</div>
	</div>

</div>