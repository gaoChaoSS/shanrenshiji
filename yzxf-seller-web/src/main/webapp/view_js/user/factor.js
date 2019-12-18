(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'Factor';
        $scope.entityTitle="服务站管理";
        $scope.tempGridList = '/view/user/t_factor_grid.jsp';
        $scope.windowInfo = '/view/user/t_factorInfo_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_factor_grid_filter.jsp';

        $scope.pendingBtnCheck=false;
        $scope.getAgentAction = function () {
            $scope.actionList=[];
            // $scope.actionList || ($scope.actionList = []);
            if (/^[4]$/.test($rootScope.agent.level)) {
                $scope.actionList.push({
                    name: "新增", action: $scope.addData
                });
                $scope.windowAdd = '/view/user/t_factorAdd_grid.jsp';
            }else if(/^[1]$/.test($rootScope.agent.level)) {
                $scope.pendingBtnCheck=true;
            }
            if(/^[14]$/.test($rootScope.agent.level)){
                $scope.actionList.push({
                    name: "修改", action: $scope.modifyData
                });
                $scope.windowAdd = '/view/user/t_factorAdd_grid.jsp';
            }
        }
        initGrid($rootScope, $scope, $http);

        $scope.showOtherData = function () {
            $scope.showImg='';
            $scope.userInfo={};
            var url = window.basePath + "/account/Factor/getFactorById?factorId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content;
                $scope.address=($scope.userInfo.area==null?'':$scope.userInfo.area)+($scope.userInfo.address==null?'':$scope.userInfo.address);
                $scope.surplusCardNum=window.isEmpty($scope.userInfo.surplusCardNum)?'0':$scope.userInfo.surplusCardNum;
                $scope.getLoginName();
                $scope.getCardNumber();
                //临时存放
                $scope.imgMore={
                    bankImg: $scope.userInfo.bankImg.split("_"),
                    contractImg:$scope.userInfo.contractImg.split("_")
                };
            });
        }

        $scope.getCardNumber=function(){
            var url = window.basePath + "/account/Agent/getAgentCardNum?userId=" + $scope.dataPage.$$selectedItem._id+"&userType=Factor";
            $http.get(url).success(function (re) {
                $scope.userInfo.surplusCardNum=0;
                $scope.cardList=re.content.items;
                if($scope.cardList!=null && $scope.cardList.length>0){
                    for(var j= 0,len=$scope.cardList.length;j<len;j++){
                        $scope.userInfo.surplusCardNum+=$scope.cardList[j].cardNum;
                    }
                }
            });
        }
        $scope.cardNO = function(cardNum){
            var cardNo ="";
            for(var i= 0;i<6-cardNum.toString().length;i++){
                cardNo += '0';
            }
            return cardNo+cardNum;
        }

        $scope.getLoginName=function(){
            var url = window.basePath + "/account/UserPending/getUserLoginName?userId=" + $scope.userInfo._id+"&userType=Factor";
            $http.get(url).success(function (re) {
                $scope.userInfo.loginName=re.content.loginName;
                $scope.userInfo.userName=re.content.userName;
            });
        }

        $scope.clearPwd=function(){
            $scope.loginPwd=''
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
            var url = window.basePath + "/account/Factor/getFactorById?factorId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content;
                $scope.pendingId="";
                $scope.isModify=true;
                modifyFactorGrid($rootScope, $scope, $http);
            });
        }
        $scope.addInit = function () {
            modifyFactorGrid($rootScope, $scope, $http);
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
                userType:"Factor",
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


        ////提交
        //$scope.submitForm = function () {
        //    if(window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length>100){
        //        malert("服务站名称在100字以内!");
        //        return;
        //    }
        //    if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.contactPerson)){
        //        malert("请填写2~10位中文汉字之间的联系人名字!");
        //        return;
        //    }
        //    if(!/^1[34578]{1}\d{9}$/.test($scope.userInfo.mobile)) {
        //        malert('联系手机格式不正确!');
        //        return;
        //    }
        //    if(window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length>200){
        //        malert("请选择完整的所在区域位置!");
        //        return;
        //    }
        //    if(window.isEmpty($scope.userInfo.address) || $scope.userInfo.address.length>200){
        //        malert("请填写所在街道,且不能超过200位字符!");
        //        return;
        //    }
        //    if(!/^[0-9]{16,19}$/.test($scope.userInfo.bankId)){
        //        malert("请输入正确的银行账号!");
        //        return;
        //    }
        //    if(window.isEmpty($scope.userInfo.bankName)){
        //        malert("请输入开户行!");
        //        return;
        //    }
        //    if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.bankUser)){
        //        malert("请填写2~10位中文汉字之间的户名!");
        //        return;
        //    }
        //    //if(!/^1[34578]{1}\d{9}$/.test($scope.userInfo.bankUserPhone)) {
        //    //    malert('请输入正确的持卡人电话!');
        //    //    return;
        //    //}
        //    //if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.bankUserCardId)){
        //    //    malert("请输入正确的持卡人身份证号码!");
        //    //    return;
        //    //}
        //    if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)){
        //        malert("请输入正确的服务站身份证号码!");
        //        return;
        //    }
        //    if(window.isEmpty($scope.userInfo.idCardImgFront)){
        //        malert("请上传身份证正面照片!");
        //        return;
        //    }
        //    if(window.isEmpty($scope.userInfo.idCardImgBack)){
        //        malert("请上传身份证背面照片!");
        //        return;
        //    }
        //    if(window.isEmpty($scope.userInfo.idCardImgHand)){
        //        malert("请上传手持身份证照片!");
        //        return;
        //    }
        //    var url = window.basePath + "/account/UserPending/submitFactor";
        //    $http.post(url, $scope.userInfo).success(function () {
        //        malert("提交成功");
        //        $rootScope.showPopWin = false;
        //        $scope.closePopWin();
        //        $scope.queryCurrentList();
        //    });
        //}
    });
})(angular);
