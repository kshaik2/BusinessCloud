<%@ page import="org.springframework.security.authentication.UsernamePasswordAuthenticationToken" %>
<%@ page import="com.infor.cloudsuite.platform.security.SecurityUser" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<body>
<%
    final UsernamePasswordAuthenticationToken userPrincipal = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
    final SecurityUser principal = (SecurityUser) userPrincipal.getPrincipal();
    request.setAttribute("principal", principal);
%>
<H2>
    <fmt:setBundle basename="cloudsuite" var="baseBundle"/>
    <fmt:message key="hello.world">
        <fmt:param value="<%= principal.getFirstName() %>"/>
        <fmt:param value="<%= principal.getLastName() %>"/>
    </fmt:message>
    <br/>
    <fmt:bundle basename="cloudsuite">
        <fmt:message key="hello.world">
            <fmt:param value="${principal.firstName}"/>
            <fmt:param value="${principal.lastName}"/>
        </fmt:message>
    </fmt:bundle>
    <br/>
    <fmt:message key="hello.world">
        <fmt:param value="<%= principal.getFirstName() %>"/>
        <fmt:param value="<%= principal.getLastName() %>"/>
    </fmt:message>
    <br/>
    <fmt:message key="hello.world" bundle="${baseBundle}">
        <fmt:param value="${principal.firstName}"/>
        <fmt:param value="${principal.lastName}"/>
    </fmt:message>
</H2>
</body>
</html>