<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

        <ed:breadcrumbs-push label="View stylesheet" level="3" />

        <div id="operations">
              <ul>
                   <li>
                       <a href="searchCR?conversionId=<bean:write name="stylesheetForm" property="stylesheetId" />&amp;schemaUrl=<bean:write name="stylesheetForm" property="schema" />">
                        <bean:message key="label.stylesheet.run" />
                    </a>
                </li>
                <logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
                    <li>
                        <html:link page="/do/stylesheetEditForm" paramId="stylesheetId" paramName="stylesheetForm" paramProperty="stylesheetId" title="edit stylesheet" >
                            <bean:message key="label.stylesheet.edit" />
                        </html:link>
                    </li>
                       <li>
                        <a href="deleteStylesheet?conversionId=<bean:write name="stylesheetForm" property="stylesheetId" />&amp;schema=<bean:write name="stylesheetForm" property="schema" />" title="delete stylesheet" onclick='return stylesheetDelete("<bean:write name="stylesheetForm" property="xsl" />");'>
                            <bean:message key="label.stylesheet.delete" />
                        </a>
                    </li>
                </logic:equal>
            </ul>
        </div>


        <h1><bean:message key="label.stylesheet.view"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

          <table class="datatable">
            <col class="labelcol"/>
            <col class="entrycol"/>
            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.stylesheet.schema"/>
                </th>
                  <td>
                    <a  href="<bean:write name="stylesheetForm" property="schema" />" title="<bean:write name="stylesheetForm" property="schema" />">
                        <bean:write name="stylesheetForm" property="schema" />
                    </a>&#160;
                </td>
            </tr>
            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.stylesheet.outputtype"/>
                </th>
              <td>
                <bean:write name="stylesheetForm" property="outputtype" />
              </td>
            </tr>


            <logic:present name="schemaInfo" scope="request">
            <logic:equal name="schemaInfo" property="schemaLang" value="EXCEL">

                <bean:define id="depOn" name="stylesheetForm" property="dependsOn" scope="request" type="java.lang.String" />
                <tr>
                <th scope="row" class="scope-row">
                        <bean:message key="label.stylesheet.dependsOn"/>
                    </th>
                    <td>
                         <logic:iterate id="st" scope="request" name="existingStylesheets">
                             <logic:equal name="st" property="convId" value="<%=depOn %>">
                                 <bean:write name="st" property="xslFileName" />
                             </logic:equal>
                         </logic:iterate>
                        <bean:write name="stylesheetForm" property="dependsOn" />

                     </td>
                </tr>

            </logic:equal>
            </logic:present>


            <tr>
                <th scope="row" class="scope-row">
                      <bean:message key="label.stylesheet.description"/>
                </th>
              <td>
                  <bean:write name="stylesheetForm" property="description"/>
              </td>
            </tr>
            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.stylesheet.xslfile"/>
                </th>
              <td>
                    <a  href="<bean:write name="webRoot"/>/<bean:write property="xsl" name="stylesheetForm"/>" title="<bean:write property="xsl" name="stylesheetForm"/>">
                                <bean:write property="xslFileName" name="stylesheetForm"/>
                    </a>
                    &#160;&#160;&#160;&#160;&#160;&#160;(<bean:message key="label.lastmodified"/>:
                    <logic:present name="stylesheetForm" property="modified">
                        <bean:write property="modified" name="stylesheetForm"/>
                    </logic:present>
                    <logic:notPresent name="stylesheetForm" property="modified">
                        <span style="color:red"><bean:message key="label.fileNotFound"/></span>
                    </logic:notPresent>
                    )
              </td>
            </tr>
          </table>
          <logic:present name="stylesheetForm" property="xslFileName">
              <pre><bean:write name="stylesheetForm" property="xslContent"/></pre>
        </logic:present>
