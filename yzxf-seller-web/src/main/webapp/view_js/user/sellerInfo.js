(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $rootScope.getFixed($scope.model, $scope.entity);

        $scope.modifyCheck=false;
        $scope.sellerId=$location.search()['sellerId'];

        $scope.getSellerInfo=function(){
            if(!window.isEmpty($scope.sellerId)){
                var url = window.basePath + '/account/Seller/getSellerInfoById?sellerId='+$scope.sellerId;
                $http.get(url).success(function (re) {
                    $scope.seller=re.content;
                    if($scope.seller==null){
                        malert("找不到该商家");
                    }
                });
            }else{
                malert("找不到商家");
            }
        }

        $scope.iconImgUrl = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
        }

        $scope.goBack=function(){
            history.back();
        }

        //提交审核数据
        $scope.submitModify=function(){
            if(window.isEmpty($scope.sellerId)){
                malert("找不到该用户");
                return;
            }

            var url = window.basePath + '/account/Seller/updateSellerCanUse';
            var date = {
                idStr:$scope.sellerId,
                canUseStr:!$scope.seller.canUse
            };
            $http.post(url, date).success(function () {
                malert("保存成功!");
                $scope.modifyCheck=false;
                $scope.getSellerInfo();
            });
        }

        $scope.getSellerInfo();
    });
})(angular);