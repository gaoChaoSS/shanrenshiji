(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'UserPending';
        $scope.entityTitle = "待复审";
        $scope.tempGridList = '/view/pending/t_pending_grid.jsp';
        $scope.windowInfo = '/view/user/t_sellerInfo_grid.jsp';
        $scope.tempGridFilter = '/view/pending/t_pending_grid_filter.jsp';
        $scope.apiName = "getPendingList";

        $scope.pendingBtnCheck = false;
        $scope.modifyAreaCheck = false;
        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            if (/^[14]$/.test($rootScope.agent.level)) {
                $scope.pendingBtnCheck = true;
            }
        }

        initGrid($rootScope, $scope, $http);
        $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
        $scope.showPendingData = function () {
            $scope.checkBtn = [false, false, false, false];
            $rootScope.showPopWin = true;
            $scope.showImg='';
            var url = window.basePath + "/account/UserPending/show?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo = re.content.text;
                $scope.verifyInfo = re.content;
                $scope.address = ($scope.userInfo.area == null ? '' : $scope.userInfo.area) + ($scope.userInfo.address == null ? '' : $scope.userInfo.address);
                $scope.integralRate = window.isEmpty($scope.userInfo.integralRate) ? '0%' : $scope.userInfo.integralRate + '%';
                $scope.openWeek = ($scope.userInfo.openWeek == null ? '' : '周' + $scope.userInfo.openWeek) + ($scope.userInfo.openTime == null ? '' : (',' + $scope.userInfo.openTime + ':00' + ($scope.userInfo.closeTime == null ? '' : '至' + $scope.userInfo.closeTime + ':00')));
                $scope.getUserCanUse();
                $scope.isPendingLevel = 5;

                $scope.imgMore={
                    bankImg: $scope.userInfo.bankImg.split("_"),
                    contractImg:$scope.userInfo.contractImg.split("_"),
                }
                if ($scope.verifyInfo.ownerType == 'Agent') {
                    $scope.isPendingLevel = 3;
                } else if ($scope.verifyInfo.ownerType == 'Factor') {
                    $scope.isPendingLevel = 4;
                } else if ($scope.verifyInfo.ownerType == 'Seller'){
                    $scope.imgMore.doorImg=$scope.userInfo.doorImg.split("_");
                    $scope.initMap();
                }
                if( typeof($scope.userInfo.canUse) != "boolean"){
                    $scope.userInfo.canUse=true;
                }
                if($scope.entityTitle == '待复审' && $scope.verifyInfo.status=='1' && /^[14]$/.test($rootScope.agent.level)){
                    $scope.isShowContract=true;
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

        $scope.uploadFile = function (inputObj, type) {
            if (type == 'bankImg' || type == 'contractImg' || type == 'doorImg') {
                if($scope.imgMore[type].length>11){
                    malert("上传图片不能超过十张!");
                    return;
                }
            }
            var entityId=$scope.userInfo._id;
            window.uploadWinObj = {
                one: true,
                entityName: $scope.verifyInfo.ownerType,
                entityField: type,
                entityId: entityId,
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        if (type == 'bankImg' || type == 'contractImg' || type == 'doorImg') {
                            $scope.imgMore[type].push(options.fileId);
                        } else{
                            $scope.userInfo[type] = options.fileId;
                        }
                    });
                }
            };
            window.uploadWinObj.files = inputObj.files;
            window.uploadFile();
        };

        $scope.delFileItemMore = function (entityField,index) {
            $scope.imgMore[entityField].remove(index);
        };


        $scope.getUserCanUse = function () {
            var url = window.basePath + "/account/UserPending/getUserCanUse?userId=" + $scope.userInfo._id + "&userType=" + $scope.verifyInfo.ownerType;
            $http.get(url).success(function (re) {
                $scope.userCanUse = re.content.canUse;
            });
        }

        $scope.getOwnerType = function (type) {
            if (type == 'Seller') {
                return '商户';
            } else if (type == 'Agent') {
                return '代理商';
            } else if (type == 'Factor') {
                return '服务站';
            }
        }

        $scope.checkBtnFun = function (index) {
            $scope.checkBtn[index] = !$scope.checkBtn[index];
        }

        //格式化图片编码
        $scope.formatImgMore=function(fileName){
            if ($scope.imgMore[fileName]!=null){
                $scope.userInfo[fileName]=$scope.imgMore[fileName].join("_");
                if($scope.userInfo[fileName].substring(0,1)=="_"){
                    $scope.userInfo[fileName]=$scope.userInfo[fileName].substring(1,$scope.userInfo[fileName].length);
                }
            }
        }

        $scope.submitForm = function (status) {
            if ($scope.checkBtn[2] && (window.isEmpty($scope.userInfo.explain) || $scope.userInfo.explain.length>200)) {
                malert("请填写审核不通过的原因,且在200字以内");
                return;
            }
            if(!$scope.checkBtn[2]){
                if($scope.verifyInfo.ownerType!="Seller"){
                    if ($scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
                        malert("请上传1~10张合同照片!");
                        return;
                    }
                    $scope.formatImgMore("contractImg");
                }

                var belongValue = $scope.filter._areaValue;
                if((window.isEmpty($scope.userInfo.belongAreaValue) && $scope.verifyInfo.ownerType == "Seller") ||
                    (window.isEmpty($scope.userInfo.areaValue) && $scope.verifyInfo.ownerType != "Seller")){
                    if(window.isEmpty($scope.userInfo.belongAreaValue) || window.isEmpty($scope.userInfo.areaValue)){
                        belongValue = belongValue.substring(10, belongValue.length - 1).replace(/\\/g, "");
                        if ($scope.verifyInfo.ownerType == "Seller" && belongValue.split("_").length != 5) {
                            malert("请选择归属服务站");
                            return;
                        } else if ($scope.verifyInfo.ownerType == "Factor" && belongValue.split("_").length != 4) {
                            malert("请选择县级归属代理商");
                            return;
                        } else if ($scope.verifyInfo.ownerType == "Agent" && belongValue.split("_").length == 0) {
                            malert("请选择归属代理商");
                            return;
                        }
                        belongValue = "_" + belongValue + "_";
                        if($scope.verifyInfo.ownerType == "Agent" || $scope.verifyInfo.ownerType == "Factor"){
                            $scope.userInfo.areaValue=belongValue;
                        }else{
                            $scope.userInfo.belongAreaValue = belongValue;
                        }
                    }
                }
            }

            $scope.userInfo.status = status;
            $scope.userInfo.pendingId = $scope.verifyInfo._id;
            var url = window.basePath + "/account/UserPending/verifyUser";
            $http.post(url, $scope.userInfo).success(function () {
                $scope.checkBtnFun(3);
                $scope.closePopWin();
                $scope.queryCurrentList();
            });
        }

        $scope.closeWin = function () {
            $scope.isPendingLevel='';
            $scope.checkBtn = [false, false, false, false];
            $rootScope.showPopWin = false;
        }

        $scope.getTimeChange = function (time) {
            return new Date(time).getTime();
        }

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }

        $scope.closeImgFun=function(){
            $scope.showImg='';
        }
    });
})(angular);
