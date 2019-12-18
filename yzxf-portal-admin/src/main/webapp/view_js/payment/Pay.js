(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $popWindow, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        // 1：积分，2：优惠券，3：现金账户，4：支付宝，5：pos刷卡, 6：现金收款，7：银行转账，8：其他, 9:产品卡, 10:微信, 11:活动折扣 12:美团在线
        $scope.payTypeList = [
            {_id: '1', name: "积分"}
            , {_id: '2', name: "优惠券"}
            , {_id: '3', name: "现金账户"}
            , {_id: '4', name: "支付宝"}
            , {_id: '5', name: "银联POS刷卡"}
            , {_id: '6', name: "现金收款"}
            , {_id: '7', name: "银行转账"}
            , {_id: '8', name: "其他"}
            , {_id: '9', name: "产品卡"}
            , {_id: '10', name: "微信"}
            , {_id: '11', name: "活动折扣"}
            , {_id: '12', name: "美团在线"}
            , {_id: '13', name: "扫呗微信"}
            , {_id: '14', name: "扫呗支付宝"}
            , {_id: '15', name: "扫呗QQ钱包"}
        ];
        $scope.clientTypeList = [
            {_id: 'PcWeb', name: 'PC浏览器'}
            , {_id: 'MobileWeb', name: '移动浏览器'}
            , {_id: 'QrCode', name: '扫码支付'}
            , {_id: 'ScanCode', name: '刷卡支付'}
            , {_id: 'JsApi', name: '微信公众号内支付'}
        ];
        $scope.payStatusList = [
            {_id: 'START', name: '未支付'}
            , {_id: 'SUCCESS', name: '成功'}
            , {_id: 'FAIL', name: '失败'}
        ];

        $scope.payTypeMap = {};
        $.each($scope.payTypeList, function (k, v) {
            $scope.payTypeMap[v._id] = v.name;
        });

        $scope.clientTypeMap = {};
        $.each($scope.clientTypeList, function (k, v) {
            $scope.clientTypeMap[v._id] = v.name;
        });

        $scope.payStatusMap = {};
        $.each($scope.payStatusList, function (k, v) {
            $scope.payStatusMap[v._id] = v.name;
        });

        $scope.getStatusClass = function (status) {
            return status == 'SUCCESS' ? 'green' : (status == 'FAIL' ? 'high' : 'notHigh');
        }

        //将所有商户房租map
        $scope.SellerMap = {}, $scope.SellerList = [];
        $scope.querySellerMap = function () {
            var url = window.basePath + '/account/Seller/query?pageSize=500';
            $http.get(url).success(function (re) {
                $scope.SellerList = re.content.items;
                $.each(re.content.items, function (k, item) {
                    $scope.SellerMap[item._id] = item.name;
                });
                $scope.queryList();
            });
        }
        $scope.StoreMap = {}, $scope.StoreList = [];
        $scope.queryStoreMap = function () {
            var url = window.basePath + '/account/StoreInfo/query?pageSize=500';
            $http.get(url).success(function (re) {
                $scope.StoreList = re.content.items;
                $.each(re.content.items, function (k, item) {
                    $scope.StoreMap[item._id] = item.name;
                });
                $scope.querySellerMap();
            });

        }

        var base_url = window.basePath + '/payment/Pay';
        $scope.filter = {};
        $scope.queryList = function () {
            $scope.filter.pageSize || ($scope.filter.pageSize = 50);
            var url = base_url + '/query?1=1';
            $.each($scope.filter, function (k, v) {
                url += '&' + k + '=' + encodeURIComponent(v);
            });
            $http.get(url).success(function (re) {
                $scope.dataPage = re.content;
                $.each($scope.dataPage.items, function (k, item) {
                    item.$$createTime = new Date(item.createTime).showDateTime();
                    item.$$payType = $scope.payTypeMap[item.payType];
                    item.$$clientType = $scope.clientTypeMap[item.clientType];
                    item.$$sellerName = $scope.SellerMap[item.sellerId];
                    item.$$storeName = $scope.StoreMap[item.storeId];
                    item.$$payStatus = $scope.payStatusMap[item.payStatus];
                });

                if ($scope.dataPage.totalPage > 1) {
                    $scope.dataPage.$$pages = [];
                    var start = $scope.dataPage.pageNo - 5, end = $scope.dataPage.pageNo + 5;
                    start = start < 1 ? 1 : start;
                    end = end > $scope.dataPage.totalPage ? $scope.dataPage.totalPage : end;
                    $scope.dataPage.$$start = start;
                    $scope.dataPage.$$end = end;
                    for (var i = start; i <= end; i++) {
                        $scope.dataPage.$$pages.push(i);
                    }
                }
            });
        }
        $scope.payReturn = function (item) {
            localStorage.setItem("payReturn_pay_item", JSON.stringify(item));

            var popWinId = 'PayTui';
            $popWindow.add(popWinId, '退款情况', 700, 500, '/payment/PayReturn');
            $scope.$on(popWinId + '/init_end', function (event, obj) {
            })
        }

        $scope.payQuery = function (item) {
            $http.get(window.basePath + '/payment/Pay/queryPayResult?payId=' + item._id).success(function (re) {
                if (re.content.payStatus == 'START') {
                    malert('支付未完成,请检查!');
                } else {
                    //根据订单的情况,5s后进行跳转
                    malert('支付成功!');
                    item.payStatus = re.content.payStatus;
                    item.$$payStatus = $scope.payStatusMap[item.payStatus];
                }
            });
        }

        $scope.queryAll = function () {
            $scope.filter = {};
            $scope.queryList();
        }


        $scope.queryStoreMap();
    });
})(angular);
