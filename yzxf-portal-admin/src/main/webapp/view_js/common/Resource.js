(function (angular, undefined) {
    var model = 'common';
    var entity = 'Resource';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;

        // tree
        $rootScope.resource_main = {};
        var vm = $rootScope.resource_main;
        vm.tree = [{
            "_id": "-1",
            "pid": "",
            "name": "all",
            "title": "所有资源"
        }];

        vm.itemIcon = function ($item) {
            if ($item.type == 'api') {
                return 'icon-file-text';
            } else {
                return $item.$$isExpend ? 'icon-folder-open' : 'icon-folder';
            }

        }

        var initTree = false, initGrid = false;
        var pid = null, filter = {};

        var gridQuery = function () {// 查询grid
            filter._pid = pid;
            window.reqUIAction($scope, 'grid_right/setDataAndQuery', {
                filter: filter,
                formData: {
                    pid: pid
                },
            });
        }
        $scope.$on('tree_left/itemClicked_after', function (event, obj) {
            pid = obj._id;
            console.log('obj.level', obj.level);
            if (obj.level == -1) {
                filter = {
                    includeKeys: 'type,name,title,desc,sellerOwner,iconClass,sortNo',
                    excludeKeys: null
                };
            } else if (obj.level == 0) {
                filter = {
                    includeKeys: 'type,name,title,sellerOwner,checkStoreId',
                    excludeKeys: null
                }
            } else {
                filter = {
                    includeKeys: null,
                    excludeKeys: '_id,pid,sellerPath,level'
                }
            }
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
        return vm;
    });
})(angular);
