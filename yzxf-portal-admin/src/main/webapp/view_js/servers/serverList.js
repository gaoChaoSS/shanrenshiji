(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.conType = 'list';
        $scope.showList = function () {
            $scope.conType = 'list';
        }
        $scope.showInfo = function (item) {
            $scope.conType = 'loading';
            console.log('item:', item);
            var url = window.basePath + '/' + item.name + '/ServerBaseInfo/info';
            $http.get(url).success(function (re) {
                re.content.type = re.content.serverConf.module_type;
                re.content.name = re.content.serverConf.module_name;
                re.content.host = re.content.serverConf.module_host;
                re.content.port = re.content.serverConf.module_port;
                re.content.$$title = re.content.name + ' (' + re.content.host + ':' + re.content.port + ') ';
                $scope.info = re.content;
                $scope.conType = 'info';
            });
        }


        $scope.getServerList = function () {
            var url = window.basePath + '/common/ServerInfo/serverAllList?pageSize=500';
            $http.get(url).success(function (re) {
                $scope.serverList = re.content.items;
                $scope.serverMap = {};
                $.each($scope.serverList, function (k, v) {
                    $scope.serverMap[v.type] || ( $scope.serverMap[v.type] = []);
                    $scope.serverMap[v.type].push(v);
                });
            });
        }


        $scope.getCommonServer = function () {
            var url = window.basePath + '/common/ServerBaseInfo/info';
            $http.get(url).success(function (re) {
                re.content.type = re.content.serverConf.module_type;
                re.content.name = re.content.serverConf.module_name;
                re.content.host = re.content.serverConf.module_host;
                re.content.port = re.content.serverConf.module_port;
                re.content.$$title = re.content.name + ' (' + re.content.host + ':' + re.content.port + ') ';
                $scope.commonServer = re.content;
            });
        }
        $scope.getServerList();
        $scope.getCommonServer();
        // $scope.serverList = [{online: true}, {}]
    });
})(angular);
