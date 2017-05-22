<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<spring:message code="label.qasandbox.extractSchema" var="extractSchemaLabel"/>
<spring:message code="label.qasandbox.searchXML" var="searchXmlLabel"/>
<spring:message code="label.qasandbox.findScripts" var="searchScriptsLabel"/>

<style>
  .dropzone .dz-preview .dz-progress {
    height: 20px;
    border: 1px solid #aaa;
    max-width: 600px;
    margin-bottom: 5px;
  }

  ul.dropzone-previews {
    line-height: normal !important;
  }

  .dz-processing .dz-progress {
    display: block;
  }

  .dz-complete .dz-progress {
    display: none;
  }

  .dropzone .dz-preview .dz-progress .dz-upload {
    display: block;
    height: 100%;
    width: 0;
    background: green;
  }

  .dropzone .dz-preview .dz-error-message {
    color: red;
    display: none;
  }

  .dz-remove {
    display: none;
  }

  .dz-success-mark {
    display: none;
  }

  .dz-error-mark {
    display: none;
  }

  .dz-details {
    margin-top: 10px;
  }

  #clickable {
    margin-top: -85px;
  }

  @media screen and (-webkit-min-device-pixel-ratio: 0) {
    #clickable {
      margin-top: -62px;
    }
  }

  @media screen and (-moz-os-version: windows-xp), screen and (-moz-os-version: windows-vista), screen and (-moz-os-version: windows-win7), screen and (-moz-os-version: windows-win8), screen and (-moz-os-version: windows-win10) {
    #clickable {
      margin-top: -72px;
    }
  }

  @media screen and (min-width: 0\0
  ) {
    #clickable {
      margin-top: -67px;
    }
  }
</style>
<%--<form:xhtml/>--%>
<div style="width:100%;">

  <ed:breadcrumbs-push label="QA sandbox" level="1"/>
  <h1><spring:message code="label.qasandbox.title"/></h1>




  <form:form action="/executeSandboxAction" method="post" modelAttribute="QASandboxForm">
    <table class="formtable">

        <%-- List of XML schemas  --%>
      <tr class="zebraeven">
        <td>
          <label class="question" for="selSchema">
            <spring:message code="label.qasandbox.xmlSchema"/>
          </label>
        </td>
      </tr>
      <tr>
        <td>
            <%--<bean:define id="schemas" name="qascript.qascriptList" property="qascripts"/>--%>
            <%--name="QASandboxForm" property="schemaUrl"--%>
          <form:select path="schemaUrl" styleId="selSchema">
            <form:option value="">--</form:option>
            <form:options collection="schemas" property="schema" labelProperty="label"/>
          </form:select>
        </td>
      </tr>
      <tr>
        <td>
          <button type="submit" value="searchXML" styleClass="button" property="action" title="${searchXmlLabel}"/>
          <button type="submit" value="searchScripts" styleClass="button" property="action"
                 title="${searchScriptsLabel}"/>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
      </tr>
    </table>
  </form:form>


  <form:form action="/executeSandboxAction" method="post" modelAttribute="QASandboxForm">
    <table class="formtable">
        <%-- CR XML files  --%>
        <%--<bean:define id="schema" name="QASandboxForm" property="schema" type="Schema"/>--%>
        <%--name="schema" property="crfiles">--%>
      <c:if test="${Schema.schema}">
        <%--<bean:size id="countfiles" name="schema" property="crfiles"/>
        <bean:define id="crfiles" name="schema" property="crfiles"/>--%>
        <tr class="zebraeven">
          <td>
            <label class="question" for="selXml">
              <spring:message code="label.qasandbox.CRxmlfiles"/> (${countfiles})
            </label>
          </td>
        </tr>
        <c:if test="${countfiles > 0}">
          <tr>
            <td>
              <form:select path="QASandboxForm.sourceUrl" name="QASandboxForm" property="sourceUrl" size="5"
                           styleId="selXml">
                <form:option value="">--</form:option>
                <form:options collection="crfiles" name="schema" property="url" labelProperty="label"/>
              </form:select>
            </td>
          </tr>
          <tr>
            <td>
              <form:input path="manualUrl" styleClass="button" property="action" title="label.qasandbox.manualUrl"/>
            </td>
          </tr>
        </c:if>
        <c:if test="${countfiles = 0}">
          <tr class="zebraeven">
            <td>
              <label class="question" for="txtSourceUrl">
                <spring:message code="label.qasandbox.sourceUrl"/>
              </label>
            </td>
          </tr>
          <tr>
            <td>
              <form:label path="sourceUrl" property="sourceUrl" styleId="txtSourceUrl" size="120"/>
            </td>
          </tr>
          <tr>
            <td>
              <form:input path="action" styleClass="button" property="action" title="${extractSchemaLabel}"/>
            </td>
          </tr>
        </c:if>
      </c:if>
    </table>
  </form:form>

  <%-- Insert URL manually --%>
  <%--notPresent name="schema" property="crfiles">--%>
  <c:if test="${!schema.crfiles}">
    <form:form action="/executeSandboxAction" method="post" modelAttribute="QASandboxForm">
      <table class="formtable">
        <tr class="zebraeven">
          <td>
            <label class="question" for="txtSourceUrl">
              <spring:message code="label.qasandbox.sourceUrl"/>
            </label>
          </td>
        </tr>
        <tr>
          <td>
              <%--styleId="txtSourceUrl" size="120" style="max-width: 780px;"/>--%>
              ${sourceUrl}
          </td>
        </tr>
        <tr>
          <td>
            <button type="submit" value="extractSchema" styleClass="button" property="action"
                   title="${extractSchemaLabel}"/>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
      </table>
    </form:form>

    <%--<c:if equal value="true" name="qascript.permissions" property="qsuPrm">--%>
    <c:if test="${qascript.permissions == 'qsuPrm'}">
      <c:if test="${not(fn:contains(header['User-Agent'],'MSIE 9.0'))}">
        <button style="float:right;" id="clickable">Upload file</button>
        <form action="/qasandbox/upload" id="my-dropzone" class="dropzone">
          <ul id="dropzone-previews" class="dropzone-previews"></ul>
        </form>
        <script type="text/javascript" src="<c:url value="/js/dropzone.min.js"/>"></script>

        <script id="mypreview" type="text/template">
          <li class="dz-preview dz-file-preview">
            <div class="dz-details">
              <div class="dz-filename">
                <span data-dz-name></span>
                <span>(<span data-dz-size></span>)</span>
                <div style="float:right">
                  <button class="dz-remove-button" style="margin-left:5px" type="button" data-dz-remove>Remove</button>
                  <button class="dz-select-button" style="margin-left:5px" type="button">Select</button>
                </div>
              </div>
              <div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div>
            </div>
            <div class="dz-success-mark"><span>✔</span></div>
            <div class="dz-error-mark"><span>✘</span></div>
            <div class="dz-error-message"><span data-dz-errormessage></span></div>
          </li>
        </script>

        <script type="text/javascript">
            $.ajaxSetup({cache: false});
            $(document).on('click', '.dz-filename span', function (event) {
                for (var index = 0; index < Dropzone.forElement("#my-dropzone").files.length; index++) {
                    if (Dropzone.forElement("#my-dropzone").files[index].name == $(this).text()) {
                        $("#txtSourceUrl").val(Dropzone.forElement("#my-dropzone").files[index].url)
                    }
                }
            });
            $(document).on('click', '.dz-select-button', function (event) {
                for (var index = 0; index < Dropzone.forElement("#my-dropzone").files.length; index++) {
                    if (Dropzone.forElement("#my-dropzone").files[index].name == $(this).parent().parent().children(':first').text()) {
                        $("#txtSourceUrl").val(Dropzone.forElement("#my-dropzone").files[index].url)
                    }
                }
            });
            var ctx = '${pageContext.request.contextPath}';
            Dropzone.options.myDropzone = {
                dictDefaultMessage: "",
                url: ctx + "/qasandbox/upload",
                clickable: "#clickable",
                acceptedFiles: ".xml, .gml",
                maxFiles: "5",
                maxFilesize: "300",
                createImageThumbnails: "false",
                addRemoveLinks: "false",
                previewsContainer: "#dropzone-previews",
                previewTemplate: document.getElementById("mypreview").innerHTML,
                init: function () {
                    $.getJSON(ctx + "/qasandbox/action?command=getFiles", function (data) {
                        if (data.Data.length != 0) {
                            $.each(data.Data, function (index, val) {
                                var mockFile = {name: val.name, size: val.size, url: val.url};
                                Dropzone.forElement("#my-dropzone").emit("addedfile", mockFile);
                                Dropzone.forElement("#my-dropzone").emit("complete", mockFile);
                                Dropzone.forElement("#my-dropzone").files.push(mockFile);
                            });
                        }
                    });
                    this.on("success", function (file, responseText) {
                        $("#txtSourceUrl").val(responseText.url)
                        var mockFile = {name: file.name, size: file.size, url: responseText.url};
                        Dropzone.forElement("#my-dropzone").files.push(mockFile);
                    });
                    this.on("uploadprogress", function (file, progress, bytesSent) {
                        //console.log("Progress :" + progress);
                        //$('.dz-upload').text(Math.round(progress) + "%")
                    });
                    this.on("removedfile", function (file) {
                        $.get(ctx + "/qasandbox/action", {command: "deleteFile", filename: file.name});
                    });
                }
            };
        </script>
      </c:if>
    </c:if>
  </c:if>
  <form:form action="/executeSandboxAction" method="post" modelAttribute="QASandboxForm">
    <table class="formtable">
        <%-- QA script type & content --%>
        <%--<c:if equal name="QASandboxForm" property="showScripts" value="false">--%>
      <c:if test="${QASandboxForm.showScripts == false}">
        <%--<c:if equal value="true" name="qascript.permissions" property="qsiPrm">--%>
        <c:if test="${qascript.permissions == 'qsiPrm'}">
          <tr>
            <td>&nbsp;</td>
          </tr>
          <tr class="zebraeven">
            <td>
              <label class="question">
                <spring:message code="label.qasandbox.qaScript"/>
              </label>
            </td>
          </tr>
          <tr>
            <td>
              <label class="question" for="selScriptType">
                <spring:message code="label.qasandbox.scriptType"/>
              </label>
                <%--<c:if present name="QASandboxForm" property="scriptId">--%>
              <c:if test="${QASandboxForm.scriptId}">
                <form:select path="scriptType1" property="scriptType" styleId="selScriptType" disabled="false">
                  <form:options collection="qascript.scriptlangs" property="convType"/>
                </form:select>
                <form:hidden path="scriptType2" property="scriptType"/>
              </c:if>
                <%--<c:if notPresent name="QASandboxForm" property="scriptId">--%>
              <c:if test="${!QASandboxForm.scriptId}">
                <form:select path="scriptType3" property="scriptType" styleId="selScriptType">
                  <form:options collection="qascript.scriptlangs" property="convType"/>
                </form:select>
              </c:if>
            </td>
          </tr>
          <tr>
            <td>
              <label class="question" for="txtScriptContent">
                <spring:message code="label.qasandbox.scriptContent"/>
              </label>
            </td>
          </tr>
          <tr>
            <td>
              <form:textarea path="scriptContent" property="scriptContent" styleId="txtScriptContent" rows="20"
                             cols="100"
                             style="width:99%"/>
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>
                <%--  Execute script --%>
              <form:input path="QARunNow" styleClass="button" property="action" title="#{label.qasandbox.runNow}"/>
                <%--  Add scripts to workqueue  --%>
                <%--<c:if equal value="true" name="qascript.permissions" property="wqiPrm">--%>
              <c:if test="${qascript.permissions == 'wqiPrm'}">
                <form:input path="addToWorkqueue" styleClass="button" property="action"
                            title="#{label.qasandbox.addToWorkqueue}"/>
              </c:if>
                <%--  Save content to file --%>
                <%--<c:if equal value="true" name="qascript.permissions" property="wquPrm">--%>
              <c:if test="${qascript.permissions == 'wquPrm'}">
                <%--<c:if equal name="QASandboxForm" property="showScripts" value="false">--%>
                <c:if test="${QASandboxForm.showScripts == 'false'}">
                  <c:if test="${QASandboxForm.scriptId}">
                    <c:if test="${QASandboxForm.scriptId == 0}">
                      <form:input path="saveFile" styleClass="button" property="action"
                                  title="#{label.qasandbox.saveFile}"/>
                    </c:if>
                  </c:if>
                </c:if>
              </c:if>
            </td>
          </tr>
        </c:if>
      </c:if>

        <%-- List of available QA scripts --%>
      <c:if test="${QASandboxForm.showScripts == true}">
        <tr class="zebraeven">
          <td>
            <label class="question">
              <spring:message code="label.qasandbox.qaScripts"/>
            </label>
          </td>
        </tr>
        <c:if test="${QASandboxForm.scriptsPresent == false}">
          <tr>
            <td><spring:message code="label.qasandbox.noScripts"/></td>
          </tr>
        </c:if>
        <c:if test="${schema.qascripts}">
          <%--<bean:size id="countscripts" name="schema" property="qascripts"/>
          <bean:define id="qascripts" name="schema" property="qascripts"/>--%>

          <c:if test="${countscripts == 0}">
            <%--<c:if iterate id="qascript" name="schema" property="qascripts" type="QAScript">--%>
            <c:forEach items="${schema.qascripts}" var="qascript">
              <%--<bean:define id="listScriptId" name="qascript" property="scriptId" type="String"/>--%>
              <tr>
                <td>
                  <form:radiobutton path="scriptId" property="scriptId" value="${listScriptId}"
                                    styleId="rad_${listScriptId}"/>
                  <label class="question" for="rad_${listScriptId}">
                      ${qascripts.shortName}
                  </label>
                  <span> -
                    <%--paramId="scriptId" paramName="qascript" paramProperty="scriptId" --%>
                      <a href="/scripts/${scriptId}" titleKey="label.qascript.view">
                          ${qascript.fileName}
                      </a>
                      (${qascripts.scriptType})
                      <%--<c:if equal value="true" name="qascript.permissions" property="qsuPrm">--%>
                      <c:if test="${qascript.permissions == 'qsuPrm'}">
                        <%--  If scriptType is NOT 'FME' --%>
                        <%--paramId="scriptId" paramName="qascript" paramProperty="scriptId" titleKey="label.qasandbox.editScriptTitle">--%>
                        <%--value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">--%>
                        <c:if test="${qascript.scriptType == 'fme'}">
                          <a href="/qasandbox/editQAScript/${scriptId}">
                            <spring:message code="label.qasandbox.editScript"/>
                          </a>
                        </c:if>
                      </c:if>
                  </span>
                </td>
              </tr>
            </c:forEach>
          </c:if>
        </c:if>
        <c:if test="${schema.doValidation == true}">
          <tr>
            <td>
              <form:radiobutton path="scriptId" property="scriptId" value="-1" styleId="radioValidate"/>
              <label class="question" for="radioValidate"><spring:message
                      code="label.qasandbox.schemaValidation"/></label>
            </td>
          </tr>
        </c:if>
        <c:if test="${qascript.permissions == 'qsiPrm'}">
          <tr>
            <td>
                <%--do/editQAScriptInSandbox?scriptId=0  titleKey="label.qasandbox.editScriptTitle"--%>
              <a href="/qaSandbox/editQAScript/0">
                <spring:message code="label.qasandbox.writeScript"/>
              </a>
            </td>
          </tr>
        </c:if>
        <tr>
          <td>&nbsp;</td>
        </tr>

        <tr>
          <td>
              <%--  Execute script --%>
            <form:input path="runNow" styleClass="button" property="action" title="#{label.qasandbox.runNow}"/>
              <%--  Add scripts to workqueue  --%>
            <c:if test="${qascript.permissions == 'wqiPrm'}">
              <form:input path="addToWorkqueuee" styleClass="button" property="action"
                          title="#{label.qasandbox.addToWorkqueue}"/>
            </c:if>
          </td>
        </tr>
      </c:if>
    </table>
  </form:form>
</div>
