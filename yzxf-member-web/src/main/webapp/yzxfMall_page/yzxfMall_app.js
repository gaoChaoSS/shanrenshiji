(function (angular, undefined) {
    //必须设置
    window.appName = 'yzxfMall';
    window.tokenName = '___MEMBER_TOKEN';
    window.basePath = '/s_member/api';
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
        $rootScope.pageCommonJs['/my/my2DBarcode'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/other/sellerApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/other/factorApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/other/agentApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];

        $rootScope.pageCommonJs['/account/login'] = ['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js'];
        $rootScope.pageCommonJs['/seller/commodityInfo'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/my/orderPay'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/my/wallet'] = ['/js/qrcode.js'];

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
        //$rootScope.goPageCur = function (url, replace) {
        //    if($location.path().indexOf(url.split("/pType")[0])!=-1){
        //        $rootScope.pathParams.pType=url.split("pType/")[1];
        //    }else{
        //        $rootScope.goPage(url,replace);
        //    }
        //}
        $rootScope.showDate = function (time) {
            return new Date(time).showDate();
        }
        $rootScope.showSearchInputCon = function () {
            $rootScope.showSearchInput = !$rootScope.showSearchInput;
            $rootScope.goPage('/product/index');
        }
        //当值为Null时改为0
        $rootScope.isNullZero = function (number) {
            if (window.isEmpty(number)) {
                return 0;
            }
            return number;
        }
        $rootScope.showYFullTime = function (time) {
            return new Date(time).showYFullTime();
        }
        //当值为Null时改为""
        $rootScope.isNullText = function (text) {
            if (window.isEmpty(text)) {
                return "";
            }
            return text;
        }
        //当值为Null时改为"暂无"
        $rootScope.isNullText2 = function (text) {
            if (window.isEmpty(text) || text == 'null') {
                return "暂无";
            }
            return text;
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
        };
        $rootScope.openPage=function(url){
            window.open($location.absUrl().split("yzxfMall")[0]+"yzxfMall/"+url);
        };
        //清空选择的地址
        //$rootScope.clearLocation = function () {
        //    $rootScope.locationSelected = null;
        //    $rootScope.locationArea = null;
        //    $rootScope.locationAreaValue = null;
        //    $rootScope.locationAreaPValue = null;
        //}
        //显示图片
        $rootScope.iconImgUrl = function (list, icon) {
            return list != null && icon != null ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfMall_page/img/notImg02.jpg';
        }
        $rootScope.iconImgUrl = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfMall_page/img/notImg02.jpg';
        }
        $rootScope.iconImg = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon ) : '/yzxfMall_page/img/notImg02.jpg';
        }
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

        actions.push(function (next) {
            (function () {
                $("#allLoadingCon").fadeOut();
            }).delay(1);
        });
        //$("#allLoadingCon").hide();
        $(document).queue('init', actions);
        $(document).dequeue('init');

        $.getScript("/yzxfMall_page/js/home/cart.js");


    });
})
(angular);