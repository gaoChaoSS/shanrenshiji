/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'queryTransaction';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.selectedText = '线下交易';


        $scope.getOrderStatus = function (item,index) {
            if (item.orderStatus == 0) {
                return "草稿";
            } else if (item.orderStatus == 1) {
                return "未支付";
            } else if (item.orderStatus == 2) {
                $scope.orderList[index]["showStatusTime"]='下单时间: '+$rootScope.showYFullTime($scope.orderList[index].bookingTime);
                return "会员已付款,等待发货";
            } else if (item.orderStatus == 3) {
                return "商家打包制作中";
            } else if (item.orderStatus == 4) {
                $scope.orderList[index]["showStatusTime"]='发货时间: '+$rootScope.showYFullTime($scope.orderList[index].sendTime);
                return "已发货";
            } else if (item.orderStatus == 5) {
                $scope.orderList[index]["showStatusTime"]='收货时间: '+$rootScope.showYFullTime($scope.orderList[index].accountTime);
                return "买家已收货";
            } else if (item.orderStatus == 6) {
                $scope.orderList[index]["showStatusTime"]='申请时间: '+$rootScope.showYFullTime($scope.orderList[index].returnApplyTime);
                return "[退货]买家申请退货";
            } else if (item.orderStatus == 7) {
                $scope.orderList[index]["showStatusTime"]='申请时间: '+$rootScope.showYFullTime($scope.orderList[index].returnApplyTime);
                return "[退货]买家准备发货";
            } else if (item.orderStatus == 8) {
                $scope.orderList[index]["showStatusTime"]='申请时间: '+$rootScope.showYFullTime($scope.orderList[index].returnApplyTime);
                return "[退货]买家已发货";
            } else if (item.orderStatus == 9) {
                $scope.orderList[index]["showStatusTime"]='退款时间: '+$rootScope.showYFullTime($scope.orderList[index].returnTime);
                return "[退货]已退款";
            } else if (item.orderStatus == 100) {
                $scope.orderList[index]["showStatusTime"]='结单时间: '+String($rootScope.showYFullTime($scope.orderList[index].endTime));
                if(!window.isEmpty(String(item.isApplyReturn)) && !item.isApplyReturn){
                    return "拒绝退款,已结算";
                }else{
                    return "已结算";
                }
            }
        };

        //默认显示当月的记录
        $scope.startDateTime = new Date().setDate(1);
        $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);

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
        $scope.getOrderList = function () {
            if ($scope.showLoginSelect) {
                $scope.orderList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isLoadMore = true;
            }
            var s = localStorage.getItem("select_pop_date_startDate");
            if (!window.isEmpty(s)) {
                $scope.startDateTime = parseInt(s);
            }
            localStorage.removeItem("select_pop_date_startDate");

            var e = localStorage.getItem("select_pop_date_endDate");
            if (!window.isEmpty(e)) {
                $scope.endDateTime = parseInt(e);
            }
            localStorage.removeItem("select_pop_date_endDate");

            $scope.startDateTime = new Date($scope.startDateTime).setHours(0, 0, 0, 0);
            $scope.endDateTime = new Date($scope.endDateTime).setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);
            if ($scope.startDateTime >= $scope.endDateTime) {
                $scope.startDateTime = $scope.endDateTime - (1000 * 60 * 60 * 24 - 1);
                $scope.startDate = $scope.endDate;
                malert("开始时间不能大于结束时间");
            }
            var url = window.basePath;
            if ($scope.selectedText == '线上交易') {
                url += '/order/OrderInfo/querySellerOrder';
            } else if ($scope.selectedText == '线下交易') {
                url += '/order/OrderInfo/querySellerOrderByOffline';
            }
            var date = {
                startTime: $scope.startDateTime,
                endTime: $scope.endDateTime,
                search: $scope.search,
                indexNum: window.indexNum,
                pageNo: window.pageNo,
                pageSize: window.pageSize
            }
            if ($scope.search != null) {
                date.search = $scope.search;
            }
            alert("url===="+url);
            $http.post(url, date).success(function (re) {
                $.each(re.content.orderList, function (k, v) {
                    $scope.orderList.push(v);
                })
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            })
        }
        $scope.searchCheckFun = function () {
            $scope.orderList = [];
            window.indexNum = 0;
            window.pageNo = 1;
            window.pageSize = 8;
            $scope.isLoadMore = true;
            $scope.getOrderList();
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
        $scope.getOrderType = function(type){
            if(type==0){
                return "会员扫码";
            }
            if(type==1){
                return "现金交易";
            }
            if(type==2){
                return "非会员扫码";
            }
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '交易查询');
                $scope.orderList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isLoadMore = true;
                $scope.getOrderList();
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

