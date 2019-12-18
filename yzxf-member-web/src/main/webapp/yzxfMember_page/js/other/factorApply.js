(function (angular, undefined) {

    var model = 'other';
    var entity = 'factorApply';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope["$$factorApplyArea"]=null;
        $scope.imgMore={
            bankImg: "",
            contractImg:""
        }
        $scope.initUser = function () {
            $scope.userInfo = {
                pendingId: "",//待审表ID
                _id: "",
                address: "",
                area: "",
                areaValue: "",
                bankId: "",
                bankName: "",
                bankUser: "",
                //bankUserCardId: "",
                //bankUserPhone: "",
                bankImg: "",
                contactPerson: "",
                idCardImgBack: "",
                idCardImgFront: "",
                idCardImgHand: "",
                contractImg:"",
                name: "",
                mobile: "",
                realCard:"",
                realAreaValue:""
            };
        };

        $scope.goBackFun=function(){
            $rootScope.notRef=false;
            $rootScope.goBack();
        }

        $scope.checkPendType=function(){
            var isGetApply=true;
            if($rootScope["$$factorApplyArea"]!=null) {
                $scope.userInfo.area = $rootScope["$$factorApplyArea"].locationArea;
                $scope.userInfo.realAreaValue = $rootScope["$$factorApplyArea"].locationAllValue;
                isGetApply=false;
            }
            if(!isGetApply || $rootScope.notRef){
                return;
            }
            if(window.isEmpty(getCookie("_member_id"))){
                var otherId;
                // 给非会员设置cookie，防止每次进入都被刷新
                if(window.isEmpty(getCookie("_other_id"))){
                    otherId="_"+genUUID();
                    setCookie("_other_id",otherId);
                }else{
                    otherId = getCookie("_other_id");
                }
                $scope.getPend('/account/UserPending/getOtherIsApply?ownerType=Factor&createId='+otherId);
            }else{
                $scope.getPend('/account/UserPending/getMemberIsApply?ownerType=Factor');
            }
        }

        $scope.getPend=function(api){
            var url = window.basePath + api;
            $http.get(url).success(function (re) {
                if (re.content.text==null || re.content.status==0) {
                    if(re.content!=null && re.content.text!=null){
                        $scope.userInfo = re.content.text;
                        $scope.userInfo.pendingId = re.content._id;
                    }else{
                        $scope.initUser();
                    }
                    $scope.userInfo.pendingId = re.content._id;
                    $scope.userInfo._id=re.content.ownerId;
                }else if(re.content.status==3){
                    $scope.userInfo = re.content.text;
                    $scope.userInfo.pendingId = re.content._id;
                } else {
                    $scope.userInfo = re.content.text;
                    if ($scope.userInfo == null) {
                        $scope.initUser();
                    }
                }
                //临时存放
                $scope.imgMore={
                    bankImg: $scope.userInfo.bankImg.split("_"),
                    contractImg:$scope.userInfo.contractImg.split("_")
                };
                $scope.userPending = re.content;

                if(!window.isEmpty(getCookie("_other_id"))){
                    $scope.userInfo["creatorType"] = "other";
                }
            })
        }

        $scope.uploadFile = function (inputObj, type) {
            if($scope.isMobile){
                type=$scope.showUploadName;
            }
            if (type == 'bankImg' || type == 'contractImg') {
                if($scope.imgMore[type].length>11){
                    malert("上传图片不能超过十张!");
                    return;
                }
            }
            var entityId=$scope.userInfo._id;
            window.uploadWinObj = {
                one: true,
                entityName: 'Factor',
                entityField: type,
                entityId: entityId,
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        if (type == 'bankImg' || type == 'contractImg') {
                            $scope.imgMore[type].push(options.fileId);
                        } else{
                            $scope.userInfo[type] = options.fileId;
                        }
                        $scope.closeUpload();
                    });
                }
            };

            window.uploadWinObj.files = inputObj.files;
            window.uploadFile();
        };

        //格式化图片编码
        $scope.formatImgMore=function(fileName){
            if ($scope.imgMore[fileName]!=null){
                $scope.userInfo[fileName]=$scope.imgMore[fileName].join("_");
                if($scope.userInfo[fileName].substring(0,1)=="_"){
                    $scope.userInfo[fileName]=$scope.userInfo[fileName].substring(1,$scope.userInfo[fileName].length);
                }
            }
        }

        $scope.delFileItem = function (imgId) {
            $scope.userInfo[imgId] = "";
        };

        $scope.delFileItemMore = function (entityField,index) {
            $scope.imgMore[entityField].remove(index);
        };

        $scope.firstSubmit=function(){
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.contactPerson)){
                malert("请填写2~10位中文汉字之间的联系人名字!");
                return;
            }
            if(!/^1[3456789]{1}\d{9}$/.test($scope.userInfo.mobile)) {
                malert('联系手机格式不正确!');
                return;
            }
            if(window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length>200){
                malert("请选择完整的所在区域位置!");
                return;
            }
            var url = window.basePath + "/account/UserPending/userApplyFirst";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope["$$factorApplyArea"]=null;
                $scope.userPending.status=0.1;
            });
        };

        $scope.submitForm = function(){
            if(window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length>100){
                malert("发卡点名称在100字以内!");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.contactPerson)){
                malert("请填写2~10位中文汉字之间的联系人名字!");
                return;
            }
            if(!/^1[3456789]{1}\d{9}$/.test($scope.userInfo.mobile)) {
                malert('联系手机格式不正确!');
                return;
            }
            if(window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length>200){
                malert("请选择完整的所在区域位置!");
                return;
            }
            if(window.isEmpty($scope.userInfo.address) || $scope.userInfo.address.length>200){
                malert("请填写所在街道,且不能超过200位字符!");
                return;
            }
            if(!/^[0-9]{16,19}$/.test($scope.userInfo.bankId)){
                malert("请输入正确的银行账号!");
                return;
            }
            if(window.isEmpty($scope.userInfo.bankName)){
                malert("请输入银行类型!");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.bankUser)){
                malert("请填写2~10位中文汉字之间的持卡人名字!");
                return;
            }
            //if(!/^1[34578]{1}\d{9}$/.test($scope.userInfo.bankUserPhone)) {
            //    malert('请输入正确的持卡人电话!');
            //    return;
            //}
            //if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.bankUserCardId)){
            //    malert("请输入正确的持卡人身份证号码!");
            //    return;
            //}
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)){
                malert("请输入正确的发卡点身份证号码!");
                return;
            }
            if(window.isEmpty($scope.userInfo.idCardImgFront)){
                malert("请上传身份证正面照片!");
                return;
            }
            if(window.isEmpty($scope.userInfo.idCardImgBack)){
                malert("请上传身份证背面照片!");
                return;
            }
            if(window.isEmpty($scope.userInfo.idCardImgHand)){
                malert("请上传手持身份证照片!");
                return;
            }
            if ($scope.imgMore.bankImg==null || $scope.imgMore.bankImg.length<1 || $scope.imgMore.bankImg.length>11){
                malert("请上传1~10张银行卡/开户许可证!");
                return;
            }else{
                $scope.formatImgMore("bankImg");
            }
            if ($scope.userInfo.contractImg==null || $scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
                malert("请上传1~10张合同照片!");
                return;
            }else{
                $scope.formatImgMore("contractImg");
            }

            var url = window.basePath + "/account/UserPending/submitFactor";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope["$$factorApplyArea"]=null;
                $rootScope.goPage("/other/other");
            });
        }

        //显示底部相机选项
        $scope.showUpload=function(name){
            if(!$scope.isMobile){
                return;
            }
            $scope.showUploadName = name;
        };

        $scope.closeUpload=function(){
            $scope.showUploadName='';
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '发卡点申请');
                $scope.checkPendType();
                $scope.upSample=false;
                $scope.isMobile=window.isMobile();
                $scope.showUploadName='';
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);
