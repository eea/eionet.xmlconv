<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
<div style="width:100%;">

    <ed:breadcrumbs-push label="QA sandbox" level="1" />
    <h1><bean:message key="label.qasandbox.title"/></h1>

    <%-- include Error display --%>
    <tiles:insert definition="Error" />

    <html:form action="/executeSandboxAction" method="post">
        <table class="formtable">

                   <%-- List of XML schemas  --%>
                <tr class="zebraeven">
                    <td>
                         <label class="question" for="selSchema">
                            <bean:message key="label.qasandbox.xmlSchema"/>
                         </label>
                     </td>
                </tr>
                <tr>
                  <td>
                      <bean:define id="schemas" name="qascript.qascriptList" property="qascripts"/>
                    <html:select name="QASandboxForm" property="schemaUrl" styleId="selSchema">
                        <html:option value="">--</html:option>
                        <html:options collection="schemas" property="schema" labelProperty="label" />
                    </html:select>
                  </td>
            </tr>
            <tr>
                <td>
                    <html:submit styleClass="button" property="action">
                        <bean:message key="label.qasandbox.searchXML"/>
                    </html:submit>
                    <html:submit styleClass="button" property="action">
                        <bean:message key="label.qasandbox.findScripts"/>
                    </html:submit>
                </td>
            </tr>

            <tr>
                <td>&nbsp;</td>
            </tr>

                   <%-- CR XML files  --%>

                 <bean:define id="schema" name="QASandboxForm" property="schema" type="Schema"/>
                <logic:present name="schema" property="crfiles">
                      <bean:size id="countfiles" name="schema" property="crfiles"/>
                      <bean:define id="crfiles" name="schema" property="crfiles"/>

                    <tr class="zebraeven">
                     <td>
                         <label class="question" for="selXml">
                            <bean:message key="label.qasandbox.CRxmlfiles" /> (<bean:write name="countfiles"/>)
                        </label>
                      </td>
                    </tr>
                      <logic:greaterThan name="countfiles" value="0">
                        <tr>
                            <td>
                                <html:select name="QASandboxForm" property="sourceUrl"  size="5" styleId="selXml">
                                    <html:option value="">--</html:option>
                                    <html:options collection="crfiles" name="schema" property="url" labelProperty="label" />
                                </html:select>
                              </td>
                        </tr>
                        <tr>
                            <td>
                                <html:submit styleClass="button" property="action">
                                    <bean:message key="label.qasandbox.manualUrl"/>
                                </html:submit>
                            </td>
                        </tr>
                    </logic:greaterThan>
                    <logic:equal name="countfiles" value="0">
                    <tr class="zebraeven">
                            <td>
                                 <label class="question" for="txtSourceUrl">
                                    <bean:message key="label.qasandbox.sourceUrl" />
                                </label>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <html:text property="sourceUrl" styleId="txtSourceUrl" size="120"/>
                              </td>
                        </tr>
                        <tr>
                            <td>
                                <html:submit styleClass="button" property="action">
                                    <bean:message key="label.qasandbox.extractSchema"/>
                                </html:submit>
                            </td>
                        </tr>
                    </logic:equal>
            </logic:present>

            <%-- Insert URL manually --%>
            <logic:notPresent name="schema" property="crfiles">
                <tr class="zebraeven">
                    <td>
                         <label class="question" for="txtSourceUrl">
                            <bean:message key="label.qasandbox.sourceUrl" />
                        </label>
                            </td>
                        </tr>
                        <tr>
                            <td>
                        <html:text property="sourceUrl" styleId="txtSourceUrl" size="120"/>
                      </td>
                 </tr>
                <tr>
                    <td>
                        <html:submit styleClass="button" property="action">
                            <bean:message key="label.qasandbox.extractSchema"/>
                        </html:submit>
                    </td>
                </tr>
             </logic:notPresent>
            <tr>
                <td>&nbsp;</td>
            </tr>

        <%-- QA script type & content --%>
        <logic:equal name="QASandboxForm" property="showScripts" value="false">
            <logic:equal name="qsiPrm" value="true"  name="qascript.permissions" property="qsiPrm" >
            <tr class="zebraeven">
                <td>
                     <label class="question">
                        <bean:message key="label.qasandbox.qaScript"/>
                    </label>
                </td>
            </tr>
            <tr>
                <td>
                     <label class="question" for="selScriptType">
                        <bean:message key="label.qasandbox.scriptType" />
                    </label>
                    <logic:present name="QASandboxForm" property="scriptId">
                      <html:select property="scriptType" styleId="selScriptType" disabled="true">
                          <html:options collection="qascript.scriptlangs" property="convType"/>
                      </html:select>
                      <html:hidden property="scriptType" />
                    </logic:present>
                    <logic:notPresent name="QASandboxForm" property="scriptId">
                      <html:select property="scriptType" styleId="selScriptType">
                          <html:options collection="qascript.scriptlangs" property="convType"/>
                      </html:select>
                    </logic:notPresent>
                </td>
            </tr>
                <tr>
                    <td>
                         <label class="question" for="txtScriptContent">
                            <bean:message key="label.qasandbox.scriptContent" />
                        </label>
                    </td>
                </tr>
                <tr>
                    <td>
                        <html:textarea property="scriptContent" styleId="txtScriptContent"  rows="20" cols="100" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>
                        <%--  Execute script --%>
                        <html:submit styleClass="button" property="action">
                            <bean:message key="label.qasandbox.runNow"/>
                        </html:submit>
                        <%--  Add scripts to workqueue  --%>
                        <logic:equal name="wqiPrm" value="true"  name="qascript.permissions" property="wqiPrm" >
                            <html:submit styleClass="button" property="action">
                                <bean:message key="label.qasandbox.addToWorkqueue"/>
                            </html:submit>
                        </logic:equal>
                        <%--  Save content to file --%>
                        <logic:equal name="wquPrm" value="true"  name="qascript.permissions" property="wquPrm" >
                            <logic:equal name="QASandboxForm" property="showScripts" value="false">
                                <logic:present name="QASandboxForm" property="scriptId">
                                    <logic:notEqual name="QASandboxForm" property="scriptId" value="0">
                                        <html:submit styleClass="button" property="action">
                                           <bean:message key="label.qasandbox.saveFile"/>
                                        </html:submit>
                                    </logic:notEqual>
                                </logic:present>
                            </logic:equal>
                        </logic:equal>
                    </td>
                </tr>
            </logic:equal>
            </logic:equal>

            <%-- List of available QA scripts --%>
            <logic:equal name="QASandboxForm" property="showScripts" value="true">
                <tr class="zebraeven">
                    <td>
                         <label class="question">
                            <bean:message key="label.qasandbox.qaScripts"/>
                        </label>
                    </td>
                </tr>
                <logic:equal name="QASandboxForm" property="scriptsPresent" value="false">
                    <tr>
                        <td><bean:message key="label.qasandbox.noScripts"/></td>
                    </tr>
                </logic:equal>
                <logic:present name="schema" property="qascripts">
                      <bean:size id="countscripts" name="schema" property="qascripts"/>
                      <bean:define id="qascripts" name="schema" property="qascripts"/>

                    <logic:greaterThan name="countscripts" value="0">
                        <logic:iterate id="qascript" name="schema" property="qascripts" type="QAScript">
                              <bean:define id="listScriptId" name="qascript" property="scriptId" type="String"/>
                            <tr>
                                <td>
                                    <html:radio property="scriptId" value="${listScriptId}" styleId="rad_${listScriptId}" />
                                     <label class="question" for="rad_${listScriptId}">
                                        <bean:write name="qascript" property="shortName"/>
                                    </label>
                                    <span> -
                                        <html:link page="/do/viewQAScriptForm" paramId="scriptId" paramName="qascript" paramProperty="scriptId" titleKey="label.qascript.view">
                                            <bean:write name="qascript" property="fileName" />
                                        </html:link>
                                        (<bean:write name="qascript" property="scriptType" />)
                                        <logic:equal name="qsuPrm" value="true"  name="qascript.permissions" property="qsuPrm" >
                                          <%--  If scriptType is NOT 'FME' --%>
                                            <logic:notEqual name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                                              <html:link page="/do/editQAScriptInSandbox" paramId="scriptId" paramName="qascript" paramProperty="scriptId" titleKey="label.qasandbox.editScriptTitle">
                                                  <bean:message key="label.qasandbox.editScript" />
                                              </html:link>
                                            </logic:notEqual>
                                        </logic:equal>
                                    </span>
                                </td>
                            </tr>
                        </logic:iterate>
                      </logic:greaterThan>
                </logic:present>
                <logic:equal name="schema" property="doValidation" value="true">
                    <tr>
                        <td>
                            <html:radio property="scriptId" value="-1" styleId="radioValidate" />
                            <label class="question" for="radioValidate"><bean:message key="label.qasandbox.schemaValidation"/></label>
                        </td>
                    </tr>
                </logic:equal>
                <logic:equal name="qsiPrm" value="true"  name="qascript.permissions" property="qsiPrm" >
                    <tr>
                        <td>
                            <html:link page="/do/editQAScriptInSandbox?scriptId=0" titleKey="label.qasandbox.editScriptTitle">
                                <bean:message key="label.qasandbox.writeScript" />
                               </html:link>
                        </td>
                    </tr>
                </logic:equal>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>
                        <%--  Execute script --%>
                        <html:submit styleClass="button" property="action">
                            <bean:message key="label.qasandbox.runNow"/>
                        </html:submit>
                        <%--  Add scripts to workqueue  --%>
                        <logic:equal name="wqiPrm" value="true"  name="qascript.permissions" property="wqiPrm" >
                            <html:submit styleClass="button" property="action">
                                <bean:message key="label.qasandbox.addToWorkqueue"/>
                            </html:submit>
                        </logic:equal>
                    </td>
                </tr>
            </logic:equal>
        </table>
    </html:form>
</div>
