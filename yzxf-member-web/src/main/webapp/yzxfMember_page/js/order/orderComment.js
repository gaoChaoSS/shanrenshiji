/**
 * Created by luoyunze on 16/12/15.
 */
(function (angular, undefined) {

    var model = 'order';
    var entity = 'orderComment';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.btnCheck='disabled';
        //评星
        $scope.starList = [
            {id:1},
            {id:2},
            {id:3},
            {id:4},
            {id:5},
        ];
        $scope.startCheck=function(id){
            $scope.starGrade=id;
            $scope.submitCheck();
        }
        $scope.submitCheck=function(){
            if(!window.isEmpty($scope.starGrade) && !window.isEmpty($scope.commentText)){
                $scope.btnCheck=false;
            }else{
                $scope.btnCheck='disabled';
            }
        }
        $scope.submitForm=function(){
            var url = window.basePath + '/order/OrderInfo/addOrderComment';
            var data = {'orderId':$rootScope.pathParams.id,
                        'serviceStar':$scope.starGrade,
                        'name':$scope.commentText,
                        'sellerId':$rootScope.pathParams.sellerId
                        };
            $http.post(url,data).success(function(){
                $rootScope.goPage("/my/order")
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '评价订单');
                //$rootScope.windowTitleHide = true;
                $rootScope.isIndex = false;
                $rootScope.isLoginPage = true;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);


