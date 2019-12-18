(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/card/t_cardInfo_grid.jsp';
        $scope.tempGridFilter = '/view/card/t_cardInfo_filter.jsp';

        $scope.entityTitle = "实体卡信息";
        //$scope.windowInfo = '/view/user/t_memberInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/account/Agent/agentQueryMemberCardInfo";


        initGrid($rootScope, $scope, $http);

    });
})(angular);
