(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeEvent';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.EventStatus = false;
        //获取活动
        $scope.getEventList = function () {
            var url = window.basePath + "/account/Seller/getStoreEvent?indexNum=" + window.indexNum + "&pageSize=" + window.pageSize + "&pageNo=" + window.pageNo;
            $http.get(url).success(function (re) {
                $.each(re.content.eventList, function (k, v) {
                    $scope.eventList.push(v);
                })
                $scope.totalActivityPage = re.content.totalPage;
                $scope.totalActivityNumber = re.content.totalNum;
                if ($scope.totalActivityPage != 0 && $scope.totalActivityPage <= window.pageNo) {
                    $scope.isActivityLoadMore = false;
                }
                for (var index in $scope.eventList) {
                    if (!$scope.eventList[index].isGoing || $scope.eventList[index].isOverdue) {
                        $scope.eventList[index].$$goingCheck = '已结束';
                        $scope.eventList[index].$$goingNo = 2;
                    } else {
                        if ($scope.eventList[index].isNoStart) {
                            $scope.eventList[index].$$goingCheck = '未开始';
                            $scope.eventList[index].$$goingNo = 0;
                        } else {
                            $scope.eventList[index].$$goingCheck = '进行中';
                            $scope.eventList[index].$$goingNo = 1;
                        }
                    }
                }
            })
        }
        $scope.moreActivity = function () {
            $scope.isActivityLoadMore = true;
            window.indexNum++;
            window.pageNo++;
            $scope.getEventList();
        }
        $scope.addScrollEvent = function () {
            $('.overflowPC').unbind('scroll');
            $(window).unbind('scroll');
            $('#storeInfoScroll').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
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
                $scope.moreActivity();
            }
            if ($('#moreButton').offset().top < ($(window).scrollTop() + $(window).height())) {
                window.stopScroll || (window.stopScroll = 0);
                window.stopScroll++;
                loadNextPage.delay(0.5);
            }
        }

        //颜色判断
        $scope.colorCheck = function (num) {
            if (num == 0) {
                return 'triangleEventBig borderTopYellow';
            } else if (num == 1) {
                return 'triangleEventBig borderTopRed';
            } else if (num == 2) {
                return 'triangleEventBig borderTopGray';
            }
        }

        $scope.closeEvent = function (id, isGoing) {
            var url = window.basePath + '/account/Seller/closeEvent?_id=' + id + "&&isGoing=" + isGoing;
            $http.get(url).success(function () {
                $scope.eventList=[];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.getEventList();
            })
        }
        $scope.delConfirm = function(id){
            $scope.menuCheck = true;
            $scope.delId = id;
        }
        $scope.deleteEvent = function (id){
            var url = window.basePath + '/account/Seller/deleteEvent?_id=' + id;
            $http.get(url).success(function () {
                $scope.menuCheck=false;
                malert('删除成功!');
                $scope.eventList=[];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.getEventList();
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '店铺活动');
                $scope.isActivityLoadMore = true;
                $scope.eventList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.getEventList();
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);