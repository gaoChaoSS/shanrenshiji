(function (angular, undefined) {

    var model = 'home';
    var entity = 'tradeList';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //默认显示今天的记录
        $scope.startDateTime = new Date().setDate(1);
        $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);

        $scope.iconImgUrl = function (icon) {
            return $scope.queryMyOrder != null && icon != null ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
        }

        $scope.getPayType=function(type){
            if(type==3){
                return '余额';
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

        $scope.queryOrderList = function () {
            $scope.orderList = [];
            window.indexNum=0;
            window.pageNo=1;
            window.pageSize=8;
            $scope.queryFactorOrderList();
        }
        $scope.queryOrderListAll = function () {
            $scope.memberCard = "";
            $scope.memberPhone = "";
            $scope.memberName = "";
            $scope.memberIdCard = "";
            $scope.orderList = [];
            //默认显示今天的记录
            $scope.isAll=true;
            var dateTemp = new Date();
            dateTemp.setFullYear(2017,0,1);
            dateTemp.setHours(0, 0, 0, 0);
            $scope.startDateTime = dateTemp.getTime();
            $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);
            $scope.queryFactorOrderList();
        }
        $scope.queryFactorOrderList = function () {
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
                malert("开始时间不能大于结束时间");
            }

            var url = window.basePath + '/account/Factor/queryFactorMoreOrderList';
            var date = {
                startTime: $scope.startDateTime,
                endTime: $scope.endDateTime,
                search: $scope.search,
                indexNum: window.indexNum,
                pageNo: window.pageNo,
                pageSize: window.pageSize
            }
            if ($scope.memberCard != null) {
                date.memberCard = $scope.memberCard;
            }
            if ($scope.memberPhone != null) {
                date.memberPhone = $scope.memberPhone;
            }
            if ($scope.memberName != null) {
                date.memberName = $scope.memberName;
            }
            if ($scope.memberIdCard != null) {
                date.memberIdCard = $scope.memberIdCard;
            }
            $http.post(url, date).success(function (re) {
                $.each(re.content.orderList, function (k, v) {
                    $scope.orderList.push(v);
                })
                $scope.totalPage = re.content.totalPage;
                $scope.totalNumber = re.content.totalNum;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
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
            $scope.queryFactorOrderList();
        };

        $scope.searchCheckFun = function () {
            if (!window.isEmpty($scope.search)) {
                $scope.searchCheck = true;
            } else {
                $scope.searchCheck = false;
            }
            $scope.queryFactorOrderList();
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '会员交易');
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isAll=false;
                $scope.orderList = [];
                $scope.isLoadMore = true;
                $scope.addScrollEvent();
                $scope.queryFactorOrderList();
                $scope.memberCard=null;
                $scope.memberPhone=null;
                $scope.memberName=null;
                $scope.memberIdCard=null;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

