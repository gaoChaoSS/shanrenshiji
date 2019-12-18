(function (angular, undefined) {
    var model = 'payment';
    var entity = 'wechatJsApiReturnCode';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        function onBridgeReady() {
            if (WeixinJSBridge) {
                WeixinJSBridge.invoke(
                    'getBrandWCPayRequest', window.jsapiParam,
                    function (res) {
                        // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
                        if (res.err_msg == "get_brand_wcpay_request:ok") {
                            //TODO
                            //跳转到支付成功页面
                        } else {
                            alert(res.err_code + res.err_desc + res.err_msg);
                        }
                    }
                );
            }
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                var postData = {
                    id: getQueryString("state"),
                    code: getQueryString("code")
                };
                alert(JSON.stringify(postData));
                var url = window.basePath + '/payment/Pay/wechatJsApiPay';
                $http.post(url, postData).success(function (re) {
                    window.jsapiParam = re.content;
                    alert(JSON.stringify(window.jsapiParam));
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
                    alert(JSON.stringify(ex));
                    console.log(ex)
                });
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
