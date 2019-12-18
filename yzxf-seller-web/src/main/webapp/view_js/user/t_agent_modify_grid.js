(function () {
    window.modifyAgentGrid = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            $scope.initUser();
            $scope.getBank();
            $scope.getBankCity();
            $scope.checkSuccess = false;
            $scope.checkNext = true;
            $scope.checkCancel = false;
            $scope.checkSubmit = false;
            $scope.selectArea = new Array(3);
            $scope.areaList = [[{name: '省'}], [{name: '市'}], [{name: '县/镇/区'}]];
            $scope.getArea(-1, 1);
            $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
            $scope.showImg='';
            $scope.selectBank={bankId:"",name:""};
            $scope.bankCityList = new Array(2);
            $scope.selectBankCity = [
                {province:'',provinceValue:''},
                {city:'',cityValue:''}
            ];
            $scope.isShowBankCity=false;
        };

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
                    bankType:"",
                    bankTypeValue:"",
                    bankProvince: "",
                    bankProvinceValue: "",
                    bankCity: "",
                    bankCityValue: "",
                    bankUser: "",
                    //bankUserCardId: "",
                    //bankUserPhone: "",
                    bankImg: "",
                    businessLicense: "",
                    contactPerson: "",
                    idCardImgBack: "",
                    idCardImgFront: "",
                    idCardImgHand: "",
                    contractImg:"",
                    name: "",
                    phone: "",
                    legalPerson:"",
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

        // 获取银行总行
        $scope.getBank=function(){
            var url = window.basePath + '/account/BankType/query';
            $http.get(url).success(function (re) {
                $scope.bankList = re.content.items;
                $scope.bankList.unshift({name: '请选择'});
                if(window.isEmpty($scope.userInfo.bankTypeValue)){
                    $scope.selectBank=$scope.bankList[0];
                }else{
                    for(var i=0,len=$scope.bankList.length;i<len;i++){
                        if($scope.userInfo.bankTypeValue===$scope.bankList[i].bankId){
                            $scope.selectBank = $scope.bankList[i];
                            break;
                        }
                    }
                }
            });
        };

        // 设置总行同步到支行
        $scope.setBankName=function(data){
            if(window.isEmpty(data) || data.name==='请选择'){
                return;
            }
            $scope.userInfo.bankName = data.name;
            $scope.userInfo.bankType = data.name;
            $scope.userInfo.bankTypeValue = data.bankId;
        };

        // 获取代付银行省份、城市
        $scope.getBankCity=function(type,data){
            var url = window.basePath + '/account/User/getBankCity?1=1';
            if(!window.isEmpty(type)){
                url+='&type='+type;
            }
            if(!window.isEmpty(data) && !window.isEmpty(data.provinceValue)){
                url+='&provinceValue='+data.provinceValue;
            }
            $http.get(url).success(function (re) {
                $scope.bankCityList[window.isEmpty(type)?0:1] = re.content.items;
                if(window.isEmpty(type)){
                    $scope.bankCityList[0].unshift({province: '请选择'});
                    if(window.isEmpty($scope.userInfo.bankProvinceValue)){
                        $scope.selectBankCity[0]=$scope.bankCityList[0][0];
                    }else{
                        for(var i=0,len=$scope.bankCityList[0].length;i<len;i++){
                            if($scope.userInfo.bankProvinceValue===$scope.bankCityList[0][i].provinceValue){
                                $scope.selectBankCity[0] = $scope.bankCityList[0][i];
                                $scope.getBankCity('city',$scope.selectBankCity[0]);
                                break;
                            }
                        }
                    }
                }else{//保存省
                    $scope.userInfo.bankProvince = $scope.selectBankCity[0].province;
                    $scope.userInfo.bankProvinceValue = $scope.selectBankCity[0].provinceValue;
                }
            });
        };

        //设置支行城市
        $scope.setBankCity=function(data){
            if(window.isEmpty(data) || window.isEmpty(data.city) || window.isEmpty(data.cityValue)){
                return;
            }
            $scope.userInfo.bankCity = data.city;
            $scope.userInfo.bankCityValue = data.cityValue;
            $scope.isShowBankCity=false;
        };

        //获取失去支行城市焦点
        $scope.blurBankCity=function(){
            if($scope.isShowBankCity){
                setTimeout(function(){
                    $scope.isShowBankCity=false;
                    if(!$scope.checkBankCity()){
                        malert("未匹配到支行城市")
                    }
                },100);
            }else{
                $scope.isShowBankCity=true;
            }
        };

        //检查银行城市编码是否正确
        $scope.checkBankCity=function(){
            var flag = false;
            for(var i=0,len=$scope.bankCityList[1].length;i<len;i++){
                if($scope.userInfo.bankCity===$scope.bankCityList[1][i].city
                    && $scope.userInfo.bankCityValue===$scope.bankCityList[1][i].cityValue){
                    flag = true;
                    break;
                }
            }
            return flag;
        };

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
                    //if(type==2 && $scope.initAreaValue==$scope.userInfo.areaValue){
                    //    $scope.initAreaValue=null;
                    //}
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
                entityName: 'Agent',
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
            var url = window.basePath + "/account/UserPending/saveApplyAgent";
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
            if (window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length > 100) {
                malert("代理商名称在100字以内!");
                return;
            }
            if (!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.contactPerson)) {
                malert("请填写2~64位中文汉字之间的联系人名字!");
                return;
            }
            if (!/^1[3456789]{1}\d{9}$/.test($scope.userInfo.phone)) {
                malert('联系手机格式不正确!');
                return;
            }
            if (window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length > 200) {
                malert("请选择完整的所在区域位置!");
                return;
            }
            if (window.isEmpty($scope.userInfo.address) || $scope.userInfo.address.length > 200) {
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
            // if(window.isEmpty($scope.userInfo.bankType) || window.isEmpty($scope.userInfo.bankTypeValue)){
            //     malert("未选择开户总行");
            //     return;
            // }
            if (window.isEmpty($scope.userInfo.bankName)) {
                malert("请输入开户支行!");
                return;
            }
            // if(window.isEmpty($scope.userInfo.bankProvince) || window.isEmpty($scope.userInfo.bankProvinceValue)){
            //     malert("未选择支行省份");
            //     return;
            // }
            // if(!$scope.checkBankCity()){
            //     malert("未匹配到支行城市");
            //     return;
            // }
            if (window.isEmpty($scope.userInfo.bankId)) {
                malert("请输入正确的银行账号!");
                return;
            }

            if (!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.bankUser)) {
                malert("请填写2~64位中文汉字之间的户名!");
                return;
            }
            //if (!/^1[34578]{1}\d{9}$/.test($scope.userInfo.bankUserPhone)) {
            //    malert('请输入正确的持卡人电话!');
            //    return;
            //}
            //if (!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.bankUserCardId)) {
            //    malert("请输入正确的持卡人身份证号码!");
            //    return;
            //}
            if(!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.legalPerson)){
                malert("请填写2~64位中文汉字之间的法人名字!");
                return;
            }
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)){
                malert("请输入正确的法人身份证号码!");
                return;
            }
            if (window.isEmpty($scope.userInfo.businessLicense)) {
                malert("请上传营业执照!");
                return;
            }
            if (window.isEmpty($scope.userInfo.idCardImgFront)) {
                malert("请上传身份证正面照片!");
                return;
            }
            if (window.isEmpty($scope.userInfo.idCardImgBack)) {
                malert("请上传身份证背面照片!");
                return;
            }
            if (window.isEmpty($scope.userInfo.idCardImgHand)) {
                malert("请上传手持身份证照片!");
                return;
            }
            if ($scope.imgMore.bankImg==null || $scope.imgMore.bankImg.length<1 || $scope.imgMore.bankImg.length>11){
                malert("请上传1~10张银行卡/开户许可证!");
                return;
            }else{
                $scope.formatImgMore("bankImg");
            }
            if ($scope.imgMore.contractImg==null || $scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
                malert("请上传1~10张合同照片!");
                return;
            }else{
                $scope.formatImgMore("contractImg");
            }
            var url = window.basePath + "/account/UserPending/submitAgent";
            $http.post(url, $scope.userInfo).success(function () {
                //$scope.checkSuccess = true;
                $scope.showSuccess();
                $scope.closePopWin();
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
        $scope.showSuccess=function(){
            $scope.checkSuccess=true
        }
        $scope.backBtn = function () {
            $scope.checkNext = true;
        }
        $scope.init();
    }
})();