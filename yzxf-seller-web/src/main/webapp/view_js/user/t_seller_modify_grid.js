(function () {
    window.modifySellerGrid = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            $scope.initUser();
            $scope.initWeek();
            $scope.ifFrist = true;
            $scope.checkNext = true;
            $scope.checkCancel = false;
            $scope.checkSubmit = false;
            $scope.selectArea = new Array(3);
            $scope.selectOperate = new Array(3);
            $scope.areaList = [[{name: '省'}], [{name: '市'}], [{name: '县/镇/区'}]];
            $scope.operateList = [[{name: '---请选择---'}], [{name: '---请选择---'}], [{name: '---请选择---'}]];
            $scope.getArea(-1, 1);
            $scope.getOperateType(-1, 1);
            $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
            $scope.showImg='';
            $scope.getLatAndLong();

            $scope.popWindowTemp = '/view/user/t_sellerAdd_grid.jsp';
        }

        $scope.addInit = function () {
            $scope.userInfo={};
            $scope.initUser();
            $scope.initWeek();
            $scope.checkNext = true;
            $scope.checkCancel = false;
            $scope.checkSubmit = false;
            $scope.selectArea = new Array(3);
            $scope.selectOperate = new Array(3);
            $scope.areaList = [[{name: '省'}], [{name: '市'}], [{name: '县/镇/区'}]];
            $scope.operateList = [[{name: '---请选择---'}], [{name: '---请选择---'}], [{name: '---请选择---'}]];
            $scope.getArea(-1, 1);
            $scope.getOperateType(-1, 1);
            $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
            //$scope.initAreaValue=null;
            //$scope.agentSelectValue[0]="";
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
                    belongArea: "",
                    belongAreaValue: $scope.agentSelectValue[0],
                    businessLicense: "",
                    closeTime: 21,
                    contactPerson: "",
                    idCardImgBack: "",
                    idCardImgFront: "",
                    idCardImgHand: "",
                    contractImg:"",
                    integralRate: "",
                    intro: "",
                    isMoneyTransaction:false,
                    isCouponVerification:false,
                    isRecommend:false,
                    isOnlinePay:false,
                    legalPerson: "",
                    name: "",
                    openTime: 9,
                    openWeek: "一,二,三,四,五,六,日",
                    operateType: "",
                    operateValue: "",
                    phone: "",
                    realCard: "",
                    serverPhone: "",
                    doorImg:"",
                    latitude:"",
                    longitude:"",
                    email:"",
                    bankAddress:""
                };
                $scope.initOperateValue=null;
                $scope.initAreaValue=null;
                $scope.agentSelectValue[0]="";
                $scope.isModify=false;
            }else{
                $scope.userInfo.pendingId = $scope.pendingId;
                $scope.initOperateValue=$scope.userInfo.operateValue;
                $scope.initAreaValue=$scope.userInfo.areaValue;
                $scope.agentSelectValue[0]=$scope.userInfo.belongAreaValue;
            }
            //临时存放
            $scope.imgMore={
                bankImg: window.isEmpty($scope.userInfo.bankImg)?[]:$scope.userInfo.bankImg.split("_"),
                contractImg:window.isEmpty($scope.userInfo.contractImg)?[]:$scope.userInfo.contractImg.split("_"),
                doorImg:window.isEmpty($scope.userInfo.doorImg)?[]:$scope.userInfo.doorImg.split("_")
            };
        }

        $scope.initWeek = function () {
            $scope.showWeek = false;
            $scope.weekList = [
                {name: '星期一', check: true, value: "一"},
                {name: '星期二', check: true, value: "二"},
                {name: '星期三', check: true, value: "三"},
                {name: '星期四', check: true, value: "四"},
                {name: '星期五', check: true, value: "五"},
                {name: '星期六', check: true, value: "六"},
                {name: '星期日', check: true, value: "日"}
            ]
        };

        $scope.checkWeekWin=function(){
            $scope.showWeek=!$scope.showWeek;
        }

        $scope.cancelWeek = function () {
            $scope.showWeek = false;
            if (!window.isEmpty($scope.userInfo.openWeek)) {
                var userWeek = $scope.userInfo.openWeek.split(",");
                for (var j = 0,i=0; j < 7; j++) {
                    if ($scope.weekList[j].value == userWeek[i]) {
                        i++;
                        $scope.weekList[j].check = true;
                    }else{
                        $scope.weekList[j].check = false;
                    }
                }
            }
        }

        $scope.setOpenWeek = function () {
            var weekList = $scope.weekList;
            var weekStr = "";
            for (var i = 0; i < weekList.length; i++) {
                if (weekList[i].check) {
                    weekStr += weekList[i].name.charAt(2) + ',';
                }
            }
            $scope.userInfo.openWeek = weekStr.substring(0, weekStr.length - 1);
            $scope.showWeek = false;
        };

        //营业时间: num:减去的数量;type:时间类型
        $scope.timeBtn = function (num, type) {
            if(typeof $scope.userInfo.openTime !=='number' || $scope.userInfo.openTime<0 || $scope.userInfo.openTime>24){
                $scope.userInfo.openTime=0;
            }
            if(typeof $scope.userInfo.closeTime !=='number' || $scope.userInfo.closeTime<0 || $scope.userInfo.closeTime>24){
                $scope.userInfo.closeTime=24;
            }
            if (type) {
                $scope.userInfo.openTime += num;
                if ($scope.userInfo.openTime < 0 || $scope.userInfo.closeTime <= $scope.userInfo.openTime) {
                    $scope.userInfo.openTime -= num;
                }
            } else {
                $scope.userInfo.closeTime += num;
                if ($scope.userInfo.closeTime <= $scope.userInfo.openTime || $scope.userInfo.closeTime > 24) {
                    $scope.userInfo.closeTime -= num;
                }
            }
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
                    $scope.userInfo.areaValue = $scope.selectArea[2].pvalue + '_' + $scope.selectArea[2].value + '_';
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

        //获取经营范围
        $scope.getOperateType = function (_id, type) {
            if(type-2>=0 && $scope.selectOperate[type-2].name=='---请选择---'){
                //var arr = $scope.userInfo.operateValue.substring(1,$scope.userInfo.operateValue.length-1).split("_");
                var typeGrade=type-3;
                if(typeGrade>=0){
                    $scope.userInfo.operateType = $scope.selectOperate[typeGrade].name;
                    if(typeGrade==0){
                        $scope.userInfo.operateValue = '_' +$scope.selectOperate[typeGrade].value + '_' ;
                    }else{
                        $scope.userInfo.operateValue = $scope.selectOperate[typeGrade].pvalue + '_' + $scope.selectOperate[typeGrade].value + '_';
                    }
                }else{
                    $scope.userInfo.operateType = '';
                    $scope.userInfo.operateValue = '';
                }
                for(var i= type-1;i<3;i++){
                    $scope.operateList[i] = [{name: '---请选择---'}];
                    $scope.selectOperate[i] = $scope.operateList[i][0];
                }
                return;
            }
            var url = window.basePath + '/account/Seller/getOperate?pid=' + _id;
            $http.get(url).success(function (re) {
                if (type == 1) {
                    $scope.operateList[0] = re.content.items;
                    $scope.operateList[0].unshift({name: '---请选择---'});

                    $scope.operateList[1] = [{name: '---请选择---'}];
                    $scope.operateList[2] = [{name: '---请选择---'}];

                    $scope.selectOperate[0] = $scope.operateList[0][0];
                    $scope.selectOperate[1] = $scope.operateList[1][0];
                    $scope.selectOperate[2] = $scope.operateList[2][0];
                } else if (type == 2) {
                    $scope.operateList[1] = re.content.items;
                    $scope.operateList[1].unshift({name: '---请选择---'});

                    $scope.operateList[2] = [{name: '---请选择---'}];

                    $scope.selectOperate[1] = $scope.operateList[1][0];
                    $scope.selectOperate[2] = $scope.operateList[2][0];
                } else if (type == 3) {
                    $scope.operateList[2] = re.content.items;
                    $scope.operateList[2].unshift({name: '---请选择---'});
                    $scope.selectOperate[2] = $scope.operateList[2][0];
                }
                if (type == 2) {
                    $scope.userInfo.operateType = $scope.selectOperate[type - 2].name;
                    $scope.userInfo.operateValue = '_' + $scope.selectOperate[type - 2].value + '_';
                } else if (type > 2) {
                    $scope.userInfo.operateType = $scope.selectOperate[type - 2].name;
                    $scope.userInfo.operateValue = $scope.selectOperate[type - 2].pvalue + '_' + $scope.selectOperate[type - 2].value + '_';
                }
                if(!window.isEmpty($scope.initOperateValue)){
                    var text = $scope.initOperateValue;
                    var operateArr = text.substring(1, text.length - 1).split("_");
                    var index=type-1;
                    if(type <=3 ){
                        if(index<operateArr.length){
                            for(var i= 1,len=$scope.operateList[index].length;i<len;i++){
                                if($scope.operateList[index][i].value==operateArr[index]){
                                    $scope.selectOperate[index] = $scope.operateList[index][i];
                                    break;
                                }
                            }
                        }

                        //$scope.selectOperate[index] = $scope.operateList[index][operateArr[index]];
                        if(typeof($scope.selectOperate[index])=="undefined"){
                            $scope.operateList[index] = [{name: '---请选择---'}];
                            $scope.selectOperate[index]=$scope.operateList[index][0];
                        }else{
                            if(index-1>0){
                                index-=1;
                            }
                            $scope.getOperateType($scope.selectOperate[index]._id,type+1);
                        }
                    }
                    var len=$scope.initOperateValue.substring(1, $scope.initOperateValue.length - 1).split("_").length+1;
                    if(type==len && $scope.initOperateValue==$scope.userInfo.operateValue){
                        $scope.initOperateValue=null;
                    }
                }
            });
        }

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
                entityName: 'Seller',
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
            var url = window.basePath + "/account/UserPending/saveApplySeller";
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
                malert("商家名称在100字以内!");
                return;
            }
            if (!/^(([123456789]|[123456789]\d)(\.\d+)?)$/.test($scope.userInfo.integralRate)) {
                malert("商家积分率应在1~99以内!");
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
            if (window.isEmpty($scope.userInfo.serverPhone) || $scope.userInfo.serverPhone.length>64) {
                malert('请输入正确的客服号码!');
                return;
            }
            if (window.isEmpty($scope.userInfo.openWeek)) {
                malert('请选择营业星期!');
                return;
            }
            if (window.isEmpty(String.valueOf($scope.userInfo.openTime)) || window.isEmpty(String.valueOf($scope.userInfo.closeTime))) {
                malert('请选择营业时间!');
                return;
            }
            if (window.isEmpty($scope.userInfo.operateType) || window.isEmpty($scope.userInfo.operateValue)
                || $scope.userInfo.operateType.indexOf("请选择")!=-1 || $scope.userInfo.operateType.indexOf("undefined")!=-1) {
                malert('请选择经营范围!');
                return;
            }
            if (window.isEmpty($scope.userInfo.areaValue) || window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length > 200
                || $scope.userInfo.areaValue.indexOf("请选择")!=-1 || $scope.userInfo.areaValue.indexOf("undefined")!=-1) {
                malert("请选择完整的所在区域位置!");
                return;
            }
            if (window.isEmpty($scope.userInfo.address) || $scope.userInfo.address.length > 200) {
                malert("请填写所在街道,且不能超过200位字符!");
                return;
            }
            if(window.isEmpty($scope.userInfo.latitude) || window.isEmpty($scope.userInfo.longitude)){
                malert("获取经纬度失败!");
                return;
            }
            if (!window.isEmpty($scope.userInfo.intro) && $scope.userInfo.intro.length > 300) {//可不填
                malert("商家简介不能超过300位字符!");
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
            if (window.isEmpty($scope.userInfo.bankName)) {
                malert("请输入银行类型!");
                return;
            }
            if (!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.bankUser)) {
                malert("请填写2~64位中文汉字之间的持卡人名字!");
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

            if (!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.legalPerson)) {
                malert("请填写2~64位中文汉字之间的法人名称!");
                return;
            }
            if (!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)) {
                malert("请输入正确的法人的身份证号码!");
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
            if ($scope.userInfo.bankUser != $scope.userInfo.legalPerson && window.isEmpty($scope.userInfo.idCardImgHand)) {
                malert("请上传手持身份证照片!");
                return;
            }
            if ($scope.imgMore.bankImg==null || $scope.imgMore.bankImg.length<1 || $scope.imgMore.bankImg.length>11){
                malert("请上传1~10张银行卡/开户许可证!");
                return;
            }else{
                $scope.formatImgMore("bankImg");
            }
            // if ($scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
            //     malert("请上传1~10张合同照片!");
            //     return;
            // }else{
            //     $scope.formatImgMore("contractImg");
            // }
            if ($scope.imgMore.doorImg.length<1 || $scope.imgMore.doorImg.length>11){
                malert("请上传1~10张店铺门头照!");
                return;
            }else{
                $scope.formatImgMore("doorImg");
            }
            var url = window.basePath + "/account/UserPending/submitSeller";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope.showPopWin = false;
                $scope.closePopWin();
                $scope.queryCurrentList();
            });
        }

        $scope.backBtn = function () {
            $scope.checkNext = true;
        };

        //根据地址获取店铺经纬度
        $scope.getLatAndLong = function(){
            if(!window.isEmpty($scope.userInfo.latitude) && !window.isEmpty($scope.userInfo.longitude)){
                $scope.initMap();
                return;
            }
            var url = 'https://apis.map.qq.com/ws/geocoder/v1/?address='+$scope.userInfo.area+$scope.userInfo.address+'&key=ZGWBZ-7CWW4-OG2UV-DA5LD-CAJTV-RKBXI&output=jsonp';
            $.ajax({
                async:false,
                url: url,
                type: "GET",
                dataType: 'jsonp',
                jsonp: 'callback',
                jsonpCallback: "QQmap",
                beforeSend: function(){
                },success: function (json) {//客户端jquery预先定义好的callback函数,成功获取跨域服务器上的json数据后,会动态执行这个callback函数
                    if(json.status==347){
                        $scope.userInfo.longitude='';
                        $scope.userInfo.latitude='';
                        malert('未获取到坐标!请重新输入详细地址!');
                    }else{
                        $scope.userInfo.longitude = json.result.location.lng;
                        $scope.userInfo.latitude = json.result.location.lat;
                    }
                    $scope.$apply();
                    $scope.initMap();
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
                zoom: 18,
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
            //点击地图,更新坐标位置
            qq.maps.event.addListener($scope.map, 'click', function(event) {
                $scope.userInfo.latitude=event.latLng.getLat();
                $scope.userInfo.longitude=event.latLng.getLng();
                $scope.$apply();
                $scope.initMap();
                //$scope.getAreaAddress();
            });
            $scope.$apply();
        };

        //逆地址解析
        //$scope.getAreaAddress=function(){
        //    var url = "http://apis.map.qq.com/ws/geocoder/v1/?location="+$scope.userInfo.latitude+","+$scope.userInfo.longitude
        //        +"&key=ZGWBZ-7CWW4-OG2UV-DA5LD-CAJTV-RKBXI&output=jsonp";
        //
        //    $.ajax({
        //        async:false,
        //        url: url,
        //        type: "GET",
        //        dataType: 'jsonp',
        //        jsonp: 'callback',
        //        jsonpCallback: "QQmap",
        //        beforeSend: function(){
        //        },success: function (json) {//客户端jquery预先定义好的callback函数,成功获取跨域服务器上的json数据后,会动态执行这个callback函数
        //            if(json.status!=0){
        //                malert('未获取到坐标!请重新输入详细地址!');
        //            }else{
        //                $scope.jsonTemp=JSON.stringify(json);
        //            }
        //        }
        //    });
        //}

        $scope.init();
    }
})();