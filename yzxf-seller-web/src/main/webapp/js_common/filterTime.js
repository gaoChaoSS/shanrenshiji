(function () {
    window.initFilterTime = function ($rootScope, $scope, type) {
        type || (type = 'filter');
        var n = new Date();
        n.setHours(0);
        n.setMilliseconds(0);
        n.setMinutes(0);
        n.setSeconds(0);

        $scope.setTimeByType = function (item) {
            $scope.filter.selectTimeBtnId = item._id;

            $scope[type].startTime || ($scope[type].startTime = n.getTime());
            $scope[type].endTime || ($scope[type].endTime = n.getTime());

            var t = new Date();
            t.setHours(0);
            t.setMilliseconds(0);
            t.setMinutes(0);
            t.setSeconds(0);

            if (item._id == 'today') {
                $scope[type].startTime = t.getTime();
                $scope[type].endTime = new Date(t.getTime()).addDays(1).addMilliseconds(-1).getTime();
            } else if (item._id == 'yesterday') {
                $scope[type].startTime = new Date(t.getTime()).addDays(-1).getTime();
                $scope[type].endTime = new Date(t.getTime()).addMilliseconds(-1).getTime();
            } else if (item._id == 'prev7day') {
                $scope[type].startTime = new Date(t.getTime()).addDays(-6).getTime();
                $scope[type].endTime = new Date(t.getTime()).addDays(1).addMilliseconds(-1).getTime();
            } else if (item._id == 'month') {
                var s = new Date(t.getTime());
                s.setDate(1);
                $scope[type].startTime = s.getTime();
                $scope[type].endTime = s.addMonths(1).addMilliseconds(-1).getTime();
            } else if (item._id == 'prevMonth') {
                var s = new Date(t.getTime()).addMonths(-1);
                s.setDate(1);
                var e = new Date(s.getTime()).addMonths(1).addMilliseconds(-1);
                $scope[type].startTime = s.getTime();
                $scope[type].endTime = e.getTime();
            }
            else if(item._id == 'setTime'){
                if(window.isEmpty($scope[type].$$startTime)){
                    $scope[type].startTime=new Date(new Date().setTime(1451577600000)).getTime();
                }else{
                    $scope[type].startTime = new Date($scope[type].$$startTime).getTime();
                }
                if(window.isEmpty($scope[type].$$endTime)){
                    $scope[type].endTime=new Date(new Date().setTime(4070880000000)).getTime();
                }else{
                    $scope[type].endTime = new Date($scope[type].$$endTime.setHours(23,59,59,999)).getTime();
                }
            }

            $scope[type].$$startTime = new Date($scope[type].startTime);
            $scope[type].$$endTime = new Date($scope[type].endTime);
            //$scope.queryCurrentList();
        }

        $scope[type] || ($scope[type] = {});
        if(!window.isEmpty($scope.timeType)){
            $scope.setTimeByType($scope.timeType);
        }
    };
})();