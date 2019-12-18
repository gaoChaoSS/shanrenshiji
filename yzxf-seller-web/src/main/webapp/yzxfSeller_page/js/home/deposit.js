(function (angular, undefined) {
    var model = "home";
    var entity = "deposit";
    window.wechatAppId = 'wx9b369a21e6ec245d';
    window.app.register.controller('home_deposit_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.submitCheck = 'disabled';
        //确认充值按钮判断
        $scope.submitBtn = function () {
            if(!/^[0-9]+(.[0-9]{1,})?$/.test($scope.money)){
                $scope.submitCheck = 'disabled';
            }else{
                $scope.submitCheck = false;
            }
        }
        $scope.isGoPage=function(){
            if($scope.type=='seller'){
                $rootScope.goPage("/store/storeAccount");
            }else if($scope.type=='factor'){
                $rootScope.goPage("/home/cardIssuingAccount");
            }
        }

        $scope.doAlipay = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = 4;
            if (isMobile()) {
                postData["clientType"] = "MobileWeb";
            } else {
                postData["clientType"] = "PcWeb";
            }
            postData["sellerId"] = $scope.order.memberId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.sellerId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("home")[0]+'store/depositSuccess/orderNo/'+$scope.order.orderNo+'/userType/'+$scope.type;
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
                // window.location.href = re.content.otherData1;
            }).error(function (ex) {
                console.log(ex)
            });
        }

        $scope.doGpay = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = 18;
            if (window.isMobile()|| window.isWechat()) {
                postData["channelType"] = "08";
            } else {
                postData["channelType"] = "07";
            }
            postData["sellerId"] = $scope.order.memberId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.sellerId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("home")[0]+'store/depositSuccess/orderNo/'+$scope.order.orderNo+'/userType/'+$scope.type;
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
                // window.location.href = re.content.otherData1;
            }).error(function (ex) {
                console.log(ex)
            });
        }

        $scope.doWechat = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = 10;
            if (isWechat()) {
                postData["clientType"] = "JsApi";
            } else {
                postData["clientType"] = "PcWeb";
            }
            postData["sellerId"] = $scope.order.memberId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.sellerId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("home")[0]+'store/depositSuccess/orderNo/'+$scope.order.orderNo+'/userType/'+$scope.type;
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

        //倒计时
        $scope.countDownFun = function () {
            $scope.countdown--;
            if ($scope.countdown == 0) {
                if ($scope.activeStatus) {
                    $rootScope.goPage($scope.type == 'seller'?"/store/storeAccount":"/home/cardIssuingAccount");
                } else if (!$scope.activeStatus && ($scope.type == 'seller' || $scope.type == 'factor')) {
                    $scope.countdown = 4;
                    $scope.active = false;
                    $rootScope.goPage($scope.type == 'seller'?"/store/storeAccount":"/home/cardIssuingAccount");
                }
            } else {
                (function () {
                    $rootScope.$apply($scope.countDownFun);
                }).delay(1);
            }
        };

        $scope.getSubmit = function () {
            var url = window.basePath + '/order/OrderInfo/checkOrderStatus';
            var data = {
                orderId: $scope.order._id,
                payType: $scope.selectPay
            };
            $http({
                method: 'post',
                hideLoading: true,
                url: url,
                data:data
            }).success(function (reData) {
                if (reData.content.payStatus == "SUCCESS") {
                    clearInterval($scope.queryTask);
                    $scope.active = true;
                    $scope.activeStatus = true;
                    $scope.countDownFun();
                }
            });
        };

        $scope.genQrCodeFun = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = $scope.selectPay;
            postData["clientType"] = "QrCode";
            postData["sellerId"] = $scope.order.memberId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.sellerId;
            var url = window.basePath + '/payment/Pay/prepay';
            $http.post(url, postData).success(function (re) {
                url = window.basePath + '/payment/Pay/startPay';
                postData = {};
                postData._id = re.content._id;
                $scope.payId = postData._id;
                $scope.payOrderNo = re.content.orderId;
                $http.post(url, postData).success(function (reData) {
                    if (reData.content && reData.content.code_url) {
                        $scope.isShowQrCodePage = true;
                        // 生产2维码图片
                        var qr = qrcode(10, 'H');
                        qr.addData(reData.content.code_url);
                        qr.make();
                        $(".qrcodeContentCon").html(qr.createImgTag());
                        $(".qrcodeContentCon > img").css({
                            width: '80%',
                            height: 'auto'
                        });
                        $scope.queryTask = setInterval(function () {
                            $scope.getSubmit();
                            if ($location.path().indexOf("deposit") == -1) {
                                clearInterval($scope.queryTask);
                            }
                        }, 5000);
                    } else {
                        alert('生成支付二维码失败，请稍后再试');
                    }
                }).error(function (ex) {
                    console.log(ex)
                });
            }).error(function (ex) {
                console.log(ex)
            });
        }

        $scope.submitOrder = function () {
            $scope.submitCheck = 'disabled';
            if ($scope.money != null && /^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                var url = window.basePath + '/order/OrderInfo/createRecharge';
                var data={
                    totalPrice:$scope.money,
                    userType:$scope.type
                }
                $http.post(url, data).success(function (re) {
                    $scope.order=re.content;
                    //电脑用扫码，手机用微信内支付
                    if(window.isMobile()){
                        if ($scope.selectPay == 4) {
                            $scope.doAlipay();
                        } else if ($scope.selectPay == 10) {
                            $scope.doWechat();
                        } else if($scope.selectPay == 18){
                            $scope.doGpay();
                        }
                    }
                    //else{
                    //    $scope.genQrCodeFun();
                    //}
                }).error(function () {
                    $scope.submitCheck=false;
                });
            } else {
                malert('请输入正确的消费金额');
                $scope.submitCheck=true;
            }
        }

        $scope.getUserType= function(text){
            if(window.isEmpty(text)){
                return;
            }
            if(text.substring(0,1)==='s'){
                return "Seller";
            }else if(text.substring(0,1)==='f'){
                return "Factor";
            }else{
                return text;
            }
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '余额充值');
                //$scope.countdown = 4;
                if(window._isWechat){
                    $scope.selectPay=10;
                }else{
                    $scope.selectPay=4;
                }
                $scope.countdown=4;
                $scope.isMobile=window.isMobile();
                $scope.queryTask = null;
                $scope.isShowQrCodePage = false;
                //$scope.active = false;
                //$scope.activeStatus = false;
                $scope.type = $rootScope.pathParams.type;
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);