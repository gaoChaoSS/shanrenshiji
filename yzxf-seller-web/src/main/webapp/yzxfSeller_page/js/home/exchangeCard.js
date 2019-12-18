(function (angular, undefined) {

    var model = 'home';
    var entity = 'exchangeCard';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.exchangeCard = function(){
            if(!/^1[34578]{1}\d{9}$/.test($scope.phoneNumber)){
                malert('请输入正确的会员手机号');
                return;
            }
            if(window.isEmpty($scope.oldCard)){
                malert('请输入旧卡号');
                return;
            }
            if(window.isEmpty($scope.newCard)){
                malert('请输入新卡号');
                return;
            }

            var url = window.basePath + '/crm/Member/exchangeCardToFactor';
            var data = {
                phoneNumber:$scope.phoneNumber,
                newCard:$scope.newCard,
                oldCard:$scope.oldCard
            }
            $http.post(url,data).success(function(re){
                malert('换卡成功!');
                $rootScope.goPage('/home/index');
            })
        }
        $scope.getExchangeCardList = function(){
            var url = window.basePath + '/crm/Member/getExchangeCardList?indexNum=' + window.indexNum +'&pageNo='+window.pageNo+'&pageSize='+window.pageSize;
            $http.get(url).success(function(re){
                $.each(re.content.exchangeCardList, function (k, v) {
                    $scope.exchangeCardList.push(v);
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
            $scope.getExchangeCardList();
        };



        $scope.cardNO = function(startCardEnd){
            $scope.cardNofield ="";
            for(var i= 0;i<8-startCardEnd.toString().length;i++){
                $scope.cardNofield += '0';
            }
            return $scope.cardNofield+startCardEnd;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '换卡');
                $scope.countdown = 4;
                $scope.phoneNumber = null;
                $scope.status = null;
                $scope.exchangeCardList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.getExchangeCardList();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);