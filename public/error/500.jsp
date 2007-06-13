<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>

<%-- include header --%>
<tiles:insert page="/WEB-INF/tiles/TmpHeader.jsp" />

<table cellpadding="0" cellspacing="0" border="0" align="center">
  <tr>
    <td nowrap="nowrap">
		<div class="error">
			<bean:message key="label.error.500"/>
		</div>
    </td>
  </tr>
</table>

<%-- include footer --%>
<tiles:insert page="/WEB-INF/tiles/MainFooter.jsp">
	<tiles:put name="showFooter" type="string" value="true"/>
</tiles:insert>

