<%@ page language="java" contentType="text/html; charset=UTF-8" import='java.util.Enumeration'
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	location.replace("login.jsp");
</script>
</head>
<body>
	<% 
		Cookie c = new Cookie("mit","");
		Cookie c1 = new Cookie("username","");
		c.setMaxAge(0);
		c1.setMaxAge(0);
		response.addCookie(c);
		response.addCookie(c1);
		Enumeration<String> em = request.getSession().getAttributeNames();
		while(em.hasMoreElements()){
			request.getSession().removeAttribute(em.nextElement());
		}
	%>
</body>
</html>