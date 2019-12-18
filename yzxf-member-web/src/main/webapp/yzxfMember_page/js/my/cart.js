/**
 * Created by zq2014 on 17/7/6.
 */
(function (angular, undefined) {

    var model = 'my';
    var entity = 'cart';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

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
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                for(var j= 0,jlen=$scope.cartList[i].product.length;j<jlen;j++){
                    if($scope.cartList[i].product[j].isSelected){
                        $scope.totalPrice+=parseFloat($scope.cartList[i].product[j].price);
                        $rootScope.selectedCart.push($scope.cartList[i].product[j]._id)
                    }
                }
            }
            $scope.totalPrice=$rootScope.getMoney($scope.totalPrice);
        };
        //设置购买数量
        $scope.setPayNum = function (pIndex,index,num) {
            var payNum = $scope.cartList[pIndex].product[index].count;
            if ((num == -1 && payNum > 1) || (num == 1 && payNum < 99)) {
                $scope.cartList[pIndex].product[index].count += num;
            }
            if($scope.cartList[pIndex].product[index].count===payNum){
                return;
            }
            //保存购物车
            var url = window.basePath + "/order/Cart/saveMyCart";
            var data={
                count: $scope.cartList[pIndex].product[index].count,
                cartId:$scope.cartList[pIndex].product[index]._id
            };
            $http.post(url,data).success(function(){
                $scope.cartList[pIndex].product[index].price=$scope.cartList[pIndex].product[index].unitPrice*$scope.cartList[pIndex].product[index].count;
                $scope.countProduct();
            }).error(function(){
                $scope.cartList[pIndex].product[index].count-=num;
                $scope.countProduct();
            });
        };
        //获取购物车列表
        $scope.getMyCart = function () {
            //var url = window.basePath + '/order/Cart/getMyCart?pageSize=' + window.pageSize + '&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum;
            var url = window.basePath + '/order/Cart/getMyCart';
            $http.get(url).success(function (re) {
                $scope.cartList=re.content.items;
                $scope.countProduct();
            })
        };
        //初始化购物车为全部未选择
        $scope.initCart=function(){
            $scope.isEdit=!$scope.isEdit;
            //清空已选择的
            $scope.selectAll(false);
        };
        //判断是否显示删除购物车界面
        $scope.checkDelCart=function(){
            var count = 0;
            for(var i= 0,len=$scope.cartList.length;i<len;i++){
                for(var j= 0,jlen=$scope.cartList[i].product.length;j<jlen;j++){
                    if($scope.cartList[i].product[j].isSelected){
                        count++;
                    }
                }
            }
            if(count>0){
                $scope.isDel=true;
            }else{
                $scope.isDel=false;
                malert("请选择要删除的商品");
            }
        }
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
        $scope.delCart=function(){
            var delStr=$scope.spliceCartId();
            var url = window.basePath + '/order/Cart/deleteMyCart';
            $http.post(url,{cartId:delStr}).success(function(){
                malert("删除成功");
                $scope.isDel=false;
                $scope.getMyCart();
            });
        };
        //跳转确认支付页面
        $scope.payOrder=function(){
            $rootScope.selectedCart=$scope.spliceCartId();
            if(window.isEmpty($rootScope.selectedCart)){
                malert("未购买商品");
                return;
            }
            $rootScope.goPage("/order/orderConfirmation");
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的购物车');
                $rootScope.isIndex = true;

                $scope.getMyCart();
                $scope.totalPrice=0.0;
                $scope.couponTotalPrice=0.0;
                $scope.isSelectedAll=false;
                $scope.selectedCart=[];
                $scope.isEdit=false;
                $scope.isDel=false;
                $rootScope.selectedCart='';
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
