<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>

<html:xhtml />
<div style="width:100%;">
    <div id="tabbedmenu">
        <ul>
            <li id="currenttab"><span style="color: black; text-decoration: none;" title='<bean:message key="label.config.system"/>'><bean:message key="label.config.system" /></span></li>
            <li>
                <html:link page="/do/dbForm"   titleKey="label.config.db" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <bean:message key="label.config.db" />
                </html:link>
            </li>
            <li>
                <html:link page="/do/ldapForm"   titleKey="label.config.ldap" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <bean:message key="label.config.ldap" />
                </html:link>
            </li>
        </ul>
	</div>
	<p>&nbsp;</p>


		<ed:breadcrumbs-push label="System configuration" level="1" />
		<h1><bean:message key="label.config.system.admin" /></h1>

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

			<html:form action="/system" method="post">
			    <table  class="formtable">
				  <col style="width:25%"/>
				  <col style="width:75%"/>
					  <tr>
						<td>
			            	<label for="qaTimeout" class="question"><bean:message key="label.config.system.qa.timeout" /></label>
			            </td>
			            <td>
			            	<html:text property="qaTimeout" maxlength="255" style="width: 30em;" styleId="qaTimeout"/>
			            </td>
			        </tr>
					  <tr>
						<td>
			            	<label for="cmdXGawk" class="question"><bean:message key="label.config.system.qa.xgawk" /></label>
			            </td>
			            <td>
			            	<html:text property="cmdXGawk" maxlength="255" style="width: 30em;" styleId="cmdXGawk"/>
			            </td>
			        </tr>
				        <tr>
				            <td colspan="2">&nbsp;</td>
				        </tr>
				        <tr>
				            <td colspan="2" align="center">
				                <html:submit styleClass="button">
				                    <bean:message key="label.config.system.save" />
				                </html:submit>
				            </td>
				        </tr>

			    </table>
			</html:form>

</div>
