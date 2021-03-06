<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>
<div style="width:100%;">
    <tiles:insertDefinition name="AdminTabs">
        <tiles:putAttribute name="selectedTab" value="generateJWTToken"/>
    </tiles:insertDefinition>

    <ed:breadcrumbs-push label="Generate JWT Token" level="1"/>

    <form:form action="/admin/generateJWTToken/generateToken" method="post" modelAttribute="form">
        <form:errors path="*" cssClass="error-msg" element="div"/>
        <p>
            A valid JWT token will be generated in order for you to use the REST API endpoints.
        </p>
        <button type="submit" class="button">
            <spring:message code="label.admin.generateJWTToken"/>
        </button>
        <div>
            <c:if test="${not empty token}" >
                <p>The generated JWT token is:</p>
                <p style="word-wrap:break-word;"><b><c:out value="${token}" /></b></p>
            </c:if>
        </div>
    </form:form>

</div>