<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Login" level="1"/>
<html:xhtml/>
<br/>

<table width="300" align="center">
<tr>
<td>
<div class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div>&nbsp;</div></div> 
		<h4><bean:message key="label.login.message"/></h4>			
			
		<div class="boxcontent">
		
			<html:form action="login" method="post" focus="username">
			  <table width="100%" cellpadding="0" cellspacing="0" border="0" align="center">
			    <tr>
			      <td>
			        <bean:message key="label.login.username"/>:
			      </td>
			      <td>&nbsp;</td>
			      <td>
			        <html:text property="username" size="15"/>
			      </td>
			    </tr>
			    <tr>
			      <td colspan="3">&nbsp;</td>
			    </tr>
			    <tr>
			      <td>
			        <bean:message key="label.login.password"/>:
			      </td>
			      <td>&nbsp;</td>
			      <td>
			        <html:password property="password" size="15"/>
			      </td>
			    </tr>
			    <tr>
			      <td colspan="3">&nbsp;</td>
			    </tr>
			    <tr>
			      <td colspan="3" align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.login.submit"/>
			        </html:submit>        
			      </td>
			    </tr>
			  </table>
			</html:form>
		</div>
		<div class="boxbottom"><div>&nbsp;</div></div> 
	</div>
</div>
</td>
</tr>
</table>