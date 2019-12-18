(function (angular, undefined) {

    var model = 'my';
    var entity = 'orderInfo';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        //页面事件处理
        //获取订单关联的卡券信息
        $scope.getCoupon=function(){
            if(window.isEmpty($scope.orderInfo.couponId)){
                return;
            }
            var url = window.basePath + "/crm/Coupon/getCouponByLinkId?isGetAll=true&linkId="+$scope.orderInfo.couponId;
            $http.get(url).success(function(re){
                $scope.couponLink=re.content.couponLink;
                $scope.coupon=re.content.coupon;
            });
        }
        $scope.getOrderInfo = function(){
            var url = window.basePath + '/order/OrderInfo/queryMyOrder?orderId='+$scope.orderId;
            $http.get(url).success(function(re){
                $scope.orderInfo = re.content.orderList[0];
                if($scope.orderInfo.orderStatus!=100){
                    $scope.orderInfo.pensionMoney='商品收货7天后,将赠送养老金';
                }else{
                    $scope.orderInfo.pensionMoney+="元";
                }
                $scope.getCoupon();
            })
        };

        $scope.getOrderStatus = function () {
            if ($scope.orderInfo.orderStatus == 0) {
                return "草稿";
            } else if ($scope.orderInfo.orderStatus == 1) {
                return "未支付";
            } else if ($scope.orderInfo.orderStatus == 2) {
                return "已支付,等待商家发货";
            } else if ($scope.orderInfo.orderStatus == 3) {
                return "商家打包制作中";
            } else if ($scope.orderInfo.orderStatus == 4) {
                return "商家已发货";
            } else if ($scope.orderInfo.orderStatus == 5) {
                return "买家已收货";
            } else if ($scope.orderInfo.orderStatus == 6) {
                return "[退货]买家申请退货";
            } else if ($scope.orderInfo.orderStatus == 7) {
                return "[退货]请填写发货信息";
            } else if ($scope.orderInfo.orderStatus == 8) {
                return "[退货]已发货";
            } else if ($scope.orderInfo.orderStatus == 9) {
                return "[退货]已退款";
            } else if ($scope.orderInfo.orderStatus == 100) {
                if(!window.isEmpty($scope.orderInfo.isReturn) && !$scope.orderInfo.isReturn){
                    return "商家拒绝退款,已结算";
                }else{
                    return "已结算";
                }
            }
        }

        $scope.getPayType=function(type){
            if(type==3){
                return '积分';
            }else if(type==4){
                return '支付宝';
            }else if(type==6){
                return '现金';
            }else if(type==10){
                return '微信';
            }else{
                return '其他';
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '订单详情');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
                $scope.orderInfo=[];
                $scope.orderId = $rootScope.pathParams.orderId;
                $scope.getOrderInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);