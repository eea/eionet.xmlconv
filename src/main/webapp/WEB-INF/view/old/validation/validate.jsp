<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ page import="eionet.gdem.utils.Utils,java.util.Date" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html:xhtml/>
    <div style="width:100%;">

        <ed:breadcrumbs-push label="Validate XML" level="1" />
        <h1><spring:message code="label.conversion.validate.title"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />
<logic:present name="conversion.valid" scope="request">
    <bean:size id="countErrors" name="conversion.valid" />

    <logic:equal name="countErrors" value="0">
        <logic:notEmpty name="conversion.originalSchema">
            <div class="ok-msg">The file is valid XML (<%=Utils.getDateTime(new Date())%>)
            <p><spring:message code="label.conversion.originalSchema" />&#160; <a
                href="<bean:write name="conversion.originalSchema"/>"><bean:write
                name="conversion.originalSchema" /></a></p>
            <logic:present name="conversion.validatedSchema">
                <p><spring:message code="label.conversion.validatedSchema" />&#160;
                <a href="<bean:write name="conversion.validatedSchema"/>"><bean:write
                    name="conversion.validatedSchema" /></a></p>
            </logic:present></div>
        </logic:notEmpty>
        <logic:empty name="conversion.originalSchema">
            <div class="error-msg">Could not validate XML.
            <p><spring:message code="label.conversion.schema.not.found" /></p>
            </div>
        </logic:empty>
    </logic:equal>
    <logic:notEqual name="countErrors" value="0">
        <div class="error-msg">The file is not valid XML <logic:notEmpty
            name="conversion.originalSchema">
            <p><spring:message code="label.conversion.originalSchema" />&#160; <a
                href="<bean:write name="conversion.originalSchema"/>"><bean:write
                name="conversion.originalSchema" /></a></p>
            <logic:present name="conversion.validatedSchema">
                <p><spring:message code="label.conversion.validatedSchema" />&#160;
                <a href="<bean:write name="conversion.validatedSchema"/>"><bean:write
                    name="conversion.validatedSchema" /></a></p>
            </logic:present>
        </logic:notEmpty> <logic:empty name="conversion.originalSchema">
            <p><spring:message code="label.conversion.schema.not.found" /></p>
        </logic:empty></div>
    </logic:notEqual>
    <logic:notEmpty name="conversion.warningMessage">
        <div class="error-msg">
        <bean:write name="conversion.warningMessage" />
        </div>
    </logic:notEmpty>
</logic:present>


            <form:form action="/old/validation" method="post" modelAttribute="form">
            <table class="datatable">
                <tr>
                 <th scope="col" class="scope-col">
                    <spring:message code="label.conversion.url"/>
                  </th>
                 </tr>
                 <tr>
                  <td>
                    <form:input path="xmlUrl" type="text" style="width: 40em;" />
                  </td>
                </tr>
                <tr>
                 <th scope="col" class="scope-col">
                    <spring:message code="label.conversion.xmlSchema.optional"/>
                  </th>
                 </tr>
                <tr>
                  <td>
                    <spring:message code="label.conversion.validate.note"/>
                  </td>
                </tr>
                 <tr>
                  <td>
                    <input type="text" property="schemaUrl"  style="width: 40em;" />
                  </td>
                </tr>
                <%--<logic:equal name="form" property="showSchemaSelection" value="true">--%>
                <tr>
                 <th scope="col" class="scope-col">
                    <spring:message code="label.conversion.xmlSchema"/>
                  </th>
                </tr>
                <tr>
                    <td>
                        <spring:message code="label.conversion.selectSchema"/>
                  </td>
                </tr>
                <tr>
                  <td>

<%--                    <form:select path="showSchemaSelection" name="showSchemaSelection" property="schemaUrl"  size="10">
                        <form:option value="">--</form:option>
                        <form:options collection="conversion.schemas" property="schema" labelProperty="label" />
                    </form:select>--%>
                  </td>
                </tr>
               <%--</logic:equal>--%>
                <tr>
                  <td align="center">
                    <input type="submit" styleClass="button">
                        <spring:message code="label.conversion.validate"/>
                    </input>
                  </td>
            </tr>
        </table>
    </form:form>
                <logic:present name="conversion.valid" scope="request">
                    <bean:size id="countErrors" name="conversion.valid" />
                    <logic:notEqual name="countErrors" value="0">
                        <table class="datatable" align="center" width="100%">
                            <col style="width:8%"/>
                            <col style="width:8%"/>
                            <col style="width:8%"/>
                            <col/>
                            <thead>
                                <tr>
                                    <th scope="col"><span title="Error"><spring:message code="label.table.conversion.type"/></span></th>
                                    <th scope="col"><span title="PositionLine"><spring:message code="label.table.conversion.line"/></span></th>
                                    <th scope="col"><span title="PositionCol"><spring:message code="label.table.conversion.col"/></span></th>
                                    <th scope="col"><span title="Message"><spring:message code="label.table.conversion.message"/></span></th>
                                </tr>
                            </thead>
                            <tbody>
                                <logic:iterate indexId="index" id="valid" name="conversion.valid" scope="request" type="ValidateDto">
                                    <tr <%=(index.intValue() % 2 == 1) ? "class=\"zebraeven\""
                                : "class=\"zebraodd\""%>>
                                        <td>
                                                <bean:write name="valid" property="type" />
                                        </td>
                                        <td>
                                                <bean:write name="valid" property="line" />
                                        </td>
                                        <td>
                                                <bean:write name="valid" property="column" />
                                        </td>
                                        <td>
                                                <bean:write name="valid" property="description" />
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </tbody>
                        </table>
                    </logic:notEqual>
                </logic:present>
    </div>
