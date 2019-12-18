(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeCommodity';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {


        $scope.EventStatus = false;
        $scope.menuCheck = false;
        $scope.curDate = new Date().getTime();
        $scope.queryData = function () {
            //获取商品
            $http.get(window.basePath + '/order/ProductInfo/queryCommoditys?pageSize=5&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum).success(function (re) {
                $.each(re.content.productList, function (k, v) {
                    $scope.commoditys.push(v);
                })
                $scope.totleNum = re.content.count;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage <= re.content.pageNo) {
                    $scope.isLoadMore = false;
                }
            });
        };

        $scope.del = function (index, _id) {
            //$("." + index).find(".del").hide();
            //$("." + index).slideUp(300);
            ////$scope.commoditys.splice(index, 1);
            var data = {"_id": _id};
            $http.post(window.basePath + '/order/ProductInfo/delCommodity', data).success(function (re) {
                $scope.commoditys.splice(index, 1);
                malert('删除成功!');
                $scope.menuCheck = false;
            });
        };
        $scope.delConfirm = function(index, _id){
            $scope.menuCheck = true;
            $scope.delIndex = index;
            $scope.delId = _id;
        }


        $scope.scrollEvent = function () {
            if ($("#moreButton").size() == 0) {
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
            $scope.queryData();
        };

        $scope.showDel = function () {
            //$(".del").animate({width: 'toggle'});
            $scope.isDel = !$scope.isDel;
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商品');
                window.pageNo = 1;
                window.indexNum = 0;
                $scope.isLoadMore = true;
                $scope.commoditys = [];
                $scope.queryData();
                $scope.addScrollEvent();
                $scope.isDel = false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();


    });
})(angular);