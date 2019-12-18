(function (angular, undefined) {

    var model = 'my';
    var entity = 'collection';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';
        $scope.clearFun=function(){
            $rootScope.indexNum = 0;
            $rootScope.pageNo = 1;
            $rootScope.pageSize = 8;
        }
        $scope.pageNumberC = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getCollectionList();

        }
        $scope.pageNextC = function (num) {
            $rootScope.pageNo += num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getCollectionList();
        }
        $scope.pageGoFunC = function (num) {
            if(window.isEmpty(num) || num>$rootScope.dataPage.totalPage){
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getCollectionList();
        }
        $scope.setPageNoC = function (num){
            $rootScope.pageNo=num;
            $scope.getCollectionList();
        }
        $scope.pageNumberS = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getSellerList();

        }
        $scope.pageNextS = function (num) {
            $rootScope.pageNo += num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getSellerList();
        }
        $scope.pageGoFunS = function (num) {
            if(window.isEmpty(num) || num>$rootScope.dataPage.totalPage){
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getSellerList();
        }
        $scope.setPageNoS = function (num){
            $rootScope.pageNo=num;
            $scope.getSellerList();
        }
        //获取商品列表
        $scope.getCollectionList = function () {
            //if(!window.isEmpty($rootScope.dataPage)&&$scope.pageGo>$rootScope.dataPage.totalPage){
            //    return;
            //}
            $scope.collectionList = [];
            var userId = getCookie("_member_id");
            var url = window.basePath + '/crm/MemberCollection/getMyCollection?id=' + userId + '&pageSize=' + $rootScope.pageSize + '&pageNo=' + $rootScope.pageNo + "&indexNum=" + $rootScope.indexNum;
            $http.get(url).success(function (re) {
                $.each(re.content.collectionList, function (k, v) {
                    $scope.collectionList.push(v)
                })
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
            })
        }
        //获取商家
        $scope.getSellerList = function () {
            if(!window.isEmpty($rootScope.dataPage)&&$scope.pageGo>$rootScope.dataPage.totalPage){
                return;
            }
            $scope.sellerList = [];
            var userId = getCookie("_member_id");
            var url = window.basePath + '/crm/MemberCollection/getMyCollectionStore?id=' + userId + '&pageSize=' + $rootScope.pageSize + '&pageNo=' + $rootScope.pageNo + "&indexNum=" + $rootScope.indexNum;
            $http.get(url).success(function (re) {
                $.each(re.content.sellerList, function (k, v) {
                    $scope.sellerList.push(v)
                })
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
            })
        }
        $scope.deleteCollection = function(type,id){
            var url = '';
            if(type=='goods'){
                url = window.basePath + '/crm/MemberCollection/addOrDelGoodsCollection?goodsId='+id;
            }else{
                url = window.basePath + '/crm/MemberCollection/addOrDelStoreCollection?sellerId='+id;
            }
            $http.get(url).success(function (re) {
              malert('删除成功');
                if(type=='goods'){
                    $scope.getCollectionList();
                }else{
                    $scope.getSellerList();
                }
            })
        }


        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的收藏');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $scope.goodsAsSeller='goods';
                $scope.titleText="collection";
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $rootScope.pageSize = 8;
                $scope.collectionList = [];
                $scope.sellerList = [];
                $scope.getCollectionList();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);