<%@ page contentType="text/html" import="org.springframework.security.authentication.UsernamePasswordAuthenticationToken" %>
<%@ page contentType="text/html" import="com.infor.cloudsuite.platform.security.SecurityUser" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
	final UsernamePasswordAuthenticationToken userPrincipal = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
    final SecurityUser principal = (SecurityUser) userPrincipal.getPrincipal();	
%>
<!DOCTYPE HTML>
<html>
<head>
	<title>
		Infor Business Cloud
	</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge" >
	<meta name="viewport" content="width=960;initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/> 
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">	
	<link rel="icon" type="image/ico" href="css/img/favicon.ico"/>
	<link href ='css/app.css' rel='stylesheet' type='text/css'>
	<link href ='css/dialogbox.css' rel='stylesheet' type='text/css'>
	<link href ='css/store.css' rel='stylesheet' type='text/css'>
	<link href ='css/masonry.css' rel='stylesheet' type='text/css'>
	<link href ='css/tile-bg.css' rel='stylesheet' type='text/css'>
	<link href ='css/button.css' rel='stylesheet' type='text/css'>	
	<link href ='css/new.button.min.css' rel='stylesheet' type='text/css'>
	<link href ='css/jqueryui/aristo/jquery.ui.aristo.css' rel='stylesheet' type='text/css'>
	<link href ='css/admin.css' rel='stylesheet' type='text/css'>
	<!-- App lib js --->
	<script src="js/lib.jquery.js"></script>
	<script src="js/lib.jquery.ui.js"></script>
	<script src="js/lib.underscore.js"></script>
	<script src="js/lib.backbone.js"></script>	
	<script src="js/lib.json2.js"></script>
	<script src="js/lib.masonry.js"></script>
	<script src="js/lib.sugar.js"></script>
	<!--[if lt IE 10]>
		<script src="js/lib.pie.js"></script>
	<![endif]-->	
	<script src="js/lib.i18n.js"></script>
	<script src="js/lib.dialogbox.js"></script>	
	<script src="js/lib.async.js"></script>	
	<script src="js/lib.timer.js"></script>
	<% if(principal.isSuperAdmin() || principal.isAdmin()){ %>		
	<!--[if lt IE 9]><script language="javascript" type="text/javascript" src="js/dashboard/excanvas.min.js"></script><![endif]-->	
	<script type="text/javascript" src="js/dashboard/jquery.jqplot.min.js"></script>
	<script type="text/javascript" src="js/dashboard/plugins/jqplot.barRenderer.min.js"></script>
	<script type="text/javascript" src="js/dashboard/plugins/jqplot.categoryAxisRenderer.min.js"></script>
	<script type="text/javascript" src="js/dashboard/plugins/jqplot.pointLabels.min.js"></script>
	<script type="text/javascript" src="js/dashboard/plugins/jqplot.pieRenderer.min.js"></script>
	<script type="text/javascript" src="js/dashboard/plugins/jqplot.donutRenderer.min.js"></script>	
	<script type="text/javascript" src="js/dashboard/plugins/jqplot.bubbleRenderer.min.js"></script>	
	<link rel="stylesheet" type="text/css" href="js/dashboard/jquery.jqplot.min.css" />	
	<% } %>	
	<!-- App js --->
	<script src="js/app.toc.js"></script>	
	<script src="js/app.i18n.js"></script>
	<% if(principal.isInfor24Admin()){ %>
			<script src="js/app.infor24.js"></script>
	<% } else {%>	
			<% if(!principal.isExternalUser()){ %><script src="js/app.admin.js"></script><% } %>	
			<script src="js/app.cloud.js"></script>	
	<% } %>		
</head>
<body class="<% if(principal.isInfor24Admin()){ %>infor24<% } %>">
	<!-- App User Vars ---->
	<input type="hidden" id="i18n-lang" 			value="<%= principal.getLanguage().toString() %>"  						/>
	<input type="hidden" id="user-full-name" 		value="<%= principal.getFirstName() %> <%= principal.getLastName() %>" 	/>	
	<input type="hidden" id="user-email-address"	value="<%= principal.getUsername() %>" 									/>
	<input type="hidden" id="scheme" 				value="<%= request.getScheme() %>" 										/>
	<input type="hidden" id="servName" 				value="<%= request.getServerName() %>" 									/>
	<input type="hidden" id="conRoot" 				value="<%= request.getContextPath() %>" 								/>
	<input type="hidden" id="ur" 					value="<% if(principal.isSuperAdmin()){ %>sad<% } %><% if(principal.isAdmin()){ %>adm<% } %><% if(principal.isSales()){ %>int<% } %><% if(principal.isExternalUser()){ %>ex<% } %>" />
	
	<!-- App HTML --->
	<div id="main-app-container" class="main-app  "> <!--  large-screen --->
		<div class="left-container">
			<div class="left-mini-container">
				<div class="left-user-box" id="account-settings" data-link-page="User Settings">
					<span class="user-drop-arrow"></span>
					<span class="user-name-text"><%= principal.getUsername() %></span>
				</div>
				<div class="left-header"></div>				
				<div class="left-menu">
					<% if(!principal.isInfor24Admin()){ %>
						<div class="btn btn-success btn-large disabled" id="global-deploy">Deploy</div>
						
						<% if(principal.isSuperAdmin() || principal.isAdmin()){ %>					
						<div class="left-menu-top">
							<div class="left-menu-item" id="dashboard-link">Dashboard</div>				
						</div>
						<% } %>				
						
						<div class="left-menu-top">
							<div class="left-menu-item" id="deployments-link">Deployments</div>					
						</div>					

						<div class="left-menu-big-list">
							<div class="left-menu-item" id="instances-link" >Instances</div>							
						</div>	
						
						<% if(principal.isSuperAdmin() || principal.isAdmin() || principal.isSales()){ %>					
						<div class="left-menu-top">
							<div class="left-menu-item" id="users-link">Users</div>				
						</div>
						<% } %>			
						
						<div class="left-menu-top">
							<div class="left-menu-item" id="marketplace-link" >Marketplace</div>					
						</div>		
					<% } %>
					<% if(principal.isInfor24Admin()){ %>					
						<div class="left-menu-top">
							<div class="left-menu-item" id="i24-customers-link">Customers</div>				
						</div>
				
						<div class="left-menu-top">
							<div class="left-menu-item" id="i24-environments-link">Environments</div>				
						</div>
					
						<div class="left-menu-top">
							<div class="left-menu-item" id="i24-instances-link">Instances</div>				
						</div>
				
						<div class="left-menu-top">
							<div class="left-menu-item" id="i24-jobs-link">Jobs</div>				
						</div>
					
						<div class="left-menu-top">
							<div class="left-menu-item" id="i24-tickets-link">Tickets</div>				
						</div>
					
						<div class="left-menu-top">
							<div class="left-menu-item" id="i24-config-link">Config</div>				
						</div>
					<% } %>						
					
					<div class="left-menu-divider" style="border-top: 1px solid #ccc;">&nbsp;</div>
					
					<div style="clear:both;"></div>
				</div>
			</div>
		</div>
		<div class="top-content-container">
			<div id="top-content-slider" style="left:0px;">		
				<div class="top-slide" id="top-slide-1">		
					<div class="top-content-main">
						<div class="header-icon-block"></div>
						<div class="header-text">Marketplace</div>
					</div>
				</div>
			</div>
		</div>
		<div class="content-container">
			<div id="content-slider" style="left:0;">
				<div class="slide" id="slide-0">
					<div class="content-main">
						<div class="main-view-content" id="tile-container">
						
						</div>
					</div>
				</div>
				<div class="slide" id="slide-1" style="display:none;">
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view" >
								<div class="app-box-header black-gradient">
									<div class="app-box-preview"><!-- bg-pic-2 --->
										<div class="box-owned-flag"></div>
										<div class="app-box-preview-title"></div>
										<div class="app-box-preview-version"></div>
									</div>
									<div class="app-box-info-buttons">
										<div class="app-box-info" id="header_desc_text"></div>
										<div class="app-box-buttons">
											<div class="button blue-style app-own-button" id="deploy-aws" data-app-id="0" data-app-name="Trial Deployment">Deploy to AWS</div>
											<div class="button blue-style" id="deploy-buy" data-app-id="0" data-app-name="Trial Deployment">Buy</div>
											<div class="button blue-style" id="deploy-trial" data-app-id="0" data-app-name="Trial Deployment">Try</div>
										</div>
									</div>
								</div>
								<div class="app-box-content">
									<div class="app-box-content-left">
										<div class="app-box-content-tabs">
											<div class="app-box-tab app-box-tab-clicked" 	id="tab-menu-overview" data-content-body-id="app-box-overview">Overview</div>
											<div class="app-box-tab" 						id="tab-menu-ion-and-workspace" data-content-body-id="app-box-ion-workspace" style="display:none;">Ion & Workspace</div>
											<div class="app-box-tab" 	id="tab-menu-running-instances" data-content-body-id="app-box-running-instances" style="display:none;">Running Instances</div>
										</div>
										<div class="app-content-container" id="app-box-overview">
											<div class="app-content-header" id="overview-header-section-1"></div>									
											<div class="app-content-body" id="overview-section-1"></div>										
											<div class="app-content-body" id="overview-section-media-1">
												<div class="app-media-preview-viewer">
													<div class="app-media-element blue-pink-gradient">video/img</div>
												</div>
												<div class="app-media-preview-thumbnails">
													<div class="app-media-thumbnail-container">
														<div class="app-media-thumbnail-element white-gradient-b"></div>
														<div class="app-media-thumbnail-element white-gradient-b"></div>
														<div class="app-media-thumbnail-element white-gradient-b"></div>
														<div class="app-media-thumbnail-element white-gradient-b"></div>
														<div class="app-media-thumbnail-element white-gradient-b"></div>
													</div>
												</div>
											</div>		
											<div class="app-content-header" id="overview-header-section-2"></div>									
											<div class="app-content-body" id="overview-section-2"></div>		
											<div class="app-content-header" id="overview-header-section-3"></div>									
											<div class="app-content-body" id="overview-section-3"></div>												
											<div style="clear:both;"></div>											
										</div>														
										<div style="clear:both;"></div>
									</div>									
								</div>
							</div>
						</div>					
					</div>
				</div>		
				<div class="slide" id="slide-2">
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view" id="deployments_data_container">
								<!--- Deployments Lists HEADER--->
								<div class="ul_header_row" style="height:35px;border:0px;position:relative;">
									<div class="ul_row_col_1 header_col" style="height:35px;width:550px;">
										<!--<div class="button" id="create-user-button" style="margin-right:0px;float:left;">Running</div>
										<div class="button" id="create-user-button" style="margin-right:10px;margin-left:-2px;float:left;">v</div>--->
										<div class="button" id="refresh-user-deployments" style="margin-right:10px;float:left;">Refresh</div>
										<input type="text" value="Search Deployments" id="deployments-list-search-box" class="search-input" style=""/>
										<div class="button search-button-clear" id="deployments-list-clear-search-button">Clear</div>									
									</div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style deploymentPageNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style deploymentPagePrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_2 header_col deployments-list-header-page-text" id="deployments-list-header-page-text" style="float:right;color:#555;width:100px;display:none;"></div>
								</div>	
								<!--- Deployments Lists HEADER--->
								<!--- Deployments Lists BODY--->
								<div class="app-content-container" id="app-box-running-instances" style="border-top:1px solid #DBDBDB;padding: 0 0 0 0;margin-top:5px;"></div>
								<!--- Deployments Lists BODY--->
								<!--- Deployments Lists Footer--->
								<div class="ul_header_row" style="height:35px;background-color: #fff;color: #222;border:0px;margin-top:-1px;padding:4px 0;">
									<div class="ul_row_col_1 header_col" id="deployments-list-footer-num-details" style="color:#777;"></div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style deploymentPageNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style deploymentPagePrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_2 header_col deployments-list-header-page-text" style="float:right;color:#777;text-align:right;"></div>
								</div>								
								<!--- Deployments Lists Footer--->							
							</div>
						</div>						
					</div>
				</div>
				<div class="slide" id="slide-3">		
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view"  id="admin_user_product_data_container">
								<!--- User Lists HEADER--->
								<div class="ul_header_row" style="height:35px;border:0px;position:relative;">
									<div class="ul_row_col_1 header_col" style="height:35px;width:550px;">
										<div class="button" id="create-user-button" style="margin-right:10px;float:left;">Create User</div>
										<input type="text" value="Search Names" id="user-list-search-box" class="search-input" />
										<div class="button search-button-clear" id="user-list-clear-search-button">Clear</div>									
									</div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style adminProductNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style adminProductPrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_2 header_col user-list-header-page-text" id="user-list-header-page-text" style="float:right;color:#555;width:100px;display:none;"></div>
								</div>	
								<!--- User Lists HEADER--->
								<!--- User Lists BODY--->
								<div id="user-list-details-body"></div>
								<!--- User Lists BODY--->
								<!--- User Lists Footer--->
								<div class="ul_header_row" style="height:35px;background-color: #fff;color: #222;border:0px;border-top:1px solid #222;margin-top:-1px;padding:4px 0;">
									<div class="ul_row_col_1 header_col" id="user-list-footer-num-details" style="color:#777;"></div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style adminProductNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style adminProductPrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_2 header_col user-list-header-page-text" style="float:right;color:#777;text-align:right;"></div>
								</div>								
								<!--- User Lists Footer--->
							</div>
						</div>						
					</div>
				</div>
				<div class="slide" id="slide-4">	
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view"  id="admin_user_trial_requests_container">
								<!--- Trial Requests HEADER--->
								<div class="ul_header_row" style="height:35px;border:0px;">
									<div class="ul_row_col_1 header_col" style="height:35px;width:550px;">
										<input type="text" value="Search Names" id="trial-requests-search-box" class="search-input" />
										<div class="button search-button-clear" id="trial-requests-clear-search-button">Clear</div>
									</div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style trialRequestsNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style trialRequestsPrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_3 header_col trial-requests-header-page-text" id="user-list-header-page-text" style="float:right;color:#555;"></div>
								</div>
								<!--- Trial Requests HEADER--->
								<!--- Trial Requests BODY--->
								<div id="trial-requests-details-body"></div>
								<!--- Trial Requests BODY--->
								<!--- Trial Requests Footer--->
								<div class="ul_header_row" style="background-color: #999;color: white;border: 0px;margin-top:0px;display:none;">
									<div class="ul_row_col_1 header_col" style="width:335px;padding-left:5px;">Name <span style="font-size:10px;">(view/edit app permissions)</span></div>
									<div class="ul_row_col_2 header_col"></div>
									<div class="ul_row_col_3 header_col"></div>
									<div class="ul_row_col_4 header_col"></div>
									<div class="ul_row_col_5 header_col" style="width:125px;padding-right:5px;"></div>
								</div>
								<div class="ul_header_row" style="height:35px;background-color: #fff;color: #222;border:0px;border-top:1px solid #dbdbdb;margin-top:-1px;padding:4px 0;">
									<div class="ul_row_col_1 header_col" id="trial-requests-footer-num-details" style="color:#777;"></div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style trialRequestsNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style trialRequestsPrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_2 header_col trial-requests-header-page-text" style="float:right;color:#777;text-align:right;"></div>
								</div>								
								<!--- Trial Requests Footer--->														
							</div>
						</div>						
					</div>
				</div>
				<div class="slide" id="slide-5">	
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view" id="user-aws-instance-list-container">
								<!--- AWS Instances Lists HEADER--->
								<div class="ul_header_row" style="height:35px;border:0px;position:relative;">
									<div class="ul_row_col_1 header_col" style="height:35px;width:550px;">
										<div class="button" id="refresh-aws-instances" style="margin-right:10px;float:left;">View / Refresh</div>
										<select id="aws_instance_credentials" 	name="test" class="cdc_input_select" style="margin-top:0px;margin-left:0px;width:225px;margin-right:10px;float:left;padding: 5px 4px 6px 5px;"></select>										
										<select id="aws_instance_regions" 		name="test" class="cdc_input_select" style="margin-top:0px;margin-left:0px;width:155px;margin-right:10px;float:left;padding: 5px 4px 6px 5px;"></select>
									</div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style awsInstancesPageNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style awsInstancesPagePrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_2 header_col aws-instances-list-header-page-text" id="aws-instances-list-header-page-text" style="float:right;color:#555;width:100px;display:none;"></div>
								</div>	
								<!--- AWS Instances Lists HEADER--->
								<!--- AWS Instances Lists BODY--->
								<div class="app-content-container" id="app-box-aws-instances" style="border-top:1px solid #DBDBDB;padding: 0 0 0 0;margin-top:5px;">
									<div  style="width:700px;float:left;height:500px;line-height:500px;font-size:18px;font-weight:bold;color:#777;text-align:center;">
											Select AWS Keys above to view Instances.
									</div>
								</div>
								<!--- AWS Instances Lists BODY--->
								<!--- AWS Instances Lists Footer--->
								<div class="ul_header_row" style="height:35px;background-color: #fff;color: #222;border:0px;margin-top:-1px;padding:4px 0;">
									<div class="ul_row_col_1 header_col" id="aws-instances-list-footer-num-details" style="color:#777;"></div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button green-style awsInstancesPageNext" style="float:right;margin-right:0px;">&gt;</span>
										<span class="button green-style awsInstancesPagePrevious" style="float:right;margin-right:4px;">&lt;</span>
									</div>
									<div class="ul_row_col_2 header_col aws-instances-list-header-page-text" style="float:right;color:#777;text-align:right;"></div>
								</div>								
								<!--- AWS Instances Lists Footer--->	
							</div>
						</div>						
					</div>
				</div>
				<div class="slide" id="slide-6">
					<div class="content-main">
						<div class="main-view-content" id="dashboard-container">
							<div class="app-box-view" id="dashboard-view">
								
								<div class="dashboard-widget-2" 		id="chart-1"><div id="chart-1-chart"></div></div>
								<div class="dashboard-widget"			id="chart-1-data"><div class="dashboard-loading">Loading...</div></div>
								
								<div class="dashboard-widget" 		id="chart-2"><div id="chart-2-chart"></div></div>	
								<div class="dashboard-widget-2"		id="chart-2-data" style="	overflow:auto;"><div class="dashboard-loading">Loading...</div></div>
								
								<div class="dashboard-widget-3" 		id="chart-3"><div id="chart-3-chart"></div></div>												
								
							</div>						
						</div>
					</div>
				</div>
				<% if(principal.isInfor24Admin()){ %>	
				<jsp:include page="infor24.jsp" />
				<% } %>
			</div>
		</div>
		<div style="clear:both;"></div>
	</div>		
</body>
</html>
