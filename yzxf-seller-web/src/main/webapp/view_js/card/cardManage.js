(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $scope.tempGridList = '/view/card/t_cardManage_grid.jsp';
        $scope.tempGridFilter = '/view/card/t_cardManage_filter.jsp';

        $scope.entityTitle = "实体卡分配";
        $scope.windowCard = '/view/card/t_cardManage_grid_info.jsp';
        $scope.windowCur = '/view/card/t_cardManage_window.jsp';

        $scope.fullQueryApi = window.basePath + "/account/Agent/getCardByBelong";
        $scope.addressCode='';
        $scope.addressCodeT='';

        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name=='分配卡号'){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: "分配卡号", action: $scope.addCard
            });
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name=='查看当前登录用户拥有卡段'){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: "查看当前登录用户拥有卡段", action: $scope.getCur
            });
        };

        initGrid($rootScope, $scope, $http);

        $scope.getCardFieldList = function () {
            var url = window.basePath + '/account/Agent/agentGetAgentInfo?agentId=' + $rootScope.agent._id;
            $http.get(url).success(function (re) {
                $scope.agentInfoObj = re.content.agentInfo;
                $scope.adminAgent = re.content.adminAgent;
                $scope.cardFieldList = re.content.cardField;
                $scope.cardObj={};
                $scope.cardTotal='';

            })
        };

        $scope.initTempCard=function(){
            $scope.tempCard={
                grant:$rootScope.agent._id,
                receive:'',
                startCardNo:'',
                endCardNo:'',
                agentLevel:'',
                name:''
            };
            $scope.agentLowerList=[];
        };

        //获取下级代理商
        $scope.getAgentLowerList=function(){
            var url = window.basePath + '/account/Agent/getAgentByValue?areaValue='+$rootScope.agent.areaValue+'&agentValueCur'+$rootScope.agent.areaValue;
            $http.get(url).success(function (re) {
                $scope.agentLowerList=re.content.items;
                $scope.agentLowerList.unshift({name:'请选择',areaValue:'1'});
                $scope.tempCard.name=$scope.agentLowerList[0];
            });
        };

        //分配卡号:初始化收货方下拉框
        $scope.addCardInit=function(){
            $scope.initTempCard();
            $scope.getAgentLowerList();
            $scope.getCardFieldList();
        };

        $scope.showCardField = function(){
            $scope.addCardInit();
            var url = window.basePath + '/account/Agent/agentGetAdminCardField';
            $http.get(url).success(function (re) {
                $scope.adminCardFieldList = re.content.adminCardFiled;
                $scope.adminSpecial = re.content.adminSpecial;
                $scope.adminLevel = re.content.adminLevel;
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
                beforeDiff = beforeEnd - beforeStart,
                afterDiff = afterEnd - afterStart,
                re = "";
            if(beforeDiff<0 || (beforeDiff === 0 && afterDiff<0)){
                return '终止卡号比起始卡号小';
            }
            if(afterDiff<0){
                beforeDiff-- ;
                afterDiff = 100000000 + afterDiff;
            }
            if(beforeDiff!==0){
                re += beforeDiff.toString();
            }
            if(beforeDiff>0){
                afterDiff = afterDiff.toString();
                if(afterDiff.length!==beforeLen){
                    for(var i=0,len = beforeLen - afterDiff.length;i<len;i++){
                        re+="0";
                    }
                }
            }

            re += afterDiff;
            return re;
        };
        $scope.cardNO = function(startCardEnd){
            $scope.cardNofield ="";
            for(var i= 0;i<8-startCardEnd.toString().length;i++){
                $scope.cardNofield += '0';
            }
            return $scope.cardNofield+startCardEnd;
        }
        $scope.allocationCard = function (){
            if(!/^[0-9]{16}$/.test($scope.tempCard.startCardNo)||!/^[0-9]{16}$/.test($scope.tempCard.endCardNo)){
                malert("请输入16位的正整数");
                return ;
            }
            if($scope.tempCard.startCardNo>$scope.tempCard.endCardNo){
                malert("起始号段不能大于终止号段");
                return ;
            }
            if(window.isEmpty($scope.tempCard.name) ||window.isEmpty($scope.tempCard.name.areaValue)){
                malert("请选择收货方");
                return ;
            }
            $scope.tempCard.receive=$scope.tempCard.name._id;
            var url = window.basePath +'/account/Agent/assignCard';
            $http.post(url,$scope.tempCard).success(function(){
                $scope.getCardFieldList();
                $scope.tempCard.startCardNo='';
                $scope.tempCard.endCardNo='';
                malert('号段设置成功!');
            })
        }
        //平台设置起始号段
        $scope.setStartCardField = function(startCardNo,endCardNo){
            if(!/^[0-9]{16}$/.test(startCardNo)||!/^[0-9]{16}$/.test(endCardNo)){
                malert("请输入16位的正整数");
                return ;
            }
            // if(window.isEmpty($scope.addressCodeT)){
            //     malert("请选择地区");
            //     return ;
            // }
            var url = window.basePath +'/account/Agent/adminSetStartCardField';
            var data = {
                startCardNo:startCardNo,
                endCardNo:endCardNo
            }
            $http.post(url,data).success(function(){
                $scope.showCardField();
                malert('号段设置成功!');
            })
        }
        $scope.selectAddressCode = function(code){
            $scope.cardObj.startCardNo = code;
            $scope.cardObj.endCardNo = code;
        }
        ////平台设置起始号段
        //$scope.setStartSpecial = function(startCardNo,endCardNo){
        //    if(!/^[0-9]{6}$/.test(startCardNo)||!/^[0-9]{6}$/.test(endCardNo)){
        //        malert("请输入6位的正整数");
        //        return ;
        //    }
        //    var url = window.basePath +'/account/Agent/adminSetSpecialField';
        //    var data = {
        //        startCardNo:startCardNo,
        //        endCardNo:endCardNo
        //    }
        //    $http.post(url,data).success(function(){
        //        $scope.showCardField();
        //    })
        //}

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
        $scope.addressList = [
            {name:'--请选择地区--',v:''},
            {name:'北京',v:'11'},
            {name:'天津',v:'12'},
            {name:'河北',v:'13'},
            {name:'山西',v:'14'},
            {name:'内蒙古',v:'15'},
            {name:'辽宁',v:'21'},
            {name:'吉林',v:'22'},
            {name:'黑龙江',v:'23'},
            {name:'上海',v:'31'},
            {name:'江苏',v:'32'},
            {name:'浙江',v:'33'},
            {name:'安徽',v:'34'},
            {name:'福建',v:'35'},
            {name:'江西',v:'36'},
            {name:'山东',v:'37'},
            {name:'河南',v:'41'},
            {name:'湖北',v:'42'},
            {name:'湖南',v:'43'},
            {name:'广东',v:'44'},
            {name:'广西',v:'45'},
            {name:'海南',v:'46'},
            {name:'重庆',v:'50'},
            {name:'四川',v:'51'},
            {name:'贵州',v:'52'},
            {name:'云南',v:'53'},
            {name:'西藏',v:'54'},
            {name:'陕西',v:'61'},
            {name:'甘肃',v:'62'},
            {name:'青海',v:'63'},
            {name:'宁夏',v:'64'},
            {name:'新疆',v:'65'},
            {name:'台湾',v:'71'},
            {name:'香港',v:'81'},
            {name:'澳门',v:'82'}
        ];


    });
})(angular);
