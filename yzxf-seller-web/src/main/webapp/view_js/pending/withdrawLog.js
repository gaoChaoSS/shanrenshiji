(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'order';
        $scope.entity = 'OrderInfo';
        $scope.entityTitle = "提现记录";
        $scope.tempGridList = '/view/pending/t_withdrawPend_grid.jsp';
        $scope.windowInfo = '/view/pending/t_withdrawPendInfo_grid.jsp';
        $scope.tempGridFilter = '/view/pending/t_withdrawPend_grid_filter.jsp';
        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getWithdrawPend";

        $scope.setUserType=function(){
            $scope.filter._status="2";
        }

        initGrid($rootScope, $scope, $http);

        $scope.showPendingData = function () {
            $scope.checkBtn=[false,false];
            var url = window.basePath + "/account/"+$scope.dataPage.$$selectedItem.userType+"/show?_id=" + $scope.dataPage.$$selectedItem.userId;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content;
            });
        }

        $scope.getUserType=function(type){
            if(type=="Seller"){
                return "商家";
            }else if(type=="Factor"){
                return "服务站";
            }else{
                return "";
            }
        }
        $scope.getStatus=function(status){
            if(status==0){
                return '待处理';
            }else if(status==1){
                return '提现中';
            }else if(status==2){
                return '已提现';
            }else if(status==3){
                return '失败';
            }
        }
    });
})(angular);
