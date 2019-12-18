(function () {
    window.initGpayBindECP = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            if(window.isEmpty($scope.dataPage) || window.isEmpty($scope.dataPage.$$selectedItem)
                || window.isEmpty($scope.dataPage.$$selectedItem._id)){
                malert("请选择一个用户");
                return;
            }
            $scope.popWindowTemp = '/view/user/gpayBindECP.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '贵商绑定ECP';

            $scope.ecpItem = {
                sellerId:$scope.dataPage.$$selectedItem._id
            };
            $scope.assignnoList = [
                {title:'请选择'},
                {_id:'0',title:'比例'},
                {_id:'1',title:'每月固定'},
                {_id:'2',title:'每笔收费'},
            ]
            $scope.ecpItem.merchantsAbbreviation=$scope.dataPage.$$selectedItem.name;
            $scope.checkSubPage(0);
        };

        $scope.submitDataEcp = function(){
            var flag = false;
            if(isEmpty($scope.ecpItem.filename)){
                malert("请填写签约文件名");
            }else if(isEmpty($scope.ecpItem.assignno)){
                malert("请填写分润方式");
            }else if(isEmpty($scope.ecpItem.merchantsAbbreviation)){
                malert("请填写商家名称");
            }else if(isEmpty($scope.ecpItem.rate)){
                malert("请填写分润率");
            }else if($scope.ecpItem.assignno==='0' && ($scope.ecpItem.rate<0 || $scope.ecpItem.rate>1)){
                malert("请填写0~1之间的分润率");
            }else if(($scope.ecpItem.assignno==='1' || $scope.ecpItem.assignno==='2') && $scope.ecpItem.rate<1){
                malert("请填写大于1的分润率");
            }else {
                flag = true;
            }
            if(flag){
                $http.post(window.basePath+"/payment/Gpay/bindECP",$scope.ecpItem).success(function(){
                    malert("绑定成功!");
                    window.goBack();
                });
            }

        }

        // 切换子页面
        $scope.checkSubPage =function(index){
            $scope.subIndex=index;
        };

        $scope.unbindDataEcp = function(){
            if(confirm("是否解绑?")){
                $http.post(window.basePath+"/payment/Gpay/unbindECP",$scope.ecpItem).success(function(){
                    malert("解绑成功!");
                    window.goBack();
                });
            }
        }

        $scope.init();
    }
})();