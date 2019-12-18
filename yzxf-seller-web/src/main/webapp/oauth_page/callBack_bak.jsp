<%@ page import="com.zq.kyb.core.model.Message" %>
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="com.zq.kyb.core.service.ServiceAccess" %>
<%
    pageContext.setAttribute("serverVersion", "0.088");
    String name = request.getServerName();
    pageContext.setAttribute("js_min", "");
%>
<%--<%--%>
<%--JSONObject respObj = new JSONObject();--%>
<%--try {--%>
<%--Message req = Message.newReqMessage("1:GET@/account/Oauth/callback");--%>
<%--JSONObject c = req.getContent();--%>
<%--c.put("code", request.getParameter("code"));--%>
<%--c.put("state", request.getParameter("state"));--%>
<%--Message resp = ServiceAccess.callService(req);--%>
<%--respObj = resp.getContent();--%>
<%--}catch (Exception ex){--%>
<%--ex.printStackTrace();--%>
<%--//        response.sendRedirect("/yzxfMember/account/bind");--%>
<%--}--%>

<%--%>--%>

<script type="text/javascript" src="/js/lib/jquery-2.1.3${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/extend-1.0.js?_v=${serverVersion}"></script>

<body>
<div id="payForm"></div>
</body>

<script type="text/javascript">
    var orderType;

    function onBridgeReady() {
        if (WeixinJSBridge) {
            WeixinJSBridge.invoke(
                'getBrandWCPayRequest', window.jsapiParam,
                function (res) {
                    // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        if(/^[13]$/.test(orderType)){//商家
                            window.location.href="http://s.phsh315.com/yzxfSeller/store/depositSuccess/payId/"+getQueryString("_id");
                        }else if(/^[47]$/.test(orderType)){//发卡点
                            window.location.href="http://s.phsh315.com/yzxfSeller/store/depositSuccess/userType/factor/payId/"+getQueryString("_id");
                        }else if(/^[0568]|(11)$/.test(orderType)){//会员
                            window.location.href="http://m.phsh315.com/yzxfMember/my/depositSuccess/payId/"+getQueryString("_id");
                        }
                    } else {
//                            alert(res.err_code + res.err_desc + res.err_msg);
                    }
                }
            );
        }
    }

    var startPay = function(){
        var url ='/s_agent/api/payment/Pay/startPay';
        var reqCon = {_id: getQueryString("_id"), openId: getQueryString("openId")}
        $.post(url, JSON.stringify(reqCon),function (re) {
            orderType = re.content.orderType;
//            alert(orderType);
            window.jsapiParam = re.content.jsApiMap;
            if (typeof WeixinJSBridge == "undefined") {
                if (document.addEventListener) {
                    document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
                } else if (document.attachEvent) {
                    document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                    document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
                }
            } else {
                onBridgeReady();
            }
        }).error(function (ex) {
            console.log(ex)
        });
    };

    startPay();

</script>