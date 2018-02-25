<%--
  Created by IntelliJ IDEA.
  User: xen
  Date: 2017/12/3
  Time: 16:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<nav class="navbar navbar-default clearfix">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">商城后台</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="${light==1?'active':''}"><a  href="category_list">分类管理</a></li>
                <li class="${light==2?'active':''}"><a  href="user_list">用户管理</a></li>
                <li class="${light==3?'active':''}"><a href="order_list">订单管理</a></li>
                <li class=""><a href="/" target="_blank">访问前台</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>
