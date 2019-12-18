<%@ page import="com.zq.kyb.core.model.Message" %>
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="com.zq.kyb.core.service.ServiceAccess" %>
<%
    pageContext.setAttribute("serverVersion", "0.091");
    String name = request.getServerName();
    pageContext.setAttribute("js_min", "");
%>
<%
    JSONObject respObj = new JSONObject();
    try {
        Message req = Message.newReqMessage("1:GET@/account/Oauth/callback");
        JSONObject c = req.getContent();
        c.put("code", request.getParameter("code"));
        c.put("state", request.getParameter("state"));
        Message resp = ServiceAccess.callService(req);
        respObj = resp.getContent();
    }catch (Exception ex){
        ex.printStackTrace();
//        response.sendRedirect("/yzxfMember/account/bind");
    }

%>

<script type="text/javascript" src="/js/lib/jquery-2.1.3${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/extend-1.0.js?_v=${serverVersion}"></script>

<body>
<div id="payForm"></div>
</body>

<script type="text/javascript">
    var content = <%=respObj.toString()%>;
    var state = '<%=request.getParameter("state")%>';
    var stateList = state.split("_");
    var postData;

    var getPay = function(){
        $.getJSON("/s_agent/api/payment/Pay/getPayById?payId="+stateList[1],function(re){
            postData=re.content;
            postData["openId"]=content.openId;
            startPay();
        });
    };

    function onBridgeReady() {
        if (WeixinJSBridge) {
            WeixinJSBridge.invoke(
                'getBrandWCPayRequest', window.jsapiParam,
                function (res) {
                    // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        //跳转到支付成功页面
                        if(/^[13]$/.test(postData.orderType)){//商家
                            window.location.href="http://s.phsh315.com/yzxfSeller/store/depositSuccess/payId/"+postData._id;
                        }else if(/^[47]$/.test(postData.orderType)){//发卡点
                            window.location.href="http://s.phsh315.com/yzxfSeller/store/depositSuccess/userType/factor/payId/"+postData._id;
                        }else if(/^[0568]|(11)$/.test(postData.orderType)){//会员
                            window.location.href="http://m.phsh315.com/yzxfMember/my/depositSuccess/payId/"+postData._id;
                        }
                    }
                }
            );
        }
    }

    var startPay = function(){
        var url ='/s_agent/api/payment/Pay/startPay';
        $.post(url, JSON.stringify(postData),function (re) {
            window.jsapiParam = re.content;
            if (typeof WeixinJSBridge == "undefined") {
                if (document.addEventListener) {
                    document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
                } else if (document.attachEvent) {
                    document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                    document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
                }
            } else {
                onBridgeReady();中
            }
        }).error(function (ex) {
            console.log(ex)
        });
    };

    //贵商银行支付调用
    var doGpay = function () {
        alert("调用贵商银行支付");
        var url ='/s_agent/api/payment/Pay/prepayGpay';
        $.post(url, JSON.stringify({
            orderId:stateList[3],
            payFrom:stateList[1],
            openId:content.openId,
            channelType:stateList[4],
        }),function (re) {
            $("#payForm").html(re.content.requestHtml);
            $("#payForm").find("form").submit();
        })
    }

    var check=function(){
        if(stateList[2]=='login'){
            var url ="";
            if(content.token!=null && content.token!=''){
                url = "http://m.yzxf8.cn/yzxfMember/my/payByFixedQrCode";
            }else{
                url = "http://m.yzxf8.cn/yzxfMember/account/bind/openId/"+content.openId;
            }
            if(stateList[3]!=null){
                url+="/sellerId/"+stateList[3];
            }
            window.location.href=url;
        } else if(stateList[2]=='gpay'){
            doGpay();
        } else{
            getPay();
        }
    };
    check();
</script>




<%--<%@ page import="com.zq.kyb.core.model.Message" %>
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="com.zq.kyb.core.service.ServiceAccess" %>
<%
    pageContext.setAttribute("serverVersion", "0.087");
    String name = request.getServerName();
    //if ("www.youlai01.com".equals(name)) {
    pageContext.setAttribute("js_min", "");
    //}
%>
<%
    JSONObject respObj = new JSONObject();
    try {
        Message req = Message.newReqMessage("1:GET@/account/Oauth/callback");
        JSONObject c = req.getContent();
        c.put("code", request.getParameter("code"));
        c.put("state", request.getParameter("state"));
        Message resp = ServiceAccess.callService(req);
        respObj = resp.getContent();
    }catch (Exception ex){
        response.sendRedirect("/yzxfMember/account/bind");
    }

%>

<script type="text/javascript" src="/js/lib/jquery-2.1.3${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/extend-1.0.js?_v=${serverVersion}"></script>

<script type="text/javascript">
    var content = <%=respObj.toString()%>;
    var state = '<%=request.getParameter("state")%>';

    var stateList = state.split("_");

    if(!isEmpty(content.token)) {
        setCookie('___MEMBER_TOKEN', content.token);
        setCookie('_member_mobile', content.mobile);
        setCookie('_member_icon', content.icon);
        setCookie('_member_id', content.id);
        if(!isEmpty(stateList[2])){
            window.location.href="/yzxfMember/my/payByFixedQrCode/sellerId/"+stateList[2];
        }else{
            window.location.href = "/yzxfMember/my/my";
        }
    }else {
        setCookie('_oauth_data', JSON.stringify(content), null, 1);
//        alert(JSON.stringify(content));
        if(!isEmpty(stateList[2])){
            window.location.href = "/yzxfMember/account/bind/sellerId/"+stateList[2];
        }else{
            window.location.href = "/yzxfMember/account/bind";
        }
    }
</script>--%>


