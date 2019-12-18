(function (angular, undefined) {

    var model = 'store';
    var entity = 'preferencePay';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.EventStatus = true;
        $scope.curDate = new Date().getTime();

        //获取活动
        $scope.getEventList = function(){
            var url = window.basePath + '/account/Seller/getStoreEvent?isGoing=true';
            $http.get(url).success(function(re){
                $scope.eventList = re.content.items;
            })
        }


        $scope.colorCheck=function(){
                return 'triangleEventBig borderTopRed';
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '优惠买单');
                $scope.getEventList();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);