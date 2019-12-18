<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<script type="text/javascript" src="https://code.jquery.com/jquery-1.11.3.js"></script>
<script type="text/javascript" src="http://www.12306test.com/12306test-2.1.min.js"></script>
<div id="wb_validate"></div>
<button onclick="submit()">确认</button>
<script>
$(document).ready(function(){
	//初始化验证码
	$("#wb_validate").validate({select:'★'});
});
var defaluts = {
	//公钥（注册获取）
	publicKey:'33f354254bec9b722674000f725110c5',
	//12306test接口
	api : 'http://www.12306test.com/client.validate',
	select : '★'
};
function submit() {
	$.fn.validate.submit($("#wb_validate"), "${pageContext.request.contextPath}/post", {}, function(status) {
		if (status == 'SUCCESS') {
			alert("验证成功");
		} else if(status == 'AUTH') {
			alert("您的帐号未审核");
		} else {
			alert("验证失败");
			setTimeout($.fn.validate.verifyCode($("#wb_validate")), 5000);
		}
	});
}
</script>
</body>
</html>