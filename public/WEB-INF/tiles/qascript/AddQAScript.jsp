<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

		<ed:breadcrumbs-push label="Add QA script" level="3" />

		<h1><bean:message key="label.qascript.add"/></h1> 

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

		<html:form action="/addQAScript" method="post" enctype="multipart/form-data">
		  <table class="formtable">
			<col class="labelcol"/>
			<col class="entrycol"/>
			<tr class="zebraeven">
				<logic:present name="schema" scope="request">
		        	<td>
						<label class="question" for="txtSchemaUrl">
				        	<bean:message key="label.qascript.schema"/>
				    	</label>
					</td>
		      		<td>
						<bean:write name="schema" scope="request"/>
					</td>
				</logic:present>
				<logic:notPresent name="schema" scope="request">
		        	<td>
						<label class="question required" for="txtSchemaUrl">
				        	<bean:message key="label.qascript.schema"/>
				    	</label>
					</td>
		      		<td>
						<input type="text" name="schema" maxlength="255"  style="width:400px"  id="txtSchemaUrl" size="64"/>
					</td>
				</logic:notPresent>
		    </tr>        
			<tr>
				<td>
					<label class="question" for="txtShortName">
			        	<bean:message key="label.qascript.shortname"/>
			    	</label>
				</td>
		      <td>
				<input type="text" id="txtShortName" class="textfield" size="64" name="shortName" value="" />
		      </td>
		    </tr>
			<tr class="zebraeven">
				<td>
					<label class="question" for="txtDescription">
			      		<bean:message key="label.qascript.description"/>
			      	</label>
				</td>
		      <td>
				<textarea rows="2" cols="30" name="description" id="txtDescription" style="width:400px"></textarea>
		      </td>
		    </tr>
			<tr>
				<td>
					<label class="question" for="selContentType">
			      		<bean:message key="label.qascript.resulttype"/>
			      	</label>
				</td>
				<td>
					<html:select name="QAScriptForm" property="resultType" styleId="selContentType">
						<html:options collection="qascript.resulttypes" property="convType"/>
					</html:select>				
				</td>
			</tr>
			<tr class="zebraeven">
				<td>
					<label class="question" for="selScriptType">
			      		<bean:message key="label.qascript.scripttype"/>
			      	</label>
				</td>
				<td>
					<html:select name="QAScriptForm" property="scriptType" styleId="selScriptType">
						<html:options collection="qascript.scriptlangs" property="convType"/>
					</html:select>				
				</td>
			</tr>
		    <tr>
	    	  <td>
				<label class="question required" for="txtFile">
			        <bean:message key="label.qascript.fileName"/>
			     </label>
	    	  </td>
		      <td>
				<input type="file" name="scriptFile" id="txtFile" style="width:400px" size="64" />
		      </td>
		    </tr>
		    <tr>
	    		<td>&#160;</td>
		      <td>
		        <html:submit styleClass="button" property="action">
		        	<bean:message key="label.save"/>
		        </html:submit>
		      </td>
		    </tr>
		    <html:hidden property="schemaId"/>		  
		</table>
	</html:form>

