(function(angular, undefined) {
    var model = "my";
    var entity = "depositRecord";
    window.app.register.controller('my_depositRecord_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {
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
            $scope.getRecord();
        };

        $scope.getPayType=function(type){
            if(type=='3'){
                return '积分兑换';
            }else if(type=='10'){
                return '微信购买';
            }else if(type=='4'){
                return '支付宝购买';
            }else {
                return '';
            }
        }

        $scope.getPayTypeIcon=function(type){
            if(type=='3'){
                return 'icon-cshy-rmb2 deepRed';
            }else if(type=='10'){
                return 'icon-iconfontweixin limeGreen';
            }else if(type=='4'){
                return 'icon-zhifubao iconfont dodgerBlue';
            }else {
                return '';
            }
        }

        $scope.getRecordDate=function(time,index){
            if(!window.isEmpty(time)){
                return time.split(" ")[index];
            }
        }

        $scope.getRecord=function(){
            var url = window.basePath + '/order/OrderInfo/getDepositRecord?pageSize=' + window.pageSize + '&pageNo=' + window.pageNo + "&indexNum=" + window.indexNum;
            $http.get(url).success(function(re){
                $.each(re.content.orderList, function (k, v) {
                    $scope.record.push(v);
                });
                $scope.totalNumber = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '购买记录');
                $scope.record = [];
                window.pageNo = 1;
                window.indexNum = 0;
                window.pageSize = 10;
                $scope.isLoadMore = true;
                $scope.getRecord();
                $scope.addScrollEvent();
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });


})(angular);