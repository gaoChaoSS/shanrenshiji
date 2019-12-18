(function (angular, undefined) {

    var model = 'store';
    var entity = 'addStoreCoupon';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //默认显示今年的记录
        $scope.startDateTime = new Date().getTime();
        $scope.startDateTime = new Date($scope.startDateTime).setHours(0, 0, 0, 0);
        $scope.endDateTime = new Date().getTime();
        $scope.curDate = new Date().getTime();

        //刷新页面
        $scope.refresh = function () {
            window.location.reload();
        }

        $scope.getStartAndEndDate = function () {

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
            $scope.endDateTime = new Date($scope.endDateTime).setHours(0, 0, 0, 0);

            if ($scope.startDateTime < new Date($scope.curDate).setHours(0, 0, 0, 0)) {
                malert("开始时间不能小于当前时间");
                $scope.startDateTime = $scope.curDate;
            }

            if ($scope.startDateTime > $scope.endDateTime) {
                malert("开始时间不能大于结束时间");
                $scope.endDateTime = $scope.startDateTime;
            }
            if ($scope.startDateTime == $scope.endDateTime) {
                $scope.endDateTime = $scope.startDateTime + (1000 * 60 * 60 * 24 - 1);
            }

            $rootScope.selectedDateEvent = null;
        }

        //添加活动
        $scope.submitBtn = function () {
            if (window.isEmpty($scope.name)) {
                malert('请填写活动名字');
                return false;
            }
            if (!/^[0-9]*(\.[0-9]{1,2})?$/.test($scope.condition) || !/^[0-9]*(\.[0-9]{1,2})?$/.test($scope.value)) {
                malert('输入金额不正确');
                return false;
            }
            if (window.isEmpty($scope.startDateTime) || window.isEmpty($scope.endDateTime)) {
                malert('请设置活动时间');
                return false;
            }
            var url = window.basePath + '/crm/Coupon/addStoreCoupon';
            var data = {
                name: $scope.name,
                condition: $scope.condition,
                value: $scope.value,
                startTime: $scope.startDateTime,
                endTime: $scope.endDateTime
            }
            $http.post(url, data).success(function () {
                malert("发布成功");
                $rootScope.goBack();
            })
        }
        //$scope.check = 'disabled';
        //$scope.nextBtn = function () {
        //    //$scope.check = 'disabled';
        //    if (window.isEmpty($scope.name)) {
        //        malert('请填写活动名字');
        //        $scope.check = false;
        //        return false;
        //    }
        //
        //    if (/^[0-9]*$/.test($scope.condition) && !window.isEmpty($scope.condition)) {
        //        malert('填写格式不正确');
        //        $scope.check = false;
        //    }
        //
        //}


        $scope.selectDateAction = function (dateTime, pName) {
            $rootScope.selectedDateEvent = true;
            $rootScope.goPage('/pop/dateSelect/type/' + pName + '/value/' + dateTime);
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '添加卡券');
                $scope.getStartAndEndDate();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

