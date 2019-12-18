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
        $scope.tempGridList = '/view/insure/t_accidentLog_grid.jsp';
        $scope.tempGridFilter = '/view/insure/t_pensionLog_filter.jsp';
        $scope.entityTitle = "意外险投保明细";
        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getAccidentLog";
        $scope.windowInfo = '/view/insure/t_accidentLog_info_grid.jsp';

        initGrid($rootScope, $scope, $http);

        $scope.showOtherData = function () {
            $scope.checkSubmit=false;
            $scope.orderInfo = {
                _id:$scope.dataPage.$$selectedItem._id,
                insureNO:$scope.dataPage.$$selectedItem.insureNO,
                company:$scope.dataPage.$$selectedItem.company,
                table:"MemberAccidentLog"
            };
            if(window.isEmpty($scope.orderInfo._id)){
                malert("获取投保单失败");
                return;
            }
            if(window.isEmpty($scope.orderInfo.company) || window.isEmpty($scope.orderInfo.insureNO)){
                $scope.titleCheck="请补全投保单"
            }else{
                $scope.titleCheck="投保信息"
            }
            var url = window.basePath + "/crm/Member/show?_id="+$scope.dataPage.$$selectedItem.memberId;
            $http.get(url).success(function(re){
                $scope.userInfo=re.content;
            });
        }

        $scope.setAccidentLog=function(){
            if(window.isEmpty($scope.orderInfo.company) || window.isEmpty($scope.orderInfo.insureNO)){
                malert("请补全保单信息后提交");
                return;
            }
            var url = window.basePath + "/order/OrderInfo/updateAccidentLog";
            $http.post(url,$scope.orderInfo).success(function(re){
                $scope.userInfo=re.content;
                $scope.closePopWin();
                $scope.queryCurrentList();
            });
        }

        $scope.isNullNumber=function(num){
            if(window.isEmpty(num)){
                return 0;
            }
            return num;
        }

        $scope.isNullCheck=function(text){
            return window.isEmpty(text);
        }

        $scope.checkWin=function(){
            $scope.checkSubmit=!$scope.checkSubmit;
        }

        //导出excel文件
        $scope.createTradeExcel = function (){
            var url = "/view/download/accidentLog_Download.jsp?1=1";
            $.each($scope.filter, function (k, v) {
                if (k == 'startTime' || k == 'endTime') {
                    return true;
                }
                if (window.isEmpty(v)) {
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
            window.location.href=url;
        };

    });
})(angular);
