(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/card/t_cardCount_grid.jsp';
        $scope.tempGridFilter = '/view/card/t_cardCount_filter.jsp';

        $scope.entityTitle = "实体卡统计";
        //$scope.windowInfo = '/view/user/t_memberInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/account/Agent/agentQueryMemberCardNum";
        //$scope.getActiveNum = function(id) {
        //    var url = window.basePath + '/account/Agent/getAgentActiveNum?_id='+id;
        //    $http.get(url).success(function(re){
        //
        //    })
        //}
        //$scope.getMemberCardNum = function(totalNum){
        //    var num = 0;
        //    for(var i = 0; i<totalNum;i++){
        //        num+=$scope.dataPage.items[i].activeNum;
        //    }
        //    return num;
        //}
        $scope.agentLevelNum = function (num) {
            if (num == '2') {
                return '省级代理商';
            }
            if (num == '3') {
                return '市级代理商';
            }
            if (num == '4') {
                return '县级代理商';
            }
            if (num == '5') {
                return '服务中心';
            }

        }

        initGrid($rootScope, $scope, $http);


    });
})(angular);
