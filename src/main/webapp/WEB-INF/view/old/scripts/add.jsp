<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Add QA script" level="3"/>

<link href="<c:url value='/static/webjars/jquery-ui/jquery-ui.css'/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<c:url value='/static/webjars/jquery/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/webjars/jquery-ui/jquery-ui.js'/>"></script>
<script type="text/javascript" src="/resources/js/statusModal.js"></script>
<script type="text/javascript" src="/resources/js/scripts.js"></script>


<form:form servletRelativeAction="/scripts" method="post" enctype="multipart/form-data" modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div"/>
  <fieldset class="fieldset">
    <legend><spring:message code="label.qascript.add"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question required" for="txtSchemaUrl">
          <spring:message code="label.qascript.schema"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input id="txtSchemaUrl" size="64" path="schema"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtShortName">
          <spring:message code="label.qascript.shortname"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input id="txtShortName" size="64" path="shortName"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtDescription">
          <spring:message code="label.qascript.description"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:textarea rows="2" cols="30" id="txtDescription" path="description" style="width:400px"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="selContentType">
          <spring:message code="label.qascript.resulttype"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select path="resultType" id="selContentType">
          <form:options items="${resulttypes}" itemLabel="convType" itemValue="convType"/>
        </form:select>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="selScriptType">
          <spring:message code="label.qascript.scripttype"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select path="scriptType" id="selScriptType">
          <form:options items="${scriptlangs}" itemLabel="convType" itemValue="convType"/>
        </form:select>
      </div>
    </div>
    <div class="row">
      <div class="columns small-8">
        <form:radiobutton path="asynchronousExecution" id="synchronousExecution" value="false" checked="checked"/>
        <label for="synchronousExecution"><spring:message code="label.qascript.synchronous"/></label>
      </div>
    </div>
    <div class="row">
      <div class="columns small-8">
        <form:radiobutton path="asynchronousExecution" id="asynchronousExecution" value="true"/>
        <label for="asynchronousExecution"><spring:message code="label.qascript.asynchronous"/></label>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question required" for="txtUpperLimit">
          <spring:message code="label.qascript.upperlimit"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input id="txtUpperLimit" size="3" path="upperLimit"/>
      </div>
    </div>
  </fieldset>

  <fieldset class="fieldset">
    <legend><spring:message code="label.qascript.mark.heavy.section"/></legend>
    <div class="row">
      <div class="columns small-8">
        <form:radiobutton path="markedHeavy" name="heavyScript" id="light" value="false" checked="checked" onchange="hideDropdown()"/>
        <label for="light"><spring:message code="label.qascript.light"/></label>
      </div>
    </div>
    <div class="row">
      <div class="columns small-8">
        <form:radiobutton path="markedHeavy" name="heavyScript" id="heavy" value="true" onchange="showDropdown()"/>
        <label for="heavy"><spring:message code="label.qascript.heavy"/></label>
      </div>
    </div>
    <div class="row" id="markedHeavyReason" style='display:none'>
      <div class="columns small-8">
        <form:select path="markedHeavyReason" id="selMarkedHeavyReason">
          <form:option value="Long running" id="longRunning" selected="true">Long running script</form:option>
          <form:option value="Out of memory" id="oom">Out of memory</form:option>
          <form:option value="Other" id="other">Other</form:option>
        </form:select>
      </div>
    </div>
    <div class="row" id="markedHeavyOtherReason" style='display:none'>
      <div class="columns small-8">
        <label for="markedHeavyOtherReasonTxt"><spring:message code="label.qascript.heavy.reason"/></label>
        <form:input id="markedHeavyOtherReasonTxt" path="markedHeavyReasonOther" maxlength="200"/>
      </div>
    </div>
  </fieldset>

  <fieldset class="fieldset">
    <legend>
      <label class="question required"><spring:message code="label.qascript.tab.title"/></label>
      <div style="font-size:75%"><i>Enter file or URL</i></div>
    </legend>
    <button class="statusHelp" type="button" style="color:#00446A; background:#ecf4f5; cursor:pointer; border: 1px solid #cfe3e4; padding: 0.5em; border-radius:6px">QA scripts status info
    </button><br><br>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtFile">
          <spring:message code="label.qascript.fileName"/>
        </label>
      </div>
      <div class="columns small-8">
        <input type="file" name="scriptFile" id="txtFile" style="width:400px" size="64"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtUrl">
          <spring:message code="label.qascript.url"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input id="txtUrl" path="url" style="width:680px"/>
      </div>
    </div>
  </fieldset>

  <div class="row">
    <div class="columns small-4">
      &#160;
    </div>
    <div class="columns small-8">
      <button type="submit" name="add" class="button">
        <spring:message code="label.save"/>
      </button>
    </div>
  </div>
  <div>
    <form:hidden path="schemaId"/>
    <form:hidden path="fileName"/>
  </div>
</form:form>

