(function (angular, undefined) {
    var model = 'common';
    var entity = 'DataQuery';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;


        $scope.selectModel = null, $scope.modelMap = {}, $scope.entityMap = {};
        var fixData = function (map, items) {
            $.each(items, function (k, v) {
                v.$$name = (!window.isEmpty(v.title) ? v.title : '未命名') + ' (' + v.name + ')';
                map[v._id] = v;
            });

            return items;
        }

        $scope.queryModel = function () {
            $http.get(window.basePath + '/common/MData/query?_pid=-1&pageSize=500').success(function (re) {
                $scope.modelList = fixData($scope.modelMap, re.content.items);
                $scope.selectModel = $scope.modelList.length > 0 ? $scope.modelList[0]._id : null;
                if (!window.isEmpty($scope.selectModel)) {
                    $scope.queryEntity($scope.selectModel);
                }
            });
        }
        $scope.queryEntity = function (pid) {
            $scope.selectModel = pid;
            $scope.selectEntity = null;
            $http.get(window.basePath + '/common/MData/query?_pid=' + pid + '&pageSize=500').success(function (re) {
                $scope.entityList = fixData($scope.entityMap, re.content.items);
                $scope.selectEntity = $scope.entityList.length > 0 ? $scope.entityList[0]._id : null;
                if (!window.isEmpty($scope.selectEntity)) {
                    $scope.changeEntity($scope.selectEntity);
                }
            });
        }
        $scope.changeEntity = function (entityId) {
            $scope.selectEntity = entityId;
            var modelName = $scope.modelMap[$scope.selectModel].name;
            var entityName = $scope.entityMap[$scope.selectEntity].name;
            window.reqUIAction($scope, 'grid_right/setData', {
                model: modelName,
                entity: entityName,
                actionPath: window.basePath + '/' + modelName + '/' + entityName
            }, function () {
                window.reqUIAction($scope, 'grid_right/genMDataAndQuery');
            });
        }

        $scope.$on('tree_left/itemClicked_after', function (event, obj) {
            window.reqUIAction($scope, 'grid_right/setDataAndQuery', {
                filter: {
                    _pid: obj._id
                },
                formData: {
                    pid: obj._id
                },
            });
        })

        $scope.queryModel();
    });
})(angular);
