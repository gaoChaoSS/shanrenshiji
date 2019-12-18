(function (angular, undefined) {

    var model = 'other';
    var entity = 'sellerApply';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope["$$sellerApplyArea"]=null;
        $rootScope["$$sellerApply"]=null;
        $rootScope.showAgreePage=false;


        $scope.markList = [
            {title3:'请选择'},
            {_id3:'0',title3:'线上'},
            {_id3:'1',title3:'线下'},
            {_id3:'2',title3:'线上线下'},
        ]

        $scope.merchantsTypeList = [
            {title4:'请选择'},
            {_id4:'0',title4:'小微商户'},
            {_id4:'3',title4:'普通商户'},
        ]

        $scope.companyCertificateTypeList = [
            {title6:'请选择'},
            {_id6:'00',title6:'三证合一'},
            {_id6:'01',title6:'营业执照'},
        ]

        $scope.accountTypeList = [
            {title7:'请选择'},
            {_id7:'00',title7:'对公'},
            {_id7:'01',title7:'对私'},
        ]

        $scope.contactTypeList = [
            {title8:'请选择'},
            {_id8:'1',title8:'法人'},
            {_id8:'2',title8:'代理人'},
        ]

        $scope.initUser = function () {
            $scope.userInfo = {
                pendingId: "",//待审表ID
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
        };
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


        $scope.changeLpcertval=function () {
            $scope.userInfo.lpcertval="999999999";
        }

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

        //检查户名和法人是否一致
        $scope.checkBankUser=function(){
            $scope.isNeedIdCardImgHand = $scope.userInfo.bankUser == $scope.userInfo.legalPerson;
            return $scope.isNeedIdCardImgHand;
        };

        $scope.goBackFun=function(){
            $rootScope.notRef=false;
            $rootScope.goBack();
        }

        $scope.checkPendType=function(){
            var isGetApply=true;
            if($rootScope["$$sellerApplyArea"]!=null) {
                $scope.userInfo.area = $rootScope["$$sellerApplyArea"].locationArea;
                $scope.userInfo.areaValue = $rootScope["$$sellerApplyArea"].locationAllValue;
                isGetApply=false;
            }
            if($rootScope["$$sellerApply"]!=null) {
                $scope.userInfo.operateType = $rootScope["$$sellerApply"].operateName;
                $scope.userInfo.operateValue = $rootScope["$$sellerApply"].operateValue;
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
                $scope.getPend('/account/UserPending/getOtherIsApply?ownerType=Seller&createId='+otherId);
            }else{
                $scope.getPend('/account/UserPending/getMemberIsApply?ownerType=Seller');
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
                $scope.checkBankUser();
                //临时存放
                $scope.imgMore={
                    bankImg: $scope.userInfo.bankImg.split("_"),
                    contractImg:$scope.userInfo.contractImg.split("_"),
                    doorImg:$scope.userInfo.doorImg.split("_"),
                };
                $scope.userPending = re.content;

                if(!window.isEmpty(getCookie("_other_id"))){
                    $scope.userInfo["creatorType"] = "other";
                }
            })
        };

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
            if(!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.contactPerson)){
                malert("请填写2~10位中文汉字之间的联系人名字!");
                return;
            }
            if(!/^1[3456789]{1}\d{9}$/.test($scope.userInfo.phone)) {
                malert('联系手机格式不正确!');
                return;
            }
           /* if(window.isEmpty($scope.userInfo.area) || $scope.userInfo.area.length>200){
                malert("请选择完整的所在区域位置!");
                return;
            }*/
            var url = window.basePath + "/account/UserPending/userApplyFirst";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope["$$sellerApplyArea"]=null;
                $scope.userPending.status=0.1;
            });
        };

        $scope.submitForm = function(){
            if(!window.isEmpty($scope.userInfo.name && /^[\u4E00-\u9FA5  0-9A-Za-z]{2,64}$/.test($scope.userInfo.name))){
                var url = window.basePath + "/account/Seller/findSellerbyName";
                $http.post(url, $scope.userInfo).success(function (re) {
                    if (re.content._id != null && re.content._id != "") {
                        malert("该商户名称已被注册");
                    }
                });
            }else {
                malert("商户名称只能输入中文汉字或数字0-9及英文字母大小写，长度100字以内!");
                return;
            }

            if(!window.isEmpty($scope.userInfo.merchantsAbbreviation) && $scope.userInfo.merchantsAbbreviation.length<=4){
                if(!/^[\u4E00-\u9FA5  0-9A-Za-z]{2,64}$/.test($scope.userInfo.merchantsAbbreviation)){
                    malert("商户简称只能输入中文汉字或数字0-9及英文字母大小写!");
                    return;
                }
            }else {
                malert("请输入商户简称，且长度4字以内!");
                return;
            }
            if(window.isEmpty($scope.userInfo.mark)){
                malert("请选择线上线下标志!");
                return;
            }
            if(!/^(([123456789]|[123456789]\d)(\.\d+)?)$/.test($scope.userInfo.integralRate)){
                malert("积分率应在1~99以内!");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.contactPerson)){
                malert("请填写2~64位中文汉字之间的联系人名字!");
                return;
            }
            if(window.isEmpty($scope.userInfo.phone) || !/^1[3456789]{1}\d{9}$/.test($scope.userInfo.phone)) {
                malert('联系手机格式不正确!');
                return;
            }
            if(!window.isEmpty($scope.userInfo.email)){
                if(!/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.userInfo.email)){
                    malert("邮箱格式错误!");
                    return;
                }
            }else {
                malert("请输入电子邮箱!");
                return;
            }
            if(window.isEmpty($scope.userInfo.registeredCapital)){
                malert("请填写注册资本");
                return;
            }else if(!window.isEmpty($scope.userInfo.registeredCapital) && !/^[1-9]{1}[0-9]{1,9}$/.test($scope.userInfo.registeredCapital)){
                malert("注册资本只能为大于零的数字且最多十位");
                return;
            }
            if (window.isEmpty($scope.userInfo.serverPhone) || !/^1[3456789]{1}\d{9}$/.test($scope.userInfo.serverPhone)) {
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
            if (window.isEmpty($scope.userInfo.operateType)) {
                malert('请选择经营范围!');
                return;
            }
            if(window.isEmpty($scope.userInfo.areaValue)){
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
            if(!/^[0-9]{16,19}$/.test($scope.userInfo.bankId)){
                malert("请输入正确的银行账号!");
                return;
            }
            if(window.isEmpty($scope.userInfo.accountType)){
                malert("请选择账户类型!");
                return;
            }
            if(window.isEmpty($scope.userInfo.contactType)){
                malert("请选择开户人类型!");
                return;
            }
            if(!window.isEmpty($scope.userInfo.bankName)){
                var url = window.basePath + "/payment/Gpay/findBankbyName";
                $http.post(url, $scope.userInfo).success(function (re) {
                    if (re.content.code != null && re.content.code != "") {
                        $scope.userInfo.basebank = re.content.code;
                    } else {
                        malert("开户行填写错误,请查正后再次填写");
                    }
                });
            }else {
                malert("请输入开户行!");
                return;
            }
            if(window.isEmpty($scope.userInfo.bankAddress) || $scope.userInfo.bankAddress.length>200){
                malert("请输入正确的开户行地址（200个字符长度以内）");
                return;
            }
            if(!/^[\u4E00-\u9FA5 A-Za-z]{2,64}$/.test($scope.userInfo.bankUser)){
                malert("请填写2~64位中文汉字或英文大小写字母之间的户名!");
                return;
            }
            if (window.isEmpty($scope.imgMore.bankImg) || $scope.imgMore.bankImg.length>10){
                malert("请上传1~10张银行卡正反面/开户许可证!");
                return;
            }else{
                $scope.formatImgMore("bankImg");
            }
            if($scope.userInfo.contactType == '2'){
                if(window.isEmpty($scope.userInfo.authphoto)){
                    malert("请上传代理人授权书照片!");
                    return;
                }
                if(window.isEmpty($scope.userInfo.agentphotof)){
                    malert("请上传代理人证件正面照!");
                    return;
                }
                if(window.isEmpty($scope.userInfo.agentphotob)){
                    malert("请上传代理人证件背面照!");
                    return;
                }
            }
            if(!window.isEmpty($scope.userInfo.merchantsType)) {
                if ($scope.userInfo.merchantsType != '0') {
                    if (window.isEmpty($scope.userInfo.companyCertificateType)) {
                        malert("选择一种商户证件类型");
                        return;
                    }
                    if (window.isEmpty($scope.userInfo.companyIdNumber)) {
                        malert("请填写商户证件号");
                        return;
                    }
                    if (window.isEmpty($scope.userInfo.registeredAddress)) {
                        malert("请填写证件注册地址");
                        return;
                    }
                    if (window.isEmpty($scope.userInfo.businessLicense)) {
                        malert("请上传营业执照!");
                        return;
                    }
                }
            }else {
                malert("选择一种商户类型");
                return;
            }
            if(window.isEmpty($scope.userInfo.legalPerson)){
                malert("请填写法人名称!");
                return;
            }
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.realCard)){
                malert("请输入正确的法人身份证号码!");
                return;
            }
            if(window.isEmpty($scope.userInfo.lpcertval)){
                malert("请填写法人证件有效期！");
                return;
            }
            if(!window.isEmpty($scope.userInfo.lpcertval) && $scope.userInfo.lpcertval !=999999999){
                if(!/^(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)$/.test($scope.userInfo.lpcertval) ){
                    malert("法人证件有效期格式填写错误！");
                    return;
                }
            }
            if(window.isEmpty($scope.userInfo.idCardImgFront)){
                malert("请上传法人身份证正面照片!");
                return;
            }
            if(window.isEmpty($scope.userInfo.idCardImgBack)){
                malert("请上传法人身份证背面照片!");
                return;
            }
            if(!$scope.isNeedIdCardImgHand && window.isEmpty($scope.userInfo.idCardImgHand)){
                malert("请上传法人手持身份证照片!");
                return;
            }

            //if ($scope.userInfo.contractImg==null || $scope.imgMore.contractImg.length<1 || $scope.imgMore.contractImg.length>11){
            //    malert("请上传1~10张合同照片!");
            //    return;
            //}else{
            //    $scope.formatImgMore("contractImg");
            //}
            if ($scope.imgMore.doorImg==null || $scope.imgMore.doorImg.length<1 || $scope.imgMore.doorImg.length>11){
                malert("请上传1~10张店铺门头照片!");
                return;
            }else{
                $scope.formatImgMore("doorImg");
            }

            if(window.isEmpty($scope.userInfo.sitephoto)){
                malert("请上传经营场所照!");
                return;
            }

            if(window.isEmpty($scope.userInfo.province_code)){
                $scope.userInfo.province_code=$rootScope["$$sellerApplyArea"].locationId[0];
            }
            if(window.isEmpty($scope.userInfo.city_code)){
                $scope.userInfo.city_code=$rootScope["$$sellerApplyArea"].locationId[1];
            }
            if(window.isEmpty($scope.userInfo.district_code)){
                $scope.userInfo.district_code=$rootScope["$$sellerApplyArea"].locationId[2];
            }
            if(window.isEmpty($scope.userInfo.trade)){
                $scope.userInfo.trade=$rootScope["$$sellerApply"].industryId[2];
            }
            $scope.userInfo.paytype="";
            if($scope.userInfo.Unionpay && $scope.userInfo.paytype==""){
                $scope.userInfo.paytype=$scope.userInfo.paytype+"00";
            }else if($scope.userInfo.Unionpay && $scope.userInfo.paytype!=""){
                $scope.userInfo.paytype=$scope.userInfo.paytype+",00";
            }
            if($scope.userInfo.Alipay && $scope.userInfo.paytype!=""){
                $scope.userInfo.paytype=$scope.userInfo.paytype+",01";
            }else if($scope.userInfo.Alipay && $scope.userInfo.paytype==""){
                $scope.userInfo.paytype=$scope.userInfo.paytype+"01";
            }
            if($scope.userInfo.Wechat && $scope.userInfo.paytype!=""){
                $scope.userInfo.paytype=$scope.userInfo.paytype+",02";
            }else if($scope.userInfo.Wechat && $scope.userInfo.paytype==""){
                $scope.userInfo.paytype=$scope.userInfo.paytype+"02";
            }
            if(window.isEmpty($scope.userInfo.paytype)|| $scope.userInfo.paytype == null || $scope.userInfo.paytype==""){
                malert("请选择支付方式!");
                return;
            }
            var url = window.basePath + "/payment/Gpay/submitAccount";
            //var url = window.basePath + "/account/UserPending/submitSeller";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope["$$sellerApplyArea"]=null;
                $rootScope["$$sellerApply"]=null;
                $rootScope.goPage("/other/other");
            });
        };

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
                $scope.checkPendType();
                $scope.initWeek();
                $scope.upSample=false;
                $scope.showMap=false;
                $scope.isMobile=window.isMobile();
                $scope.showUploadName='';
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);
