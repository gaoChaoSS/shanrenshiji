(function(angular, undefined) {
    // var path = window.location.href.split('#')[1];
    var model = 'common';
    var entity = 'MData';
    window.app.register.controller(model + '_MDataField_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        var gridId = 'grid_main';
        $scope.$on(gridId + '/init_end', function(event, obj) {
            $http.get(window.basePath + '/' + model + '/' + entity + '/queryMDataObj').success(function(re) {
                var pid = re.content.pid;
                window.reqUIAction($scope, 'grid_right/setDataAndQuery', {
                    filter : {
                        _entityName : 'MData'
                    },
                    baseFormData :{
                        pid : pid
                    }
                });
            });
        });
    });
})(angular);
