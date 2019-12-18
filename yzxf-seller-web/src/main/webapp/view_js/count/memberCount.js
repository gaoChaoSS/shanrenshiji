(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/count/t_memberCount_grid.jsp';
        $scope.tempGridFilter = '/view/count/t_memberCount_filter.jsp';

        $scope.entityTitle = "会员统计";
        //$scope.windowInfo = '/view/user/t_memberInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getMemberAddCount";


        initGrid($rootScope, $scope, $http);
        $scope.getFactorName = function(name){
            if(window.isEmpty(name)){
                return "普惠生活-平台";
            }
            return name;
        }

        //导出excel文件
        $scope.createTradeExcel = function (){
            var url = "/view/download/memberCount_Download.jsp?1=1";
            $.each($scope.filter, function (k, v) {
                if (k == 'startTime' || k == 'endTime') {
                    return true;
                }
                if (window.isEmpty(v)) {
                    return true;
                }
                url += '&' + k + '=' + encodeURIComponent(v);
            });
            if (!isEmpty($scope.filter.$$startTime) || !isEmpty($scope.filter.$$endTime)) {
                $scope.timeType={_id:"setTime"};
                window.initFilterTime($rootScope, $scope);
                url += '&_createTime=___in_' + $scope.filter.startTime + '-' + $scope.filter.endTime;
            }else if(!isEmpty($scope.filter.startTime) && !isEmpty($scope.filter.endTime)){
                url += '&_createTime=___in_' + $scope.filter.startTime + '-' + $scope.filter.endTime;

            }
            window.location.href=url;
        };
    });
})(angular);
