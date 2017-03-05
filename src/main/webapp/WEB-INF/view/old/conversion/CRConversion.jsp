<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %><%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
    <div style="width:100%;">
        <tiles:insert definition="ConverterTabs">
            <tiles:put name="selectedTab" value="searchXML" />
        </tiles:insert>

        <ed:breadcrumbs-push label="Search CR for XML files" level="1" />
        <h1><spring:message code="label.conversion.crconversion.title"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

            <form:form action="/searchCR" method="get">
            <table class="formtable">
                <tr>
                 <th scope="col" class="scope-col">
                    <spring:message code="label.conversion.xmlSchema"/>
                  </th>
                </tr>
                <tr>
                  <td>
                    <html:select name="ConversionForm" property="schemaUrl"  size="10">
                        <html:option value="">--</html:option>
                        <html:options collection="conversion.schemas" property="schema" labelProperty="label" />
                    </html:select>

                  </td>
                </tr>
                <tr>
                  <td align="center">
                    <html:submit styleClass="button">
                        <spring:message code="label.conversion.searchXML"/>
                    </html:submit>
                  </td>
                </tr>
               </table>
            </form:form>
            <!--  Show XML files -->
            <logic:present name="ConversionForm" property="schema">
            <bean:define id="schema"  name="ConversionForm" property="schema" />
              <bean:size name="schema" id="countfiles" property="crfiles"/>
              <bean:define id="crfiles" name="schema" property="crfiles"/>

            <form:form action="/testConversion" method="post" >
            <table class="datatable">
                <tr>
                 <th scope="col" class="scope-col">
                    <spring:message code="label.conversion.CRxmlfiles"/> (<bean:write name="countfiles"/>)
                  </th>
                </tr>

                          <bean:define id="selUrl" value="" type="String" />
                          <logic:notEmpty name="converted.url" scope="session">
                              <bean:define id="selUrl" name="converted.url" scope="session" type="String" />
                         </logic:notEmpty>


                          <logic:greaterThan name="countfiles" value="0">

                          <tr>
                          <td>
                            <html:select name="ConversionForm" property="url"  size="10">
                                <html:option value="">--</html:option>
                                <html:options collection="crfiles" property="url" labelProperty="label" />
                            </html:select>

                          </td>
                         </tr>
                        </logic:greaterThan>
                     <logic:equal name="countfiles" value="0">
                         <tr>
                          <td>
                                <spring:message code="label.conversion.noCRFiles"/>
                          </td>
                         </tr>
                        <tr>
                              <td>
                                <input type="text" name="url" style="width: 30em;" value="<%=selUrl %>" ></input>
                              </td>
                        </tr>
                    </logic:equal>
                    <tr>
                      <td>
                            <html:hidden name="ConversionForm" property="schemaUrl"/>
                            <html:hidden name="ConversionForm" property="errorForward" value="errorCR" />
                      </td>
                    </tr>
                    <tr>
                     <th scope="col" class="scope-col">
                        <spring:message code="label.conversion.selectConversion"/>
                      </th>
                    </tr>

                      <bean:define id="idConv" name="converted.conversionId" scope="session" type="String" />
                      <logic:empty name="idConv">
                          <bean:define id="idConv" name="ConversionForm" property="conversionId" scope="session" type="String" />
                      </logic:empty>
                <tr>
                  <td align="left">
                        <logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">
                                <logic:equal name="stylesheet" property="convId" value="<%=idConv%>">
                                    <input type="radio" checked="checked" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />" value="<bean:write name="stylesheet" property="convId" />" />
                                </logic:equal>
                                <logic:notEqual name="stylesheet" property="convId" value="<%=idConv%>">
                                    <input type="radio" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />"  value="<bean:write name="stylesheet" property="convId" />" />
                                </logic:notEqual>
                                <label for="r_<bean:write name="stylesheet" property="convId" />"><bean:write name="stylesheet" property="type" />
                                &nbsp;-&nbsp;<bean:write name="stylesheet" property="description" /></label><br/>
                        </logic:iterate>
                  </td>
                </tr>
                <tr>
                  <td align="center">
                          <bean:size name="schema" id="count" property="stylesheets"/>
                          <logic:greaterThan name="count" value="0">
                            <html:submit styleClass="button">
                                <spring:message code="label.conversion.convert"/>
                            </html:submit>
                        </logic:greaterThan>
                        <logic:equal name="count" value="0">
                        <p style="color: red; font-weight: bold;"><spring:message code="label.conversion.noconversion"/></p>
                       </logic:equal>
                  </td>
                </tr>
            </table>
        </form:form>
    </logic:present>
</div>
