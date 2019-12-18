(function (angular, undefined) {

    var model = 'seller';
    var entity = 'commodityInfo';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.resultsNum = 1;
        $scope.infoAsComment='info';
        //缩略图
        $scope.comodityImgList=[
            {id:0,url:'/yzxfMall_page/img/531.png'},
            {id:1,url:'/yzxfMall_page/img/533.png'},
            {id:2,url:'/yzxfMall_page/img/569.png'},
            {id:3,url:'/yzxfMall_page/img/570.png'}
        ]
        //$scope.fangdaImgUrl = $scope.comodityImgList[0].url;
        $scope.fangdaImg = function(icon){
            if(window.isEmpty(icon)){
                $scope.showImg=$scope.goodsInfo.icon;
            }else{
                $scope.showImg=icon;
            }
        }

        $scope.leaveImg=function(){
            $scope.showImg=$scope.goodsInfo.icon;
        }


        //商品是否已收藏
        $scope.getGoodsIsCollection = function(){
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + '/crm/MemberCollection/getGoodsIsCollection?memberId='+$scope.memberId+'&goodsId='+$rootScope.pathParams.goodsId;
            $http.get(url).success(function(re){
                if(re.content._id!=null&&!window.isEmpty(re.content._id)){
                        $scope.isCollection=true;
                    }else{
                    $scope.isCollection=false;
                }
            })
        }
        //添加或者删除商品收藏
        $scope.addOrDelGoodsCollection = function(){
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + '/crm/MemberCollection/addOrDelGoodsCollection?memberId='+$scope.memberId+'&goodsId='+$scope.goodsInfo._id;
            $http.get(url).success(function(re){
                $scope.getGoodsIsCollection();
            })
        }
        $scope.getGoodsInfo = function(){
            $scope.goodsInfo =null;
            var url = window.basePath + '/order/ProductInfo/goodsInfoShow?_id='+$rootScope.pathParams.goodsId;
            $http.get(url).success(function(re){
                $scope.goodsInfo = re.content;
                if($scope.goodsInfo==null || $scope.goodsInfo.length==0){
                    malert("未找到商品");
                    $rootScope.goBack();
                }
                $scope.showImg=$scope.goodsInfo.icon;
                $scope.totalMoney='';
                //生成选择数组
                var len = $scope.goodsInfo.spec.length;
                $scope.selectItems=new Array(len);
                for(var i=0;i<len;i++){
                    $scope.selectItems[i]={name:'',items:'',addMoney:''};
                }
                $scope.getSellerInfo();
            })
        }
        //获取商户信息
        $scope.getSellerInfo=function(){
            var url = window.basePath + "/account/StoreInfo/getSellerIntroById?sellerId="+$scope.goodsInfo.sellerId;
            $http.get(url).success(function(re){
                $scope.sellerInfo=re.content;
            });
        }


        $scope.queryProductInfoImgList = function (type) {
            var entityId = $rootScope.pathParams.goodsId;
            var entityName = 'ProductInfo';
            var url = window.basePath + '/file/FileItem/queryEntityFiles' +
                '?_entityName=' + entityName + '&_entityField=' + type + '&_entityId=' + entityId;
            $http.get(url).success(function (re) {
                $scope[type] = re.content.items;
            });
        }

        $scope.getGoodsComment = function(){
            var url = window.basePath + '/order/ProductInfo/goodsCommentShow?goodsId='+$rootScope.pathParams.goodsId;
            $http.get(url).success(function(re){
                $scope.goodsComment = re.content.items;
                if (!window.isEmpty($scope.goodsComment.mobile)) {
                    $scope.goodsComment.mobile = $scope.goodsComment.mobile.substr(0, 3) + "****" + $scope.goodsComment.mobile.substr(7);
                }
            })
        }
        //选择的参数
        $scope.setSelectedItems = function (pid,id) {
            $scope.selectItems[pid]["name"] = $scope.goodsInfo.spec[pid].name;
            $scope.selectItems[pid]["items"] = $scope.goodsInfo.spec[pid].items[id];
            $scope.selectItems[pid]["addMoney"] = $scope.goodsInfo.spec[pid].addMoney[id];
        }
        //确认订单
        $scope.sureOrderFun = function(){
            $scope.countMoney();
            if(window.isEmpty($scope.totalMoney)){
                malert("请选择产品规格");
                return;
            }
        }

        //购买数量
        $scope.commodityBtn = function (num) {
            if (num == -1 && $scope.resultsNum > 1) {
                $scope.resultsNum += num;
            } else if (num == 1 && $scope.resultsNum < 99) {
                $scope.resultsNum += num;
            }
            $scope.countMoney();
        }
        //星级评价
        $scope.starNo=function(number){
            var str="";
            for(var no=0;no<number;no++){
                str+='★';
            }
            return str;
        }
        //计算总价
        $scope.countMoney=function(){
            var len=$scope.selectItems.length;
            $scope.totalMoney=0;
            for(var i= 0;i<len;i++){
                if(!window.isEmpty($scope.selectItems[i]["items"])){
                    $scope.totalMoney++;
                }else{
                    $scope.totalMoney='';
                }
            }
            if($scope.totalMoney==len){
                $scope.totalMoney=$scope.goodsInfo.salePrice;
                for(var i= 0;i<len;i++){
                    if(!window.isEmpty($scope.selectItems[i]["addMoney"]) || $scope.selectItems[i]["addMoney"]==0){
                        $scope.totalMoney+=parseFloat($scope.selectItems[i]["addMoney"]);
                    }
                }
                $scope.totalMoney=$scope.totalMoney*$scope.resultsNum;
                $scope.pensionMoney = $rootScope.getMoney($scope.totalMoney * $scope.sellerInfo.integralRate/100/2);
            }else{
                $scope.totalMoney='';
            }
        };

        //加入购物车
        $scope.saveCart=function(){
            var url = window.basePath + "/order/Cart/saveMyCart";
            var data={
                spec: $scope.selectItems,
                count: $scope.resultsNum,
                productId:$rootScope.pathParams.goodsId
            };
            $http.post(url,data).success(function(){
                malert("已加入购物车");
                $scope.getMyCart();
            });
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商品详情');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $rootScope.isLoginPage = true;
                $scope.isCollection=false;
                $scope.isQrcode=false;
                $scope.getGoodsInfo();
                $scope.thumbnail = [];
                $scope.imgList = [];
                $scope.queryProductInfoImgList('imgList');
                $scope.queryProductInfoImgList('thumbnail');
                $scope.getGoodsIsCollection();

            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);