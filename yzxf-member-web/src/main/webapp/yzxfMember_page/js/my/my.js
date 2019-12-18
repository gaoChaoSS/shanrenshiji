(function (angular, undefined) {

    var model = 'my';
    var entity = 'my';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //初始化订单导航栏
        $scope.initHeader=function(){
            $scope.headerList = [
                {_id: 1, name: '未付款', icon: 'icon2-qianbao1'},
                {_id: 2, name: '待发货', icon: 'icon2-daifahuo1'},
                {_id: 4, name: '待收货', icon: 'icon2-daishouhuo'},
                {_id: 5, name: '已收货', icon: 'icon2-yiwancheng'},
                {_id: 100, name: '已结算', icon: 'icon2-jiesuandan'},
            ];

            $scope.otherList = [
                {name:'退款售后',iconfont:'iconfont2 icon2-tuikuan',color:'#F9A037',page:'/my/order/pageCheck/drawback'},
                {name:'消费积分',iconfont:'iconfont2 icon2-qianbao',color:'#3E9AEE',page:'/my/wallet'},
                {name:'养老金',iconfont:'iconfont2 icon2-woyaotouziqianbi',color:'#2CC8BF',page:'/my/pension'},
                {name:'我的红包',iconfont:'iconfont2 icon2-jiaoyi',color:'#F9A037',page:'/my/teamOrder'},
                {name:'团队成员',iconfont:'iconfont icon-friends',color:'#cb00d8',page:'/my/team'},
                {name:'我的卡券',iconfont:'iconfont2 icon2-3',color:'#2CC8BF',page:'/my/coupon'},
                {name:'我的收藏',iconfont:'iconfont icon-tubiao2223',color:'#fd2a89',page:'/my/collection'},
                {name:'我的评价',iconfont:'iconfont2 icon2-pingjia1',color:'#3E9AEE',page:'/my/comment'},
                {name:'收货地址',iconfont:'iconfont icon-dizhi',color:'#fd2a89',page:'/my/address'},
            ];
        };

        $scope.checkMyStoreIcon = function(){
            $scope.otherList.push({name:$rootScope.myInfo.sellerInfo?$rootScope.myInfo.sellerInfo.sellerName:'我的店铺',iconfont:'iconfont2 icon2-shangpin',color:'#3E9AEE',fun:function(){
                if($rootScope.myInfo.sellerInfo){
                    $scope.goMyStore();
                }else{
                    $rootScope.goPage("/other/sellerApply");
                }
            }});
        }

        //设置头部导航栏状态数量
        $scope.setHeaderCount=function(count){
            for(var i= 0,len=$scope.headerList.length;i<len;i++){
                for(var j= 0,jlen=count.length;j<jlen;j++){
                    if($scope.headerList[i]._id==count[j].orderStatus){
                        if(count[j].count>99){
                            count[j].count=99;
                        }
                        $scope.headerList[i]["count"]=count[j].count;
                        break;
                    }
                }
            }
        };

        //获取订单列表
        $scope.getOrderList = function () {
            var url = window.basePath + '/order/OrderInfo/queryMyOrder?' +
                "isCount=true&isNotShowOrder=true";
            $http.get(url).success(function (re) {
                $scope.setHeaderCount(re.content.countList);
            })
        };

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
        $scope.getMyInfo = function () {
            $scope.errorPension = false;
            $scope.isShowPensionTap = false;
            var url = window.basePath + '/crm/Member/getMyInfo';
            $http.get(url).success(function (re) {
                $rootScope.myInfo = re.content;
                $scope.loginName = $scope.getLoginName();
                $scope.loginName=$scope.loginName.substr(0,3)+"****"+$scope.loginName.substr(7);
                $scope.isShowPensionTap = !window.isEmpty($rootScope.myInfo.realName);
                $scope.isShowPensionTap = !window.isEmpty($rootScope.myInfo.cardNo);
                $scope.cardNo =window.isEmpty($rootScope.myInfo.cardNo)?"":$rootScope.myInfo.cardNo;

                if(!$rootScope.myInfo.isRealName && !$rootScope.myInfo.isBindCard){
                    $scope.showText = "点击前往实名认证并激活养老金补充卡，享投保服务";
                    $scope.goPageText="/my/realName/isActive/Y";
                }
                if($rootScope.myInfo.isRealName){
                    $scope.showText = "您已认证，点击前往激活养老金补充卡，享投保服务";
                    $scope.goPageText="/my/orderPay";
                }
                if($rootScope.myInfo.isRealName && $rootScope.myInfo.isBindCard){
                    $scope.showText = "";
                    $scope.goPageText="";
                }
                $scope.checkMyStoreIcon();
            });
        }

        //是否激活
        // $scope.pensionCanUse = function () {
        //     if(window.isEmpty($rootScope.myInfo.cardNo)){
        //         $scope.errorPension = true;
        //     }else{
        //         $rootScope.goPage('/my/pension');
        //     }
        // }

        //是否实名认证
        $scope.getIsRealName = function(){
            if(!window.isEmpty($rootScope.myInfo.realName)){
                $rootScope.goPage('/my/orderPay');
            }else{
                malert('请先实名认证');
                $rootScope.goPage('/my/realName/isActive/Y');
            }
        };

        $scope.iconCheck = function () {
            if ($scope.isShowPensionTap) {
                $scope.iconImg = '/yzxfMember_page/img/logo2.png';
                $scope.iconText = '已认证';
                $scope.icon2DCard = '/yzxfMember_page/img/2DCODE.png';
            } else {
                $scope.iconImg = '/yzxfMember_page/img/logo1.png';
                $scope.iconText = '未认证';
                $scope.icon2DCard = '/yzxfMember_page/img/2DCODE.png';
            }
        }

        $scope.getQrcode=function(){
            $scope.myCard=!$scope.myCard;
            var qr = qrcode(10, 'H');
            qr.addData($rootScope.myInfo.mobile);
            qr.make();
            $(".qrcode").html(qr.createImgTag());
            $(".qrcode img").addClass("iconImg2");
        }

        $scope.goMyStore = function(){
            if(!$rootScope.myInfo.sellerInfo || window.isEmpty($rootScope.myInfo.sellerInfo)){
                malert("获取商家失败");
                return;
            }
            var url = window.basePath+"/account/Seller/memberToSeller";
            $http.get(url).success(function(re){
                var path = $location.absUrl();
                if(path.indexOf("localhost")>-1){
                    window.open($location.absUrl().split("7800")[0]+"7801/yzxfSeller/account/login/val/"+encodeURIComponent(JSON.stringify(re.content)));
                }else{
                    path = $location.absUrl().split("yzxfMember")[0].replace('m.','s.')+"yzxfSeller/account/login/val/"+encodeURIComponent(JSON.stringify(re.content));
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
                window.setWindowTitle($rootScope, '我的');
                //$rootScope.windowTitleHide = true;
                $rootScope.isIndex = true;
                $scope.getMyInfo();
                $scope.memberProtocol = false;
                $scope.initHeader();
                $scope.getOrderList();
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
