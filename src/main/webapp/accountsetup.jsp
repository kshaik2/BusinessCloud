<%@page contentType="text/html" import="java.util.*" %>
<%@page contentType="text/html" import="java.text.*" %>
<%@ page import="com.infor.cloudsuite.entity.Validation" %>
<%@ page import="com.infor.cloudsuite.entity.ValidationType"%>
<%@ page import="com.infor.cloudsuite.service.StringDefs" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	Date today = new Date();
	Format formatter = new SimpleDateFormat("yyyy");
	String todayString = formatter.format(today);
%>
<!DOCTYPE HTML>
<% Validation validation = (Validation) session.getAttribute(StringDefs.VALIDATION_NAME); %>
<fmt:setLocale value="<%= validation.getLanguage() %>"/>
<html>
<head>	
	<title>
	<%if(validation.getType().equals(ValidationType.REGISTRATION)){%>	
		Infor Business Cloud - Complete Registration
	<% }else if(validation.getType().equals(ValidationType.LOST_PASSWORD)){ %>
		Infor Business Cloud - Reset Password
	<%} %>	
	</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge" >
	<!-- Libraries & Utils CSS -->
	<link rel="icon" type="image/ico" href="css/img/favicon.ico"/>
	<link href ='css/button.css' rel='stylesheet' type='text/css'>
	<link href ='css/app.css' rel='stylesheet' type='text/css'>
	<link href ='css/dialogbox.css' rel='stylesheet' type='text/css'>	
	<link href ='css/login.css' rel='stylesheet' type='text/css'>

	<!-- Libraries & Utils JS -->
	<script src ='js/lib.jquery.js'></script> 
	<script src ='js/lib.jquery.ui.js'></script>
	<script src ='js/lib.i18n.js'></script>		
	<script src ='js/lib.dialogbox.js'></script>		
	<script src ='js/lib.underscore.js'></script>
	<script src ='js/lib.json2.js'></script>
	<script src ='js/lib.backbone.js'></script>
	<script src ='js/lib.cookie.js'></script>		
	<script src ='js/lib.validate.js'></script>	

	<!-- Application Setup & Init -->
	<script src ='js/app.i18n.js'></script>		
	<script src ='js/app.accountsetup.js'></script>		
</head>
<body style="background:#545454;">
	<div id="header_container">
		<div id="header">
			<a href="#/" id="infor_logo"></a>
			<input type="hidden" value="<%= validation.getLanguage() %>" id="locale_val"/>
			<input type="hidden" value="<%= validation.getLanguage() %>" id="i18n_lang" />
		</div>
	</div>
	<div id="content">
		<% if(validation.getType().equals(ValidationType.REGISTRATION)){ %>	
			<div id="cloud_admin" style="width:757px;height:432px;margin:0 auto;position:relative;float:none;">
				<div id="cloud_admin_header" style="border:1px solid #222;border-bottom:1px solid #8f8f8f;"><fmt:message key="complete.dialog.1"/><span style="color:#fff;margin-left:15px;border-left:1px solid #545454;padding-left:15px;"><%= validation.getFirstName() %> <%=validation.getLastName() %></span></div>
				<div id="cloud_admin_content" style="border-left:1px solid #222;border-right:1px solid #222;height:311px;">
					<div class="ca_col ca_col_one">
						<div class="ca_input_desc_text"><fmt:message key="complete.dialog.2"/><span class="required">*</span></div>
						<input type="email" value="<%= validation.getEmail() %>" disabled class="ca_input" />
						<div class="ca_input_desc_text"><fmt:message key="complete.dialog.3"/><span class="required">*</span></div>
						<input type="password" value="" id="password" class="ca_input ca_input_focus" />
						<div class="ca_input_desc_text"><fmt:message key="complete.dialog.4"/><span class="required">*</span></div>
						<input type="password" value=""  id="password2" class="ca_input ca_input_focus" />
						<div class="ca_input_desc_text"><fmt:message key="complete.dialog.5"/><span class="required">*</span></div>
						<select id="lang" class="ca_input_select">
							<option value="en_US"><fmt:message key="account.16"/></option>							
							<option value="pt_BR"><fmt:message key="account.17"/></option>												
						</select>
						<input type="hidden" value="<%= validation.getId() %>" id="validationId" />
					</div>
					<div class="ca_col ca_col_two">
						<div class="ca_input_desc_text"><fmt:message key="account.30"/><span class="required">*</span></div>
						<input type="text" value="" id="user_phone" class="ca_input" />
						<div class="ca_input_desc_text"><fmt:message key="account.24"/><span class="required">*</span></div>
						<input type="text" value="" id="user_address_1" class="ca_input" />
						<div class="ca_input_desc_text"><fmt:message key="account.25"/><span class="required">*</span></div>
						<input type="text" value="" id="user_address_2" class="ca_input" />
						<div class="ca_input_desc_text"><fmt:message key="account.26"/><span class="required">*</span></div>
						<input type="text" value="" id="user_country" class="ca_input" />
					</div>
					<div class="ca_col ca_col_three">
						<div class="ca_input_desc_text"><fmt:message key="account.6"/><span class="required">*</span></div>
						<input type="text" value="<%= validation.getCompany() %>" id="company_name" class="ca_input" />
						<input type="hidden" value="-1" id="company_id" class="ca_input" />
						<div class="ca_input_desc_text">Industry<span class="required">*</span></div>
						<select name="test" class="ca_input_select" id="industry_id"></select>
						<div class="ca_input_desc_text">InforID</div>
						<input type="text" value="" id="infor_id" class="ca_input" />
					</div>
				</div>
				<div id="cloud_admin_button_container" style="border:1px solid #222;">
					<span class="cloud_admin_button" id="complete_registration_save" style="width:120px;text-align:center;" ><fmt:message key="complete.dialog.13"/></span>
				</div>
			</div>	
		<% }else if(validation.getType().equals(ValidationType.LOST_PASSWORD)){ %>
			<div id="cloud_admin" style="width:375px;height:331px;margin:0 auto;position:relative;float:none;">
				<div id="cloud_admin_header" style="border:1px solid #222;border-bottom:1px solid #8f8f8f;width:353px;"><fmt:message key="password.dialog.1"/><span style="color:#fff;margin-left:15px;border-left:1px solid #545454;padding-left:15px;"><%= validation.getFirstName() %> <%=validation.getLastName() %></span></div>
				<div id="cloud_admin_content" style="border-left:1px solid #222;border-right:1px solid #222;width:353px;height:210px;overflow:hidden;">
					<div class="ca_input_desc_text" style="font-weight:bold;width:300px;"><fmt:message key="password.dialog.11"/></div>
					<div class="ca_input_desc_text"><fmt:message key="password.dialog.12"/></div>
					<input type="password" value="" id="password" class="ca_input ca_input_focus" style="width:300px;" />
					<div class="ca_input_desc_text"><fmt:message key="password.dialog.13"/></div>
					<input type="password" value="" id="password2" class="ca_input ca_input_focus"  style="width:300px;"/>
					<input type="hidden" value="<%= validation.getId() %>" id="validationId" />
				</div>
				<div id="cloud_admin_button_container" style="border:1px solid #222;width:353px;">
					<span class="cloud_admin_button" id="reset_password_save" style="width:120px;text-align:center;" ><fmt:message key="password.dialog.14"/></span>
				</div>
			</div>		
		<%} %>
		<div class="clear"></div>
	</div>
	<div id="footer_container">
		<div id="footer">
			<div id="footer_inner"><div class="clear"></div></div>
		</div>
	</div>
	<div id="footer_bottom_container">
		<div id="footer_bottom">
			<div id="footer_bottom_inner">
				<div id="footer_logo"></div>
				<a href="<fmt:message key="footer.1.link"/>" class="footer_links"><fmt:message key="footer.1"/></a>
				<span class="footer_divider">|</span>
				<a href="<fmt:message key="footer.2.link"/>" class="footer_links"><fmt:message key="footer.2"/></a>
				<span class="footer_divider">|</span>
				<a href="<fmt:message key="footer.3.link"/>" class="footer_links"><fmt:message key="footer.3"/></a>
				<span class="footer_cpy">&copy;<fmt:message key="footer.4"><fmt:param value="<%= todayString %>"/></fmt:message></span>
				<div class="clear"></div>
			</div>
		</div>		
	</div>
</body>
</html>