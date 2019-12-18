(function (angular, undefined) {
    var model = "store";
    var entity = "order";
    window.app.register.controller('store_order_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.getOrderStatus = function (item,index) {
            if (item.orderStatus == 0) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].createTime;
                return "草稿";
            } else if (item.orderStatus == 1) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].createTime;
                return "未支付";
            } else if (item.orderStatus == 2) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].bookingTime;
                return "会员已付款,等待发货";
            } else if (item.orderStatus == 3) {
                return "商家打包制作中";
            } else if (item.orderStatus == 4) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].sendTime;
                return "已发货";
            } else if (item.orderStatus == 5) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].accountTime;
                return "买家已收货";
            } else if (item.orderStatus == 6) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnApplyTime;
                return "[退货]买家申请退货";
            } else if (item.orderStatus == 7) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnApplyTime;
                return "[退货]买家准备发货";
            } else if (item.orderStatus == 8) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnApplyTime;
                return "[退货]买家已发货";
            } else if (item.orderStatus == 9) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].returnTime;
                return "[退货]已退款";
            } else if (item.orderStatus == 100) {
                $scope.orderList[index]["showStatusTime"]=$scope.orderList[index].endTime;
                if(item.isReturn){
                    return "拒绝退款,已结算";
                }else{
                    return "已结算";
                }
            }
        };

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
                window.setWindowTitle($rootScope, '退款售后管理');
                $scope.headerList = [
                    {_id: -2, name: '全部退款售后', icon: 'icon-calendar'},
                    {_id: 6, name: '申请退款', icon: 'icon-calendar'},
                    {_id: 7, name: '等待发货', icon: 'icon-daifahuo'},
                    {_id: 8, name: '已发货', icon: 'icon-daishouhuo'},
                    {_id: 9, name: '已退款', icon: 'icon-cshy-rmb2'},
                ];
            }else{
                window.setWindowTitle($rootScope, '在线订单管理');
                $scope.headerList = [
                    {_id: -1, name: '全部在线订单', icon: 'icon-calendar'},
                    {_id: 2, name: '待发货', icon: 'icon-daifahuo'},
                    {_id: 4, name: '已发货', icon: 'icon-daishouhuo'},
                    {_id: 5, name: '已收货', icon: 'icon-jiaoyi'},
                    {_id: 100, name: '已结算', icon: 'icon-cshy-rmb2'}
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

        //获取订单列表
        $scope.getOrderList = function () {
            var url = window.basePath + '/order/OrderInfo/queryMyOrder?' +
                'pageSize=' + window.pageSize+
                '&pageNo=' + window.pageNo +
                "&indexNum=" + window.indexNum +
                "&orderStatus=" + $scope.selectedHead._id+
                "&userType=seller";
            $http.get(url).success(function (re) {
                $.each(re.content.orderList, function (k, v) {
                    $scope.orderList.push(v);
                });
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
                //$scope.setHeaderCount(re.content.countList);
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
        };

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
        };

        //删除订单
        $scope.delOrder = function (id) {
            var url = window.basePath + '/order/OrderInfo/del';
            var data = {'_id': id};
            $http.post(url, data).success(function () {
                malert("删除成功");
                $scope.getOrderList();
            });
        };

        $scope.setShowDraw=function(_id){
            $scope.isShowDraw=!$scope.isShowDraw;
            if($scope.isShowDraw){
                $scope.showDrawId = _id;
            }else{
                $scope.showDrawId = '';
            }
        };

        //确认退款收货
        $scope.submitDrawback=function(){
            var url = window.basePath + '/order/OrderInfo/endDrawbackOnlineOrder';
            $http.post(url, {orderId:$scope.showDrawId}).success(function () {
                malert("已确认收货,退款成功");
                $scope.isShowDraw=false;
                $scope.orderList = [];
                $scope.getOrderStatusList($scope.selectedHead._id);
            });
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.pageSize = 8;
                window.indexNum = 0;
                window.pageNo = 1;
                $scope.orderList = [];
                $scope.isLoadMore = true;
                $scope.isShowDraw = '';
                $scope.pageCheck = $rootScope.pathParams.pageCheck;
                $scope.initHeader();
                $scope.addScrollEvent();
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