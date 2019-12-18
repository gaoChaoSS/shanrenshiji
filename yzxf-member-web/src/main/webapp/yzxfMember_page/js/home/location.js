(function (angular, undefined) {

    var model = 'home';
    var entity = 'location';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        var init = function () {
            //地区ID
            $scope.locationId = null;
            $scope.locationName = null;
            //地区名字
            $scope.areaName = [null, null, null, null];

            //地区value
            $scope.areaValue = "";

            //地区pvalue
            $scope.pValue = "";

            //地区集合
            $scope.list1 = null;
            $scope.list2 = null;
            $scope.list3 = null;
            $scope.list4 = null;

            $scope.areaId = [null, null, null, null];

            //经纬度
            $scope.longitude = '';
            $scope.latitude = '';

            //是否有四级选项
            //$scope.isList4 = false;
        }
        //获取地理位置
        $scope.getLocation = function (parentCode, level,code, name,id,value,pValue) {
            var url = window.basePath + '/crm/Member/getLocation?parentCode=' + parentCode;
            $http.get(url).success(function (re) {
                if (!window.isEmpty(level)) {
                    $scope.areaValue = value;
                    $scope.pValue = pValue;
                    $scope.parentCode = parentCode;
                    $scope.areaLevel = level;
                    $scope.locationId = code;
                    $scope.locationName = name;
                }

                if (level == "01") {
                    $scope.list1 = re.content.items;
                }
                if (level == "02") {
                    $scope.list2 = re.content.items;
                    $scope.areaId[0] = parentCode;
                    $scope.areaName[0] = name;

                    $scope.list3 = null;
                    $scope.list4 = null;

                    $scope.areaId[1] = null;
                    $scope.areaId[2] = null;
                    $scope.areaId[3] = null;

                    $scope.areaName[1] = "";
                    $scope.areaName[2] = "";
                    $scope.areaName[3] = "";
                }
                if (level == "03") {
                    $scope.list3 = re.content.items;
                    $scope.areaId[1] = parentCode;
                    $scope.areaName[1] = name;

                    $scope.list4 = null;

                    $scope.areaId[2] = null;
                    $scope.areaId[3] = null;

                    $scope.areaName[2] = "";
                    $scope.areaName[3] = "";
                }
                if (level == "04") {
                    $scope.list4 = re.content.items;
                    //if ($scope.list4.length > 0) {
                    //    $scope.isList4 = true;
                    //} else {
                    //    $scope.isList4 = false;
                    //}
                    $scope.areaId[2] = parentCode;
                    $scope.areaName[2] = name;

                    $scope.areaName[3] = "";

                    $scope.areaId[3] = null;
                }
                if (level == "05") {
                    $scope.areaName[3] = name;
                    $scope.areaId[3] = parentCode;
                }
            })
        }

        $scope.getLocationBySelected = function () {
            //if($rootScope['$$storeArea'] != null){
            //    var valueStr = $rootScope['$$storeArea'].locationAllValue;
            //    valueStr=$rootScope['$$storeArea'].locationAllValue.substring(1,valueStr.length-1);
            //
            //    var valueArr = valueStr.split("_");//区域value数组
            //    var idArr = $rootScope['$$storeArea'].locationId;//id数组
            //    var nameArr = $rootScope['$$storeArea'].locationName;//name数组
            //
            //    for(var index=0;index<valueArr.length+1;index++){
            //        $scope.getLocation(index==0?-1:idArr[index-1], index+1,nameArr[index],valueArr[index],index==0?null:valueArr[index-1]);
            //    }
            //}else{
            $scope.getLocation("CN", "01");
            //}
        }

        $scope.goBackFun=function(){
            $rootScope.notRef=true;
            $rootScope.goBack();
        }

        $scope.isGoPage = function () {
            var area = $scope.areaName.join("");
            if (window.isEmpty($rootScope.pathParams.locationQuery)) {
                if (window.isEmpty(area)) {
                    malert("请选择城市");
                    return;
                }
                if (window.isEmpty($scope.areaName[0]) || window.isEmpty($scope.areaName[1]) ||
                    window.isEmpty($scope.areaName[2])) {
                    malert("请至少选择三级地址");
                    return;
                }
            } else {
                if ($scope.selectBtn() == '所有商家') {
                    $rootScope['$$storeArea'] = 'all';
                    setCookie('storeArea', JSON.stringify($rootScope['$$storeArea']));
                    $rootScope.goBack();
                    return;
                }
            }

            var locationAllValue = "";
            if (!window.isEmpty(area)) {
                if (window.isEmpty($scope.pValue)) {
                    locationAllValue = "_" + $scope.areaValue + "_";
                } else {
                    locationAllValue = $scope.pValue + "_" + $scope.areaValue + "_";
                }
            }

            $rootScope['$$' + $rootScope.pathParams.selectedArea] = {
                locationSelected: $scope.locationName,
                locationArea: area,
                locationAreaValue: $scope.areaValue,//筛选查询地址value时使用
                locationAreaPValue: $scope.pValue,
                locationAllValue: locationAllValue,//保存地址value时使用
                locationId: $scope.areaId,          //区域ID数组
                locationName: $scope.areaName,       //区域name数组
                lng: $scope.longitude,   //经度
                lat: $scope.latitude     //纬度
            };

            if ($rootScope.pathParams.selectedArea == 'storeArea') {
                if (window.isEmpty($scope.longitude) || window.isEmpty($scope.latitude)) {
                    $scope.getLatAndLong(area);
                    return;
                }
                setCookie('storeArea', JSON.stringify($rootScope['$$' + $rootScope.pathParams.selectedArea]));
            }
            $rootScope.goBack();
        }

        //确定按钮
        $scope.selectBtn = function () {
            if (!window.isEmpty($rootScope.pathParams.locationQuery)) {
                if (window.isEmpty($scope.locationName)) {
                    return '所有商家';
                }
            }
            return '确定';
        };

        $scope.goPageByLat = function (item) {
            $scope.longitude = item.location.lng;
            $scope.latitude = item.location.lat;
            $scope.locationName = item.title;
            $scope.isGoPage();
        };

        //根据地址获取经纬度
        $scope.getLatAndLong = function (area) {
            if(window.isEmpty(area)){
                return;
            }
            var url = 'https://apis.map.qq.com/ws/geocoder/v1/?address=' + area + '&key=ZGWBZ-7CWW4-OG2UV-DA5LD-CAJTV-RKBXI&output=jsonp';
            $.ajax({
                async: false,
                url: url,
                type: "GET",
                dataType: 'jsonp',
                jsonp: 'callback',
                jsonpCallback: "QQmap",
                beforeSend: function () {
                }, success: function (json) {//客户端jquery预先定义好的callback函数,成功获取跨域服务器上的json数据后,会动态执行这个callback函数
                    if (json.status == 347) {
                        $scope.longitude = '';
                        $scope.latitude = '';
                        malert('未获取到坐标,请重新选择!(至少选择到市级)');
                    } else {
                        $rootScope['$$' + $rootScope.pathParams.selectedArea].lng = json.result.location.lng;
                        $rootScope['$$' + $rootScope.pathParams.selectedArea].lat = json.result.location.lat;

                        if ($rootScope.pathParams.selectedArea == 'storeArea') {
                            setCookie('storeArea', JSON.stringify($rootScope['$$' + $rootScope.pathParams.selectedArea]));
                        }
                        $rootScope.goBack();
                    }
                }
            });
        };

        $scope.initSearch = function () {
            $scope.searchList = [];
            $scope.pageNo = 1;
            $scope.totalPage = 1;
            $scope.totalNum = 1;
        };

        $scope.isShowCity = function () {
            $scope.showCity = window.isEmpty(keyword);
        };

        //搜索
        $scope.search = function (newSearch) {
            var currentCity;
            if (window.isEmpty($scope.areaName) || $scope.areaName.length == 0) {
                malert("请选择城市后进行搜索");
                return;
            } else {
                currentCity = $scope.areaName.join("");
                if (window.isEmpty(currentCity)) {
                    malert("请选择城市后进行搜索");
                    return;
                }
            }
            if (window.isEmpty($scope.keyword)) {
                malert("请输入街道名称");
                return;
            }
            if ($scope.keyword.length > 64) {
                malert("街道名称过长");
                return;
            }
            if (newSearch) {
                $scope.initSearch();
            }
            $scope.showCity = false;
            var url = 'https://apis.map.qq.com/ws/place/v1/search?keyword=' + encodeURI($scope.keyword) + '&boundary=region('
                + currentCity + ',0)&orderby=_distance&key=ZGWBZ-7CWW4-OG2UV-DA5LD-CAJTV-RKBXI&output=jsonp&page_index=' + $scope.pageNo;
            $.ajax({
                async: false,
                url: url,
                type: "GET",
                dataType: 'jsonp',
                jsonp: 'callback',
                jsonpCallback: "QQmap",
                beforeSend: function () {
                }, success: function (json) {//客户端jquery预先定义好的callback函数,成功获取跨域服务器上的json数据后,会动态执行这个callback函数
                    if (json.status == 0) {
                        if (newSearch) {
                            $scope.totalNum = json.count;
                            $scope.totalPage = $scope.totalNum / 10;
                        }
                        $.each(json.data, function (k, v) {
                            $scope.searchList.push(v);
                        });
                        $scope.$apply();
                    }
                }
            });
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '选择城市');
                $rootScope.isIndex = false;
                init();
                $scope.initSearch();
                $scope.keyword = '';
                $scope.showCity = true;
                $scope.getLocationBySelected();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);