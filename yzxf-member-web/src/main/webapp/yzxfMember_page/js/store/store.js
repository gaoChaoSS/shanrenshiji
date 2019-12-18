(function (angular, undefined) {
    var model = "store";
    var entity = "store";
    window.app.register.controller('store_store_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //初始化条件菜单的默认选项
        $scope.selectType = '经营范围';
        $scope.orderName = "排序方式";
        $scope.selectedIndex = -1;

        $scope.isLoadMore = true;
        window.indexNum = 0;
        window.pageNo = 1;
        window.pageSize = 10;

        $scope.locationName = "";
        $scope.selectedValue = "";
        $scope.selectedPValue = "";

        $scope.typeList = [
            {name: '积分率↓',index:0},
            //{name: '距离最近',index:1},
            {name: '推荐商家',index:2},
            {name: '销量↓',index:3},
            {name: '评分↓',index:4}
        ];

        $scope.storeInclude = "/yzxfMember_page/view/store/i_store.jsp";

        //$scope.iconImgUrlTop = function (icon) {
        //    return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon + '&wh=150_70') : '/yzxfMember_page/img/notImg02.jpg';
        //}

        $scope.selectedCheck = function (index, name) {
            if (index == $scope.selectedIndex) {
                $scope.selectedIndex = -1;
                $scope.orderName = "排序方式";
            } else {
                $scope.selectedIndex = index;
                $scope.orderName = name;
            }
            $scope.isLoadMore = true;
            window.indexNum = 0;
            window.pageNo = 1;
            window.pageSize = 10;
            $scope.orderCheck = false;
            $scope.sellerList = [];
            $scope.getSellerList();
        }
        $scope.getFoodCourList = function(){
            var url = window.basePath + '/account/Seller/getFoodCourList?1=1';
            if (!window.isEmpty($rootScope['$$storeArea'].lat) && !window.isEmpty($rootScope['$$storeArea'].lng)){
                url += "&lat=" + $rootScope['$$storeArea'].lat+"&lng="+$rootScope['$$storeArea'].lng;
            }

            $http.get(url).success(function(re){
                $scope.foodCourList = re.content.items;
            })
        }

        //获取商家列表
        $scope.getSellerList = function () {
            if($scope.selectedIndex==-1 && !window.isEmpty($rootScope.pathParams.selectedIndex)){
                $scope.selectedIndex=$rootScope.pathParams.selectedIndex;
                // $rootScope.pathParams.selectedIndex="";
                // $location.path("/store/store");
            }
            // if ($location.path() != '/store/store') {
            //     return;
            // }

            if ($rootScope['$$storeArea'] != null) {
                $scope.locationName = $rootScope['$$storeArea'].locationSelected;
                $scope.selectedValue = $rootScope['$$storeArea'].locationAreaValue;
                $scope.selectedPValue = $rootScope['$$storeArea'].locationAreaPValue;
            }

            if (!window.isEmpty($scope.selectedValue)) {
                if (window.isEmpty($scope.selectedPValue)) {
                    $scope.selectedValue = "_" + $scope.selectedValue + "_";
                } else {
                    //$scope.selectedValue = $scope.selectedPValue.replace(new RegExp("_", "g"), "\\_") + "\\_" + $scope.selectedValue + "\\_";
                    $scope.selectedValue = $scope.selectedPValue + "_" + $scope.selectedValue + "_";
                }
            }
            if ($rootScope['$$operateStore'] != null) {
                $scope.selectType = $rootScope['$$operateStore'].operateName;
                $scope.selectOperateValue = $rootScope['$$operateStore'].operateValue;
            } else {
                $scope.selectType = '经营范围';
            }
            var url = window.basePath + "/account/Seller/querySellerList?1=1";
            if ($scope.selectType != '经营范围') {
                url += "&selectType=" + $scope.selectOperateValue;
            }
            url += "&selectedIndex=" + $scope.selectedIndex;
            if (!window.isEmpty($scope.selectedValue)) {
                url += "&areaValue=" + $scope.selectedValue;
            }
            if (!window.isEmpty(window.indexNum)) {
                url += "&indexNum=" + window.indexNum;
            }
            if (!window.isEmpty(window.pageNo)) {
                url += "&pageNo=" + window.pageNo;
            }
            if (!window.isEmpty(window.pageSize)) {
                url += "&pageSize=" + window.pageSize;
            }
            if (!window.isEmpty($rootScope['$$storeArea'].lat) && !window.isEmpty($rootScope['$$storeArea'].lng)){
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

        }
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
                //$("#moreButton").click();
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
            $('#storeScroll').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.more = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.getSellerList();
        };

        window.getMap = function() {
            //var center = new qq.maps.LatLng(39.916527, 116.397128);
            //map = new qq.maps.Map(document.getElementById('container'), {
            //    center: center,
            //    zoom: 13
            //});
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
                    lng: lng ,     //经度
                    fromX: lat,       //纬度
                    fromY: lng      //经度
                };
                setCookie('storeArea', JSON.stringify($rootScope['$$storeArea']));
                $scope.locationName=$rootScope['$$storeArea'].locationSelected;
                $scope.selectedValue=re.content.items[0].value;
                $scope.selectedPValue=re.content.items[0].pvalue;
                $scope.fromX=lat;
                $scope.fromY=lng;
                $scope.getSellerList();
                $scope.getFoodCourList();
            })
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商家');
                //if(window.isWechat()){
                //    $rootScope.goPage("/my/my");
                //    return;
                //}
                if($rootScope['$$storeArea']=='all'){
                    $scope.getSellerList();
                    $scope.getFoodCourList();
                }else if (!window.isEmpty(getCookie("storeArea"))) {
                    $rootScope.$$storeArea = JSON.parse(getCookie("storeArea"));
                    $scope.locationName = $rootScope['$$storeArea'].locationSelected;
                    $scope.selectedValue = $rootScope['$$storeArea'].locationAreaValue;
                    $scope.selectedPValue = $rootScope['$$storeArea'].locationAreaPValue;
                    $scope.fromX=$rootScope['$$storeArea'].fromX;
                    $scope.fromY=$rootScope['$$storeArea'].fromY;

                    if (!window.isEmpty($scope.selectedValue)) {
                        if (window.isEmpty($scope.selectedPValue)) {
                            $scope.selectedValue = "_" + $scope.selectedValue + "_%";
                        } else {
                            $scope.selectedValue = $scope.selectedPValue.replace(new RegExp("_", "g"), "\\_") + "\\_" + $scope.selectedValue + "\\_%";
                        }
                    }
                    $scope.getSellerList();
                    $scope.getFoodCourList();
                }else{
                    window.getMap();
                }
                $scope.isLoadMore = true;
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 10;
                $rootScope.isIndex = true;
                $scope.sellerList = [];
                $scope.addScrollEvent();
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