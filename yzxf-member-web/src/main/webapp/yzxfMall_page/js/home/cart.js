/**
 * Created by zq2014 on 17/7/14.
 */
(function () {
    window.initCart = function ($rootScope, $scope, $http, $interval, $location) {
        //显示我的购物车
        $scope.showCart=function(){
            if($(".mod-transparent").css("right").split("px")[0]==0){
                $(".mod-transparent").animate({right:"-270px"});
                $("#myCart").removeClass("bkColorBlue1");
            }else{
                $(".mod-transparent").animate({right:0});
                $("#myCart").addClass("bkColorBlue1");
            }
        };

        //选择/取消购物车的某一项
        $scope.selectCart=function(pIndex,index,flag){
            if(flag==true || flag==false){//固定值
                $scope.cartList[pIndex].product[index].isSelected=flag;
            }else{
                $scope.cartList[pIndex].product[index].isSelected=!$scope.cartList[pIndex].product[index].isSelected;
            }
            $scope.isSelectedAll=$scope.checkSelectedAll();
            $scope.cartList[pIndex].isSellerSelected=$scope.checkSelectedSeller(pIndex);
            $scope.countProduct();
        };
        //选择/取消某个商家的所有商品
        $scope.selectSeller=function(index,flag){
            for(var i= 0,len=$scope.cartList[index].product.length;i<len;i++){
                $scope.selectCart(index,i,flag);
            }
        };
        //选择/取消全部
        $scope.selectAll=function(flag){
            var isAll=false;
            if(flag==true || flag==false) {//固定值
                $scope.isSelectedAll=flag;
            }else{//取相反
                $scope.isSelectedAll=!$scope.checkSelectedAll();
            }
            isAll=$scope.isSelectedAll;
            //循环调用商家全选选择/取消方法
            for(var i=0;i<$scope.cartList.length;i++){
                $scope.selectSeller(i,isAll);
            }
        };
        //检查该商家下是否全部选择,或全部未选择
        $scope.checkSelectedSeller=function(index){
            var flag=true;
            var items=$scope.cartList[index].product;
            var count=0;
            for(var i = 0,len=items.length;i<len;i++){
                if(items[i].isSelected){
                    count++;
                }
            }
            flag=(count==items.length);
            return flag;
        };
        //检查是否全部选择,或全部未选择
        $scope.checkSelectedAll=function(){
            var count=0;
            var len = $scope.cartList.length;
            for(var i= 0;i<len;i++){
                if($scope.checkSelectedSeller(i)){
                    count++;
                }
            }
            return count==len;
        };
        //累计已选择的商品
        $scope.countProduct=function(){
            $rootScope.selectedCart=[];//选择的购物车ID
            $scope.totalPrice=0.0;
            $scope.totalCount=0;
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                for(var j= 0,jlen=$scope.cartList[i].product.length;j<jlen;j++){
                    if($scope.cartList[i].product[j].isSelected){
                        $scope.totalPrice+=parseFloat($scope.cartList[i].product[j].price);
                        $rootScope.selectedCart.push($scope.cartList[i].product[j]._id);
                        $scope.totalCount++;
                    }
                }
            }
            $scope.totalPrice=$rootScope.getMoney($scope.totalPrice);
        };
        //设置购买数量
        $scope.setPayNum = function (pIndex,index,num) {
            var payNum = $scope.cartList[pIndex].product[index].count;
            if (num == -1 && payNum > 1) {
                $scope.cartList[pIndex].product[index].count += num;
            } else if (num == 1 && payNum < 99) {
                $scope.cartList[pIndex].product[index].count += num;
            }
            //保存购物车
            var url = window.basePath + "/order/Cart/saveMyCart";
            var data={
                count: $scope.cartList[pIndex].product[index].count,
                cartId:$scope.cartList[pIndex].product[index]._id
            };
            $http.post(url,data).success(function(){
                $scope.countProduct();
                $scope.cartList[pIndex].product[index].price=$scope.cartList[pIndex].product[index].unitPrice*$scope.cartList[pIndex].product[index].count;
            }).error(function(){
                $scope.cartList[pIndex].product[index].count-=num;
            });
        };
        //获取购物车列表
        $scope.getMyCart = function () {
            var url = window.basePath + '/order/Cart/getMyCart';
            $http.get(url).success(function (re) {
                $scope.cartList=re.content.items;
                $scope.countProduct();
                //$('.cart-list').hover(function(){
                //    var top=$('body').scrollTop();
                //    $(window).scroll(function(){
                //        alert("x");
                //        //滚动时，固定页面滚动条到页顶的高度，使其不滚动
                //        $('body').scrollTop(top);
                //    });
                //},function(){//鼠标移开后，解除固定
                //    $(window).off('scroll');
                //});
            })
        };
        //初始化购物车为全部未选择
        $scope.initCart=function(){
            //清空已选择的
            $scope.selectAll(false);
        };
        //处理选择的购物车,拼接ID
        $scope.spliceCartId=function(){
            var str="";
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                for(var j= 0,jlen=$scope.cartList[i].product.length;j<jlen;j++){
                    if($scope.cartList[i].product[j].isSelected){
                        str+=$scope.cartList[i].product[j]._id+",";
                    }
                }
            }
            return str.substring(0,str.length-1);
        };
        //删除购物车
        $scope.delCart=function(cartId){
            var url = window.basePath + '/order/Cart/deleteMyCart';
            $http.post(url,{cartId:cartId}).success(function(){
                malert("删除成功");
                $scope.getMyCart();
            });
        };
        //跳转确认支付页面
        $scope.payOrder=function(){
            $rootScope.selectedCart=$scope.spliceCartId();
            if(window.isEmpty($rootScope.selectedCart)){
                malert("请选择商品");
                return;
            }
            $rootScope.goPage("/my/orderPay");
        };

        //订单支付/登录/注册页面不显示
        //if($location.path().indexOf("/my/orderPay")!=-1){
        if(/^(.*((orderPay)|(reg)|(login)).*)$/.test($location.path()) || window.isEmpty(getCookie('_member_id'))){
            $(".mod-transparent").css("display","none");
            return;
        }else{
            $(".mod-transparent").css("display","block");
            $(".mod-transparent").css("right","-270px");
            $("#myCart").removeClass("bkColorBlue1");
        }

        $scope.totalPrice=0.0;
        $scope.totalCount=0;
        $scope.couponTotalPrice=0.0;
        $scope.isSelectedAll=false;
        $rootScope.selectedCart=[];
        $scope.getMyCart();
    };

})();