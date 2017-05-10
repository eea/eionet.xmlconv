<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>


<html:xhtml/>
        <ed:breadcrumbs-push label="Add Stylesheet" level="3" />
        <h1><bean:message key="label.stylesheet.add"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

        <html:form action="/stylesheetAdd" method="post" enctype="multipart/form-data">
            <table class="datatable" style="width:100%">
                <col class="labelcol"/>
                <col class="entrycol"/>
                <tr>
                    <th scope="row" class="scope-row">
                        <label class="question" for="txtSchemaUrl">
                            <bean:message key="label.stylesheet.schema"/>
                        </label>
                    </th>
                    <td>
                        <div id="newSchemasContainer">
                            <div class="newSchemaContainer">
                                <logic:present name="schema" scope="request">
                                    <input type="url" name="newSchemas" value="<bean:write name="schema" scope="request"/>" style="width:400px" class="newSchema" id="txtSchemaUrl"/>
                                </logic:present>
                                <logic:notPresent name="schema" scope="request">
                                    <input type="url" name="newSchemas" maxlength="255"  style="width:400px" class="newSchema" id="txtSchemaUrl"/>
                                </logic:notPresent>
                                <a href='#' class="delNewSchemaLink"><img style='border:0' src='<c:url value="/images/button_remove.gif" />' alt='Remove' /></a><br/>
                            </div>
                        </div>
                        <jsp:include page="ManageStylesheetSchemas.jsp"/>
                    </td>
                </tr>
                    <tr>
                        <th scope="row" class="scope-row">
                            <label class="question" for="selOutputType">
                                <bean:message key="label.stylesheet.outputtype"/>
                            </label>
                        </th>
                        <td>
                            <select name="outputtype" style="width:100px;" id="selOutputType">
                                <logic:iterate id="opt" name="stylesheet.outputtype" scope="session"  property="convTypes" type="ConvType">
                                    <c:set var="selected">
                                        <logic:equal name="opt" property="convType" value="HTML">selected="selected"</logic:equal>
                                    </c:set>
                                    <option value="<bean:write name="opt" property="convType" />" ${selected} >
                                        <bean:write name="opt" property="convType" />
                                    </option>
                                </logic:iterate>
                            </select>
                        </td>
                    </tr>

                <logic:present name="schemaInfo" scope="request">
                    <logic:equal name="schemaInfo" property="schemaLang" value="EXCEL">
                        <tr>
                            <th scope="row" class="scope-row">
                                <label class="question" for="chkDepends">
                                    <bean:message key="label.stylesheet.dependsOn"/>
                                </label>
                            </th>
                            <td>
                                <select name="dependsOn" id="chkDepends">
                                    <option value="" selected="selected">--</option>
                                    <logic:iterate id="st" scope="request" name="existingStylesheets">
                                        <option value="<bean:write name="st" property="convId" />">
                                            <bean:write name="st" property="xslFileName" />
                                        </option>
                                    </logic:iterate>
                                </select>
                            </td>
                        </tr>
                    </logic:equal>
                </logic:present>
                    <tr>
                        <th scope="row" class="scope-row">
                            <label class="question" for="txtDescription">
                                <bean:message key="label.stylesheet.description"/>
                            </label>
                        </th>
                        <td>
                            <input type="text" name="description"  style="width:400px" id="txtDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="scope-row">
                            <label class="question" for="fileXsl">
                                <bean:message key="label.stylesheet.xslfile"/>
                            </label>
                        </th>
                        <td>
                            <html:file property="xslfile"  style="width:400px" size="64" styleId="fileXsl"/>
                        </td>
                    </tr>
                    <tr>
                        <td>&#160;</td>
                        <td>
                            <html:submit styleClass="button">
                                <bean:message key="label.xsl.save"/>
                            </html:submit>
                            <html:cancel styleClass="button">
                                <bean:message key="label.stylesheet.cancel"/>
                            </html:cancel>
                        </td>
                    </tr>
                </table>
            </html:form>

