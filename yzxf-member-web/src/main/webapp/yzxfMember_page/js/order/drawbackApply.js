(function (angular, undefined) {
    var model = "order";
    var entity = "drawbackApply";
    window.app.register.controller('order_drawbackApply_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {

        $scope.uploadFile = function (inputObj) {
            window.uploadWinObj = {
                one: true,
                entityName: 'OrderInfo',
                entityField: 'returnImg',
                entityId: $rootScope.pathParams.orderId,
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        $scope.returnImg=options.fileId;
                    })
                }
            };
            window.uploadWinObj.files = inputObj.files;
            window.uploadFile();
        };

        $scope.delFileItem = function () {
            $scope.returnImg = "";
        };

        $scope.getOrderInfo=function(){
            if(window.isEmpty($rootScope.pathParams.orderId)){
                malert("获取订单失败!");
                $rootScope.goPage("/my/order");
                return;
            }
            var url = window.basePath + "/order/OrderInfo/queryMyOrder?orderId="+$rootScope.pathParams.orderId;
            $http.get(url).success(function(re){
                $scope.order = re.content.orderList[0];
            });
        };

        $scope.submitCheck=function(){
            if(window.isEmpty($scope.returnDesc)){
                $scope.btnCheck='disbaled';
            }else{
                $scope.btnCheck=false;
            }
        };

        $scope.submitForm=function(){
            var url = window.basePath + '/order/OrderInfo/applyDrawbackOnlineOrder';
            var data = {
                orderId:$rootScope.pathParams.orderId,
                returnDesc:$scope.returnDesc,
                returnImg:$scope.returnImg
            };
            $http.post(url,data).success(function(){
                malert("申请退货成功!");
                $rootScope.goBack();
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '申请退货');
                $scope.btnCheck='disabled';
                $rootScope.isIndex = false;
                $rootScope.isLoginPage = true;
                $scope.getOrderInfo();
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
