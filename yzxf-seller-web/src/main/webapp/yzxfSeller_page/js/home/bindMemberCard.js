(function (angular, undefined) {

    var model = 'home';
    var entity = 'bindMemberCard';
    var entityUrl = '/' + model + '/' + entity;
    window.wechatAppId = 'wx9b369a21e6ec245d';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {
        $scope.check = 'disabled';

        //是否能点击提交按钮判断
        $scope.submitBtn = function () {
            if (!window.isEmpty($scope.memberCardId)) {
                $scope.check = false;
            }else{
                $scope.check = 'disabled';
            }
        }

        //支付密码输入框
        $scope.getPwdWin = function () {
            if ($scope.selectPay == 3) {
                var url = window.basePath + '/account/Factor/isFactorSetPayPwd';
                $http.get(url).success(function (re) {
                    $scope.isGoPagePwd = re.content.flag;
                    if (!$scope.isGoPagePwd) {
                        $scope.menuCheck = true;
                        $scope.pwd1 = null;
                        $scope.pwd2 = null;
                        $scope.pwd3 = null;
                        $scope.pwd4 = null;
                        $scope.pwd5 = null;
                        $scope.pwd6 = null;
                        setTimeout(function () {
                            $(".enterInput input").eq(0).focus();
                        }, 200)
                    }
                });
            } else {
                $scope.submitOrder();
            }
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
            postData["sellerId"] = getCookie("_factor_id");//收钱
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.sellerId;//付钱
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("home")[0] + 'store/depositSuccess/orderNo/' + $scope.order.orderNo+'/userType/factor';
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

        //贵商行支付
        $scope.dopay = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = 18;
            if (window.isMobile() || window.isWechat()) {
                postData["channelType"] = "08";//WAP
            } else {
                postData["channelType"] = "07";//PC
            }
            postData["sellerId"] = getCookie("_factor_id");//收钱
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.sellerId;//付钱
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("home")[0] + 'store/depositSuccess/orderNo/' + $scope.order.orderNo+'/userType/factor';
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
            postData["sellerId"] = getCookie("_factor_id");
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.sellerId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("home")[0] + 'store/depositSuccess/orderNo/' + $scope.order.orderNo+'/userType/factor';
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
                            if ($location.path().indexOf("bindMemberCard") == -1) {
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

        //倒计时
        $scope.countDownFun = function () {
            $scope.countdown--;
            if ($scope.countdown == 0) {
                if ($scope.activeStatus) {
                    $rootScope.goPage("/home/index");
                } else {
                    $scope.countdown = 4;
                    $scope.active = false;
                    $rootScope.goPage("/home/index");
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

        $scope.submitOrder = function () {
            $scope.submitCheck = 'disabled';
            $scope.menuCheck = false;
            var url = window.basePath + '/order/OrderInfo/createMemberCardByFactor';
            var data = {
                payType: $scope.selectPay,
                memberCardId: $scope.memberCardId,
                memberId: $rootScope.pathParams.memberId
            }
            if ($scope.selectPay == 3) {
                data.payPwd = $scope.pwd1 + $scope.pwd2 + $scope.pwd3 + $scope.pwd4 + $scope.pwd5 + $scope.pwd6;
            }
            $http.post(url, data).success(function (re) {
                $scope.order = re.content;

                //电脑用扫码，手机用微信内支付
                if ($scope.selectPay == 3) {
                    if (re.content.orderStatus == 100) {
                        $scope.countTimeNum = 3;
                        $scope.isSuccess = true;
                        $scope.goPageTime = $interval(function () {
                            $scope.countTimeNum--;
                            if ($scope.countTimeNum == 0) {
                                $interval.cancel($scope.goPageTime);
                                $rootScope.goPage("/home/index");
                            }
                            if ($location.path().indexOf("bindMemberCard") == -1) {
                                $interval.cancel($scope.goPageTime);
                            }
                        }, 1000);
                    }
                }else if(window.isMobile()){
                    if ($scope.selectPay == 4) {
                        $scope.doAlipay();
                    } else if ($scope.selectPay == 10) {
                        $scope.doWechat();
                    } else if($scope.selectPay ==18){
                        $scope.doGpay();
                    }
                }else{
                    $scope.genQrCodeFun();
                }
            }).error(function () {
                $scope.submitCheck = false;
            });
        }

        $scope.closePwd = function () {
            $scope.menuCheck = false;
            $scope.submitCheck = false;
        }

        //获取服务站未激活卡号
        $scope.getSendCardLog = function () {
            var data = {
                indexNum: 0,
                pageNo: 1,
                pageSize: 1,
                checkCard:1
            }
            var url = window.basePath + '/account/Seller/getNotSendCardLog';
            $http.post(url, data).success(function (re) {
                if(re.content!=null && re.content.sendCardList!=null){
                    $scope.memberCardId=re.content.sendCardList[0].startCardNo;
                }
            })
        }

        //获取服务站未激活卡号
        $scope.getActiveMoney = function () {
            var url = window.basePath + '/order/OrderInfo/getActiveMoney2';
            $http.get(url).success(function (re) {
                // $scope.payMoney=re.content.activeMoney;
                $scope.payMoney=0;
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '绑定会员卡');
                $scope.selectPay = $rootScope.getDefaultPayType();
                $scope.memberCardId = null;
                $scope.phoneNumber = null;
                $scope.isGoPagePwd = false;

                $scope.isMobile=window.isMobile();
                $scope.countdown=4;
                $scope.getSendCardLog();
                $scope.getActiveMoney();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);