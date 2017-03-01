<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Constants"%>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %><%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

<ed:breadcrumbs-push label="XML Schemas" level="1" />

<logic:present name="schemas.uploaded">

    <logic:equal value="true" name="schemas.uploaded" property="ssiPrm" >
        <div id="operations">
          <ul>
           <li><a href="/old/schemas/add"><spring:message code="label.uplSchema.add" /></a></li>
            </ul>
        </div>
    </logic:equal>

    <h1 class="documentFirstHeading">
        <spring:message code="label.schemas.uploaded"/>
    </h1>

    <%-- include Error display --%>
    <tiles:insertDefinition name="Error" />


    <logic:present name="schemas.uploaded" property="schemas" >
        <html:form action="/deleteUplSchema" method="post">
            <table class="datatable" width="100%">
                <logic:equal value="true" name="schemas.uploaded" property="ssdPrm" >
                    <col style="width:5%"/>
                </logic:equal>
                <col/>
                <col/>
                <col style="width:20px"/>
                <col style="width:20px"/>
                <col style="width:20px"/>
                <thead>
                    <tr>
                        <logic:equal value="true" name="schemas.uploaded" property="ssdPrm" >
                            <th scope="col"></th>
                        </logic:equal>
                        <th scope="col"><span title="Schema"><spring:message code="label.table.uplSchema.schema"/></span></th>
                        <th scope="col"><span title="Description"><spring:message code="label.table.uplSchema.description"/></span></th>
                        <th scope="col" title="Uploaded schemas">XSD</th>
                        <th scope="col" title="Stylesheets">XSL</th>
                        <th scope="col" title="QA scripts">QA</th>
                    </tr>
                </thead>
                <tbody>
                    <logic:iterate indexId="index" id="schema" name="schemas.uploaded" property="schemas" type="Schema">
                        <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                            <logic:equal value="true"  name="schemas.uploaded" property="ssdPrm" >
                                <td align="center" >
                                    <bean:define id="schemaId"  name="schema" property="id" />
                                    <html:radio property="schemaId" value="${schemaId}" />
                                </td>
                            </logic:equal>
                            <td>
                                <a href="/old/schemas/${schema.id}" title="view XML Schema properties">
                                    <bean:write name="schema" property="schema"  />
                                </a>
                            </td>
                            <td>
                                    <bean:write name="schema" property="description" />
                            </td>
                            <td align="center" >
                                <logic:notEmpty name="schema" property="uplSchemaFileName">
                                    <a href="<bean:write name="webRoot"/>/<%= Constants.SCHEMA_FOLDER%><bean:write name="schema" property="uplSchemaFileName" />" class="link-xsd" title="Open uploaded schema file"></a>
                                </logic:notEmpty>
                            </td>
                            <td>
                                <logic:greaterThan name="schema" property="countStylesheets" value="0">
                                    <c:url var="stylesheetsUrl" value="schemaStylesheets" >
                                        <c:param name="schema">${schema.schema}</c:param>
                                    </c:url>
                                    <a href="${stylesheetsUrl}" title="View schema stylesheets (<bean:write name="schema" property="countStylesheets" />)"  class="link-xsl"></a>
                                </logic:greaterThan>
                            </td>
                            <td>
                                <logic:greaterThan name="schema" property="countQaScripts" value="0">
                                    <a href="schemaQAScripts?schemaId=<bean:write name="schema" property="id" />" title="View schema QA scripts (<bean:write name="schema" property="countQaScripts" />)" class="link-xquery">
                                    </a>
                                </logic:greaterThan>
                            </td>
                        </tr>
                    </logic:iterate>
                </tbody>
            </table>
            <logic:equal value="true" name="schemas.uploaded" property="ssdPrm" >
                <div class="boxbottombuttons">
                    <input type="button"  class="button" value="<spring:message code="label.schema.delete"/>" onclick="return submitAction(1,'deleteUplSchema?deleteSchema=true');" />
                </div>
            </logic:equal>
        </html:form>
    </logic:present>

    <logic:notPresent name="schemas.uploaded" property="schemas" >
        <div class="advice-msg">
            <spring:message code="label.uplSchema.noSchemas"/>
        </div>
    </logic:notPresent>
    <div class="visualClear">&nbsp;</div>


</logic:present>

