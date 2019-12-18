(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.tempGridList = '/view/profit/t_parameterLog_grid.jsp';
        $scope.tempGridFilter = '/view/profit/t_parameterLog_filter.jsp';
        $scope.model = 'order';
        $scope.entity = 'Parameter';
        $scope.entityTitle = "修改配置日志";
        $scope.fullQueryApi = window.basePath + "/order/Parameter/getParameterLog";
        initGrid($rootScope, $scope, $http);

        // 获取类别
        $scope.getParameter = function () {
            var url = window.basePath + "/order/Parameter/getParameter?isType=1";
            $http.get(url).success(function (re) {
                $scope.typeList = re.content.items;
                $scope.typeList.unshift({type:'',typeTitle:'-----请选择-----'})
                $scope.filter['type'] = $scope.typeList[0].type;
            })
        }

        $scope.getParameter();
    });
})(angular);
