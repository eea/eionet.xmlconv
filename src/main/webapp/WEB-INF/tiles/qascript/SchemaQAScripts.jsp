<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
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
                            <html:link page="/do/viewSchemaForm?schemaId=${schemaId}"   titleKey="label.tab.title.schema" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                                <bean:message key="label.tab.title.schema" />
                            </html:link>
                        </li>
                        <li>
                            <html:link page="/do/schemaStylesheets?schema=${schemaUrl}"   titleKey="label.tab.title.xsl" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                                <bean:message key="label.tab.title.xsl" />
                            </html:link>
                        </li>
                        <li id="currenttab">
                            <span style="color: black; text-decoration: none;" title='<bean:message key="label.tab.title.scripts"/>'><bean:message key="label.tab.title.scripts" /></span>
                        </li>
                    </ul>
                </div>
                <div id="operations">
                      <ul>
                        <logic:equal name="ssiPrm" value="true"  name="qascript.permissions" property="ssiPrm" >
                               <li>
                                <a href="addQAScriptForm?schemaId=<bean:write name="schema" property="id" />&amp;schema=<bean:write name="schema" property="schema" />">
                                    <bean:message key="label.qascript.add" />
                                </a>
                            </li>
                        </logic:equal>
                        <li>
                            <html:link page="/do/openQAServiceInSandbox" paramId="schemaId" paramName="schema" paramProperty="id" titleKey="label.qascript.runservice.title">
                                   <bean:message key="label.qascript.runservice" />
                            </html:link>
                        </li>
                    </ul>
                </div>
            <h1 class="documentFirstHeading">
                <bean:message key="label.schema.qascripts"/>&nbsp;<bean:write name="schema" property="schema" />
            </h1>

    </logic:iterate>
    <%-- include Error display --%>
    <tiles:insert definition="Error" />

    <logic:iterate indexId="index" id="schema" name="schema.qascripts" property="qascripts" type="Schema">
        <div class="visualClear">&nbsp;</div>
        <html:form action="/viewQAScriptForm" method="post">
            <table class="formtable">
                <tr>
                    <td style="width:510px">
                        <label class="question" for="validatefield"><bean:message key="label.qascript.schema.validate"/></label>
                    </td>
                    <td style="width:40px">
                        <logic:equal name="ssiPrm" value="true"  name="qascript.permissions" property="ssiPrm" >
                            <html:checkbox name="schema" property="doValidation" styleId="validatefield" />
                        </logic:equal>
                        <logic:equal name="ssiPrm" value="false"  name="qascript.permissions" property="ssiPrm" >
                            <bean:write  name="schema" property="doValidation" />
                        </logic:equal>
                    </td>
                    <td rowspan="2" style="vertical-align:bottom">
                        <logic:equal name="ssiPrm" value="true"  name="qascript.permissions" property="ssiPrm" >
                            <!-- save button -->
                                   <input type="button"  class="button" value="<bean:message key="label.save"/>" onclick="return submitAction(1,'saveSchemaValidation');" />
                            <input type="hidden" name="schemaId" value="${schemaId}" />
                            <input type="hidden" name="schema" value="${schemaUrl}" />
                        </logic:equal>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label class="question" for="blockerValidation"><bean:message key="label.qascript.schema.isBlockerValidation"/></label>
                    </td>
                    <td>
                        <logic:equal name="ssiPrm" value="true"  name="qascript.permissions" property="ssiPrm" >
                            <html:checkbox name="schema" property="blocker" styleId="blockerValidation" />
                        </logic:equal>
                        <logic:equal name="ssiPrm" value="false"  name="qascript.permissions" property="ssiPrm" >
                            <bean:write  name="schema"   property="blocker" />
                        </logic:equal>
                    </td>
                </tr>
            </table>
        </html:form>

        <logic:present name="qascripts" name="schema" scope="page" property="qascripts" >
            <html:form action="/searchCR" method="post">
            <table class="datatable" width="100%">
                <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" property="ssdPrm" >
                    <col style="width:10px"/>
                </logic:equal>
                <col style="width:10px"/>
                <col/>
                <col/>
                <col/>
                <thead>

                  <tr>
                    <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" property="ssdPrm" >
                          <th scope="col">&#160;</th>
                    </logic:equal>
                      <th scope="col">&#160;</th>
                      <th scope="col"><bean:message key="label.qascript.shortname"/></th>
                      <th scope="col"><bean:message key="label.qascript.description"/></th>
                      <th scope="col"><bean:message key="label.qascript.fileName"/></th>
                      <th scope="col"><bean:message key="label.lastmodified"/></th>
                      <th scope="col"><bean:message key="label.qascript.isActive"/></th>
                   </tr>
                </thead>
               <tbody>
            <logic:iterate indexId="index" id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">
                <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                    <bean:define id="scriptId" name="qascript" property="scriptId" />
                    <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" property="ssdPrm" >
                        <td align="center">
                            <input type="radio" name="scriptId" value="${scriptId}" />
                        </td>
                    </logic:equal>
                    <td>
                        <logic:equal name="qsuPrm" value="true"  name="qascript.permissions" property="qsuPrm" >
                            <%--  If scriptType is NOT 'FME' --%>
                            <logic:notEqual name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                                <html:link page="/do/editQAScriptInSandbox?reset=true" paramId="scriptId" paramName="qascript" paramProperty="scriptId" titleKey="label.qasandbox.label.qasandbox.editScript">
                                    <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"></img>
                                </html:link>
                            </logic:notEqual>
                            <%--  If scriptType is 'FME' --%>
                            <logic:equal name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                                <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>" title="<bean:message key="label.qascript.runservice.title" />">
                                    <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"></img>
                                </a>
                            </logic:equal>
                        </logic:equal>
                        <logic:notEqual name="qsuPrm" value="true"  name="qascript.permissions" property="qsuPrm" >
                            <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>" title="<bean:message key="label.qascript.runservice.title" />">
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
                        <logic:notEqual name="fileExists" value=""  name="qascript" property="modified" >
                            <bean:write name="qascript" property="modified" />
                        </logic:notEqual>
                        <logic:equal name="fileNotExists" value=""  name="qascript" property="modified" >
                            <span style="color:red"><bean:message key="label.fileNotFound"/></span>
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
                    <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" property="ssdPrm" >
                           <input type="button"  class="button" value="<bean:message key="label.qascript.delete"/>" onclick="return submitAction(2,'deleteQAScript');" />
                        <input type="hidden" name="schemaId" value="${schemaId}" />
                    </logic:equal>
                    <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" property="ssdPrm" >
                           <input type="button"  class="button" value="<bean:message key="label.qascript.activate"/>" onclick="return submitAction(2,'activateQAScript');" />
                        <input type="hidden" name="schemaId" value="${schemaId}" />
                    </logic:equal>
                    <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" property="ssdPrm" >
                           <input type="button"  class="button" value="<bean:message key="label.qascript.deactivate"/>" onclick="return submitAction(2,'deactivateQAScript');" />
                        <input type="hidden" name="schemaId" value="${schemaId}" />
                    </logic:equal>
                </div>
            </html:form>

            </logic:present>
            <logic:notPresent name="qascripts" name="schema" scope="page" property="qascripts" >
                <div class="advice-msg">
                    <bean:message key="label.schema.noQAScripts"/>
                </div>
            </logic:notPresent>
    </logic:iterate>

    <div class="visualClear">&nbsp;</div>
</logic:present>



