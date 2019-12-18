/**
 * Created by tianchangsen on 17/1/24.
 */

(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'Agent';
        $scope.entityTitle="代理商管理";
        $scope.tempGridList = '/view/user/t_agent_grid.jsp';
        $scope.windowInfo = '/view/user/t_agentInfo_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_agent_grid_filter.jsp';

        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            if (/^[123]$/.test($rootScope.agent.level)) {
                for(var i =0;i<$scope.actionList.length;i++){
                    if($scope.actionList[i].name=='新增'){
                        $scope.actionList.remove(i);
                        break;
                    }
                }
                $scope.actionList.push({
                    name: "新增", action: $scope.addData
                });
                $scope.windowAdd = '/view/user/t_agentAdd_grid.jsp';

                for(var i =0;i<$scope.actionList.length;i++){
                    if($scope.actionList[i].name=='修改'){
                        $scope.actionList.remove(i);
                        break;
                    }
                }
                $scope.actionList.push({
                    name: "修改", action: $scope.modifyData
                });
            }
        }

        $scope.setUserType=function(){
            if($scope.filter._areaValue=='___like_\\_A-000001\\_'){
                $scope.filter._areaValue='___like_\\_A-000001\\_A';
            }
        }

        initGrid($rootScope, $scope, $http);

        $scope.showOtherData = function () {
            $scope.showImg='';
            $scope.userInfo={};
            var url = window.basePath + "/account/Agent/getAgentById?agentId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content;
                $scope.userInfo.address=($scope.userInfo.area==null?'':$scope.userInfo.area)+($scope.userInfo.address==null?'':$scope.userInfo.address);
                //$scope.userInfo.surplusCardNum=window.isEmpty($scope.userInfo.surplusCardNum)?'0':$scope.userInfo.surplusCardNum;
                $scope.getLoginName();
                $scope.getCardNumber();
                //临时存放
                $scope.imgMore={
                    bankImg: $scope.userInfo.bankImg.split("_"),
                    contractImg:$scope.userInfo.contractImg.split("_")
                };
            });
        }

        $scope.getCardNumber=function(){
            var url = window.basePath + "/account/Agent/getAgentCardNum?userId=" + $scope.dataPage.$$selectedItem._id+"&userType=Agent";
            $http.get(url).success(function (re) {
                $scope.userInfo.surplusCardNum=0;
                $scope.cardList=re.content.items;
                if($scope.cardList!=null && $scope.cardList.length>0){
                    for(var j= 0,len=$scope.cardList.length;j<len;j++){
                        $scope.userInfo.surplusCardNum+=$scope.cardList[j].cardNum;
                    }
                }
            });
        }
        $scope.cardNO = function(cardNum){
            var cardNo ="";
            for(var i= 0;i<6-cardNum.toString().length;i++){
                cardNo += '0';
            }
            return cardNo+cardNum;
        }

        $scope.getLoginName=function(){
            var url = window.basePath + "/account/UserPending/getUserLoginName?userId=" + $scope.userInfo._id+"&userType=Agent";
            $http.get(url).success(function (re) {
                $scope.userInfo.loginName=re.content.loginName;
                $scope.userInfo.userName=re.content.userName;
            });
        }

        $scope.resetUserPwd=function(){
            if(window.isEmpty($scope.userInfo.loginName)){
                malert("获取用户信息失败!");
                return;
            }

            var url = window.basePath + "/account/User/resetUserPwd?loginName="+$scope.userInfo.userName;
            $http.get(url).success(function (re) {
                $scope.loginPwd=re.content.password;
            });
        }

        $scope.clearPwd=function(){
            $scope.loginPwd=''
        }

        $scope.modifyInfo=function(){
            $scope.userInfo={};
            var url = window.basePath + "/account/Agent/getAgentById?agentId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content;
                $scope.pendingId="";
                $scope.isModify=true;
                modifyAgentGrid($rootScope, $scope, $http);
            });
        }
        $scope.addInit = function () {
            modifyAgentGrid($rootScope, $scope, $http);
        }

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }

        $scope.closeImgFun=function(){
            $scope.showImg='';
        }

    });
})(angular);
