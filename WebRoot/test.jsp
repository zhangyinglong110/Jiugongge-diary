<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script language="javascript" src="JS/AjaxRequest.js"></script>
<script language="javascript">
	function loginSubmit(form2) {
		if (form2.username.value == "") { //验证用户名是否为空
			alert("请输入用户名！");form2.username.focus();return false;
		}
		if (form2.pwd.value == "") { //验证密码是否为空
			alert("请输入密码！");form2.pwd.focus();return false;
		}
		var param = "username=" + form2.username.value + "&pwd=" + form2.pwd.value; //将登录信息连接成字符串，作为发送请求的参数
		var loader = new net.AjaxRequest("testServlet?action=login", deal_login, onerror, "POST", encodeURI(param));
	}
	function onerror() {
		alert("您的操作有误");
	}
	function deal_login() {
		/*****************显示提示信息******************************/
		var h = this.req.responseText;
		h = h.replace(/\s/g, ""); //去除字符串中的Unicode空白
		alert(h);
		if (h == "登录成功！") {
			alert("登录成功")
		} else {
			form2.username.value = ""; //清空用户名文本框 
			form2.pwd.value = ""; //清空密码文本框
			form2.username.focus(); //让用户名文本框获得焦点
		}

	}
</script>


</head>
<body>
	<form name="form2" method="post" action="" id="form2">
		用户名： <input type="text" name="username">
		密&nbsp;&nbsp;&nbsp;&nbsp;码： <input type="password" name="pwd">
		<input name="Submit" type="button" onclick="loginSubmit(this.form)"
			value="登录">
	</form>
</body>
</html>