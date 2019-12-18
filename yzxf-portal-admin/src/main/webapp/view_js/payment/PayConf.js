(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
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
            , {_id: '10', name: "微信会员版"}
            , {_id: '11', name: "活动折扣"}
            , {_id: '12', name: "美团在线"}
            , {_id: '13', name: "扫呗支付"}
            , {_id: '16', name: "微信会员APP支付"}
            , {_id: '17', name: "微信商家APP支付"}
            , {_id: '18', name: "贵商支付"}
        ];

        var base_url = window.basePath + '/payment/PayConfig';
        $scope.queryList = function () {
            var url = base_url + '/query?pageSize=100';
            $http.get(url).success(function (re) {
                $scope.dataPage = re.content;
                $.each($scope.dataPage.items, function (k, item) {
                    item.$$content = [];
                    if (item.content != null && item.content != '') {
                        //var obj = JSON.parse(item.content);
                        $.each(item.content, function (k, v) {
                            if (k.indexOf('_desc') == -1) {
                                var vobj = {};
                                vobj.name = k;
                                vobj.value = v;
                                vobj.desc = item.content[k + '_desc'];
                                console.log('xxxxx', vobj);
                                item.$$content.push(vobj);
                            }
                        })
                    }
                });
            });
        }

        $scope.addItem = function () {
            $scope.dataPage || ($scope.dataPage = {});
            $scope.dataPage.items || ($scope.dataPage.items = []);
            $scope.dataPage.totalNum || ($scope.dataPage.totalNum = 0);
            var item = {_id: genUUID()};
            $scope.dataPage.items.unshift(item);
            $scope.dataPage.totalNum++;
            $scope.saveItem(item);
        }
        $scope.addContent = function (item) {
            item.$$content || (item.$$content = []);
            item.$$content.push({});
        }

        $scope.saveItem = function (item, isDelete) {
            var data = item;
            item.content = {};
            if (item.$$content != null) {
                $.each(item.$$content, function (k, v) {
                    item.content[v.name] = v.value;
                    item.content[v.name + '_desc'] = v.desc;
                })
            }

            $http.post(base_url + '/save', data).success(function () {
                malert(isDelete ? '删除成功' : '保存成功!');
            });
        }

        $scope.deleteItem = function ($index) {
            var item = $scope.dataPage.items[$index];
            $http.post(base_url + '/del', {_id: item._id}).success(function () {
                $scope.dataPage.items.splice($index, 1);
                $scope.dataPage.totalNum--;
            });
        }

        $scope.queryList();
    });
})(angular);
