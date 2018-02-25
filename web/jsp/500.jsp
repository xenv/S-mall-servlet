<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.PrintStream" %><%--
  Created by IntelliJ IDEA.
  User: xen
  Date: 2017/12/14
  Time: 22:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true"%>
<html>
<head>
<title>500 服务器内部错误</title>
</head>
<body>
<%=exception.getMessage()%>
</body>
</html>
