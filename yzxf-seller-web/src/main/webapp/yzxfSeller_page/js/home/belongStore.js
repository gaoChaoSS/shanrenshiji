(function (angular, undefined) {

    var model = 'home';
    var entity = 'belongStore';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.entityPage='belongStore';
        $scope.getBelongSeller = function(){
            var url = window.basePath+"/account/StoreInfo/getBelongSeller?pageSize=20&pageNo="+window.pageNo;
            $http.get(url).success(function(re){
                // $scope.sellerList = re.content.items;
                // $scope.page = re.content;
                if(window.isEmpty($scope.sellerList)){
                    $scope.sellerList=[];
                }

                $.each(re.content.items, function (k, v) {
                    $scope.sellerList.push(v);
                });
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            });
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
            $scope.getBelongSeller();
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '归属商家');
                window.indexNum = 0;
                window.pageNo = 1;
                // window.pageSize = 8;
                $scope.isLoadMore = true;
                $scope.getBelongSeller();
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);