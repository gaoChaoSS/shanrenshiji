(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        var gridId = 'grid_main';
        $scope.$on(gridId + '/init_end', function (event, obj) {
            console.log('init tree:' + obj);
            window.reqUIAction($scope, gridId + '/query');
        });
    });
})(angular);
