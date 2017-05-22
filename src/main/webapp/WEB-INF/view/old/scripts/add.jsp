<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Add QA script" level="3"/>

<h1><spring:message code="label.qascript.add"/></h1>




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
        <form:input styleId="txtSchemaUrl" size="64" path="schema"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtShortName">
          <spring:message code="label.qascript.shortname"/>
        </label>
      </td>
      <td>
        <form:input styleId="txtShortName" size="64" path="shortName"/>
      </td>
    </tr>
    <tr class="zebraeven">
      <td>
        <label class="question" for="txtDescription">
          <spring:message code="label.qascript.description"/>
        </label>
      </td>
      <td>
        <form:textarea rows="2" cols="30" styleId="txtDescription" path="description" style="width:400px"/>
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
        <form:input styleId="txtUpperLimit" size="3" path="upperLimit"/>
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
              <html:file property="scriptFile" styleId="txtFile" style="width:400px" size="64"/>
            </td>
          </tr>
          <tr class="zebraeven">
            <td>
              <label class="question" for="txtUrl">
                <spring:message code="label.qascript.url"/>
              </label>
            </td>
            <td>
              <form:input styleId="txtUrl" path="url" style="width:680px"/>
            </td>
          </tr>
        </table>
        <!-- /div-->
      </td>
    </tr>
    <tr>
      <td>&#160;</td>
      <td>
        <button type="submit" styleClass="button" property="action">
          <spring:message code="label.save"/>
        </button>
      </td>
    </tr>
  </table>
  <div>
    <form:hidden path="schemaId"/>
    <form:hidden path="fileName"/>
  </div>
</form:form>
