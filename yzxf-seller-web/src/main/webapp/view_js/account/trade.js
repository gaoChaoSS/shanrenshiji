/**
 * Created by tianchangsen on 17/1/23.
 */
(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $scope.tempGridList = '/view/account/t_trade_grid.jsp';
        $scope.tempGridFilter = '/view/account/t_trade_filter.jsp';
        $scope.entityTitle = "交易流水";
        $scope.windowInfo = '/view/account/t_tradeInfo_grid.jsp';
        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getBill";

        $scope.setUserType=function(){
            $scope.filter._notOrderStatus='0,1';
        };

        $scope.showBalance = function(){
            initBalance($rootScope, $scope, $http);
        }

         $scope.getAgentAction = function () {
             $scope.actionList || ($scope.actionList = []);
             if (/^[1]$/.test($rootScope.agent.level)) {
                 $scope.actionList.push({
                     name: "生成对账文件", action: $scope.showBalance
                 });
             }
         };

        initGrid($rootScope, $scope, $http);
        initTrade($rootScope, $scope, $http);

        $scope.customFun=function(){
            $scope.popWindowTemp = '/view/user/t_memberCard_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '交易统计';

        };


        //交易统计
        $scope.getCountOrder=function(){

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

        $scope.drawback = function(){
            $rootScope.getTool({
                title:'是否退款',
                desc:'退款操作不可逆（目前仅支持"现金交易退款"和"错误的互联网收款"）',
                exec:function(){
                    var url = window.basePath + '/order/OrderInfo/drawbackOrderByRelate';
                    var data = {
                        orderId:$scope.dataPage.$$selectedItem._id
                    };
                    $http.post(url,data).success(function(){
                        malert("退款成功");
                        $rootScope.tool={};
                        $scope.dataPage.$$selectedItem.orderStatus=9;
                    })
                }
            });


        };

        $scope.getBillAccount= function () {
            var url = window.basePath + '/order/OrderInfo/getTradeSum?1=1';
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
                if(re!=null){
                    $scope.tradeSum = re.content.items[0].tradeMoneySum;
                }
            })
            $scope.totalPricePage = $rootScope.formatCountField($scope.dataPage.items,"payMoney");
        };

        //导出excel文件
        $scope.createTradeExcel = function (){
            var url = "/view/download/downLoad.jsp?1=1";
            $.each($scope.filter, function (k, v) {
                if (k == 'startTime' || k == 'endTime') {
                    return true;
                }
                if (window.isEmpty(v)) {
                    return true;
                }
                if (k == "_areaValue" && ($scope.entity == 'Member' || $scope.entity == 'Seller')) {
                    url += '&_belongAreaValue=' + v;
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
            if ($scope.entity != 'Member') {
                url += '&_applyTime=___notnull';
            }
            window.location.href=url;
        };

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

    });
})(angular);
