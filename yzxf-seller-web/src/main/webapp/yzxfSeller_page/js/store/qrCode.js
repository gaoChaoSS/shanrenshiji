(function (angular, undefined) {

    var model = 'store';
    var entity = 'qrCode';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //根据卡号获取会员信息
        $scope.getMemberInfo = function () {
            var url = window.basePath + '/crm/Member/getMemberInfoByCard?card=' + $scope.memberCard;
            $http.get(url).success(function (re) {
                $scope.memberInfo = re.content.items[0];
                $scope.memberPayId = $scope.memberInfo._id;
                $scope.genQrCode();
            })
        }
        $scope.getIntegralRate = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.integralRate = re.content.integralRate;
            });
        }
        $scope.getSubmit = function () {
            if (!window.isEmpty($rootScope.pathParams.orderNo)) {
                var url = window.basePath + '/order/OrderInfo/checkOrderStatus';
                var data = {
                    orderId: $scope.orderId,
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
                        $scope.countDown();
                    }
                });
            } else {
                //malert("获取订单失败!");
            }
        }
        //倒计时
        $scope.countDown = function () {
            $scope.countdown--;
            if ($scope.countdown == 0) {
                if ($scope.activeStatus == true) {
                    $rootScope.goPage("/store/store");
                } else if ($scope.activeStatus == false && $scope.memberType == 'member') {
                    $scope.countdown = 4;
                    $scope.active = false;
                    $rootScope.goPage("/store/memberRichScan");
                } else if ($scope.activeStatus == false && $scope.memberType == 'other') {
                    $scope.countdown = 4;
                    $scope.active = false;
                    $rootScope.goPage("/store/otherRichScan");
                }
                return;
            } else {
                (function () {
                    $rootScope.$apply($scope.countDown);
                }).delay(1);
            }
        }
        $scope.queryTask = null;

        $scope.doGpay = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = 18;
            if (window.isMobile() || window.isWechat()) {
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

        $scope.getTitle=function(){
            var type=$rootScope.pathParams.payType;
            if(!window.isEmpty(type)){
                if(type=="10"){
                    return '微信';
                }else if(type=='4'){
                    return '支付宝'
                }
            }else{
                return '扫码';
            }
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.memberType = $rootScope.pathParams.memberType;
                if ($rootScope.memberConsumption != null && $rootScope.memberCard != null && $scope.memberType == 'member') {
                    window.setWindowTitle($rootScope, $scope.getTitle()+'支付');
                    $scope.countdown = 4;
                    $scope.active = false;
                    $scope.activeStatus = false;
                    $scope.getIntegralRate();
                    //$scope.getMemberInfo();
                    $scope.memberCard = $rootScope.memberCard;
                    $scope.money = $rootScope.memberConsumption;
                    $scope.payType = $rootScope.pathParams.payType;
                    $scope.orderNo = $rootScope.pathParams.orderNo;
                    $scope.orderId = $rootScope.pathParams.orderId;
                } else if ($rootScope.memberConsumption != null && $scope.memberType == 'other') {
                    window.setWindowTitle($rootScope, $scope.getTitle()+'支付');
                    $scope.countdown = 4;
                    $scope.memberId = null;
                    $scope.active = false;
                    $scope.activeStatus = false;
                    $scope.getIntegralRate();
                    $scope.money = $rootScope.memberConsumption;
                    $scope.orderNo = $rootScope.pathParams.orderNo;
                    $scope.orderId = $rootScope.pathParams.orderId;
                    $scope.payType = $rootScope.pathParams.payType;
                } else if ($scope.memberType == 'other') {
                    $scope.goPage("/store/otherRichScan");
                } else {
                    $scope.goPage("/store/memberRichScan");
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

