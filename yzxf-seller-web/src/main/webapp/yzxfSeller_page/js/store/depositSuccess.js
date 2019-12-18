(function (angular, undefined) {
    var model = "store";
    var entity = "depositSuccess";
    window.app.register.controller('store_depositSuccess_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {
        $scope.getOrderStatus=function(){
            if(window.isEmpty($rootScope.pathParams.orderNo) && window.isEmpty($rootScope.pathParams.orderId) && window.isEmpty($rootScope.pathParams.payId)){
                $scope.isSuccess=3;
                $scope.getIcon();
                return;
            }
            var url = window.basePath + '/order/OrderInfo/checkOrderStatus';
            var data = {
                orderId: $rootScope.pathParams.orderId,//orderId,orderNo,payId,只传其中一个就可以
                orderNo: $rootScope.pathParams.orderNo,
                payId: $rootScope.pathParams.payId,
                payType: window.isEmpty($rootScope.pathParams.payType)?'':$rootScope.pathParams.payType
            };
            $http({
                method: 'post',
                hideLoading: true,
                url: url,
                data:data
            }).success(function (re) {
                $scope.orderInfo = re.content;
                if(window.isEmpty(re.content.orderId) && window.isEmpty(re.content.orderNo)){
                    $scope.isSuccess=4;
                    $scope.getIcon();
                    $interval.cancel($scope.countTime);
                    return;
                }
                re.content.payStatus?$scope.isSuccess=1:$scope.isSuccess=2;
                if(re.content.payStatus!="SUCCESS"){
                    return;
                }
                $scope.getIcon();
                $interval.cancel($scope.countTime);
                $scope.countTimeNum=3;
                var goPageTime=$interval(function () {
                    $scope.countTimeNum--;
                    if($scope.countTimeNum==0){
                        $interval.cancel(goPageTime);
                        if(!window.isEmpty($rootScope.pathParams.userType) && $rootScope.pathParams.userType=='factor'){
                            $rootScope.goPage("/home/index");
                        }else{
                            $rootScope.goPage("/store/store");
                        }
                    }
                    if ($location.path().indexOf("depositSuccess") == -1) {
                        $interval.cancel(goPageTime);
                    }
                }, 1000);
            }).error(function (ex) {
                $scope.isSuccess=2;
                $interval.cancel($scope.countTime);
            });
        }

        //0:处理中;1:成功;2:失败
        $scope.getIcon=function(){
            if($scope.isSuccess==1){
                $scope.statusIcon= 'bgGreen2 icon-gou';
                $scope.statusText='支付成功';
            }else if($scope.isSuccess==2){
                $scope.statusIcon= 'bgNone orangeRed3 textSize130 icon-cuowu';
                $scope.statusText='支付失败';
            }else if($scope.isSuccess==3){
                $scope.statusIcon= 'bgNone orangeRed3 textSize130 icon-cuowu';
                $scope.statusText='获取订单失败';
            }else if($scope.isSuccess==4){
                $scope.statusIcon= 'bgNone orangeRed3 textSize130 icon-cuowu';
                $scope.statusText='订单已关闭';
            }else{
                $scope.statusIcon= 'bgBlue icon-gengduo';
                $scope.statusText='4444支付处理中';
            }
        }

        $scope.goMyWallet=function(){
            $interval.cancel($scope.countTime);
            if(!window.isEmpty($rootScope.pathParams.userType) && $rootScope.pathParams.userType=='factor'){
                $rootScope.goPage("/home/index");
            }else{
                $rootScope.goPage("/store/store");
            }
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '支付结果');
                $scope.isSuccess=0;
                $scope.getIcon();
                $scope.getOrderStatus();
                $scope.countTime=$interval(function () {
                    $scope.getOrderStatus();
                    if ($location.path().indexOf("depositSuccess") == -1) {
                        $interval.cancel($scope.countTime);
                    }
                }, 5000);
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);