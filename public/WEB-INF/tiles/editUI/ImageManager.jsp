<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<title>Image Manager</title>

    <style type="text/css" media="screen">
		<!-- @import url(<c:url value="/css/main.css"/>); -->
	</style>
	<style type="text/css" media="screen">
		<!-- @import url(<c:url value="/css/portlet.css"/>); -->
	</style>

</head>
<body style="background-image: none;">

		<div style="margin-left:50px;">
			<br /><br />
			<h1>Image Manager</h1>
			<br/>
			<%-- include Error display --%>
			<tiles:insert definition="Error" />
			<br/><br/>
<html:form action="/editUI/imageManager" styleId="ImageMan" method="POST" enctype="multipart/form-data">
	<bean:message key="label.editUI.uploadPicture"/>
	<html:file property="picFile" size="40"/><br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<html:submit value="   Upload   " styleClass="button"/>&nbsp;
	<input type="button" class="button" name="cancel" value="   Cancel   " onclick="window.close()" />
	<html:hidden styleId="picToDelete" property="deletePic" value=""/>
	<br/><br/>

			<logic:present name="fileList">
			<table class="datatable" style="width:75%">
				<col style="width:30%"/>
				<col style="width:65%"/>
				<col style="width:5%"/>
				<thead>
				<tr>
					<th scope="col">Name</th>
					<th scope="col">Image (click on image to select)</th>
					<th scope="col">Action</th>
				</tr>
				</thead>
				<tbody>

				<c:forEach var="filelist" items="${fileList}" varStatus="counter">
					<tr <c:if test="${(counter.index % 2) == 1}">class="zebraeven"</c:if>>
					<td style="text-align:center;"><bean:write name="filelist"/></td>
					<td style="text-align:center;">
						<img style="cursor:crosshair;" src="<c:url value="/images/gallery/"/><bean:write name="filelist"/>"
						alt="<bean:write name="filelist"/>" onclick="window.opener.document.getElementById('slika').value=this.alt;window.close()"/>
					</td>
					<td style="text-align:center;"><input type="image" title="Delete <bean:write name="filelist"/>?" src="<c:url value="/images/delete.gif"/>"
					 onclick="if(confirm('Are you sure you want to delete <bean:write name="filelist"/>')){document.getElementById('picToDelete').value='<bean:write name="filelist"/>';document.forms[0].submit;}else{return false;}"/></td>
 				</tr>
				</c:forEach>
				</tbody>
			</table>
		</logic:present>
<br/><br/>
</html:form>
</div>
</body>
</html>
