(function (angular, undefined) {

    var model = 'other';
    var entity = 'agreeAll';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.menuList=[
            {title:"商家服务协议",include:"/yzxfMember_page/view/other/agreeText_sellerApply.jsp"}
        ];

        $scope.getMenu=function(index){
            $scope.curMenu = $scope.menuList[index];
        };

        $scope.closeMenu=function(){
            $scope.curMenu=null;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '相关协议');
                $rootScope.isIndex = true;
                $scope.curMenu=null;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);