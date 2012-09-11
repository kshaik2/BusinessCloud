<%@page contentType="text/html" import="java.util.*" %>
<%@page contentType="text/html" import="java.text.*" %>
<%@ page import="org.springframework.security.authentication.UsernamePasswordAuthenticationToken" %>
<%@ page import="com.infor.cloudsuite.platform.security.SecurityUser" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	Date today = new Date();
	Format formatter = new SimpleDateFormat("yyyy");
	String todayString = formatter.format(today);
%>
<!DOCTYPE HTML>
<html>
<head>	
<%
    final UsernamePasswordAuthenticationToken userPrincipal = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
    final SecurityUser principal = (SecurityUser) userPrincipal.getPrincipal();
%>
	<title>
		Infor Business Cloud
	</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge" >
	<!-- Libraries & Utils CSS -->
	<link rel="icon" type="image/ico" href="css/img/favicon.ico"/>
	<link href ='css/app.css' rel='stylesheet' type='text/css'>
	<link href ='css/dialogbox.css' rel='stylesheet' type='text/css'>
	<link href ='css/masonry.css' rel='stylesheet' type='text/css'>
	<link href ='css/tile-bg.css' rel='stylesheet' type='text/css'>
	<link href ='css/gradient.css' rel='stylesheet' type='text/css'>	
	<link href ='css/button.css' rel='stylesheet' type='text/css'>		
	<link href ='css/store.css' rel='stylesheet' type='text/css'>	
	<link href ='css/admin.css' rel='stylesheet' type='text/css'>
	
	<!-- Libraries & Utils JS -->
	<script src="js/lib.jquery.js"></script>
	<script src="js/lib.jquery.ui.js"></script>
	<script src="js/lib.underscore.js"></script>
	<script src="js/lib.backbone.js"></script>	
	<script src="js/lib.json2.js"></script>
	<script src="js/lib.masonry.js"></script>
	<script src="js/lib.i18n.js"></script>		
	<script src="js/lib.dialogbox.js"></script>	
	<script src="js/lib.validate.js"></script>

	<!-- Application Setup & Init -->
	<script src ='js/app.i18n.js'></script>	
	<script src ='js/app.admin.cs.js'></script>
	
</head>
<body>
	<!-- App User Vars --->
	<input type="hidden" id="i18n-lang" 			value="<%= principal.getLanguage().toString() %>"  						/>
	<input type="hidden" id="user-full-name" 		value="<%= principal.getFirstName() %> <%= principal.getLastName() %>" 	/>	
	<input type="hidden" id="user-email-address"	value="<%= principal.getUsername() %>" 									/>
	<input type="hidden" id="scheme" 				value="<%= request.getScheme() %>" 										/>
	<input type="hidden" id="servName" 				value="<%= request.getServerName() %>" 									/>
	<input type="hidden" id="conRoot" 				value="<%= request.getContextPath() %>" 								/>
	
<!-- App HTML -->
	<div id="main-app-container" class="main-app large-screen"> <!--  large-screen -->
		<div class="left-container">
			<div class="left-mini-container">
				<div class="left-user-box" id="account-settings" data-link-page="User Settings">
					<span class="user-drop-arrow"></span>
					<span class="user-name-text"><%= principal.getUsername() %></span>
					<span style="display:none;" id="user_full_name"><%= principal.getFirstName() %> <%= principal.getLastName() %></span>	
					<span style="display:none;" id="user_email_address"><%= principal.getUsername() %></span>
					<span style="display:none;" id="bdr"><% if(principal.isBdrAdmin()){ %>true<% } %></span>
					<span style="display:none;" id="salesrep"><% if(principal.isSalesRep()){ %>true<% } %></span>
					<span style="display:none;" id="salesopts"><% if(principal.isSalesOps()){ %>true<% } %></span>					
				</div>
				<div class="left-header"></div>				
				<div class="left-menu">
					<div class="left-menu-divider"></div>
					<div class="left-menu-top">
						<div class="left-menu-item user-list-link">Customers</div>
						<div class="left-menu-item-sub user-list-link">My Customers</div>	
						<div class="left-menu-item-sub user-list-link">All Customers</div>					
					</div>
					<div class="left-menu-divider"></div>
					<div class="left-menu-big-list">
						<div class="left-menu-item" id="trial-request-link">Trial Requests</div>						
					</div>					
					<div style="clear:both;"></div>
				</div>
			</div>
		</div>
		<div class="top-content-container">
			<div id="top-content-slider" style="left:-2880px;">
				<div class="top-slide" id="top-slide-minus-3">		
					<div class="top-content-main">
						<div class="header-text">minus 3</div>
					</div>
				</div>
				<div class="top-slide" id="top-slide-minus-2">	
					<div class="top-content-main">
						<div class="header-text">minus 2</div>
					</div>
				</div>
				<div class="top-slide" id="top-slide-minus-1">	
					<div class="top-content-main">
						<div class="header-text">minus 1</div>
					</div>				
				</div>			
				<div class="top-slide" id="top-slide-1">		
					<div class="top-content-main">
						<div class="header-text">User List</div>
					</div>
				</div>
				<div class="top-slide" id="top-slide-2">		
					<div class="top-content-main">
						<div class="button blue-style back-button" style="display:none;">Back</div>
						<div class="header-text">Trial Requests</div>
					</div>
				</div>
				<div class="top-slide" id="top-slide-3">		
					<div class="top-content-main">
						<div class="button blue-style back-button" style="display:none;">Back</div>
						<div class="header-text">slide 3</div>
					</div>
				</div>
			</div>
		</div>
		<div class="content-container">
			<div id="content-slider" style="left:-2880px;">
				<div class="slide" id="slide-minus-3">		
					<div class="content-main">
						<div class="main-view-content mongoose-view">
							<!-- -3 Slide -->
						</div>						
					</div>
				</div>
				<div class="slide" id="slide-minus-2">	
					<div class="content-main">
						<div class="main-view-content ion-connect-view">
							<!-- -2 Slide -->
						</div>						
					</div>
				</div>
				<div class="slide" id="slide-minus-1">	
					<div class="content-main">
						<div class="main-view-content topology-view">
							<!-- -1 Slide -->
						</div>						
					</div>
				</div>
				<div class="slide" id="slide-1">
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view"  id="admin_user_product_data_container">
								<!--- User Lists HEADER-->
								<div class="ul_header_row" style="height:35px;border:0px;">
									<div class="ul_row_col_1 header_col" style="height:35px;width:550px;">
										<div class="button" id="create-user-button" style="margin-right:10px;float:left;">Create User</div>
										<input type="text" value="Search Names" id="user-list-search-box" class="search-input" />
										<div class="button search-button-clear" id="user-list-clear-search-button">Clear</div>									
									</div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button teal-style adminProductNext" style="float:right;margin-right:0px;">></span>
										<span class="button teal-style adminProductPrevious" style="float:right;margin-right:4px;"><</span>
									</div>
									<div class="ul_row_col_3 header_col user-list-header-page-text" id="user-list-header-page-text" style="float:right;color:#555;"></div>
								</div>
								<div class="ul_header_row" style="background-color: #999;color: white;border: 0px;">
									<div class="ul_row_col_1 header_col" style="width:365px;padding-left:5px;">Name <span style="font-size:10px;">(view/edit app permissions)</span></div>
									<div class="ul_row_col_2 header_col">Last Login</div>
									<div class="ul_row_col_3 header_col">Logins</div>
									<div class="ul_row_col_4 header_col"></div>
									<div class="ul_row_col_5 header_col" style="width:125px;padding-right:5px;">Account Status</div>
								</div>	
								<!--- User Lists HEADER-->
								<!--- User Lists BODY-->
								<div id="user-list-details-body"></div>
								<!--- User Lists BODY-->
								<!--- User Lists Footer-->
								<div class="ul_header_row" style="background-color: #999;color: white;border: 0px;margin-top:0px;">
									<div class="ul_row_col_1 header_col" style="width:365px;padding-left:5px;">Name <span style="font-size:10px;">(view/edit app permissions)</span></div>
									<div class="ul_row_col_2 header_col">Last Login</div>
									<div class="ul_row_col_3 header_col">Logins</div>
									<div class="ul_row_col_4 header_col"></div>
									<div class="ul_row_col_5 header_col" style="width:125px;padding-right:5px;">Account Status</div>
								</div>
								<div class="ul_header_row" style="height:35px;margin-top:4px;border:0px;">
									<div class="ul_row_col_1 header_col" id="user-list-footer-num-details" style="color:#555;"></div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button teal-style adminProductNext" style="float:right;margin-right:0px;">></span>
										<span class="button teal-style adminProductPrevious" style="float:right;margin-right:4px;"><</span>
									</div>
									<div class="ul_row_col_3 header_col user-list-header-page-text" style="float:right;color:#555;"></div>
								</div>								
								<!--- User Lists Footer-->
							</div>
						</div>
					</div>
				</div>
				<div class="slide" id="slide-2">
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view"  id="admin_user_trial_requests_container">
								<!--- Trial Requests HEADER-->
								<div class="ul_header_row" style="height:35px;border:0px;">
									<div class="ul_row_col_1 header_col" style="height:35px;">
										<input type="text" value="Search Names" id="trial-requests-search-box" class="search-input" />
										<div class="button search-button-clear" id="trial-requests-clear-search-button">Clear</div>
									</div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button teal-style trialRequestsNext" style="float:right;margin-right:0px;">></span>
										<span class="button teal-style trialRequestsPrevious" style="float:right;margin-right:4px;"><</span>
									</div>
									<div class="ul_row_col_3 header_col trial-requests-header-page-text" id="user-list-header-page-text" style="float:right;color:#555;"></div>
								</div>
								<div class="ul_header_row" style="background-color: #999;color: white;border: 0px;">
									<div class="ul_row_col_1 header_col" style="width:365px;padding-left:5px;">Name <span style="font-size:10px;">(view/edit app permissions)</span></div>
									<div class="ul_row_col_2 header_col"></div>
									<div class="ul_row_col_3 header_col"></div>
									<div class="ul_row_col_4 header_col"></div>
									<div class="ul_row_col_5 header_col" style="width:125px;padding-right:5px;"></div>
								</div>	
								<!--- Trial Requests HEADER-->
								<!--- Trial Requests BODY-->
								<div id="trial-requests-details-body"></div>
								<!--- Trial Requests BODY-->
								<!--- Trial Requests Footer-->
								<div class="ul_header_row" style="background-color: #999;color: white;border: 0px;margin-top:0px;">
									<div class="ul_row_col_1 header_col" style="width:365px;padding-left:5px;">Name <span style="font-size:10px;">(view/edit app permissions)</span></div>
									<div class="ul_row_col_2 header_col"></div>
									<div class="ul_row_col_3 header_col"></div>
									<div class="ul_row_col_4 header_col"></div>
									<div class="ul_row_col_5 header_col" style="width:125px;padding-right:5px;"></div>
								</div>
								<div class="ul_header_row" style="height:35px;margin-top:4px;border:0px;">
									<div class="ul_row_col_1 header_col" id="trial-requests-footer-num-details" style="color:#555;"></div>
									<div class="ul_row_col_5 header_col" style="height:32px;width:90px;">
										<span class="button teal-style trialRequestsNext" style="float:right;margin-right:0px;">></span>
										<span class="button teal-style trialRequestsPrevious" style="float:right;margin-right:4px;"><</span>
									</div>
									<div class="ul_row_col_3 header_col trial-requests-header-page-text" style="float:right;color:#555;"></div>
								</div>								
								<!--- Trial Requests Footer-->														
							</div>
						</div>					
					</div>
				</div>		
				<div class="slide" id="slide-3">
					<div class="content-main">
						<div class="main-view-content">
							<div class="app-box-view">
								<!-- +3 Slide -->
							</div>
						</div>						
					</div>
				</div>					
			</div>
		</div>
		<div style="clear:both;"></div>
	</div>	
</body>
</html>