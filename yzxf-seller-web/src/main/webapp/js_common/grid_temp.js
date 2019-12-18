(function () {
    window.initGrid = function ($rootScope, $scope, $http) {
        $scope.timeFilter = '/temp/filter_time.html';
        $scope.popWindowTemp = $scope.windowInfo;//详细信息
        window.initFilterTime($rootScope, $scope);

        $scope.queryCurrentList = function () {
            if($scope.entity=="agentIncome"||$scope.entity=="factorIncome"||$scope.entity=="sellerIncome"||$scope.entity=="financailReport"){
                if(!$scope.setTLeMentTime()){
                    return false;
                }
            }

            if ($scope.fullQueryApi == null) {
                $scope.apiName = $scope.apiName == null ? 'query' : $scope.apiName;
                $scope.fullQueryApi = window.basePath + "/" + $scope.model + "/" + $scope.entity + "/" + $scope.apiName;
            }
            var url = $scope.fullQueryApi + "?1=1";
            $.each($scope.filter, function (k, v) {
                if (k == 'startTime' || k == 'endTime') {
                    return true;
                }
                if (window.isEmpty(v)) {
                    return true;
                }
                if (k == "_areaValue" && ($scope.entity == 'Member' || $scope.entity == 'Seller')) {
                    if($scope.entity != 'Member' || v!="___like_\\_A-000001\\_"){
                        url += '&_belongAreaValue=' + v;
                    }
                    return true;
                }
                if( k=="_belongArea" && $scope.entity == 'Member'){
                    url += '&_belongArea=___like_' + v;
                }
                if($scope.model == 'crm' && $scope.entity!='Member'){
                    if($scope.filter._areaValue=='___like_\\_A-000001\\_'){
                        $scope.filter._areaValue='___like_\\_A-000001\\_A';
                    }
                }
                url += '&' + k + '=' + encodeURIComponent(v);
            });
            if (!isEmpty($scope.filter.$$startTime) || !isEmpty($scope.filter.$$endTime)) {
                $scope.timeType={_id:"setTime"};
                window.initFilterTime($rootScope, $scope);
                url += '&_createTime=___in_' + $scope.filter.startTime + '-' + $scope.filter.endTime;
            }else if(!isEmpty($scope.filter.startTime) && !isEmpty($scope.filter.endTime)){
                url += '&_createTime=___in_' + $scope.filter.startTime + '-' + $scope.filter.endTime;

            }
            if($scope.entity=='Agent'){
                url+="&_adminType=___null"
            }
            //if ($scope.entity != 'Member') {
            //    url += '&_applyTime=___notnull';
            //}

            $http.get(url).success(function (re) {
                if($scope.model=='insure' && $scope.entity=='pensionLog'){
                    $scope.getPensionMoneyStatistics();
                }
                if($scope.model=='account' && ($scope.entity=='sellerIncome'||$scope.entity=='factorIncome'||$scope.entity=='agentIncome')){
                    $scope.getMonthMoneyStatistics();
                }
                $scope.dataPage = re.content;
                //$scope.filter.pageNo = $scope.dataPage.pageNo;
                $scope.dataPage.$$pageList = [];
                var start = $scope.dataPage.pageNo - 3;
                var end = $scope.dataPage.pageNo + 4;
                $scope.memberNum = 0;

                start = start < 1 ? 1 : start;
                end = end > $scope.dataPage.totalPage ? $scope.dataPage.totalPage : end;
                if($scope.model=='card' && $scope.entity=='cardCount'){
                    $scope.activeNumAll = $scope.dataPage.items[0].activeNumAll;
                }
                for (var i = start; i <= end; i++) {
                    $scope.dataPage.$$pageList.push(i);
                }
                for (var i = 0; i < $scope.dataPage.items.length; i++) {
                    if (!window.isEmpty($scope.dataPage.items[i].belongArea)) {
                        var areaArr = $scope.dataPage.items[i].belongArea.split("-");
                        $scope.dataPage.items[i].belongArea = areaArr[areaArr.length - 1];
                    }
                    if(!window.isEmpty($scope.dataPage.items[i].count)){
                        $scope.memberNum +=$scope.dataPage.items[i].count;
                    }
                }
                if($scope.getBillAccount){
                    $scope.getBillAccount();
                }
                //如果起始坐标大于总数量,则回到第一页(例如此时在第二页,查询'A'有一条,仍在第二页,未重置到第一页)
                if($scope.dataPage.startIndex>$scope.dataPage.totalNum){
                    $scope.filter.pageNo=1;
                    $scope.queryCurrentList();
                }
            });
        }

        $scope.resetFilter = function () {
            $scope.filter = {
                pageNo: 1, pageSize: 20, keywords: 'name,belongArea',
                _areaValue: ''
            };
            $scope.agentSelectValue = ["", "", "", "", ""];
            if ($rootScope.agent != null) {
                $scope.initArea();
            } else {
                window.pageCallback = function () {
                    $scope.initArea();
                }
            }
            if($scope.setUserType){
                $scope.setUserType();
            }

            //$scope.queryCurrentList();
        }

        $rootScope.getAgentAreaValueById=function(v){
            var url = window.basePath + "/account/Agent/getAgentAreaValueById?areaValue=" + v;
            $http.get(url).success(function (re) {
                $scope.agentNameAll = re.content.agentNameAll;
            });
        }

        //查看
        $scope.showDataInfo = function () {
            $scope.popWindowTemp = $scope.windowInfo;//详细信息
            if ($scope.dataPage == null || $scope.dataPage.$$selectedItem == null) {
                malert('请先选择一条数据');
                return;
            }
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '详情';

            var v = "";
            $scope.agentNameAll="";
            if ($scope.entity == "Member" || $scope.entity == "Seller" || $scope.entity == "UserPending") {
                v = $scope.dataPage.$$selectedItem.belongAreaValue;
            } else {
                v = $scope.dataPage.$$selectedItem.areaValue;
            }
            if (!window.isEmpty(v)) {
                $rootScope.getAgentAreaValueById(v);
            }

            //查询详细信息页面
            if ($scope.showOtherData) {
                $scope.showOtherData();
            }
            //查询审批页面
            if ($scope.showPendingData) {
                if($scope.dataPage.$$selectedItem.ownerType=="Seller"){
                    $scope.popWindowTemp = '/view/user/t_sellerInfo_grid.jsp';
                }else if($scope.dataPage.$$selectedItem.ownerType=="Factor"){
                    $scope.popWindowTemp = '/view/user/t_factorInfo_grid.jsp';
                }else if($scope.dataPage.$$selectedItem.ownerType=="Agent"){
                    $scope.popWindowTemp = '/view/user/t_agentInfo_grid.jsp';
                }
                $scope.showPendingData();
            }
        }
        //新增
        $scope.addData = function () {
            $scope.popWindowTemp = $scope.windowAdd;//新增
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '新增';
            //如果是新增/修改,则不显示不可用的服务站
            $scope.isFactorCanUse = true;
            $scope.initArea();
            //新增页面初始化:初始化 地址
            if ($scope.addInit) {
                $scope.addInit();
            }
        }
        //修改
        $scope.modifyData = function () {
            $scope.popWindowTemp = $scope.windowAdd;//修改
            if ($scope.dataPage == null || $scope.dataPage.$$selectedItem == null) {
                malert('请先选择一条数据');
                return;
            }
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '修改';
            //如果是新增/修改,则不显示不可用的服务站
            $scope.isFactorCanUse = true;
            $scope.initArea();

            if($scope.modifyInfo){
                $scope.modifyInfo();
            }
        }

        //删除
        $scope.delData=function(){
            if($scope.delFun){
                $scope.delFun();
            }
        }
        //提交
        $scope.subData=function(){
            if($scope.submitFun){
                $scope.submitFun();
            }
        }

        $scope.getCur = function () {
            $scope.popWindowTemp = $scope.windowCur;//详细信息
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '当前登录用户拥有卡段';
            //查询详细信息页面
            if ($scope.showCardField) {
                $scope.showCardField();
            }
        }

        //分配
        $scope.addCard = function () {
            $scope.popWindowTemp = $scope.windowCard;
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '分配卡号';
            //新增页面初始化:初始化 地址
            if ($scope.addCardInit) {
                $scope.addCardInit();
            }
        };

        $scope.customBtn=function(){
            if($scope.customFun){
                $scope.customFun();
            }
        }

        $scope.closePopWin = function () {
            $rootScope.showEditor = false;
            $rootScope.showPopWin = false;
            //$scope.queryCurrentList();
            if($scope.entityTitle=='待审批' && $scope.agentSelectValue && $scope.agentSelectValue.length>0){
                for(var i=0;i<$scope.agentSelectValue.length;i++){
                    $scope.agentSelectValue[i]="";
                }
                $scope.agentChildList.splice(1,$scope.agentChildList.length);
                $scope.filter._areaValue="";
            }
            $scope.isFactorCanUse=false;
            $scope.isPendingLevel='';
            //$scope.initArea();
        }
        $scope.pageNumber = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $scope.filter.pageNo = num;
            $scope.queryCurrentList();

        }
        $scope.pageNext = function (num) {
            $scope.filter.pageNo += num;
        }
        $scope.pageGoFun = function (num) {
            if(num>$scope.dataPage.totalPage){
                malert('跳转页面超过上限!');
                return;
            }
            $scope.filter.pageNo = num;
        }
        $scope.getMonthMoneyStatistics = function(){
            var url = window.basePath + '/order/OrderInfo/getUserEarningsSum?1=1';
            $.each($scope.filter, function (k, v) {
                if (k == 'startTime' || k == 'endTime') {
                    return true;
                }
                if (window.isEmpty(v)) {
                    return true;
                }
                if (k == "_areaValue" && ($scope.entity == 'Member' || $scope.entity == 'Seller')) {
                    if($scope.entity != 'Member' || v!="___like_\\_A-000001\\_"){
                        url += '&_belongAreaValue=' + v;
                    }
                    return true;
                }
                url += '&' + k + '=' + encodeURIComponent(v);
            });
            if (!isEmpty($scope.filter.$$startTime) || !isEmpty($scope.filter.$$endTime)) {
                $scope.timeType={_id:"setTime"};
                window.initFilterTime($rootScope, $scope);
                url += '&_createTime=___in_' + $scope.filter.startTime + '-' + $scope.filter.endTime;
            }else if(!isEmpty($scope.filter.startTime) && !isEmpty($scope.filter.endTime)){
                url += '&_createTime=___in_' + $scope.filter.startTime + '-' + $scope.filter.endTime;
            }
            $http.get(url).success(function(re){
                $scope.monthMoneySum = re.content.items[0].monthMoneySum

            })
        }

        var selectNames = [
            {areaValue: '', name: '---选择省代理商---'}
            , {areaValue: '', name: '---选择市代理商---'}
            , {areaValue: '', name: '---选择县/区代理商---'}
            , {areaValue: '', name: '---选择服务站---'}
        ];
        $scope.agentSelectValue = ["", "", "", "", ""];
        $scope.getAgentChildren = function (areaValue, index) {
            if(window.isEmpty($rootScope.agent.level)){
                return;
            }
            var level = parseInt($rootScope.agent.level) + index;

            var maxLevel = $scope.entity == 'Factor' || $scope.entity == 'memberCount' || $scope.entity == 'Agent' || $scope.entity == 'agentIncome'
            || $scope.entity == 'cardCount'||$scope.entity == 'financailReport'? 4 : 5;
            if(!window.isEmpty($scope.isPendingLevel)){
                maxLevel=$scope.isPendingLevel;
            }
            if (level >= maxLevel) {
                if (!isEmpty(areaValue)) {
                    $scope.filter._areaValue = "___like_" + areaValue.replace(/_/g, "\\_");
                }else{
                    if(isEmpty($scope.filter._areaValue)){
                        $scope.filter._areaValue = "___like_" + $rootScope.agent.areaValue.replace(/_/g, "\\_");
                        return;
                    }
                    var areaValueArr = $scope.filter._areaValue.substring(10,$scope.filter._areaValue.length-2).split("\\_");
                    var areaValueStr="";
                    for(var i= 0,index;i<level-1;i++){
                        areaValueStr+=areaValueArr[i]+"_";
                    }
                    areaValueStr="_"+areaValueStr;
                    $scope.filter._areaValue = "___like_" + areaValueStr.replace(/_/g, "\\_");
                }
                if(level==5 && $scope.getNotActiveCardNo){
                    $scope.getNotActiveCardNo();
                }
                return;
            }
            if (areaValue == "") {
                var areaValueStr=$scope.filter._areaValue;
                $scope.agentChildList = $scope.agentChildList.slice(0, index);
                $scope.filter._areaValue = $scope.agentSelectValue[$scope.agentChildList.length - 1];
                if (!isEmpty($scope.filter._areaValue)) {
                    $scope.filter._areaValue = "___like_" + $scope.filter._areaValue.replace(/_/g, "\\_");
                }else{
                    var areaValueArr = areaValueStr.substring(10,areaValueStr.length-2).split("\\_");
                    areaValueStr="";
                    for(var i= 0,index;i<level-1;i++){
                        areaValueStr+=areaValueArr[i]+"_";
                    }
                    areaValueStr="_"+areaValueStr;
                    $scope.filter._areaValue = "___like_" + areaValueStr.replace(/_/g, "\\_");
                }
                return;
            }


            var list = [];
            list.push(selectNames[level - 1]);
            var url = window.basePath + '/account/Agent/getAgentByValue?areaValue=' + areaValue+"&isFactorCanUse="+$scope.isFactorCanUse;
            $http.get(url).success(function (re) {
                $.each(re.content.items, function (k, v) {
                    v.level = level + 1;
                    list.push(v);
                });
                $scope.agentChildList[index] = list;
                $scope.agentChildList = $scope.agentChildList.slice(0, index + 1);

                $scope.filter._areaValue = $scope.agentSelectValue[$scope.agentChildList.length - 2];
                if (!isEmpty($scope.filter._areaValue)) {
                    $scope.filter._areaValue = "___like_" + $scope.filter._areaValue.replace(/_/g, "\\_");
                }else{
                    $scope.filter._areaValue = "___like_" + $rootScope.agent.areaValue.replace(/_/g, "\\_");
                }
            });

        }

        $scope.initArea = function () {
            $scope.agentChildList = [];
            $scope.getAgentChildren($rootScope.agent.areaValue, 0);
            if ($scope.getAgentAction) {
                $scope.getAgentAction();
            }
        }
        $scope.getNewPending();
        $scope.resetFilter();
    }
})();