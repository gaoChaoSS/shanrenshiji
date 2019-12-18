/**
 * Created by haozigg on 17/3/16.
 */
(function () {
    window.initTypeGrid = function ($rootScope, $scope, $http , $interval , $location) {
        initCart($rootScope, $scope, $http , $interval , $location);
        $scope.indexTypeIsShow = false;
        $scope.typeNext = false;
        $scope.isMyHover = false;
        $scope.isMobileHover = false;
        $rootScope.searchType = 'seller';
        // $rootScope.searchSOP = '';

        $scope.mouseUp = function (pid,check) {
            $scope.typeHover = pid;
            $scope.typeNext = true;
            if(check!='no'){
                $scope.getOperateNext(pid);
            }
        }
        $scope.mouseDown = function () {
            $scope.typeNext = false;
            $rootScope.titleHover = '';
        }
        $scope.mouseDownChild = function () {
            $scope.typeNext = false;
            $scope.typeHover = -1;
        }
        $scope.mouseTitle = function (index) {
            $rootScope.titleHover = index;
        }

        $scope.getOperate=function(){
            var url = window.basePath+"/account/Seller/queryTypeForMall";
            $http.get(url).success(function(re){
                $scope.fenlei=re.content.items;
            });
        }
        $scope.setNav=function(){
            $rootScope.pType='1';
        }

        $scope.getOperateNext=function(pid){
            var url = window.basePath+"/account/Seller/getOperate?pid="+pid;
            $http.get(url).success(function(re){
                $scope.fenleiNext=re.content.items;
            });
        }

        $scope.loginOut = function () {
            deleteCookie('___MEMBER_TOKEN');
            deleteCookie('_member_loginName');
            deleteCookie('_member_mobile');
            deleteCookie('_member_icon');
            deleteCookie('lastLoginType');
            deleteCookie('_member_id');
            $rootScope.myInfo = null;
            $rootScope.goPage('/home/index');
        }
        $scope.getLoginName = function () {
            if (!window.isEmpty($rootScope.myInfo.name)) {
                return $rootScope.myInfo.name;
            }
            if (!window.isEmpty($rootScope.myInfo.mobile)) {
                return $rootScope.myInfo.mobile;
            }
            if (!window.isEmpty($rootScope.myInfo.cardNo)) {
                return $rootScope.myInfo.cardNo;
            }
            if (!window.isEmpty($rootScope.myInfo.idCard)) {
                return $rootScope.myInfo.idCard;
            }
        }
        $scope.pageNumber = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();

        }
        $scope.pageNext = function (num) {
            $rootScope.pageNo = parseInt(num)+parseInt($rootScope.pageNo);
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();
        }
        $scope.pageGoFun = function (num) {
            if(window.isEmpty(num) || num>$rootScope.dataPage.totalPage){
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();
        }
        $scope.setPageNo = function (num){
            $rootScope.pageNo=num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.queryCurrentList();
        }
        $scope.setSearchSOP=function(text){
            $rootScope.searchSOP=text;
        };

        $scope.setSearchType=function(type){
            $rootScope.searchType=type;
        }
        $scope.goPageSearch = function(){
            if($rootScope.searchType=='seller'){
                if($location.path().indexOf('searchSellerList')==-1){
                    $rootScope.goPage('/seller/searchSellerList');
                }else{
                    $scope.getSearch();
                }
            }else{
                if($location.path().indexOf('commodityList/searchType/product')==-1){
                    $rootScope.goPage('/seller/commodityList/searchType/product');
                }else{
                    $scope.getSearchProduct();
                }
            }
        }

        $scope.loginAfterGetMemberInfo = function () {
            var memberId = getCookie('_member_id');
            if (window.isEmpty(memberId)) {
                return;
            }
            url = window.basePath + '/crm/Member/getMyInfo';
            $http.get(url).success(function (re) {
                $rootScope.myInfo = re.content;
                $rootScope.loginAfterName = $scope.getLoginName();
                $rootScope.loginAfterName = $rootScope.loginAfterName.substr(0, 3) + "****" + $scope.loginAfterName.substr(7);
                if (re.content.isRealName == null || re.content.isRealName == false) {
                    $scope.memberIsRealName = false;
                } else {
                    $scope.memberIsRealName = true;
                }
                if (!window.isEmpty($rootScope.myInfo.idCard)) {
                    if (($rootScope.myInfo.idCard).length == 15) {
                        $rootScope.myInfo.idCard = $rootScope.myInfo.idCard.substr(0, 3) + "*********" + $rootScope.myInfo.idCard.substr(11);
                    } else if (($rootScope.myInfo.idCard).length == 18) {
                        $rootScope.myInfo.idCard = $rootScope.myInfo.idCard.substr(0, 3) + "***********" + $rootScope.myInfo.idCard.substr(14);
                    }
                }
                if (!window.isEmpty($rootScope.myInfo.mobile)) {
                    $rootScope.myInfo.mobile = $rootScope.myInfo.mobile.substr(0, 3) + "****" + $rootScope.myInfo.mobile.substr(7);
                }
            })
        }

        //百度地图
        window.getMap=function(){
            // 百度地图API功能
            var map = new BMap.Map("allmap");
            function myFun(result){
                var cityName = result.name;
                map.setCenter(cityName);
                $scope.getAreaByName = function(){
                    var url = window.basePath + '/crm/Member/getLocationByName?cityName='+cityName;
                    $http.get(url).success(function(re){
                        $rootScope.showArea.name=re.content.items[0].name;
                        $rootScope.showArea.areaValue=$scope.getAreaValue(re.content.items[0].pvalue,re.content.items[0].value);
                        if($scope.queryCurrentList){
                            $scope.queryCurrentList();
                        }
                        //自动选择地址
                        for(var i=re.content.items.length-1;i>=0;i--){
                            window.getAreaCur(re.content.items[i]);
                        }
                        //保存到cookie,以后读取地址不再调用百度地图
                        setCookie("_member_map",JSON.stringify(re.content.items));
                    })
                }
                $scope.getAreaByName();
                $scope.$apply();
            }

            var myCity = new BMap.LocalCity();
            myCity.get(myFun);
        };

        //获取地址编码
        $scope.getAreaValue=function(pvalue,value){
            if(window.isEmpty(pvalue) && !window.isEmpty(value)){
                return "_"+value+"_";
            }else if(!window.isEmpty(pvalue) && !window.isEmpty(value)){
                return pvalue+"_"+value+"_";
            }
        }

        $scope.imgList = [];

        $scope.getMallBanner = function(){
            var url = window.basePath +'/account/User/getMallBanner';
            $http.get(url).success(function(re){
                $scope.imgList = re.content.items;
            })
        }
        $scope.getMallAdvertising = function(){
            var url = window.basePath +'/account/User/getMallAdvertising';
            $http.get(url).success(function(re){
                $scope.mallAdvertising = re.content.items;
            })
        }
        $scope.advertisingGoPage = function(obj){
            if(obj.entityType=='Seller'){
                $rootScope.goPage('/seller/sellerInfo/sellerId/'+obj.entityId);
            }else if(obj.entityType=='ProductInfo'){
                $rootScope.goPage('/seller/commodityInfo/goodsId/'+obj.entityId);
            }else if(obj.entityType=='link'){
                if(obj.entityId.indexOf("yzxfMall")==-1){
                    $rootScope.goPage(obj.entityId);
                }else{
                    $rootScope.goPage(obj.entityId.split("yzxfMall")[1]);
                }
            }
        }
        //轮播图
        $scope.imgCarousel = function () {
            if(!window.isEmpty($scope.timeCount)){
                $interval.cancel($scope.timeCount);
            }
            //var count = $scope.imgList.length;
            $scope.timeCount = $interval(function () {
                if ($scope.imgCur + 1 >= 3) {
                    $scope.imgCur = 0;
                } else {
                    $scope.imgCur++;
                }
                if ($location.path().indexOf("index") ==-1) {
                    $interval.cancel($scope.timeCount);
                }
            }, 5000);
        }
        $scope.selectImg = function (index) {
            $scope.imgCur = index;
            $interval.cancel($scope.timeCount);
            $scope.imgCarousel();
        }

        //获取地址
        window.getAreaCur=function(area,isReturn){
            $scope.getAreaCur(area,isReturn);
        };

        $scope.getAreaCur=function(area,isReturn){
            var pid;
            if(window.isEmpty(area)){
                return;
            }
            //是否为'返回'
            if(isReturn){
                pid=area.pid;
                $rootScope.selectedArea.pop();
                $rootScope.showArea.areaValue=$scope.getAreaValue($rootScope.selectedArea[$rootScope.selectedArea.length-1].pvalue,
                    $rootScope.selectedArea[$rootScope.selectedArea.length-1].value);
            }else{
                pid=area._id;
                var pvalueTemp='';
                if(!window.isEmpty(area.pvalue)){
                    pvalueTemp=area.pvalue;
                }
                $rootScope.showArea.areaValue=$scope.getAreaValue(pvalueTemp,area.value);
            }

            if($scope.queryCurrentList && window.isEmpty(area.first)){
                if($location.path().indexOf("commodityList")!=-1){
                    return;
                }
                $scope.queryCurrentList();
            }

            //获取名字
            if(!window.isEmpty(area) && area.type==3){//不查第四级
                $rootScope.showArea.name=area.name;
                return;
            }else if(!window.isEmpty(area.type)){
                $rootScope.showArea.name=area.name;
                if(area.type!=1 && isReturn){
                    if($rootScope.selectedArea.length<1){
                        $rootScope.showArea.name=area.name;
                    }else{
                        $rootScope.showArea.name=$rootScope.selectedArea[$rootScope.selectedArea.length-1].name;
                    }
                }else if(area.type==1 && isReturn){
                    $rootScope.showArea.name="全部";
                }
            }else{
                $rootScope.showArea.name="全部";
            }

            //获取地址集合
            var url = window.basePath + '/crm/Member/getLocation?pid=' + pid;
            $http.get(url).success(function (re) {
                if(re.content.items!=null && re.content.items.length>0){
                    if(!isReturn){
                        $rootScope.selectedArea.push(area);
                    }
                    $rootScope.areaList=re.content.items;
                    if($rootScope.selectedArea.length>3){
                        for(var i= 3,len=$rootScope.selectedArea.length;i<len;i++){
                            $rootScope.selectedArea.remove(3);
                        }
                    }
                }
            });
        }
        if(window.isEmpty($rootScope.selectedArea)){
            $rootScope.selectedArea=new Array(0);
            $rootScope.showArea={name:'',areaValue:''};
            window.getAreaCur({_id:-1,first:1});
        }
        if(!window.isEmpty(getCookie("_member_map"))){
            var map = JSON.parse(getCookie("_member_map"));
            setTimeout(function(){
//                window.getAreaCur({_id:-1,first:1});
                for(var i=map.length-1;i>=0;i--){
                    if(window.getAreaCur){
                        window.getAreaCur(map[i]);
                    }
                }
            },2000);
        }else{
            $rootScope.selectedArea=new Array(0);
            $rootScope.showArea={name:'',areaValue:''};
            window.getAreaCur({_id:-1,first:1});
        }

        //跳转到商品分类页面
        $scope.goOperatePage=function(item){
            if($location.path().indexOf("/seller/commodityList/pOperate")!=-1){
                $scope.operateType=$rootScope.isNullText(item.pvalue)+'_'+item.value+'_';
            }else{
                var url = '/seller/commodityList/pOperate/'+$rootScope.isNullText(item.pvalue)+'_'+item.value+'_';
                $rootScope.goPage(url);
            }
            if($scope.queryCurrentList){
                $scope.queryCurrentList();
            }
            if($scope.getNavGoodsType){
                $scope.getNavGoodsType();
            }
        };

        if($location.path().indexOf("searchType")!=-1){
            $rootScope.searchType='product';
        }else{
            $rootScope.searchType='seller';
        }

        //获取底部帮助模块
        $scope.getHelp=function(){
            var url = window.basePath + "/crm/Article/getMenu?name='商城帮助'";
            $http.get(url).success(function(re){
                $rootScope.menuList = re.content.items;
            });
        };

        $scope.selectedHelp=function(item){
            $rootScope.selectedHelpMenu = item;
            if($location.path().indexOf("help")===-1){
                $rootScope.goPage("/help/help");
            }else{
                if($scope.showArt){
                    $scope.showArt($rootScope.selectedHelpMenu);
                }
            }
        };

        $scope.loginAfterGetMemberInfo();
        $scope.getOperate();
        $scope.imgCur = 0;
        $scope.getMallBanner();
        $scope.getMallAdvertising();
        $scope.imgCarousel();
        $scope.getHelp();
    }
})();