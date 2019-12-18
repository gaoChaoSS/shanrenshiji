(function (angular, undefined) {
    var model = 'pop';
    var entity = 'dateSelect';
    var entityUrl = '/' + model + '/' + entity;

    window.app.register.controller('pop_dateSelect_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {


        $scope.weekTitleNames = ['日', '一', '二', '三', '四', '五', '六'];
        $scope.monthTitleNames = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
        $scope.showDate = function () {
            var value = $rootScope.pathParams.value;
            value = value == null || value == '' || value == 'null' ? new Date().getTime() : parseInt(value);

            $scope.setDateValue(value);
        }
        $scope.changeShowType = function (type) {
            if ($scope.showType != null && $scope.showType == type) {
                $scope.showType = null;
                return;
            }
            $scope.showType = type;
        }
        $scope.changeDate = function (type, v) {
            if (type == 'year') {
                $scope.valueDate.setFullYear(v);
            }
            if (type == 'month') {
                $scope.valueDate.setMonth(v);
            }
            $scope.setDateValue($scope.valueDate);
        }
        $scope.selectDate = function (item) {
            if ($rootScope.pathParams.type == null) {
                malert('必须设置type');
                return;
            }
            $rootScope.selectDataBack = true;
            localStorage.setItem("select_pop_date_" + $rootScope.pathParams.type, item.value);
            $rootScope.goBack();
        }

        $scope.setDateValue = function (value) {
            $scope.valueDate = new Date(value);
            $scope.years = [];
            $scope.currentYear = $scope.valueDate.getFullYear();
            $scope.currentMonth = $scope.valueDate.getMonth();
            var startYear = $scope.valueDate.getFullYear() - 7;
            var endYear = $scope.valueDate.getFullYear() + 7;
            for (i = startYear; i < endYear; i++) {
                $scope.years.push(i);
            }

            var firstDay = new Date();
            firstDay.setFullYear($scope.valueDate.getFullYear());
            firstDay.setMonth($scope.valueDate.getMonth(), $scope.valueDate.getDate());
            firstDay.setDate(1);
            var week = firstDay.getDay();// 这个月的第一天是星期几
            firstDay.setDate((week * -1) + 1 + (week == 0 ? -7 : 0));

            var endDay = new Date();
            endDay.setFullYear($scope.valueDate.getFullYear());
            endDay.setMonth($scope.valueDate.getMonth(), $scope.valueDate.getDate());
            endDay.setDate(getMonthLastDay($scope.valueDate.getFullYear(), $scope.valueDate.getMonth() + 1) - 1);
            var startDateNum = week;
            startDateNum = week == 0 ? (startDateNum + 7) : startDateNum;
            var endDateNum = endDay.getDate() + startDateNum;


            $scope.dateList = [];
            for (var i = 0; i < 42; i++) {
                var className = '';
                if (i < startDateNum || i > endDateNum) {
                    className = 'noThisMonthTd';
                } else if ($scope.valueDate.getDate() == i - startDateNum + 1) {
                    className = 'highL';
                } else {

                }
                var time = firstDay.getTime();

                var minTime = $rootScope.pathParams.minTime
                var maxTime = $rootScope.pathParams.maxTime
                var hasMin = minTime != null && time < minTime;
                var hasMax = maxTime != null && time > maxTime;
                if (hasMin || hasMax) {
                    className = 'date_disabled';
                    className = 'date_disabled1';
                }

                $scope.dateList.push({value: time, dayStr: firstDay.getDate(), className: className})
                firstDay.setDate(firstDay.getDate() + 1);
            }
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                //window.setWindowTitle($rootScope, '选择日期');
                $scope.showDate();
            }
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);