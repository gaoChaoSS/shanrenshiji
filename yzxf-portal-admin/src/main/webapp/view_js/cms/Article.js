(function (angular, undefined) {
    var model = 'cms';
    var entity = 'Article';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;

        var initTree = false, initGrid = false;
        var pid = null;

        var gridQuery = function () {// 查询grid
            if (pid != null && pid != -1) {
                var data = {
                    filter: {
                        _articleDirList: pid
                    },
                    baseFormData: {
                        articleDirList: [{
                            _id: pid
                        }]
                    }
                };
                window.reqUIAction($scope, 'grid_right/setDataAndQuery', data);
            } else {
                window.reqUIAction($scope, 'grid_right/resetFilter');
                window.reqUIAction($scope, 'grid_right/query');
            }

        }
        $scope.$on('tree_left/itemClicked_after', function (event, obj) {
            pid = obj._id;
            if (initGrid) {
                gridQuery();
            }
        })

        $scope.$on('tree_left/init_end', function (event, obj) {
            window.reqUIAction($scope, 'tree_left/itemExpended');
            window.reqUIAction($scope, 'tree_left/itemClicked');
        });
        $scope.$on('grid_right/init_end', function (event, obj) {
            initGrid = true;
            if (pid != null) {
                gridQuery();
            }
        });
    });
})(angular);
