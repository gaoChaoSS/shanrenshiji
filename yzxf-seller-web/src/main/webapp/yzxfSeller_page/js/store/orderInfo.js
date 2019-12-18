(function (angular, undefined) {
    var model = "store";
    var entity = "orderInfo";
    window.app.register.controller('store_orderInfo_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

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
        };

        $scope.getOrderInfo=function(){
            if(window.isEmpty($rootScope.pathParams.orderId)){
                malert("获取订单失败!");
                $rootScope.goBack();
                return;
            }
            var url = window.basePath + "/order/OrderInfo/queryMyOrder?orderId="+$rootScope.pathParams.orderId+"&userType=seller";
            $http.get(url).success(function(re){
                $scope.order = re.content.orderList[0];
                $scope.tempData={
                    orderId:$scope.order._id,
                    express:'',
                    expressNo:'',
                    freight:''
                }
                $scope.showMod[1]=/^[456789]|(100)$/.test($scope.order.orderStatus);
                //$scope.showMod[2]=(/^[6789]|(100)$/.test($scope.order.orderStatus) && window.isEmpty(String($scope.order.isApplyReturn)));
                if(/^[6789]$/.test($scope.order.orderStatus)){
                    $scope.showMod[2]=$scope.order.isApplyReturn;
                }else if($scope.order.orderStatus==100){
                    $scope.showMod[2]=$scope.order.isReturn
                }else {
                    $scope.showMod[2]=false;
                }
                $scope.getCoupon();
            });
        };

        //发货
        $scope.getSubmit=function(){
            //window.location.href='http://baidu.kuaidi100.com/';
            if(window.isEmpty($scope.tempData.freight)){
                $scope.tempData.freight=0;
            }
            var url = window.basePath + "/order/OrderInfo/updateOnlineOrderBySeller";
            $http.post(url,$scope.tempData).success(function(){
                malert("已发货");
                $rootScope.goBack();
            });
        };

        //初始化退货订单信息
        $scope.initDrawOrder=function(){
            $scope.drawOrder={
                orderId:$rootScope.pathParams.orderId,
                isApplyReturn:'',
                returnRefuse:$scope.returnRefuse,
                returnContact:'',
                returnAddress:'',
                returnPhone:''
            }
        };

        $scope.getSellerInfo=function(){
            var url = window.basePath + "/account/Seller/querySeller";
            $http.get(url).success(function(re){
                $scope.drawOrder.returnContact=re.content.contactPerson;
                $scope.drawOrder.returnAddress=re.content.area+re.content.address;
                $scope.drawOrder.returnPhone=re.content.phone;
            });
        };

        $scope.setShowMod=function(index){
            $scope.showMod[index]=!$scope.showMod[index];
            $scope.returnRefuse='';
            $scope.initDrawOrder();
            if(index==4 && $scope.showMod[index]){
                $scope.getSellerInfo();
            }
        };

        //是否同意退货
        $scope.drawback=function(check){
            $scope.isDrawbackBtn='disabled';
            var url = window.basePath + "/order/OrderInfo/isReturnDrawbackOnlineOrder";
            $scope.drawOrder.isApplyReturn=check;
            $http.post(url,$scope.drawOrder).success(function(){
                $scope.isDrawbackBtn=false;
                malert("已"+(check?"同意":"拒绝")+"退货");
                $rootScope.goPage("/store/order");
            }).error(function(){
                $scope.isDrawbackBtn=false;
            });
        };

        //确认退款收货
        $scope.submitDrawback=function(){
            var url = window.basePath + '/order/OrderInfo/endDrawbackOnlineOrder';
            $http.post(url, {orderId:$rootScope.pathParams.orderId}).success(function () {
                malert("已确认收货,退款成功");
                $scope.getOrderInfo();
            });
        };

        // 无货退款
        $scope.notStockReturn=function(){
            mconfirm("是否直接退款给会员？","请提前和会员沟通协商后再做操作",function(){
                var url = window.basePath + '/order/OrderInfo/endDrawbackOnlineOrder';
                $http.post(url, {orderId:$rootScope.pathParams.orderId,notStock:true}).success(function () {
                    malert("退款成功");
                    $scope.getOrderInfo();
                });
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的订单');
                $scope.showMod=[false,false,false,false,false];
                $scope.initDrawOrder();
                $scope.getOrderInfo();
                if(!window.isEmpty($rootScope.pathParams.showModIndex)){
                    $scope.setShowMod($rootScope.pathParams.showModIndex);
                }
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