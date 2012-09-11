<%@page contentType="text/html" import="com.infor.cloudsuite.platform.components.RequestServices" %>
<%@page contentType="text/html" import="com.infor.cloudsuite.service.StringDefs" %>
<%@ page import="java.text.Format" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	// Format date for footer 
    RequestServices reqServices = new RequestServices();
    Date today = new Date();
	Format formatter = new SimpleDateFormat("yyyy");
	String todayString = formatter.format(today);
	
	// Set default locale
	Locale curLocale;

	// Handle language selection form submission
    final String parameter = request.getParameter(StringDefs.LANG_REQ_PARAM);
	if (parameter != null) {
		// Set cookie
        reqServices.setLocaleCookie(response, parameter);
		// Set Session if cookies are disabled
		reqServices.setLocale(session, parameter);
	}
	
	// Update locale based on cookie, and if those are disabled, try using just the session
	curLocale = reqServices.getLocale(request, parameter);
%>
<fmt:setLocale value="<%= curLocale %>"/>
<!DOCTYPE HTML>
<html>
<head>	
	<title>Infor Business Cloud</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge" >
	<meta name="viewport" content="width=960;initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/> 
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">	
	<!-- Libraries & Utils CSS -->	
	<link rel="icon" type="image/ico" href="css/img/favicon.ico"/>
	<link href ='css/button.css' rel='stylesheet' type='text/css'>
	<link href ='css/app.css' rel='stylesheet' type='text/css'>
	<link href ='css/dialogbox.css' rel='stylesheet' type='text/css'>
	<link href ='css/index.css' rel='stylesheet' type='text/css'>

	<!-- Libraries & Utils JS -->
	<script src ='js/lib.jquery.js'></script> 
	<script src ='js/lib.jquery.ui.js'></script>
	<script src ='js/lib.i18n.js'></script>	
	<script src ='js/lib.dialogbox.js'></script>		
	<script src ='js/lib.underscore.js'></script>
	<script src ='js/lib.json2.js'></script>
	<script src ='js/lib.backbone.js'></script>	
	<script src ='js/lib.validate.js'></script>	

	<!-- Application Setup & Init -->
	<script src ='js/app.i18n.js'></script>	
	<script src ='js/app.index.js'></script>	
</head>
<body>
	<div id="header-top">
		<div id="header-top-content">	
			<div id="infor-bc-logo"></div>
			<div id="login-button-top" ></div>	
			<div id="get-started-button-top" ></div>				
			<input type="hidden" value="<%=curLocale.toString() %>" id="i18n_lang" />
		</div>
	</div>
	<div id="content-main">
		<div id="content-one" 	class="bg-grey">
			<div class="sub-content">
				<div id="monitor-site"></div>
				<div id="huge-headline-text"></div>
			</div>
		</div>
		<div id="content-two" 	class="bg-black" style="display:none;">
			<div class="sub-content">
				<div id="section-two-icon-list"></div>
				<div id="section-two-icon-list"></div>
			</div>					
		</div>		
		<div id="content-five" 	class="bg-white" style="display:none;">
			<div class="sub-content">
				<div id="industry-list"></div>
				<div id="horizontal-by-need"></div>
			</div>		
		</div>
		<div id="content-three"	class="bg-black">
			<div class="sub-content">
				<div id="deploy-in-minutes"></div>
			</div>			
		</div>		
		<div id="content-four" 	class="bg-white"  style="display:none;">
			<div class="sub-content">
				<a class="ibc-blog-title" href="http://inforbusinesscloud.tumblr.com">
					business cloud blog
				</a>			
			</div>
			<div class="sub-content" id="blog">
					<span class="loading">Loading...</span>
			</div>		
			<div class="sub-content">
				<a class="ibc-blog-view-more" href="http://inforbusinesscloud.tumblr.com">
					view more posts ...
				</a>			
			</div>			
			<div style="clear:both;"></div>
		</div>		
		<div id="content-six" 	class="bg-black">
			<div class="sub-content" style="height:50px;line-height:50px;font-weight:bold;">
				<a href="https://twitter.com/inforbizcloud" target="_blank" class="twitter-follow-button"></a>
			</div>
			<div class="sub-content" style="height:50px;line-height:50px;font-weight:bold;">
				<div id="footer_logo" style="margin-top:10px;"></div>
				<a href="http://www.infor.com" class="footer_links">Home</a>
				<span class="footer_divider">|</span>
				<a href="http://www.infor.com/company/privacy/" class="footer_links">Privacy</a>
				<span class="footer_divider">|</span>
				<a href="http://www.infor.com/content/LegalNotice/" class="footer_links">Legal Notice</a>
				<span class="footer_cpy">&copy;&nbsp;Copyright 2012. Infor. All rights reserved.</span>			
			</div>	
		</div>
	</div>
	<!-- <script src="https://inforbusinesscloud.tumblr.com/api/read/json?callback=tumblr&amp;num=3" type="text/javascript"></script> -->
	<!-- Start of Woopra Code --> <script type="text/javascript"> function woopraReady(tracker) { tracker.setDomain('businesscloud.infor.com'); tracker.setIdleTimeout(60000); tracker.trackPageview({type:'pageview',url:window.location.pathname+window.location.search,title:document.title}); return false; } (function() { var wsc = document.createElement('script'); wsc.src = document.location.protocol+'//static.woopra.com/js/woopra.js'; wsc.type = 'text/javascript'; wsc.async = true; var ssc = document.getElementsByTagName('script')[0]; ssc.parentNode.insertBefore(wsc, ssc); })(); </script> <!-- End of Woopra Code -->	
	<script type="text/javascript">
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-32733184-1']);
	  _gaq.push(['_setDomainName', 'infor.com']);
	  _gaq.push(['_trackPageview']);
	  (function() {
		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	</script>	
</body>
</html>