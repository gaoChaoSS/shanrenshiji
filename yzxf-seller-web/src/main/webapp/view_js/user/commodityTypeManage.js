(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {


        $scope.tempGridList = '/view/user/t_commodityType_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_commodityType_grid_filter.jsp';
        $scope.entityTitle = "商品类型管理";
        $scope.fullQueryApi = window.basePath + "/account/Seller/getCommodityTypeList";
        $scope.windowInfo = '/view/user/t_commodityInfo_grid.jsp';
        $scope.showImg='';
        $scope.adminCheck=false;
        initGrid($rootScope, $scope, $http);

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }


        $scope.closeImgFun=function(){
            $scope.showImg='';
        }

        $scope.showOtherData = function () {
            var url = window.basePath + '/order/ProductInfo/goodsInfoShow?_id='+$scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function(re){
                $scope.goodsInfo = re.content;
            })
        }



        $scope.changeCommodityType = function(id,status,type){
            var url = window.basePath + '/order/OrderInfo/changeCommodityType?commodityId='+id+'&commodityType='+type+'&status='+status;
            $http.get(url).success(function(re){
                malert('修改成功!');
                $scope.queryCurrentList();
            })
        }

    });
})(angular);
