(function(angular, undefined) {
    var model = 'account';
    var entity = 'Department';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;

        // tree
        $rootScope.demo = {};
        var vm = $rootScope.demo;
        vm.tree = [ {
            "_id" : "-1",
            "pid" : "",
            "name" : "所有部门"
        } ];

        var initTree = false, initGrid = false;
        var pid = null;

        var gridQuery = function() {// 查询grid
            window.reqUIAction($scope, 'grid_right/setDataAndQuery', {
                filter : {
                    _pid : pid
                },
                baseFormData :{
                    pid : pid
                }
            });
        }
        $scope.$on('tree_left/itemClicked_after', function(event, obj) {
            pid = obj._id;
            if (initGrid) {
                gridQuery();
            }
        })

        $scope.$on('tree_left/init_end', function(event, obj) {
            window.reqUIAction($scope, 'tree_left/itemExpended');
            window.reqUIAction($scope, 'tree_left/itemClicked');

        });
        $scope.$on('grid_right/init_end', function(event, obj) {
            initGrid = true;
            if (pid != null) {
                gridQuery();
            }
        });

        return vm;
    });
})(angular);
