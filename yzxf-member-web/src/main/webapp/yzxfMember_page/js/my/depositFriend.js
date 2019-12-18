(function(angular, undefined) {
    var model = "my";
    var entity = "depositFriend";
    window.wechatAppId = 'wx9b369a21e6ec245d';
    window.app.register.controller('my_depositFriend_Ctrl', function($rootScope, $scope, $location,$interval, $http, $element, $compile) {

        $scope.menuCheck = false;
        $scope.selectPay=3;
        //支付密码输入框
        $scope.getPwdWin = function () {
            if (window.isEmpty($scope.phone)) {
                malert('请输入需要充值的号码');
            } else if ($scope.money < 0) {
                malert('请输入正数');
            } else if(window.isEmpty($scope.money)){
                malert('请输入充值金额');
            }else if($scope.selectPay==3){
                $scope.menuCheck = true;
                $scope.pwd1 = null;
                $scope.pwd2 = null;
                $scope.pwd3 = null;
                $scope.pwd4 = null;
                $scope.pwd5 = null;
                $scope.pwd6 = null;
                setTimeout(function(){
                    $(".enterInput input").eq(0).focus();
                },200)
            }else{
                $scope.submitOrder();
            }
            return false;
        }
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
                window.location =
                    'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + wechatAppId + '&redirect_uri=' + uri +
                    '&response_type=code&scope=snsapi_base&state=' + state + '#wechat_redirect';
                // window.location.href = re.content.otherData1;
            }).error(function (ex) {
                console.log(ex)
            });
        }

        //检查余额
        $scope.checkAccount = function () {
            // if($scope.isMobile && !window.isWechat()){
            //     $scope.check = 'disabled';
            //     malert("手机浏览器暂不支持支付,请在前往微信公众号支付");
            //     return;
            // }
            if ($scope.money == null || !/^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                malert("请输入充值金额");
                return;
            }
            var url = window.basePath + '/order/OrderInfo/checkUserAccount?userType=Member&checkUpper=true&money='+$scope.money+"&userId="+$scope.phone;
            $http.get(url).success(function (re) {
                if(re.content.status=='SUCCESS'){
                    $scope.submitOrder();
                }else{
                    malert("代充值用户积分已达到上限");
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
                            if ($location.path().indexOf("depositFriend") == -1) {
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
            $scope.menuCheck=false;
            if(!/^1[3456789]{1}\d{9}$/.test($scope.phone)){
                malert("手机号码格式错误!");
                return;
            }
            if(!/^\d+(?:\.\d{1,2})?$/.test($scope.money)){
                malert("充值金额错误!");
                return;
            }
            if ($scope.money != null && /^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                var url = window.basePath + '/order/OrderInfo/createRecharge';
                var data = {
                    totalPrice: $scope.money,
                    userType: "memberFriend",
                    payType:$scope.selectPay,
                    friendMobile:$scope.phone
                }
                //if($scope.selectPay==3){
                //    data.payPwd=$scope.pwd1 + $scope.pwd2 + $scope.pwd3 + $scope.pwd4 + $scope.pwd5 + $scope.pwd6;
                //}
                $http.post(url, data).success(function (re) {
                    $scope.order = re.content;
                    //电脑用扫码，手机用微信内支付
                    if(window.isMobile()){
                        if ($scope.selectPay == 4) {
                            $scope.doAlipay();
                        } else if ($scope.selectPay == 10) {
                            $scope.doWechat();
                        }
                    }else{
                        $scope.genQrCodeFun();
                    }
                }).error(function () {
                    $scope.submitCheck = false;
                });
            } else {
                malert('请输入正确的消费金额');
                $scope.submitCheck = true;
            }
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
                window.setWindowTitle($rootScope, '代购积分');
                $scope.isSuccess=false;
                if(window._isWechat){
                    $scope.selectPay=10;
                }else{
                    $scope.selectPay=4;
                }
                $scope.countdown=4;
                $scope.submitCheck=false;
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);