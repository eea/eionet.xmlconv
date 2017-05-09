<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml/>

<ed:breadcrumbs-push label="Schema stylesheets" level="2" />

<logic:present name="schema.stylesheets">
    <form:form action="/searchCR" method="post">

       <bean:define id="schemaUrl" name="schema" scope="request" type="String"/>
    <logic:iterate indexId="index" id="schema" name="schema.stylesheets" property="handCodedStylesheets" type="Schema">
            <logic:equal  value="true"  name="schema.stylesheets" property="handcoded" >
                   <bean:define id="id" name="schema" property="id" />
                <div id="tabbedmenu">
                    <ul>
                        <li>
                            <html:link page="/old/schemas/${id}" titleKey="label.tab.title.schema" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                                <spring:message code="label.tab.title.schema" />
                            </html:link>
                        </li>
                        <li id="currenttab">
                            <span style="color: black; text-decoration: none;" title='<spring:message code="label.tab.title.xsl"/>'><spring:message code="label.tab.title.xsl" /></span>
                        </li>
                        <li>
                            <html:link page="/old/schemas/${id}/qaScripts" titleKey="label.tab.title.scripts" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                                <spring:message code="label.tab.title.scripts" />
                            </html:link>
                        </li>
                    </ul>
                </div>
                <logic:equal value="true"  name="stylesheet.permissions" property="ssiPrm" >
                    <div id="operations">
                          <ul>
                               <li><a href="addStylesheetForm?schema=<bean:write name="schema" property="schema" />"><spring:message code="label.stylesheet.add" /></a></li>
                        </ul>
                    </div>
                </logic:equal>
            </logic:equal>
            <h1 class="documentFirstHeading">
                <spring:message code="label.schema.stylesheets"/>&nbsp;<bean:write name="schema" property="schema" />
            </h1>

    </logic:iterate>

    <%-- include Error display --%>
    <tiles:insertDefinition name="Error" />

    <logic:iterate indexId="index" id="schema" name="schema.stylesheets" property="handCodedStylesheets" type="Schema">
            <div class="visualClear">&nbsp;</div>

            <logic:present name="schema" scope="page" property="stylesheets" >
                    <table class="datatable" width="100%">
                        <logic:equal value="true"  name="stylesheet.permissions" property="ssdPrm" >
                            <col style="width:10px"/>
                        </logic:equal>
                        <col style="width:10px"/>
                        <col/>
                        <col/>
                        <col/>
                        <col/>
                        <thead>
                            <tr>
                                <logic:equal value="true"  name="stylesheet.permissions" property="ssdPrm" >
                                      <th scope="col">&#160;</th>
                                </logic:equal>
                                  <th scope="col">&#160;</th>
                                <th scope="col"><spring:message code="label.table.stylesheet.type"/></th>
                                <th scope="col"><spring:message code="label.table.stylesheet.description"/></th>
                                <th scope="col"><spring:message code="label.table.stylesheet.stylesheet"/></th>
                                <th scope="col"><spring:message code="label.table.stylesheet.modified"/></th>
                            </tr>
                        </thead>
                        <tbody>
                            <logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">
                                <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                                    <bean:define id="convId" name="stylesheet" property="convId" />
                                    <logic:equal value="true"  name="stylesheet.permissions" property="ssdPrm" >
                                        <td align="center">
                                            <input type="radio" name="conversionId" value="${convId}" />
                                        </td>
                                    </logic:equal>
                                    <td>
                                        <a href="searchCR?conversionId=${convId}&amp;schemaUrl=${schemaUrl}"><img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run" title="Run conversion"></img></a>
                                    </td>
                                    <td align="center">
                                        <logic:equal  value="false"  name="stylesheet"  property="ddConv" >
                                            <a href="stylesheetViewForm?stylesheetId=<bean:write name="stylesheet" property="convId" />" title="view stylehseet properties">
                                                <bean:write name="stylesheet" property="type" />
                                            </a>
                                        </logic:equal>
                                        <logic:equal value="true"  name="stylesheet"  property="ddConv" >
                                            <bean:write name="stylesheet" property="type" />
                                        </logic:equal>
                                    </td>
                                    <td>
                                        <bean:write name="stylesheet" property="description" />
                                    </td>
                                    <td>
                                        <logic:notEqual value="false"  name="stylesheet"  property="ddConv" >
                                            <a  href="<bean:write name="stylesheet" property="xsl" />" class="link-xsl">
                                                <bean:write name="stylesheet" property="xslFileName" />
                                            </a>&#160;
                                        </logic:notEqual>
                                        <logic:equal value="false"  name="stylesheet"  property="ddConv" >
                                            <a  href="<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />" class="link-xsl">
                                                <bean:write name="stylesheet" property="xslFileName" />
                                            </a>&#160;
                                        </logic:equal>
                                    </td>
                                    <td align="center">
                                        <logic:equal value="true"  name="stylesheet"  property="ddConv" >
                                            Generated
                                        </logic:equal>
                                        <logic:equal value="false"  name="stylesheet"  property="ddConv" >
                                            <logic:notEqual value=""  name="stylesheet" property="modified" >
                                                        <bean:write name="stylesheet" property="modified" />
                                            </logic:notEqual>
                                            <logic:equal value=""  name="stylesheet" property="modified" >
                                                <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                                            </logic:equal>
                                        </logic:equal>
                                    </td>
                                </tr>
                            </logic:iterate>
                            <tr>
                                <td valign="top" colspan="7">
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="boxbottombuttons">
                        <logic:equal value="true"  name="stylesheet.permissions" property="ssdPrm" >
                               <input type="button"  class="button" value="<spring:message code="label.stylesheet.delete"/>" onclick="return submitAction(1,'deleteStylesheet');" />
                        </logic:equal>

                        <input type="hidden" name="schemaUrl" value="${schemaUrl}" />
                    </div>
            </logic:present>
            <logic:notPresent name="schema" scope="page" property="stylesheets" >
                <div class="advice-msg">
                    <spring:message code="label.schema.noStylesheets"/>
                </div>
            </logic:notPresent>
    </logic:iterate>

    <div class="visualClear">&nbsp;</div>
    </form:form>
</logic:present>



