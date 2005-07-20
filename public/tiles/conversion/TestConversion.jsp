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

			<ed:breadcrumbs-push label="Test conversion" level="1" />
			<h4><bean:message key="label.conversion.testconversion"/></h4> 

		<div class="boxcontent">

<logic:iterate id="schema" name="conversion.schemas" scope="session" type="Schema">

		<html:form action="/testConversionForm.do" method="post" >
		  <table cellpadding="0" cellspacing="0" border="0" align="center">
		    <tr>
		      <td align=right> 
		       <B> <bean:message key="label.conversion.url"/>: </B>
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:text property="url"  style="width: 30em;" />		        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan=3>
		        <bean:message key="label.conversion.selectConversion"/>
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>		    

		    <tr>
		      <td align=right> 
		       <B> <bean:message key="label.conversion.xmlSchema"/>: </B>
		      </td>
		      <td>&nbsp;</td>
		      <td>
			        <a target="blank" href="<bean:write name="schema" property="schema" />" title="<bean:write name="schema" property="schema" />">						
							<bean:write name="schema" property="schema" />
			    	</a>
		        	        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <bean:define id="idConv" name="idConv" scope="request" type="java.lang.String"/>	    
		    <logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">						
		    <tr>
		      <td align=right>
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
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan="3" align="center">
		        <input type="button" styleClass="button" class="button" value="<bean:message key="label.conversion.convert"/>" onclick="return submitAction('convert');">	        
		      </td>
		    </tr>
		  </table>
		</html:form>
		</logic:iterate>
		<div class="visualClear">&nbsp;</div>
		<logic:present name="conversion.valid" scope="request">
			<div style="width: 97%">
				<table class="sortable" align="center" width="100%">
					<tr>
						<th scope="col"><span title="Error"><bean:message key="label.table.conversion.type"/></span></th>
						<th scope="col"><span title="PositionLine"><bean:message key="label.table.conversion.line"/></span></th>
						<th scope="col"><span title="PositionCol"><bean:message key="label.table.conversion.col"/></span></th>
						<th scope="col"><span title="Message"><bean:message key="label.table.conversion.message"/></span></th>
					</tr>
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
				</table>
			</div>

		</logic:present>
		</div>
		<div class="boxbottom"><div></div></div> 
	</div>
</div>

