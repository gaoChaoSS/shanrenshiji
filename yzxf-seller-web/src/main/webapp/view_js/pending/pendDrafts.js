(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'UserPending';
        $scope.entityTitle="草稿箱";
        $scope.tempGridList = '/view/pending/t_pendDrafts_grid.jsp';
        $scope.windowInfo = '/view/user/t_agentAdd_grid.jsp';
        $scope.tempGridFilter = '/view/pending/t_pendDrafts_grid_filter.jsp';
        $scope.apiName="getPendDrafts";

        $scope.isDel=false;

        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name=='删除'){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: "删除", action: $scope.delData
            });

        }

        $scope.getBillAccount=function(){
            $scope.delList=$scope.dataPage.items;
            $scope.selectThName="全选";
        }

        initGrid($rootScope, $scope, $http);

        $scope.delFun=function(){
            var count=0;
            for(var i =0,len=$scope.delList.length;i<len;i++) {
                if ($scope.delList[i].$$selectedItem) {
                    count++;
                }
            }
            if(count==0){
                malert("请选择要删除的草稿");
                return;
            }
            $scope.isDel=true;
        }



        $scope.showOtherData = function () {
            $scope.showImg='';
            var url=window.basePath + "/account/UserPending/show?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content.text;
                $scope.pendingId=re.content._id;
                if($scope.dataPage.$$selectedItem.ownerType=="Seller"){
                    modifySellerGrid($rootScope, $scope, $http);
                    $scope.popWindowTemp = '/view/user/t_sellerAdd_grid.jsp';
                }else if($scope.dataPage.$$selectedItem.ownerType=="Factor"){
                    modifyFactorGrid($rootScope, $scope, $http);
                    $scope.popWindowTemp = '/view/user/t_factorAdd_grid.jsp';
                }else if($scope.dataPage.$$selectedItem.ownerType=="Agent"){
                    modifyAgentGrid($rootScope, $scope, $http);
                    $scope.popWindowTemp = '/view/user/t_agentAdd_grid.jsp';
                }
            });
        }

        $scope.delSubmit=function(){
            $scope.isDel=false;
            var delStr = "";
            var len=$scope.delList.length;
            for(var i=0;i<len;i++){
                if($scope.delList[i].$$selectedItem){
                    delStr+="_"+$scope.delList[i]._id;
                }
            }
            if(delStr.length>1){
                delStr=delStr.substring(1,delStr.length);
            }else{
                return;
            }
            var url=window.basePath + "/account/UserPending/delUserPending?idList=" + delStr;
            $http.get(url).success(function () {
                $scope.delSuccess=true;
                $scope.closePopWin();
                $scope.queryCurrentList();
            });
        }

        $scope.closeDel=function(){
            $scope.isDel=false;
            $scope.delSuccess=false;
        }


        $scope.getOwnerType=function(type){
            if(type=='Seller'){
                return '商户';
            }else if(type=='Agent'){
                return '代理商';
            }else if(type=='Factor'){
                return '服务站';
            }
        }
        $scope.getStatus=function(status){
            if(status=='1'){
                return '待审';
            }else if(status=='2'){
                return '通过';
            }else if(status=='3'){
                return '不通过';
            }
        }
        $scope.getStatusClass=function(status){
            if(status=='1'){
                return 'colorGray888';
            }else if(status=='2'){
                return 'colorGreen1';
            }else if(status=='3'){
                return 'colorRed2';
            }
        }
        $scope.selectAll=function(){
            if($scope.delList==null){
                return;
            }
            for(var i= 0,len=$scope.delList.length;i<len;i++){
                $scope.delList[i].$$selectedItem=($scope.selectThName=="全选");
            }
            if($scope.selectThName=='全选'){
                $scope.selectThName='全不选';
            }else{
                $scope.selectThName='全选';
            }
        }
    });
})(angular);
