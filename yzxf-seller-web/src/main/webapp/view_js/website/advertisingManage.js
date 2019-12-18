(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.entityTitle = "官网图片管理";
        $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
        $scope.associatedType='';
        $scope.associatedId='';
        initGrid($rootScope, $scope, $http);
        $scope.getWebSiteBanner = function(){
            var url = window.basePath +'/account/User/getWebSiteBanner?type=5';
            $http.get(url).success(function(re){
                $scope.webSiteBanner = re.content.items;
                for(var i = 0,len=3-re.content.items.length;i<len;i++){
                    $scope.webSiteBanner.push({_id:'',icon:'',serialNum:i+1});
                }
            })
        };

        //获取官网每个页面顶部的背景图
        $scope.getWebSiteTop=function(){
            var url = window.basePath +'/account/User/getWebSiteBanner?type=6';
            $http.get(url).success(function(re){
                $scope.webSiteTop = re.content.items;
                var pageName=['关于惠网','新闻动态','分支机构','帮助中心','联系我们'];
                var len = $scope.webSiteTop.length;
                for(var i = len;i<5;i++){
                    $scope.webSiteTop.push({_id:'',icon:'',serialNum:i+1,entityId:pageName[i]});
                }
            })
        };

        $scope.getImg=function(){
            $scope.getWebSiteBanner();
            $scope.getWebSiteTop();
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
            window.uploadFileBig();
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
