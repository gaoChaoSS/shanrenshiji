(function (angular, undefined) {

    var model = 'home';
    var entity = 'index';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http,$interval, $element, $compile) {
            $scope.indexBtnDiv = false;
            //window.indexNum = 0;
            //window.pageNo = 1;
            //window.pageSize = 8;

            //不显示商家数量
            $scope.notShowSellerNum=true;

            //$scope.getSellerTypeList = function () {
            //    var url = window.basePath + '/account/SellerType/query?pageSize=8';
            //    $http.get(url).success(function (re) {
            //        //$scope.sellerType=
            //        $scope.sellerTypeList = re.content.items;
            //    })
            //}

            $scope.getMemberBanner = function(){
                var url = window.basePath +'/account/User/getMemberBanner';
                $http.get(url).success(function(re){
                    $scope.imgList = re.content.items;
                })
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
            $scope.advertisingGoPage = function(obj){
                if(obj.entityType=='Seller'){
                    $rootScope.goPage('/store/storeInfo/sellerId/'+obj.entityId);
                }else if(obj.entityType=='ProductInfo'){
                    $rootScope.goPage('/store/commodity/goodsId/'+obj.entityId);
                }else if(obj.entityType=='link'){
                    if(obj.entityId.indexOf("yzxfMember")==-1){
                        $rootScope.goPage(obj.entityId);
                    }else{
                        $rootScope.goPage(obj.entityId.split("yzxfMember")[1]);
                    }
                }
            }

            //获取商家列表
            $scope.getIndexSellerList = function () {
                //if ($location.path() != '/home/index') {
                //    return;
                //}
                if (!window.isEmpty(getCookie("storeArea"))) {
                    $rootScope.$$storeArea = JSON.parse(getCookie("storeArea"));
                    $rootScope.locationName = $rootScope['$$storeArea'].locationSelected;
                    $scope.selectedValue = $rootScope['$$storeArea'].locationAreaValue;
                    $scope.selectedPValue = $rootScope['$$storeArea'].locationAreaPValue;

                    if($scope.getSellerCount==0){
                        window.indexNum = 0;
                        window.pageNo = 1;
                        window.pageSize = 8;
                        $scope.sellerList=[];
                        $scope.getSellerCount++;
                    }
                }

                if (!window.isEmpty($scope.selectedValue)) {
                    if (window.isEmpty($scope.selectedPValue)) {
                        $scope.selectedValue = "_" + $scope.selectedValue + "_";
                    } else {
                        //$scope.selectedValue = $scope.selectedPValue.replace(new RegExp("_", "g"), "\\_") + "\\_" + $scope.selectedValue + "\\_";
                        $scope.selectedValue = $scope.selectedPValue + "_" + $scope.selectedValue + "_";
                    }
                }

                var url = window.basePath + "/account/Seller/querySellerList?1=1";
                if(!window.isEmpty(window.indexNum)){
                    url += "&indexNum=" + window.indexNum;
                }else{
                    url += "&indexNum=0";
                }
                if (!window.isEmpty(window.pageNo)) {
                    url += "&pageNo=" + window.pageNo;
                }else{
                    url += "&pageNo=1";
                }
                if (!window.isEmpty(window.pageSize)) {
                    url += "&pageSize=" + window.pageSize;
                }else{
                    url += "&pageSize=8";
                }
                if (!window.isEmpty($scope.selectedValue)) {
                    url += "&areaValue=" + $scope.selectedValue;
                }
                if ($rootScope['$$storeArea']!=null && !window.isEmpty($rootScope['$$storeArea'].lat) && !window.isEmpty($rootScope['$$storeArea'].lng)){
                    url += "&lat=" + $rootScope['$$storeArea'].lat+"&lng="+$rootScope['$$storeArea'].lng;
                }
                $http.get(url).success(function (re) {
                    $.each(re.content.sellerList, function (k, v) {
                        $scope.sellerList.push(v);
                    })
                    $scope.totalNum = re.content.totalNum;
                    $scope.totalPage = re.content.totalPage;
                    if ($scope.totalPage <= window.pageNo) {
                        $scope.isLoadMore = false;
                    }
                });
                //$rootScope.clearLocation();

            };

            window.getMap = function() {
                //设置城市信息查询服务
                $scope.citylocation = new qq.maps.CityService();
                //请求成功回调函数
                $scope.citylocation.setComplete(function(result) {
                    //malert(result.detail.latLng.lat+","+result.detail.latLng.lng);
                    $scope.getLocationByName(result.detail.name,result.detail.latLng.lat,result.detail.latLng.lng);
                });
                $scope.citylocation.searchLocalCity();
            };

            $scope.getLocationByName=function(name,lat,lng){
                var url = window.basePath + '/crm/Member/getLocationByName?cityName='+name;
                $http.get(url).success(function(re){
                    var locationAllValue = "";
                    if (window.isEmpty($scope.pValue)) {
                        locationAllValue = "_" + $scope.areaValue + "_";
                    } else {
                        locationAllValue = $scope.pValue + "_" + $scope.areaValue + "_";
                    }
                    $rootScope['$$storeArea'] = {
                        locationSelected: re.content.items[0].name,
                        locationAreaValue: re.content.items[0].value,//筛选查询地址value时使用
                        locationAreaPValue: re.content.items[0].pvalue,
                        locationAllValue: locationAllValue,//保存地址value时使用
                        locationId:re.content.items[0]._id,          //区域ID数组
                        locationName:re.content.items[0].name,       //区域name数组
                        lat: lat,       //纬度
                        lng: lng      //经度
                    };
                    $rootScope.locationName=$rootScope['$$storeArea'].locationSelected;
                    $scope.selectedValue=re.content.items[0].value;
                    $scope.selectedPValue=re.content.items[0].pvalue;
                    $scope.getIndexSellerList();
                    $scope.getProductList();
                    $scope.queryStoreList();
                })
            };

            $scope.scrollEvent = function () {
                if ($('#moreButton').size() == 0) {
                    return;
                }
                var loadNextPage = function () {
                    window.stopScroll--;
                    if (window.stopScroll > 0) {
                        return;
                    }
                    if ($scope.totalPage <= window.pageNo) {
                        return;
                    }
                    $scope.more();
                }
                if ($('#moreButton').offset().top < ($(window).scrollTop() + $(window).height())) {
                    window.stopScroll || (window.stopScroll = 0);
                    window.stopScroll++;
                    loadNextPage.delay(0.5);
                }
            }

            $scope.addScrollEvent = function () {
                $('.overflowPC').unbind('scroll');
                $(window).unbind('scroll');
                $('#indexScroll').on('scroll', $scope.scrollEvent);
                $(window).on('scroll', $scope.scrollEvent);
            }

            $scope.more = function () {
                window.indexNum++;
                window.pageNo++;
                $scope.getIndexSellerList();
            };

            $scope.pensionCanUse = function () {
                var url = window.basePath + '/crm/Member/pensionCanUse';
                $http.get(url).success(function (re) {
                    if (re.content.items.length > 0) {
                        $rootScope.goPage('/my/pension');
                    } else {
                        $scope.errorPension = true;
                    }
                })
            }
            //$scope.operate = [
            //    {name: '快餐', img: '/yzxfMember_page/img/kuaican.png',value:'_1_8_'},
            //    {name: '火锅', img: '/yzxfMember_page/img/huoguo.png',value:'_1_7_'},
            //    {name: '甜点饮品', img: '/yzxfMember_page/img/tianpin.png',value:'_12_'},
            //    {name: '烧烤', img: '/yzxfMember_page/img/shaokao.png',value:'_1_3_'},
            //    {name: '生鲜果蔬', img: '/yzxfMember_page/img/shenxian.png',value:'_10_'},
            //    {name: '生活超市', img: '/yzxfMember_page/img/lifeMarket.png',value:'_4_'},
            //    {name: '海鲜', img: '/yzxfMember_page/img/haixian.png',value:'_1_2_'},
            //    {name: '药品', img: '/yzxfMember_page/img/yaopin.png',value:'_8_'}
            //];
            $scope.queryType = function () {
                var url = window.basePath + "/account/Seller/queryType";
                $http.get(url).success(function (re) {
                    $scope.operate = re.content.items;
                })
            }
            $scope.queryIndexProduct = function () {
                var url = window.basePath + "/order/ProductInfo/queryIndexProduct";
                $http.get(url).success(function (re) {
                    if (re.content.items.length > 0) {
                        $scope.storePic = re.content.items[0];
                        $scope.storeFPic = re.content.items[1];
                        re.content.items.splice(0, 1);
                        re.content.items.splice(0, 1);
                        $scope.storeInfo = re.content.items;
                    } else {
                        $scope.storePic = [];
                        $scope.storeFPic = [];
                        $scope.storePic.push({icon: null});
                        $scope.storeFPic.push({icon: null});
                        $scope.storeInfo = [];
                        for (var i = 0; i < 4; i++) {
                            $scope.storeInfo.push({icon: null})
                        }
                    }
                    // $scope.storeInfo = re.content.items;
                })
            }

            //分类商家跳转
            $scope.goPageType = function (name, value ,pvalue) {
                var selectedValue="";
                if(window.isEmpty(pvalue)){
                    selectedValue="_"+value+"_";
                }else{
                    selectedValue=pvalue+"_"+value+"_";
                }
                $rootScope["$$operateStore"]={
                    operateName:name,
                    operateValue:selectedValue
                };
                $rootScope.goPage("/store/store");
            }

            $scope.getSellerTypeList = function () {
                var url = window.basePath + '/account/SellerType/getOperateType';
                $http.get(url).success(function (re) {
                    $scope.sellerTypeList = re.content.items;
                })
            }
            //首页数据 天天特价
            $scope.getProductList = function () {
                var url = window.basePath + "/order/ProductInfo/queryProduct?areaValue=" + $scope.selectedValue;
                $http.get(url).success(function (re) {
                    if (re.content.items.length > 0) {
                        $scope.productInfo = re.content.items;
                    } else {
                        var url = window.basePath + "/order/ProductInfo/queryProduct?areaValue=''";
                        $http.get(url).success(function (re) {
                            $scope.productInfo = re.content.items;
                        })
                    }
                });
            }
            //公益专区
            $scope.queryGongyi = function () {
                var url = window.basePath + "/order/ProductInfo/querySpecial?pageNo=1&pageSize=8&type=gongyi";
                $http.get(url).success(function (re) {
                    $scope.productGongyi=re.content.items;
                });
            }
            //首页数据 附近商家
            //$scope.getStoreList = function () {
            //    var url = window.basePath + "/account/StoreInfo/queryStore";
            //    $http.get(url).success(function (re) {
            //        if (re.content.items.length > 0) {
            //            $scope.storePic = re.content.items[0];
            //            $scope.storeFPic = re.content.items[1];
            //            re.content.items.splice(0, 1);
            //            re.content.items.splice(0, 1);
            //            $scope.storeInfo = re.content.items;
            //        } else {
            //            $scope.storePic = [];
            //            scope.storeFPic = [];
            //            $scope.storePic.push({icon: null});
            //            $scope.storeFPic.push({icon: null});
            //            $scope.storeInfo = [];
            //            for (var i = 0; i < 4; i++) {
            //                $scope.storeInfo.push({icon: null})
            //            }
            //        }
            //    });
            //}
            //推荐商家
            $scope.queryStoreList = function () {
                var url = window.basePath + "/account/StoreInfo/queryStoreList?areaValue=" + $scope.selectedValue;
                if (!window.isEmpty($rootScope['$$storeArea']) && !window.isEmpty($rootScope['$$storeArea'].lat)
                    && !window.isEmpty($rootScope['$$storeArea'].lng)){
                    url += "&lat=" + $rootScope['$$storeArea'].lat+"&lng="+$rootScope['$$storeArea'].lng;
                }
                $http.get(url).success(function (re) {
                    $scope.storeTInfo = re.content.items;
                });
            }
            //首页商家展示
            // $scope.getStoreInfo = function () {
            //     var url = window.basePath + "/account/Seller/queryAllSellerList";
            //     $http.get(url).success(function (re) {
            //         $scope.storeList = re.content.items;
            //     });
            // }
            //首页搜索
            $scope.search = function () {
                var keyword = $scope.keywords;
                if (keyword != null && keyword != '') {
                    $rootScope.keyword = keyword;
                    $rootScope.goPage('/store/search');
                } else {
                    malert("搜索关键字不能为空!");
                    return;
                }
            }

            //是否实名认证
            $scope.isRealName = function () {
                var url = window.basePath + '/crm/Member/getMemberIsRealName';
                $http.get(url).success(function (re) {
                    if (re.content.items[0].isRealName == true) {
                        $rootScope.goPage('/my/orderPay');
                    } else {
                        malert('请先实名认证');
                        $rootScope.goPage('/my/realName');
                    }
                })
            };

            //展示在养老金上面的是否认证
            $scope.haveCertification = function () {
                if (window.isEmpty(getCookie('_member_id'))) {
                    $scope.certification=true;
                    return;
                }
                var url = window.basePath + '/crm/Member/getMemberIsRealName';
                $http.get(url).success(function (re) {
                    $scope.certification = re.content.items[0].isRealName;
                })
            };

            //页面事件处理
            $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
                onResume: function () {
                    window.setWindowTitle($rootScope, '首页');

                    //if(window.isWechat()){
                    //    $rootScope.goPage("/my/my");
                    //}else{
                        //$rootScope.windowTitleHide = true;
                        $rootScope.isIndex = true;
                        if($rootScope['$$storeArea']=='all'){
                            $scope.getIndexSellerList();
                            $scope.getProductList();
                            $scope.queryStoreList();
                        }else if (!window.isEmpty(getCookie("storeArea"))) {
                            $rootScope.$$storeArea = JSON.parse(getCookie("storeArea"));
                            $rootScope.locationName = $rootScope['$$storeArea'].locationSelected;
                            $scope.selectedValue = $rootScope['$$storeArea'].locationAreaValue;
                            $scope.selectedPValue = $rootScope['$$storeArea'].locationAreaPValue;

                            if (!window.isEmpty($scope.selectedValue)) {
                                if (window.isEmpty($scope.selectedPValue)) {
                                    $scope.selectedValue = "_" + $scope.selectedValue + "_%";
                                } else {
                                    $scope.selectedValue = $scope.selectedPValue.replace(new RegExp("_", "g"), "\\_") + "\\_" + $scope.selectedValue + "\\_%";
                                }
                            }
                            $scope.getIndexSellerList();
                            $scope.getProductList();
                            $scope.queryStoreList();
                        }else{
                            window.getMap();
                        }
                        $scope.storeInclude = "/yzxfMember_page/view/store/i_store.jsp";

                        $scope.getSellerCount=0;
                        if ($location.path().indexOf("home/index") == -1) {
                            $scope.getSellerCount=0;
                        }
                        window.indexNum = 0;
                        window.pageNo = 1;
                        window.pageSize = 8;
                        $scope.sellerList = [];
                        $scope.isLoadMore = true;
                        $scope.getSellerTypeList();
                        $scope.queryIndexProduct();
                        $scope.haveCertification();
                        $scope.addScrollEvent();
                        $scope.errorPension = false;
                        $scope.memberProtocol = false;
                        $scope.queryType();
                        $scope.imgCur = 0;
                        $scope.imgList=[];
                        $scope.getMemberBanner();
                        $scope.imgCarousel();
                        $scope.queryGongyi();
                }
            };
            $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
        }
    );
})(angular);