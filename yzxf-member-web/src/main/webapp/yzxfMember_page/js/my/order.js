(function (angular, undefined) {
    var model = "my";
    var entity = "order";
    window.app.register.controller('my_order_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {

        $scope.getOrderStatus = function (item,index) {
            if (item.orderStatus == 0) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].createTime;
                return "草稿";
            } else if (item.orderStatus == 1) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].createTime;
                return "未支付";
            } else if (item.orderStatus == 2) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].bookingTime;
                return "已支付,等待商家发货";
            } else if (item.orderStatus == 3) {
                return "商家打包制作中";
            } else if (item.orderStatus == 4) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].sendTime;
                return "商家已发货";
            } else if (item.orderStatus == 5) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].accountTime;
                return "买家已收货";
            } else if (item.orderStatus == 6) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnApplyTime;
                return "[退货]买家申请退货";
            } else if (item.orderStatus == 7) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnApplyTime;
                return "[退货]请填写发货信息";
            } else if (item.orderStatus == 8) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnApplyTime;
                return "[退货]已发货";
            } else if (item.orderStatus == 9) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnTime;
                return "[退货]已退款";
            } else if (item.orderStatus == 100) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].endTime;
                if(!window.isEmpty(item.isReturn) && !item.isReturn){
                    return "商家拒绝退款,已结算";
                }else{
                    return "已结算";
                }
            }
        }

        //切换页面
        $scope.setPageCheck=function(){
            if($scope.pageCheck=='drawback'){
                $scope.pageCheck=null;
            }else{
                $scope.pageCheck='drawback'
            }
            $scope.initHeader();
        };

        //初始化头部导航栏
        $scope.initHeader=function(){
            $scope.orderList=[];
            if($scope.pageCheck=='drawback'){
                $scope.headerList = [
                    {_id: 6, name: '申请退货', icon: 'icon-calendar'},
                    {_id: 7, name: '确认退货', icon: 'icon-daifahuo'},
                    {_id: 8, name: '已发货', icon: 'icon-daishouhuo'},
                    {_id: 9, name: '已退款', icon: 'icon-cshy-rmb2'},
                ];
            }else{
                $scope.headerList = [
                    {_id: -1, name: '全部订单', icon: 'icon-calendar'},
                    {_id: 1, name: '未付款', icon: 'icon-wallets'},
                    {_id: 2, name: '待发货', icon: 'icon-daifahuo'},
                    {_id: 4, name: '待收货', icon: 'icon-daishouhuo'},
                    {_id: 5, name: '已收货', icon: 'icon-jiaoyi'},
                    {_id: 100, name: '已结算', icon: 'icon-cshy-rmb2'},
                ];
            };
            if(!window.isEmpty($rootScope.pathParams.headerIndex)){
                $scope.selectedHead=$scope.headerList[parseInt($rootScope.pathParams.headerIndex)+1];
                window.setWindowTitle($rootScope, $scope.selectedHead.name);
            }else{
                $scope.selectedHead=JSON.parse(JSON.stringify(($scope.headerList[0])));
            }
            $scope.getOrderStatusList($scope.selectedHead._id);
        };

        //设置头部导航栏状态数量
        $scope.setHeaderCount=function(count){
            if(count==null || count.length==0){
                return;
            }
            for(var i= 0,len=$scope.headerList.length;i<len;i++){
                for(var j= 0,jlen=count.length;j<jlen;j++){
                    if($scope.headerList[i]._id==count[j].orderStatus){
                        if(count[j].count>99){
                            count[j].count=99;
                        }
                        $scope.headerList[i]["count"]=count[j].count;
                        break;
                    }
                }
            }
        };

        //是否跳转评论页面
        $scope.isGoPage = function (isComment, orderNo, id, sellerId) {
            if (!isComment) {
                $rootScope.goPage("/order/orderComment/orderNo/" + orderNo + "/id/" + id + "/sellerId/" + sellerId);
            }
        }
        //数字是否为空
        $scope.isNullZero = function (number) {
            if (number == null) {
                return 0;
            } else {
                return number;
            }
        }
        //获取订单列表
        $scope.getOrderList = function () {
            //未知的错误,其他页面可能会调用到order的方法
            //if(!/^\/my\/order$/.test($location.path())){
            //    return;
            //}
            var url = window.basePath + '/order/OrderInfo/queryMyOrder?' +
                'pageSize=' + window.pageSize +
                '&pageNo=' + window.pageNo +
                "&indexNum=" + window.indexNum +
                "&orderStatus=" + $scope.selectedHead._id+
                "&isCount=true";
            $http.get(url).success(function (re) {
                $.each(re.content.orderList, function (k, v) {
                    $scope.orderList.push(v);
                });
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
                $scope.setHeaderCount(re.content.countList);
            })
        }
        $scope.scrollEvent = function () {
            if ($('#moreButton').size() == 0) {
                return;
            }
            var loadNextPage = function () {
                window.stopScroll--;
                if (window.stopScroll > 0) {
                    return;
                }
                if ($scope.totalPage <= window.pageNo) {
                    return;
                }
                $scope.more();
            }
            if ($('#moreButton').offset().top < ($(window).scrollTop() + $(window).height())) {
                window.stopScroll || (window.stopScroll = 0);
                window.stopScroll++;
                loadNextPage.delay(0.5);
            }
        }

        $scope.addScrollEvent = function () {
            $('.overflowPC').unbind('scroll');
            $(window).unbind('scroll');
            $('.overflowPC').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.more = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.getOrderList();
        };
        //根据状态获取订单列表
        $scope.getOrderStatusList = function (index) {
            $scope.selectedHead._id = index;
            window.pageSize = 8;
            window.indexNum = 0;
            window.pageNo = 1;
            $scope.orderList = [];
            $scope.isLoadMore = true;
            $scope.getOrderList();
        }

        //删除订单
        //$scope.delOrder = function (id) {
        //    var url = window.basePath + '/order/OrderInfo/del';
        //    var data = {'_id': id};
        //    $http.post(url, data).success(function () {
        //        $scope.getOrderList();
        //    });
        //}

        //加入购物车
        $scope.saveCart=function(product){
            var url = window.basePath + "/order/Cart/saveMyCart";
            var data={
                spec: product.selectSpec,
                count: product.count,
                productId:product.productId,
                isReset:true
            };
            $http.post(url,data).success(function(re){
                $rootScope.selectedCart+=re.content._id+",";
                $scope.countCart++;
            });
        };

        $scope.queryInfo=function(item){
            if(item.orderType!=11){
                return;
            }
            if(item.orderStatus==1){
                $rootScope.selectedCart="";
                var len=item.productItems.length;
                for(var i= 0;i<len;i++){
                    $scope.saveCart(item.productItems[i]);
                }
                //等待加入购物车
                var goPageTime = $interval(function(){
                    if($scope.countCart==len){
                        $interval.cancel(goPageTime);
                        $rootScope.selectedCart=$rootScope.selectedCart.substring(0,$rootScope.selectedCart.length-1);
                        $rootScope.goPage('/order/orderConfirmation/pid/'+item.pid+'/childOrderId/'+item._id);
                    }
                },200);
            }else{
                $rootScope.goPage('/order/orderInfo/orderId/'+item._id);
            }
        };

        $scope.setPassId=function(id){
            $scope.isPassId=id;
            if($scope.isPassId!=null && $scope.isPassId!=''){
                $scope.isShowPass = true;
            }
        }

        $scope.submitPass=function(){
            if(window.isEmpty($scope.isPassId)){
                malert("获取订单数据失败");
                return;
            }
            var url = window.basePath+"/order/OrderInfo/updateOnlineOrderByMember?orderId="+$scope.isPassId;
            $http.get(url).success(function(){
                malert("已确认收货");
                $scope.isPassId='';
                $scope.isShowPass=false;

                $scope.getOrderStatusList($scope.selectedHead._id);
            });
        }

        $scope.delOrder=function(item) {
            var url = window.basePath + '/order/OrderInfo/delOrderById?pid='+item.pid+'&childOrderId='+item._id;
            $http.get(url).success(function(){
                malert("删除成功");
                $scope.getOrderStatusList($scope.selectedHead._id);
            });
        };

        $scope.delOrderByOffline = function(item){
            var url = window.basePath+"/order/OrderInfo/del";
            $http.post(url,{_id:item._id}).success(function(){
                malert("删除成功");
                $scope.getOrderStatusList($scope.selectedHead._id);
            });
        };

        //申请退货
        $scope.applyDrawback=function(item){
            if(item.accountTime>parseInt(new Date().getTime())-(1000)){
                malert("只有7天之内收货的商品才可申请退款");
                return;
            }
            $rootScope.goPage("/order/drawbackApply/orderId/"+item._id);
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.pageSize = 8;
                window.indexNum = 0;
                window.pageNo = 1;
                $scope.countCart=0;
                $scope.orderList = [];
                $scope.isLoadMore = true;
                $scope.pageCheck = $rootScope.pathParams.pageCheck;
                $scope.initHeader();
                $scope.addScrollEvent();
                $scope.isPassId='';
                $scope.isShowPass = false;
            }
        }
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
