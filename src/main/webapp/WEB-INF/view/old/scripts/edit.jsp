<%@ taglib prefix="v-slot" uri="/WEB-INF/eurodyn.tld" %>
<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<style>
  .borderFix {
    border-style: ridge;
  }
  .v-application > .v-application--wrap {
    min-height: auto;
  }
</style>
<script type="text/javascript" src="/resources/js/scripts.js"></script>
<script type="text/javascript" src="/static/webjars/vue/2.6.14/vue.min.js"></script>
<script type="text/javascript" src="/static/webjars/vuetify/2.6.1/dist/vuetify.js"></script>
<link type="text/css" href="/static/webjars/vuetify/2.6.1/dist/vuetify.min.css" rel="stylesheet">
<link type="text/css" href="/static/webjars/mdi__font/6.2.95/css/materialdesignicons.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="/static/css/vueTable.css"/>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab"><span style="color: black; text-decoration: none;"> </span>
        <spring:message code="label.qascript.tab.title"/>
      </li>
      <li>
        <a href="/new/scripts/history/${form.scriptId}" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.history"/>
        </a>
      </li>
      <li>
        <a href="/new/scripts/${form.scriptId}/executionHistory" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.executionHistory"/>
        </a>
      </li>
    </ul>
  </div>
  <ed:breadcrumbs-push label="Edit QA script" level="3"/>

  <%--<h1><spring:message code="label.qascript.edit"/></h1>--%>

  <form:form action="/scripts" method="post" enctype="multipart/form-data" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset class="fieldset">
      <legend><spring:message code="label.qascript.edit"/></legend>
      <div class="row" style="display: block;">
        <div class="columns small-4">
          <label class="question">
            <spring:message code="label.qascript.schema"/>
          </label>
        </div>
        <div class="columns small-8">
            ${form.schema}
        </div>
      </div>
      <div class="row" style="display: block">
        <div class="columns small-4">
          <label class="question" for="txtShortName">
            <spring:message code="label.qascript.shortname"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:input name="form" path="shortName" id="txtShortName" size="64"/>
        </div>
      </div>
      <div class="row" style="display: block;">
        <div class="columns small-4">
          <label class="question" for="txtDescription">
            <spring:message code="label.qascript.description"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:textarea class="borderFix" path="description" rows="2" cols="30" style="width:400px" id="txtDescription"/>
        </div>
      </div>
      <div class="row" style="display: block">
        <div class="columns small-4">
          <label class="question" for="selContentType">
            <spring:message code="label.qascript.resulttype"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:select class="borderFix" name="form" path="resultType" id="selContentType">
            <form:options items="${resulttypes}" itemValue="convType" itemLabel="convType"/>
          </form:select>
        </div>
      </div>
      <div class="row" style="display: block">
        <div class="columns small-4">
          <label class="question" for="selScriptType">
            <spring:message code="label.qascript.scripttype"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:select class="borderFix" name="form" path="scriptType" id="selScriptType" disabled="false">
            <form:options items="${scriptlangs}" itemValue="convType" itemLabel="convType"/>
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
      <div class="row" style="display: block">
        <div class="columns small-4">
          <label class="question" for="txtUpperLimit">
            <spring:message code="label.qascript.upperlimit"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:input id="txtUpperLimit" size="3" path="upperLimit"/>
        </div>
      </div>
    </fieldset>

    <c:if test="${form.scriptType != 'fme'}">
      <div id="heavyLightSection">
        <fieldset class="fieldset">
          <legend><spring:message code="label.qascript.mark.heavy.section"/></legend>
          <div class="row" style="display: block">
            <div class="columns small-8">
              <form:radiobutton path="markedHeavy" name="heavyScript" id="light" value="false" checked="checked" onchange="hideDropdown()"/>
              <label for="light"><spring:message code="label.qascript.light"/></label>
            </div>
          </div>
          <div class="row" style="display:block">
            <div class="columns small-8">
              <form:radiobutton path="markedHeavy" name="heavyScript" id="heavy" value="true" onchange="showDropdown()"/>
              <label for="heavy"><spring:message code="label.qascript.heavy"/></label>
            </div>
          </div>
          <c:choose>
          <c:when test="${form.markedHeavy == true}">
          <div class="row" id="markedHeavyReason" style="display: block">
            </c:when>
            <c:otherwise>
            <div class="row" id="markedHeavyReason" style="display:none">
              </c:otherwise>
              </c:choose>
              <div class="columns small-8">
                <form:select path="markedHeavyReason" id="selMarkedHeavyReason" class="borderFix">
                  <form:option value="Long running" id="longRunning" selected="true">Long running script</form:option>
                  <form:option value="Out of memory" id="oom">Out of memory</form:option>
                  <form:option value="Other" id="other">Other</form:option>
                </form:select>
              </div>
            </div>
            <c:choose>
            <c:when test="${form.markedHeavyReason == 'Other'}">
            <div class="row" id="markedHeavyOtherReason" style="display: block">
              </c:when>
              <c:otherwise>
              <div class="row" id="markedHeavyOtherReason" style="display:none">
                </c:otherwise>
                </c:choose>
                <div class="columns small-8">
                  <label for="markedHeavyOtherReasonTxt"><spring:message code="label.qascript.heavy.reason"/></label>
                  <form:input id="markedHeavyOtherReasonTxt" class="borderFix" path="markedHeavyReasonOther" maxlength="200"/>
                </div>
              </div>
        </fieldset>
      </div>
    </c:if>

    <c:if test="${!form.markedHeavy && form.scriptType != 'fme'}">
      <div id="scriptRulesSection">
        <fieldset class="fieldset">
          <legend><spring:message code="label.qascript.rules.section"/></legend>
          <div class="row">
            <div class="columns small-8">
              <form:radiobutton path="ruleMatch" id="allRules" value="all" checked="checked"/>
              <label for="allRules"><spring:message code="label.qascript.rules.match.all"/></label>
            </div>
          </div>
          <div class="row">
            <div class="columns small-8">
              <form:radiobutton path="ruleMatch" id="atLeastOne" value="atLeastOne"/>
              <label for="atLeastOne"><spring:message code="label.qascript.rules.match.at.least.one"/></label>
            </div>
          </div>
          <div id="app">
            <v-app>
              <v-data-table
                      :headers="headers"
                      :items="scriptRules"
                      class="elevation-1"
              >
                <template v-slot:top>
                  <v-toolbar
                          flat
                  >
                    <v-toolbar-title>Rules for handling script as heavy</v-toolbar-title>
                    <v-divider
                            class="mx-4"
                            inset
                            vertical
                    ></v-divider>
                    <v-spacer></v-spacer>
                    <v-dialog
                            v-model="dialog"
                            max-width="500px"
                    >
                      <template v-slot:activator="{ on, attrs }">
                        <v-btn
                                color="primary"
                                dark
                                class="mb-2"
                                v-bind="attrs"
                                v-on="on"
                        >
                          New Rule
                        </v-btn>
                      </template>
                      <v-card>
                        <v-card-title>
                          <span class="text-h5">{{ formTitle }}</span>
                        </v-card-title>

                        <v-card-text>
                          <v-container>
                            <v-row>
                              <v-col
                                      cols="12"
                                      sm="12"
                                      md="12"
                              >
                                <v-select
                                        v-model="editedItem.field"
                                        :items="fieldValues"
                                        label="Field"
                                        @change="showProperTypeValues"
                                        :rules="[(v) => !!v || 'Field is required']"
                                ></v-select>
                              </v-col>
                              <v-col
                                      cols="12"
                                      sm="12"
                                      md="12"
                              >
                                <v-select
                                        v-model="editedItem.type"
                                        :items="typeValues"
                                        label="Type"
                                        :rules="[(v) => !!v || 'Type is required']"
                                ></v-select>
                              </v-col>
                              </v-col>
                              <v-col
                                      cols="12"
                                      sm="12"
                                      md="12"
                              >
                                <v-text-field
                                        v-model="editedItem.value"
                                        label="Value"
                                        :rules="[(v) => !!v || 'Value is required']"
                                ></v-text-field>
                              </v-col>
                              <v-col
                                      cols="12"
                                      sm="12"
                                      md="12"
                              >
                                <v-text-field
                                        v-model="editedItem.description"
                                        label="Description"
                                ></v-text-field>
                              </v-col>
                              <v-col
                                      cols="12"
                                      sm="12"
                                      md="12"
                              >
                                <v-checkbox
                                        v-model="editedItem.enabled"
                                        :label="`Enabled`"
                                ></v-checkbox>
                              </v-col>
                            </v-row>
                          </v-container>
                        </v-card-text>

                        <v-card-actions>
                          <v-spacer></v-spacer>
                          <v-btn
                                  color="blue darken-1"
                                  text
                                  @click="close"
                          >
                            Cancel
                          </v-btn>
                          <v-btn
                                  color="blue darken-1"
                                  text
                                  @click="save"
                          >
                            Save
                          </v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-dialog>
                    <v-dialog v-model="dialogDelete" max-width="500px">
                      <v-card>
                        <v-card-title class="text-h5">Are you sure you want to delete this rule?</v-card-title>
                        <v-card-actions>
                          <v-spacer></v-spacer>
                          <v-btn color="blue darken-1" text @click="closeDelete">Cancel</v-btn>
                          <v-btn color="blue darken-1" text @click="deleteItemConfirm">OK</v-btn>
                          <v-spacer></v-spacer>
                        </v-card-actions>
                      </v-card>
                    </v-dialog>
                  </v-toolbar>
                </template>
                <template v-slot:item.actions="{ item }">
                  <v-icon
                          small
                          class="mr-2"
                          @click="editItem(item)"
                  >
                    mdi-pencil
                  </v-icon>
                  <v-icon
                          small
                          @click="deleteItem(item)"
                  >
                    mdi-delete
                  </v-icon>
                </template>
              </v-data-table>
            </v-app>
          </div>
        </fieldset>
      </div>
    </c:if>

    <fieldset class="fieldset">
      <legend>Script file properties</legend>

      <div class="row" style="display: block">
        <div class="columns small-4">
          <label class="question" for="txtFile">
            <spring:message code="label.qascript.fileName"/>
          </label>
        </div>
        <div class="columns small-8">
            <%--  If scriptType is 'FME' don't show the link to the local script file --%>
          <c:if test="${form.scriptType == 'fme'}">
            <a href="/${form.filePath}" title="${form.filePath}">
                ${form.fileName}
            </a>
            &#160;&#160;&#160;&#160;&#160;&#160;(<spring:message code="label.lastmodified"/>:
            <c:choose>
              <c:when test="${form.modified}">
                ${form.modified}
              </c:when>
              <c:otherwise>
                <span style="color:red"><spring:message code="label.fileNotFound"/></span>
              </c:otherwise>
            </c:choose>
            )
          </c:if>
            <%--  If scriptType is 'FME' don't show the link to the local script file --%>
          <c:if test="${form.scriptType == 'fme'}">
            ${form.fileName}
          </c:if>
        </div>
      </div>
        <%--  If scriptType is 'FME' don't show the FileUpload --%>
      <c:if test="${form.scriptType != 'fme'}">
        <div class="row">
          <div class="columns small-4">
            &#160;
          </div>
          <div class="columns small-8">
            <button type="submit" class="button" name="upload" style="color:#ECF4F5;">
              <spring:message code="label.qascript.upload"/>
            </button>
            <input type="file" name="scriptFile" style="width:400px" size="64"/>
          </div>
        </div>
      </c:if>

      <div class="row">
        <div class="columns small-4">
          <label class="question" for="txtUrl">
            <spring:message code="label.qascript.url"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:input id="txtUrl" path="url" size="107"/>
        </div>
      </div>
      <div class="row" style="display: block">
        <div class="columns small-4">
          <label class="question" for="isActive">
            <spring:message code="label.qascript.isActive"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:checkbox path="active" id="isActive"/>
          <%--<form:hidden path="active" value="false"/>--%>
        </div>
      </div>

        <%--  If scriptType is 'FME' don't show the 'Check for updates' --%>
      <c:if test="${form.scriptType != 'fme'}">
        <div class="row">
          <div class="columns small-4">

          </div>
          <div class="columns small-8">
            <c:if test="${!empty form.fileName}">
              <button type="submit" class="button" name="diff" style="color:#ECF4F5;">
                <spring:message code="label.qascript.checkupdates"/>
              </button>
            </c:if>
          </div>
        </div>
      </c:if>
      <c:if test="${!empty form.fileName}">
        <c:if test="${form.scriptType != 'fme'}">
          <div class="row">
            <label class="question" for="txtUrl">
              <spring:message code="label.qascript.source"/>
            </label>
            <form:textarea class="borderFix" path="scriptContent" style="width: 98%;" rows="20" cols="55" id="txtFile"/>
          </div>
        </c:if>

        <button type="submit" class="button" name="update" style="color:#ECF4F5;">
          <spring:message code="label.qascript.save"/>
        </button>
        <form:hidden path="fileName"/>
        <form:hidden path="checksum" />
        <form:hidden path="scriptId" id="queryId"/>
        <form:hidden path="schemaId" />
        <form:hidden path="scriptRules" id="scriptRules"/>
        <%--<form:hidden path="active" />--%>

        <%--<input type="file" name="scriptFile" style="width:400px" size="64"/>--%>
      </c:if>
    </fieldset>
  </form:form>
</div>

<script>
  var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
      return {
        sortBy: "field",
        dialog: false,
        dialogDelete: false,
        queryId: document.querySelector("#queryId").defaultValue,
        fieldValues: [
          "collection path",
          "xml file size (in MB)",
        ],
        typeValues: [
          "includes",
          "greater than",
          "smaller than",
        ],
        headers: [
          {text: "Field", value: "field"},
          {text: "Type", value: "type", sortable: false},
          {text: "Value", value: "value", sortable: false},
          {text: "Description", value: "description", sortable: false},
          {text: "Enabled", value: "enabled", sortable: false},
          {text: "Actions", value: "actions", sortable: false},
        ],
        scriptRules: [],
        editedIndex: -1,
        editedItem: {
          id: '',
          field: '',
          type: '',
          value: '',
          description: '',
          enabled: false
        },
        defaultItem: {
          id: '',
          field: '',
          type: '',
          value: '',
          description: '',
          enabled: false
        },
      };
    },
    computed: {
      formTitle() {
        return this.editedIndex === -1 ? 'New Rule' : 'Edit Rule'
      },
    },

    watch: {
      deep: true,
      dialog(val) {
        val || this.close()
      },
      dialogDelete(val) {
        val || this.closeDelete()
      },
    },

    methods: {
      editItem(item) {
        this.editedIndex = this.scriptRules.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.dialog = true
      },

      deleteItem(item) {
        this.editedIndex = this.scriptRules.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.dialogDelete = true
      },

      deleteItemConfirm() {
        this.scriptRules.splice(this.editedIndex, 1);
        document.getElementById("scriptRules").value = JSON.stringify(this.scriptRules);
        this.closeDelete();
      },

      close() {
        this.dialog = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem)
          this.editedIndex = -1
        })
      },

      closeDelete() {
        this.dialogDelete = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem)
          this.editedIndex = -1
        })
      },

      save() {
        if (this.editedIndex === -1) {
          this.scriptRules.push(this.editedItem);
        } else {
          this.scriptRules[this.editedIndex] = this.editedItem;
        }
        this.scriptRules = this.scriptRules.slice();
        document.getElementById("scriptRules").value = JSON.stringify(this.scriptRules);
        this.close();
      },

      showProperTypeValues() {
        if (this.editedItem.field == 'collection path'){
          this.typeValues = ["includes"];
        } else if (this.editedItem.field == 'xml file size (in MB)') {
          this.typeValues = [
            "greater than",
            "smaller than"
          ];
        }
      },

      //Reading data from API method.
      readRuleEntries() {
        this.scriptRules = JSON.parse(document.getElementById("scriptRules").value);
      }
    },

    //this will trigger in the onReady State
    mounted() {
      this.readRuleEntries();
    }
  })
</script>