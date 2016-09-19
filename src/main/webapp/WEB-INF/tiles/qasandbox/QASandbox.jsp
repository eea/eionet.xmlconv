<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .dropzone .dz-preview .dz-progress {
        height: 20px;
        border: 1px solid #aaa;
        max-width: 600px;
        margin-bottom: 5px;
    }
    .dz-processing .dz-progress {
        display:block;
    }
    .dz-complete .dz-progress {
        display:none;
    }
    .dropzone .dz-preview .dz-progress .dz-upload {
        display: block;
        height: 100%;
        width: 0;
        background: green; }
    .dropzone .dz-preview .dz-error-message {
        color: red;
        display: none;
    }
    .dz-remove { display:none; }
    .dz-success-mark { display:none; }
    .dz-error-mark { display:none; }
    .dz-details { margin-top: 20px; }
    #clickable {
        margin-top: -85px;
    }
    @media screen and (-webkit-min-device-pixel-ratio:0) {
        #clickable {
            margin-top: -62px;
        }
    }
</style>
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
        </table>
    </html:form>


    <html:form action="/executeSandboxAction" method="post">
        <table class="formtable">
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
        </table>
        </html:form>

            <%-- Insert URL manually --%>
            <logic:notPresent name="schema" property="crfiles">
                <html:form action="/executeSandboxAction" method="post">
                <table class="formtable">
                <tr class="zebraeven">
                    <td>
                         <label class="question" for="txtSourceUrl">
                            <bean:message key="label.qasandbox.sourceUrl" />
                        </label>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <html:text property="sourceUrl" styleId="txtSourceUrl" size="120" style="max-width: 780px;"/>
                            </td>
                 </tr>
                 <tr>
                    <td>
                        <html:submit styleClass="button" property="action">
                            <bean:message key="label.qasandbox.extractSchema"/>
                        </html:submit>
                    </td>
                 </tr>
                 <tr>
                   <td>&nbsp;</td>
                 </tr>
                </table>
                </html:form>
                <logic:equal value="true" name="qascript.permissions" property="qsuPrm" >
                    <button style="float:right;" id="clickable">Upload file</button>
                    <form action="/qasandbox/upload" id="my-dropzone" class="dropzone"></form>
                <script type="text/javascript" src="<c:url value="/scripts/dropzone.min.js"/>"></script>

                <script id="mypreview" type="text/template">
                    <div class="dz-preview dz-file-preview">
                        <div class="dz-details">
                            <div class="dz-size" data-dz-size></div>
                            <div class="dz-filename">
                                <span data-dz-name></span>
                            </div>
                            <div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div>
                            <button class="dz-remove-button" type="button" data-dz-remove>Remove</button>
                            <button class="dz-select-button" type="button">Select</button>
                        </div>
                        <div class="dz-success-mark"><span>✔</span></div>
                        <div class="dz-error-mark"><span>✘</span></div>
                        <div class="dz-error-message"><span data-dz-errormessage></span></div>
                    </div>
                </script>

                <script>
                    $(document).on('click', '.dz-filename span', function(event) {
                        for (var index = 0; index < Dropzone.forElement("#my-dropzone").files.length; index++) {
                            if (Dropzone.forElement("#my-dropzone").files[index].name == $(this).text()) {
                                $("#txtSourceUrl").val(Dropzone.forElement("#my-dropzone").files[index].url)
                            }
                        }
                    });
                    $(document).on('click', '.dz-select-button', function(event) {
                        for (var index = 0; index < Dropzone.forElement("#my-dropzone").files.length; index++) {
                            if (Dropzone.forElement("#my-dropzone").files[index].name == $(this).siblings('.dz-filename').children('span').text()) {
                                $("#txtSourceUrl").val(Dropzone.forElement("#my-dropzone").files[index].url)
                            }
                        }
                    });
                    Dropzone.options.myDropzone = {
                        dictDefaultMessage: "",
                        url: "/qasandbox/upload",
                        clickable: "#clickable",
                        acceptedFiles: ".xml, .gml",
                        maxFiles: "5",
                        maxFilesize: "300",
                        createImageThumbnails: "false",
                        addRemoveLinks: "false",
                        previewTemplate: document.getElementById("mypreview").innerHTML,
                        init: function() {
                            $.getJSON("/qasandbox/action?command=getFiles", function(data) {
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
                            this.on("uploadprogress", function(file, progress, bytesSent) {
                                //console.log("Progress :" + progress);
                                //$('.dz-upload').text(Math.round(progress) + "%")
                            });
                            this.on("removedfile", function(file) {
                               $.get("/qasandbox/action", { command: "deleteFile", filename: file.name });
                            });
                        }
                    };
                </script>
                </logic:equal>
            </logic:notPresent>
    <html:form action="/executeSandboxAction" method="post">
        <table class="formtable">
        <%-- QA script type & content --%>
        <logic:equal name="QASandboxForm" property="showScripts" value="false">
            <logic:equal value="true"  name="qascript.permissions" property="qsiPrm" >
            <tr>
                <td>&nbsp;</td>
            </tr>
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
                      <html:select property="scriptType" styleId="selScriptType" disabled="false">
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
                        <logic:equal value="true"  name="qascript.permissions" property="wqiPrm" >
                            <html:submit styleClass="button" property="action">
                                <bean:message key="label.qasandbox.addToWorkqueue"/>
                            </html:submit>
                        </logic:equal>
                        <%--  Save content to file --%>
                        <logic:equal value="true"  name="qascript.permissions" property="wquPrm" >
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
                                        <logic:equal value="true" name="qascript.permissions" property="qsuPrm" >
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
                <logic:equal value="true"  name="qascript.permissions" property="qsiPrm" >
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
                        <logic:equal value="true"  name="qascript.permissions" property="wqiPrm" >
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
