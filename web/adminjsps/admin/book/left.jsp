<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'left.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<c:url value='/adminjsps/admin/css/book/left.css'/>">
	<script type="text/javascript" src="<c:url value='/menu/mymenu.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<link rel="stylesheet" href="<c:url value='/menu/mymenu.css'/>" type="text/css" media="all">
	  <script language="javascript">
		  //1.对象名必须与第一参数相同
		  //2.第二个参数是显示在菜单上的大标题
		  var bar = new Q6MenuBar("bar", "传智播客网上书城");
		  $(function() {
			  bar.colorStyle = 0;		//指定颜色样式(0-4)
			  bar.config.imgDir = "<c:url value='/menu/img/'/>";//加和减两张图片的路径
			  bar.config.radioButton=true;//多个分类之间是否排斥
			  //      一级分类       二级分类           二级分类的跳转地址         iframe框架页
			  // bar.add("程序设计", "Java Javascript", "/goods/jsps/book/list.jsp", "body");
			  <c:forEach items="${parents}" var="parent">
			  <c:forEach items="${parent.children}" var="child">
			  bar.add("${parent.cname}","${child.cname}","/goods/admin/adminBookServlet?method=findByCategory&cid=${child.cid}","body");
			  </c:forEach>
			  </c:forEach>

			  //最后调用小工具的toString()方法生成字符串并转成html代码添加到页面中
			  $("#menu").html(bar.toString());
		  });
	  </script>
  </head>
  
<div id="menu"></div>
  </body>
</html>
