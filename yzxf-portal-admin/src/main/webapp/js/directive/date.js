/**
 * 使用例子：(须注意先后顺序)
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";

    window.showDateStr = function (inputType, d) {
        if (!window.isEmpty(d)) {
            if (inputType == 'dateTime') {
                return new Date(parseInt(d)).showDateTime();
            } else if (inputType == 'date') {
                return new Date(parseInt(d)).showDate();
            } else if (inputType == 'time') {
                return new Date(parseInt(d)).showTime();
            }
        } else {
            return '';
        }
    }

    window.app.directive('date', function () {
        return {
            restrict: 'AE',
            transclude: true,
            replace: true,
            scope: {
                id: '@',
                value: '@',
                type: '@',
                name: '@'
            },
            templateUrl: '/temp/date.html?_v=' + window.angular_temp_version,
            controller: function ($rootScope, $scope, $element, $http, $q, $compile, $popWindow, $templateCache) {
                if ($scope.id == null) {
                    $scope.id = 'date_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }

                $rootScope.allMenuHide = false;
                $scope.type || ($scope.type = 'date');
                //$scope.value || ($scope.value = new Date().showDate());

                $scope.weekTitleNames = ['日', '一', '二', '三', '四', '五', '六'];
                $scope.monthTitleNames = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
                $scope.HoursList = [];
                for (var i = 0; i < 24; i++) {
                    $scope.HoursList.push(greStrInt(i, 2));
                }
                $scope.MinutesList = [];
                for (var i = 0; i <= 60; i++) {
                    new Date().getMinutes()
                    $scope.MinutesList.push(greStrInt(i, 2));
                }

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
                $scope.selectValue = function (item) {
                    $rootScope.$broadcast('/setDateValue', {id: $scope.id, name: $scope.name, value: item.value});
                }
                $scope.selectTimeValue = function () {
                    if (window.isEmpty($scope.currentHours)) {
                        malert('必须选择小时');
                        $scope.currentHoursError = true;
                        return;
                    }
                    if (window.isEmpty($scope.currentMinutes)) {
                        malert('必须选择分钟');
                        $scope.currentMinutesError = true;
                        return;
                    }
                    $scope.valueDate.setHours(parseInt($scope.currentHours));
                    $scope.valueDate.setMinutes(parseInt($scope.currentMinutes));
                    $rootScope.$broadcast('/setDateValue', {
                        id: $scope.id,
                        name: $scope.name,
                        value: $scope.valueDate.getTime()
                    });
                }
                $scope.clearValue = function () {
                    $rootScope.$broadcast('/setDateValue', {id: $scope.id, name: $scope.name, value: ''});
                }
                $scope.setDateValue = function (value) {
                    $scope.valueDate = new Date(value);
                    $scope.years = [];
                    $scope.currentYear = $scope.valueDate.getFullYear();
                    $scope.currentMonth = $scope.valueDate.getMonth();
                    $scope.currentHours = greStrInt($scope.valueDate.getHours(), 2);
                    $scope.currentMinutes = greStrInt($scope.valueDate.getMinutes(), 2);
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

                        //var minTime = $rootScope.pathParams.minTime
                        //var maxTime = $rootScope.pathParams.maxTime
                        //var hasMin = minTime != null && time < minTime;
                        //var hasMax = maxTime != null && time > maxTime;
                        //if (hasMin || hasMax) {
                        //    className = 'date_disabled';
                        //    className = 'date_disabled1';
                        //}

                        $scope.dateList.push({value: time, dayStr: firstDay.getDate(), className: className})
                        firstDay.setDate(firstDay.getDate() + 1);
                    }
                }
                $scope.currentDate = new Date();
                if (!window.isEmpty($scope.value)) {
                    if ($scope.type == 'date') {
                        $scope.currentDate = $scope.currentDate.setDateByStr($scope.value);
                    } else if ($scope.type == 'time') {
                        $scope.currentDate = $scope.currentDate.setTimeByStr($scope.value);
                    } else if ($scope.type == 'dateTime') {
                        $scope.currentDate = $scope.currentDate.setDateByStr($scope.value.split(' ')[0]);
                        $scope.currentDate = $scope.currentDate.setTimeByStr($scope.value.split(' ')[1]);
                    }
                }

                $scope.setDateValue($scope.currentDate.getTime());
                window.exportUiApi($scope, ['setOptions']);


                $rootScope.$broadcast($scope.id + '/init_end');
            },

            compile: function (element, attrs, transclude) {
                return function ($scope, element, attrs, ctrl, transclude) {
                };
            }
        }
    });

})(angular);