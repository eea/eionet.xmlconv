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

	<ed:breadcrumbs-push label="Test conversion" level="1" />
	<h4><bean:message key="label.conversion.testconversion"/></h4> 
	<div class="boxcontent">
		<logic:iterate id="schema" name="conversion.schemas" scope="session" type="Schema">	
			<html:form action="/testConversionForm" method="post" >
				  <table cellpadding="0" cellspacing="0" border="0" align="center">
				    <tr>
				      <td align="left" class="label"> 
					       <bean:message key="label.conversion.url"/>:
				      </td>
				      <td>&nbsp;</td>
				      <td>
					        <html:text property="url"  style="width:300px" />		        
				      </td>
				    </tr>
				    <tr>
				      <td colspan="3">&nbsp;</td>
				    </tr>
				    <tr>
				      <td colspan="3">
					        <bean:message key="label.conversion.selectConversion"/>
				      </td>
				    </tr>
				    <tr>
				      <td colspan="3">&nbsp;</td>
				    </tr>		    
				    <tr>
				      <td align="left" class="label"> 
					        <bean:message key="label.conversion.xmlSchema"/>:
				      </td>
				      <td>&nbsp;</td>
				      <td>
							<a target="blank" href="<bean:write name="schema" property="schema" />" title="<bean:write name="schema" property="schema" />">						
									<bean:write name="schema" property="schema" />
							</a>			        	        
				      </td>
				    </tr>
				    <tr>
				      <td colspan="3">&nbsp;</td>
				    </tr>
				    
				    <bean:define id="idConv" name="ConversionForm" property="conversionId"  type="java.lang.String"/>	    
				    
				    <logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">						
					    <tr>
					      <td align="right">
								<logic:equal name="stylesheet" property="convId" value="<%=idConv%>">
									<input type="radio" checked="checked" name="format" value="<bean:write name="stylesheet" property="convId" />" />
								</logic:equal>
								<logic:notEqual name="stylesheet" property="convId" value="<%=idConv%>">
									<input type="radio" name="format" value="<bean:write name="stylesheet" property="convId" />" />
								</logic:notEqual>
					      </td>
					      <td>&nbsp;</td>
					      <td>
								<a target="blank" href="<bean:write name="stylesheet" property="xsl" />" title="<bean:write name="stylesheet" property="xsl_descr" />">						
									<bean:write name="stylesheet" property="type" />
								</a>&#160;
								&nbsp;-&nbsp;<bean:write name="stylesheet" property="xsl_descr" /> 								
					      </td>
					    </tr>
					</logic:iterate>		    
				    <tr>
				      <td colspan="3">&nbsp;</td>
				    </tr>
				    <tr>
				      <td colspan="3" align="center">
				        <input type="button" class="button" value="<bean:message key="label.conversion.convert"/>" onclick="return submitAction('<bean:write name="webRoot" />/convert');"/>	        
				      </td>
				    </tr>
				    <tr>
				      <td colspan="3">&nbsp;</td>
				    </tr>
				  </table>
				</html:form>
				</logic:iterate>
				<div class="visualClear">&nbsp;</div>
				<logic:present name="conversion.valid" scope="request">
					<div style="width: 97%">
						<table class="sortable" align="center" width="100%">
							<thead>
								<tr>
									<th scope="col"><span title="Error"><bean:message key="label.table.conversion.type"/></span></th>
									<th scope="col"><span title="PositionLine"><bean:message key="label.table.conversion.line"/></span></th>
									<th scope="col"><span title="PositionCol"><bean:message key="label.table.conversion.col"/></span></th>
									<th scope="col"><span title="Message"><bean:message key="label.table.conversion.message"/></span></th>
								</tr>
							</thead>
							<tbody>
								<logic:iterate indexId="index" id="valid" name="conversion.valid" scope="request" type="ValidateDto">
									<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
										<td width="7%">
												<bean:write name="valid" property="type" />
										</td>
										<td width="7%">
												<bean:write name="valid" property="line" />
										</td>
										<td width="7%">
												<bean:write name="valid" property="column" />
										</td>
										<td width="79%">
												<bean:write name="valid" property="description" />
										</td>
									</tr>
								</logic:iterate>
							</tbody>
						</table>
					</div>	
				</logic:present>
	</div>
	<div class="boxbottom"><div>&nbsp;</div></div> 
	</div>
</div>
