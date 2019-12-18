(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/account/t_payLog_grid.jsp';
        $scope.tempGridFilter = '/view/account/t_payLog_filter.jsp';

        $scope.entityTitle = "第三方支付记录";
        $scope.windowInfo = '/view/account/t_tradeInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/queryPayLog";

        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            $scope.addBtn('统计',$scope.customBtn);
        };

        $scope.addBtn=function(name,action){
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name===name){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: name, action: action
            });
        };

        initGrid($rootScope, $scope, $http);
        $scope.isInitBill=true;
        initTrade($rootScope, $scope, $http);
        $scope.checkBtn=[false,false,false];

        $scope.orderTypeList = [
            {name:'会员扫码',type:0},
            {name:'现金交易',type:1},
            {name:'互联网收款',type:2},
            {name:'商家充值',type:3},
            {name:'发卡点充值',type:4},
            {name:'会员充值',type:5},
            {name:'会员代充值',type:6},
            {name:'服务站激活会员卡',type:7},
            {name:'会员端激活会员卡',type:8},
            {name:'会员在线购买',type:11}
        ];

        $scope.customFun=function(){
            $scope.popWindowTemp = '/view/account/t_payLog_count_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '统计';
            $scope.countFilter = {
                startTime:new Date(new Date().setHours(0,0,0,0)),
                endTime:new Date(new Date().setHours(23,59,59,999))
            };

            $scope.getCountPay();
        };

        // 判断统计筛选时间
        $scope.checkTime=function(){
            if(!window.isEmpty($scope.countFilter.startTime) && !window.isEmpty($scope.countFilter.endTime)){
                if($scope.countFilter.startTime.getTime()>$scope.countFilter.endTime.getTime()){
                    $scope.countFilter.startTime=new Date($scope.countFilter.endTime.setHours(0,0,0,0));
                    $scope.countFilter.endTime.setHours(23,59,59,999);
                }
            }
        };

        // 获取统计
        $scope.getCountPay=function(){
            var url = window.basePath + "/payment/Pay/countPay?1=1";
            if(!window.isEmpty($scope.countFilter.startTime)){
                url+="&startTime="+$scope.countFilter.startTime.getTime();
            }
            if(!window.isEmpty($scope.countFilter.endTime)){
                url+="&endTime="+$scope.countFilter.endTime.getTime();
            }
            $http.get(url).success(function (re) {
                $scope.rawItem = re.content;
                $scope.formatCountPay();
            });
        };

        // 格式化统计内容
        $scope.formatCountPay=function(){
            if(window.isEmpty($scope.rawItem)){
                return;
            }
            $scope.countItem = [
                {
                    title:'支付宝',
                    count:$scope.rawItem.alipayCount,
                    totalPrice:$scope.rawItem.alipay,
                    list:[]
                },{
                    title:'微信',
                    count:$scope.rawItem.wechatCount,
                    totalPrice:$scope.rawItem.wechat,
                    list:[]
                }
            ];
            $scope.formatPayList(0,'alipay');
            $scope.formatPayList(1,'wechat');
        };

        $scope.formatPayList=function(index,entity){
            for (var i=0,len=$scope.orderTypeList.length;i<len;i++){
                $scope.countItem[index].list.push({
                    name:$scope.orderTypeList[i].name,
                    type:$scope.orderTypeList[i].type,
                    count:$scope.rawItem[entity+$scope.orderTypeList[i].type+'Count'],
                    totalPrice:$scope.rawItem[entity+$scope.orderTypeList[i].type]
                });
            }
        };

        $scope.payResults = function(status,payReturn){
            if(payReturn==='SUCCESS'){
                return '已退款';
            }else{
                if(status=='START'){
                    return '未支付';
                }else if(status=='SUCCESS'){
                    return '成功';
                }else if(status=='FAIL'){
                    return '失败';
                }
            }
        };

        $scope.payResultsClass=function(status,payReturn){
            if(payReturn==='SUCCESS'){
                return 'colorRed1';
            }else{
                if(status=='START'){
                    return 'colorGray888';
                }else if(status=='SUCCESS'){
                    return 'colorGreen1';
                }else if(status=='FAIL'){
                    return 'colorRed1';
                }
            }
        };

        $scope.getPayType = function(type){
            if(type=='4' || type=='14') {
                return '支付宝';
            }else if(type=='10' || type=='13'){
                return '微信';
            }else if(type=='3'){
                return '余额支付';
            }
        };
        $scope.getPayName = function(type,m,s,f){
            if(/^[0568]$/.test(type) || /^(11)$/.test(type)){
                return m;
            }else if(/^[13]$/.test(type)){
                return s;
            }else if(/^[47]$/.test(type)){
                return f;
            }else if(/^[2]$/.test(type)){
                return '非会员';
            }
        };
        $scope.getPayNameType = function(type){
            if(/^[0568]$/.test(type) || /^(11)$/.test(type)){
                return '会员';
            }else if(/^[13]$/.test(type)){
                return '商家';
            }else if(/^[47]$/.test(type)){
                return '服务站';
            }else if(/^[2]$/.test(type)){
                return '非会员';
            }
        };

        $rootScope.showYFullTime = function (time) {
            return new Date(time).showYFullTime();
        };

        $scope.isNullNumber = function (num) {
            if (window.isEmpty(num)) {
                return 0;
            }
            return num;
        };

        $scope.setPayId=function(pay){
            $scope.selectedPay=pay;
        }

        $scope.checkBtnFun=function(index){
            for(var i= 0,len=$scope.checkBtn.length;i<len;i++){
                $scope.checkBtn[i]=i==index;
            }
            if(!window.isEmpty($scope.selectedPay.trId)){
                $scope.selectedPay.trId='';
            }
        }

        $scope.checkShowBtn = function(){
            return $scope.isInitBill && $scope.selectedPay && ($scope.selectedPay.payStatus=='START'
                || ($scope.selectedPay.payStatus=='SUCCESS' && !/^(9)|(100)$/.test($scope.selectedPay.orderStatus)
                    && !/^(9)|(10)|(12)|(13)|(14)$/.test($scope.selectedPay.orderType)))
        }

        $scope.submitForm=function(){
            if(!$scope.checkShowBtn()){
                malert("仅支持补全未支付的订单");
                return;
            }
            if($scope.checkBtn[2] && window.isEmpty($scope.selectedPay.trId)){
                malert("请输入第三方订单号");
                return;
            }
            var url = window.basePath + "/order/OrderInfo/updateOrderStatusByThird?payId="+$scope.selectedPay.payId;
            if(!window.isEmpty($scope.selectedPay.trId)){
                url+="&trId="+$scope.selectedPay.trId;
            }

            $http.get(url).success(function () {
                $scope.checkBtnFun(1);
                $scope.queryCurrentList();
            });
        }

        $scope.closeWin = function () {
            $scope.checkBtn = [false, false,false];
            $rootScope.showPopWin = false;
            $scope.closePopWin();
        }


    });
})(angular);
