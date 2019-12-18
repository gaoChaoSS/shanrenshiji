(function (angular, undefined) {

    var model = 'my';
    var entity = 'comment';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';

        $scope.pageNumber = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();

        }
        $scope.pageNext = function (num) {
            $rootScope.pageNo += num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();
        }
        $scope.pageGoFun = function (num) {
            if(window.isEmpty(num) || num>$rootScope.dataPage.totalPage){
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();

        }
        $scope.setPageNo = function (num){
            $rootScope.pageNo=num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();
        }

        $scope.queryCurrentList = function () {
            //if(!window.isEmpty($rootScope.dataPage)&&$scope.pageGo>$rootScope.dataPage.totalPage){
            //    return;
            //}
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + "/crm/OrderComment/getMyComment?memberId=" + $scope.memberId + '&pageSize=' + $rootScope.pageSize + '&pageNo=' + $rootScope.pageNo + "&indexNum=" + $rootScope.indexNum;
            $http.get(url).success(function (re) {
                $scope.commentList=re.content.commentList;
                $rootScope.dataPage = re.content;
                //$scope.filter.pageNo = $scope.dataPage.pageNo;
                $rootScope.dataPage.$$pageList = [];
                var start = $rootScope.dataPage.pageNo - 3;
                var end = $rootScope.dataPage.pageNo + 4;

                start = start < 1 ? 1 : start;
                end = end > $rootScope.dataPage.totalPage ? $rootScope.dataPage.totalPage : end;

                for (var i = start; i <= end; i++) {
                    $rootScope.dataPage.$$pageList.push(i);
                }
            });
        }
        $scope.starNo = function (number) {
            var str = "";
            for (var no = 0; no < number; no++) {
                str += '★';
            }
            return str;
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的评价');
                $scope.titleText="comment";
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $rootScope.pageSize = 8;
                $scope.commentList = [];
                $scope.queryCurrentList();
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);