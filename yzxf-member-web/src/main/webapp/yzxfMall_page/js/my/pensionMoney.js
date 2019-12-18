(function (angular, undefined) {

    var model = 'my';
    var entity = 'pensionMoney';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';

        $scope.getMyPension = function () {
            var url = window.basePath + '/order/OrderInfo/getPensionCount';
            $http.get(url).success(function (re) {
                $scope.pensionMoney = re.content;
            })
        }
        $scope.clearFun=function(){
            $rootScope.indexNum = 0;
            $rootScope.pageNo = 1;
            $rootScope.pageSize = 10;
        }
        //$scope.pageNumber = function (num) {
        //    if (num < 1 || $scope.totalPage < num) {
        //        return;
        //    }
        //    $rootScope.pageNo = num;
        //    $rootScope.indexNum = $rootScope.pageNo-1;
        //    $scope.getOrderList();
        //}
        //$scope.pageNext = function (num) {
        //    $rootScope.pageNo += num;
        //    $rootScope.indexNum = $rootScope.pageNo-1;
        //    $scope.getOrderList();
        //}
        //$scope.pageGoFun = function (num) {
        //    $rootScope.pageNo = num;
        //    $rootScope.indexNum = $rootScope.pageNo-1;
        //    if(num>$rootScope.dataPage.totalPage){
        //        malert('跳转页面超过上限!');
        //        return;
        //    }
        //    $scope.getOrderList();
        //}
        //$scope.setPageNo = function (num){
        //    $rootScope.pageNo=num;
        //    $scope.getOrderList();
        //}
        $scope.getMoney=function(money){
            return (parseInt($rootScope.isNullZero(money)*100))/100;
        }

        //投保判断
        $scope.isInsureFun = function (isInsure, insureCount, insureCountUse) {

            if (isInsure) {
                if (window.isEmpty(insureCountUse) || insureCountUse == 'null') {
                    insureCountUse = 0;
                }
                return '已投保: ' + $rootScope.getMoney(insureCountUse) + " 元";
            }
            if (window.isEmpty(insureCount) || insureCount == 'null') {
                insureCount = 0;
            }
            return '未投保: ' + $rootScope.getMoney(insureCount) + " 元";
        }

        $scope.queryCurrentList = function () {
            $scope.orderList = [];
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

            $scope.startTime = new Date($scope.startDateTime).setHours(0, 0, 0, 0);
            $scope.endTime = new Date($scope.endDateTime).setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);

            if ($scope.startTime >= $scope.endTime) {
                $scope.startTime = $scope.endTime - (1000 * 60 * 60 * 24 - 1);
                $scope.startDateTime = new Date().setDateByStr(new Date().getFullYear() + "-"+(new Date().getMonth()+1)+"-1");
                $scope.endDateTime = new Date(new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1));
                malert("开始时间不能大于结束时间");
            }

            var url = window.basePath;
            if ($scope.o2o == '0') {
                url += '/crm/Member/getMyPensionMoney?startTime=' + $scope.startTime + '&endTime=' + $scope.endTime + '&pageSize=' + $rootScope.pageSize + '&pageNo=' + $rootScope.pageNo + "&indexNum=" + $rootScope.indexNum;
            } else if ($scope.o2o == '1') {
                url += '/crm/Member/getMyPensionMoneyByOffline?startTime=' + $scope.startTime + '&endTime=' + $scope.endTime + '&pageSize=' + $rootScope.pageSize + '&pageNo=' + $rootScope.pageNo + "&indexNum=" + $rootScope.indexNum;
            }

            $http.get(url).success(function (re) {
                //$.each(re.content.orderList, function (k, v) {
                //    $scope.orderList.push(v);
                //})
                $scope.orderList=re.content.orderList;
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



        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的养老金');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $scope.titleText="pensionMoney";
                $scope.o2o='0';
                //默认显示今年的记录
                $scope.startDateTime = new Date().setDateByStr(new Date().getFullYear() + "-"+(new Date().getMonth()+1)+"-1");
                $scope.endDateTime = new Date(new Date().setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1));
                $rootScope.pageSize = 8;
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $scope.orderList = [];
                //默认显示今年的记录
                $scope.getMyPension();
                $scope.queryCurrentList();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);