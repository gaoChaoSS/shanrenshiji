(function (angular, undefined) {
    var model = "my";
    var entity = "payByFixedQrCode";
    window.wechatAppId = 'wx9b369a21e6ec245d';

    window.app.register.controller('my_payByFixedQrCode_Ctrl', function ($rootScope, $scope, $location, $interval, $http, $element, $compile) {

        //支付密码输入框
        $scope.getPwdWin = function () {
            if ($scope.totalPrice == null || !/^[0-9]+(.[0-9]{1,2})?$/.test($scope.totalPrice)) {
                malert("请输入正确的金额!");
                return;
            }
            if($scope.payType==3){
                $scope.menuCheck = true;
                $scope.pwd1 = null;
                $scope.pwd2 = null;
                $scope.pwd3 = null;
                $scope.pwd4 = null;
                $scope.pwd5 = null;
                $scope.pwd6 = null;
                setTimeout(function () {
                    $(".enterInput input").eq(0).focus();
                }, 200);
            }else{
                $scope.submitOrder();
            }
        };

        $(".enterInput input").bind('keyup', function (e) {
            var currKey = 0, e = e || event;
            currKey = e.keyCode || e.which || e.charCode;
            if (currKey == 8) {
                $(this).val('');
                $(this).prev().val('').focus();
            } else {
                if ($(this).val() == '') {
                    return;
                }
                $(this).next().focus();
            }
        });

        $scope.doAlipay = function () {
            var postData = {};
            postData["type"] = "orderPay";
            postData["payType"] = 4;
            if (isMobile()) {
                postData["clientType"] = "MobileWeb";
            } else {
                postData["clientType"] = "PcWeb";
            }
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("my")[0]+'my/depositSuccess/orderId/'+$scope.order._id;
            var url = window.basePath + '/payment/Pay/prepay';
            $http.post(url, postData).success(function (re) {
                url = window.basePath + '/payment/Pay/startPay';
                postData = {};
                postData._id = re.content._id;
                $http.post(url, postData).success(function (reData) {
                    $("#payForm").html(reData.content.formStr);
                    $("#payForm").find("form").submit();
                }).error(function (ex) {
                    console.log(ex)
                });
            }).error(function (ex) {
                console.log(ex)
            });
        }

        //贵商行支付
        $scope.doGpay = function () {
            var postData = {};
            postData["type"] = "orderPay";
            postData["payType"] = 18;
           // postData["sellerName"] = $scope.seller.name;
            if (window.isMobile() || window.isWechat()) {
                postData["channelType"] = "08";//WAP
            } else {
                postData["channelType"] = "07";//PC
            }
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("my")[0]+'my/depositSuccess/orderId/'+$scope.order._id+'/sellerName/'+$scope.seller.name;
            var url = window.basePath + '/payment/Pay/prepay';
            $http.post(url, postData).success(function (re) {
                url = window.basePath + '/payment/Pay/startPay';
                postData._id = re.content._id;
                $http.post(url, postData).success(function (reData) {
                    $("#payForm").html(reData.content.requestHtml);
                    $("#payForm").find("form").submit();
                }).error(function (ex) {
                    console.log(ex)
                });
            }).error(function (ex) {
                console.log(ex)
            });
        }

        $scope.doWechat = function () {
            var postData = {};
            postData["type"] = "orderPay";
            postData["payType"] = 10;
            if (isWechat()) {
                postData["clientType"] = "JsApi";
            } else {
                postData["clientType"] = "PcWeb";
            }
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("store")[0]+'store/depositSuccess/orderId/'+$scope.order._id;
            var url = window.basePath + '/payment/Pay/prepay';
            $http.post(url, postData).success(function (re) {
                url = window.basePath + '/payment/Pay/startPay';
                postData = {};
                postData._id = re.content._id;
                var state = "wechatMobile_"+postData._id;
                var uri = encodeURIComponent("http://s.phsh315.com/oauth_page/callback.jsp");
                window.location =
                    'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + wechatAppId + '&redirect_uri=' + uri +
                    '&response_type=code&scope=snsapi_base&state=' + state + '#wechat_redirect';
                // window.location.href = re.content.otherData1;
            }).error(function (ex) {
                console.log(ex)
            });
        }

        $scope.submitOrder = function () {
            $scope.menuCheck=false;
            var url = window.basePath + "/order/OrderInfo/createOrderOfflineByMember";
            var data = {
                sellerId:$rootScope.pathParams.sellerId,
                payType:$scope.payType,
                totalPrice:$scope.totalPrice
            };
            if($scope.notLogin){
                if(window.isEmpty($scope.mobile)){
                    malert("请输入手机号码");
                    return;
                }
                data['mobile'] = $scope.mobile;
                if (!/^1[345789]{1}\d{9}$/.test($scope.mobile)) {
                    malert("请输入正确的手机号码!");
                    return;
                }
            }

            if($scope.payType==3){
                data.pwd=$scope.pwd1 + $scope.pwd2 + $scope.pwd3 + $scope.pwd4 + $scope.pwd5 + $scope.pwd6;
            }
            $http.post(url, data).success(function (re) {
                if($scope.payType==3){
                    $rootScope.goPage("/my/depositSuccess/orderNo/" + re.content.orderNo + "/payType/3");
                }else{
                    $scope.order=re.content;
                    if($scope.payType===4){
                        alert("对不起，支付宝已暂停使用，带来不便还望谅解!")
                        // $scope.doAlipay();
                    }else if($scope.payType===10){
                        $scope.doWechat();
                    }else if($scope.payType ==18){
                        $scope.doGpay();
                    }
                }
            });
        };

        $scope.getSellerIntro = function(){
            var url = window.basePath + '/account/StoreInfo/getSellerIntroById?sellerId='+$rootScope.pathParams.sellerId;
            $http.get(url).success(function (re) {
                $scope.seller = re.content;
            });
        };

        $scope.isLogin = function(){
            // $scope.checkLogin();
            // $rootScope.goPage("/account/reg/shareId/"+$rootScope.pathParams.sellerId + "/shareType/Seller/returnPayByFixedQrCode/1");
            $scope.notLogin = window.isEmpty(getCookie("_member_id"));
            $scope.getSellerIntro();
        };

        $scope.checkLogin = function(){
            // if(!window.isEmpty($rootScope.pathParams.token)){
            //     setCookie('___MEMBER_TOKEN', $rootScope.pathParams.token);
            // }else{
            //     $scope.goLogin();
            //     return;
            // }
            var url = window.basePath + '/crm/Member/getMyInfo';
            $http.get(url).success(function (re) {
                if(!window.isEmpty(re.content)){
                    // setCookie('_member_loginName', re.content.loginName);
                    // setCookie('_member_mobile', re.content.mobile);
                    // setCookie('_member_icon', re.content.icon);
                    // setCookie('_member_id', re.content._id);
                    $scope.getSellerIntro();
                }else{
                    $scope.goLogin();
                }
            });
        };

        $scope.goLogin = function(){
            $scope.isShowBind=true;
            var url = window.basePath + '/account/Oauth/start?' +
                'type='+(window.isMobile()?'wechatMobile':'wechatPc')+
                '&deviceId='+genUUID()+
                '&sellerId='+$rootScope.pathParams.sellerId;
            $http.get(url).success(function (re) {
                window.location.href=re.content.sendUrl;
            });
        }

        //$scope.delOrder = function () {
        //    var url = window.basePath + "/order/OrderInfo/del";
        //    $http.post(url, {_id: $scope.orderInfo.orderId}).success(function () {
        //        malert("已取消订单");
        //        $rootScope.goPage("/my/my");
        //    });
        //};

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '扫固定收银牌');
                $scope.menuCheck = false;
                $scope.isShowBind= false;
                $scope.payType=$rootScope.getDefaultPayType();
                $scope.isLogin();

            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);