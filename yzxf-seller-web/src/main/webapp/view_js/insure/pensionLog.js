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

        $scope.tempGridList = '/view/insure/t_pensionLog_grid.jsp';
        $scope.tempGridFilter = '/view/insure/t_pensionLog_filter.jsp';

        $scope.entityTitle = "养老金投保明细";
        //$scope.windowInfo = '/view/user/t_memberInfo_grid.jsp';

        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getInsure";
        // $scope.windowInfo = '/view/insure/t_accidentLog_info_grid.jsp';

        $scope.getAgentAction = function () {
            // $scope.actionList || ($scope.actionList = []);
            // for(var i =0;i<$scope.actionList.length;i++){
            //     if($scope.actionList[i].name==='变更为已投保'){
            //         $scope.actionList.remove(i);
            //         break;
            //     }
            // }
            // $scope.actionList.push({
            //     name: "变更为已投保", action: $scope.customBtn
            // });
            $scope.actionList=[{name: "变更为已投保", action: $scope.customBtn}
                            // ,{name: "查看", action: $scope.showYFullTime}
                            ];
        };

        initGrid($rootScope, $scope, $http);

        $rootScope.showYFullTime = function (time) {
            return new Date(time).showYFullTime();
        }

        $scope.isNullNumber=function(num){
            if(window.isEmpty(num)){
                return 0;
            }
            return num;
        }
        $scope.getInsureStatus = function(status){
            return status?'已投':'未投';
        }
        $scope.insureType = function(type){
            if(type=='1'){
                return '在线';
            }
            if(type=='2'){
                return '线下';
            }
        }
        $scope.getPensionMoneyStatistics = function(){
            var url = window.basePath + '/order/OrderInfo/getInsureMoneySum?1=1';
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
                $scope.totalUse = re.content.items[0].totalUse;
                $scope.totalNot = re.content.items[0].totalNot;
            })
        }

        $scope.showOtherData = function () {
            $scope.checkSubmit=false;
            $scope.orderInfo = {
                _id:$scope.dataPage.$$selectedItem._id,
                insureNO:$scope.dataPage.$$selectedItem.insureNO,
                company:$scope.dataPage.$$selectedItem.company,
                table:"MemberPensionLog"
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
        };

        $scope.getBillAccount=function(){
            $scope.selectThName="全选";
        }
        $scope.selectAll=function(){
            if($scope.dataPage.items==null){
                return;
            }
            for(var i= 0,len=$scope.dataPage.items.length;i<len;i++){
                if($scope.dataPage.items[i].insureStatus!=2){
                    $scope.dataPage.items[i].$$selectedSubmit=false;
                }else{
                    $scope.dataPage.items[i].$$selectedSubmit=($scope.selectThName=="全选");
                }
            }
            if($scope.selectThName==='全选'){
                $scope.selectThName='全不选';
            }else{
                $scope.selectThName='全选';
            }
        }
        //获得已选中的，拼接为字符串
        $scope.getSelected=function(){
            var str = "";
            $.each($scope.dataPage.items,function(k,v){
                 if(v.$$selectedSubmit){
                     str+=";"+v._id;
                 }
            });
            if(window.isEmpty(str)){
                return '';
            }
            return str.substring(1);
        }

        $scope.customFun=function(){
            var selected = $scope.getSelected();
            if(window.isEmpty(selected)){
                malert('请选择勾选变更数据');
                return;
            }
            if(!confirm("是否变更已选择的数据(未投保0元的数据将不会变更)")){
                return;
            }
            var url = window.basePath + "/order/OrderInfo/manualInsure";
            var data={
                _id:selected
            }
            $http.post(url,data).success(function(){
                malert("更改成功");
                $scope.queryCurrentList();
            });
        }


        //导出excel文件
        $scope.createTradeExcel = function (){
            $rootScope.getExcel("/view/download/pensionLog_Download.jsp",$scope.filter);
        };
    });
})(angular);
