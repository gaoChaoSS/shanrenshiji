/**
 * Created by hujoey on 16/12/26.
 */
(function (angular, undefined) {
    window.basePath = '/s_admin/api'
    window.app = angular.module('phonecat', []);
    window.app.config(function ($controllerProvider, $compileProvider, $filterProvider, $provide) {
        app.register = {
            controller: $controllerProvider.register,
            directive: $compileProvider.directive,
            filter: $filterProvider.register,
            factory: $provide.factory,
            service: $provide.service
        };
    });
    window.app.controller('allBodyCtrl', function ($rootScope, $scope, $http, $location) {
        $rootScope.clickAllBody = function () {
            $rootScope.$broadcast('/allMenuHide');

        }
        $scope.isEdit = false;
    });
})(angular);