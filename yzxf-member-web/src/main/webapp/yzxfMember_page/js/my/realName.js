(function(angular, undefined) {
    var model = "my";
    var entity = "realName";
    window.app.register.controller('my_realName_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.isRealName = false;

        $scope.area="";
        $scope.areaValue="";
        $scope.pValue="";

        $scope.check = 'disabled';
        //提交按钮判断
        $scope.submitBtn=function(){
            $scope.check = 'disabled';
            if(!window.isEmpty($scope.realName) && !window.isEmpty($scope.address) && !window.isEmpty($scope.cardNumber)
                && !$scope.cardError){
                $scope.check = false;
            }
        }
        $scope.cardErrorFuc=function(){
            if(/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.cardNumber)){
                $scope.cardError=false;
            }else{
                $scope.cardError=true;
            }
        }

        $scope.emailErrorFuc=function(){
            if(/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.myEmail)){
                $scope.emailError=false;
            }else{
                $scope.emailError=true;
            }
        }

        $scope.getSex=function(sex){
            if(sex == 1){
                return '男';
            }else if(sex == 2){
                return '女';
            }else{
                return '未知';
            }
        }

        $scope.submitForm = function(){
            if(window.isEmpty($scope.areaValue) || window.isEmpty($scope.address) || window.isEmpty($scope.area)){
                malert("请填写完整地址!");
                return;
            }
            if($scope.area.length>200){
                malert("所在区域不能超过200位");
                return;
            }
            if($scope.address.length>200){
                malert("街道地址不能超过200位");
                return;
            }
            // if(!window.isEmpty($scope.myEmail)){
            //     if(!/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.myEmail)){
            //         malert("邮箱格式错误");
            //         return;
            //     }
            // }

            if(window.isEmpty($scope.pValue)){
                $scope.areaValue="_"+$scope.areaValue+"_";
            }else{
                $scope.areaValue=$scope.pValue+"_"+$scope.areaValue+"_";
            }
            var url = window.basePath + '/crm/Member/memberRealName';
            var data = {
                memberId : getCookie('_member_id'),
                realName : $scope.realName,
                idCard : $scope.cardNumber,
                // email : $scope.myEmail,
                realArea : $scope.area,
                realAreaValue : $scope.areaValue,
                realAddress : $scope.address,
                // sex:$scope.sex
            }
            $http.post(url, data).success(function (re) {
                if(!window.isEmpty($scope.isActive)){
                    $rootScope.goPage('/my/orderPay');
                }else{
                    if(re.length!=0){
                        $scope.getMemberIsRealName();
                        $scope.getMyInfo();
                    }
                    $rootScope["$$realNameArea"]=null;
                }
            })
        }
        $scope.getMyInfo = function () {
            var url = window.basePath + '/crm/Member/getMyInfo';
            $http.get(url).success(function (re) {
                $scope.memberInfo = re.content;
            })
        }
        $scope.getMemberIsRealName = function(){
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + '/crm/Member/getMemberIsRealName?memberId='+$scope.memberId;
            $http.get(url).success(function(re){
                if(re.content.items[0].isRealName==true){
                    $scope.isRealName = true;
                    $scope.getMyInfo();
                }
            })

        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '实名认证');
                if($rootScope["$$realNameArea"]!=null){
                    $scope.area=$rootScope["$$realNameArea"].locationArea;
                    $scope.areaValue=$rootScope["$$realNameArea"].locationAreaValue;
                    $scope.pValue=$rootScope["$$realNameArea"].locationAreaPValue;
                }
                $scope.isActive =  $rootScope.pathParams.isActive;
                $scope.getMemberIsRealName();
                $rootScope.isLoginPage = true;
                $scope.submitBtn();
                // $scope.sex=1;//男
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);