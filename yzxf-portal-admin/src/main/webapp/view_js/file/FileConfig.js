(function (angular, undefined) {
    var model = 'file';
    var entity = 'FileConfig';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;


        $scope.reInitIndex = function () {
            var url = window.basePath + '/file/FileConfig/initIndexMap';
            $http.put(url, {}).success(function (re) {
                $scope.dirInfo = re.content;
                malert('操作成功!');
            });
        }

        $scope.getFileDirInfo = function () {
            var url = window.basePath + '/file/FileConfig/getFileDirInfo';
            $http.get(url).success(function (re) {
                $scope.dirInfo = re.content;
            });
        }
        $scope.getFileDirInfo();
    });
})(angular);
