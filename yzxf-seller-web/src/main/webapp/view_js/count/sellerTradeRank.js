(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/count/t_sellerTradeRank_grid.jsp';
        $scope.tempGridFilter = '/view/count/t_sellerTradeRank_filter.jsp';

        $scope.entityTitle = "商家交易排行";
        //$scope.windowInfo = '/view/user/t_memberInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getSellerTradeRank";

        $scope.setUserType=function(){
            $scope.filter._userType="Seller";
        }
        initGrid($rootScope, $scope, $http);
    });
})(angular);
