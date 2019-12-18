(function (angular, undefined) {

    var model = 'my';
    var entity = 'team';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //默认显示当月的记录
        $scope.startDateTime = new Date().setDate(1);
        $scope.endDateTime = new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);

        $scope.getItemTitle = function(item){
            var str ="";
            if(!$rootScope.isEmpty2(item.cardNo)){
                str = item.cardNo+' ('+$rootScope.getMobile(item.mobile)+")";
            }else{
                str = $rootScope.getMobile(item.mobile)
            }
            return str;
        }

        $scope.getTeamUpUnder = function(){
            var api = "getTeamUpUnder";
            if($scope.notFirstLoad){
                api = "getTeamUnder"
            }
            var url = window.basePath + '/order/Team/'+api+'?pageNo='+window.pageNo+"&pageSize="+window.pageSize;
            $http.get(url).success(function(re){
                if(window.isEmpty($scope.memberList)){
                    $scope.memberList=[];
                }
                $scope.updatePage($scope.notFirstLoad?re.content.items:re.content.under);
                if(!$scope.notFirstLoad){
                    $scope.member = re.content.up;
                }
                $scope.notFirstLoad = true;
            })
        };

        $scope.updatePage = function(under){
            $.each(under.items, function (k, v) {
                $scope.memberList.push(v);
            });
            $scope.totalNumer = under.totalNum;
            $scope.totalPage = under.totalPage;
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
            $scope.getTeamUpUnder();
        };

        $scope.initPage=function(){
            window.indexNum = 0;
            window.pageNo = 1;
            window.pageSize = 10;
            $scope.isLoadMore = true;
        }

        $scope.getQrcode=function(){
            $scope.showCode=!$scope.showCode;
            var qr = qrcode(10, 'H');
            qr.addData('http://m.phsh315.com/yzxfMember/account/reg/shareId/'+$rootScope.$$myInfo._id);
            qr.make();
            $(".qrcode").html(qr.createImgTag());
            $(".qrcode img").css({height:'100%',width:'100%'});
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.titleName = '团队成员';
                window.setWindowTitle($rootScope, $scope.titleName);

                $scope.initPage();
                $scope.getTeamUpUnder();
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
