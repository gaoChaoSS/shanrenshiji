(function (angular, undefined) {

    var model = 'my';
    var entity = 'collection';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.titleCheck = true;

        //获取商品列表
        $scope.getCollectionList = function (type) {
            $scope.titleCheck = true;
            if (type == 'first') {
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.collectionList = [];
            }
            var userId = getCookie("_member_id");
            var url = window.basePath + '/crm/MemberCollection/getMyCollection?id=' + userId + '&pageSize=' + window.pageSize + '&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum;
            $http.get(url).success(function (re) {
                $.each(re.content.collectionList, function (k, v) {
                    $scope.collectionList.push(v)
                })
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            })
        }
        //获取商家
        $scope.getSellerList = function (type) {
            $scope.titleCheck = false;
            if (type == 'first') {
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.sellerList = [];
                $scope.isSLoadMore = true;
            }
            var userId = getCookie("_member_id");
            var url = window.basePath + '/crm/MemberCollection/getMyCollectionStore?id=' + userId + '&pageSize=' + window.pageSize + '&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum;
            $http.get(url).success(function (re) {
                $.each(re.content.sellerList, function (k, v) {
                    $scope.sellerList.push(v)
                })
                $scope.totalSNumer = re.content.totalNum;
                $scope.totalSPage = re.content.totalPage;
                if ($scope.totalSPage != 0 && $scope.totalSPage <= window.pageNo) {
                    $scope.isSLoadMore = false;
                }
            })
        }
        $scope.scrollEvent = function () {
            if ($('#moreButton').size() == 0 || $('#morePButton').size() == 0) {
                return;
            }
            var topHeight = 0;
            var loadNextPage = function () {
                window.stopScroll--;

                if (window.stopScroll > 0) {
                    return;
                }

                if (!$scope.titleCheck) {//商家
                    if ($scope.totalSNumer <= window.pageNo) {
                        return;
                    }
                    $scope.more();
                    topHeight = $('#moreButton').offset().top;
                } else {//商品
                    if ($scope.totalNumer <= window.pageNo) {
                        return;
                    }
                    $scope.moreGoods();
                    topHeight = $('#morePButton').offset().top
                }
            }
            if (topHeight < ($(window).scrollTop() + $(window).height())) {
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
            $scope.getSellerList();
        };
        $scope.moreGoods = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.getCollectionList('productInfo');
        };

        //商品收藏与店铺收藏切换
        $scope.titleCheck = true;

        //收藏店铺内容的显示和隐藏
        //$scope.showStore=false;
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的收藏');
                //$rootScope.windowTitleHide = true;
                $scope.titleCheck = true;
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.collectionList = [];
                $scope.sellerList = [];
                $scope.isLoadMore = true;
                $scope.getCollectionList('productInfo');
                $scope.addScrollEvent();
            }
        };
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
