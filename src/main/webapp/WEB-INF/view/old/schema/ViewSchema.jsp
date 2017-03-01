<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>

<html:xhtml/>

    <ed:breadcrumbs-push label="XML Schema or DTD" level="2" />

    <logic:notEmpty name="schemaForm" property="schema">
       <bean:define id="schemaURL" name="schemaForm" property="schema" />
       <bean:define id="id" name="schemaForm" property="schemaId" />

        <div id="tabbedmenu">

        <ul>
            <li id="currenttab">
                <span style="color: black; text-decoration: none;" title='<spring:message code="label.tab.title.schema"/>'><spring:message code="label.tab.title.schema" /></span>
            </li>
            <li>
                <html:link page="/old/schemas/schemaStylesheets?schema=${schemaURL}" titleKey="label.tab.title.xsl" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <spring:message code="label.tab.title.xsl" />
                </html:link>
            </li>
            <li>
                <html:link page="/old/schemas/schemaQAScripts?schemaId=${id}" titleKey="label.tab.title.scripts" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <spring:message code="label.tab.title.scripts" />
                </html:link>
            </li>
        </ul>
    </div>
    </logic:notEmpty>

    <h1><spring:message code="label.schema.view"/></h1>

    <%-- include Error display --%>
    <tiles:insertDefinition name="Error" />

    <logic:notEmpty name="schemaForm" property="schema">

        <logic:equal value="true"  name="schema.rootElements" property="xsduPrm" >
            <div id="operations">
              <ul>
                   <li><a href="${schemaForm.schemaId}/edit"><spring:message code="label.schema.edit.button" /></a></li>
            </ul>
            </div>
        </logic:equal>

        <fieldset><legend><spring:message code="label.schema.fldset.properties"/></legend>
          <table class="datatable">
           <col class="labelcol"/>
           <col class="entrycol"/>
            <tr>
                <th scope="row" class="scope-row">
                        <spring:message code="label.schema.url"/>
                 </th>
                  <td align="left">
                        <a href="<bean:write name="schemaForm" property="schema" />"><bean:write name="schemaForm" property="schema" /></a>
                  </td>
                </tr>
                <tr>
                    <th scope="row" class="scope-row">
                        <spring:message code="label.schema.description"/>
                  </th>
                  <td align="left">
                    <bean:write name="schemaForm" property="description" />
                  </td>
                </tr>
                <tr>
                    <th scope="row" class="scope-row">
                        <spring:message code="label.schema.language"/>
                  </th>
                  <td>
                    <bean:write name="schemaForm" property="schemaLang" />
                  </td>
                </tr>
                <tr>
                    <th scope="row" class="scope-row">
                        <spring:message code="label.schema.dovalidation"/>
                      </th>
                  <td>
                        <bean:write name="schemaForm" property="doValidation" />
                  </td>
                </tr>
                <tr>
                    <th scope="row" class="scope-row">
                        <spring:message code="label.schema.isBlockerValidation"/>
                      </th>
                  <td>
                        <bean:write name="schemaForm" property="blocker" />
                  </td>
                </tr>
                <tr>
                    <th scope="row" class="scope-row">
                        <spring:message code="label.schema.expireDate"/>
                      </th>
                  <td>
                        <bean:write name="schemaForm" property="longExpireDate" />
                  </td>
                </tr>
                <logic:equal value="true" name="schemaForm" property="dtd" >
                    <tr>
                        <th scope="row" class="scope-row">
                            <spring:message code="label.elem.dtdid"/>
                      </th>
                      <td align="left">
                            <bean:write name="schemaForm" property="dtdId" />
                      </td>
                    </tr>
                </logic:equal>
                <tr>
                    <th scope="row" class="scope-row">
                        <spring:message code="label.uplSchema.schemaFile"/>
                  </th>
                  <td>
                      <logic:notEmpty  name="schemaForm" property="uplSchemaFileName">
                        <a  href="<bean:write name="schemaForm" property="uplSchemaFileUrl" />">
                            <bean:write name="schemaForm" property="uplSchemaFileName" />
                        </a>&#160;
                        <logic:present name="schemaForm" property="lastModified">
                            &#160;&#160;(<spring:message code="label.lastmodified"/>: <bean:write property="lastModified" name="schemaForm"/>)
                        </logic:present>
                      </logic:notEmpty>
                </td>
                </tr>
          </table>
        </fieldset>
        <logic:equal name="schema.rootElements" property="rootElemsPresent" value="true">
            <fieldset><legend><spring:message code="label.schema.fldset.rootelems"/></legend>

              <table class="datatable" width="80%">
                  <thead>
                    <tr>
                        <th scope="col"><span title="Element name"><spring:message code="label.schema.table.element"/></span></th>
                        <th scope="col"><span title="Namespace"><spring:message code="label.schema.table.namespace"/></span></th>
                    </tr>
                   </thead>
                   <tbody>
                        <logic:present name="schema.rootElements" property="rootElem" >
                            <logic:iterate indexId="index" id="elem" name="schema.rootElements" property="rootElem" type="RootElem">
                                <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
                                    <td>
                                        <bean:write name="elem" property="name" />
                                    </td>
                                    <td>
                                        <bean:write name="elem" property="namespace" />
                                    </td>
                                </tr>
                            </logic:iterate>
                        </logic:present>
                   </tbody>
                </table>

            </fieldset>
        </logic:equal>
    </logic:notEmpty>
