(function (angular, undefined) {
    var model = 'store';
    var entity = 'storeApply';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope["$$sellerApplyArea"]=null;
        $rootScope["$$sellerApplyOperate"]=null;
        $rootScope.showAgreePage=false;
        $scope.initUser = function () {
            $scope.userInfo = {
                pendingId: "",//待审表IDs
                _id: "",
                address: "",
                area: "",
                areaValue: "",
                bankId: "",
                bankName: "",
                bankImg:"",
                bankUser: "",
                //bankUserCardId: "",
                //bankUserPhone: "",
                businessLicense: "",
                closeTime: 21,
                contactPerson: "",
                idCardImgBack: "",
                idCardImgFront: "",
                idCardImgHand: "",
                contractImg:"",
                integralRate: "",
                intro: "",
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
            $rootScope.showAgreePage=true;
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

        $scope.closeWeek=function(){
            $scope.showWeek = false;
        };

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

        $scope.getIsApply = function(){
            var isGetApply=true;
            if($rootScope["$$sellerApplyArea"]!=null) {
                $scope.userInfo.area = $rootScope["$$sellerApplyArea"].locationArea;
                $scope.userInfo.areaValue = $rootScope["$$sellerApplyArea"].locationAllValue;
                isGetApply=false;
            }
            if($rootScope["$$sellerApplyOperate"]!=null) {
                $scope.userInfo.operateType = $rootScope["$$sellerApplyOperate"].operateType;
                $scope.userInfo.operateValue = $rootScope["$$sellerApplyOperate"].operateValue;
                isGetApply=false;
            }
            if(isGetApply){
                var url = window.basePath + '/account/UserPending/getSellerIsApply?ownerType=Seller';
                $http.get(url).success(function (re) {
                    if (re.content.text==null || re.content.text.status==0) {
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
                    }  else {
                        $scope.userInfo = re.content.text;
                        if ($scope.userInfo == null) {
                            $scope.initUser();
                        }
                    }
                    //临时存放
                    $scope.imgMore={
                        bankImg: $scope.userInfo.bankImg.split("_"),
                        contractImg:$scope.userInfo.contractImg.split("_"),
                        doorImg:$scope.userInfo.doorImg.split("_")
                    };
                    $scope.userPending = re.content;
                })
            }
        }

        $scope.uploadFile = function (inputObj, type) {
            if($scope.isMobile){
                type=$scope.showUploadName;
            }
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
                $rootScope["$$sellerApplyArea"]=null;
                $scope.userPending.status=0.1;
            });
        };

        $scope.submitForm = function(){
            if(window.isEmpty($scope.userInfo.name) || $scope.userInfo.name.length>100){
                malert("商家名称在100字以内!");
                return;
            }
            if(!/^(([123456789]|[123456789]\d)(\.\d+)?)$/.test($scope.userInfo.integralRate)){
                malert("商家积分率应在1~99以内的整数!");
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
            if(!window.isEmpty($scope.userInfo.email) && !/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.userInfo.email)){
                malert("邮箱格式错误");
                return;
            }
            if (window.isEmpty($scope.userInfo.serverPhone) || $scope.userInfo.serverPhone.length>64) {
                malert('请输入正确的客服号码!');
                return;
            }
            if(window.isEmpty($scope.userInfo.openWeek)) {
                malert('请选择营业星期!');
                return;
            }
            if(window.isEmpty(String.valueOf($scope.userInfo.openTime)) || window.isEmpty(String.valueOf($scope.userInfo.closeTime))) {
                malert('请选择营业时间!');
                return;
            }
            if(window.isEmpty($scope.userInfo.operateType) || window.isEmpty($scope.userInfo.operateValue)) {
                malert('请选择经营范围!');
                return;
            }
            if(window.isEmpty($scope.userInfo.areaValue) || window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length>200){
                malert("请选择完整的所在区域位置!");
                return;
            }
            if(window.isEmpty($scope.userInfo.address) || $scope.userInfo.address.length>200){
                malert("请填写所在街道,且不能超过200位字符!");
                return;
            }
            if(window.isEmpty($scope.userInfo.latitude) || window.isEmpty($scope.userInfo.longitude)){
                malert("获取经纬度失败!");
                return;
            }
            if(!window.isEmpty($scope.userInfo.intro) && $scope.userInfo.intro.length>300){//可不填
                malert("商家简介不能超过300位字符!");
                return;
            }
            if(!/^[0-9]{16,19}$/.test($scope.userInfo.bankId)){
                malert("请输入正确的银行账号!");
                return;
            }
            if(window.isEmpty($scope.userInfo.bankName)){
                malert("请输入开户行!");
                return;
            }
            if(!window.isEmpty($scope.userInfo.bankName) && $scope.userInfo.bankAddress.length>200){
                malert("请输入正确的开户行地址（200个字符长度以内）");
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
            //if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.bankUserCardId)){
            //    malert("请输入正确的持卡人身份证号码!");
            //    return;
            //}
            if(window.isEmpty($scope.userInfo.legalPerson)){
                malert("请填写法人名称!");
                return;
            }
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)){
                malert("请输入正确的法人的身份证号码!");
                return;
            }
            if(window.isEmpty($scope.userInfo.businessLicense)){
                malert("请上传营业执照!");
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
            if($scope.userInfo.bankUser != $scope.userInfo.legalPerson && window.isEmpty($scope.userInfo.idCardImgHand)){
                malert("请上传手持身份证照片!");
                return;
            }
            if ($scope.imgMore.bankImg==null || $scope.imgMore.bankImg.length<1 || $scope.imgMore.bankImg.length>11){
                malert("请上传1~10张银行卡/开户许可证!");
                return;
            }else{
                $scope.formatImgMore("bankImg");
            }
            //if ($scope.userInfo.contractImg==null || $scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
            //    malert("请上传1~10张合同照片!");
            //    return;
            //}else{
            //    $scope.formatImgMore("contractImg");
            //}
            if ($scope.imgMore.doorImg==null || $scope.imgMore.doorImg.length<1 || $scope.imgMore.doorImg.length>11){
                malert("请上传1~10张店铺门头照!");
                return;
            }else{
                $scope.formatImgMore("doorImg");
            }

            var url = window.basePath + "/account/UserPending/submitSeller";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope["$$sellerApplyArea"]=null;
                $rootScope["$$sellerApplyOperate"]=null;
                $scope.getIsApply();
            });
        }

        //根据地址获取店铺经纬度
        $scope.getLatAndLong = function(){
            $scope.showMap=true;
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
        $scope.initMap = function (check) {
            $scope.showMap=true;
            if(window.isEmpty($scope.userInfo.latitude) || window.isEmpty($scope.userInfo.longitude)){
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
            if(check!='noClick'){
                //点击地图,更新坐标位置
                qq.maps.event.addListener($scope.map, 'click', function(event) {
                    $scope.userInfo.latitude=event.latLng.getLat();
                    $scope.userInfo.longitude=event.latLng.getLng();
                    $scope.$apply();
                    $scope.initMap();
                    //$scope.getAreaAddress();
                });
            }
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
                window.setWindowTitle($rootScope, '商家申请');
                $scope.getIsApply();
                $scope.initWeek();
                $scope.upSample=false;
                $scope.isMobile=window.isMobile();
                $scope.showUploadName='';
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);
