(function (angular, undefined) {

    var model = 'store';
    var entity = 'cashTransactionsScan';
    var entityUrl = '/' + model + '/' + entity;
    window.wechatAppId = 'wx9b369a21e6ec245d';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.memberCheck = false;

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

        $scope.getIntegralRate = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.integralRate = re.content.integralRate;
                $scope.getAccount();
            });
        }

        $scope.getBrokerage=function(){
            if(/^[0-9]+(.[0-9]{1,2})?$/.test($scope.money)){
                $scope.brokerage=$rootScope.getMoney(parseInt($rootScope.getMoney($scope.money*($scope.integralRate/100)*100))/100.0);
                //$scope.checkSellerAccount();
                $scope.isShowRecharge=$scope.brokerage<=$scope.sellerAccount?'SUCCESS':'FAIL';
            }else{
                $scope.isShowRecharge='';
            }
        };

        //检查余额
        $scope.checkSellerAccount = function () {
            if(!$scope.getBrokerage()){
                return;
            }
            return $scope.brokerage>$scope.sellerAccount;
        };

        $scope.getAccount=function(){
            var url = window.basePath + '/order/OrderInfo/getUserAccount?userType=Seller';
            $http.get(url).success(function (re) {
                $scope.sellerAccount=re.content.cashCount;
            })
        };

        //根据卡号获取会员信息
        $scope.getMemberInfo = function () {
            if (!window.isEmpty($scope.memberCard) && $scope.memberCard != 'null') {
                $scope.notMember = false;
                var url = window.basePath + '/crm/Member/getMemberInfoByCard?card=' + $scope.memberCard;
                $http.get(url).success(function (re) {
                    $scope.memberInfo = re.content.items[0];
                    if(window.isEmpty($scope.memberInfo.canUse) || !$scope.memberInfo.canUse){
                        malert("该会员已被禁用");
                        return;
                    }
                    $scope.memberCheck = true;
                    $scope.memberIsPay = true;
                }).error(function () {
                    $scope.memberIsPay = true;
                    $scope.memberCheck = false;
                    $scope.notMember = true;
                });
            } else {
                malert('找不到该会员!');
            }
        };

        $scope.doAlipay = function () {
            var postData = {};
            postData["type"] = "orderPay";
            postData["payType"] = 4;
            if (isMobile()) {
                postData["clientType"] = "MobileWeb";
            } else {
                postData["clientType"] = "PcWeb";
            }
            postData["sellerId"] = getCookie('_seller_id');
            postData["totalFee"] = $scope.brokerage;
            postData["storeId"] = getCookie('_seller_id');
            postData["orderId"] = $scope.order.orderId;
            postData["memberId"] = $scope.memberInfo._id;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("store")[0]+'store/depositSuccess/orderId/'+$scope.order.orderId;
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

        //贵商银行支付
        $scope.doGpay = function () {
            var postData = {};
            postData["type"] = "orderPay";
            postData["payType"] = 18;
            if (window.isMobile() || window.isWechat()) {
                postData["channelType"] = "08";//WAP
            } else {
                postData["channelType"] = "07";//PC
            }
            postData["sellerId"] = getCookie('_seller_id');
            postData["totalFee"] = $scope.brokerage;
            postData["storeId"] = getCookie('_seller_id');
            postData["orderId"] = $scope.order.orderId;
            postData["memberId"] = $scope.memberInfo._id;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("store")[0]+'store/depositSuccess/orderId/'+$scope.order.orderId;
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
            postData["sellerId"] = getCookie('_seller_id');
            postData["totalFee"] = $scope.brokerage;
            postData["storeId"] = getCookie('_seller_id');
            postData["orderId"] = $scope.order.orderId;
            postData["memberId"] = $scope.memberInfo._id;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("store")[0]+'store/depositSuccess/orderId/'+$scope.order.orderId;
            var url = window.basePath + '/payment/Pay/prepay';
            $http.post(url, postData).success(function (re) {
                url = window.basePath + '/payment/Pay/startPay';
                postData = {};
                postData._id = re.content._id;
                var state = 'wechatMobile_'+postData._id;
                var uri = encodeURIComponent("http://s.phsh315.com/oauth_page/callback.jsp");
                var location='https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + wechatAppId + '&redirect_uri=' + uri +
                    '&response_type=code&scope=snsapi_base&state=' + state + '#wechat_redirect';
                window.location =location;
                // window.location.href = re.content.otherData1;
            }).error(function (ex) {
                console.log(ex)
            });
        }

        $scope.tradeType = function () {
            if(!$scope.notMember){
                if(window.isEmpty($scope.memberInfo.canUse) || !$scope.memberInfo.canUse){
                    malert("该会员已被禁用");
                    return;
                }
                if ($scope.memberCheck != true) {
                    malert("请输入正确的会员号!");
                    return;
                }
            }

            if ($scope.money == null || !/^[0-9]+(.[0-9]{1,2})?$/.test($scope.money)) {
                malert("请输入正确的消费金额!");
                return;
            }
            if ($scope.selectPay==3 && $scope.isShowRecharge!="SUCCESS"){
                malert("余额不足!");
                return;
            }
            $scope.submitBtnCheck = 'disabled';
            $scope.menuCheck=false;
            <!-- 这里应传入会员交易金额,而不是佣金;调取微信支付宝的时候传入的是佣金 -->
            if ($scope.money != null && /^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                var url = window.basePath + '/order/OrderInfo/createOrderOffline';
                var data={
                    money:$scope.money,
                    sellerPayType:$scope.selectPay
                };
                //
                if(!$scope.notMember){
                    data['memberId'] = $scope.memberInfo._id;
                }else{
                    if(window.isEmpty($scope.memberCard) || !/^[1][3456789][0-9]{9}$/.test($scope.memberCard)){
                        malert("不是一个手机号码");
                        return;
                    }
                    data['mobile'] = $scope.memberCard
                }
                //if($scope.selectPay==3){
                //    data.pwd=$scope.pwd1 + $scope.pwd2 + $scope.pwd3 + $scope.pwd4 + $scope.pwd5 + $scope.pwd6;
                //}
                $http.post(url, data).success(function (re) {
                    $scope.order=re.content;
                    if ($scope.selectPay == 4) {
                        $scope.doAlipay();
                    } else if ($scope.selectPay == 10) {
                        $scope.doWechat();
                    } else if($scope.selectPay == 18){
                        $scope.doGpay();
                    }else if($scope.selectPay==3){
                        $scope.menuCheck=false;
                        $scope.active = true;
                        $scope.activeStatus = true;
                        $scope.countDown();
                    }
                }).error(function () {
                    $scope.submitBtnCheck=false;
                });
            } else {
                malert('请输入正确的消费金额');
                $scope.submitBtnCheck=true;
            }
        }

        $scope.submitFun2=function(){
            if($scope.selectPay==3){
                var url = window.basePath + '/account/Seller/isSellerSetPayPwd';
                $http.get(url).success(function (re) {
                    $scope.isGoPagePwd=re.content.flag;
                    if(!$scope.isGoPagePwd){
                        $scope.menuCheck=true;
                        $scope.pwd1 = null;
                        $scope.pwd2 = null;
                        $scope.pwd3 = null;
                        $scope.pwd4 = null;
                        $scope.pwd5 = null;
                        $scope.pwd6 = null;
                        setTimeout(function(){
                            $(".enterInput input").eq(0).focus();
                        },200)
                    }
                });
            }else{
                $scope.tradeType();
            }
        }

        //倒计时
        $scope.countDown = function () {
            $scope.countdown--;
            if ($scope.countdown == 0) {
                if ($scope.activeStatus == true) {
                    $rootScope.goPage("/store/store");
                } else {
                    $scope.countdown = 4;
                    $scope.active = false;
                    $rootScope.goPage("/store/cashTransactionsScan");
                }
                return;
            } else {
                (function () {
                    $rootScope.$apply($scope.countDown);
                }).delay(1);
            }
        }

        $scope.closePwd=function(){
            $scope.menuCheck=false;
            $scope.submitBtnCheck=false;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '现金交易');
                $scope.memberIsPay = false;
                $scope.countdown = 4;

                $scope.active = false;
                $scope.activeStatus = false;
                $scope.getIntegralRate();
                $scope.memberCard = null;
                $scope.menuCheck = false;
                $scope.isGoPagePwd=false;

                // if(window._isWechat){
                //     $scope.selectPay = 10;
                // }else{
                //     $scope.selectPay = 4;
                // }
                $scope.selectPay = $rootScope.getDefaultPayType();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})
(angular);
