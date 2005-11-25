<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html-el.tld" prefix="html-el"%>


<script type="text/javascript" src="<c:url value="/scripts/editUI.js"/>"></script>
<script type="text/javascript" src="<c:url value="/scripts/picker.js"/>"></script>
<ed:breadcrumbs-push label="Edit header" level="1" />
<div style="width:100%">
<div id="tabbedmenu">
		<ul>
		<li class="currenttab"><span style="color:black;text-decoration: none;" title="Header Design"><bean:message key="label.editUI.headereditor"/></span></li>
		<li><a style="color:black;text-decoration: none;" title="Footer Design" href="<c:url value="/do/editUI/viewFooter"/>"><bean:message key="label.editUI.footereditor"/></a></li>
		</ul>
</div>
<p>&nbsp;</p>
<h1><bean:message key="label.editUI.headerpreview"/></h1>
<br/>
<br/>
<div style="border: thin dashed #606060; padding: 1em;width:95%;">
<ed:ui-renderer id="header" enableJs="true"/>
</div>
<br/>
<br/>
<table>
	<tr>
	<td style="text-align:right;"><bean:message key="label.editUI.text"/></td>
	<td>
	<input type="text" id="vrednost" style="width:400px"/>
	<input type="radio" name="vrsta" value="text" checked="checked" onclick="switchType()"/>
	</td></tr>
	<tr>
	<td style="text-align:right;"><bean:message key="label.editUI.image"/></td>
    <td>
    <input type="text" id="slika" style="width:400px"/>
    <input type="radio" name="vrsta" value="img" onclick="switchType()"/>
    <input type="button" id="imgManager" value="Image Manager" class="button" 
    onclick="imageManage()" style="visibility: hidden" />
    </td>
    </tr>
	<tr>
    	<td style="text-align:right;"><bean:message key="label.editUI.link"/></td>
    	<td><input type="text" id="link" disabled="disabled" style="width:400px;" value="http://" />
    				<input type="checkbox" id="linkable" onclick="document.getElementById('link').disabled=!(document.getElementById('link').disabled)" />
    				&lt;-- <bean:message key="label.editUI.enable"/>
    			</td>
    		</tr>
	<tr>
    			<td style="text-align:right;"><bean:message key="label.editUI.fontSize"/></td>
    			<td><select id="size" style="width:17em;">
		    			<option value="">&nbsp;</option>
	    				<option value="xx-small">xx-small</option>
	    				<option value="x-small">x-small</option>

	    				<option value="small">small</option>
	    				<option value="medium">medium</option>
	    				<option value="large">large</option>
	    				<option value="x-large">x-large</option>
	    				<option value="xx-large">xx-large</option>
    				</select>

    			</td>
    		</tr>
    		<tr>
    			<td style="text-align:right;"><bean:message key="label.editUI.fontFamily"/></td>
    			<td><select id="tip" style="width:17em;">
			    		<option value="">&nbsp;</option>
			    		<option value="arial,sans-serif" style="font-family:arial,sans-serif">arial sans-serif</option>
			    		<option value="verdana,sans-serif" style="font-family:verdana,sans-serif">verdana sans-serif</option>

			    		<option value="courier,sans-serif" style="font-family:courier,sans-serif">courier sans-serif</option>
			    		<option value="times,sans-serif" style="font-family:times,sans-serif">times sans-serif</option>
			    		<option value="helvetica,sans-serif" style="font-family:helvetica,sans-serif">helvetica sans-serif</option>
			    		<option value="geneva,sans-serif" style="font-family:geneva,sans-serif">geneva sans-serif</option>
			    		<option value="avenir,sans-serif" style="font-family:avenir,sans-serif">avenir sans-serif</option>
			    		    		
			    		<option value="arial,cursive" style="font-family:arial,cursive">arial cursive</option>

			    		<option value="verdana,cursive" style="font-family:verdana,cursive">verdana cursive</option>
			    		<option value="courier,cursive" style="font-family:courier,cursive">courier cursive</option>
			    		<option value="times,cursive" style="font-family:times,cursive">times cursive</option>
			    		<option value="helvetica,cursive" style="font-family:helvetica,cursive">helvetica cursive</option>
			    		<option value="geneva,cursive" style="font-family:geneva,cursive">geneva cursive</option>
			    		<option value="avenir,cursive" style="font-family:avenir,cursive">avenir cursive</option>

			    		
			    		<option value="arial,monospace" style="font-family:arial,monospace">arial monospace</option>
			    		<option value="verdana,monospace" style="font-family:verdana,monospace">verdana monospace</option>
			    		<option value="courier,monospace" style="font-family:courier,monospace">courier monospace</option>
			    		<option value="times,monospace" style="font-family:times,monospace">times monospace</option>
			    		<option value="helvetica,monospace" style="font-family:helvetica,monospace">helvetica monospace</option>
			    		<option value="geneva,monospace" style="font-family:geneva,monospace">geneva monospace</option>

			    		<option value="avenir,monospace" style="font-family:avenir,monospace">avenir monospace</option>
    				</select>
    				<input type="checkbox" id="bold" /><span style="font-weight:bold">Bold</span>
    				<input type="checkbox" id="italic" /><span style="font-style:italic">Italic</span>
    				<input type="checkbox" id="underline" /><span style="text-decoration:underline">Underline</span>
    			</td>
    		</tr>

    		<tr>
    			<td style="text-align:right;"><bean:message key="label.editUI.fontColor"/></td>
    			<td><input type="text" id="boja" style="width:8em" />
    				<a title="Font Color" id="colpik" href="javascript:TCP.popup(document.getElementById('boja'))" style="visibility: visible">
    					<img width="15" height="13" border="0" alt="" src="<c:url value="/images/sel.gif"/>" />
    				</a>
    			</td>
    		</tr>

    		<tr>
    			<td style="text-align:right;"><bean:message key="label.editUI.horizontal"/></td>
    			<td>
    				<table>
    				<tr>
    				<td><input type="radio" name="pozicija" value="left" /> Left
	    			<input type="radio" name="pozicija" value="center" /> Center
	    			<input type="radio" name="pozicija" value="right" /> Right
	    			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.editUI.vertical"/>
	    			</td>
	    			<td><input type="radio" name="verticalAlign" value="top" /> Top <br/>
	    				<input type="radio" name="verticalAlign" value="middle" /> Middle <br/>
	    				<input type="radio" name="verticalAlign" value="bottom" /> Bottom <br/>
	    			</td>
	    			</tr>
	    			</table>
	    		</td>
	    	</tr>
	    		  
	</table>
    	<br />
    	
	<html:form action="/editUI/processTemplate" method="post" styleId="matrix">
		
		<html:xhtml/>
		<input type="hidden" name="template" value="header"/>
		<table>
		<tr>
    			<td>
    				<input type="button" name="set" value="    Set    " class="button" onclick="updateCell()" />&nbsp;
    				<input type="button" name="revert" value="   Revert   " class="button" onclick="document.forms[0].reset();window.location.reload();"/>&nbsp;
					<input type="button" name="save" value="    Save    " class="button" onclick="saveForm();document.forms[0].submit()" />&nbsp;
    			</td>
    		</tr>
    	</table>
    	<br /> 
		<c:forEach var="temp" items="${dynamicItemForm.map.temp}">
			<html-el:hidden property="temp(${temp.key})" styleId="${temp.key}"/>	
		</c:forEach>
		
	</html:form>
</div>
	 	