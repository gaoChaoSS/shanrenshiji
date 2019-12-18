(function () {
    window.modifyFactorGrid = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            $scope.initUser();
            $scope.checkSuccess = false;
            $scope.checkNext = true;
            $scope.checkCancel = false;
            $scope.checkSubmit = false;
            $scope.selectArea = new Array(3);
            $scope.areaList = [[{name: '省'}], [{name: '市'}], [{name: '县/镇/区'}]];
            $scope.getArea(-1, 1);
            $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
            $scope.showImg='';
        }

        $scope.initUser = function () {
            if($scope.popWinTitle == '新增'){
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
                $scope.initAreaValue=null;
                $scope.isModify=false;
            }else{
                $scope.userInfo.pendingId=$scope.pendingId;
                $scope.initAreaValue=$scope.userInfo.realAreaValue;
            }
            //临时存放
            $scope.imgMore={
                bankImg: window.isEmpty($scope.userInfo.bankImg)?[]:$scope.userInfo.bankImg.split("_"),
                contractImg:window.isEmpty($scope.userInfo.contractImg)?[]:$scope.userInfo.contractImg.split("_")
            };
        }

        //获取地理位置
        $scope.getArea = function (_id, type) {
            var url = window.basePath + '/crm/Member/getLocation?pid=' + _id;
            $http.get(url).success(function (re) {
                if (type == 1) {
                    $scope.areaList[0] = re.content.items;
                    $scope.areaList[0].unshift({name: '省'});

                    $scope.areaList[1] = [{name: '市'}];
                    $scope.areaList[2] = [{name: '县/镇/区'}];

                    $scope.selectArea[0] = $scope.areaList[0][0];
                    $scope.selectArea[1] = $scope.areaList[1][0];
                    $scope.selectArea[2] = $scope.areaList[2][0];
                    $scope.userInfo.area='';
                    $scope.userInfo.realAreaValue ='';
                } else if (type == 2) {
                    $scope.areaList[1] = re.content.items;
                    $scope.areaList[1].unshift({name: '市'});

                    $scope.areaList[2] = [{name: '县/镇/区'}];

                    $scope.selectArea[1] = $scope.areaList[1][0];
                    $scope.selectArea[2] = $scope.areaList[2][0];
                    $scope.userInfo.area='';
                    $scope.userInfo.realAreaValue ='';
                } else if (type == 3) {
                    $scope.areaList[2] = re.content.items;
                    $scope.areaList[2].unshift({name: '县/镇/区'});
                    $scope.selectArea[2] = $scope.areaList[2][0];
                    $scope.userInfo.area='';
                    $scope.userInfo.realAreaValue ='';
                } else if (type == 4) {
                    $scope.userInfo.area = $scope.selectArea[0].name + $scope.selectArea[1].name + $scope.selectArea[2].name;
                    $scope.userInfo.realAreaValue = $scope.selectArea[2].pvalue + '_' + $scope.selectArea[2].value + '_';
                }

                if(!window.isEmpty($scope.initAreaValue)){
                    var text = $scope.initAreaValue;
                    var areaArr = text.substring(1, text.length - 1).split("_");
                    var index=type-1;
                    if(type <=3 ){
                        for(var i= 1,len=$scope.areaList[index].length;i<len;i++){
                            if($scope.areaList[index][i].value==areaArr[index]){
                                $scope.selectArea[index] = $scope.areaList[index][i];
                            }
                        }
                        if(typeof($scope.selectArea[index])=="undefined"){
                            $scope.areaList[index] = [{name: '---请选择---'}];
                            $scope.selectArea[index]=$scope.areaList[index][0];
                        }else{
                            $scope.getArea($scope.selectArea[index]._id,type+1);
                        }
                    }
                }
            });
        }

        $scope.uploadFile = function (inputObj, type) {
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
                    });
                }
            };
            window.uploadWinObj.files = inputObj.files;
            window.uploadFile();
        };

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }

        $scope.closeImgFun=function(){
            $scope.showImg='';
        }

        //格式化图片编码
        $scope.formatImgMore=function(fileName){
            if (!window.isEmpty($scope.imgMore[fileName])){
                $scope.userInfo[fileName]=$scope.imgMore[fileName].join("_");
                if($scope.userInfo[fileName].substring(0,1)=="_"){
                    $scope.userInfo[fileName]=$scope.userInfo[fileName].substring(1,$scope.userInfo[fileName].length);
                }
            }
        }

        $scope.save = function () {
            $scope.formatImgMore("bankImg");
            $scope.formatImgMore("contractImg");
            var url = window.basePath + "/account/UserPending/saveApplyFactor";
            $http.post(url, $scope.userInfo).success(function (re) {
                if (window.isEmpty($scope.userInfo.pendingId)) {
                    $scope.userInfo.pendingId = re.content.pendingId;
                    $scope.userInfo._id = re.content.ownerId;
                }
                malert("已保存草稿");
            });
        };

        $scope.delFileItem = function (imgId) {
            $scope.userInfo[imgId] = "";
        };

        $scope.delFileItemMore = function (entityField,index) {
            $scope.imgMore[entityField].remove(index);
        };

        //第一步:提交基本信息,返回ID
        $scope.nextFun = function () {
            if(window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length>100){
                malert("服务站名称在100字以内!");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.contactPerson)){
                malert("请填写2~64位中文汉字之间的联系人名字!");
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

            if(!$scope.isModify){
                $scope.save();
            }
            $scope.checkNext = false;
        }

        //提交
        $scope.submitForm = function () {
            if($scope.isModify){
                $scope.nextFun();
            }
            if($scope.checkNext){
                return;
            }
            if (window.isEmpty($scope.userInfo.bankId)) {
                malert("请输入正确的银行账号!");
                return;
            }
            if(window.isEmpty($scope.userInfo.bankName)){
                malert("请输入开户行!");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.bankUser)){
                malert("请填写2~64位中文汉字之间的户名!");
                return;
            }
            //if(!/^1[34578]{1}\d{9}$/.test($scope.userInfo.bankUserPhone)) {
            //    malert('请输入正确的持卡人电话!');
            //    return;
            //}
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)){
                malert("请输入正确的服务站身份证号码!");
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
                malert("请上传1~10张银行卡正反面/开户许可证!");
                return;
            }else{
                $scope.formatImgMore("bankImg");
            }
            if ($scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
                malert("请上传1~10张合同照片!");
                return;
            }else{
                $scope.formatImgMore("contractImg");
            }
            var url = window.basePath + "/account/UserPending/submitFactor";
            $http.post(url, $scope.userInfo).success(function () {
                $scope.checkSuccess=true;
                $scope.queryCurrentList();
            });
        }

        $scope.closeWin = function () {
            $rootScope.showPopWin = false;
            initGrid($rootScope, $scope, $http);
        }
        $scope.closeSuccess = function () {
            $scope.checkSuccess = false
        }
        $scope.backBtn = function () {
            $scope.checkNext = true;
        }
        $scope.init();
    }
})();