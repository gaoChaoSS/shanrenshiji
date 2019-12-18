(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $scope.tempGridList = '/view/card/t_cardRecycle_grid.jsp';
        $scope.tempGridFilter = '/view/card/t_cardRecycle_filter.jsp';

        $scope.entityTitle = "实体卡回收";

        $scope.fullQueryApi = window.basePath + "/account/Agent/agentCriteriaAgentList";

        $scope.windowInfo = '/view/card/t_cardRecycle_window.jsp';
        initGrid($rootScope, $scope, $http);

        $scope.showOtherData = function () {
            var url = window.basePath + '/account/Agent/agentGetAgentInfo?agentId=' + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.agentInfoObj = re.content.agentInfo;
                $scope.adminAgent = re.content.adminAgent;
                $scope.superiorAgent = re.content.superiorAgent.name;
                $scope.cardFieldList = re.content.cardField;
                $scope.cardObj={};
                $scope.cardTotal='';

            })
        }
        $scope.countCardTotal = function(start,end){
            if(!/^[0-9]{16}$/.test(start)||!/^[0-9]{16}$/.test(end)){
                $scope.cardTotal='请输入16位卡号长度';
                return ;
            }
            $scope.cardTotal = $scope.getSubtraction(start,end);
        }
        $scope.getSubtraction = function(start,end){
            if(start.length!==end.length){
                return '长度不一致';
            }
            if(start.toString()===end.toString()){
                return 0;
            }

            var afterLen = start.length,
                beforeLen = Math.floor(afterLen/2),
                beforeStart = start.substring(0,beforeLen),
                afterStart = start.substring(beforeLen,afterLen),
                beforeEnd = end.substring(0,beforeLen),
                afterEnd = end.substring(beforeLen,afterLen),
                beforeDiff = beforeStart - beforeEnd,
                afterDiff = afterStart - afterEnd,
                re = "";
            if(beforeDiff<0 || (beforeDiff === 0 && afterDiff<0)){
                return '终止卡号比起始卡号大';
            }
            if(afterDiff<0){
                beforeDiff-- ;
                afterDiff = 100000000 + afterDiff;
            }
            if(beforeDiff!==0){
                re += beforeDiff.toString();
            }
            afterDiff = afterDiff.toString();
            if(afterDiff.length!==beforeLen){
                for(var i=0,len = beforeLen - afterDiff.length;i<len;i++){
                    re+="0";
                }
            }
            re += afterDiff;
            return re;
        };

        $scope.cardRecycle = function (startCardNo,endCardNo){
            if(!/^[0-9]{16}$/.test(startCardNo)||!/^[0-9]{16}$/.test(endCardNo)){
                malert("请输入16位的正整数");
                return ;
            }
            if(startCardNo>endCardNo){
                malert("起始号段不能大于终止号段");
                return ;
            }
            var url = window.basePath +'/account/Agent/cardRecycle';
            var data = {
                agentId:$scope.agentInfoObj._id,
                areaValue:$scope.agentInfoObj.areaValue,
                agentLevel:$scope.agentInfoObj.level,
                startCardNo:startCardNo,
                endCardNo:endCardNo
            }
            $http.post(url,data).success(function(){
                $scope.showOtherData();
                malert('号段回收成功!');
            })
        }
        $scope.agentLevelNum = function (num) {
            if (num == '2') {
                return '省级代理商';
            }
            if (num == '3') {
                return '市级代理商';
            }
            if (num == '4') {
                return '县级代理商';
            }
            if (num == '5') {
                return '服务中心';
            }
        }


    });
})(angular);
