/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'home';
    var entity = 'cardRecords';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //默认显示当月的记录
        $scope.startDateTime = new Date().setDate(1);
        $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);

        $scope.getCardListAll = function () {
            $scope.memberCard = "";
            $scope.memberPhone = "";
            $scope.memberName = "";
            $scope.memberIdCard = "";
            $scope.CardLogList = [];
            //默认显示今天的记录
            $scope.isAll=true;
            var dateTemp = new Date();
            dateTemp.setFullYear(2017,0,1);
            dateTemp.setHours(0, 0, 0, 0);
            $scope.startDateTime = dateTemp.getTime();
            $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);
            $scope.getSendCardLog();
        }
        $scope.getCardList = function () {
            $scope.CardLogList = [];
            $scope.getSendCardLog();
        }
        $scope.getSendCardLog = function () {
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

            var data = {
                indexNum: window.indexNum,
                pageNo: window.pageNo,
                pageSize: window.pageSize,
                startTime: $scope.startDateTime,
                endTime: $scope.endDateTime
            }
            if ($scope.memberCard != null) {
                data.memberCard = $scope.memberCard;
            }
            if ($scope.memberPhone != null) {
                data.memberPhone = $scope.memberPhone;
            }
            if ($scope.memberName != null) {
                data.memberName = $scope.memberName;
            }
            if ($scope.memberIdCard != null) {
                data.memberIdCard = $scope.memberIdCard;
            }
            var url = window.basePath + '/account/Seller/getSendCardLog';
            $http.post(url, data).success(function (re) {
                $.each(re.content.sendCardList, function (k, v) {
                    $scope.CardLogList.push(v);
                })
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
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
            $scope.getSendCardLog();
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '发卡记录');
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isLoadMore = true;
                $scope.CardLogList = [];
                $scope.getSendCardLog();
                $scope.addScrollEvent();
                $scope.memberCard=null;
                $scope.memberPhone=null;
                $scope.memberName=null;
                $scope.memberIdCard=null;
                $scope.isAll=false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
