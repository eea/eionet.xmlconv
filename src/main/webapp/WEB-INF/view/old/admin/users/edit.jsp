<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/vue@2.5.17/dist/vue.js"></script>

<div style="width:100%;" id="app">
  <tiles:insertDefinition name="AdminTabs">
    <tiles:putAttribute name="selectedTab" value="users"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Users" level="1"/>

  <form:form action="/admin/users" method="post" modelAttribute="form">

    <form:errors path="*" cssClass="error-msg" element="div"/>
    <%--<h3><spring:message code="label.admin.users.title"/></h3>--%>
    <div style="display: flex; flex-wrap: wrap;">
      <fieldset v-for="(group, key, groupIndex) in groups" class="fieldset" style="display: inline-block;">
      <%--<fieldset v-for="(group, key, groupIndex) in groups" class="fieldset" style="display: inline-block;">--%>
        <legend>{{ key }}</legend>
        <ul>
          <li v-for="(user, userKey, userIndex) in group">
            <input v:name="group[groupIndex].user[userIndex]" type="text" v:value="user"/>
          </li>
        </ul>
        <button class="btn btn-primary" type="button" v-on:click="add(${group.key})">ADD+</button>
      </fieldset>
    </div>
    <button type="submit" class="button" name="edit">
      <spring:message code="label.admin.users.edit"/>
    </button>
    <%--<button type="submit" class="button" name="delete">
      <spring:message code="label.admin.users.delete"/>
    </button>--%>

  </form:form>

</div>

<script>
  var app = new Vue({
    el: '#app',
    data: {
      message: 'Hello Vue!',
      users: [],
      input: [],
      groups: {}
    },
    mounted: function() {
      <c:forEach items="${groups}" var="group" varStatus="groupStatus">
        <%--this.input['${group.key}'] = {}--%>
        this.input['${group.key}'] = [];
        this.groups['${group.key}'] = [];
        <c:forEach items="${group.value}" var="user" varStatus="userStatus">
          this.groups['${group.key}'].push('${user}');
        </c:forEach>
      </c:forEach>
      this.message = "Hello Vuex!";
      // console.log(JSON.stringify(this.groups));
    },
    methods: {
      add: function(group) {
        this.input[group].users.push("");
      }
    }
  })

</script>
