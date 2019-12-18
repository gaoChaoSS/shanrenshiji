(function(angular, undefined) {
    var model = "my";
    var entity = "wallet";
    window.app.register.controller('my_wallet_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope.isIndex =false;


        $scope.getWallet = function(){
            var url = window.basePath + '/crm/Member/getWallet';
            $http.get(url).success(function(re){
                $scope.wallet = re.content.items[0];
            })
        }

        //检查余额
        $scope.checkAccount = function () {
            if($scope.isPayPwd){
                $rootScope.goPage('my/paySetPassword');
                return;
            }
            var url = window.basePath + '/order/OrderInfo/checkUserAccount?userType=Member&checkUpper=true';
            $http.get(url).success(function (re) {
                if(re.content.status=='SUCCESS'){
                    $rootScope.goPage('/my/deposit');
                }else{
                    malert("您的积分已达到5000分上限");
                }
            })
        };

        $scope.isMemberPayPwd = function(goPageUrl){
            if($scope.isPayPwd){
                $rootScope.goPage('my/paySetPassword');
            }else{
                $rootScope.goPage(goPageUrl);
            }
        }

        $scope.getIsPayPwd=function(){
            var url = window.basePath + '/crm/Member/isMemberPayPwd';
            $http.get(url).success(function(re){
                $scope.isPayPwd=re.content.flag;
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '消费积分');
                $scope.getWallet();
                $scope.getIsPayPwd();
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });


})(angular);