(function (angular, undefined) {
    var model = "my";
    var entity = "comment";
    window.app.register.controller('my_comment_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.getMyComment = function () {
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + "/crm/OrderComment/getMyComment?memberId=" + $scope.memberId + '&pageSize=' + window.pageSize + '&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum;
            $http.get(url).success(function (re) {
                $.each(re.content.commentList, function (k, v) {
                    $scope.commentList.push(v);
                })
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            });
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
                $("#moreButton").click();
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
            $scope.getMyComment();
        }
        $scope.starNo = function (number) {
            var str = "";
            for (var no = 0; no < number; no++) {
                str += '★';
            }
            return str;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的评价');
                $scope.isLoadMore = true;
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.commentList = [];
                $scope.getMyComment();
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