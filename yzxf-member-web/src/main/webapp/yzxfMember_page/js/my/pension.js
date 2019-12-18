(function (angular, undefined) {

    var model = 'my';
    var entity = 'pension';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.selectedText = '线下交易';

        //默认显示当月的记录
        $scope.startDateTime = new Date().setDate(1);
        $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);

        //刷新页面
        $scope.refresh = function () {
            window.location.reload();
        }
        $scope.getMyPension = function () {
            var url = window.basePath + '/order/OrderInfo/getPensionCount';
            $http.get(url).success(function (re) {
                $scope.pensionMoney = re.content;
            })

        }
        //投保判断
        $scope.isInsureFun = function (isInsure, insureCount, insureCountUse) {

            if (isInsure) {
                if (window.isEmpty(insureCountUse) || insureCountUse == 'null') {
                    insureCountUse = 0;
                }
                return '已投保: ' + $rootScope.getMoney(insureCountUse) + " 元";
            }
            if (window.isEmpty(insureCount) || insureCount == 'null') {
                insureCount = 0;
            }
            return '未投保: ' + $rootScope.getMoney(insureCount) + " 元";
        }

        $scope.getOrderType=function(type){
            if (type == "0") {
                return '会员扫码';
            } else if (type == "1") {
                return '现金交易';
            } else if (type == "2") {
                return '非会员扫码';
            } else if (type == "3") {
                return '商家充值';
            } else if (type == "4") {
                return '服务站充值';
            } else if (type == "5") {
                return '会员充值';
            } else if (type == "6") {
                return '会员替朋友充值';
            } else if (type == "7") {
                return '服务站激活会员卡';
            } else if (type == "8") {
                return '会员端激活会员卡';
            } else if (type == "9") {
                return '商家提现';
            } else if (type == "10") {
                return '发卡点提现';
            } else if (type == "11") {
                return '会员在线购买';
            } else if (type == "12") {
                return '快易帮现金收款';
            } else if (type == "13") {
                return '养老金激活会员卡';
            } else if (type == "14") {
                return '会员提现';
            }
        }

        $scope.getOrderList = function () {
            if ($scope.showLoginSelect) {
                $scope.orderList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 10;
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
                malert("开始时间不能大于结束时间");
            }

            var url = window.basePath;
            if ($scope.selectedText == '线上交易') {
                url += '/crm/Member/getMyPensionMoney?startTime=' + $scope.startDateTime + '&endTime=' + $scope.endDateTime + '&pageSize=' + window.pageSize + '&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum;
            } else if ($scope.selectedText == '线下交易') {
                url += '/crm/Member/getMyPensionMoneyByOffline?startTime=' + $scope.startDateTime + '&endTime=' + $scope.endDateTime + '&pageSize=' + window.pageSize + '&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum;
            }

            $http.get(url).success(function (re) {
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

        $scope.getMyInfo = function () {
            var url = window.basePath + '/crm/Member/getMyInfo';
            $http.get(url).success(function (re) {
                $rootScope.myInfo = re.content;
                if(!$rootScope.myInfo.isRealName && !$rootScope.myInfo.isBindCard){
                    $scope.showText = "点击前往实名认证并激活养老金补充卡，享投保服务";
                    $scope.goPageText="/my/realName/isActive/Y";
                }
                if($rootScope.myInfo.isRealName){
                    $scope.showText = "您已认证，点击前往激活养老金补充卡，享投保服务";
                    $scope.goPageText="/my/orderPay";
                }
                if($rootScope.myInfo.isRealName && $rootScope.myInfo.isBindCard){
                    $scope.showText = "";
                    $scope.goPageText="";
                }
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的养老金');
                //$rootScope.windowTitleHide = true;
                $rootScope.isIndex = false;
                $rootScope.isLoginPage = true;
                $scope.showLoginSelect = false;
                window.pageSize = 10;
                window.indexNum = 0;
                window.pageNo = 1;
                $scope.orderList = [];
                $scope.isLoadMore = true;
                $scope.getOrderList();
                $scope.getMyPension();
                $scope.addScrollEvent();
                $scope.getMyInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);


