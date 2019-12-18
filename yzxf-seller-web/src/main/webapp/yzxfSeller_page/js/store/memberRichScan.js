(function (angular, undefined) {

    var model = 'store';
    var entity = 'memberRichScan';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.getBrokerage=function(){
            if(/^[0-9]+(.[0-9]{1,})?$/.test($scope.memberConsumption)){
                $scope.brokerage=$rootScope.getMoney(parseInt($rootScope.getMoney($scope.memberConsumption*($scope.integralRate/100)*100))/100.0);
            }
        };
        $scope.getIntegralRate = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.sellerInfo = re.content;
                $scope.integralRate = re.content.integralRate;
            });
        }
        //根据卡号获取会员信息
        $scope.getMemberInfo = function () {
            if (window.isEmpty($scope.memberCard)) {
                return;
            }
            var url = window.basePath + '/crm/Member/getMemberInfoByCard?card=' + $scope.memberCard;
            $http.get(url).success(function (re) {
                $scope.memberInfo = re.content.items[0];
                if(window.isEmpty($scope.memberInfo.canUse) || !$scope.memberInfo.canUse){
                    malert("该会员已被禁用");
                    return;
                }
                $scope.memberIsPay = true;
                $scope.memberCheck = true;
            });
        };
        //产生二维码/订单
        $scope.produce2DCode = function () {
            if(!$scope.memberInfo.canUse){
                malert("该会员已被禁用");
                return;
            }
            if($scope.memberConsumption==0){
                malert("金额不能为0!");
                return;
            }
            if (!window.isEmpty($scope.memberConsumption) && !window.isEmpty($scope.memberCard) && /^(([1-9]\d{0,9})|0)(\.\d{1,2})?$/.test($scope.memberConsumption)) {
                var url = window.basePath + '/order/OrderInfo/createOrderOfflineByScan?card=' + $scope.memberCard;
                var data = {
                    memberId: $scope.memberInfo._id,
                    totalPrice: $scope.memberConsumption,
                    payType:$scope.selectPay
                };
                $http.post(url, data).success(function (re) {
                    $scope.orderTemp = re.content;
                    $scope.memberCheck = true;
                    $rootScope.memberCard = $scope.memberCard;
                    $rootScope.memberConsumption = $scope.memberConsumption;
                    if(re.content.isRepeat){
                        $scope.isRepeat=true;
                        return;
                    }
                    if($scope.selectPay=='3'){//商家发起订单,会员在会员版支付
                        $scope.goPage("/store/depositSuccess/orderNo/"+re.content.orderNo+"/payType/3");
                    }else{
                        $scope.goPage("/store/qrCode/memberType/member/orderNo/" + re.content.orderNo + "/payType/" + $scope.selectPay + "/orderId/"
                            + re.content._id);
                    }
                });
            } else if (window.isEmpty($scope.memberConsumption)) {
                malert('请输入会员消费金额');
            } else if (!/^(([1-9]\d{0,9})|0)(\.\d{1,2})?$/.test($scope.memberConsumption)) {
                malert('请输入正确的消费金额:大于0且最多有2为小数');
            } else {
                malert('请输入会员卡号');
            }
        };

        $scope.setFlag=function(name){
            $scope[name]=!$scope[name];
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '会员扫码');
                $rootScope.isLoginPage = true;
                $scope.memberIsPay = false;
                $scope.s2DCode = false;
                $scope.isRepeat = false;
                $scope.memberCard = null;
                $scope.memberConsumption = null;

                //$scope.memberCard = '13888888002';
                //$scope.memberConsumption = 0.01;
                $scope.isWechat = window.isWechat();
                // if($scope.isWechat){
                    $scope.selectPay = 10;
                // }else{
                //     $scope.selectPay = 4;
                // }
                $scope.getIntegralRate();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

