<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>top</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta http-equiv="content-type" content="text/html;charset=utf-8">
    <!--
    <link rel="stylesheet" type="text/css" href="styles.css">
    -->
    <style type="text/css">
        body {
            background: #15B69A;
            margin: 0px;
            color: #ffffff;
        }

        a {
            text-transform: none;
            text-decoration: none;
            color: #ffffff;
            font-weight: 900;
        }

        a:hover {
            text-decoration: underline;
            color: #c77405;
        }
    </style>
</head>

<body>
<h1 style="text-align: center;">传智播客网上书城系统</h1>
<div style="font-size: 10pt; line-height: 10px;">
    <!--根据用户是否登录,显示不同的链接-->
    <c:choose>
        <c:when test="${empty sessionScope.sessionUser}">
            <a href="<c:url value='/jsps/user/login.jsp'/>" target="_parent">传智会员登录</a> |&nbsp;
            <a href="<c:url value='/jsps/user/regist.jsp'/>" target="_parent">注册传智会员</a>
        </c:when>
        <c:otherwise>
            传智会员：${sessionScope.sessionUser.loginname}&nbsp;&nbsp;|&nbsp;&nbsp;
            <a href="<c:url value='/cartServlet?method=myCart'/>" target="body">我的购物车</a>&nbsp;&nbsp;|&nbsp;&nbsp;
            <a href="<c:url value='/orderServlet?method=myOrder'/>" target="body">我的传智订单</a>&nbsp;&nbsp;|&nbsp;&nbsp;
            <a href="<c:url value='/jsps/user/pwd.jsp'/>" target="body">修改密码</a>&nbsp;&nbsp;|&nbsp;&nbsp;
            <a href="<c:url value='/userServlet?method=quit'/>" target="_parent">退出</a>
        </c:otherwise>
    </c:choose>


</div>
</body>
</html>
