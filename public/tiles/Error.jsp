<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>

<logic:present name="org.apache.struts.action.ACTION_MESSAGE">
<table cellpadding="0" cellspacing="0" border="0" align="center">
  <tr>
    <td>
      <div class="success">
		  <html:messages id="message" message="true">
		     <bean:write name="message" filter="false"/>
		  </html:messages>
      </div>
    </td>
  </tr>
</table>
</logic:present>

<logic:present name="dcm.messages">
<table cellpadding="0" cellspacing="0" border="0" align="center">
  <tr>
    <td>
      <div class="success">
		  <html:messages id="message" name="dcm.messages">
		     <bean:write name="message" filter="false"/>
		  </html:messages>		  
      </div>
    </td>
  </tr>
</table>
</logic:present>

<logic:present name="dcm.errors">
<table cellpadding="0" cellspacing="0" border="0" align="center">
  <tr>
    <td>
      <div class="error">
		  <html:messages id="message" name="dcm.errors">
		     <bean:write name="message" filter="false"/>
		  </html:messages>
      </div>
    </td>
  </tr>
</table>
</logic:present>

<logic:present name="org.apache.struts.action.ERROR">
<table cellpadding="0" cellspacing="0" border="0" align="center">
  <tr>
    <td>
      <div class="error"><html:errors/></div>
    </td>
  </tr>
</table>
</logic:present>

