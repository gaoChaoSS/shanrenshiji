(function (angular, undefined) {
    var model = 'bugtracking';
    var entity = 'TestCase';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;

        var pid = null;

        document.title = '测试用例';


        $scope.getTypeOptions = function (item) {
            //product：产品，project:项目，model模块，function:功能点，checkPoint检查点
            var a = [];
            if (item == null || item.level == null) {
                a.push({_id: "product", name: "产品"});
                return a;
            } else {

            }
        }


        $scope.saveData = function (item) {
            var url = window.basePath + '/' + model + '/' + entity + '/save';
            var data = {_id: item._id, pid: item.pid, name: item.name, desc: item.desc};
            if (data.name == null || data.name == '') {
                malert('名称必须填写!');
                return;
            }
            $http.put(url, data).success(function (re) {
                malert('保存成功!');
                item.$$add = false;
            })
        }
        $scope.delData = function (item, $index) {
            if (confirm('删除该条数据,将不能恢复!')) {
                if (item.$$add) {
                    $scope.selectItem.children.splice($index, 1);
                    return;
                }

                var url = window.basePath + '/' + model + '/' + entity + '/del';
                var data = {_id: item._id};
                $http.post(url, data).success(function () {
                    malert('删除成功!');
                    $scope.selectItem.children.splice($index, 1);
                })
            }
        }
        $scope.keyAction = function (item, $event) {
            if ($event.keyCode == 13) {
                $scope.saveData(item);
                item.$$edit = false;
                $scope.addItem();
            }
        }
        $scope.addItem = function () {
            var plevel = $scope.selectItem.level;
            plevel = plevel != null ? plevel : 0;
            $scope.selectItem.children.push({_id: genUUID(), pid: pid, $$edit: true, level: plevel + 1, $$add: true});
        }

        $scope.typeName = {'0': '产品', '1': '项目', '2': '模块', '3': '功能', '4': '检查点'};

        $scope.$on('tree_left/itemClicked_after', function (event, obj) {
            pid = obj._id;
            console.log('obj==', obj);
            $scope.selectItem = obj;
            if (!$scope.selectItem.$$isLoad) {
                $http.get(window.basePath + '/bugtracking/TestCase/query?pageSize=500&_pid=' + obj._id).success(function (re) {
                    $scope.selectItem.$$isLoad = !$scope.selectItem.$$isLoad;
                    re.content.items || (re.content.items = []);
                    $scope.selectItem.children = re.content.items;
                });
            }
        });

        $scope.$on('tree_left/init_end', function (event, obj) {
            window.reqUIAction($scope, 'tree_left/itemExpended');
            window.reqUIAction($scope, 'tree_left/itemClicked');
        });

        //$scope.$on('grid_right/init_end', function (event, obj) {
        //    initGrid = true;
        //    if (pid != null) {
        //        gridQuery();
        //    }
        //});

    });
})(angular);
