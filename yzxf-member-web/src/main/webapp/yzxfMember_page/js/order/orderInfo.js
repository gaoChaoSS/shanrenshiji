(function (angular, undefined) {
    var model = "order";
    var entity = "orderInfo";
    window.app.register.controller('order_orderInfo_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.getPayType=function(type){
            if(type==3){
                return '余额';
            }else if(type==4){
                return '支付宝';
            }else if(type==6){
                return '现金';
            }else if(type==10){
                return '微信';
            }else{
                return '其他';
            }
        };

        $scope.getSpec=function(spec){
            var text = spec.name+' : '+spec.items;
            if(!window.isEmpty(spec.addMoney) && spec.addMoney!=0){
                text+=', 加价:'+spec.addMoney+'元';
            }
            return text;
        };

        //获取订单关联的卡券信息
        $scope.getCoupon=function(){
            if(window.isEmpty($scope.order.couponId)){
                return;
            }
            var url = window.basePath + "/crm/Coupon/getCouponByLinkId?isGetAll=true&linkId="+$scope.order.couponId;
            $http.get(url).success(function(re){
                $scope.couponLink=re.content.couponLink;
                $scope.coupon=re.content.coupon;
            });
        }

        $scope.getOrderInfo=function(){
            if(window.isEmpty($rootScope.pathParams.orderId)){
                malert("获取订单失败!");
                $rootScope.goBack();
                return;
            }
            var url = window.basePath + "/order/OrderInfo/queryMyOrder?orderId="+$rootScope.pathParams.orderId;
            $http.get(url).success(function(re){
                $scope.order = re.content.orderList[0];
                $scope.tempData={
                    orderId:$scope.order._id,
                    express:'',
                    expressNo:'',
                    freight:''
                };
                $scope.showMod[0]=($scope.order.orderStatus==2);
                $scope.showMod[1]=/^[456789]|(100)$/.test($scope.order.orderStatus);
                //$scope.showMod[2]=(/^[6789]|(100)$/.test($scope.order.orderStatus) && window.isEmpty($scope.order.isApplyReturn));
                if(/^[6789]$/.test($scope.order.orderStatus)){
                    $scope.showMod[2]=$scope.order.isApplyReturn;
                }else if($scope.order.orderStatus==100){
                    $scope.showMod[2]=$scope.order.isReturn
                }else {
                    $scope.showMod[2]=false;
                }
                //获取养老金金额,会员未收货时,不显示0元的养老金金额
                if($scope.order.orderStatus!=100){
                    $scope.order.pensionMoney='商品收货7天后,将赠送养老金';
                }else{
                    $scope.order.pensionMoney+="元";
                }
                $scope.getCoupon();
            });
        };

        $scope.setShowMod=function(index){
            $scope.showMod[index]=!$scope.showMod[index];
        };

        //提交退货的发货信息
        $scope.sendDrawbackOrder=function(){
            var url = window.basePath + "/order/OrderInfo/sendDrawbackOnlineOrder";
            $http.post(url,$scope.drawOrder).success(function(){
                malert("已提交");
                $rootScope.goBack();
            });
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '订单详情');
                $scope.showMod=[false,false,false,false];
                $scope.drawOrder={
                    orderId:$rootScope.pathParams.orderId,
                    returnExpress:'',
                    returnExpressNo:''
                };
                if(!window.isEmpty($rootScope.pathParams.showModIndex)){
                    $scope.setShowMod($rootScope.pathParams.showModIndex);
                }
                $scope.getOrderInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });

    //当图片加载失败时,显示的404图片
    window.app.register.directive('errSrc', function () {
        return {
            link: function (scope, element, attrs) {
                element.bind('error', function () {
                    if (attrs.src != attrs.errSrc) {
                        attrs.$set('src', attrs.errSrc);
                    }
                });
            }
        }

    });
})(angular);