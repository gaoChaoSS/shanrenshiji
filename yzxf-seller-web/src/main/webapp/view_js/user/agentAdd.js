(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = "agent";
    var entity = "agentAdd";
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $rootScope.getFixed($scope.model, $scope.entity);

        $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);

        $scope.areaList=[[{name:'省'}],[{name:'市'}],[{name:'县/镇/区'}]];

        $scope.areaValue="";

        $scope.cancelCheck=false;

        $scope.agentInfo = {
            _id: "",
            name: "",
            area: "",
            areaValue:"",
            address: "",
            contactPerson: "",
            realCard: "",
            phone: "",
            businessLicense: "",
            idCardImgFront: "",
            idCardImgBack: "",
            idCardImgHand: ""
        };

        $scope.iconImgUrl = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
        };

        $scope.cancelBtn = function () {
            if(window.isEmpty($scope.agentInfo._id)){
                history.back();
            }else{
                var url = window.basePath + "/account/Agent/deleteAgent";
                $http.post(url, {idStr:$scope.agentInfo._id}).success(function () {
                    history.back();
                });
            }
        };

        $scope.uploadFile = function (inputObj, type) {
            window.uploadWinObj = {
                one: true,
                entityName: 'Agent',
                entityField: type,
                entityId: $scope.agentInfo._id,
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        if (type == 'businessLicense') {
                            $scope.agentInfo.businessLicense = options.fileId;
                        }
                        else if (type == 'idCardImgFront') {
                            $scope.agentInfo.idCardImgFront = options.fileId;
                        }
                        else if (type == 'idCardImgBack') {
                            $scope.agentInfo.idCardImgBack = options.fileId;
                        }
                        else if (type == 'idCardImgHand') {
                            $scope.agentInfo.idCardImgHand = options.fileId;
                        }
                        $scope.save(false);
                    });
                }
            };
            window.uploadWinObj.files = inputObj.files;
            window.uploadFile();
        };

        $scope.save = function () {
            var url = window.basePath + "/account/Agent/saveApplyAgent";
            $http.post(url, $scope.agentInfo).success(function (re) {
                if(window.isEmpty($scope.agentInfo._id)){
                    $scope.agentInfo._id=re.content._id;
                }
                $scope.nextCheck=true;
            });
        };

        $scope.delFileItem = function (imgId) {
            if (imgId == 'businessLicense') {
                $scope.agentInfo.businessLicense = null;
            }
            else if(imgId == 'idCardImgFront'){
                $scope.agentInfo.idCardImgFront = null;
            }
            else if(imgId == 'idCardImgBack'){
                $scope.agentInfo.idCardImgBack = null;
            }
            else if(imgId == 'idCardImgHand'){
                $scope.agentInfo.idCardImgHand = null;
            }
            $scope.save();
        };

        //获取地理位置
        $scope.getLocation = function (_id, type) {
            var url = window.basePath + '/crm/Member/getLocation?pid=' + _id;
            $http.get(url).success(function (re) {
                if(type==1){
                    $scope.areaList[0]=re.content.items;
                    $scope.areaList[0].unshift({name:'省'});

                    $scope.areaList[1]=[{name:'市'}];
                    $scope.areaList[2]=[{name:'县/镇/区'}];

                    $scope.select1=$scope.areaList[0][0];
                    $scope.select2=$scope.areaList[1][0];
                    $scope.select3=$scope.areaList[2][0];
                }else if(type==2){
                    $scope.areaList[1]=re.content.items;
                    $scope.areaList[1].unshift({name:'市'});

                    $scope.areaList[2]=[{name:'县/镇/区'}];

                    $scope.select2=$scope.areaList[1][0];
                    $scope.select3=$scope.areaList[2][0];
                }else if(type==3){
                    $scope.areaList[2]=re.content.items;
                    $scope.areaList[2].unshift({name:'县/镇/区'});
                    $scope.select3=$scope.areaList[2][0];
                }else if(type==4){
                    $scope.agentInfo.area=$scope.select1.name+$scope.select2.name+$scope.select3.name;
                    $scope.agentInfo.areaValue=$scope.select3.pvalue+'_'+$scope.select3.value+'_';
                }
            });
        }

        //第一步:提交代理商基本信息,返回ID
        $scope.nextFun=function(){
            if(window.isEmpty($scope.agentInfo.name)){
                malert("请填写代理商名称!");
                return;
            }
            if(window.isEmpty($scope.agentInfo.areaValue)  || window.isEmpty($scope.agentInfo.area)){
                malert("请选择完整的所在区域位置!");
                return;
            }
            if($scope.agentInfo.area.length>200){
                malert("所在区域不能超过200位");
                return;
            }
            if(window.isEmpty($scope.agentInfo.address)){
                malert("请填写所在街道!");
                return;
            }
            if($scope.agentInfo.address.length>200){
                malert("街道地址不能超过64位");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,10}$/.test($scope.agentInfo.contactPerson)){
                malert("请填写2~10位中文汉字之间的联系人名字!");
                return;
            }
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.agentInfo.realCard)){
                malert("身份证格式错误!");
                return;
            }
            if(!/^1[3456789]{1}\d{9}$/.test($scope.agentInfo.phone)) {
                malert('手机号码格式不正确');
                return;
            }
            $scope.save();
        }

        $scope.submitForm=function(){
            if($scope.agentInfo.businessLicense==null){
                malert("请上传营业执照");
                return;
            }
            if($scope.agentInfo.idCardImgFront==null){
                malert("请上传身份证正面照片");
                return;
            }
            if($scope.agentInfo.idCardImgBack==null){
                malert("请上传身份证背面照片");
                return;
            }
            if($scope.agentInfo.idCardImgHand==null){
                malert("请上传手持身份证照片");
                return;
            }

            var url = window.basePath + '/account/Agent/addAgent';
            $http.post(url, $scope.agentInfo).success(function () {
                malert("添加代理商成功!");
                goPage('#/agent/agentAudit');
            })
        }

        $scope.getLocation(-1,1);
    });
})(angular);