<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

        <ed:breadcrumbs-push label="Add QA script" level="3" />

        <h1><bean:message key="label.qascript.add"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />
        <html:form action="/addQAScript" method="post" enctype="multipart/form-data">
          <table class="formtable">
            <col class="labelcol"/>
            <col class="entrycol"/>
            <tr class="zebraeven">
                    <td>
                        <label class="question required" for="txtSchemaUrl">
                            <bean:message key="label.qascript.schema"/>
                        </label>
                    </td>
                      <td>
                        <html:text styleId="txtSchemaUrl" size="64" property="schema" />
                    </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="txtShortName">
                        <bean:message key="label.qascript.shortname"/>
                    </label>
                </td>
              <td>
                <html:text styleId="txtShortName" size="64" property="shortName" />
              </td>
            </tr>
            <tr class="zebraeven">
                <td>
                    <label class="question" for="txtDescription">
                          <bean:message key="label.qascript.description"/>
                      </label>
                </td>
              <td>
                <html:textarea rows="2" cols="30" styleId="txtDescription" property="description"  style="width:400px" />
              </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="selContentType">
                          <bean:message key="label.qascript.resulttype"/>
                      </label>
                </td>
                <td>
                    <html:select name="QAScriptForm" property="resultType" styleId="selContentType" value="HTML">
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
                    <html:select name="QAScriptForm" property="scriptType" styleId="selScriptType" value="xquery">
                        <html:options collection="qascript.scriptlangs" property="convType"/>
                    </html:select>
                </td>
            </tr>
            <tr>
                <td>
                    <label class="question required" for="txtUpperLimit">
                          <bean:message key="label.qascript.upperlimit"/>
                      </label>
                </td>
                <td>
                    <html:text styleId="txtUpperLimit" size="3" property="upperLimit" />
                </td>
            </tr>
            <tr class="zebraeven">
              <td>
                <label class="question required" for="txtFile">
                    <bean:message key="label.qascript.fileName"/>
                 </label>
              </td>
              <td>
                <html:file property="scriptFile" styleId="txtFile" style="width:400px" size="64" />
              </td>
            </tr>
            <tr>
                <td>&#160;</td>
              <td>
                <html:submit styleClass="button" property="action">
                    <bean:message key="label.save"/>
                </html:submit>
              </td>
            </tr>
        </table>
        <div><html:hidden property="schemaId" /></div>
    </html:form>

