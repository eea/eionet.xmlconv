<%@ page contentType="text/html; charset=UTF-8"
    import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml />
<div style="width: 100%;">
    <tiles:insert definition="ConverterTabs">
        <tiles:put name="selectedTab" value="json2xml" />
    </tiles:insert>

    <ed:breadcrumbs-push label="Json to Xml" level="1" />
    <h1>
        <spring:message code="label.conversion.json2xml.title" />
    </h1>

    <%-- include Error display --%>
    <tiles:insert definition="Error" />

    <form action="convertJson2Xml" method="get" id="json2XmlForm">
        <div>
            <label for="param">
                <spring:message code="label.conversion.json2xml.source" />
            </label>
            <textarea name="json" id="param" rows="10" cols="100"></textarea>
            <input type="submit" class="button" value="Convert" />
        </div>
    </form>
</div>
