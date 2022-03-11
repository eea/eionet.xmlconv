<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Add QA script" level="3"/>

<style>
  .borderFix {
    border-style: ridge;
  }
  .v-application > .v-application--wrap {
    min-height: auto;
  }
</style>
<script type="text/javascript" src="/static/webjars/vue/2.6.14/vue.min.js"></script>
<script type="text/javascript" src="/static/webjars/axios/0.19.0/dist/axios.min.js"></script>
<script type="text/javascript" src="/static/webjars/vuetify/2.6.1/dist/vuetify.js"></script>
<link type="text/css" href="/static/webjars/vuetify/2.6.1/dist/vuetify.min.css" rel="stylesheet">
<link type="text/css" href="/static/webjars/mdi__font/6.2.95/css/materialdesignicons.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="/static/css/vueTable.css"/>
<link href="<c:url value='/static/webjars/jquery-ui/jquery-ui.css'/>" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<script type="text/javascript" src="<c:url value='/static/webjars/jquery/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/webjars/jquery-ui/jquery-ui.js'/>"></script>
<script type="text/javascript" src="/resources/js/statusModal.js"></script>
<script type="text/javascript" src="/resources/js/scripts.js"></script>


<form:form servletRelativeAction="/scripts" method="post" enctype="multipart/form-data" modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div"/>
  <fieldset class="fieldset">
    <legend><spring:message code="label.qascript.add"/></legend>
    <div class="row" style="display:block;">
      <div class="columns small-4">
        <label class="question required" for="txtSchemaUrl">
          <spring:message code="label.qascript.schema"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input id="txtSchemaUrl" size="64" path="schema"/>
      </div>
    </div>
    <div class="row" style="display:block;">
      <div class="columns small-4">
        <label class="question" for="txtShortName">
          <spring:message code="label.qascript.shortname"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input id="txtShortName" size="64" path="shortName"/>
      </div>
    </div>
    <div class="row" style="display:block;">
      <div class="columns small-4">
        <label class="question" for="txtDescription">
          <spring:message code="label.qascript.description"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:textarea class="borderFix" rows="2" cols="30" id="txtDescription" path="description" style="width:400px"/>
      </div>
    </div>
    <div class="row" style="display:block;">
      <div class="columns small-4">
        <label class="question" for="selContentType">
          <spring:message code="label.qascript.resulttype"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select class="borderFix" path="resultType" id="selContentType">
          <form:options items="${resulttypes}" itemLabel="convType" itemValue="convType"/>
        </form:select>
      </div>
    </div>
    <div class="row" style="display:block;">
      <div class="columns small-4">
        <label class="question" for="selScriptType">
          <spring:message code="label.qascript.scripttype"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select class="borderFix" path="scriptType" id="selScriptType">
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
    <div class="row" style="display:block;">
      <div class="columns small-4">
        <label class="question required" for="txtUpperLimit">
          <spring:message code="label.qascript.upperlimit"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input id="txtUpperLimit" size="3" path="upperLimit"/>
      </div>
      <div>
        <p>
          <i class="material-icons" style="color:#00446A;font-size:18px">info</i><span style="background:#ecf4f5;">If the script is active and file size < ${form.upperLimit}, the script will be available for OnDemand QA on CDR/BDR. More details on <a target="_blank" href="https://taskman.eionet.europa.eu/projects/reportnet/wiki/Notes_on_Reportnet_2_application_integration_and_usage#End-to-end-workflow-between-the-systems-for-running-different-types-of-QA-scripts">Notes on Reportnet 2 application integration and usage</a></span>
        </p>
      </div>
    </div>
  </fieldset>

  <div id="heavyLightSection">
    <fieldset class="fieldset">
      <legend><spring:message code="label.qascript.mark.heavy.section"/></legend>
      <div class="row" style="display:block;">
        <div class="columns small-8">
          <form:radiobutton path="markedHeavy" name="heavyScript" id="light" value="false" checked="checked" onchange="hideDropdown()"/>
          <label for="light"><spring:message code="label.qascript.light"/></label>
        </div>
      </div>
      <div class="row">
        <div class="columns small-8" style="display:block;">
          <form:radiobutton path="markedHeavy" name="heavyScript" id="heavy" value="true" onchange="showDropdown()"/>
          <label for="heavy"><spring:message code="label.qascript.heavy"/></label>
        </div>
      </div>
      <div class="row" id="markedHeavyReason" style='display:none'>
        <div class="columns small-8">
          <form:select path="markedHeavyReason" id="selMarkedHeavyReason" class="borderFix">
            <form:option value="Long running" id="longRunning" selected="true">Long running script</form:option>
            <form:option value="Out of memory" id="oom">Out of memory</form:option>
            <form:option value="Other" id="other">Other</form:option>
          </form:select>
        </div>
      </div>
      <div class="row" id="markedHeavyOtherReason" style='display:none'>
        <div class="columns small-8">
          <label for="markedHeavyOtherReasonTxt"><spring:message code="label.qascript.heavy.reason"/></label>
          <form:input id="markedHeavyOtherReasonTxt" class="borderFix" path="markedHeavyReasonOther" maxlength="200"/>
        </div>
      </div>
    </fieldset>
  </div>

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
                              type='button'
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
      <button type="submit" name="add" class="button" style="color:#ECF4F5;">
        <spring:message code="label.save"/>
      </button>
    </div>
  </div>
  <div>
    <form:hidden path="schemaId"/>
    <form:hidden path="fileName"/>
    <form:hidden path="scriptRules" id="scriptRules"/>
  </div>
</form:form>

<script>
  var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
      return {
        dialog: false,
        dialogDelete: false,
        fieldValues: [
          "collection path",
          "xml file size (in MB)"
        ],
        typeValues: [
          "includes",
          "greater than",
          "smaller than"
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
        return this.editedIndex === -1 ? 'New Rule' : 'Edit Rule';
      }
    },

    watch: {
      deep: true,
      dialog(val) {
        val || this.close()
      },
      dialogDelete(val) {
        val || this.closeDelete()
      }
    },

    methods: {
      editItem(item) {
        this.editedIndex = this.scriptRules.indexOf(item);
        this.editedItem = Object.assign({}, item);
        this.dialog = true;
      },

      deleteItem(item) {
        this.editedIndex = this.scriptRules.indexOf(item);
        this.editedItem = Object.assign({}, item);
        this.dialogDelete = true;
      },

      deleteItemConfirm() {
        this.scriptRules.splice(this.editedIndex, 1);
        document.getElementById("scriptRules").value = JSON.stringify(this.scriptRules);
        this.closeDelete();
      },

      close() {
        this.dialog = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem);
          this.editedIndex = -1;
        })
      },

      closeDelete() {
        this.dialogDelete = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem);
          this.editedIndex = -1;
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
      }
    }
  })
</script>

