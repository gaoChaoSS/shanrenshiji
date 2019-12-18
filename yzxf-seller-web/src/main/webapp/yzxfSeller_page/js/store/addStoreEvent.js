(function (angular, undefined) {

    var model = 'store';
    var entity = 'addStoreEvent';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //刷新页面
        $scope.refresh = function () {
            window.location.reload();
        }
        $scope.startDateTime = new Date().setHours(0, 0, 0, 0);
        $scope.endDateTime = new Date().getTime();


        $scope.getStartAndEndDate = function () {
            $scope.curDate = new Date().setHours(0, 0, 0, 0);
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

            if ($scope.startDateTime >= $scope.endDateTime) {
                $scope.startDateTime = $scope.endDateTime - (1000 * 60 * 60 * 24 - 1);
                malert("开始时间不能大于结束时间");
            }

            if ($scope.endDateTime < $scope.curDate) {
                malert("结束时间不能小于当前时间");
                $scope.endDateTime = $scope.curDate;
            }

            if ($scope.startDateTime == $scope.endDateTime) {
                $scope.endDateTime = $scope.startDateTime + (1000 * 60 * 60 * 24 - 1);
            }
            //if($scope.item!=null){
            //    $scope.name=$scope.event.name;
            //    $scope.content=$scope.event.content;
            //    $scope.startTime=$scope.event.startTime;
            //    $scope.endTime=$scope.event.endTime;
            //    $scope.explain=$scope.event.explain;
            //}
            $rootScope.selectedDateEvent = null;
        }

        //添加活动
        $scope.submitBtn = function () {
            if (window.isEmpty($scope.name)) {
                malert('请填写活动名字');
                return false;
            }
            if (window.isEmpty($scope.content)) {
                malert('请填写优惠规则');
                return false;
            }
            if (window.isEmpty($scope.explain)) {
                malert('请填写特殊说明');
                return false;
            }
            if (window.isEmpty($scope.startDateTime) || window.isEmpty($scope.endDateTime)) {
                malert('请设置活动时间');
                return false;
            }
            if($scope.isUpdate){
                var url = window.basePath + '/account/Seller/addStoreEvent?_id='+$rootScope.pathParams.eventId;
            }else{
                var url = window.basePath + '/account/Seller/addStoreEvent';
            }
            var data = {
                name: $scope.name,
                content: $scope.content,
                startTime: $scope.startDateTime,
                endTime: $scope.endDateTime,
                explain: $scope.explain
            }
            $http.post(url, data).success(function () {
                malert("添加成功");
                $rootScope.goBack();
            })
        }

        //获取需要修改的活动详情
        $scope.getEvent = function (id, isGoing) {
            var url = window.basePath + '/account/Seller/getStoreEventById?eventId=' + $rootScope.pathParams.eventId;
            $http.get(url).success(function (re) {
                $scope.event = re.content;
                //alert($scope.event.name);
                $scope.name = $scope.event.name;
                $scope.content = $scope.event.content;
                $scope.startDateTime = $scope.event.startTime;
                $scope.endDateTime = $scope.event.endTime;
                $scope.explain = $scope.event.explain;

            })
        }

        $scope.selectDateAction = function (dateTime, pName) {
            $rootScope.selectedDateEvent = true;
            $rootScope.goPage('/pop/dateSelect/type/' + pName + '/value/' + dateTime);
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.eventId = $rootScope.pathParams.eventId;
                if ($scope.eventId != null || $scope.eventId != undefined) {
                    $scope.isUpdate = true;
                    window.setWindowTitle($rootScope, '修改会员活动');
                    $scope.titleEvent="修改活动";
                    $scope.getEvent();
                }else{
                    $scope.isUpdate = false;
                    window.setWindowTitle($rootScope, '新增会员活动');
                    $scope.titleEvent="添加活动";
                }
                $scope.getStartAndEndDate();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

