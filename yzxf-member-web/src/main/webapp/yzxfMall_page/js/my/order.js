(function (angular, undefined) {

    var model = 'my';
    var entity = 'order';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval,$timeout, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';
        $scope.selectedHead = -1;
        $scope.starList = [
            {id:1},
            {id:2},
            {id:3},
            {id:4},
            {id:5}
        ];

        $scope.startCheck=function(id){
            $scope.starGrade=id;
        };

        $scope.getOrderStatus = function (item,index) {
            if (item.orderStatus == 0) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].createTime;
                return "草稿";
            } else if (item.orderStatus == 1) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].createTime;
                return "未支付";
            } else if (item.orderStatus == 2) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].bookingTime;
                return "已支付,等待商家发货";
            } else if (item.orderStatus == 3) {
                return "商家打包制作中";
            } else if (item.orderStatus == 4) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].sendTime;
                return "商家已发货";
            } else if (item.orderStatus == 5) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].accountTime;
                return "买家已收货";
            } else if (item.orderStatus == 6) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].returnApplyTime;
                return "[退货]买家申请退货";
            } else if (item.orderStatus == 7) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].returnApplyTime;
                return "[退货]请填写发货信息";
            } else if (item.orderStatus == 8) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].returnApplyTime;
                return "[退货]已发货";
            } else if (item.orderStatus == 9) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].returnTime;
                return "[退货]已退款";
            } else if (item.orderStatus == 100) {
                $scope.queryMyOrder[index]["showStatusTime"]=$scope.queryMyOrder[index].endTime;
                if(!window.isEmpty(item.isReturn) && !item.isReturn){
                    return "商家拒绝退款,已结算";
                }else{
                    return "已结算";
                }
            }
        };

        //初始化头部导航栏
        $scope.initHeader=function(){
            $scope.queryMyOrder=[];
            if($scope.pageCheck=='drawback'){
                $scope.headerList = [
                    {_id: 6, name: '申请退货'},
                    {_id: 7, name: '确认退货'},
                    {_id: 8, name: '已发货'},
                    {_id: 9, name: '已退款'}
                ];
            }else{
                $scope.headerList = [
                    {_id: -1, name: '全部'},
                    {_id: 1, name: '未付款'},
                    {_id: 2, name: '待发货'},
                    {_id: 4, name: '待收货'},
                    {_id: 5, name: '已收货'},
                    {_id: 100, name: '已结算'}
                ];
            };
            $scope.selectedHead=$scope.headerList[0]._id;
        };

        //切换页面
        $scope.setPageCheck=function(check){
            $scope.pageCheck=check;
            $scope.initHeader();
            $scope.queryMyOrderList();
        };

        $scope.pageNumberO = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryMyOrderList();

        }
        $scope.pageNextO = function (num) {
            $rootScope.pageNo += num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryMyOrderList();
        }
        $scope.pageGoFunO = function (num) {
            if(window.isEmpty(num) || num>$rootScope.dataPage.totalPage){
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryMyOrderList();
        }
        $scope.setPageNo0 = function (num){
            $rootScope.pageNo=num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryMyOrderList();
        }

        //获取订单列表
        $scope.queryMyOrderList = function () {
            //if(!window.isEmpty($rootScope.dataPage)&&$scope.pageGo>$rootScope.dataPage.totalPage){
            //    return;
            //}
            $scope.queryMyOrder = [];
            var url = window.basePath + '/order/OrderInfo/queryMyOrder?pageSize=' + $rootScope.pageSize + '&pageNo=' + $rootScope.pageNo+"&indexNum="+$rootScope.indexNum + "&orderStatus=" + $scope.selectedHead;
            $http.get(url).success(function (re) {
                $scope.queryMyOrder = re.content.orderList;
                $rootScope.dataPage = re.content;
                //$scope.filter.pageNo = $scope.dataPage.pageNo;
                $rootScope.dataPage.$$pageList = [];
                var start = $rootScope.dataPage.pageNo - 3;
                var end = $rootScope.dataPage.pageNo + 4;

                start = start < 1 ? 1 : start;
                end = end > $rootScope.dataPage.totalPage ? $rootScope.dataPage.totalPage : end;

                for (var i = start; i <= end; i++) {
                    $rootScope.dataPage.$$pageList.push(i);
                }
            })
        }
        //根据状态获取订单列表
        $scope.getOrderStatusList = function (index) {
            $scope.selectedHead = index;
            $rootScope.pageSize = 8;
            $rootScope.indexNum = 0;
            $rootScope.pageNo = 1;
            $scope.queryMyOrder = [];
            $scope.isLoadMore = true;
            $scope.queryMyOrderList();
        }

        //删除订单
        $scope.delOrder = function (item) {
            var url = window.basePath + '/order/OrderInfo/delOrderById?pid='+item.pid+'&childOrderId='+item._id;
            $http.get(url).success(function () {
                malert("删除成功");
                $scope.queryMyOrderList();
            });
        };

        $scope.delOrderByOffline = function(item){
            var url = window.basePath+"/order/OrderInfo/del";
            $http.post(url,{_id:item._id}).success(function(){
                malert("删除成功");
                $scope.queryMyOrderList();
            });
        };

        $scope.submitPass=function(id){
            if(window.isEmpty(id)){
                malert("获取订单数据失败");
                return;
            }
            if(!confirm("是否确认收货?")){
                return;
            }
            var url = window.basePath+"/order/OrderInfo/updateOnlineOrderByMember?orderId="+id;
            $http.get(url).success(function(){
                malert("已确认收货");
                $scope.isPassId='';
                $scope.isShowPass=false;

                $rootScope.pageSize = 8;
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $scope.queryMyOrder = [];
                $scope.queryMyOrderList();
            });
        }
        $scope.goComment = function(obj){
            $scope.commentBoxInfo = obj;
            $scope.commentBox = true;
        }
        $scope.submitComment = function(){
            var url = window.basePath + '/order/OrderInfo/addOrderComment';
            var data = {'orderId':$scope.commentBoxInfo._id,
                'serviceStar':$scope.starGrade,
                'name':$scope.commentText,
                'sellerId':$scope.commentBoxInfo.sellerId
            };
            $http.post(url,data).success(function(){
                $scope.countTimeNum = 3;
                $scope.isSuccess = true;
                $scope.goPageTime = $interval(function () {
                    $scope.countTimeNum--;
                    if ($scope.countTimeNum == 0) {
                        $interval.cancel($scope.goPageTime);
                        $scope.queryMyOrderList();
                        $scope.isSuccess = false;
                        $scope.commentBox = false;
                    }
                }, 1000);
                $scope.commentBoxInfo=[];
                $scope.starGrade='';
                $scope.commentText='';
            });
        };

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
                    if($scope.countCart==len) {
                        $interval.cancel(goPageTime);
                        $rootScope.selectedCart = $rootScope.selectedCart.substring(0, $rootScope.selectedCart.length - 1);
                        $rootScope.goPage('/my/orderPay/pid/' + item.pid+'/childOrderId/'+item._id);
                    }
                },1000);
            }else{
                $rootScope.openPage('/my/orderInfo/orderId/'+item._id)
            }
        };

        $scope.setShowMod=function(index,order){
            $scope.showMod[index]=!$scope.showMod[index];
            if($scope.showMod[index]){
                $scope.drawOrderId=order._id;
            }else{
                $scope.drawOrderId='';
            }
        };

        //申请退货
        $scope.returnDescSubmit=function(){
            if(!confirm("是否确认提交?")){
                return;
            }
            var url = window.basePath + '/order/OrderInfo/applyDrawbackOnlineOrder';
            var data = {
                orderId:$scope.drawOrderId,
                returnDesc:$scope.returnDesc
            };
            $http.post(url,data).success(function(){
                malert("申请退货成功!");
                $scope.setShowMod(0);
                $scope.queryMyOrderList();
            });
        };

        //提交退货的发货信息
        $scope.sendDrawbackOrder=function(){
            if(!confirm("是否确认提交?")){
                return;
            }
            $scope.drawOrder.orderId=$scope.drawOrderId;
            var url = window.basePath + "/order/OrderInfo/sendDrawbackOnlineOrder";
            $http.post(url,$scope.drawOrder).success(function(){
                malert("已提交");
                $scope.drawOrder={returnExpress:'',returnExpressNo:''};
                $scope.setShowMod(1);
                $scope.queryMyOrderList();
            });
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的订单');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
                $scope.showMod=[false,false];
                $scope.titleText="order";
                $scope.initHeader();
                $rootScope.pageSize = 8;
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $scope.countCart=0;
                $scope.queryMyOrder = [];
                $scope.isLoadMore = true;
                $scope.queryMyOrderList();
                $scope.selectedHead = -1;
                $scope.isPassId='';
                $scope.isShowPass = false;
                $scope.commentBox = false;
                $scope.isSuccess = false;

            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);