(function (angular, undefined) {

    var model = 'store';
    var entity = 'store';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.remind = false;

        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $rootScope.loginInfo = re.content;
            });
        };

        $scope.getRelateStore=function(){
            if(!window.isEmpty(getCookie("_relateStore_id"))){
                $scope.relateStoreId = getCookie("_relateStore_id");
                return;
            }
            var url = window.basePath + '/account/RelateStore/show?localSellerId='+getCookie("_seller_id");
            $http.get(url).success(function (re) {
                if(!window.isEmpty(re.content) && !window.isEmpty(re.content.relateStoreId)){
                    $scope.relateStoreId = re.content.relateStoreId;
                    setCookie("_relateStore_id",$scope.relateStoreId);
                }
            });
        };

        $scope.goRelate=function(){
            if(window.isEmpty($scope.relateStoreId)){
                window.location.href='https://s.kyb.vip/store/store/relateStore/relateStoreId/'+getCookie("_seller_id");
            }else{
                window.location.href='https://s.kyb.vip/store/home/index';
            }
        }

        $scope.cancelShowConfirm = function (type) {
            if (type == 'showMoneyCon') {
                $scope.showMoneyCon = false;
            } else if (type == 'showCouponCon') {
                $scope.showCouponCon = false;
            }
        }
        //商家版用户登陆后店铺页面的权限
        $scope.getUserCanUseStore = function () {
            var url = window.basePath + '/account/User/getUserCanUseStore?userId='+getCookie("_user_id");
            $http.get(url).success(function (re) {
                if (re.content.items == null) {
                    $scope.showType = 'notAccess';
                    $rootScope.notSeller=true;
                    if($rootScope.notSeller && $rootScope.notFactor){
                        $rootScope.goPage('/account/login');
                    }
                } else {
                    $scope.showType = 'ok';
                    $scope.getStoreInfo();
                    $rootScope.notSeller=false;
                }
            }).error(function(){
                $rootScope.goPage('/account/login');
            })
        };

        //初始化订单导航栏
        $scope.initHeader=function(){
            $scope.headerList = [
                {_id: 2, name: '待发货', icon: 'icon2-daifahuo1'},
                {_id: 4, name: '已发货', icon: 'icon2-daishouhuo'},
                {_id: 5, name: '已收货', icon: 'icon2-yiwancheng'},
                {_id: 100, name: '已结算', icon: 'icon2-jiesuandan'},
            ];
            $scope.headerList2 = [
                {_id: 6, name: '申请退款', icon: 'icon2-tuikuan'},
                {_id: 7, name: '等待发货', icon: 'icon2-fahuo1'},
                {_id: 8, name: '已发货', icon: 'icon2-fahuo'},
                {_id: 9, name: '已退款', icon: 'icon2-yituikuan'},
            ];
        };

        //设置头部导航栏状态数量
        $scope.setHeaderCount=function(entity,count){
            for(var i= 0,len=$scope[entity].length;i<len;i++){
                for(var j= 0,jlen=count.length;j<jlen;j++){
                    if($scope[entity][i]._id==count[j].orderStatus){
                        if(count[j].count>99){
                            count[j].count=99;
                        }
                        $scope[entity][i]["count"]=count[j].count;
                        break;
                    }
                }
            }
        };

        $scope.setShowFixedQrCode=function(){
            $scope.showFixedQrCode=!$scope.showFixedQrCode;
        };

        $scope.getQrcode=function(){
            $scope.setShowFixedQrCode();
            $scope.createQrCode("#qrcode");
            $scope.createQrCode("#qrcode2");
        };

        $scope.createQrCode = function(entityId){
            var qr = qrcode(10, 'H');
            // var url = "http://m.phsh315.com/yzxfMember/my/payByFixedQrCode/sellerId/"+$rootScope.loginInfo._id;
            //测试贵商支付改为测试环境，正式环境要改回
            var url = "http://m.yzxf8.cn/yzxfMember/my/payByFixedQrCode/sellerId/"+$rootScope.loginInfo._id;
            qr.addData(url);
            qr.make();
            $(entityId).html(qr.createImgTag());
        };

        //获取订单列表
        $scope.getOrderList = function () {
            var url = window.basePath + '/order/OrderInfo/queryMyOrder?' +
                "isCount=true&isNotShowOrder=true&userType=seller";
            $http.get(url).success(function (re) {
                $scope.setHeaderCount('headerList',re.content.countList);
                $scope.setHeaderCount('headerList2',re.content.countList);
            })
        };

        $scope.goMyMember = function(){
            if(!$scope.loginInfo.member || isEmpty($scope.loginInfo.member._id)){
                malert("获取会员失败");
                return;
            }
            var url = window.basePath+"/account/Seller/sellerToMember";
            $http.get(url).success(function(re){
                var path = $location.absUrl();
                if(path.indexOf("localhost")>-1){
                    window.open($location.absUrl().split("7801")[0]+"7800/yzxfMember/account/login/val/"+encodeURIComponent(JSON.stringify(re.content)));
                }else{
                    path = $location.absUrl().split("yzxfSeller")[0].replace('s.','m.')+"yzxfMember/account/login/val/"+encodeURIComponent(JSON.stringify(re.content));
                    if(!!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)){
                        window.location.href=path;
                    }else{
                        window.open(path);
                    }
                }
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.remind = false;
                $scope.initHeader();

                $scope.getUserCanUseStore();
                window.setWindowTitle($rootScope, '店铺');
                //$rootScope.isIndex = true;
                $scope.showMoneyCon = false;
                $scope.showCouponCon = false;
                $scope.showFixedQrCode = false;

                $scope.getOrderList();
                $scope.getRelateStore();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);