<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>



<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

        <ed:breadcrumbs-push label="Add QA script" level="3" />

        <h1><spring:message code="label.qascript.add"/></h1>

        <%-- include Error display --%>
        <tiles:insertDefinition name="Error" />
        <form:form action="/addQAScript" method="post" enctype="multipart/form-data">
          <table class="formtable">
            <col class="labelcol"/>
            <col class="entrycol"/>
            <tr class="zebraeven">
                    <td>
                        <label class="question required" for="txtSchemaUrl">
                            <spring:message code="label.qascript.schema"/>
                        </label>
                    </td>
                      <td>
                        <html:text styleId="txtSchemaUrl" size="64" property="schema" />
                    </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="txtShortName">
                        <spring:message code="label.qascript.shortname"/>
                    </label>
                </td>
              <td>
                <html:text styleId="txtShortName" size="64" property="shortName" />
              </td>
            </tr>
            <tr class="zebraeven">
                <td>
                    <label class="question" for="txtDescription">
                          <spring:message code="label.qascript.description"/>
                      </label>
                </td>
              <td>
                <html:textarea rows="2" cols="30" styleId="txtDescription" property="description"  style="width:400px" />
              </td>
            </tr>
            <tr>
                <td>
                    <label class="question" for="selContentType">
                          <spring:message code="label.qascript.resulttype"/>
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
                          <spring:message code="label.qascript.scripttype"/>
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
                    <label class="question required" for="txtUpperLimit">
                          <spring:message code="label.qascript.upperlimit"/>
                      </label>
                </td>
                <td>
                    <html:text styleId="txtUpperLimit" size="3" property="upperLimit" />
                </td>
            </tr>

            <tr class="zebraeven">
              <td>
                <label class="question required">
                    <spring:message code="label.qascript.tab.title"/>
                 </label>
                 <div style="font-size:75%"><i>Enter file or URL</i></div>
              </td>
              <td>
                &#160;
              </td>
            </tr>

            <tr class="zebraeven">
                <td colspan="2">
                    <!-- div style="border:1px solid"-->
                    <table class="formtable">
                      <col class="labelcol"/>
                      <col class="entrycol"/>
                      <tr class="zebraeven">
                        <td>
                            <label class="question" for="txtFile">
                                <spring:message code="label.qascript.fileName"/>
                            </label>
                        </td>
                        <td>
                            <html:file property="scriptFile" styleId="txtFile" style="width:400px" size="64" />
                        </td>
                     </tr>
                     <tr class="zebraeven">
                        <td>
                            <label class="question" for="txtUrl">
                                <spring:message code="label.qascript.url"/>
                            </label>
                        </td>
                        <td>
                            <html:text styleId="txtUrl" property="url"  style="width:680px"/>
                        </td>
                    </tr>
                  </table>
                  <!-- /div-->
                </td>
            </tr>
            <tr>
                <td>&#160;</td>
              <td>
                <html:submit styleClass="button" property="action">
                    <spring:message code="label.save"/>
                </html:submit>
              </td>
            </tr>
        </table>
        <div>
            <html:hidden property="schemaId" />
            <html:hidden property="fileName" />
        </div>
    </form:form>

