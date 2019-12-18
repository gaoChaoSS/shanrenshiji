(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;

        $scope.tempGridList = '/view/count/t_memberTradeRank_grid.jsp';
        $scope.tempGridFilter = '/view/count/t_sellerTradeRank_filter.jsp';
        $scope.windowInfo = '/view/count/t_memberTradeRank_info.jsp';

        $scope.entityTitle = "会员交易排行";
        //$scope.windowInfo = '/view/user/t_memberInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getSellerTradeRank";

        $scope.memberOrderCount=function(){
            $scope.popWindowTemp = "/view/count/t_memberTradeRank_count_grid.jsp";
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '会员交易统计';

            var url = window.basePath+"/order/OrderInfo/getMemberOrderCount";
            $http.get(url).success(function(re){
                $scope.orderCount = re.content;
                $scope.getGroupOrder();
            });
        };

        //对数据进行处理,json转数组
        $scope.getGroupOrder=function(){
            $scope.countList = new Array(6);
            $scope.maxCount = 0;//以最大数为基数去算百分比
            var index=0;
            for(var order in $scope.orderCount){
                $scope.countList[index] = {
                    key:order,
                    num:$rootScope.getMoney($scope.orderCount[order]),
                    scaleNum:0
                };
                if($scope.maxCount<parseFloat($scope.countList[index].num)){
                    $scope.maxCount=parseFloat($scope.countList[index].num);
                }
                index++;
            }
            for(var i= 0,len=$scope.countList.length;i<len;i++){
                $scope.countList[i].scaleNum=$rootScope.getMoney($scope.countList[i].num/$scope.maxCount*100);
            }
        };

        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);

            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name=='查看报表统计'){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: "会员交易统计", action: $scope.memberOrderCount
            });
        };

        $scope.setUserType=function(){
            $scope.filter._userType="Member";
        };
        initGrid($rootScope, $scope, $http);

        $scope.isCashOrder=false;
        $scope.showOtherData=function(){
            incomeGrid($rootScope, $scope, $http);
        };

        $scope.setCashOrder=function(flag){
            $scope.isCashOrder=flag;
            $scope.init();
        };
    });
})(angular);
