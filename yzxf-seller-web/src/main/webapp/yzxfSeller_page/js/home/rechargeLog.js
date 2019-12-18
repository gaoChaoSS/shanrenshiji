(function (angular, undefined) {
    var model = "home";
    var entity = "rechargeLog";
    window.app.register.controller('home_rechargeLog_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //查询交易记录
        $scope.getLog = function () {
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
            if ($scope.startDateTime > $scope.endDateTime) {
                $scope.startDateTime = $scope.endDateTime - (1000 * 60 * 60 * 24 - 1);
            }

            if(window.isEmpty($rootScope.pathParams.orderType) || !/^[349]|(10)$/.test($rootScope.pathParams.orderType)){
                malert("获取订单类型错误");
                return;
            }
            if(window.isEmpty($rootScope.pathParams.userType) || !/^(Seller)|(Factor)$/.test($rootScope.pathParams.userType)){
                malert("获取用户类型错误");
                return;
            }
            var url = window.basePath + '/account/'+$rootScope.pathParams.userType+'/queryTransaction?'
                + 'indexNum=' + window.indexNum
                + "&pageNo=" + window.pageNo
                + "&pageSize=" + window.pageSize
                + "&tradeType="+$rootScope.pathParams.orderType
                + "&startTime="+$scope.startDateTime
                + "&endTime="+$scope.endDateTime;
            $http.get(url).success(function (re) {
                $.each(re.content.items, function (k, v) {
                    $scope.order.push(v);
                });
                $scope.total=re.content;
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            })
        };

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
            $scope.queryTransaction();
        };

        $scope.getStorePayType = function(payType){
            if(payType=='4') {
                return '支付宝支付';
            }else if(payType=='10'){
                return '微信支付';
            }else if(payType=='3'){
                return '余额支付';
            }else if(payType=='6'){
                return '现金支付';
            }
        };

        $scope.setPage=function(){
            if(window.isEmpty($rootScope.pathParams.orderType)){
                return;
            }
            if(/^[34]$/.test($rootScope.pathParams.orderType)){
                $scope.pageTitle = "充值"
            }else if(/^(9)|(10)$/.test($rootScope.pathParams.orderType)){
                $scope.pageTitle = "提现"
            }
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.pageTitle="";
                $scope.setPage();
                window.setWindowTitle($rootScope, $scope.pageTitle+'明细');

                //默认显示当月的记录
                $scope.startDateTime = new Date().setHours(0, 0, 0, 0);
                $scope.endDateTime = new Date().setHours(24, 0, 0, 0) - 1;

                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.order=[];

                $scope.getLog();
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);