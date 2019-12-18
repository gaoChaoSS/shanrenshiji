/**
 * Created by tianchangsen on 17/1/24.
 */
(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'Seller';
        $scope.entityTitle = "商家管理";
        $scope.tempGridList = '/view/user/t_seller_grid.jsp';
        $scope.windowInfo = '/view/user/t_sellerInfo_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_seller_grid_filter.jsp';

        $scope.pendingBtnCheck=false;

        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            if ($rootScope.agent.level==4) {
                $scope.addBtn('新增',$scope.addData);
                $scope.windowAdd = '/view/user/t_sellerAdd_grid.jsp';
            }
            if(/^[1]$/.test($rootScope.agent.level)) {
                $scope.pendingBtnCheck=true;
            }
            if ($rootScope.agent.level==1) {//因修改导致错误问题，暂时针对除平台管理员外的用户关闭修改功能  修改时间20190116 AliCao
                $scope.addBtn('修改',$scope.modifyData);
            }
            $scope.addBtn('团队关系',$scope.teamBtn);
            $scope.addBtn('贵商绑定/解绑ECP',$scope.gpayBindECP);
        }

        $scope.addBtn=function(name,action){
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name==name){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: name, action: action
            });
        };

        // 团队关系
        $scope.teamBtn = function(){
            teamGrid($rootScope, $scope, $http);
        };

        $scope.gpayBindECP = function(){
            initGpayBindECP($rootScope, $scope, $http);
        }


        initGrid($rootScope, $scope, $http);

        // 关联账号
        $scope.goRelate=function(){
            // if(true){
            //     malert("功能开发中");
            //     return;
            // }
            window.location.href="https://s.kyb.vip/store/store/relateStore/relateStoreId/"+$scope.dataPage.$$selectedItem._id;
        };

        $scope.showOtherData = function () {
            $scope.showImg='';
            $scope.userInfo={};
            var url = window.basePath + "/account/Seller/getSellerInfoById?sellerId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo = re.content;
                $scope.openWeek = ($scope.userInfo.openWeek == null ? '' : '周' + $scope.userInfo.openWeek) + ($scope.userInfo.openTime == null ? '' : (',' + $scope.userInfo.openTime + ':00' + ($scope.userInfo.closeTime == null ? '' : '至' + $scope.userInfo.closeTime + ':00')));
                $scope.address = ($scope.userInfo.area == null ? '' : $scope.userInfo.area) + ($scope.userInfo.address == null ? '' : $scope.userInfo.address);
                $scope.integralRate = window.isEmpty($scope.userInfo.integralRate) ? '0%' : $scope.userInfo.integralRate + '%';
                if(/^[1]$/.test($rootScope.agent.level)) {
                    $scope.getUserCanUse();
                }
                $scope.getLoginName();
                $scope.getRelate();
                //临时存放
                $scope.imgMore={
                    bankImg: window.isEmpty($scope.userInfo.bankImg)?[]:$scope.userInfo.bankImg.split("_"),
                    contractImg:window.isEmpty($scope.userInfo.contractImg)?[]:$scope.userInfo.contractImg.split("_"),
                    doorImg:window.isEmpty($scope.userInfo.doorImg)?[]:$scope.userInfo.doorImg.split("_")
                };
                $scope.initMap();
            });
        }

        $scope.getRelate=function(){
            var url = window.basePath + "/account/RelateStore/getRelateStore?localSellerId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.relateStore = re.content;
            });
        }

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

        $scope.clearPwd=function(){
            $scope.loginPwd=''
        }

        $scope.getLoginName=function(){
            var url = window.basePath + "/account/UserPending/getUserLoginName?userId=" + $scope.userInfo._id+"&userType=Seller";
            $http.get(url).success(function (re) {
                $scope.userInfo.loginName=re.content.loginName;
                $scope.userInfo.userName=re.content.userName;
            });
        }

        $scope.getUserCanUse=function(){
            var url = window.basePath + "/account/UserPending/getUserCanUse?userId=" + $scope.userInfo._id+"&userType=Seller";
            $http.get(url).success(function (re) {
                $scope.userCanUse=re.content.canUse;
            });
        }

        $scope.resetUserPwd=function(){
            if(window.isEmpty($scope.userInfo.loginName)){
                malert("获取用户信息失败!");
                return;
            }

            var url = window.basePath + "/account/User/resetUserPwd?loginName="+$scope.userInfo.userName;
            $http.get(url).success(function (re) {
                $scope.loginPwd=re.content.password;
            });
        }

        $scope.modifyInfo=function(){
            $scope.userInfo={};
            var url = window.basePath + "/account/Seller/getSellerInfoById?sellerId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content;
                $scope.pendingId="";
                $scope.isModify=true;
                var url = window.basePath + "/account/Agent/getAgentAreaValueById?areaValue=" + $scope.userInfo.belongAreaValue;
                $http.get(url).success(function (re) {
                    $scope.agentNameAll = re.content.agentNameAll;
                    modifySellerGrid($rootScope, $scope, $http);
                });
            });
        }
        $scope.addInit = function () {
            modifySellerGrid($rootScope, $scope, $http);
        }

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }

        $scope.closeImgFun=function(){
            $scope.showImg='';
        }

        $scope.modifyCanUse=function(){
            var url = window.basePath + "/account/UserPending/modifyCanUse";
            var data = {
                userType:"Seller",
                userId:$scope.dataPage.$$selectedItem._id,
                canUse:$scope.userInfo.canUse
            }
            $http.post(url,data).success(function () {
                malert("修改成功");
                $rootScope.showPopWin = false;
                $scope.closePopWin();
                $scope.queryCurrentList();
            });
        }



        //提交
        //$scope.submitForm = function () {
        //    if (window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length > 100) {
        //        malert("商家名称在100字以内!");
        //        return;
        //    }
        //    if (!/^[1-9][0-9]?$/.test($scope.userInfo.integralRate)) {
        //        malert("商家积分率应在1~99以内的整数!");
        //        return;
        //    }
        //    if (!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.contactPerson)) {
        //        malert("请填写2~10位中文汉字之间的联系人名字!");
        //        return;
        //    }
        //    if (!/^1[34578]{1}\d{9}$/.test($scope.userInfo.phone)) {
        //        malert('联系手机格式不正确!');
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.serverPhone) || $scope.userInfo.serverPhone.length>64) {
        //        malert('请输入正确的客服号码!');
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.openWeek)) {
        //        malert('请选择营业星期!');
        //        return;
        //    }
        //    if (window.isEmpty(String.valueOf($scope.userInfo.openTime)) || window.isEmpty(String.valueOf($scope.userInfo.closeTime))) {
        //        malert('请选择营业时间!');
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.operateType) || window.isEmpty($scope.userInfo.operateValue)
        //        || $scope.userInfo.operateType.indexOf("请选择")!=-1 || $scope.userInfo.operateType.indexOf("undefined")!=-1) {
        //        malert('请选择经营范围!');
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.areaValue) || window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length > 200
        //        || $scope.userInfo.areaValue.indexOf("请选择")!=-1 || $scope.userInfo.areaValue.indexOf("undefined")!=-1) {
        //        malert("请选择完整的所在区域位置!");
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.address) || $scope.userInfo.address.length > 200) {
        //        malert("请填写所在街道,且不能超过200位字符!");
        //        return;
        //    }
        //    if (!window.isEmpty($scope.userInfo.intro) && $scope.userInfo.intro.length > 100) {//可不填
        //        malert("商家简介不能超过100位字符!");
        //        return;
        //    }
        //    if (!/^[0-9]{16,19}$/.test($scope.userInfo.bankId)) {
        //        malert("请输入正确的银行账号!");
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.bankName)) {
        //        malert("请输入开户行!");
        //        return;
        //    }
        //    if (!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.bankUser)) {
        //        malert("请填写2~10位中文汉字之间的户名!");
        //        return;
        //    }
        //    //if (!/^1[34578]{1}\d{9}$/.test($scope.userInfo.bankUserPhone)) {
        //    //    malert('请输入正确的持卡人电话!');
        //    //    return;
        //    //}
        //    //if (!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.bankUserCardId)) {
        //    //    malert("请输入正确的持卡人身份证号码!");
        //    //    return;
        //    //}
        //    if (!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.legalPerson)) {
        //        malert("请填写2~10位中文汉字之间的法人名称!");
        //        return;
        //    }
        //    if (!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)) {
        //        malert("请输入正确的法人的身份证号码!");
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.businessLicense)) {
        //        malert("请上传营业执照!");
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.idCardImgFront)) {
        //        malert("请上传身份证正面照片!");
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.idCardImgBack)) {
        //        malert("请上传身份证背面照片!");
        //        return;
        //    }
        //    if (window.isEmpty($scope.userInfo.idCardImgHand)) {
        //        malert("请上传手持身份证照片!");
        //        return;
        //    }
        //
        //    var url = window.basePath + "/account/UserPending/submitSeller";
        //    $http.post(url, $scope.userInfo).success(function () {
        //        malert("提交成功");
        //        $rootScope.showPopWin = false;
        //        $scope.closePopWin();
        //        $scope.queryCurrentList();
        //    });
        //}
    });
})(angular);
