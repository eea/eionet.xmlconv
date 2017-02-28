<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java"%>
<%@ page import="eionet.gdem.Properties" %>
<%@ page import="eionet.gdem.utils.SecurityUtil" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<%
pageContext.setAttribute("org.apache.struts.globals.XHTML", "true", 1);
%>
<logic:present name="user">
    <bean:define id="username" name="user" scope="session"/>
</logic:present>

<div id="container">

    <div id="toolribbon">
        <div id="lefttools">
          <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
          <a id="ewlink" href="http://ew.eea.europa.eu/">EnviroWindows</a>
        </div>
        <div id="righttools">
            <logic:notPresent name="user">
                <a id="loginlink" href="<%=SecurityUtil.getLoginURL(request)%>" title="Login">Login</a>
            </logic:notPresent>
            <logic:present name="user">
                <a id="logoutlink" href="<c:url value="/do/logout"/>" title="Logout">Logout <span>(<bean:write name="user" scope="session"/>)</span></a>
            </logic:present>
            <a href="javascript:openWindow(applicationRoot+'/help/index.jsp','olinehelp');" title="Help">Online Help</a>
            <a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>
            <a id="fullscreenlink" href="javascript:toggleFullScreenMode()" title="Switch to/from full screen mode"><span>Switch to/from full screen mode</span></a>
            <a id="acronymlink" href="http://www.eionet.europa.eu/acronyms" title="Look up acronyms"><span>Acronyms</span></a>
            <form action="https://google.com/search" method="get">
                <div id="freesrchform">
                    <label for="freesrchfld">Search</label>
                    <input type="text" id="freesrchfld" name="q"
                           onfocus="if (this.value == 'Search the site')
                                                   this.value = '';"
                           onblur="if (this.value == '')
                                                   this.value = 'Search the site';"
                           value="Search the site"/>
                    <input type="hidden" name="sitesearch" value="<%=Properties.appHost%>" />
                    <input id="freesrchbtn" type="image" src="<c:url value="/images/button_go.gif"/>" alt="Go"/>
                </div>
            </form>
        </div>
    </div> <!-- toolribbon -->

    <div id="pagehead">
        <c:url value="/images/eea-print-logo.gif" var="logoUrl" />
        <a href="/"><img src="${logoUrl}" alt="Logo" id="logo" /></a>
        <div id="networktitle">Eionet</div>
        <div id="sitetitle">XML Services</div>
        <div id="sitetagline">Conversion and Quality Assessment Service</div>
    </div> <!-- pagehead -->

    <div id="menuribbon">
        <%@ include file="dropdownmenus.txt" %>
    </div> <!-- menuribbon -->

    <div id="portal-breadcrumbs" class="breadcrumbtrail">
        <div class="breadcrumbhead">You are here:</div>
        <div class="breadcrumbitem eionetaccronym"><a href="http://www.eionet.europa.eu" title="European Environment Information and Observation Network (Eionet)">Eionet</a></div>
    </div> <!-- breadcrumbtrail -->

    <div id="leftcolumn" class="localnav">
        <ul>
            <li>New menu</li>
            <ed:menuItem action="/web/projects" title="Schemas">
                <bean:message key="label.menu.projects"/>
            </ed:menuItem>
            <ed:menuItem action="/old/schemas" title="Schemas">
                <bean:message key="label.menu.schemas"/>
            </ed:menuItem>
            <ed:menuItem action="/old/conversions" title="Handcoded Stylesheets">
                <bean:message key="label.menu.stylesheets"/>
            </ed:menuItem>
            <ed:menuItem action="/old/generatedConversions" title="Generated Stylesheets">
                <bean:message key="label.menu.stylesheetsGenerated"/>
            </ed:menuItem>
            <ed:menuItem action="/old/converter" title="Converter">
                <bean:message key="label.menu.converter"/>
            </ed:menuItem>
            <ed:menuItem action="/old/validation" title="Validate XML">
                <bean:message key="label.conversion.tab.validation"/>
            </ed:menuItem>
            <ed:menuItem action="/old/workqueue" title="QA jobs">
                <bean:message key="label.menu.QAJobs"/>
            </ed:menuItem>
            <ed:menuItem action="/old/qaScripts" title="QA Scripts">
                <bean:message key="label.menu.queries"/>
            </ed:menuItem>
            <ed:menuItem action="/old/qaSandbox" title="QA Sandbox">
                <bean:message key="label.menu.xqsendbox"/>
            </ed:menuItem>
            <ed:menuItem action="/old/xmlFiles" title="XML Files">
                <bean:message key="label.menu.xmlfiles"/>
            </ed:menuItem>
            <ed:hasPermission username="username" acl="host" permission="v">
                <ed:menuItem action="/old/hosts"  title="Hosts">
                    <bean:message key="label.menu.hosts"/>
                </ed:menuItem>
            </ed:hasPermission>
            <ed:hasPermission username="username" acl="config" permission="u">
                <ed:menuItem action="/old/config/database"  title="Edit application configurations">
                    <bean:message key="label.menu.config"/>
                </ed:menuItem>
            </ed:hasPermission>
            <ed:hasPermission username="username" acl="serverstatus" permission="v">
                <ed:menuItem action="/serverstatus.jsp"  title="View the server status">
                    <bean:message key="label.menu.serverstatus"/>
                </ed:menuItem>
            </ed:hasPermission>
        </ul>
        <!-- To be removed after eveything is migrated -->
        <hr />
        <ul>
            <li>Old Menu</li>
            <ed:menuItem action="/do/uplSchemas" title="Schemas">
                <bean:message key="label.menu.schemas"/>
            </ed:menuItem>
            <ed:menuItem action="/do/stylesheetList" title="Handcoded Stylesheets">
                <bean:message key="label.menu.stylesheets"/>
            </ed:menuItem>
            <ed:menuItem action="/do/generatedStylesheetList" title="Generated Stylesheets">
                <bean:message key="label.menu.stylesheetsGenerated"/>
            </ed:menuItem>
            <ed:menuItem action="/do/listConvForm" title="Converter">
                <bean:message key="label.menu.converter"/>
            </ed:menuItem>
            <ed:menuItem action="/do/validateXMLForm" title="Validate XML">
                <bean:message key="label.conversion.tab.validation"/>
            </ed:menuItem>
            <ed:menuItem action="/workqueue.jsp" title="QA jobs">
                <bean:message key="label.menu.QAJobs"/>
            </ed:menuItem>
            <ed:menuItem action="/do/qaScripts" title="QA Scripts">
                <bean:message key="label.menu.queries"/>
            </ed:menuItem>
            <ed:menuItem action="/do/qaSandboxForm" title="QA Sandbox">
                <bean:message key="label.menu.xqsendbox"/>
            </ed:menuItem>
            <ed:menuItem action="/do/uplXmlFiles" title="XML Files">
                <bean:message key="label.menu.xmlfiles"/>
            </ed:menuItem>
            <ed:hasPermission username="username" acl="host" permission="v">
                <ed:menuItem action="/do/hosts/list"  title="Hosts">
                    <bean:message key="label.menu.hosts"/>
                </ed:menuItem>
            </ed:hasPermission>
            <ed:hasPermission username="username" acl="config" permission="u">
                <ed:menuItem action="/do/dbForm"  title="Edit application configurations">
                    <bean:message key="label.menu.config"/>
                </ed:menuItem>
            </ed:hasPermission>
            <ed:hasPermission username="username" acl="serverstatus" permission="v">
                <ed:menuItem action="/serverstatus.jsp"  title="View the server status">
                    <bean:message key="label.menu.serverstatus"/>
                </ed:menuItem>
            </ed:hasPermission>
        </ul>
    </div> <!-- leftcolumn -->

    <div id="workarea">
