(function (angular, undefined) {

    var model = 'other';
    var entity = 'agentApply';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope["$$agentApplyArea"]=null;
        $scope.initUser = function () {
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
        }

        $scope.goBackFun=function(){
            $rootScope.notRef=false;
            $rootScope.goBack();
        }

        $scope.checkPendType=function(){
            var isGetApply=true;
            if($rootScope["$$agentApplyArea"]!=null) {
                $scope.userInfo.area = $rootScope["$$agentApplyArea"].locationArea;
                $scope.userInfo.realAreaValue = $rootScope["$$agentApplyArea"].locationAllValue;
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
                $scope.getPend('/account/UserPending/getOtherIsApply?ownerType=Agent&createId='+otherId);
            }else{
                $scope.getPend('/account/UserPending/getMemberIsApply?ownerType=Agent');
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
                } else if(re.content.status==3){
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

                if(!/^[12]$/.test(re.content.status)){
                    $scope.getBank();
                    $scope.getBankCity();
                }

                $scope.selectBank={bankId:"",name:""};
                $scope.bankCityList = new Array(2);
                $scope.selectBankCity = [
                    {province:'',provinceValue:''},
                    {city:'',cityValue:''}
                ];
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

        // 获取银行总行
        $scope.getBank=function(){
            var url = window.basePath + '/account/BankType/query';
            $http.get(url).success(function (re) {
                $scope.bankList = re.content.items;
                // $scope.bankList.unshift({name: '请选择'});
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
                    // $scope.selectBankCity[0]=$scope.bankCityList[0][0];
                    if(!window.isEmpty($scope.userInfo.bankProvinceValue)){
                        for(var i=0,len=$scope.bankCityList[0].length;i<len;i++){
                            if($scope.userInfo.bankProvinceValue===$scope.bankCityList[0][i].provinceValue){
                                $scope.selectBankCity[0] = $scope.bankCityList[0][i];
                                $scope.getBankCity('city',$scope.selectBankCity[0]);
                                break;
                            }
                        }
                    }
                }else{//保存省
                    $scope.selectBankCity[0]=data;
                    $scope.userInfo.bankProvince = $scope.selectBankCity[0].province;
                    $scope.userInfo.bankProvinceValue = $scope.selectBankCity[0].provinceValue;
                    $scope.userInfo.bankCity='';
                    $scope.userInfo.bankCityValue='';
                    $scope.selectBankCity[0].city='';
                    $scope.selectBankCity[0].cityValue='';
                    $scope.keyword='';
                }
            });
        };

        $scope.firstSubmit=function(){
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.contactPerson)){
                malert("请填写2~10位中文汉字之间的联系人名字!");
                return;
            }
            if(!/^1[3456789]{1}\d{9}$/.test($scope.userInfo.phone)) {
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
                $rootScope["$$agentApplyArea"]=null;
                $scope.userPending.status=0.1;
            });
        };

        $scope.submitForm = function(){
            if(window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length>100){
                malert("代理商名称在100字以内!");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.contactPerson)){
                malert("请填写2~10位中文汉字之间的联系人名字!");
                return;
            }
            if(!/^1[3456789]{1}\d{9}$/.test($scope.userInfo.phone)) {
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
            if(window.isEmpty($scope.userInfo.bankType) || window.isEmpty($scope.userInfo.bankTypeValue)){
                malert("请选择开户总行!");
                return;
            }
            if(window.isEmpty($scope.userInfo.bankName)){
                malert("请输入开户支行!");
                return;
            }
            if(window.isEmpty($scope.userInfo.bankProvince) || window.isEmpty($scope.userInfo.bankProvinceValue)
                || window.isEmpty($scope.userInfo.bankCity) || window.isEmpty($scope.userInfo.bankCityValue)){
                malert("请选择开户支行城市!");
                return;
            }
            if(!/^[0-9]{16,19}$/.test($scope.userInfo.bankId)){
                malert("请输入正确的银行账号!");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.bankUser)){
                malert("请填写2~10位中文汉字之间的户名!");
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
            if(window.isEmpty($scope.userInfo.idCardImgFront)){
                malert("请上传法人身份证正面照片!");
                return;
            }
            if(window.isEmpty($scope.userInfo.idCardImgBack)){
                malert("请上传法人身份证背面照片!");
                return;
            }
            if(window.isEmpty($scope.userInfo.idCardImgHand)){
                malert("请上传法人手持身份证照片!");
                return;
            }
            if ($scope.imgMore.bankImg==null || $scope.imgMore.bankImg.length<1 || $scope.imgMore.bankImg.length>11){
                malert("请上传1~10张银行卡正反面/开户许可证!");
                return;
            }else{
                $scope.formatImgMore("bankImg");
            }
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.userInfo.legalPerson)){
                malert("请填写2~10位中文汉字之间的法人名字!");
                return;
            }
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)){
                malert("请输入正确的法人身份证号码!");
                return;
            }
            if ($scope.userInfo.contractImg==null || $scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
                malert("请上传1~10张合同照片!");
                return;
            }else{
                $scope.formatImgMore("contractImg");
            }


            var url = window.basePath + "/account/UserPending/submitAgent";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope["$$agentApplyArea"]=null;
                $rootScope.goPage("/other/other");
            });
        }

        $scope.setShowPage=function(num){
            $scope.showPage[num]=!$scope.showPage[num];
        };

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
                window.setWindowTitle($rootScope, '代理商申请');
                $scope.checkPendType();
                $scope.upSample=false;
                $scope.showPage=[false,false];
                $rootScope.isListenerInput=true;
                $scope.isMobile=window.isMobile();
                $scope.showUploadName='';
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);
