<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>



<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml/>

<ed:breadcrumbs-push label="Schema QA scripts" level="2" />



<logic:present name="schema.qascripts">

       <bean:define id="schemaId" name="schemaId" scope="request" type="String"/>
    <logic:iterate indexId="index" id="schema" name="schema.qascripts" property="qascripts" type="Schema">
               <bean:define id="schemaUrl" name="schema" property="schema" />
                <div id="tabbedmenu">
                    <ul>
                        <li>
                            <html:link page="/old/schemas/${schemaId}" titleKey="label.tab.title.schema" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                                <spring:message code="label.tab.title.schema" />
                            </html:link>
                        </li>
                        <li>
                            <html:link page="/old/schemas/schemaStylesheets?schema=${schemaUrl}"   titleKey="label.tab.title.xsl" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                                <spring:message code="label.tab.title.xsl" />
                            </html:link>
                        </li>
                        <li id="currenttab">
                            <span style="color: black; text-decoration: none;" title='<spring:message code="label.tab.title.scripts"/>'><spring:message code="label.tab.title.scripts" /></span>
                        </li>
                    </ul>
                </div>
                <div id="operations">
                      <ul>
                        <logic:equal value="true"  name="qascript.permissions" property="ssiPrm" >
                               <li>
                                <a href="addQAScriptForm?schemaId=<bean:write name="schema" property="id" />&amp;schema=<bean:write name="schema" property="schema" />">
                                    <spring:message code="label.qascript.add" />
                                </a>
                            </li>
                        </logic:equal>
                        <li>
                            <%--paramId="schemaId" paramName="schema" paramProperty="id"--%>
                            <html:link page="/old/qaSandbox/run/${schemaId}"titleKey="label.qascript.runservice.title">
                                   <spring:message code="label.qascript.runservice" />
                            </html:link>
                        </li>
                    </ul>
                </div>
            <h1 class="documentFirstHeading">
                <spring:message code="label.schema.qascripts"/>&nbsp;<bean:write name="schema" property="schema" />
            </h1>

    </logic:iterate>
    <%-- include Error display --%>
    <tiles:insertDefinition name="Error" />

    <logic:iterate indexId="index" id="schema" name="schema.qascripts" property="qascripts" type="Schema">
        <div class="visualClear">&nbsp;</div>
        <form:form action="/viewQAScriptForm" method="post">
            <table class="formtable">
                <tr>
                    <td style="width:510px">
                        <label class="question" for="validatefield"><spring:message code="label.qascript.schema.validate"/></label>
                    </td>
                    <td style="width:40px">
                        <logic:equal value="true"  name="qascript.permissions" property="ssiPrm" >
                            <html:checkbox name="schema" property="doValidation" styleId="validatefield" />
                        </logic:equal>
                        <logic:equal value="false"  name="qascript.permissions" property="ssiPrm" >
                            <bean:write  name="schema" property="doValidation" />
                        </logic:equal>
                    </td>
                    <td rowspan="2" style="vertical-align:bottom">
                        <logic:equal value="true"  name="qascript.permissions" property="ssiPrm" >
                            <!-- save button -->
                                   <input type="button"  class="button" value="<spring:message code="label.save"/>" onclick="return submitAction(1,'saveSchemaValidation');" />
                            <input type="hidden" name="schemaId" value="${schemaId}" />
                            <input type="hidden" name="schema" value="${schemaUrl}" />
                        </logic:equal>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label class="question" for="blockerValidation"><spring:message code="label.qascript.schema.isBlockerValidation"/></label>
                    </td>
                    <td>
                        <logic:equal value="true"  name="qascript.permissions" property="ssiPrm" >
                            <html:checkbox name="schema" property="blocker" styleId="blockerValidation" />
                        </logic:equal>
                        <logic:equal value="false"  name="qascript.permissions" property="ssiPrm" >
                            <bean:write  name="schema"   property="blocker" />
                        </logic:equal>
                    </td>
                </tr>
            </table>
        </form:form>

        <logic:present name="schema" scope="page" property="qascripts" >
            <form:form action="/searchCR" method="post">
            <table class="datatable" width="100%">
                <logic:equal value="true"  name="qascript.permissions" property="ssdPrm" >
                    <col style="width:10px"/>
                </logic:equal>
                <col style="width:10px"/>
                <col/>
                <col/>
                <col/>
                <thead>

                  <tr>
                    <logic:equal value="true"  name="qascript.permissions" property="ssdPrm" >
                          <th scope="col">&#160;</th>
                    </logic:equal>
                      <th scope="col">&#160;</th>
                      <th scope="col"><spring:message code="label.qascript.shortname"/></th>
                      <th scope="col"><spring:message code="label.qascript.description"/></th>
                      <th scope="col"><spring:message code="label.qascript.fileName"/></th>
                      <th scope="col"><spring:message code="label.lastmodified"/></th>
                      <th scope="col"><spring:message code="label.qascript.isActive"/></th>
                   </tr>
                </thead>
               <tbody>
            <logic:iterate indexId="index" id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">
                <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                    <bean:define id="scriptId" name="qascript" property="scriptId" />
                    <logic:equal value="true"  name="qascript.permissions" property="ssdPrm" >
                        <td align="center">
                            <input type="radio" name="scriptId" value="${scriptId}" />
                        </td>
                    </logic:equal>
                    <td>
                        <logic:equal value="true"  name="qascript.permissions" property="qsuPrm" >
                            <%--  If scriptType is NOT 'FME' --%>
                            <logic:notEqual name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                                <%--paramId="scriptId" paramName="qascript" paramProperty="scriptId"--%>
                                <html:link page="/old/qaSandbox/edit/${scriptId}" titleKey="label.qasandbox.label.qasandbox.editScript">
                                    <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"></img>
                                </html:link>
                            </logic:notEqual>
                            <%--  If scriptType is 'FME' --%>
                            <logic:equal name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                                <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>" title="<spring:message code="label.qascript.runservice.title" />">
                                    <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"></img>
                                </a>
                            </logic:equal>
                        </logic:equal>
                        <logic:notEqual value="true"  name="qascript.permissions" property="qsuPrm" >
                            <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>" title="<spring:message code="label.qascript.runservice.title" />">
                                <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"></img>
                            </a>
                        </logic:notEqual>
                    </td>
                    <td>
                        <a href="viewQAScriptForm?scriptId=<bean:write name="qascript" property="scriptId" />" title="view QAScript properties">
                            <bean:write name="qascript" property="shortName" />
                        </a>
                    </td>
                    <td>
                        <bean:write name="qascript" property="description" />
                    </td>
                    <td>
                    <%--  If scriptType is 'FME' don't show the link to the local script file --%>
                    <logic:notEqual name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                        <a  href="<bean:write name="webRoot"/>/<bean:write name="qascript" property="filePath" />" title="open QA script file">
                            <bean:write name="qascript" property="fileName" />
                        </a>
                    </logic:notEqual>
                    <logic:equal name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                        <bean:write name="qascript" property="fileName" />
                    </logic:equal>
                    </td>
                    <td>
                    <%--  If scriptType is 'FME' don't show the script Last Modified Date --%>
                    <logic:notEqual name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                        <logic:notEqual value=""  name="qascript" property="modified" >
                            <bean:write name="qascript" property="modified" />
                        </logic:notEqual>
                        <logic:equal value=""  name="qascript" property="modified" >
                            <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                        </logic:equal>
                    </logic:notEqual>
                    </td>
                    <td>
                        <logic:equal name="qascript" property="active" value="true">
                            <input type="checkbox" checked="checked" disabled/>
                        </logic:equal>
                        <logic:notEqual name="qascript" property="active" value="true">
                            <input type="checkbox" disabled/>
                        </logic:notEqual>
                    </td>
                </tr>
            </logic:iterate>
            </tbody>
            </table>
                <div class="boxbottombuttons">
                    <logic:equal value="true"  name="qascript.permissions" property="ssdPrm" >
                           <input type="button"  class="button" value="<spring:message code="label.qascript.delete"/>" onclick="return submitAction(2,'deleteQAScript');" />
                        <input type="hidden" name="schemaId" value="${schemaId}" />
                    </logic:equal>
                    <logic:equal value="true"  name="qascript.permissions" property="ssdPrm" >
                           <input type="button"  class="button" value="<spring:message code="label.qascript.activate"/>" onclick="return submitAction(2,'activateQAScript');" />
                        <input type="hidden" name="schemaId" value="${schemaId}" />
                    </logic:equal>
                    <logic:equal value="true"  name="qascript.permissions" property="ssdPrm" >
                           <input type="button"  class="button" value="<spring:message code="label.qascript.deactivate"/>" onclick="return submitAction(2,'deactivateQAScript');" />
                        <input type="hidden" name="schemaId" value="${schemaId}" />
                    </logic:equal>
                </div>
            </form:form>

            </logic:present>
            <logic:notPresent name="schema" scope="page" property="qascripts" >
                <div class="advice-msg">
                    <spring:message code="label.schema.noQAScripts"/>
                </div>
            </logic:notPresent>
    </logic:iterate>

    <div class="visualClear">&nbsp;</div>
</logic:present>



