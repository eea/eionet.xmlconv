<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html:xhtml/>

<ed:breadcrumbs-push label="Stylesheets" level="1" />



<logic:present name="stylesheet.stylesheetListHolder">

    <logic:equal value="true"  name="stylesheet.permissions" property="ssiPrm" >
        <div id="operations">
            <ul>
                <li><a href="addStylesheetForm"><spring:message code="label.stylesheet.add" /></a></li>
            </ul>
        </div>
    </logic:equal>

    <h1 class="documentFirstHeading">
        <spring:message code="label.stylesheet.handcoded"/>
    </h1>

    <%-- include Error display --%>
    <tiles:insertDefinition name="Error" />

    <div class="visualClear">&nbsp;</div>


    <logic:present name="stylesheet.stylesheetListHolder" property="stylesheetList" >
        <form:form action="/deleteStylesheet" method="post">

            <table id="tbl_stylesheets" class="display datatable" width="100%">
                <logic:equal value="true" name="stylesheet.permissions" property="ssdPrm" >
                    <col style="width:5%"/>
                </logic:equal>
                <col/>
                <col/>
                <col/>
                <col style="width:140px"/>
                <thead>
                    <tr>
                        <logic:equal value="true" name="stylesheet.permissions" property="ssdPrm" >
                            <th scope="col" class="scope-col"></th>
                        </logic:equal>
                        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.file"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.title"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.type"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.modified"/></th>
                        <th style="display:none"/>
                    </tr>
                </thead>
                <tbody>
                    <logic:iterate indexId="index" id="stylesheet" name="stylesheet.stylesheetListHolder" property="stylesheetList" type="Stylesheet">
                    <bean:define id="stylesheetId" name="stylesheet" property="convId" />
                    <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                        <logic:equal value="true" name="stylesheet.permissions" property="ssdPrm" >
                            <td style="text-align:center">
                                <input type="radio" name="conversionId" value="${stylesheetId}" ></input>
                            </td>
                        </logic:equal>
                        <td>
                            <%--paramId="stylesheetId" paramName="stylesheet" paramProperty="convId"--%>
                            <html:link page="/old/conversions/${stylesheetId}" title="View stylesheet" >
                                <bean:write name="stylesheet" property="xslFileName" />
                            </html:link>&#160;
                        </td>
                        <td>
                            <bean:write name="stylesheet" property="description" />
                        </td>
                        <td>
                            <bean:write name="stylesheet" property="type" />
                        </td>
                        <td style="font-size:0.8em;">
                            <bean:write name="stylesheet" property="modified" />
                        </td>
                        <td style="display:none">
                            <bean:write name="stylesheet" property="lastModifiedTime" format="yyyy-MM-dd HH:mm:ss"/>
                        </td>
                    </tr>
                    </logic:iterate>
                </tbody>
            </table>
            <logic:equal value="true"  name="stylesheet.permissions" property="ssdPrm" >
                <br/>
                <div class="boxbottombuttons">
                       <input type="button"  class="button" value="<spring:message code="label.schema.delete"/>" onclick="return submitAction(1,'deleteStylesheet');" />
                   </div>
               </logic:equal>
        </form:form>
    </logic:present>
    <logic:notPresent name="stylesheet.stylesheetListHolder" property="stylesheetList" >
        <div class="advice-msg">
            <spring:message code="label.stylesheet.noHandCodedConversions"/>
        </div>
    </logic:notPresent>

    <div class="visualClear">&nbsp;</div>



</logic:present>
