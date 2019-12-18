(function (angular, undefined) {
    var model = "my";
    var entity = "depositSuccess";
    window.app.register.controller('my_depositSuccess_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {
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
                re.content.payStatus?$scope.isSuccess=1:$scope.isSuccess=2;
                if(re.content.payStatus!='SUCCESS'){
                    return;
                }
                $scope.getIcon();
                $interval.cancel($scope.countTime);
                $scope.countTimeNum=3;
                var goPageTime=$interval(function () {
                    $scope.countTimeNum--;
                    if($scope.countTimeNum==0){
                        $interval.cancel(goPageTime);
                        $rootScope.goPage("/my/my");
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
                $scope.statusIcon= 'bgJColor icon-gou';
                $scope.statusText='支付成功';
            }else if($scope.isSuccess==2){
                $scope.statusIcon= 'bgNone colorRed1 textSize130 icon-cuowu';
                $scope.statusText='支付失败';
            }else if($scope.isSuccess==3){
                $scope.statusIcon= 'bgNone colorRed1 textSize130 icon-cuowu';
                $scope.statusText='获取订单失败';
            }else{
                $scope.statusIcon= 'bgBblue icon-gengduo';
                $scope.statusText='支付处理中,请稍候';
            }
        }

        $scope.goMyWallet=function(){
            $interval.cancel($scope.countTime);
            $rootScope.goPage("/my/my");
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '支付结果');
                $scope.isSuccess=0;
                $scope.getIcon();
                $scope.countTime=$interval(function () {
                    $scope.getOrderStatus();
                    if ($location.path().indexOf("depositSuccess") == -1) {
                        $interval.cancel($scope.countTime);
                    }
                }, 3000);
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);