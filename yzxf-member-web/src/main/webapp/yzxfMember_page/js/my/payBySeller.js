(function (angular, undefined) {
    var model = "my";
    var entity = "payBySeller";
    window.app.register.controller('my_payBySeller_Ctrl', function ($rootScope, $scope, $location, $interval, $http, $element, $compile) {

        //支付密码输入框
        $scope.getPwdWin = function () {
            $scope.menuCheck = true;
            $scope.pwd1 = null;
            $scope.pwd2 = null;
            $scope.pwd3 = null;
            $scope.pwd4 = null;
            $scope.pwd5 = null;
            $scope.pwd6 = null;
            setTimeout(function () {
                $(".enterInput input").eq(0).focus();
            }, 200);
        };

        $(".enterInput input").bind('keyup', function (e) {
            var currKey = 0, e = e || event;
            currKey = e.keyCode || e.which || e.charCode;
            if (currKey == 8) {
                $(this).val('');
                $(this).prev().val('').focus();
            } else {
                if ($(this).val() == '') {
                    return;
                }
                $(this).next().focus();
            }
        });

        $scope.submitOrder = function () {
            var url = window.basePath + "/order/OrderInfo/payOrderOfflineByMember";
            $http.post(url, {
                orderId: $scope.orderInfo.orderId,
                pwd: $scope.pwd1 + $scope.pwd2 + $scope.pwd3 + $scope.pwd4 + $scope.pwd5 + $scope.pwd6
            }).success(function () {
                $rootScope.goPage("/my/depositSuccess/orderNo/" + $scope.orderInfo.orderNo + "/payType/3");
            });
        };

        $scope.getOrder = function () {
            var url = window.basePath + "/order/OrderInfo/getOrderOfflineByMember";
            $http({
                method: 'GET',
                hideLoading: true,
                url: url
            }).success(function (re) {
                $scope.isPay = re.content != null && re.content.length != 0 && !window.isEmpty(re.content.orderId);
                if ($scope.isPay) {
                    $interval.cancel($scope.countTime);
                    $scope.orderInfo = re.content;
                }
            });
        };

        $scope.delOrder = function () {
            var url = window.basePath + "/order/OrderInfo/del";
            $http.post(url, {_id: $scope.orderInfo.orderId}).success(function () {
                malert("已取消订单");
                $rootScope.goPage("/my/my");
            });
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我要付款');
                $scope.menuCheck = false;

                $scope.getOrder();
                $scope.countTime = $interval(function () {
                    $scope.getOrder();
                    if ($scope.isPay || $location.path().indexOf("payBySeller") == -1) {
                        $interval.cancel($scope.countTime);
                    }
                }, 3000);
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