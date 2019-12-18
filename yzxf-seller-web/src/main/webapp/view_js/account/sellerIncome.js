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


        $scope.entityTitle = "商家月收益结算";
        $scope.windowInfo = '/view/account/t_agentIncomeInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getUserEarnings";
        $scope.setUserType=function(){
            $scope.filter._userType=2;

        }

        initGrid($rootScope, $scope, $http);
        $scope.setUserType();
        $scope.showOtherData=function(){
            incomeGrid($rootScope, $scope, $http);
        }
        $scope.getBillAccount=function(){
            $scope.totalIncomeAccount = $rootScope.countField($scope.dataPage.items,"incomeAccount");
            if(!window.isEmpty($scope.totalIncomeAccount) && $scope.totalIncomeAccount!==0){
                $scope.totalIncomeAccount="("+$scope.totalIncomeAccount+")";
            }else{
                $scope.totalIncomeAccount='';
            }

            $scope.totalOrderCash = $rootScope.countField($scope.dataPage.items,"orderCash");
            if(!window.isEmpty($scope.totalOrderCash) && $scope.totalOrderCash!==0){
                $scope.totalOrderCash="("+$scope.totalOrderCash+")";
            }else{
                $scope.totalOrderCash='';
            }
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


    });
})(angular);
