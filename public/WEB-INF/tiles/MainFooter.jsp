<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java"%>
<%@ page import="eionet.gdem.web.filters.EionetCASFilter" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>

<% String a=request.getContextPath(); session.setAttribute("webRoot",a==null?"":a); %>
<logic:present name="user">
	<bean:define id="username" name="user" scope="session"/>
</logic:present>

							</div>
							
						</div>
					</div>
				</div>
				<!-- end of main content block -->
				<ed:breadcrumbs-list htmlid="portal-breadcrumbs" classStyle="breadcrumbSep" classStyleEnd="breadcrumbEnd" delimiter="&nbsp;" />
				<!-- start of the left (by default at least) column -->
				<div id="portal-column-one">
					<div class="visualPadding">
						<div class="portlet" style="text-align: center;">
							<h5>Contents</h5>
							<div class="portletBody">
								<div class="portletContent odd">
	   								<div>
	   								
										<ul class="portal-subnav">	
											<ed:menuItem action="/do/uplSchemas" title="Schemas">
												<bean:message key="label.menu.schemas"/>
											</ed:menuItem>
											<ed:menuItem action="/do/stylesheetList" title="Stylesheets">
												<bean:message key="label.menu.stylesheets"/>
											</ed:menuItem>
											<ed:menuItem action="/do/listConvForm" title="Converter">
												<bean:message key="label.menu.converter"/>
											</ed:menuItem>
											<ed:menuItem action="/workqueue.jsp" title="QA jobs">
												<bean:message key="label.menu.QAJobs"/>
											</ed:menuItem>
											<ed:menuItem action="/queriesindex.jsp" title="Queries">
												<bean:message key="label.menu.queries"/>
											</ed:menuItem>
											<ed:menuItem action="/sandbox.jsp" title="XQ Sandbox">
												<bean:message key="label.menu.xqsendbox"/>
											</ed:menuItem>
											<ed:hasPermission username="username" acl="host" permission="v">
												<ed:menuItem action="/do/hosts/list"  title="Hosts">
													<bean:message key="label.menu.hosts"/>
												</ed:menuItem>
											</ed:hasPermission>
											<ed:hasPermission username="username" acl="config" permission="u">
												<ed:menuItem action="/do/ldapForm"  title="Edit application configurations">
													<bean:message key="label.menu.config"/>
												</ed:menuItem>
											</ed:hasPermission>
											<ed:menuItem action="#" onclick="javascript:openWindow(applicationRoot+'/help/index.jsp','olinehelp');" title="Help">
												<bean:message key="label.menu.help"/>
											</ed:menuItem>
										</ul>

									</div>
								</div>
							</div>
						</div>
						<div class="visualClear"> </div>
	
						<logic:notPresent name="user">
						<div class="portlet" style="text-align: center;">
							<div>
								<h5>
								<span><bean:message key="label.menu.notlogged"/></span>
								</h5>								
								<div class="portletBody">
									<div class="portletContent odd">									
										<ul class="portal-subnav">										
											<li><a href="<%=EionetCASFilter.getCASLoginURL(request)%> " title="login">Login</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>						
						</logic:notPresent>
						
						<logic:present name="user">
						<div class="portlet" style="text-align: center;">
							<div>
								<h5>
								<span><bean:message key="label.menu.logged"/></span>
								<br/>
								<span>
								<bean:write name="user" scope="session"/>
								</span>								
								</h5>
								<div class="portletBody">
									<div class="portletContent odd">									
										<ul class="portal-subnav">										
											<li><a href="<c:url value="/do/start?logout"/>" title="Logout">Logout</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>						
						</logic:present>
						
						<div class="visualClear"> </div>
						<div>
							<div class="portlet" style="text-align: center;">
								<h5>Reportnet </h5>
								<div class="portletBody">
									<div class="portletContent odd">
										<ul class="portal-subnav">
											<li><a title="Reporting Obligations" href="http://rod.eionet.europa.eu/">ROD</a></li>
											<li><a title="Central Data Repository" href="http://cdr.eionet.europa.eu/">CDR</a></li>
											<li><a title="Data Dictionary" href="http://dd.eionet.europa.eu/">DD</a></li>
											<li><a title="Content Registry" href="http://cr.eionet.europa.eu/">CR</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>					
					</div>
				</div>
				<!-- end of the left (by default at least) column -->
			</div>
			<!-- end of the main and left columns -->
			<!-- start of right (by default at least) column -->			
			<!-- end of the right (by default at least) column -->
			<div class="visualClear">&nbsp;</div>
		

<tiles:useAttribute id="showFooter" name="showFooter"/>
<logic:equal name="showFooter" value="true">
	<div id="portal-footer">
		<div style="text-align: center">
			<a href="http://eea.europa.eu/">European Environment Agency</a><br/>
			<span style="font-style:italic">Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark - Phone: +45 3336 7100</span>
		</div>
	</div>
</logic:equal>


		</div>
		<!-- end column wrapper -->

	</div>
	<!-- end portal-top -->
	
</div>
<!-- end visual-portal-wrapper -->
