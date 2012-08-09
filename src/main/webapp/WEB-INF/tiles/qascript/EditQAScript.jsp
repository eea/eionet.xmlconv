<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

    <div style="width:100%;">
    <div id="tabbedmenu">
        <ul>

            <li id="currenttab"><span style="color: black; text-decoration: none;" title='<bean:message key="label.config.system"/>'><bean:message key="label.qascript.tab.title" /></span></li>
            <li>
                <html:link page="/do/qaScriptHistory"  paramId="script_id" paramName="QAScriptForm" paramProperty="scriptId"  titleKey="label.qascript.history"    style="color: black; text-decoration: none;">
                    <bean:message key="label.qascript.history" />
                </html:link>
            </li>
        </ul>
    </div>
        <ed:breadcrumbs-push label="Edit QA script" level="3" />

        <h1><bean:message key="label.qascript.edit"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

        <html:form action="/editQAScript" method="post" enctype="multipart/form-data">
          <table class="formtable">
            <col class="labelcol"/>
            <col class="entrycol"/>
            <tr class="zebraeven">
                <td>
                    <label class="question">
                        <bean:message key="label.qascript.schema"/>
                    </label>
                </td>
                  <td>
                    <bean:write name="QAScriptForm" property="schema" />
                </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="txtShortName">
                        <bean:message key="label.qascript.shortname"/>
                    </label>
                </td>
              <td>
                <html:text name="QAScriptForm" property="shortName" styleId="txtShortName"  size="64" />
              </td>
            </tr>
            <tr class="zebraeven">
                <td>
                    <label class="question" for="txtDescription">
                          <bean:message key="label.qascript.description"/>
                      </label>
                </td>
              <td>
                <html:textarea property="description"  rows="2" cols="30" style="width:400px" styleId="txtDescription"/>
              </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="selContentType">
                          <bean:message key="label.qascript.resulttype"/>
                      </label>
                </td>
                <td>
                    <html:select name="QAScriptForm" property="resultType" styleId="selContentType">
                        <html:options collection="qascript.resulttypes" property="convType"/>
                    </html:select>
                </td>
            </tr>
            <tr class="zebraeven">
                <td>
                    <label class="question" for="selScriptType">
                          <bean:message key="label.qascript.scripttype"/>
                      </label>
                </td>
                <td>
                    <html:select name="QAScriptForm" property="scriptType" styleId="selScriptType">
                        <html:options collection="qascript.scriptlangs" property="convType"/>
                    </html:select>
                </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="txtUpperLimit">
                          <bean:message key="label.qascript.upperlimit"/>
                      </label>
                </td>
                <td>
                    <html:text styleId="txtUpperLimit" size="3" property="upperLimit" />
                </td>
            </tr>

            <tr class="zebraeven">
                <td>
                    <label class="question" for="txtFile">
                        <bean:message key="label.qascript.fileName"/>
                     </label>
                </td>
              <td>
                    <a  href="<bean:write name="webRoot"/>/<bean:write property="filePath" name="QAScriptForm"/>" title="<bean:write property="filePath" name="QAScriptForm"/>">
                                <bean:write property="fileName" name="QAScriptForm"/>
                    </a>
                    &#160;&#160;&#160;&#160;&#160;&#160;(<bean:message key="label.lastmodified"/>:
                    <logic:present name="QAScriptForm" property="modified">
                        <bean:write property="modified" name="QAScriptForm"/>
                    </logic:present>
                    <logic:notPresent name="QAScriptForm" property="modified">
                        <span style="color:red"><bean:message key="label.fileNotFound"/></span>
                    </logic:notPresent>
                    )
              </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="txtFile">
                        <bean:message key="label.qascript.url"/>
                     </label>
                </td>
                <td>
                    <html:text styleId="txtUrl" property="url" size="64"/>
                </td>
            </tr>

                  <logic:present name="QAScriptForm" property="fileName">
                    <tr>
                          <td colspan="2">
                            <html:textarea property="scriptContent" style="width: 98%;" rows="20" cols="55" styleId="txtFile"/>
                          </td>
                    </tr>
                <tr>
                    <td>&#160;</td>
                      <td>
                        <html:submit styleClass="button" property="action">
                            <bean:message key="label.qascript.save"/>
                        </html:submit>
                        <html:hidden property="fileName" />
                        <html:hidden property="checksum" name="QAScriptForm" />
                        <html:hidden property="scriptId" name="QAScriptForm" />
                        <html:hidden property="schemaId" name="QAScriptForm" />
                      </td>
                </tr>
            <tr>
              <td colspan="2">&#160;</td>
             </tr>
            <tr>
              <td>&#160;</td>
              <td>
                <html:file property="scriptFile" style="width:400px" size="64" />
              </td>
            </tr>
            </logic:present>
            <tr>
                <td>&#160;</td>
              <td>
                <html:submit styleClass="button" property="action">
                    <bean:message key="label.qascript.upload"/>
                </html:submit>

               <logic:notEmpty  name="QAScriptForm" property="fileName">
                    <input type="button"  class="button" value="<bean:message key="label.qascript.checkupdates"/>" onclick="return submitAction(1,'diffUplScripts');" />
              </logic:notEmpty>



              </td>
            </tr>		  </table>
        </html:form>
</div>

