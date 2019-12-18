(function (angular, undefined) {

    var model = 'home';
    var entity = 'belongMember';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.getBelongMember = function(){
            var api = 'getMemberListByFactor';
            if(!window.isEmpty($rootScope.pathParams.userType) && $rootScope.pathParams.userType==='Seller'){
                api = 'getMemberListBySeller';
            }
            var url = window.basePath+"/crm/Member/"+api+"?pageSize=20&pageNo="+window.pageNo;
            $http.get(url).success(function(re){
                if(window.isEmpty($scope.memberList)){
                    $scope.memberList=[];
                }

                $.each(re.content.items, function (k, v) {
                    $scope.memberList.push(v);
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
            $scope.getBelongMember();
        };
        
        $scope.initPage=function(){
            window.indexNum = 0;
            window.pageNo = 1;
            // window.pageSize = 8;
            $scope.isLoadMore = true;
        }

        $scope.getQrcode=function(){
            $scope.showCode=!$scope.showCode;
            var qr = qrcode(10, 'H');
            qr.addData('https://m.phsh315.com/yzxfMember/account/reg/shareId/'+$rootScope.$$factor._id+'/shareType/Factor');
            qr.make();
            $(".qrcode").html(qr.createImgTag());
            $(".qrcode img").css({height:'100%',width:'100%'});
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '归属会员');
                $scope.initPage();
                $scope.getBelongMember();
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);