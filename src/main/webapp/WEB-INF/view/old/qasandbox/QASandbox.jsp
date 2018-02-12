<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:set var="scriptlangs" scope="page" value="${applicationScope['qascript.scriptlangs']}"/>

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

<c:set var="permissions" scope="page" value="${sessionScope['qascript.permissions']}"/>
<ed:breadcrumbs-push label="QA sandbox" level="1"/>
<%--<h1><spring:message code="label.qasandbox.title"/></h1>--%>

<form:form servletRelativeAction="/qaSandbox" method="post" modelAttribute="form">
  <fieldset class="fieldset">
    <legend><spring:message code="label.qasandbox.title"/></legend>

    <div class="row">
      <div class="columns small-4">
        <label class="question" for="selSchema">
          <spring:message code="label.qasandbox.xmlSchema"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select path="schemaUrl" styleId="selSchema">
          <form:option value="">--</form:option>
          <form:options items="${scripts.qascripts}" itemValue="schema" itemLabel="label"/>
        </form:select>
      </div>
    </div>
    <div class="row">
      <button type="submit" name="searchCR" class="button">
        <spring:message code="label.qasandbox.searchXML"/>
      </button>
      <button type="submit" name="searchScripts" class="button">
        <spring:message code="label.qasandbox.findScripts"/>
      </button>
    </div>

      <%-- CR XML files  --%>
    <c:set var="schema" value="${QASandboxForm.schema}"/>
    <c:choose>
      <c:when test="${!empty schema.crfiles}">
        <%--<bean:size id="countfiles" name="schema" property="crfiles"/>
        <bean:define id="crfiles" name="schema" property="crfiles"/>--%>
        <div class="row">
          <label class="question" for="selXml">
            <spring:message code="label.qasandbox.CRxmlfiles"/> (${countfiles})
          </label>
        </div>

        <c:if test="${countfiles > 0}">
          <div class="row">
            <div class="columns small-4">
              <form:select path="sourceUrl" size="5" styleId="selXml">
                <form:option value="">--</form:option>
                <form:options items="${crfiles}" name="schema" itemValue="url" itemLabel="label"/>
              </form:select>
            </div>
            <div class="columns small-8">
              <button name="manualUrl" class="button">
                <spring:message code="label.qasandbox.manualUrl"/>
              </button>
            </div>
          </div>
        </c:if>

        <c:if test="${countfiles = 0}">
          <div class="row">
            <div class="columns small-4">
              <label class="question" for="txtSourceUrl">
                <spring:message code="label.qasandbox.sourceUrl"/>
              </label>
            </div>
            <div class="columns small-8">
              <form:input type="text" path="sourceUrl" styleId="txtSourceUrl" size="120"/>
            </div>
          </div>
          <div class="row">
            <form:button name="extractSchema" class="button">
              <spring:message code="label.qasandbox.extractSchema"/>
            </form:button>
          </div>
        </c:if>
      </c:when>
      <c:otherwise>
        <%--<fieldset class="fieldset">--%>

        <div class="row">
          <div class="columns small-4">
            <label class="question" for="txtSourceUrl">
              <spring:message code="label.qasandbox.sourceUrl"/>
            </label>
          </div>
          <div class="columns small-8">
            <form:input type="text" path="sourceUrl" styleId="txtSourceUrl" size="120" style="max-width: 780px;"/>
          </div>
        </div>
        <div class="row">
          <button type="submit" name="extractSchema" class="button">
            <spring:message code="label.qasandbox.extractSchema"/>
          </button>
        </div>

        <div class="row">
          <div class="columns small-4">

          </div>
          <div class="columns small-8">

          </div>
        </div>

        <div class="row">
          <div class="columns small-4">

          </div>
          <div class="columns small-8">

          </div>
        </div>

        <div class="row">
          <div class="columns small-4">

          </div>
          <div class="columns small-8">

          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </fieldset>


  <%-- QA script type & content --%>
  <c:if test="${QASandboxForm.showScripts == false}">
    <c:if test="${permissions.qsiPrm}">
      <fieldset class="fieldset">
      <legend><spring:message code="label.qasandbox.qaScript"/></legend>
      <div class="row">
        <label class="question" for="selScriptType">
          <spring:message code="label.qasandbox.scriptType"/>
        </label>
        <c:choose>
          <c:when test="${!empty form.scriptId}">
            <form:select path="scriptType" styleId="selScriptType" disabled="false">
              <form:options items="${scriptlangs}" itemValue="convType" itemLabel="convType"/>
            </form:select>
            <%--<form:hidden path="scriptType" property="scriptType"/>--%>
          </c:when>
          <c:otherwise>
            <form:select path="scriptType" property="scriptType" styleId="selScriptType">
              <form:options items="${scriptlangs}" itemValue="convType" itemLabel="convType"/>
            </form:select>
          </c:otherwise>
        </c:choose>
      </div>
      <div class="row">
        <label class="question" for="txtScriptContent">
          <spring:message code="label.qasandbox.scriptContent"/>
        </label>
      </div>
      <div class="row">
        <form:textarea path="scriptContent" styleId="txtScriptContent" rows="8" cols="100" style="width:99%"/>
      </div>
      <div class="row">
        &nbsp;
      </div>
      <div class="row">
        <button name="QARunNow" class="button">
          <spring:message code="label.qasandbox.runNow"/>
        </button>
        <c:if test="${permissions.wqiPrm}">
          <button name="addToWorkqueue" class="button">
            <spring:message code="label.qasandbox.addToWorkqueue"/>
          </button>
        </c:if>
          <%--  Save content to file --%>
        <c:if test="${permissions.wquPrm}">
          <c:if test="${form.showScripts == false}">
            <c:if test="${form.scriptId}">
              <c:if test="${form.scriptId == 0}">
                <form:button name="saveFile" class="button">
                  <spring:message code="label.qasandbox.saveFile"/>
                </form:button>
              </c:if>
            </c:if>
          </c:if>
        </c:if>
      </div>

    </c:if>
  </c:if>

  <%-- List of available QA scripts --%>
  <c:if test="${form.showScripts == true}">
    <div class="row">
      <label class="question">
        <spring:message code="label.qasandbox.qaScripts"/>
      </label>
    </div>

    <div class="row">
      <div class="columns small-4">

      </div>
      <div class="columns small-8">

      </div>
    </div>
    <div class="row">
      <div class="columns small-4">

      </div>
      <div class="columns small-8">

      </div>
    </div>

    <c:if test="${form.scriptsPresent == false}">
      <div class="row">
        <spring:message code="label.qasandbox.noScripts"/>
      </div>
    </c:if>

    <c:set var="scripts" value="${schema.qascripts}"/>
    <c:if test="${!empty schema.qascripts}">
      <c:if test="${countscripts == 0}">
        <c:forEach items="${schema.qascripts}" var="qascript">
          <c:set var="listScriptId" value="${qascript.scriptId}"/>
          <div class="row">
            <form:radiobutton path="scriptId" property="scriptId" value="${listScriptId}"
                              styleId="rad_${listScriptId}"/>
            <label class="question" for="rad_${listScriptId}">
                ${qascripts.shortName}
            </label>
            <span> -
                      <a href="/scripts/${scriptId}" title="label.qascript.view">
                          ${qascript.fileName}
                      </a>
                      (${qascripts.scriptType})
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
          </div>
        </c:forEach>
      </c:if>
    </c:if>
    <c:if test="${schema.doValidation == true}">
      <div class="row">
        <form:radiobutton path="scriptId" property="scriptId" value="-1" styleId="radioValidate"/>
        <label class="question" for="radioValidate"><spring:message
                code="label.qasandbox.schemaValidation"/></label>
      </div>
    </c:if>
    <c:if test="${permissions.qsiPrm}">
      <div class="row">
          <%--do/editQAScriptInSandbox?scriptId=0  titleKey="label.qasandbox.editScriptTitle"--%>
        <a href="/qaSandbox/editQAScript/0">
          <spring:message code="label.qasandbox.writeScript"/>
        </a>
      </div>
    </c:if>
    <div class="row">
      &nbsp;
    </div>
    <div class="row">
      <form:button name="runNow" class="button">
        <spring:message code="label.qasandbox.runNow"/>
      </form:button>
      <c:if test="${permissions.wqiPrm}">
        <form:button name="addToWorkqueuee" class="button">
          <spring:message code="label.qasandbox.addToWorkqueue"/>
        </form:button>
      </c:if>
    </div>
  </c:if>

  </div>
  </fieldset>
</form:form>

<c:if test="${permissions.qsuPrm}">
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