(function(angular, undefined) {
    var model = "home";
    var entity = "memberRealName";
    window.app.register.controller(model+'_'+entity+'_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.isRealName = false;

        $scope.area="";
        $scope.areaValue="";
        $scope.pValue="";

        $scope.check = 'disabled';
        //提交按钮判断
        $scope.submitBtn=function(){
            $scope.check = 'disabled';
            if(!window.isEmpty($scope.realName) && !window.isEmpty($scope.address) && !window.isEmpty($scope.cardNumber)
                && !$scope.cardError && !$scope.emailError){
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
            if(window.isEmpty($scope.myEmail)){
                $scope.emailError=false;
            }else{
                if(/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.myEmail)){
                    $scope.emailError=false;
                }else{
                    $scope.emailError=true;
                }
            }

        }

        $scope.submitForm = function(){
            if(window.isEmpty($scope.sex)){
                malert("请选择性别");
                return;
            }
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

            var url = window.basePath + '/crm/Member/memberRealName';
            var data = {
                memberId : getCookie("_active_memberId")==null?$rootScope.pathParams.memberId:getCookie("_active_memberId"),
                realName : $scope.realName,
                idCard : $scope.cardNumber,
                email : $scope.myEmail,
                realArea : $scope.area,
                realAreaValue : $scope.areaValue,
                realAddress : $scope.address,
                sex:$scope.sex
            }
            $http.post(url, data).success(function (re) {
                if(re.length!=0){
                    $rootScope["$$realNameArea"]=null;
                    malert('认证成功,请绑定会员卡');
                    $rootScope.goPage('/home/bindMemberCard/memberId/'+re.content.memberId);
                }
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '实名认证');
                if($rootScope["$$realNameArea"]!=null){
                    $scope.area=$rootScope["$$realNameArea"].locationArea;
                    $scope.areaValue=$rootScope["$$realNameArea"].locationAreaValue;
                }
                $rootScope.isLoginPage = true;
                $scope.submitBtn();
                // $scope.sex=1;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);