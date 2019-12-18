(function (angular, undefined) {
    var model = "store";
    var entity = "commodity";
    window.app.register.controller('store_commodity_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //标题切换初始化
        $scope.titleCheck = 1;
        //选择商品类型菜单初始化
        $scope.hideMenuCheck = 0;
        //$scope.colorCheck=false;
        $scope._id = $rootScope.pathParams.goodsId;
        //$scope.storeId = $rootScope.pathParams._id;

        //选择的参数
        $scope.setSelectedItems = function (pid,id) {
            $scope.selectItems[pid]["name"] = $scope.goodsInfo.spec[pid].name;
            $scope.selectItems[pid]["items"] = $scope.goodsInfo.spec[pid].items[id];
            $scope.selectItems[pid]["addMoney"] = $scope.goodsInfo.spec[pid].addMoney[id];

            $scope.countMoney();
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
                productId:$scope._id
            };
            $http.post(url,data).success(function(){
                malert("已加入购物车");
            });
        };

        //获取商户信息
        $scope.getSellerInfo=function(){
            var url = window.basePath + "/account/StoreInfo/getSellerIntroById?sellerId="+$scope.goodsInfo.sellerId;
            $http.get(url).success(function(re){
                $scope.sellerInfo=re.content;
            });
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
        //商品是否已收藏
        $scope.getGoodsIsCollection = function(){
            $scope.memberId = getCookie('_member_id');
            var url = window.basePath + '/crm/MemberCollection/getGoodsIsCollection?memberId='+$scope.memberId+'&goodsId='+$scope._id;
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
            var url = window.basePath + '/crm/MemberCollection/addOrDelGoodsCollection?memberId='+$scope.memberId+'&goodsId='+$scope._id;
            $http.get(url).success(function(re){
                $scope.getGoodsIsCollection();
            })
        }
        $scope.getGoodsInfo = function(){
            $scope.selectItems=[];
            var url = window.basePath + '/order/ProductInfo/goodsInfoShow?_id='+$scope._id;
            $http.get(url).success(function(re){
                $scope.goodsInfo = re.content;

                $scope.totalMoney='';
                //生成选择数组
                if(!window.isEmpty($scope.goodsInfo.spec)){
                    var len = $scope.goodsInfo.spec.length;
                    $scope.selectItems=new Array(len);
                    for(var i=0;i<len;i++){
                        $scope.selectItems[i]={name:'',items:'',addMoney:''};
                    }
                }

                $scope.queryProductInfoImgList('imgList');
                $scope.queryProductInfoImgList('thumbnail');
                $scope.getSellerInfo();
            })
        }

        $scope.queryProductInfoImgList = function (entityField) {
            var entityId = $scope._id;
            var entityName = 'ProductInfo';

            var url = window.basePath + '/file/FileItem/queryEntityFiles' +
                '?_entityName=' + entityName + '&_entityField=' + entityField + '&_entityId=' + entityId;
            $http.get(url).success(function (re) {
                $scope[entityField] = re.content.items;
                if("thumbnail"==entityField){
                    $scope.thumbnail.unshift({icon:$scope.goodsInfo.icon});
                    if($scope.thumbnail.length<=0){
                        return;
                    }
                    for(var i= 0,len=$scope.thumbnail.length;i<len;i++){
                        $scope.thumbnail[i].index=i;
                    }
                    $scope.selectedIcon=$scope.thumbnail[0];
                }
            });
        };

        //图片缩略图翻页
        $scope.setSelectIcon=function(num){
            var len = $scope.thumbnail.length;
            var index=$scope.selectedIcon.index;
            if(index+num>len-1 || index+num<0){
                return;
            }
            if(num>0){
                $scope.isLeftAnimation=true;
            }else{
                $scope.isLeftAnimation=false;
            }
            $scope.selectedIcon=$scope.thumbnail[index+num];
        };

        //获取动画方向
        $scope.getAnimation=function(tImg){
            if($scope.selectedIcon.index!=tImg.index){
                return '';
            }
            return $scope.isLeftAnimation?'img-Carousel1-right':'img-Carousel1-left';
        };

        $scope.getGoodsComment = function(){
            var url = window.basePath + '/order/ProductInfo/goodsCommentShow?goodsId='+$scope._id;
            $http.get(url).success(function(re){
                $scope.goodsComment = re.content.items;
            })
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商品页');
                $rootScope.isIndex = false;
                //购买数量初始化
                $scope.resultsNum = 1;
                $scope.getGoodsInfo();
                $scope.getGoodsIsCollection();
                $scope.errorFun = false;
                $rootScope.isLoginPage = true;
                $scope.isCollection=false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });

    //当图片加载失败时,显示的404图片
    window.app.register.directive('errSrc', function () {
        return {
            link: function (scope, element, attrs) {
                element.bind('error', function () {
                    if (attrs.src != attrs.errSrc) {
                        attrs.$set('src', attrs.errSrc);
                    }
                });
            }
        }
    });
})(angular);