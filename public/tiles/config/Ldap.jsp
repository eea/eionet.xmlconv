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
        <li class="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.config.ldap"/>"><bean:message key="label.config.ldap" /></span></li>
        <li>
            <html:link page="/do/dbForm" titleKey="label.config.db" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                <bean:message key="label.config.db" />
            </html:link>
        </li>
    </ul>
</div>
<p>&nbsp;</p>

<div id="stylesheet" class="box">
<div class="boxleft">
<div class="boxtop">
<div></div>
</div>

<ed:breadcrumbs-push label="LDAP configuration" level="1" />
<h4><bean:message key="label.config.ldap.admin" /></h4>

<div class="boxcontent">
<html:form action="/ldap" method="post">
    <table cellpadding="0" cellspacing="0" border="0" align="center">
        <tr>
            <td align="left">
            <bean:message key="label.config.ldap.url" />:
            </td>
            <td>&nbsp;</td>
            <td><html:text property="url" maxlength="255" style="width: 30em;" /></td>
		</tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
		<tr>
            <td align="left">
            <bean:message key="label.config.ldap.context" />:
            </td>
            <td>&nbsp;</td>
            <td><html:text property="context" maxlength="255" style="width: 30em;" /></td>
		</tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
		<tr>
            <td align="left">
            <bean:message key="label.config.ldap.userDir" />:
            </td>
            <td>&nbsp;</td>
            <td><html:text property="userDir" maxlength="255" style="width: 30em;" /></td>
		</tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
		<tr>
            <td align="left">
            <bean:message key="label.config.ldap.attrUid" />:
            </td>
            <td>&nbsp;</td>
            <td><html:text property="attrUid" maxlength="255" style="width: 30em;" /></td>
        </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="3" align="center">
                <html:submit styleClass="button">
                    <bean:message key="label.config.ldap.save" />
                </html:submit>
            </td>
        </tr>
    </table>
</html:form></div>
<div class="boxbottom">
<div></div>
</div>
</div>
</div>

</div>
