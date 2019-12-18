(function (angular, undefined) {

    var model = 'store';
    var entity = 'otherRichScan';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //产生二维码
        $scope.produce2DCode = function () {
            if (!window.isEmpty($scope.memberConsumption)&& /^(([1-9]\d{0,9})|0)(\.\d{1,2})?$/.test($scope.memberConsumption)) {
                $scope.memberCheck = true;
                $scope.showPCode();
            } else if(!/^(([1-9]\d{0,9})|0)(\.\d{1,2})?$/.test($scope.memberConsumption)){
                malert('请输入正确的消费金额:大于0且最多有2为小数');
            }else {
                malert('请输入会员消费金额');
            }
        }
        $scope.getIntegralRate = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.integralRate = re.content.integralRate;
            });
        }
        $scope.showPCode = function () {
            var url = window.basePath + '/order/OrderInfo/createOrderOfflineByScan';
            var data={
                totalPrice:$scope.memberConsumption,
                payType:$scope.selectPay
            };
            $http.post(url,data).success(function (re) {
                $scope.order = re.content;
                $scope.memberCheck = true;

                $rootScope.memberCard = $scope.memberCard;
                $rootScope.memberConsumption = $scope.memberConsumption;
                $scope.doGpay();
            });
        }
        $scope.getBrokerage=function(){
            if(/^[0-9]+(.[0-9]{1,})?$/.test($scope.memberConsumption)){
                $scope.brokerage=Math.floor($scope.memberConsumption*($scope.integralRate/100)*100)/100.0;
            }
        };

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

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '互联网收款');
                $scope.getIntegralRate();
                $rootScope.isLoginPage = true;
                $scope.memberIsPay = false;
                $scope.memberConsumption=null;
                $scope.brokerage=0;
                $scope.selectPay=10;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

