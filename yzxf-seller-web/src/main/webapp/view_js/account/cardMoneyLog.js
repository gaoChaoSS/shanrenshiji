(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $scope.tempGridList = '/view/account/t_cardMoneyLog_grid.jsp';
        $scope.tempGridFilter = '/view/account/t_cardMoneyLog_filter.jsp';
        $scope.entityTitle = "会员激活记录";
        $scope.windowInfo = '/view/account/t_tradeInfo_grid.jsp';
        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getCardMoneyLog";
        initGrid($rootScope, $scope, $http);

        //例如：1转账、2充值、3线上消费、4现金交易 5.会员扫码
        $scope.getTradeType = function (type) {
            if (type == "1") {
                return '转账';
            } else if (type == "2") {
                return '充值';
            } else if (type == "3") {
                return '线上交易';
            } else if (type == "4") {
                return '现金交易';
            } else if (type == "5") {
                return '会员扫码';
            }
        }

        $rootScope.showYFullTime = function (time) {
            return new Date(time).showYFullTime();
        }

        $scope.isNullNumber = function (num) {
            if (window.isEmpty(num)) {
                return 0;
            }
            return num;
        }
        $scope.agentLevelNum = function (num) {
            if (num == '1') {
                return '平台';
            }
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

        $scope.showOtherData = function () {
            $scope.showImg='';
            var url = window.basePath + '/order/OrderInfo/getCardMoneyDistribution?orderNo=' + $scope.dataPage.$$selectedItem.orderNo;
            $http.get(url).success(function (re) {
                $scope.$$agentLog = re.content.agent;

                $scope.getModel($scope.dataPage.$$selectedItem.orderType);

                $scope.$$agentLogList = [{}, {}, {}, {}];//平台
                if ($scope.$$agentLog == null || $scope.$$agentLog.length < 1) {
                    return;
                }
                var countMoney=0;
                var logLen =$scope.$$agentLog.length;
                for (var i = 0; i < 4; i++) {
                    var obj = $scope.$$agentLog[i];
                    if (obj == null) {
                        obj = {orderCash: 0.0};
                    }
                    if(i<logLen){
                        countMoney+=$scope.$$agentLog[i].orderCash;
                    }
                    $scope.$$agentLogList[i] = obj;
                }
                $scope.factor = re.content.factor;
            });
        }

        //获取详情显示的模块
        $scope.getModel=function(orderType){
            //利润,会员卡分润,会员,商家,发卡点,会员朋友
            $scope.modelList={profit:false,card:false,member:false,seller:false,factor:false,friend:false};
            $scope.userList=[];
            if(/^[01]$/.test(orderType)){
                $scope.modelList.profit=true;//利润
            }
            if(/^[78]$/.test(orderType)){
                $scope.modelList.card=true;//会员卡分润
            }
            if(/^[015678]|(14)$/.test(orderType)){
                $scope.modelList.member=true;//会员
                $scope.getUserInfo(0,"/crm/Member/show?_id="+$scope.dataPage.$$selectedItem.memberId);
                $scope.getBelong(0,$scope.dataPage.$$selectedItem.belongValueMember);
            }
            if(/^[01239]$/.test(orderType)){
                $scope.modelList.seller=true;//商家
                $scope.getUserInfo(1,"/account/Seller/getSellerInfoById?sellerId="+$scope.dataPage.$$selectedItem.sellerId);
                $scope.getBelong(1,$scope.dataPage.$$selectedItem.belongValueSeller);
            }
            if(/^(4)|(7)|(10)$/.test(orderType)){
                $scope.modelList.factor=true;//发卡点
                $scope.getUserInfo(2,"/account/Factor/getFactorById?factorId="+$scope.dataPage.$$selectedItem.factorId);
                $scope.getBelong(2,$scope.dataPage.$$selectedItem.belongValueFactor);
            }
            if(/^[6]$/.test(orderType)){
                $scope.modelList.friend=true;//会员朋友
                $scope.getUserInfo(3,"/crm/Member/show?_id="+$scope.dataPage.$$selectedItem.friendId);
                $scope.getBelong(3,$scope.dataPage.$$selectedItem.belongValueFriend);
            }
        }
        $scope.getUserInfo=function(index,api){
            $http.get(window.basePath+api).success(function(re){
                $scope.userList[index]=re.content;
            });
        }

        $scope.getBelong=function(index,belongValue){
            if(window.isEmpty(belongValue)){
                return;
            }
            var url = window.basePath + "/account/Agent/getAgentAreaValueById?areaValue=" + belongValue;
            $http.get(url).success(function (re) {
                $scope.userList[index].belongArea = re.content.agentNameAll;
            });
        }
    });
})(angular);
