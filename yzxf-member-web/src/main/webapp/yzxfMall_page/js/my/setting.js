(function (angular, undefined) {

    var model = 'my';
    var entity = 'setting';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';

        $scope.modifyPwd = function(){
            if(window.isEmpty($scope.oldPwd)||window.isEmpty($scope.firstPwd)||window.isEmpty($scope.secondPwd)){
                malert('请输入密码!');
                return;
            }
            var url = window.basePath + '/crm/Member/modifyMyPwd';
            var data={
                oldPwd: $scope.oldPwd,
                firstPwd: $scope.firstPwd,
                secondPwd: $scope.secondPwd
            };
            $http.post(url,data).success(function (re) {
                $scope.oldPwd = null;
                $scope.firstPwd = null;
                $scope.secondPwd = null;
                $scope.isOK = true;
                $scope.okOrFail=true;
                $scope.isBox=true;
            })
        }
        $scope.submitForm = function(){
            if(window.isEmpty($scope.selectArea[2].value) || window.isEmpty($scope.address) || window.isEmpty($scope.selectArea[2].name)){
                malert("请填写完整地址!");
                return;
            }
            if($scope.area.length>200){
                malert("所在区域不能超过200位");
                return;
            }
            if($scope.address.length>200){
                malert("街道地址不能超过200位");
                return;
            }
            if(!window.isEmpty($scope.myEmail)){
                if(!/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.myEmail)){
                    malert("邮箱格式错误");
                    return;
                }
            }

            if(window.isEmpty($scope.selectArea[2].value)){
                malert('请选择完整的省市区!');
                return;
            }else{
                $scope.areaValue=$scope.selectArea[2].pvalue+"_"+$scope.selectArea[2].value+"_";
            }
            var url = window.basePath + '/crm/Member/memberRealName';
            var data = {
                memberId : getCookie('_member_id'),
                realName : $scope.realName,
                idCard : $scope.cardNumber,
                email : $scope.myEmail,
                realArea : $scope.selectArea[0].name+$scope.selectArea[1].name+$scope.selectArea[2].name,
                realAreaValue : $scope.areaValue,
                realAddress : $scope.address
            }
            $http.post(url, data).success(function (re) {
                if(re.length!=0){
                    $scope.isRealName();
                    $scope.getMyInfo();
                }
                $rootScope["$$realNameArea"]=null;
            })
        }
        $scope.getMyInfo = function () {
            var url = window.basePath + '/crm/Member/getMyInfo';
            $http.get(url).success(function (re) {
                $scope.memberInfo = re.content;
            })
        }
        //是否实名认证
        $scope.isRealName = function(){
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + '/crm/Member/getMemberIsRealName?memberId='+$scope.memberId;
            $http.get(url).success(function(re){
                if(re.content.items[0].isRealName==true){
                    $scope.memberIsRealName=true;
                    $scope.getMyInfo();
                }else{
                    $scope.memberIsRealName=false;
                }
            })
        };//获取地理位置
        $scope.getArea = function (_id, type) {
            var url = window.basePath + '/crm/Member/getLocation?pid=' + _id;
            $http.get(url).success(function (re) {
                if (type == 1) {
                    $scope.areaListEd[0] = re.content.items;
                    $scope.areaListEd[0].unshift({name: '省'});

                    $scope.areaListEd[1] = [{name: '市'}];
                    $scope.areaListEd[2] = [{name: '县/镇/区'}];

                    $scope.selectArea[0] = $scope.areaListEd[0][0];
                    $scope.selectArea[1] = $scope.areaListEd[1][0];
                    $scope.selectArea[2] = $scope.areaListEd[2][0];
                    $scope.area = '';
                    $scope.areaValue = '';
                } else if (type == 2) {
                    $scope.areaListEd[1] = re.content.items;
                    $scope.areaListEd[1].unshift({name: '市'});

                    $scope.areaListEd[2] = [{name: '县/镇/区'}];

                    $scope.selectArea[1] = $scope.areaListEd[1][0];
                    $scope.selectArea[2] = $scope.areaListEd[2][0];
                    $scope.area = '';
                    $scope.areaValue = '';
                } else if (type == 3) {
                    $scope.areaListEd[2] = re.content.items;
                    $scope.areaListEd[2].unshift({name: '县/镇/区'});
                    $scope.selectArea[2] = $scope.areaListEd[2][0];
                    $scope.area = '';
                    $scope.areaValue = '';
                } else if (type == 4) {
                    $scope.area = $scope.selectArea[0].name + $scope.selectArea[1].name + $scope.selectArea[2].name;
                    $scope.areaValue = $scope.selectArea[2].pvalue + '_' + $scope.selectArea[2].value + '_';
                }

                if(!window.isEmpty($scope.initAreaValue)){
                    var text = $scope.initAreaValue;
                    var areaArr = text.substring(1, text.length - 1).split("_");
                    var index=type-1;
                    if(type <=3 ){
                        for(var i= 1,len=$scope.areaListEd[index].length;i<len;i++){
                            if($scope.areaListEd[index][i].value==areaArr[index]){
                                $scope.selectArea[index] = $scope.areaListEd[index][i];
                            }
                        }
                        if(typeof($scope.selectArea[index])=="undefined"){
                            $scope.areaListEd[index] = [{name: '---请选择---'}];
                            $scope.selectArea[index]=$scope.areaListEd[index][0];
                        }else{
                            $scope.getArea($scope.selectArea[index]._id,type+1);
                        }
                    }
                    //if(type==2 && $scope.initAreaValue==$scope.userInfo.areaValue){
                    //    $scope.initAreaValue=null;
                    //}
                }
            })
        }
        $scope.getAddress = function () {
            var url = window.basePath + '/crm/MemberAddress/getMemberAddress';
            $http.get(url).success(function (re) {
                $scope.queryAddress = re.content.items;
            })
        }

        $scope.delAddress = function (id) {
            var url = window.basePath + '/crm/MemberAddress/delAddress';
            var data={_id:id}
            $http.post(url,data).success(function () {
                $scope.getAddress();
            })
        }
        $scope.openUpdate=function(addressId){
            $scope.Uconsignee = '';
            $scope.UcheckMan = '';
            $scope.UphoneNumber ='';
            $scope.Uaddress = '';
            $scope.Upostalcode = '';
            $scope.UareaValue = '';
            $scope.Uarea = '';
            var url = window.basePath + '/crm/MemberAddress/getMemberAddress?_id=' + addressId;
            $http.get(url).success(function (re) {
                $scope.Uconsignee = re.content.items[0].name;
                $scope.UcheckMan = re.content.items[0].gender;
                $scope.UphoneNumber = re.content.items[0].phone;
                $scope.Uaddress = re.content.items[0].address;
                $scope.Upostalcode = re.content.items[0].postcode;
                $scope.UareaValue = re.content.items[0].areaValue;
                $scope.Uarea = re.content.items[0].area;
                $scope.updateTakeAdd=true;
                $scope.initAreaValue=$scope.UareaValue;
                $scope.addressId=addressId;
                $scope.getArea(-1, 1);
            })
        }
        $scope.updateAddress=function(id){
            var url = window.basePath + '/crm/MemberAddress/addAddress';
            var data = {
                consignee: $scope.Uconsignee,
                gender: $scope.UcheckMan,
                phone: $scope.UphoneNumber,
                address: $scope.Uaddress,
                area: $scope.area,
                areaValue: $scope.areaValue,
                postalcode: $scope.Upostalcode
            };
            if (!window.isEmpty(id)) {
                data._id=id;
            }
            $http.post(url, data).success(function () {
                $scope.updateTakeAdd=false;
                $scope.getAddress();
                $scope.areaValue='';
                $scope.area='';
            })
        }
        $scope.setDefaultAddress = function(addressId){
            var url = window.basePath + '/crm/MemberAddress/setDefaultAddress?addressId='+addressId;
            $http.get(url).success(function () {
                $scope.getAddress();
            })
        }

        $scope.submitTakeAddress=function(){
            if (!/^1[34578]{1}\d{9}$/.test($scope.phoneNumber) || window.isEmpty($scope.consignee)
                || window.isEmpty($scope.address) || window.isEmpty($scope.postalcode)) {
                malert('请填写正确的收货信息!');
                return;
            }
            if(window.isEmpty($scope.checkMan)){
                malert('请选择性别');
                return;
            }
            if(window.isEmpty($scope.selectArea[2].value)){
                malert('请选择完整的省市区!');
                return;
            }else{
                $scope.areaValue=$scope.selectArea[2].pvalue+"_"+$scope.selectArea[2].value+"_";
            }
            var url = window.basePath + '/crm/MemberAddress/addAddress';
            var data = {
                consignee: $scope.consignee,
                gender: $scope.checkMan,
                phone: $scope.phoneNumber,
                address: $scope.address,
                area: $scope.selectArea[0].name+$scope.selectArea[1].name+$scope.selectArea[2].name,
                areaValue: $scope.areaValue,
                postalcode: $scope.postalcode
            };
            if ($rootScope.pathParams.addressId) {
                data._id=$rootScope.pathParams.addressId;
            }
            $http.post(url, data).success(function () {
                $scope.addTakeAdd=false;
                $scope.getAddress();
                $scope.areaValue='';
                $scope.area='';
            })
        }
        $scope.addressMouseUp=function(index){
            $scope.addListIsHover=index;
        }
        $scope.addressMouseDown=function(){
            $scope.addListIsHover=-1;
        }

        $scope.initOpen=function(){
            $scope.addTakeAdd=true;
            $scope.Uconsignee = '';
            $scope.UcheckMan = '';
            $scope.UphoneNumber ='';
            $scope.Uaddress = '';
            $scope.Upostalcode = '';
            $scope.UareaValue = '';
            $scope.Uarea = '';
            $scope.initAreaValue='';
            $scope.areaListEd = [[{name: '省'}], [{name: '市'}], [{name: '县/镇/区'}]];
            $scope.getArea(-1, 1);
            $scope.areaValue = '';
            $scope.area = '';
            $scope.selectArea = new Array(3);
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '账户设置');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $scope.settingType='1';
                $scope.areaListEd = [[{name: '省'}], [{name: '市'}], [{name: '县/镇/区'}]];
                $scope.getArea(-1, 1);
                $scope.titleText="setting";
                $scope.areaValue='';
                $scope.area='';
                $scope.selectArea = new Array(3);
                $scope.memberIsRealName=true;
                $scope.addTakeAdd=false;
                $scope.updateTakeAdd=false;
                $scope.getAddress();
                $scope.isBox=false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);