<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>


<logic:present name="org.apache.struts.action.ERROR">
<table cellpadding="0" cellspacing="0" border="0" align="center">
  <tr>
    <td colspan="3">
      <div class="error"><html:errors/></div>
    </td>
  </tr>
</table>
</logic:present>