<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%--<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>--%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<%--classname="java.lang.String"--%>
<tiles:importAttribute name="selectedTab" />
<div id="tabbedmenu">
    <ul>
        <ed:tabItem href="/do/listConvForm" title="Convert XML" id="convertXML" selectedTab="${selectedTab}">
            <bean:message key="label.conversion.tab.converters"/>
        </ed:tabItem>
        <ed:tabItem href="/do/crConversionForm" title="Search CR for XML files" id="searchXML" selectedTab="${selectedTab}">
            <bean:message key="label.conversion.tab.crconversion"/>
        </ed:tabItem>
        <ed:tabItem href="/do/excel2XmlConversionForm" title="Convert spreadsheet to DD XML" id="excel2xml" selectedTab="${selectedTab}">
            <bean:message key="label.conversion.tab.excel2xml"/>
        </ed:tabItem>
        <ed:tabItem href="/do/json2XmlForm" title="Convert JSON to XML" id="json2xml" selectedTab="${selectedTab}">
            <bean:message key="label.conversion.tab.json2xml"/>
        </ed:tabItem>
    </ul>
</div>
<div id="tabbedmenuend"></div>
