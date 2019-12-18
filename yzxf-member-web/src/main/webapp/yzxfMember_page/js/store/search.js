(function (angular, undefined) {

    var model = 'store';
    var entity = 'search';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //查询关键字
        $scope.search = function () {
            if ($scope.keyWord != null && $scope.keyWord != '') {
                $scope.isSearch = true;
                $scope.isLoadMore = true;
                var url = window.basePath + "/account/StoreInfo/queryKeyword?keyword=" + $scope.keyWord + "&indexNum=" + window.indexNum + "&pageNo=" + window.pageNo + "&pageSize=" + window.pageSize + "&type=" + $scope.queryType;
                $http.get(url).success(function (re) {
                    $.each(re.content.resultList, function (k, v) {
                        if (v._id.substr(0,1)=='S') {
                            v.type = 's';
                            $scope.sellerList.push(v);
                        } else {
                            v.type = 'p';
                            $scope.productList.push(v);
                        }
                        $scope.resultList.push(v);
                    })
                    $scope.totalNumer = re.content.totalNum;
                    if ($scope.queryType == 'all' || $scope.restartSearch == true) {
                        $scope.totalNum = re.content.totalNum;
                        $scope.sellerSize = re.content.sellerNum;
                        $scope.productSize = re.content.productNum;
                    }
                    $scope.totalPage = re.content.totalPage;
                    if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                        $scope.isLoadMore = false;
                    }
                });
            } else {
                $scope.resultList = [];
                $scope.sellerList = [];
                $scope.productList = [];
                $scope.totalNum = 0;
                $scope.sellerSize = 0;
                $scope.totalNumer = 0;
                $scope.productSize = 0;
                $scope.isLoadMore = false;
                malert("搜索关键字不能为空!");
                return;
            }
        }
        $scope.querySearch = function () {
            window.indexNum = 0;
            window.pageNo = 1;
            window.pageSize = 8;
            $scope.resultList = [];
            $scope.sellerList = [];
            $scope.productList = [];
            $scope.isLoadMore = true;
            $scope.restartSearch = true;
            $rootScope.keyword = $scope.keyWord;
            $scope.search();
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
                //$("#moreButton").click();
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
            $('#storeScroll').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.more = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.search();
        };
        $scope.changgeShow = function (type) {
            if (type == 'all') {
                $(".sDaohang button").removeClass("selected");
                $(".sDaohang button").eq(0).addClass("selected");
                window.indexNum = 0;
                window.pageNo = 1;
                $scope.resultList = [];
                $scope.queryType = "all";
                $scope.search();
                $scope.restartSearch = true;
            } else if (type == 'seller') {
                $(".sDaohang button").removeClass("selected");
                $(".sDaohang button").eq(1).addClass("selected");
                window.indexNum = 0;
                window.pageNo = 1;
                $scope.resultList = [];
                $scope.queryType = "seller";
                $scope.search();
                $scope.restartSearch = false;
            } else if (type == 'product') {
                $(".sDaohang button").removeClass("selected");
                $(".sDaohang button").eq(2).addClass("selected");
                window.indexNum = 0;
                window.pageNo = 1;
                $scope.resultList = [];
                $scope.queryType = "product";
                $scope.search();
                $scope.restartSearch = false;
            }
        }
        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                //$rootScope.windowTitle = '主页';
                window.setWindowTitle($rootScope, '搜索');
                //$(".sDaohang button").eq(0).addClass("selected");
                $scope.isLoadMore = true;
                $scope.isSearch = false;
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.queryType = "all";
                $scope.resultList = [];
                $scope.sellerList = [];
                $scope.productList = [];
                $scope.keyWord = $rootScope.keyword;
                if ($scope.keyWord != null) {
                    $scope.search();
                }
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);