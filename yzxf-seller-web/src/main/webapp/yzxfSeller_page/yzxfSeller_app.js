(function (angular, undefined) {
    //必须设置
    window.appName = 'yzxfSeller';
    window.tokenName = '___MEMBER_TOKEN';
    window.basePath = '/s_user/api';
    setCookie('apiVersion', 1);
    window.initWindowConf();

    window.app.controller('allBodyCtrl', function ($rootScope, $scope, $http, $location, $templateCache) {
        //console.log('$location=', $location.path());

        $scope.colors = ['#cc0000', '#ddd', 'red', 'green', 'blue'];

        //定义无需登录的页面
        $rootScope.notLoginPage = {};
        $rootScope.notLoginPage['/product/index'] = true;

        //定义一些页面需要的公共js
        $rootScope.pageCommonJs = {};
        $rootScope.pageCommonJs['/home/bindMemberCard'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/home/belongMember'] = ['/js/qrcode.js'];

        $rootScope.pageCommonJs['/store/store'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/home/index'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/store/setting'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/store/factorApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/store/storeApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];

        $rootScope.pageCommonJs['/account/login'] = ['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js'];
        $rootScope.pageCommonJs['/store/addStoreCommodity'] = ['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/home/cardIssuingAccount'] = ['/js/lib/g2.js', '/js/lib/g2m.js'];
        $rootScope.pageCommonJs['/store/storeAccount'] = ['/js/lib/g2.js', '/js/lib/g2m.js'];
        $rootScope.pageCommonJs['/store/qrCode'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/store/store'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/home/deposit'] = ['/js/qrcode.js'];

        $rootScope.bottomTabList = [
            {
                _id: '1',
                name: '服务站',
                iconClass: 'iconfont icon-card index_page_bottomBtn',
                hoverIcon: 'icon-svgmoban56 iconfont',
                url: '/home/index',
            },
            {
                _id: '2',
                name: '店铺',
                iconClass: 'iconfont icon-user index_page_bottomBtn',
                hoverIcon: 'icon-wodeshixin iconfont',
                url: '/store/store'
            }];
        $rootScope.tabBottomItemWidth = 100 / $scope.bottomTabList.length + "%";
        //$scope.selectBottomItem($scope.bTabSelected);
        //********* 接收到页面处理的信号 *************
        //接收到页面跳转的请求.
        //跳转到某个页面
        $rootScope.goPage = function (url, replace) {
            replace = replace == null ? false : true;

            if (replace) {//替换当前页,当前页需要注销
                $rootScope.finishLastPage();
            }
            $location.path(url);
            $rootScope.backUrlDef = null;
        }
        $rootScope.showDate = function (time) {
            return new Date(time).showDate();
        }

        $rootScope.showYFullTime = function (time) {
            return new Date(time).showYFullTime();
        }

        $rootScope.showSearchInputCon = function () {
            $rootScope.showSearchInput = !$rootScope.showSearchInput;
            $rootScope.goPage('/product/index');
        }
        //当值为Null时改为0
        $rootScope.isNullZero = function (number) {
            if (window.isEmpty(number) || number == 'null') {
                return 0;
            }
            return number;
        }
        $rootScope.getMoney=function(money){
            money=$rootScope.isNullZero(money);
            var moneyStr=money.toString().split(".")[1];
            if(!window.isEmpty(moneyStr) && moneyStr.length>2){
                if(moneyStr.substring(2,3)=="9"){
                    money=money.toFixed(2);
                }
            }
            return money.toString().replace(/([0-9]+.[0-9]{2})[0-9]*/,"$1");
        }
        $rootScope.isEmpty2 = function(str){
            return str == null || str == '' || str=='null';
        }
        $rootScope.getBankCard=function(num){
            if(window.isEmpty(num)){
                return '';
            }
            return '('+num.substring(num.length-4,num.length)+")";
        }
        $rootScope.setScopeFlag=function(entity){
            $rootScope[entity]=!$rootScope[entity];
        }
        //数字相关
        Number.prototype.toFixed = function (d) {
            var s = this + "";
            if (!d)d = 0;
            if (s.indexOf(".") == -1)s += ".";
            s += new Array(d + 1).join("0");
            if (new RegExp("^(-|\\+)?(\\d+(\\.\\d{0," + (d + 1) + "})?)\\d*$").test(s)) {
                var s = "0" + RegExp.$2, pm = RegExp.$1, a = RegExp.$3.length, b = true;
                if (a == d + 2) {
                    a = s.match(/\d/g);
                    if (parseInt(a[a.length - 1]) > 4) {
                        for (var i = a.length - 2; i >= 0; i--) {
                            a[i] = parseInt(a[i]) + 1;
                            if (a[i] == 10) {
                                a[i] = 0;
                                b = i != 1;
                            } else break;
                        }
                    }
                    s = a.join("").replace(new RegExp("(\\d+)(\\d{" + d + "})\\d$"), "$1.$2");

                }
                if (b)s = s.substr(1);
                return (pm + s).replace(/\.$/, "");
            }
            return this + "";
        };
        //显示图片
        $rootScope.iconImgUrl = function (list, icon) {
            return list != null && icon != null ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
        }
        $rootScope.iconImgUrlAll = function (list, icon) {
            return list != null && icon != null ? ('/s_img/icon.jpg?_id=' + icon + 'wh=650_0') : '/yzxfSeller_page/img/notImg02.jpg';
        }
        $rootScope.iconImgUrl = function (icon) {
            return icon != null ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
        }
        //获取默认支付方式
        $rootScope.getDefaultPayType=function(){
            if(window._isWechat){
                return 10;
            }else{
                return 4;
            }
        };
        // 获取商户头像，若没有则获取门头照
        $rootScope.getSellerIcon=function(icon,doorImg){
            if(window.isEmpty(icon)){
                if(!window.isEmpty(doorImg)){
                    return doorImg.split("_")[0];
                }else{
                    return '';
                }
            }else{
                return icon;
            }
        };

        //$rootScope.getStoreAndSellerInfo = function (storeNo, isSetCookie, next) {
        //    $http.get(window.basePath + '/account/StoreInfo/getStoreAndSeller?storeNo=' + storeNo).success(function (re) {
        //        if (isSetCookie) {
        //            setCookie('storeId', re.content._id);
        //            setCookie('storeNo', re.content.storeNo);
        //            setCookie('sellerId', re.content.seller._id);
        //            setCookie('sellerNo', re.content.seller.sellerNo);
        //        }
        //        $rootScope.storeInfo = re.content;
        //        $rootScope.storeInfo.$$statusClass = $rootScope.storeInfo.isRun ? 'num' : 'high';
        //        $rootScope.storeInfo.$$statusTitle = $rootScope.storeInfo.isRun ? '(营业中)' : '(休息中)';
        //        $rootScope.storeInfo.$$statusDesc = $rootScope.storeInfo.isRun ? '正在营业中, 并提供送货上门服务' : '停止营业及送货';
        //        if ($rootScope.storeInfo.notSend) {
        //            $rootScope.storeInfo.$$statusClass = 'yellow';
        //            $rootScope.storeInfo.$$statusTitle = '(暂停送货)';
        //            $rootScope.storeInfo.$$statusDesc = '实体店正常营业,但暂停送货上门';
        //        }
        //        //运费规则
        //        $rootScope.storeInfo.freightRuleList = [];
        //        if ($rootScope.storeInfo.sendPrice != null) {
        //            $.each($rootScope.storeInfo.sendPrice.split(','), function (k, v) {
        //                var obj = {};
        //                obj.startPrice = parseFloat(v.split('-')[0]);
        //                var endStr = v.split('-')[1];
        //                obj.endPrice = parseFloat(endStr.split('=')[0]);
        //                obj.sendPrice = parseFloat(endStr.split('=')[1]);
        //
        //                $rootScope.storeInfo.freightRuleList.push(obj);
        //            })
        //        }
        //
        //        if (next) {
        //            next();
        //        }
        //    });
        //}

        initAngularRootScope($rootScope, $scope, $http, $location);


        //注册设备
        var actions = [];

        //actions.push(function (next) {
        //    (function () {
        //        $("#allLoadingCon").fadeOut(2000);
        //    }).delay(2);
        //});
        //$("#allLoadingCon").hide(2000);
        $(document).queue('init', actions);
        $(document).dequeue('init');
        $().ready(function () {
            setTimeout(function () {
                $("#allLoadingCon").hide();
            }, 2000);
        })

        $rootScope.$on('$locationChangeStart', function (e, path) {
            var sellerId = getCookie("_seller_id");
            var factorId = getCookie("_factor_id");
            var model = $location.path().split('/')[1];
            var userType = $rootScope.pathParams.userType;
            var type= $rootScope.pathParams.type;
            if(window.isEmpty($rootScope.isGoLogin)){
                $rootScope.isGoLogin=0;
            }
            if($rootScope.isGoLogin!=0 && $rootScope.isGoLogin>3){
                $rootScope.isGoLogin = 0;
            }
            if(!window.isEmpty(sellerId)&&model=='store'&&sellerId!='undefined'){
                if(!window.isEmpty(userType)||!window.isEmpty(type)){
                    if(userType=='seller'||type=='seller'){
                        var url = window.basePath + '/account/Seller/getSellerIsTrue?sellerId='+sellerId;
                        $http.get(url).success(function(re){
                            if(!re.content.canUse){
                                $rootScope.isGoLogin++;
                                $rootScope.goPage('/home/index');
                                malert("抱歉!商户已禁用!请联系管理员!");
                            }
                        })
                    }
                }else{
                    var url = window.basePath + '/account/Seller/getSellerIsTrue?sellerId='+sellerId;
                    $http.get(url).success(function(re){
                        if(!re.content.canUse){
                            $rootScope.isGoLogin++;
                            $rootScope.goPage('/home/index');
                            malert("抱歉!商户已禁用!请联系管理员!");
                        }
                    })
                }
            }
            if(!window.isEmpty(factorId)&&model=='home'&&factorId!='undefined'){
                if(!window.isEmpty(userType)||!window.isEmpty(type)){
                    if(userType=='factor'||type=='factor'){
                        var url = window.basePath + '/account/Factor/getFactorIsTrue?factorId='+factorId;
                        $http.get(url).success(function(re){
                            $rootScope.$$factor = re.content;
                            if(!re.content.canUse){
                                //deleteCookie('___USER_TOKEN');
                                //deleteCookie('_user_name');
                                //deleteCookie('_user_icon');
                                //deleteCookie('_user_id');
                                //deleteCookie('_factor_id');
                                //deleteCookie('_seller_id');
                                //$rootScope.loginInfo = null;
                                //malert("抱歉!服务站已禁用!请联系管理员!");
                                $rootScope.isGoLogin++;
                                $rootScope.goPage('/store/store');
                                malert("抱歉!服务站已禁用!请联系管理员!");
                            }
                        })
                    }
                }else{
                    var url = window.basePath + '/account/Factor/getFactorIsTrue?factorId='+factorId;
                    $http.get(url).success(function(re){
                        $rootScope.$$factor = re.content;
                        if(!re.content.canUse){
                            $rootScope.isGoLogin++;
                            $rootScope.goPage('/store/store');
                            malert("抱歉!服务站已禁用!请联系管理员!");
                        }
                    })
                }
            }
            if($rootScope.isGoLogin>2){
                deleteCookie('___USER_TOKEN');
                deleteCookie('_user_name');
                deleteCookie('_user_icon');
                deleteCookie('_user_id');
                deleteCookie('_factor_id');
                deleteCookie('_seller_id');
                $rootScope.loginInfo = null;
                malert("抱歉!您的账户已停用!请联系管理员!");
            }
        });
    });
})
(angular);