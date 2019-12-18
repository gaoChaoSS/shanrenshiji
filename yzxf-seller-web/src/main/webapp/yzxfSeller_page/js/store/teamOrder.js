(function (angular, undefined) {

    var model = 'store';
    var entity = 'teamOrder';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //默认显示当月的记录
        $scope.startDateTime = new Date().setDate(1);
        $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);

        $scope.setDateTime = function(){
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
        }

        $scope.getTeamLog = function(){
            $scope.setDateTime();

            var url = window.basePath+'/order/Team/getTeamLog?pageNo='+window.pageNo+"&pageSize="+window.pageSize;
            if(!window.isEmpty($scope.startDateTime)){
                url +="&startTime="+$scope.startDateTime;
            }
            if(!window.isEmpty($scope.endDateTime)){
                url +="&endTime="+$scope.endDateTime;
            }
            $http.get(url).success(function(re){
                $scope.updatePage(re.content);
            })
        }
        $scope.getTeamCount = function(){
            var url = window.basePath+'/order/Team/getTeamCount';
            $http.get(url).success(function(re){
                $scope.countItem = re.content;
            })
        }

        $scope.updatePage = function(content){
            $.each(content.items, function (k, v) {
                $scope.team.push(v);
            });
            $scope.totalNumer = content.totalNum;
            $scope.totalPage = content.totalPage;
            if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                $scope.isLoadMore = false;
            }
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
            $scope.getTeamLog();
        };

        $scope.initPage=function(){
            window.indexNum = 0;
            window.pageNo = 1;
            window.pageSize = 10;
            $scope.isLoadMore = true;
            $scope.team=[];
        }

        $scope.getQrcode=function(){
            $scope.showCode=!$scope.showCode;
            var qr = qrcode(10, 'H');
            qr.addData(JSON.stringify({
                shareMember:$rootScope.$$myInfo._id,
                type:'share'
            }));
            qr.make();
            $(".qrcode").html(qr.createImgTag());
            $(".qrcode img").css({height:'100%',width:'100%'});
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的红包');
                $scope.addScrollEvent();
                $scope.initPage();
                $scope.getTeamLog();
                $scope.getTeamCount();
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
