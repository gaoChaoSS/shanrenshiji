(function (angular, undefined) {
    var model = 'account';
    var entity = 'SellerResource';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;

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
            filter = {
                includeKeys: 'name,title,type,path,iconClass,desc,sortNo,level,pid,_id'
            };
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
            window.reqUIAction($scope, 'grid_right/addButtomAction', {
                name: '导出数据到文件',
                click: function () {
                    var url = window.basePath + '/' + model + '/' + entity + '/export2File';
                    $http.put(url, {
                        dataFrom: window.location.host
                    }).success(function () {
                        alert('导出成功，请提交根路径下的MData.json文件到svn用于同步')
                    });
                }
            });
            // window.reqUIAction($scope, 'grid_right/addButtomAction', {
            // name : '文件导入到数据库',
            // click : function() {
            // var url = window.basePath + '/' + model + '/' + entity + '/importByFile';
            // $http.put(url).success(function() {
            // alert('成功导入')
            // });
            // }
            // });

            if (pid != null) {
                gridQuery();
            }
        });
    });
})(angular);
