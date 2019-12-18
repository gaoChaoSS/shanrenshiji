(function (angular, undefined) {

    var model = 'my';
    var entity = 'my';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';
        $scope.getLoginName = function () {
            if (!window.isEmpty($rootScope.myInfo.name)) {
                return $rootScope.myInfo.name;
            }
            if (!window.isEmpty($rootScope.myInfo.mobile)) {
                return $rootScope.myInfo.mobile;
            }
            if (!window.isEmpty($rootScope.myInfo.cardNo)) {
                return $rootScope.myInfo.cardNo;
            }
            if (!window.isEmpty($rootScope.myInfo.idCard)) {
                return $rootScope.myInfo.idCard;
            }
        }
        $scope.getWallet = function(){
            var url = window.basePath + '/crm/Member/getWallet';
            $http.get(url).success(function(re){
                $scope.wallet = re.content.items[0].cashCount;
            })
        }
        $scope.getMyPension = function () {
            var url = window.basePath + '/order/OrderInfo/getPensionCount';
            $http.get(url).success(function (re) {
                $scope.pensionMoney = re.content.pensionCount;
            })
        }
        //$scope.getMemberInfo = function(){
        //    url = window.basePath + '/crm/Member/getMyInfo';
        //    $http.get(url).success(function (re) {
        //        //$rootScope.myInfo = re.content;
        //        $scope.loginName = $scope.getLoginName();
        //        $scope.loginName=$scope.loginName.substr(0,3)+"****"+$scope.loginName.substr(7);
        //        if(re.content.isRealName==null || re.content.isRealName==false){
        //            $scope.memberIsRealName=false;
        //        }else{
        //            $scope.memberIsRealName=true;
        //        }
        //        if (!window.isEmpty($rootScope.myInfo.idCard)) {
        //            if (($rootScope.myInfo.idCard).length == 15) {
        //                $rootScope.myInfo.idCard = $rootScope.myInfo.idCard.substr(0, 3) + "*********" + $rootScope.myInfo.idCard.substr(11);
        //            } else if (($rootScope.memberInfo.idCard).length == 18) {
        //                $rootScope.myInfo.idCard = $rootScope.myInfo.idCard.substr(0, 3) + "***********" + $rootScope.myInfo.idCard.substr(14);
        //            }
        //        }
        //        if (!window.isEmpty($rootScope.myInfo.mobile)) {
        //            $rootScope.myInfo.mobile = $rootScope.myInfo.mobile.substr(0, 3) + "****" + $rootScope.myInfo.mobile.substr(7);
        //        }
        //    });
        //}
        //是否实名认证
        $scope.isRealName = function(){
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + '/crm/Member/getMemberIsRealName?memberId='+$scope.memberId;
            $http.get(url).success(function(re){
                if(re.content.items[0].isRealName==true){
                    $rootScope.goPage('/my/orderPay');
                }else{
                    malert('请先实名认证');
                    $rootScope.goPage('/my/realName');
                }
            })
        };
        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的账户');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $scope.titleText="my";
                $scope.getMyPension();
                $scope.getWallet();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);