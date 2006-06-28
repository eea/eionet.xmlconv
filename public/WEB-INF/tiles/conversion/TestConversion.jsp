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
				      <td colspan="3">
					        <bean:message key="label.conversion.selectSource"/>
				      </td>
				    </tr>
				    <tr>
				      <td colspan="3">&nbsp;</td>
				    </tr>
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
				      <td align="left" class="label"> 
					       <bean:message key="label.conversion.cdrfiles"/>:
				      </td>
				      <td>&nbsp;</td>
			      		<bean:size name="schema" id="countfiles" property="cdrfiles"/>
				      	<logic:greaterThan name="countfiles" value="0">
					      <td>
					        <select name="cdrFile"  size="10" >
								<option selected="selected" value="">
									--
								</option>		        
								<logic:iterate id="cdrfile" name="schema" scope="page"  property="cdrfiles" type="CdrFileDto">
										<option value="<bean:write name="cdrfile" property="url" />">
											<bean:write name="cdrfile" property="country" />&nbsp;-&nbsp;
											<bean:write name="cdrfile" property="title" />
											<logic:notEqual name="cdrfile" property="year" value="0">
												&nbsp;-&nbsp;(<bean:write name="cdrfile" property="year" />
												<logic:notEqual name="cdrfile" property="endyear" value="0">
													&nbsp;-&nbsp;<bean:write name="cdrfile" property="endyear" />
												</logic:notEqual>
												<logic:equal name="cdrfile" property="endyear" value="0">
													<logic:notEqual name="cdrfile" property="partofyear" value="">
														&nbsp;-&nbsp;<bean:write name="cdrfile" property="partofyear" />
													</logic:notEqual>
												</logic:equal>)
											</logic:notEqual>
										</option>
								</logic:iterate>
						    </select>
						  </td>
						</logic:greaterThan>
				      	<logic:equal name="countfiles" value="0">
					      <td>
						        <bean:message key="label.conversion.noCdrFiles"/>
					      </td>
						</logic:equal>
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
				    <tr>
				      <td colspan="3">
					        <bean:message key="label.conversion.selectConversion"/>
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
									<input type="radio" checked="checked" name="format" id="r_<bean:write name="stylesheet" property="convId" />" value="<bean:write name="stylesheet" property="convId" />" />
								</logic:equal>
								<logic:notEqual name="stylesheet" property="convId" value="<%=idConv%>">
									<input type="radio" name="format" id="r_<bean:write name="stylesheet" property="convId" />"  value="<bean:write name="stylesheet" property="convId" />" />
								</logic:notEqual>
					      </td>
					      <td>&nbsp;</td>
					      <td>
								<label for="r_<bean:write name="stylesheet" property="convId" />"><bean:write name="stylesheet" property="type" />
								&nbsp;-&nbsp;<bean:write name="stylesheet" property="xsl_descr" /></label>
					      </td>
					    </tr>
					</logic:iterate>		    
				    <tr>
				      <td colspan="3">&nbsp;</td>
				    </tr>
				    <tr>
				      <td colspan="3" align="center">
				      		<bean:size name="schema" id="count" property="stylesheets"/>
					      	<logic:greaterThan name="count" value="0">
						        <input type="button" class="button" value="<bean:message key="label.conversion.convert"/>" onclick="return submitAction('<bean:write name="webRoot" />/convert');"/>	        
					        </logic:greaterThan>
					        <logic:equal name="count" value="0">
					        <p style="color: red; font-weight: bold;"><bean:message key="label.conversion.noconversion"/></p>
					        </logic:equal>

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

