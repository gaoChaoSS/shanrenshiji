(function (angular, undefined) {
    window.app.register.controller('payment_PayReturn_Ctrl', function ($rootScope, $scope, $location, $http, $popWindow, $element, $compile) {
        $scope.model = 'payment';
        $scope.entity = 'PayReturn';

        $scope.returnStatusList = [
            {_id: 'START', name: '申请中'}
            , {_id: 'SUCCESS', name: '成功'}
            , {_id: 'FAIL', name: '失败'}
        ];
        $scope.returnStatusMap = {};
        $.each($scope.returnStatusList, function (k, v) {
            $scope.returnStatusMap[v._id] = v.name;
        });
        $scope.getStatusClass = function (status) {
            return status == 'SUCCESS' ? 'green' : (status == 'FAIL' ? 'high' : 'notHigh');
        }

        var itemStr = localStorage.getItem("payReturn_pay_item");
        var payInfo = JSON.parse(itemStr);
        localStorage.removeItem("payReturn_pay_item");
        $scope.payInfo = payInfo;

        $scope.queryList = function () {
            $http.get(window.basePath + '/payment/PayReturn/query?pageSize=500&_payId=' + $scope.payInfo._id).success(function (re) {
                $scope.dataPage = re.content;
                $scope.returnAmountAll = 0.0;
                $.each($scope.dataPage.items, function (k, item) {
                    item.$$createTime = new Date(item.createTime).showDateTime();
                    item.$$returnStatus = $scope.returnStatusMap[item.returnStatus];
                    if (item.returnStatus == 'SUCCESS') {
                        $scope.returnAmountAll += parseFloat(item.returnAmount);
                    }
                });
                $scope.returnAmountAll = parseFloat($scope.returnAmountAll.toFixed(2));

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
        $scope.submitReq = function () {
            var returnAmount = 0.0;
            try {
                returnAmount = parseFloat($scope.returnAmount);
            } catch (e) {
            }
            if (isNaN(returnAmount) || returnAmount <= 0) {
                malert('必须输入大于0的数字!');
                $('#returnAmount').addClass('error').focus();
                return;
            }
            if ((returnAmount + $scope.returnAmountAll) > $scope.payInfo.totalFee) {
                malert('累计退款金额不能大于支付金额!');
                $('#returnAmount').addClass('error').focus();
                return;
            }
            if (!confirm('请确认对该笔支付进行退款? 退款后不可撤销!!!')) {
                return;
            }

            $scope.returnAmount = '';
            var url = window.basePath + '/payment/Pay/refund';
            $http.post(url, {
                payId: $scope.payInfo._id,
                returnAmount: returnAmount
            }).success(function (re) {
                malert(re.isSuccess ? '退款成功' : '退款失败');
                $scope.queryList();
            }).error(function () {
                malert('退款失败');
                $scope.queryList();
            })
        }

        $scope.returnQuery = function (item) {
            var url = window.basePath + '/payment/PayReturn/returnQuery?payId=' + item.payId + '&_id=' + item._id;
            $http.get(url).success(function (re) {
                alert(JSON.stringify(re.content));
            }).error(function (re) {
            })
        }

        $scope.queryList();
    });
})(angular);
