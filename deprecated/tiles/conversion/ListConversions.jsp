<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
    <div style="width:100%;">
        <tiles:insert definition="ConverterTabs">
            <tiles:put name="selectedTab" value="convertXML" />
        </tiles:insert>

        <ed:breadcrumbs-push label="Convert XML" level="1" />
        <h1><bean:message key="label.conversion.find"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />


            <html:form action="/listConversionsByXML" method="get" >
            <table class="datatable">
                <tr>
                 <th scope="col" class="scope-col">
                    <bean:message key="label.conversion.url"/>
                  </th>
                 </tr>
                <tr>
                  <td>
                    <bean:message key="label.conversion.insertURL"/>
                  </td>
                </tr>
                 <tr>
                  <td>
                    <html:text property="url"  style="width: 40em;" />
                  </td>
                </tr>
                <logic:equal name="ConversionForm" property="showSchemaSelection" value="true">
                <tr>
                 <th scope="col" class="scope-col">
                    <bean:message key="label.conversion.xmlSchema"/>
                  </th>
                </tr>
                <tr>
                    <td>
                        <bean:message key="label.conversion.selectSchema"/>
                  </td>
                </tr>
                <tr>
                  <td>

                    <html:select name="ConversionForm" property="schemaUrl"  size="10">
                        <html:option value="">--</html:option>
                        <html:options collection="conversion.schemas" property="schema" labelProperty="label" />
                    </html:select>
                  </td>
                </tr>
               </logic:equal>
                <tr>
                  <td align="center">
                    <html:submit styleClass="button" property="searchAction">
                        <bean:message key="label.conversion.list"/>
                    </html:submit>
                  </td>
                </tr>
                </table>
                <logic:notEmpty name="ConversionForm" property="action">
                <table class="datatable">
                 <tr>
                 <th scope="col" class="scope-col">
                    <bean:message key="label.conversion.selectConversion"/>
                  </th>
                </tr>
                <logic:notEmpty name="ConversionForm" property="schemas">
                  <bean:define id="idConv" name="converted.conversionId" scope="session" type="String" />
                  <logic:empty name="idConv">
                      <bean:define id="idConv" name="ConversionForm" property="conversionId" scope="session" type="String" />
                  </logic:empty>

                <logic:iterate indexId="index" id="schema" name="ConversionForm" scope="session" property="schemas" type="Schema">
                    <tr>
                      <td align="left">
                              <strong><bean:write name="schema" property="schema"/></strong>
                              <br/>
                            <logic:iterate indexId="index" id="stylesheet" name="schema" property="stylesheets" type="Stylesheet">
                                <logic:equal name="stylesheet" property="convId" value="<%=idConv%>">
                                    <input type="radio" checked="checked" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />" value="<bean:write name="stylesheet" property="convId" />" />
                                </logic:equal>
                                <logic:notEqual name="stylesheet" property="convId" value="<%=idConv%>">
                                    <input type="radio" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />"  value="<bean:write name="stylesheet" property="convId" />" />
                                </logic:notEqual>
                                <label for="r_<bean:write name="stylesheet" property="convId" />"><bean:write name="stylesheet" property="type" />
                                &nbsp;-&nbsp;<bean:write name="stylesheet" property="description" /></label><br/>
                            </logic:iterate>
                        </logic:iterate>
                  </td>
                </tr>
                <tr>
                  <td align="center">
                    <html:submit styleClass="button" property="convertAction">
                        <bean:message key="label.conversion.convert"/>
                    </html:submit>
                  </td>
                </tr>
                </logic:notEmpty>
                <logic:empty name="ConversionForm" property="schemas">
                    <tr>
                        <td>
                            <bean:message key="label.conversion.noconversion"/>
                        </td>
                    </tr>
                </logic:empty>
              </table>
        </logic:notEmpty>
    </html:form>
</div>
