(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {


        $scope.tempGridList = '/view/user/t_sellerRecommendManage_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_sellerRecommendManage_grid_filter.jsp';
        $scope.entityTitle = "商户权限管理";
        $scope.fullQueryApi = window.basePath + "/account/Seller/getSellerRecommendList";

        $scope.adminCheck=false;
        initGrid($rootScope, $scope, $http);
        $scope.changeCommodityType = function(id,status,type){
            var url = window.basePath + '/account/Seller/changeSellerRecommend?sellerId='+id+'&recommendTypeType='+type+'&status='+status;
            $http.get(url).success(function(re){
                malert('修改成功!');
                $scope.queryCurrentList();
            })
        }

    });
})(angular);
