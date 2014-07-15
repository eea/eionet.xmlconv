<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

        <ed:breadcrumbs-push label="Upload Schema" level="2" />
        <h1><bean:message key="label.title.uplSchema.add"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

            <html:form action="/addUplSchema" method="post" enctype="multipart/form-data">
          <table class="formtable">
           <col class="labelcol"/>
           <col class="entrycol"/>
            <tr class="zebraeven">
                    <td>
                    <label class="question required" for="txtSchemaUrl">
                        <bean:message key="label.schema.url"/>
                    </label>
                  </td>
                  <td>
                    <html:text property="schemaUrl" maxlength="255" style="width:500px" styleId="txtSchemaUrl"/>
                  </td>
                </tr>
                <tr>
                    <td>
                    <label class="question" for="txtDescription">
                        <bean:message key="label.schema.description"/>
                    </label>
                  </td>
                  <td>
                    <html:textarea property="description"  rows="2" cols="30" style="width:500px" styleId="txtDescription"/>
                  </td>
                </tr>
                <tr class="zebraeven">
                    <td>
                    <label class="question" for="txtSchemaFile">
                        <bean:message key="label.schema.add.file"/>
                    </label>
                  </td>
                  <td>
                    <html:file property="schemaFile" size="50" style="width:500px" styleId="txtSchemaFile"/>
                  </td>
                </tr>
                <tr>
                    <td>
                    <label class="question" for="txtSchemaLang">
                        <bean:message key="label.schema.language"/>
                    </label>
                  </td>
                  <td>
                    <html:select property="schemaLang"  styleId="txtSchemaLang" value="XSD">
                        <html:options property="schemaLanguages" />
                    </html:select>
                  </td>
                </tr>
                <tr class="zebraeven">
                          <td>
                    <label class="question" for="txtValidation">
                        <bean:message key="label.schema.dovalidation"/>
                    </label>
                  </td>
                  <td>
                    <html:checkbox property="doValidation"  styleId="txtValidation"/>
                  </td>
                </tr>
                <tr>
                          <td>
                    <label class="question" for="txtBlockerValidation">
                        <bean:message key="label.schema.isBlockerValidation"/>
                    </label>
                  </td>
                  <td>
                    <html:checkbox property="blockerValidation" styleId="txtBlockerValidation"/>
                  </td>
                </tr>
                <tr>
                  <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td>
                    <html:submit styleClass="button">
                        <bean:message key="label.schema.save"/>
                    </html:submit>
                    <html:cancel styleClass="button">
                        <bean:message key="label.cancel"/>
                    </html:cancel>
                  </td>
                </tr>
              </table>
            </html:form>
