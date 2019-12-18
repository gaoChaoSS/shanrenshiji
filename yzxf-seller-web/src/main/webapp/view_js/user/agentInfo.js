(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = "user";
    var entity = "agentInfo";
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $rootScope.getFixed($scope.model, $scope.entity);

        $scope.modifyCheck=false;
        $scope.agentId=$location.search()['agentId'];

        $scope.getAgentInfo=function(){
            if(!window.isEmpty($scope.agentId)){
                var url = window.basePath + '/account/Agent/getAgentById?agentId='+$scope.agentId;
                $http.get(url).success(function (re) {
                    $scope.agent=re.content;
                    if($scope.agent==null){
                        malert("找不到该代理商");
                    }
                });
            }else{
                malert("找不到该代理商");
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
            if(window.isEmpty($scope.agentId)){
                malert("找不到该用户");
                return;
            }

            var url = window.basePath + '/account/Agent/updateAgentCanUse';
            var date = {
                idStr:$scope.agentId,
                canUseStr:!$scope.agent.canUse
            };
            $http.post(url, date).success(function () {
                malert("保存成功!");
                $scope.modifyCheck=false;
                $scope.getAgentInfo();
            });
        }

        $scope.getAgentInfo();
    });
})(angular);