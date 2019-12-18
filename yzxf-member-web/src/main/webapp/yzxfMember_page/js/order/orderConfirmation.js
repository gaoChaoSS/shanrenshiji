(function (angular, undefined) {

    var model = 'order';
    var entity = 'orderConfirmation';
    var entityUrl = '/' + model + '/' + entity;
    window.wechatAppId = 'wx9b369a21e6ec245d';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {
        //获取默认收货地址
        $scope.getAddress=function(){
            var url = window.basePath + "/crm/MemberAddress/getDefaultAddress";
            $http.get(url).success(function(re){
                $scope.memberAddress=re.content;
                if(window.isEmpty($scope.memberAddress.area)){
                    $scope.memberAddress.area="请设置收货地址";
                }
            });
        };
        //获取所有收货地址
        $scope.getAddressList=function(){
            var url = window.basePath + '/crm/MemberAddress/getMemberAddress';
            $http.get(url).success(function (re) {
                $scope.addressList = re.content.items;
            });
            $scope.isShowAddress=true;
        };
        //设置收货地址
        $scope.selectedAddress=function(item){
            $scope.memberAddress=item;
            $scope.isShowAddress=false;
        };
        //获取已选择的购物车信息
        $scope.getCart=function(){
            var url = window.basePath + "/order/Cart/getMyCart?cartId="+$rootScope.selectedCart;
            $http.get(url).success(function(re){
                $scope.cartList=re.content.items;
                //当只在一家商家购买产品时,才显示留言框
                if($scope.cartList.length==1){
                    $scope.isShowRemark=true;
                }
                $scope.getSellerCoupon();
            });
        };
        //获取会员在每个商户下领取的卡券
        $scope.getSellerCoupon=function(sellerId){
            var sellerStr = "";
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                sellerStr+=$scope.cartList[i].sellerId+",";
            }
            sellerStr = sellerStr.substring(0,sellerStr.length-1);
            var url = window.basePath + "/crm/Coupon/queryStoreCouponReceive?isFormat=true&isCanUse=true&sellerId="+sellerStr;
            $http.get(url).success(function(re){
                $scope.sellerCoupon=re.content.items;
                //将结果添加进购物车中,方面页面遍历
                for(var i= 0,len=$scope.sellerCoupon.length;i<len;i++){
                    for(var j= 0,jlen=$scope.cartList.length;j<jlen;j++){
                        if($scope.cartList[j].sellerId==$scope.sellerCoupon[i].sellerId){
                            $scope.cartList[j].couponList = $scope.sellerCoupon[i].couponList;
                        }
                    }
                }
                $scope.countSellerProduct();
                $scope.initSelectedCoupon();
                $scope.checkCoupon();
            });
        };
        //计算每个商家下所有商品的总价(不含卡券)
        $scope.countSellerProduct=function(){
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                var sellerPrice=0.0;
                for(var j= 0,jlen=$scope.cartList[i].product.length;j<jlen;j++){
                    sellerPrice+=$scope.cartList[i].product[j].price;
                }
                $scope.cartList[i].sellerPrice=sellerPrice;
            }
        };
        //计算每个商家的最终价格,最终总价格,养老金
        $scope.countFinalPrice=function(){
            $scope.totalPrice=0.0;//总价格
            $scope.totalPension=0.0;//总养老金
            $scope.totalCouponPrice=0.0;//总折扣
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                $scope.cartList[i].sellerPrice-=$scope.selectedCoupon[i].value;
                $scope.totalCouponPrice+=$scope.selectedCoupon[i].value;
                $scope.totalPrice+=$scope.cartList[i].sellerPrice;
                var sellerPension = $rootScope.getMoney($scope.cartList[i].sellerPrice*($scope.cartList[i].integralRate/100.0));
                $scope.totalPension=parseFloat(sellerPension)+parseFloat($scope.totalPension);
            }
            $scope.totalPension/=2.0;

            $scope.totalCouponPrice=$rootScope.getMoney($scope.totalCouponPrice);
            $scope.totalPrice=$rootScope.getMoney($scope.totalPrice);
            $scope.totalPension=$rootScope.getMoney($scope.totalPension);
        };
        //循环检查每个商店下满足条件的卡券,删除不满足条件的卡券
        $scope.checkCoupon=function(){
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                var couponListTemp=[];
                if(!window.isEmpty($scope.cartList[i].couponList) && $scope.cartList[i].couponList.length>0){
                    for(var j= 0,jlen=$scope.cartList[i].couponList.length;j<jlen;j++){
                        if($scope.cartList[i].couponList[j].condition<=$scope.cartList[i].sellerPrice){
                            couponListTemp.push($scope.cartList[i].couponList[j]);
                        }
                    }
                }
                $scope.cartList[i].couponList=couponListTemp;
                //默认设置每一个卡券列表的第一个卡券为选中
                $scope.setCoupon(i,$scope.cartList[i].couponList[0]);
            }
            $scope.countFinalPrice();
        };
        //初始化已卡券选择数组
        $scope.initSelectedCoupon=function(){
            var sellerCount=$scope.cartList.length;
            $scope.selectedCoupon=new Array(sellerCount);
            for(var i= 0;i<sellerCount;i++){
                $scope.selectedCoupon[i]={
                    name:'未选择卡券',
                    value:0,
                    linkId:''
                };
            }
        };
        //设置优惠券
        $scope.setCoupon=function(index,coupon,isCount){
            //若不选择卡券,为每一个商家选择的卡券设置一个空
            if(window.isEmpty(coupon)){
                $scope.selectedCoupon[index]={
                    name:'未选择卡券',
                    value:0,
                    linkId:''
                };
            }else{
                $scope.selectedCoupon[index]=coupon;
            }
            if(window.isEmpty($scope.cartList[index].couponList) || $scope.cartList[index].couponList.length==0){
                $scope.selectedCoupon[index]={
                    name:'无可用卡券',
                    value:0,
                    linkId:''
                };
            }
            //是否需要重新计算最终金额
            if(isCount){
                $scope.countSellerProduct();
                $scope.countFinalPrice();
            }
            $scope.isShowCoupon=false;
        };
        //显示卡券选择页面
        $scope.showCouponPage=function(index){
            if(window.isEmpty($scope.cartList[index].couponList) || $scope.cartList[index].couponList.length==0){
                malert("'"+$scope.cartList[index].sellerName+"'商家下无可用卡券");
                return;
            }
            $scope.isShowCoupon=true;
            $scope.showCoupon=$scope.cartList[index].couponList;
            $scope.selectedSeller={
                name:$scope.cartList[index].sellerName,
                index:index
            };
        };
        //将数组里的LinkId组成一个新的数组
        $scope.getLinkIdArr=function(){
            var arr=[];
            for(var i= 0,len=$scope.selectedCoupon.length;i<len;i++){
                arr.push({
                    sellerId:$scope.cartList[i].sellerId,
                    linkId:$scope.selectedCoupon[i].linkId
                });
            }
            return arr;
        };
        //支付密码输入框
        $scope.getPwdWin = function () {
            if(window.isEmpty($scope.memberAddress.area)){
                malert("请设置收货地址");
                return;
            }
            if(window.isEmpty($scope.totalPrice) || $scope.totalPrice < 0 || window.isEmpty($scope.selectPay)){
                malert('获取订单失败');
            }else if($scope.selectPay==3){
                $scope.menuCheck = true;
                $scope.pwd1 = null;
                $scope.pwd2 = null;
                $scope.pwd3 = null;
                $scope.pwd4 = null;
                $scope.pwd5 = null;
                $scope.pwd6 = null;
                setTimeout(function(){
                    $(".enterInput input").eq(0).focus();
                },200)
            }else{
                $scope.createOrder();
            }
            return false;
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
            postData["type"] = "orderPay";
            postData["payType"] = 4;
            if (isMobile()) {
                postData["clientType"] = "MobileWeb";
            } else {
                postData["clientType"] = "PcWeb";
            }
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("/order/")[0]+'/my/depositSuccess/orderNo/'+$scope.order.orderNo;
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

        //贵商银行支付调用
        $scope.doGpay = function () {
            if (window.isMobile() || window.isWechat()) {
                var state = "wechatMobile_member_gpay_"+$scope.order._id +"_08";
                var uri = encodeURIComponent("http://s.yzxf8.cn/oauth_page/callback.jsp");
                window.location =
                    'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + wechatAppId + '&redirect_uri=' + uri +
                    '&response_type=code&scope=snsapi_base&state=' + state + '#wechat_redirect';
            } else {
                var postData = {};
                postData["type"] = "orderPay";
                postData["payType"] = 18;
                postData["channelType"] = "07";
                postData["sellerId"] = $scope.order.sellerId;
                postData["totalFee"] = $scope.order.totalPrice;
                postData["storeId"] = "";
                postData["orderId"] = $scope.order._id;
                postData["memberId"] = $scope.order.memberId;
                postData["returnUrl"] = $location.absUrl().split("?")[0].split("/order/")[0]+'/my/depositSuccess/orderNo/'+$scope.order.orderNo;
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
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("/order/")[0]+'/my/depositSuccess/orderNo/'+$scope.order.orderNo;
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
            }).error(function (ex) {
                console.log(ex)
            });
        };
        //生成订单
        $scope.createOrder=function(){
            $scope.menuCheck=false;
            $scope.submitCheck='disabled';
            var orderInfo={
                cartId:$rootScope.selectedCart,
                memberRemark:$scope.memberRemark,
                payPwd:$scope.pwd1+$scope.pwd2+$scope.pwd3+$scope.pwd4+$scope.pwd5+$scope.pwd6,
                payType:$scope.selectPay,
                linkIdArr:$scope.getLinkIdArr(),
                addressId:$scope.memberAddress._id,
                pid:$scope.pathParams.pid,
                childOrderId:$scope.pathParams.childOrderId
            };
            //若果是修改未支付的订单,则需要pid
            if(!window.isEmpty($rootScope.pathParams.pid)){
                orderInfo.pid=$rootScope.pathParams.pid;
            }
            var url = window.basePath + '/order/OrderInfo/createOnlineOrder';
            $http.post(url, orderInfo).success(function (re) {
                $scope.order=re.content;
                $scope.submitCheck='disabled';
                //if ($scope.selectPay == 4) {
                //    $scope.doAlipay();
                //} else if ($scope.selectPay == 10) {
                //    $scope.doWechat();
                //} else
                if($scope.selectPay ==18){
                    $scope.doGpay();
                }else if ($scope.selectPay == 3){
                    if(re.content.orderStatus==2){
                        $scope.countTimeNum=3;
                        $scope.isSuccess=true;
                        $scope.goPageTime=$interval(function () {
                            $scope.countTimeNum--;
                            if($scope.countTimeNum==0){
                                $interval.cancel($scope.goPageTime);
                                $rootScope.goPage("/my/my");
                            }
                            if ($location.path().indexOf("orderConfirmation") == -1) {
                                $interval.cancel($scope.goPageTime);
                            }
                        }, 1000);
                    }
                }
            }).error(function(){
                $scope.submitCheck=false;
            })
        };

        //支付成功后清空卡券
        $scope.clearCoupon=function(){
            $scope.coupon=[];

        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '确认订单');
                //如果没有选择的购物车,则返回上一个页面
                if(window.isEmpty($rootScope.selectedCart)){
                    $rootScope.goBack();
                }
                $rootScope.isIndex = false;
                $scope.isSuccess=false;//是否显示余额是否支付成功
                $scope.menuCheck=false;//是否显示余额支付密码框
                $scope.isShowCoupon=false;//是否显示卡券
                $scope.isShowAddress=false;//是否显示地址
                $scope.isShowRemark=false;//是否显示留言
                $scope.memberRemark='';//留言
                $scope.selectPay=$rootScope.getDefaultPayType();

                $scope.getAddress();
                $scope.getCart();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
    //当图片加载失败时,显示的404图片
    window.app.register.directive('errSrc', function() {
        return {
            link: function(scope, element, attrs) {
                element.bind('error', function() {
                    if (attrs.src != attrs.errSrc) {
                        attrs.$set('src', attrs.errSrc);
                    }
                });
            }
        }
    });
})(angular);


