(function(angular, undefined) {
    var model = 'common';
    var entity = 'MDataCompare';
    var _fixList = function(list, map) {

        var pid = "-1";
       
        $.each(list, function(k, v) {
            if (v.pid == pid) {
                map[v._id] = v;
            }
        });

    }
    window.app.register.controller(model + '_' + entity + '_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;
        var url = window.basePath + '/common/MData/mdataCompare';
        $http.get(url).success(function(re) {
            $scope.backUrl = re.content.backUrl;
            $scope.backTime = new Date(re.content.backTime).showYFullTime();
            $scope.backMap = {};
            $scope.myMap = {};
            _fixList(re.content.backList, $scope.backMap);
            _fixList(re.content.myList, $scope.myMap);
        });

    });
})(angular);
