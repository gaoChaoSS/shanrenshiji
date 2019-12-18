(function (angular, undefined) {
    var model = 'store';
    var entity = 'aboutUs';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $(".logo1").click(function(){
            $(".logo1").animate({transform:"rotate(360deg)"},1000)
        })

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '关于我们');
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
