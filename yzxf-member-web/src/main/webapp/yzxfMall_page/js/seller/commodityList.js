(function (angular, undefined) {

    var model = 'seller';
    var entity = 'commodityList';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';



        $scope.hoverUp=function(index){
            $scope.isGoodsHover=index;
        }
        $scope.hoverDown=function(){
            $scope.isGoodsHover='';
        }
        //获取商品列表
        $scope.queryCurrentList=function(){
            if($rootScope.pathParams.searchType=='product'){
                $scope.getSearchProduct();
                return;
            }
            //$rootScope.goodsList=[];
            //window.pageNo=1;
            //window.pageSize=40;
            //if(!window.isEmpty($rootScope.dataPage) && ($rootScope.pageNo>$rootScope.dataPage.totalPage || $rootScope.pageNo<1)){
            //    return;
            //}
            var url = window.basePath + '/account/Seller/getCommodityForType?1=1';
            //根据页面进入的地方
            if($rootScope.pType=='tejia'){
                url += '&isTejia=Y';
            }else if($rootScope.pType=='gongyi'){
                url += '&isGongyi=Y';
            }else if($rootScope.pType=='remen'){
                url += '&isRemen=Y';
            }
            //根据商品类别
            if(!window.isEmpty($scope.operateType)){
                url += '&operate='+$scope.operateType;
            }
            //if (!window.isEmpty($rootScope.showArea) && !window.isEmpty($rootScope.showArea.areaValue)) {
            //    url += "&areaValue=" + $rootScope.showArea.areaValue;
            //}
            if(!window.isEmpty($scope.isClick)){
                url+="&selectedIndex="+$scope.isClick;
            }

            url+="&pageNo="+$rootScope.pageNo+'&pageSize='+$rootScope.pageSize;

            $http.get(url).success(function (re){
                $rootScope.dataPage = re.content;
                //$scope.filter.pageNo = $scope.dataPage.pageNo;
                $rootScope.dataPage.$$pageList = [];
                var start = $rootScope.dataPage.pageNo - 3;
                var end = $rootScope.dataPage.pageNo + 4;

                start = start < 1 ? 1 : start;
                end = end > $rootScope.dataPage.totalPage ? $rootScope.dataPage.totalPage : end;

                for (var i = start; i <= end; i++) {
                    $rootScope.dataPage.$$pageList.push(i);
                }
            })
        }
        $scope.pension = function(integralRate,salePrice){
            return $rootScope.getMoney(integralRate*salePrice/100/2);
        }
        $scope.getNavGoodsType = function(){
            if(window.isEmpty($scope.operateType)){
                return;
            }
            var url = window.basePath + '/account/Seller/getNavGoodsType?operateType='+$scope.operateType;
            $http.get(url).success(function(re){
                $rootScope.navGoodsType = re.content.items;
            })
        }

        $scope.getProductByOperate=function(index){
            if(!window.isEmpty($scope.operateType)){
                var arr=$scope.operateType.substring(1,$scope.operateType.length-1).split("_");
                $scope.operateType='';
                for(var i= 0,len=index;i<=len;i++){
                    $scope.operateType+="_"+arr[i];
                }

                $scope.operateType+="_";
                if(index==0) {
                    $rootScope.navGoodsType = [$rootScope.navGoodsType[0]];
                }
                $scope.queryCurrentList();
            }
        }


        //$scope.goProductPage=function(item){
        //    $("#commodityInfo_Ctrl").parent().remove();
        //
        //    var url = '/seller/commodityInfo/goodsId/'+item._id;
        //    $scope.goPage(url,true);
        //}

        $scope.getSearchProduct=function(){
            if(window.isEmpty($rootScope.searchSOP) || window.isEmpty($rootScope.pageNo)){
                return;
            }
            var url= window.basePath + "/account/Seller/getCommodityForType?searchSOP="+$rootScope.searchSOP+"&pageNo="+$rootScope.pageNo+"&pageSize=25";
            $http.get(url).success(function(re){
                $rootScope.dataPage = re.content;
                //$scope.filter.pageNo = $scope.dataPage.pageNo;
                $rootScope.dataPage.$$pageList = [];
                var start = $rootScope.dataPage.pageNo - 3;
                var end = $rootScope.dataPage.pageNo + 4;

                start = start < 1 ? 1 : start;
                end = end > $rootScope.dataPage.totalPage ? $rootScope.dataPage.totalPage : end;

                for (var i = start; i <= end; i++) {
                    $rootScope.dataPage.$$pageList.push(i);
                }

            })
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商品列表');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $rootScope.goodsList=[];
                $rootScope.pageNo=1;
                $rootScope.pageSize=25;
                $rootScope.navGoodsType = [];
                $scope.operateType = $rootScope.pathParams.pOperate;
                $scope.isClick='';
                $rootScope.pType = $rootScope.pathParams.pType;
                if($rootScope.pathParams.searchType=='product'){
                    $scope.getSearchProduct();
                }else{
                    if(!window.isEmpty($rootScope.pType)){
                        $scope.queryCurrentList();
                        $scope.getNavGoodsType();
                        if($rootScope.pType=='remen'){
                            $rootScope.navGoodsType = ['热门商品'];
                        }else if($rootScope.pType=='gongyi'){
                            $rootScope.navGoodsType = ['公益商品'];
                        }else if($rootScope.pType=='tejia'){
                            $rootScope.navGoodsType = ['特价商品'];
                        }
                    }
                    if(!window.isEmpty($scope.operateType)){
                        $scope.queryCurrentList();
                        $scope.getNavGoodsType();
                    }
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);