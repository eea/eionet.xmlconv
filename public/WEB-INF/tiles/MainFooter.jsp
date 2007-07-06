<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>

<% String a=request.getContextPath(); session.setAttribute("webRoot",a==null?"":a); %>

		
</div> <!-- workarea -->
</div> <!-- container -->

<tiles:useAttribute id="showFooter" name="showFooter"/>
<logic:equal name="showFooter" value="true">
	<div id="pagefoot">
		<p>
            <a href="http://www.eea.europa.eu" style="font-weight:bold">European Environment Agency</a>
            <br/>Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark - Phone: +45 3336 7100
         </p>
	</div>
</logic:equal>