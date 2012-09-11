<%@page contentType="text/html" import="com.infor.cloudsuite.platform.components.RequestServices" %>
<%@page contentType="text/html" import="com.infor.cloudsuite.service.StringDefs" %>
<%@ page import="java.text.Format" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	// Handle language selection form submission
    final String hashParameter = request.getParameter("g");

%>
<!DOCTYPE HTML>
<html>
<head>	
	<title>Infor</title>
	<script src = '<%= request.getContextPath() %>/js/lib.jquery.js'></script> 
	<script>
		$(function(){
		
			var hash = '<%= hashParameter %>',
				conRoot = '<%= request.getContextPath() %>',
				serURL = conRoot+"/services/trialService/getActualUrl";
			
			var post_data = {
				g : hash
			}			
			
			$.ajax({ 
				url: serURL, 
				type: "GET",
				cache: false,
				data : post_data,
				dataType: "json",
				processData : true,
				contentType: "application/json; charset=utf-8",
				success: function(resp){
					window.location = resp.redirectUrl;
				},
				error: function(resp){					
					window.location = '<%= request.getContextPath() %>/cloud.jsp';
				}
			});
		
		});
	
	</script>
</head>
<body>

	<!-- Start of Woopra Code 
	<script type="text/javascript">
	function woopraReady(tracker) {
		tracker.setDomain('cloudsuite.infor.com');
		tracker.setIdleTimeout(300000);
		tracker.track();
		return false;
	}
	(function() {
		var wsc = document.createElement('script');
		wsc.src = document.location.protocol+'//static.woopra.com/js/woopra.js';
		wsc.type = 'text/javascript';
		wsc.async = true;
		var ssc = document.getElementsByTagName('script')[0];
		ssc.parentNode.insertBefore(wsc, ssc);
	})();
	</script>
	<!-- End of Woopra Code -->
	
</body>
</html>