(function (angular, undefined) {
    //必须设置
    window.appName = 'yzxfMember';
    window.tokenName = '___MEMBER_TOKEN';
    window.basePath = '/s_member/api';
    setCookie('apiVersion', 1);
    window.initWindowConf();

    window.app.controller('allBodyCtrl', function ($rootScope, $scope, $http, $location, $templateCache) {
        //console.log('$location=', $location.path());

        $scope.colors = ['#cc0000', '#ddd', 'red', 'green', 'blue'];

        //定义无需登录的页面
        $rootScope.notLoginPage = {};

        //定义一些页面需要的公共js
        $rootScope.pageCommonJs = {};
        $rootScope.pageCommonJs['/my/coupon'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/my/my2DBarcode'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js', '/js/qrcode.js'];
        $rootScope.pageCommonJs['/my/team'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/my/teamOrder'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/other/sellerApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/other/factorApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/other/agentApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];
        $rootScope.pageCommonJs['/order/drawbackApply'] = ['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js', '/js/imageUpload.js'];

        $rootScope.pageCommonJs['/account/login'] = ['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js'];
        $rootScope.pageCommonJs['/my/my'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/my/deposit'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/my/depositFriend'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/store/storeIntro'] = ['/js/qrcode.js'];
        $rootScope.pageCommonJs['/other/appDownload'] = ['/js/qrcode.js'];

        $rootScope.bottomTabList = [
            {
                _id: '1',
                name: '首页',
                iconClass: 'iconfont icon-home index_page_bottomBtn',
                hoverIcon: 'icon-shouye4 iconfont',
                url: '/home/index',
            },
            {
                _id: '2',
                name: '商家',
                iconClass: 'iconfont icon-wxbmingxingdianpu index_page_bottomBtn',
                hoverIcon: 'icon-shangjia2 iconfont',
                url: '/store/store',
            },
            {
                _id: '3',
                name: '购物车',
                iconClass: 'iconfont2 icon2-gouwuche1 index_page_bottomBtn',
                hoverIcon: 'icon2-gouwuche2 iconfont2',
                url: '/my/cart',
            },
            {
                _id: '4',
                name: '我的',
                iconClass: 'iconfont icon-user index_page_bottomBtn',
                hoverIcon: 'icon-wodeshixin iconfont',
                url: '/my/my',
            },
            {
                _id: '5',
                name: '更多',
                iconClass: 'iconfont icon-others index_page_bottomBtn',
                hoverIcon: 'icon-gengduo iconfont',
                url: '/other/other',
            }];
        $rootScope.tabBottomItemWidth = "20%";
        //$rootScope.tabBottomItemWidth = 100 / $scope.bottomTabList.length + "%";
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
        $rootScope.showSearchInputCon = function () {
            $rootScope.showSearchInput = !$rootScope.showSearchInput;
            $rootScope.goPage('/product/index');
        }
        //当值为Null时改为0
        $rootScope.isNullZero = function (number) {
            if ($rootScope.isEmpty2(number)) {
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
        $rootScope.getMoney = function (money) {
            money = $rootScope.isNullZero(money);
            var moneyStr = money.toString().split(".")[1];
            if (!window.isEmpty(moneyStr) && moneyStr.length > 2) {
                if (moneyStr.substring(2, 3) == "9") {
                    money = money.toFixed(2);
                }
            }
            return money.toString().replace(/([0-9]+.[0-9]{2})[0-9]*/, "$1");
        };

        $rootScope.getPension = function (money, integral) {
            if (window.isEmpty(money) || window.isEmpty(integral) || money <= 0 || integral <= 0) {
                return 0;
            }
            return $rootScope.getMoney(money * integral / 100.0 / 2.0);
        }

        $rootScope.formatAddress = function (address) {
            if (window.isEmpty(address) || address.indexOf('省') === -1) {
                return;
            }
            var list = address.split("省");
            list.remove(0);
            var re = '';
            $.each(list, function (k, v) {//防止出现两个'省'
                re += v;
            });
            return re;
        }
        $rootScope.setScopeFlag = function (entity) {
            $rootScope[entity] = !$rootScope[entity];
        }
        //清空选择的地址
        //$rootScope.clearLocation = function () {
        //    $rootScope.locationSelected = null;
        //    $rootScope.locationArea = null;
        //    $rootScope.locationAreaValue = null;
        //    $rootScope.locationAreaPValue = null;
        //}
        //显示图片
        $rootScope.iconImgUrl = function (list, icon) {
            return list != null && icon != null ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfMember_page/img/notImg02.jpg';
        }
        $rootScope.iconImgUrl = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon + '&wh=300_300') : '/yzxfMember_page/img/notImg02.jpg';
        }
        $rootScope.iconImg = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon ) : '/yzxfMember_page/img/notImg02.jpg';
        };
        $rootScope.setOtherId = function () {
            setCookie("_other_id", "_" + genUUID());
        }
        // 获取商户头像，若没有则获取门头照
        $rootScope.getSellerIcon = function (icon, doorImg) {
            if (window.isEmpty(icon)) {
                if (!window.isEmpty(doorImg)) {
                    return doorImg.split("_")[0];
                } else {
                    return '';
                }
            } else {
                return icon;
            }
        };
        //处理距离单位,大于1km,单位km;小于为m
        $rootScope.getDistance = function (num) {
            if (num === 0) {
                return '1 m';
            } else if (window.isEmpty(num) && num !== 0) {
                return '无';
            } else if (num < 1) {
                return $rootScope.getMoney(num * 1000.0) + ' m';
            } else {
                return $rootScope.getMoney(num) + ' km';
            }
        };
        //获取默认支付方式
        $rootScope.getDefaultPayType = function () {
            if (window._isWechat) {
                return 10;
            } else {
                return 4;
            }
        };
        // 处理身份证
        $rootScope.getIdCard = function (text) {
            if (window.isEmpty(text) || text.length != 18) {
                return text;
            }
            return text.substring(0, 4) + "**********" + text.substring(14, 18);
        };
        $rootScope.getMobile = function (text) {
            if (window.isEmpty(text) || text.length != 11) {
                return text;
            }
            return text.substring(0, 3) + "****" + text.substring(7, 11);
        };
        $rootScope.getRealName = function (text) {
            if (window.isEmpty(text) || text.length < 2) {
                return text;
            }
            var str = "";
            for (var i = 0; i < text.length - 1; i++) {
                str += "*";
            }
            return str + text.substring(text.length - 1, text.length);
        };
        $rootScope.isEmpty2 = function (str) {
            return str == null || str == '' || str == 'null';
        }

        $rootScope.goMap = function(locationName,to, toX, toY){
            window.initWeChatConfig($rootScope, $scope, $http, function () {
                wx.openLocation({
                    latitude: toX,//目的地latitude
                    longitude: toY,//目的地longitude
                    name: to,
                    address: to,
                    scale: 15//地图缩放大小，可根据情况具体调整
                });

                wx.error(function (res) {
                    alert(JSON.stringify(res));
                });
            });
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

        window.initWeChatConfig = function ($rootScope, $scope, $http, callback) {
            if (window.initWechatConfig && !window.isWechat()) {
                callback();
                return;
            }
            $.getJSON(window.basePath + '/account/WeChatJS/getConfig?url=' + encodeURIComponent(window.location.href.split('#')[0]), function (re) {
                window.initWechatConfig = true;
                re.content.debug = false;
                if (re.content.url) {
                    re.content.url = encodeURIComponent(re.content.url);
                }
                wx.config(re.content);
                wx.ready(function () {
                    callback();
                });
                wx.error(function (res) {
                });
            });
        }

        $rootScope.getAddress = function(lng,lat){
            var map = new BMap.Map('allmap');
            var point = new BMap.Point(lng,lat);
            map.centerAndZoom(point,12);
            var geoc = new BMap.Geocoder();
            geoc.getLocation(point,function(rs){
                var addComp = rs.addressComponents;
                setCookie("storeArea",JSON.stringify({
                    lat:lat,
                    lng:lng,
                    locationSelected:addComp.district,
                }));
                $rootScope.locationName = addComp.district;
                $rootScope.$apply();
            });
        }
        window.initWeChatConfig($rootScope, $scope, $http, function () {
            wx.getLocation({
                type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                success: function (res) {
                    $rootScope.autoLoaction = res;
                    $rootScope.getAddress(res.longitude,res.latitude);

                    //$rootScope['$$storeArea'].lat =
                    //var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                    //var longitude = res.longitude ; // 经度，浮点数，范围为180 ~ -180。
                    //var speed = res.speed; // 速度，以米/每秒计
                    //var accuracy = res.accuracy; // 位置精度
                }
            });
        })

        $rootScope.scanQrCode = function () {
            if (!window.isWechat()) {
                malert('该功能只能在微信中使用');
                return;
            }
            window.initWeChatConfig($rootScope, $scope, $http, function () {
                wx.scanQRCode({
                    // 默认为0，扫描结果由微信处理，1则直接返回扫描结果
                    needResult: 1,
                    scanType: ["qrCode"],
                    desc: 'scanQRCode desc',
                    success: function (res) {
                        var result = res.resultStr;
                        // if(window.isEmpty(result) || window.isEmpty(result.type)){
                        //     malert("功能开发中");
                        //     return;
                        // }
                        // if(result.type === 'share'){
                        //     $rootScope.goPage("/account/reg/shareId/"+result.shareId);
                        // }else{
                        //     malert("功能开发中");
                        //     return;
                        // }
                        if (window.isEmpty(result)) {
                            malert("解析二维码失败");
                            return;
                        }
                        window.location.href = result;

                    }, fail: function (res) {
                    }
                });
            });
        }

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

        // window.getMap=function(){
        //     // 百度地图API功能
        //     var map = new BMap.Map("allmap");
        //     //var point = new BMap.Point(116.331398,39.897445);
        //     //map.centerAndZoom(point,12);
        //     function myFun(result){
        //         var cityName = result.name;
        //         map.setCenter(cityName);
        //         //$scope.locationName=cityName;
        //         $scope.getAreaByName = function(){
        //             var url = window.basePath + '/crm/Member/getLocationByName?cityName='+cityName;
        //             $http.get(url).success(function(re){
        //                 var locationAllValue = "";
        //                 if (window.isEmpty($scope.pValue)) {
        //                     locationAllValue = "_" + $scope.areaValue + "_";
        //                 } else {
        //                     locationAllValue = $scope.pValue + "_" + $scope.areaValue + "_";
        //                 }
        //                 $rootScope['$$storeArea'] = {
        //                     'locationSelected': re.content.items[0].name,
        //                     'locationAreaValue': re.content.items[0].value,//筛选查询地址value时使用
        //                     'locationAreaPValue': re.content.items[0].pvalue,
        //                     'locationAllValue': locationAllValue,//保存地址value时使用
        //                     'locationId':re.content.items[0]._id,          //区域ID数组
        //                     'locationName':re.content.items[0].name,       //区域name数组
        //                 };
        //                 $scope.locationName=$rootScope['$$storeArea'].locationSelected;
        //                 $scope.selectedValue=re.content.items[0].value;
        //                 $scope.selectedPValue=re.content.items[0].pvalue;
        //             })
        //         }
        //         $scope.getAreaByName();
        //         $scope.$apply();
        //     }
        //
        //     var myCity = new BMap.LocalCity();
        //     myCity.get(myFun);
        // }

        initAngularRootScope($rootScope, $scope, $http, $location);


        //注册设备
        var actions = [];

        actions.push(function (next) {
            if ($location.path().indexOf("payByFixedQrCode") == -1 && $location.path().indexOf("/account/bind") == -1) {
                (function () {
                    $("#allLoadingCon").fadeOut();
                }).delay(1);
            } else {
                $("#allLoadingCon").css("display", "none");
            }
        });
        //$("#allLoadingCon").hide();
        $(document).queue('init', actions);
        $(document).dequeue('init');
        $rootScope.$on('$locationChangeStart', function (e, path) {
            var memberId = getCookie("_member_id");
            if (window.isEmpty(memberId) || typeof(memberId) == "undefined") {
                return;
            }
            var url = window.basePath + '/crm/Member/getMemberIsTrue?memberId=' + memberId;
            $http.get(url).success(function (re) {
                if (!re.content.canUse) {
                    deleteCookie('___MEMBER_TOKEN');
                    deleteCookie('_member_loginName');
                    deleteCookie('loginName');
                    deleteCookie('_member_mobile');
                    deleteCookie('_member_icon');
                    deleteCookie('lastLoginType');
                    deleteCookie('_member_id');
                    $rootScope.myInfo = null;
                    malert("抱歉，账户已禁用");
                }
                $rootScope.$$myInfo = re.content;
            })
        });

        //获取页面高度
        // var clientHeight = document.body.clientHeight;
        // var focusElem;
        //设置监听聚焦事件
        // document.body.addEventListener("focus", function(e) {
        //     focusElem = $('input');
        //     if(window.isMobile() && !$rootScope.isListenerInput){
        //         $('input').focus(function(){
        //             $('.hideMenu').css({position:'absolute',top:0,left:0});
        //         }).blur(function(){
        //             $('.hideMenu').css('position','fixed');
        //         });
        //     }
        // }, true);
        //设置监听窗口变化时间
        // window.addEventListener("resize", function() {
        //     if(window.isMobile() && focusElem && document.body.clientHeight < clientHeight) {
        //         //使用scrollIntoView方法来控制输入框
        //         focusElem.scrollIntoView(false);
        //     }
        // });

    });
})
(angular);