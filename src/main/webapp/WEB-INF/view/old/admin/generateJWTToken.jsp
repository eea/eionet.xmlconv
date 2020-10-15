<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>
<div style="width:100%;">
    <tiles:insertDefinition name="AdminTabs">
        <tiles:putAttribute name="selectedTab" value="generateJWTToken"/>
    </tiles:insertDefinition>

    <ed:breadcrumbs-push label="Generate JWT Token" level="1"/>

    <form:form action="/admin/generateJWTToken" method="post" modelAttribute="form">
        <form:errors path="*" cssClass="error-msg" element="div"/>
        <div style="display: flex; flex-wrap: wrap;">
            <c:if test="${not empty token}" >
                <p>The generated JWT token is:</p>
                <p style="word-wrap:break-word;"><b><c:out value="${token}" /></b></p>
            </c:if>
        </div>
        <a class="button" href="/admin/users/generateToken"><spring:message code="label.admin.generateJWTToken"/></a>
    </form:form>

</div>