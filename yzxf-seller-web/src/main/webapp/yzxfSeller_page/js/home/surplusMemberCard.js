/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'home';
    var entity = 'surplusMemberCard';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.getSendCardLog = function () {

            $scope.factorId = getCookie('_factor_id');
            var data = {
                factorId: $scope.factorId,
                indexNum: window.indexNum,
                pageNo: window.pageNo,
                pageSize: window.pageSize,
            }
            var url = window.basePath + '/account/Seller/getNotSendCardLog';
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

        $scope.cardNO = function(startCardEnd){
            $scope.cardNofield ="";
            for(var i= 0;i<6-startCardEnd.toString().length;i++){
                $scope.cardNofield += '0';
            }
            return $scope.cardNofield+startCardEnd;
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
                window.setWindowTitle($rootScope, '未激活会员卡号段');
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isLoadMore = true;
                $scope.CardLogList = [];
                $scope.getSendCardLog();
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
