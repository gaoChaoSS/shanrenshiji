(function (angular, undefined) {
    var model = "my";
    var entity = "deposit";
    window.wechatAppId = 'wx9b369a21e6ec245d';
    window.app.register.controller('my_deposit_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.check = 'disabled';
        $scope.moneyCheck = 'disabled';
        //$scope.selectPay = 4;
        //确认充值按钮判断
        $scope.submitBtn = function () {
            if(!/^[0-9]+(.[0-9]{1,})?$/.test($scope.money)){
                $scope.check = 'disabled';
            }else{
                $scope.check = false;
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
            postData["sellerId"] = "member";
            postData["totalFee"] = $scope.money;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("my")[0]+'my/depositSuccess/orderNo/'+$scope.order.orderNo;
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
            if (window.isMobile()  || window.isWechat()) {
                postData["channelType"] = "08";
            } else {
                postData["channelType"] = "07";
            }
            postData["sellerId"] = "member";
            postData["totalFee"] = $scope.money;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("my")[0]+'my/depositSuccess/orderNo/'+$scope.order.orderNo;
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
            postData["type"] = "pushCash";
            postData["payType"] = 10;
            if (isWechat()) {
                postData["clientType"] = "JsApi";
            } else {
                postData["clientType"] = "PcWeb";
            }
            postData["sellerId"] = "member";
            postData["totalFee"] = $scope.money;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("my")[0]+'my/depositSuccess/orderNo/'+$scope.order.orderNo;
            var url = window.basePath + '/payment/Pay/prepay';
            $http.post(url, postData).success(function (re) {
                url = window.basePath + '/payment/Pay/startPay';
                postData = {};
                postData._id = re.content._id;
                var state = "wechatMobile_"+postData._id;
                var uri = encodeURIComponent("http://s.phsh315.com/oauth_page/callback.jsp");
                var url ='https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + wechatAppId + '&redirect_uri=' + uri +
                '&response_type=code&scope=snsapi_base&state=' + state + '#wechat_redirect';
                window.location =url;
                // window.location.href = re.content.otherData1;
            }).error(function (ex) {
                console.log(ex)
            });
        }

        $scope.submitOrder = function () {
            $scope.check = 'disabled';
            // if($scope.isMobile && !window.isWechat()){
            //     malert("手机浏览器暂不支持支付");
            //     return;
            // }
            if ($scope.money != null && /^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                var url = window.basePath + '/order/OrderInfo/createRecharge';
                var data = {
                    totalPrice: $scope.money,
                    userType: "member"
                }
                $http.post(url, data).success(function (re) {
                    $scope.order = re.content;
                    //电脑用扫码，手机用微信内支付
                    if(window.isMobile()){
                        if ($scope.selectPay == 4) {
                            $scope.doAlipay();
                        } else if ($scope.selectPay == 10) {
                            $scope.doWechat();
                        } else if ($scope.selectPay == 18){
                            $scope.doGpay();
                        }
                    }else{
                        $scope.genQrCodeFun();
                    }
                }).error(function () {
                    $scope.check = false;
                });
            } else {
                malert('请输入正确的消费金额');
                $scope.check = true;
            }
        }

        //检查余额
        $scope.checkAccount = function () {
            if ($scope.money == null || !/^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                return;
            }
            var url = window.basePath + '/order/OrderInfo/checkUserAccount?userType=Member&checkUpper=true&money='+$scope.money;
            $http.get(url).success(function (re) {
                if(re.content.status=='SUCCESS'){
                    $scope.submitOrder();
                }else{
                    malert("您的积分上限为5000分，请核查后购买");
                }
            })
        };

        //倒计时
        $scope.countDownFun = function () {
            $scope.countdown--;
            if ($scope.countdown == 0) {
                if ($scope.activeStatus) {
                    $rootScope.goPage("/my/wallet");
                } else{
                    $scope.countdown = 4;
                    $scope.active = false;
                    $rootScope.goPage("/my/wallet");
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
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
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

        $scope.getRechargePensionRatio = function(){
            var url = window.basePath + '/order/Parameter/query?_type=rechargePensionRatio';
            $http.get(url).success(function(re){
                $scope.rechargePensionRatio = re.content.items[0].val + re.content.items[0].unit;
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.getRechargePensionRatio();
                window.setWindowTitle($rootScope, '购买积分');
                $scope.isMobile=window.isMobile();
                $scope.selectPay=$rootScope.getDefaultPayType();

                $scope.countdown = 4;

                //$scope.active = false;
                //$scope.activeStatus = false;
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);