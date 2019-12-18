(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller('financial_financailReport_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/financial/t_financailReport_grid.jsp';
        $scope.tempGridFilter = '/view/financial/t_financailReport_filter.jsp';
        $scope.settlement_time = '/temp_new/settlement_time.html';
        $scope.entityTitle = "代理商收益结算";
        $scope.windowInfo = '/view/financial/t_financailReportInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getUserEarnings";

        $scope.isSubmit=false;


        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name=='提交到银行'){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: "提交到银行", action: $scope.subData
            });

            if($rootScope.agent.level==1){
                for(var i =0;i<$scope.actionList.length;i++){
                    if($scope.actionList[i].name=='生成上个月收益结算'){
                        $scope.actionList.remove(i);
                        break;
                    }
                }
                $scope.actionList.push({
                    name: "生成上个月收益结算", action: $scope.addData
                });
            }

        };

        $scope.addInit=function(){
            $rootScope.showPopWin = false;
            var url = window.basePath + "/order/OrderInfo/createAgentAccountMonthForWeb";
            $http.get(url).success(function(){
                malert("已生成");
            });
        };

        $scope.setUserType = function(){
            $scope.filter._userType=0;
            $scope.filter._cwCheck=1;
            $scope.startYearTime = "";
            $scope.startMonthTime = "";
            $scope.endYearTime = "";
            $scope.endMonthTime = "";
            $scope.selectedShow=[false,false,false,false];
        }

        initGrid($rootScope, $scope, $http);
        $scope.setUserType();
        $scope.showOtherData=function(){
            incomeGrid($rootScope, $scope, $http);
            $scope.getFyPay($scope.dataPage.$$selectedItem.orderNo,$scope.dataPage.$$selectedItem.month);
            $scope.fyList=$scope.fyList[0];
        };

        //查询转账情况
        $scope.getFyPay = function(orderNo,time){
            var url=window.basePath + "/order/OrderInfo/getFyPay";
            var data = {
                orderNo:orderNo,
                time:time
            };
            $http.post(url,data).success(function(re){
                $scope.fyList = re.content.items;
            })
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
        $scope.getBillAccount=function(){
            $scope.submitList=$scope.dataPage.items;
            $scope.selectThName="全选";
        }

        $scope.selectAll=function(){
            if($scope.submitList==null){
                return;
            }
            for(var i= 0,len=$scope.submitList.length;i<len;i++){
                if($scope.submitList[i].isTransfer){
                    $scope.submitList[i].$$selectedItem=false;
                }else{
                    $scope.submitList[i].$$selectedItem=($scope.selectThName=="全选");
                }
            }
            if($scope.selectThName=='全选'){
                $scope.selectThName='全不选';
            }else{
                $scope.selectThName='全选';
            }
        }

        $scope.submitFun=function(){
            var count=0;
            for(var i =0,len=$scope.submitList.length;i<len;i++) {
                if ($scope.submitList[i].$$selectedItem) {
                    count++;
                }
            }
            if(count==0){
                malert("请选择要提交的代理商");
                return;
            }
            // $scope.isSubmit=true;

            $scope.popWindowTemp = '/view/financial/t_financailReport_submit_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '账单';
        }

        $scope.transferSubmit=function(){
            $scope.isSubmit=false;
            var data = "";
            for(var i=0,len=$scope.submitList.length;i<len;i++){
                if($scope.submitList[i].$$selectedItem){
                    data+="_"+$scope.submitList[i]._id;
                }
            }
            data=data.substring(1,data.length);

            var url=window.basePath + "/order/OrderInfo/agentIncomeSubmit";
            $http.post(url,{idList:data}).success(function (re) {
                $scope.transferList = re.content.items;
                $scope.submitSuccess=true;
                $scope.closePopWin();
            });
        }
        $scope.closeSubmit=function(){
            $scope.isSubmit=false;
            $scope.submitSuccess=false;
        }
        $scope.setFlag = function(entity){
            $scope[entity]=!$scope[entity];
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

    });
})(angular);
