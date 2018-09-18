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
    <div style="display: flex; flex-wrap: wrap;">
      <fieldset v-for="(group, key, groupIndex) in groups" class="fieldset" style="display: inline-block;">
        <legend>{{ key }}</legend>
        <ul>
          <li v-for="(user, userIndex) in group" style="display: flex; flex-wrap:wrap;">
            <div class="input-group">
              <input type="text" v-bind:name="'groups[' + [groupIndex] + '].users'" v-model="groups[key][userIndex]">
              <div class="input-group-button" style="height:39px;">
                <button class="button" type="button" v-on:click="del(key, userIndex)">-</button>
              </div>
            </div>
          </li>
        </ul>
        <input hidden="hidden" v-bind:name="'groups[' + [groupIndex] + '].name'" :value="key"/>
        <button class="button" type="button" v-on:click="add(key)">Add user</button>
      </fieldset>
    </div>
    <button type="submit" class="button">
      <spring:message code="label.admin.users.save"/>
    </button>

  </form:form>

</div>

<script>
  var app = new Vue({
    el: '#app',
    data: function() {
        return {
        groups: {
        <c:forEach items="${groups}" var="group" varStatus="groupStatus">
         ${group.key} : "",
        </c:forEach>
        }
      }
    },
    mounted: function() {
      <c:forEach items="${groups}" var="group" varStatus="groupStatus">
        this.groups['${group.key}'] = [];
        <c:forEach items="${group.value}" var="user" varStatus="userStatus">
          this.groups['${group.key}'].push('${user}');
        </c:forEach>
      </c:forEach>
    },
    methods: {
      add: function(group) {
        this.groups[group].push("");
        console.log(JSON.stringify(this.groups));
      },
      del: function(group, index) {
          this.groups[group].splice(index, 1);
      }
    }
  })

</script>
