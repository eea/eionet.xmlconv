<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>

<html:xhtml/>
<div id="stylesheet" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div></div>	</div> 
			<ed:breadcrumbs-push label="Add Stylesheet" level="2" />
			<h4><bean:message key="label.stylesheet.add"/></h4> 

		<div class="boxcontent">

		<html:form action="/stylesheetAdd" method="post" enctype="multipart/form-data">
		<div style="padding-left:50px">
		  <table cellpadding="0" cellspacing="0" border="0">
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.schema"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
				<logic:present name="schema" scope="request">
		          <input type="text" name="schema" value="<bean:write name="schema" scope="request"/>" style="width:400px" />
		        </logic:present>
		        <logic:notPresent name="schema" scope="request">
		          <input type="text" name="schema" maxlength="255"  style="width:400px" />
		        </logic:notPresent>
		      </td>
		    </tr>
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <logic:present name="user">
		    <tr>
		      <td colspan="3">
		        <bean:message key="label.stylesheet.selectDDSchema"/>:
		      </td>
		    </tr>		    
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>		    
		    <tr>
		      <td colspan="3">		    
		          <select name="xmlSchema"  size="6" onchange="setSchema()">
									<option selected="selected" value="">
										--
									</option>		        
					<logic:iterate id="schema" name="stylesheet.DDSchemas" scope="session"  type="Schema">
									<option value="<bean:write name="schema" property="schema" />">
										<bean:write name="schema" property="schema" />
										<logic:notEqual name="schema" property="table" value="">
											&nbsp;-&nbsp;
											<bean:write name="schema" property="table" />&nbsp;(
											<bean:write name="schema" property="dataset" />)
										</logic:notEqual>										
									</option>
					</logic:iterate>
		        </select>
		       </td>
		    </tr>
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>		    
		    </logic:present>		    
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.outputtype"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>	                         
		        <select name="outputtype" style="width:100px;">
					<logic:iterate id="opt" name="stylesheet.outputtype" scope="session"  property="convTypes" type="ConvType">
						<option value="<bean:write name="opt" property="convType" />">
							<bean:write name="opt" property="convType" />
						</option>
					</logic:iterate>
		        </select>
		      </td>
		    </tr>
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		      	<bean:message key="label.stylesheet.description"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:textarea property="description"  rows="3" style="width:400px"/>
		      </td>
		    </tr>
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		        <bean:message key="label.stylesheet.xslfile"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:file property="xslfile"  />
		      </td>
		    </tr>
		    <tr>
		      <td colspan="3">&nbsp;</td>
		    </tr>
		    <tr>
		      <td colspan="3" align="center">
		        <html:submit styleClass="button">
		        	<bean:message key="label.stylesheet.upload"/>
		        </html:submit>
		        <html:cancel styleClass="button">
		        	<bean:message key="label.stylesheet.cancel"/>
		        </html:cancel>
		        
		      </td>
		    </tr>
		  </table>
		 </div>
		</html:form>
		
		</div>
		<div class="boxbottom"><div></div></div> 
	</div>
</div>

