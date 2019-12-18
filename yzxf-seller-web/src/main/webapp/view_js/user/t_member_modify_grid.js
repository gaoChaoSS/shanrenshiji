(function () {
    window.modifyMemberGrid = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            $scope.userInfo={};
            $scope.selectArea = new Array(3);
            $scope.areaList = [[{name: '省'}], [{name: '市'}], [{name: '县/镇/区'}]];
            $scope.getArea(-1, 1);

            if($scope.modifyType==='modify'){
                $scope.getInfo();
            }else if($scope.modifyType==='add'){
                $scope.initUserInfo();
            }else{
                malert("操作失败，请重试");
            }
        };

        $scope.initUserInfo = function(){
            $scope.userInfo={
                mobile:'',
                realName:'',
                sex:'0',
                email:'',
                idCard:'',
                realArea:'',
                realAreaValue:'',
                realAddress:'',
            };
            $scope.initAreaValue='';
        }

        $scope.getInfo=function(){
            var url = window.basePath + "/" + $scope.model + "/" + $scope.entity + "/show?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.allInfo=re.content;
                var entity = ["_id","mobile","realName","sex","email","realArea","realAreaValue","realAddress","idCard","isRealName","canUse"];
                for(var i=0,max = entity.length;i<max;i++){
                    $scope.userInfo[entity[i]]=re.content[entity[i]];
                }

                $scope.initAreaValue=$scope.allInfo.realAreaValue;
                $scope.getBelongAgentName($scope.allInfo.belongAreaValue);
            });
        }

        $scope.getBelongAgentName=function(value){
            if(window.isEmpty(value)){
                return;
            }
            var url = window.basePath + "/account/Agent/getAgentAreaValueById?areaValue=" + value;
            $http.get(url).success(function (re) {
                $scope.agentNameAll = re.content.agentNameAll;
            });
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
                    $scope.userInfo.realArea='';
                    $scope.userInfo.realAreaValue ='';
                } else if (type == 2) {
                    $scope.areaList[1] = re.content.items;
                    $scope.areaList[1].unshift({name: '市'});

                    $scope.areaList[2] = [{name: '县/镇/区'}];

                    $scope.selectArea[1] = $scope.areaList[1][0];
                    $scope.selectArea[2] = $scope.areaList[2][0];
                    $scope.userInfo.realArea='';
                    $scope.userInfo.realAreaValue ='';
                } else if (type == 3) {
                    $scope.areaList[2] = re.content.items;
                    $scope.areaList[2].unshift({name: '县/镇/区'});
                    $scope.selectArea[2] = $scope.areaList[2][0];
                    $scope.userInfo.realArea='';
                    $scope.userInfo.realAreaValue ='';
                } else if (type == 4) {
                    $scope.userInfo.realArea = $scope.selectArea[0].name + $scope.selectArea[1].name + $scope.selectArea[2].name;
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

        $scope.submitForm = function () {
            if (!/^1[3456789]{1}\d{9}$/.test($scope.userInfo.mobile)) {
                malert('手机格式不正确!');
                return;
            }
            if (!window.isEmpty($scope.userInfo.realName) && !/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.realName)) {
                malert("请填写2~64位中文汉字之间的会员名字!");
                return;
            }
            if ($scope.userInfo.sex!=="1" && $scope.userInfo.sex!=="2"){
                malert('请选择会员性别!');
                return;
            }
            if (!window.isEmpty($scope.userInfo.email) && !/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.userInfo.email)){
                malert('电子邮格式错误!');
                return;
            }
            if (!window.isEmpty($scope.userInfo.realAreaValue) && (window.isEmpty($scope.userInfo.realArea) || $scope.userInfo.realArea.length > 200
                || $scope.userInfo.realAreaValue.indexOf("请选择")!=-1 || $scope.userInfo.realAreaValue.indexOf("undefined")!=-1)) {
                malert("请选择完整的所在区域位置!");
                return;
            }
            if (!window.isEmpty($scope.userInfo.address) && $scope.userInfo.address.length > 200) {
                malert("请填写所在街道,且不能超过200位字符!");
                return;
            }
            if (!window.isEmpty($scope.userInfo.idCard) && !/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.idCard)) {
                malert("请输入正确的法人的身份证号码!");
                return;
            }
            if(window.isEmpty($scope.modifyType)){
                malert("操作失败，请重试");
                return;
            }

            var url = window.basePath + "/crm/Member/modifyMember";
            $scope.userInfo['modifyType'] = $scope.modifyType;
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope.showPopWin = false;
                $scope.closePopWin();
                $scope.queryCurrentList();
            });
        }

        $scope.init();
    }
})();