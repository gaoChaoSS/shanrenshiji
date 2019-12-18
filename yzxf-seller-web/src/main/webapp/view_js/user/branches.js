/**
 * Created by zq2014 on 17/7/24.
 */
(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'Branches';
        $scope.entityTitle="分支机构管理";
        $scope.tempGridList = '/view/user/t_branches_grid.jsp';
        $scope.windowInfo = '/view/user/t_branchesInfo_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_branches_grid_filter.jsp';
        $scope.fullQueryApi = window.basePath + "/account/Factor/getBranches";
        $scope.pendingBtnCheck=false;
        $scope.isAdd=false;
        $scope.isModify=false;
        $scope.typeList=['城市',"分支机构"];
        $scope.checkBtn = [false, false, false,false];
        $scope.selectType={name:''};
        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            $scope.removeActionList("新增");
            $scope.removeActionList("修改");
            $scope.removeActionList("删除");
            $scope.windowAdd=$scope.windowInfo;
            $scope.actionList.push({
                name: "新增", action: $scope.addData
            });
            $scope.actionList.push({
                name: "修改", action: $scope.modifyData
            });
            $scope.actionList.push({
                name: "删除", action: $scope.delData
            });
        };


        $scope.removeActionList=function(text){
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name==text){
                    $scope.actionList.remove(i);
                    break;
                }
            }
        };

        //新增 初始化
        $scope.addInit=function(){
            $scope.isAdd=true;
            $scope.isModify=false;
            $scope.hideSubmit=false;

            $scope.getCityList();

            $scope.initBranch();
            if(window.isEmpty($scope.selectType.name)){
                $scope.selectType.name=$scope.typeList[0];
            }
        };

        $scope.modifyInfo=function(){
            $scope.getBranch();
            $scope.isModify=true;
            $scope.isAdd=true;
            $scope.hideSubmit=false;

        };

        initGrid($rootScope, $scope, $http);

        $scope.initBranch=function(){
            $scope.userInfo={
                pid:'',
                name:'',
                mobile:'',
                address:''
            };
        };

        //查看详情
        $scope.showOtherData = function () {
            $scope.isAdd=false;
            $scope.isModify=false;
            $scope.userInfo={};
            $scope.hideSubmit=true;
            $scope.getBranch();
        };

        //获取详细信息
        $scope.getBranch=function(){
            var url = window.basePath + "/account/Factor/getBranches?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content.items[0];
                if($scope.userInfo.pid==-1){
                    $scope.selectType.name=$scope.typeList[0];
                }else{
                    $scope.getCityList();
                    $scope.selectType.name=$scope.typeList[1];
                }
            });
        };

        //获取城市列表
        $scope.selectCity={};
        $scope.getCityList = function(){
            var url = window.basePath + "/account/Factor/getBranches?_pid=-1&pageNo=1&pageSize=9999";
            $http.get(url).success(function (re) {
                $scope.cityList=re.content.items;
                if(!window.isEmpty($scope.userInfo.pid)){
                    for(var i= 0,len=$scope.cityList.length;i<len;i++){
                        if($scope.cityList[i]._id==$scope.userInfo.pid){
                            $scope.selectCity._id=$scope.cityList[i]._id;
                        }
                    }
                }else{
                    $scope.selectCity._id=$scope.cityList[0]._id;
                }
            });
        };
        //修改或新增
        $scope.modifyBranches=function(){
            if($scope.selectType.name=='城市'){
                $scope.userInfo.pid=-1;
            }else{
                $scope.userInfo.pid=$scope.selectCity._id;
            }
            if($scope.selectType.name=='分支机构' && window.isEmpty($scope.userInfo.pid)){
                malert("请选择归属城市!");
                return;
            }
            if(window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length>100){
                malert("名称在100位字符以内!");
                return;
            }
            if($scope.selectType.name=='分支机构' && (window.isEmpty($scope.userInfo.mobile) || $scope.userInfo.mobile.length>20)){
                malert("联系方式在20位字符以内!");
                return;
            }
            if($scope.selectType.name=='分支机构' && (window.isEmpty($scope.userInfo.address) || $scope.userInfo.address.length>200)){
                malert("地址在300位字符以内!");
                return;
            }
            var url = window.basePath + "/account/Factor/modifyBranches";
            $http.post(url,$scope.userInfo).success(function () {
                $scope.checkBtnFun(2);
                $scope.queryCurrentList();
            });
        };

        //选择删除
        $scope.delFun=function(){
            if(window.isEmpty($scope.dataPage) || window.isEmpty($scope.dataPage.$$selectedItem) ||
                window.isEmpty($scope.dataPage.$$selectedItem._id)){
                malert("请选择要删除的草稿");
                return;
            }
            $scope.checkBtnFun(3);
        };
        //删除
        $scope.delSubmit=function(){
            var url = window.basePath + "/account/Factor/delBranches?_id="+$scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function () {
                $scope.checkBtnFun(2);
                $scope.checkBtnFun(3);
                $scope.queryCurrentList();
            });
        };

        $scope.checkBtnFun = function (index) {
            $scope.checkBtn[index] = !$scope.checkBtn[index];
        };

        $scope.closeWin = function () {
            $scope.checkBtn = [false, false, false,false];
            $rootScope.showPopWin = false;
            $scope.selectCity={};
            $scope.selectType={name:''};
        }
    });
})(angular);
