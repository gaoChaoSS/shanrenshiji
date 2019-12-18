(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/account/t_agentIncome_grid.jsp';
        $scope.tempGridFilter = '/view/account/t_agentIncome_filter.jsp';
        $scope.settlement_time = '/temp_new/settlement_time.html';
        $scope.entityTitle = "代理商月收益结算";
        $scope.windowInfo = '/view/account/t_agentIncomeInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getUserEarnings";

        $scope.setUserType=function(){
            $scope.filter._userType=0;
        };

        $scope.getAgentAction = function () {
            $scope.actionList = [];
            if($rootScope.agent.level==1){
                $scope.actionList.push({
                    name: "生成上个月收益结算", action: $scope.addData
                });
            }
            $scope.actionList.push({
                name: "查看当前代理商月结算", action: $scope.getMyIncome
            });
        };

        $scope.getMyIncome=function(){
            $scope.popWindowTemp = "/view/account/agentIncome_my_grid.jsp";
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '当前用户月结算';

            var startDate = new Date(new Date(new Date(new Date().setMonth(new Date().getMonth()-1)).setDate(1)).setHours(0,0,0,0));
            // var endDate = new Date(new Date(startDate).setMonth(startDate.getMonth()+1)-1);
            var month = startDate.getFullYear()+""+(parseInt(startDate.getMonth())+1);
            //生成当前用户的信息
            $scope.dataPage={
                $$selectedItem:{
                    userId:$rootScope.agent._id,
                    areaValue:$rootScope.agent.areaValue,
                    month:month
                }
            };

            var yearIndex = 0;
            var queryYear = startDate.getFullYear();
            $.each($scope.startYear,function(k,v){
                if(window.isEmpty(v.date) || v.date!=queryYear){
                    yearIndex++;
                }
            });
            $scope.selectTime[0]=$scope.startYear[yearIndex-1];
            $scope.selectTime[1]=$scope.startMonth[parseInt(startDate.getMonth())+1];
            $scope.selectedShow[0]=true;

            $scope.showOtherData();
            $rootScope.getAgentAreaValueById($rootScope.agent.areaValue);
            $scope.getMyIncomeMoney(month);
        };

        // 在窗口中条件筛选查询
        $scope.getMyIncomePage=function(){
            var month = '';
            if(!window.isEmpty($scope.selectTime[0]) && !window.isEmpty($scope.selectTime[1])){
                month = $scope.selectTime[0].date+''+($scope.selectTime[1].date>10?$scope.selectTime[1].date:'0'+$scope.selectTime[1].date);
            }
            if(!window.isEmpty(month)){
                $scope.dataPage.$$selectedItem.month = month;
            }

            $scope.showOtherData();
            $scope.getMyIncomeMoney(month);
        };

        $scope.getMyIncomeMoney = function(month){
            var url = window.basePath + "/order/OrderInfo/getUserEarnings" +
                "?pageNo=1" +
                "&pageSize=1" +
                "&_dateStart=" + month +
                "&_dateEnd=" + month+
                "&_userType=0" +
                "&_userId="+$rootScope.agent._id;
            $http.get(url).success(function(re){
                if(!window.isEmpty(re.content) && !window.isEmpty(re.content.items)
                        && re.content.items.length>0){
                    $scope.dataPage.$$selectedItem.incomeAccount=re.content.items[0].incomeAccount;
                }
            });
        };

        initGrid($rootScope, $scope, $http);
        $scope.setUserType();

        $scope.showOtherData=function(){
            incomeGrid($rootScope, $scope, $http);
        };

        $scope.getBillAccount=function(){
            $scope.totalIncomeAccount = $rootScope.countField($scope.dataPage.items,"incomeAccount");
            if(!window.isEmpty($scope.totalIncomeAccount) && $scope.totalIncomeAccount!==0){
                $scope.totalIncomeAccount="("+$scope.totalIncomeAccount+")";
            }else{
                $scope.totalIncomeAccount='';
            }
        };

        $scope.addInit=function(){
            $rootScope.showPopWin = false;
            var url = window.basePath + "/order/OrderInfo/createAgentAccountMonthForWeb";
            $http.get(url).success(function(){
                malert("已生成");
            });
        };

        $scope.getAccountMonth = function (text) {
            if (!window.isEmpty(text)) {
                var year = text.toString().substring(0, text.toString().length - 2);
                var month = text.toString().substring(text.toString().length - 2, text.toString().length);
                return year + "年" + month + "月";
            }
        }

        $scope.getIsTransfer=function(text){
            if(window.isEmpty(text)){
                return '否';
            }else{
                return '是';
            }
        }

        $scope.initTime=function(){
            //默认显示今年的记录
            var defalutYear = 2017;
            var len = parseInt(new Date().getFullYear())-defalutYear+1;
            $scope.startYear = new Array(len);
            $scope.endYear = new Array(len);
            $scope.selectTime=new Array(4);

            $scope.selectedShow=[false,false,false,false];

            for(var i = 0 ; i <len ;i++){
                $scope.startYear[i]={date:''};
                $scope.endYear[i]={date:''};
                $scope.startYear[i].date= defalutYear+i;
                $scope.endYear[i].date= defalutYear+i;
            }
            $scope.startYear.unshift({date: '请选择年'});
            $scope.endYear.unshift({date: '请选择年'});

            $scope.selectTime[0]=$scope.startYear[0];
            $scope.selectTime[2]=$scope.endYear[0];

            $scope.month = [
                {date:'请选择月'},
                {date:1},
                {date:2},
                {date:3},
                {date:4},
                {date:5},
                {date:6},
                {date:7},
                {date:8},
                {date:9},
                {date:10},
                {date:11},
                {date:12}
            ];
            $scope.startMonth=$scope.month;
            $scope.endMonth=$scope.month;
            $scope.selectTime[1]=$scope.startMonth[1];
            $scope.selectTime[3]=$scope.endMonth[12];
        }
        $scope.initTime();

        $scope.setTLeMentTime = function(){
            var dateStart = "";
            var dateEnd = "";
            if($scope.selectTime[0].date!='请选择年' && $scope.selectTime[1].date=='请选择月'){
                dateStart = $rootScope.isNullText($scope.selectTime[0].date)+"01";
            }
            if($scope.selectTime[2].date!='请选择年' && $scope.selectTime[3].date=='请选择月'){
                dateEnd = $rootScope.isNullText($scope.selectTime[2].date)+"12";
            }
            if($scope.selectTime[0].date!='请选择年' && $scope.selectTime[1].date!='请选择月'){
                dateStart = $rootScope.isNullText($scope.selectTime[0].date)+""+$scope.getMonthFun($scope.selectTime[1].date);
            }
            if($scope.selectTime[2].date!='请选择年' && $scope.selectTime[3].date!='请选择月'){
                dateEnd = $rootScope.isNullText($scope.selectTime[2].date)+""+$scope.getMonthFun($scope.selectTime[3].date);
            }

            if(!window.isEmpty(dateStart)){
                $scope.filter._dateStart=dateStart;
            }
            if(!window.isEmpty(dateEnd)){
                $scope.filter._dateEnd=dateEnd;
            }
            return true;
        }

        $scope.setUserType = function(){
            $scope.initTime();
        }

        $scope.getMonthFun=function(m){
            if(window.isEmpty(m)){
                return "";
            }
            if(parseInt(m)<10){
                return '0'+m;
            }
            return m;
        }

        $scope.getTimeYear=function(index){
            $scope.selectedShow[index]=true;
            if(index==0){
                $scope.selectTime[1]=$scope.startMonth[0];
            }else if(index==2){
                $scope.selectTime[3]=$scope.startMonth[0];
            }
        }

        //$scope.isDateFun=function(index,str){
        //    if(str=='请选择年'){
        //        $scope.selectTime[index].date=''
        //    }else if(str=='请选择月'){
        //        $scope.selectTime[index].date=''
        //    }
        //}
        //$scope.model = model;
        //$scope.entity = entity;
        //$rootScope.getFixed($scope.model, $scope.entity);
        //$scope.checkCardManage = true;
        //$scope.isCreateCard = false;
        //$scope.assignNum=null;
        //$scope.agentTradeList=[];
        //$scope.tradeType = 1;
        //$scope.agentAreaValue=getCookie("___agent_AreaValue").toString().split(",");
        //$scope.timeFilter = '/temp/filter_time.html';
        //$scope.filter = {startTime:"",endTime:"",pageNo:1,pageSize:30,areaValue:$scope.agentAreaValue[$scope.agentAreaValue.length - 1]};
        //window.initFilterTime($rootScope, $scope);
        //window.indexNum = 0;
        //window.pageNo = 1;
        //window.pageSize = 30;
        //$scope.isLoadMore = true;
        ////显示页数
        //$scope.pageList=[];
        //
        ////上一页/下一页
        //$scope.pageNext = function (num,isCriteria) {
        //    if (window.pageNo + num < 1 || $scope.totalPage < window.pageNo + num) {
        //        return;
        //    }
        //    window.pageNo += num;
        //    $scope.criteriaQuery();
        //
        //}
        ////跳转页码
        //$scope.pageNumber = function (num,isCriteria) {
        //    if (num < 1 || $scope.totalPage < num) {
        //        return;
        //    }
        //    window.pageNo = num;
        //    $scope.criteriaQuery();
        //
        //}
        //
        //$scope.pageCur=function(index){
        //    $scope.pageIndex=index;
        //}
        //$scope.initArea=function(){
        //    $scope.areaList=[[{name:'---选择省代理商---'}],[{name:'---选择市代理商---'}],[{name:'---选择县/镇/区代理商---'}],[{name:'---选择发卡点---'}]];
        //    $scope.selectArea = new Array(4);
        //
        //    var level=$scope.agentAreaValue.length;
        //    if(level<1){
        //        malert("您无此查询权限");
        //        return;
        //    }
        //    if($scope.agentAreaValue=="-1"){
        //        $scope.getLocation("-1",1);
        //    }else{
        //        for(var index=0;index<level-1;index++){
        //            $scope.areaList[index][0].name=$scope.agentAreaValue[index];
        //            $scope.selectArea[index]=$scope.areaList[index][0];
        //        }
        //        $scope.selectArea[level-2]["areaValue"]=$scope.agentAreaValue[level-1];
        //        $scope.getLocation($scope.agentAreaValue[level-1],level);
        //    }
        //
        //}
        //
        ////获取地理位置
        //$scope.getLocation = function (areaValue, type) {
        //    var url = window.basePath + '/account/Agent/getAgentByValue?areaValue=' + areaValue;
        //    $http.get(url).success(function (re) {
        //        if(type==1){
        //            $scope.areaList[0]=re.content.items;
        //            $scope.areaList[0].unshift({name:'---选择省代理商---'});
        //
        //            $scope.areaList[1]=[{name:'---选择市代理商---'}];
        //            $scope.areaList[2]=[{name:'---选择县/镇/区代理商---'}];
        //            $scope.areaList[3]=[{name:'---选择发卡点---'}];
        //
        //            $scope.selectArea[0]=$scope.areaList[0][0];
        //            $scope.selectArea[1]=$scope.areaList[1][0];
        //            $scope.selectArea[2]=$scope.areaList[2][0];
        //            $scope.selectArea[3]=$scope.areaList[3][0];
        //        }else if(type==2){
        //            $scope.areaList[1]=re.content.items;
        //            $scope.areaList[1].unshift({name:'---选择市代理商---'});
        //
        //            $scope.areaList[2]=[{name:'---选择县/镇/区代理商---'}];
        //            $scope.areaList[3]=[{name:'---选择发卡点---'}];
        //
        //            $scope.selectArea[1]=$scope.areaList[1][0];
        //            $scope.selectArea[2]=$scope.areaList[2][0];
        //            $scope.selectArea[3]=$scope.areaList[3][0];
        //        }else if(type==3){
        //            $scope.areaList[2]=re.content.items;
        //            $scope.areaList[2].unshift({name:'---选择县/镇/区代理商---'});
        //            $scope.areaList[3]=[{name:'---选择发卡点---'}];
        //            $scope.selectArea[2]=$scope.areaList[2][0];
        //            $scope.selectArea[3]=$scope.areaList[3][0];
        //        }else if(type==4){
        //            $scope.areaList[3]=re.content.items;
        //            $scope.areaList[3].unshift({name:'---选择发卡点---'});
        //            $scope.selectArea[3]=$scope.areaList[3][0];
        //        }
        //        $scope.selectAreaValue=areaValue;
        //        if(type>1 && (typeof(areaValue)=="undefined" || window.isEmpty(areaValue))){
        //            $scope.selectAreaValue=$scope.selectArea[type-3].areaValue;
        //        }else if(type==1){
        //            $scope.selectAreaValue="-1";
        //        }
        //        $scope.filter.areaValue=$scope.selectAreaValue;
        //    });
        //}
        //$scope.criteriaSelect = function(){
        //    window.indexNum = 0;
        //    window.pageNo = 1;
        //    window.pageSize = 30;
        //    $rootScope.isCriteria = true;
        //    $scope.queryCurrentList();
        //}
        //$scope.queryCurrentList = function(){
        //    var url = window.basePath + "/order/OrderInfo/getAgentEarnings?1=1";
        //    $.each($scope.filter, function (k, v) {
        //        if (k == '$$startTime' || k == '$$endTime') {
        //            return true;
        //        }
        //        if(typeof(v)=="undefined"){
        //            return true;
        //        }
        //        url += '&' + k + '=' + encodeURIComponent(v);
        //    });
        //    //if (!isEmpty($scope.filter.startTime) && !isEmpty($scope.filter.endTime)) {
        //    //    url += '&startTime=' + $scope.filter.startTime + '&endTime=' + $scope.filter.endTime;
        //    //}
        //    $http.get(url).success(function(re){
        //        $scope.agentTradeList=[];
        //        if(re.content.agentTradeList==null){
        //            return true;
        //        }
        //        $.each(re.content.agentTradeList, function (k, v) {
        //            $scope.agentTradeList.push(v);
        //        });
        //        $scope.totalNumber = re.content.totalNum;
        //        $scope.totalPage = re.content.totalPage;
        //        //$scope.totalPage = 5;
        //
        //        //页码集合
        //        $scope.pageList=[];
        //        //当前显示的页码从第几页开始
        //        var listCur=1;
        //        var listCurCount=5;
        //        if(window.pageNo<=5 && $scope.totalPage<=5){
        //            listCur=1;
        //            listCurCount=$scope.totalPage;
        //        }else{
        //            listCur=window.pageNo-2;
        //            if(listCur<=1){
        //                listCur=1;
        //            }else if((window.pageNo>$scope.totalPage-3 && listCur>=4) || $scope.totalPage==6){
        //                listCur=$scope.totalPage-4;
        //            }
        //        }
        //        for(var index= 0;index<listCurCount;index++,listCur++){
        //            $scope.pageList.push({num:listCur});
        //        }
        //        //选中的是第几个页码
        //        $scope.pageIndex=window.pageNo;
        //        //是否显示最后一页的页码
        //        $scope.isLastPage=($scope.pageIndex<$scope.totalPage-2) && $scope.totalPage>5;
        //        //是否显示第一页的页码
        //        $scope.isFirstPage=$scope.pageIndex>4 && $scope.totalPage>5;
        //    })
        //}
        //$scope.agentLevelNum = function(num){
        //    if(num=='2'){
        //        return '省级代理商';
        //    }
        //    if(num=='3'){
        //        return '市级代理商';
        //    }
        //    if(num=='4'){
        //        return '县级代理商';
        //    }
        //    if(window.isEmpty(num)){
        //        return '服务中心';
        //    }
        //
        //}
        //$scope.clearSellectData = function(){
        //    $scope.initArea();
        //    window.indexNum = 0;
        //    $scope.agentName = "";
        //    $scope.agentId = "";
        //    $scope.filter = {startTime:"",endTime:"",pageNo:1,pageSize:30,
        //        areaValue:$scope.agentAreaValue[$scope.agentAreaValue.length - 1]};
        //    window.pageNo = 1;
        //    window.pageSize = 30;
        //    $scope.queryCurrentList();
        //}
        //$scope.initArea();
    });
})(angular);
