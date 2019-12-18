(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.entityTitle = "广告投放管理";
        $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
        $scope.associatedType='';
        $scope.associatedId='';
        initGrid($rootScope, $scope, $http);
        $scope.getMemberBanner = function(){
            var url = window.basePath +'/account/User/getMemberBanner';
            $http.get(url).success(function(re){
                $scope.memberBanner = re.content.items;
                for(var i = 0,len=3-re.content.items.length;i<len;i++){
                    $scope.memberBanner.push({_id:'',icon:'',serialNum:i+1});
                }
            })
        }
        $scope.getMallBanner = function(){
            var url = window.basePath +'/account/User/getMallBanner';
            $http.get(url).success(function(re){
                $scope.mallBanner = re.content.items;
                for(var i = 0,len=3-re.content.items.length;i<len;i++){
                    $scope.mallBanner.push({_id:'',icon:'',serialNum:i+1});
                }
            })
        }

        $scope.getIndexIcon = function(){
            var url = window.basePath +'/account/User/getIndexIcon';
            $http.get(url).success(function(re){
                $scope.indexIconOne = re.content.items;
            })
        }
        $scope.getMallIndexIcon= function(){
            var url = window.basePath +'/account/User/getMallIndexIcon';
            $http.get(url).success(function(re){
                $scope.mallIndexIconOne = re.content.items;
            })
        }
        $scope.getMallAdvertising = function(){
            var url = window.basePath +'/account/User/getMallAdvertising';
            $http.get(url).success(function(re){
                $scope.mallAdvertising = re.content.items;
                for(var i = 0,len=2-re.content.items.length;i<len;i++){
                    $scope.mallAdvertising.push({_id:'',icon:'',serialNum:i+1});
                }
            })
        }
        $scope.getMallRectangle = function(){
            var url = window.basePath +'/account/User/getMallRectangle';
            $http.get(url).success(function(re){
                $scope.mallRectangle = re.content.items;
                for(var i = 0,len=5-re.content.items.length;i<len;i++){
                    $scope.mallRectangle.push({_id:'',icon:'',serialNum:i+1});
                }
            })
        }
        $scope.getImg=function(){
            $scope.getMemberBanner();
            $scope.getMallBanner();
            $scope.getIndexIcon();
            $scope.getMallIndexIcon();
            $scope.getMallAdvertising();
            $scope.getMallRectangle();
        }

        $scope.uploadFile = function (inputObj) {
            var id = $scope.uploadData.entityId;
            if(window.isEmpty(id)){
                id = genUUID();
            }
            window.uploadWinObj = {
                one: true,
                entityName: $scope.uploadData.entity,
                entityField: $scope.uploadData.field,
                entityId: id,
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        $scope.changeImg=options.fileId;
                    });
                }
            };
            window.uploadWinObj.files = inputObj.files;
            //if($scope.uploadData.scope=='mallBanner'||$scope.uploadData.scope=='mallRectangle'){
            window.uploadFileBig();
            //}else{
            //    window.uploadFile();
            //}
        };

        $scope.updateAdvertising=function(){
            var associatedIdTemp;
            if(window.isEmpty($scope.associatedId)){
                malert('请输入关联的商品或者商家ID');
                return;
            }else{
                if(/^[SAF].*$/.test($scope.associatedId) && $scope.associatedId.length<10 && $scope.associatedId.substring(1,2)!='-'){
                    var first = $scope.associatedId.substring(0,1);
                    var second = $scope.associatedId.substring(1,$scope.associatedId.length);
                    associatedIdTemp = first +"-" +second;
                }else{
                    associatedIdTemp = $scope.associatedId;
                }
            }
            var url = window.basePath + '/account/User/updateAdvertising';
            var data = {
                _id:$scope.uploadData.entityId,
                serialNum:$scope.uploadData.serialNum,
                icon:$scope.changeImg,
                entityType:$scope.associatedType,
                entityId:associatedIdTemp,
                type:$scope.uploadData.type
            };
            $http.post(url,data).success(function(re){
                $scope[$scope.uploadData.scope][$scope.uploadData.index][$scope.uploadData.field] = $scope.changeImg;
                $scope.changeBox=false;
                $scope.getImg();
            })
        }
        //更换官网的轮播图
        $scope.updateWebSiteImg = function(){
            var url = window.basePath + '/account/User/updateWebSite';
            var data = {
                _id:$scope.uploadData.entityId,
                serialNum:$scope.uploadData.serialNum,
                icon:$scope.changeImg,
                entityId:$scope.webAddress,
                type:$scope.uploadData.type
            };
            $http.post(url,data).success(function(re){
                $scope[$scope.uploadData.scope][$scope.uploadData.index][$scope.uploadData.field] = $scope.changeImg;
                $scope.changeBox=false;
                $scope.getImg();
            })
        }
        $scope.updateIcon = function(){
            if(window.isEmpty($scope.operateId)){
                malert('请选择图标类型');
                return;
            }
            var url = window.basePath + '/account/User/updateIcon';
            var data = {
                oldId:$scope.uploadData.entityId,
                newId:$scope.operateId,
                icon:$scope.changeImg,
                field:$scope.uploadData.field,
                serialNum:$scope.uploadData.serialNum,
                section:$scope.uploadData.section,
                bgColor:$scope.selectedColor
            };
            $http.post(url,data).success(function(re){
                $scope[$scope.uploadData.scope][$scope.uploadData.index][$scope.uploadData.field] = $scope.changeImg;
                $scope.changeBox=false;
                $scope.getImg();
            })
        }
        $scope.setWebsiteValue = function(text){
            $scope.isWebSite=text;
        }

        //更换图片BOX
        $scope.changeDivFun = function(obj,img,entity,field,scope,index,type,section){
            $scope.associatedType='';
            $scope.associatedId='';

            if(!window.isEmpty(obj.bgColor)){
                $scope.selectedColor=obj.bgColor;
            }

            if(!window.isEmpty(obj.value)){
                $scope.changeType = 'icon';
                $scope.operateList = [[{name: '---请选择---'}], [{name: '---请选择---'}], [{name: '---请选择---'}]];
                $scope.selectOperate = new Array(3);
                if(window.isEmpty(obj.pvalue)){
                    $scope.initOperateValue = '_' +obj.value + '_' ;
                }else{
                    $scope.initOperateValue = obj.pvalue + '_' + obj.value + '_';
                }
                $scope.getOperateType(-1, 1);
            }else if($scope.isWebSite=='website') {
                $scope.changeType='website';
            }else if($scope.isWebSite=='websiteTop'){
                $scope.webAddress=obj.entityId;
                $scope.changeType='websiteTop';
            }else{
                $scope.changeType = 'advertising';
            }
            $scope.changeImg = img;
            $scope.changeBox = true;
            var serial = 0;
            if(section=='isNav'){
                serial=obj.memberSerialNum;
            }else if(section=='mallIsNav'){
                serial=obj.mallSerialNum;
            }else{
                serial=obj.serialNum;
            }
            //设置上传图片参数
            $scope.uploadData={
                entity:entity,
                field:field,
                scope:scope,
                entityId:obj._id,
                serialNum:serial,
                index:index,
                type:type,
                section:section
            };
        }
        $scope.isSetColor = function(type){
            $scope.colorLocation=type;
        }
        //获取经营范围
        $scope.getOperateType = function (_id, type) {
            if(type-2>=0 && $scope.selectOperate[type-2].name=='---请选择---'){
                var typeGrade=type-3;
                if(typeGrade>=0){
                    $scope.operateId=$scope.selectOperate[typeGrade]._id;
                    $scope.operateType = $scope.selectOperate[typeGrade].name;
                    if(typeGrade==0){
                        $scope.operateValue = '_' +$scope.selectOperate[typeGrade].value + '_' ;
                    }else{
                        $scope.operateValue = $scope.selectOperate[typeGrade].pvalue + '_' + $scope.selectOperate[typeGrade].value + '_';
                    }
                    for(var i= type-1;i<3;i++){
                        $scope.operateList[i] = [{name: '---请选择---'}];
                        $scope.selectOperate[i] = $scope.operateList[i][0];
                    }
                }else if(typeGrade==-1){
                    for(var i= type-1;i<3;i++){
                        $scope.operateList[i] = [{name: '---请选择---'}];
                        $scope.selectOperate[i] = $scope.operateList[i][0];
                    }
                    $scope.operateType = '';
                    $scope.operateValue = '';
                    $scope.operateId='';
                } else{
                    $scope.operateType = '';
                    $scope.operateValue = '';
                    $scope.operateId='';
                }
                return;
            }
            $scope.operateId=_id;

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
                    $scope.operateType = $scope.selectOperate[type - 2].name;
                    $scope.operateValue = '_' + $scope.selectOperate[type - 2].value + '_';
                } else if (type > 2) {
                    $scope.operateType = $scope.selectOperate[type - 2].name;
                    $scope.operateValue = $scope.selectOperate[type - 2].pvalue + '_' + $scope.selectOperate[type - 2].value + '_';
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
                    if(type==len && $scope.initOperateValue==$scope.operateValue){
                        $scope.initOperateValue=null;
                    }
                }
            });
        }
        $scope.getImg();
        $scope.mbIsShow = '';
        $scope.iIconIsShow = '';
        $scope.mIconIsShow = '';
        $scope.mIconTIsShow = '';
        $scope.mllbIsShow = '';
        $scope.wsbIsShow = '';
        $scope.maIsShow = '';
        $scope.mrIsShow = '';
        $scope.changeBox = false;
        $scope.changeType= 'advertising';
        $scope.colorLocation = '';
        $scope.isWebSite = '';


    });
})(angular);
