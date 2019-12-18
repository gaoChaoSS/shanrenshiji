(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'UserPending';
        $scope.entityTitle="审批历史";
        $scope.tempGridList = '/view/pending/t_pendRecord_grid.jsp';
        $scope.windowInfo = '/view/user/t_agentInfo_grid.jsp';
        $scope.tempGridFilter = '/view/pending/t_pendRecord_grid_filter.jsp';
        $scope.fullQueryApi = window.basePath + "/account/UserPending/getPendRecord";
        initGrid($rootScope, $scope, $http);

        $scope.showPendingData = function () {
            $scope.showPendCheck=false;
            $scope.pendRecordSubmitCheck=false;
            if($scope.entityTitle=='审批历史' && $scope.dataPage.$$selectedItem.status==3){
                $scope.showPendCheck=true;
                if($scope.agent._id!=$scope.dataPage.$$selectedItem.createId){
                    $scope.showPendText=$scope.dataPage.$$selectedItem.create+",";
                }else{
                    $scope.showPendText='您';
                }
                $scope.showPendText+='将可以在草稿箱里重新编辑提交';
            }
            $scope.showImg='';
            var url=window.basePath + "/account/UserPending/show?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.verifyInfo=re.content;
                $scope.userInfo=re.content.text;
                $scope.openWeek = ($scope.userInfo.openWeek == null ? '' : '周' + $scope.userInfo.openWeek) + ($scope.userInfo.openTime == null ? '' : (',' + $scope.userInfo.openTime + ':00' + ($scope.userInfo.closeTime == null ? '' : '至' + $scope.userInfo.closeTime + ':00')));
                $scope.address = ($scope.userInfo.area == null ? '' : $scope.userInfo.area) + ($scope.userInfo.address == null ? '' : $scope.userInfo.address);
                $scope.integralRate = window.isEmpty($scope.userInfo.integralRate) ? '0%' : $scope.userInfo.integralRate + '%';
                $scope.imgMore={
                    bankImg: $scope.userInfo.bankImg.split("_"),
                    contractImg:$scope.userInfo.contractImg.split("_"),
                }
                if ($scope.verifyInfo.ownerType == 'Seller'){
                    $scope.imgMore.doorImg=$scope.userInfo.doorImg.split("_");
                    $scope.initMap();
                }
            });
        };

        //初始化地图
        $scope.initMap = function () {
            if(window.isEmpty($scope.userInfo.latitude) || window.isEmpty($scope.userInfo.longitude)){
                $("#container").html("");
                return;
            }
            $scope.center = new qq.maps.LatLng($scope.userInfo.latitude, $scope.userInfo.longitude);
            $scope.map = new qq.maps.Map(document.getElementById("container"), {
                center: $scope.center,
                zoom: 18
            });
            var marker = new qq.maps.Marker({
                position: $scope.center,
                map: $scope.map
            });
            $scope.markerCluster = new qq.maps.MarkerCluster({
                map: $scope.map,
                minimumClusterSize: 2, //默认2
                markers: [],
                zoomOnClick: true, //默认为true
                gridSize: 60, //默认60
                averageCenter: true, //默认false
                maxZoom: 16 //默认18
            });
        };

        $scope.submitModifyCheck=function(){
            $scope.pendRecordSubmitCheck=!$scope.pendRecordSubmitCheck;
        }

        $scope.submitModify=function(){
            if($scope.dataPage.$$selectedItem.status!=3){
                malert("您不能转换审核通过的记录");
                return;
            }
            var url=window.basePath + "/account/UserPending/modifyPendRecord?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function () {
                $scope.closePopWin();
                malert("恢复草稿成功");
                $scope.queryCurrentList();
            });
        }

        $scope.getExplain=function(text){
            if(!window.isEmpty(text) && text.length>10){
                return text.substring(0,10);
            }else{
                return text;
            }
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
            if(/^(0.2)|(1)$/.test(status)){
                return '初审通过';
            }else if(status=='0.3'){
                return '初审不通过';
            }else if(status=='2'){
                return '复审通过';
            }else if(status=='3'){
                return '复审不通过';
            }
        }
        //获取审核人/时间
        $scope.getStatusData=function(status,first,second){
            if(/^(0.2)|(0.3)|(1)$/.test(status)){
                return first;
            }else if(/^[23]$/.test(status)){
                return second;
            }
        };
        $scope.getStatusClass=function(status){
            if(/^(2)$/.test(status)){
                return 'colorGreen1';
            }else if(/^(0.2)|(1)$/.test(status)){
                return 'colorYellow1';
            }else if(/^(0.3)|(3)$/.test(status)){
                return 'colorRed2';
            }
        }

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }

        $scope.closeImgFun=function(){
            $scope.showImg='';
        }
    });
})(angular);
