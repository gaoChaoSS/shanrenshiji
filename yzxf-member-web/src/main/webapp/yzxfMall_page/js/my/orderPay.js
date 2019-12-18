/**
 * Created by zq2014 on 17/7/18.
 */
(function (angular, undefined) {

    var model = 'my';
    var entity = 'my';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        //获取所有收货地址
        $scope.getAddressList=function(){
            $scope.defaultAddress={area:''};
            var url = window.basePath + '/crm/MemberAddress/getMemberAddress';
            $http.get(url).success(function (re) {
                $scope.addressList = re.content.items;
                if(window.isEmpty($scope.addressList)){
                    $scope.defaultAddress.area="请设置收货地址";
                }else{
                    //优先选择默认地址
                    for(var i= 0,len=$scope.addressList.length;i<len;i++){
                        if($scope.addressList[i].defaultAddress){
                            $scope.defaultAddress=$scope.addressList[i];
                        }
                    }
                    //若没有默认地址,则选择第一个地址.
                    if(window.isEmpty($scope.defaultAddress.areaValue)){
                        $scope.defaultAddress=$scope.addressList[0];
                    }
                }
            });
        };
        //设置收货地址
        $scope.selectedAddress=function(item){
            $scope.defaultAddress=item;
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
            $scope.totalPrice=0.0;
            $scope.totalPension=0.0;
            $scope.totalCouponPrice=0.0;//总折扣
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                $scope.cartList[i].sellerPrice=$rootScope.getMoney($scope.cartList[i].sellerPrice-$scope.selectedCoupon[i].value);
                $scope.totalCouponPrice=parseFloat($scope.totalCouponPrice)+parseFloat($scope.selectedCoupon[i].value);
                $scope.totalPrice=parseFloat($scope.totalPrice)+parseFloat($scope.cartList[i].sellerPrice);
                var sellerPension = $rootScope.getMoney($scope.cartList[i].sellerPrice*($scope.cartList[i].integralRate/100.0));
                $scope.totalPension=parseFloat(sellerPension)+parseFloat($scope.totalPension);
            }
            $scope.totalPension/=2.0;

            $scope.totalPrice=$rootScope.getMoney($scope.totalPrice);
            $scope.totalCouponPrice=$rootScope.getMoney($scope.totalCouponPrice);
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
                $scope.cartList[i].couponList.unshift({name:'未选择优惠券',value:0,linkId:''});
                //默认设置每一个卡券列表的第一个卡券为选中
                $scope.setCoupon(i,$scope.cartList[i].couponList[$scope.cartList[i].couponList.length>1?1:0]);
            }
            $scope.countFinalPrice();
        };
        //初始化已选择卡券数组
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
            if(window.isEmpty($scope.defaultAddress.area)){
                malert("请设置收货地址");
                return;
            }
            if(window.isEmpty($scope.totalPrice) || $scope.totalPrice < 0 || window.isEmpty($scope.selectPay)){
                malert('订单数据错误');
            }else if($scope.selectPay==3){
                $("#blur").css("filter","blur(3px)");
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
        };
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

        $scope.genQrCode = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = $scope.selectPay;
            if (window.isMobile() || window.isWechat()) {
                postData["channelType"] = "08";//WAP
            } else {
                postData["channelType"] = "07";//PC
            }
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            postData["returnUrl"] = $location.absUrl().split("?")[0].split("my")[0]+'my/depositSuccess/orderNo/'+$scope.order.orderNo;
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


        //检查订单
        $scope.checkOrder = function () {
            if (!window.isEmpty($scope.order.orderNo)) {
                $scope.isSuccess=0;
                var url = window.basePath + '/order/OrderInfo/checkOrderStatus';
                var data = {
                    orderNo: $scope.order.orderNo
                }
                $http({
                    method: 'post',
                    hideLoading: true,
                    url: url,
                    data:data
                }).success(function (reData) {
                    if (reData.content.payStatus == 'SUCCESS') {
                        clearInterval($scope.queryTask);
                        $scope.isQrcode=false;
                        $scope.isSuccess=1;
                        $scope.getIcon();
                        $scope.isSuccessWin=true;
                    }
                });
            }
        }

        //生成订单
        $scope.createOrder=function(){
            $scope.defaultBlur();
            $scope.menuCheck=false;
            var orderInfo={
                cartId:$rootScope.selectedCart,
                memberRemark:$scope.memberRemark,
                payPwd:$scope.pwd1+$scope.pwd2+$scope.pwd3+$scope.pwd4+$scope.pwd5+$scope.pwd6,
                payType:$scope.selectPay,
                linkIdArr:$scope.getLinkIdArr(),
                addressId:$scope.defaultAddress._id,
                pid:$scope.pathParams.pid,
                childOrderId:$scope.pathParams.childOrderId
            };
            var url = window.basePath + '/order/OrderInfo/createOnlineOrder';
            $http.post(url, orderInfo).success(function (re) {
                $scope.order=re.content;
                $scope.submitCheck='disabled';
                if ($scope.selectPay == 3){
                    $("#blur").css("filter","blur(3px)");
                    $scope.isSuccessWin=true;
                    if(re.content.orderStatus==2){
                        $scope.isSuccess=1;
                    }else{
                        $scope.isSuccess=0;
                    }
                    $scope.getIcon();
                    $scope.submitCheck=false;
                }else{
                    //$scope.isQrcode=true;
                    //$scope.sureOrder=false;
                    $scope.genQrCode();
                }
            }).error(function(){
                $scope.submitCheck=false;
            })
        };

        //支付结果图标/文字
        $scope.getIcon=function(){
            if($scope.isSuccess==1){
                $scope.statusIcon= 'bkColorGreen1 icon-gou';
                $scope.statusText='支付成功';
            }else if($scope.isSuccess==2){
                $scope.statusIcon= 'bgNone font100px colorRed1 icon-cuowu';
                $scope.statusText='支付失败';
            }else if($scope.isSuccess==3){
                $scope.statusIcon= 'bgNone font100px colorRed1 icon-cuowu';
                $scope.statusText='获取订单失败';
            }else if($scope.isSuccess==0){
                $scope.statusIcon= 'bkColorBlue1 font70px icon-gengduo';
                $scope.statusText='222支付处理中';
            }
        };

        //支付成功后清空卡券
        $scope.clearCoupon=function(){
            $scope.coupon=[];
        };

        $scope.defaultBlur=function(){
            $("#blur").css("filter","none");
        };

        $scope.closeSuccessWin=function(){
            $scope.isSuccessWin=false;
            $scope.sureOrder=false;
            $scope.defaultBlur();
            $rootScope.goPage("/my/order");
        };

        $scope.closeQrcode=function(){
            $scope.isQrcode=false;
            $scope.submitCheck=false;
            clearInterval($scope.queryTask);
            $scope.defaultBlur();
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '确认订单');
                if(window.isEmpty($rootScope.selectedCart)){
                    $rootScope.goBack();
                }
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
                //$(".mod-transparent").css("display","none");
                $scope.isSuccessWin=false;//是否显示余额是否支付成功
                $scope.menuCheck=false;//是否显示余额支付密码框
                $scope.isShowCoupon=false;//是否显示卡券
                $scope.isShowAddress=false;//是否显示地址
                $scope.isShowRemark=false;//是否显示留言
                $scope.memberRemark='';//留言
                $scope.selectPay=3;//默认选择余额

                $scope.getAddressList();
                $scope.getCart();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);