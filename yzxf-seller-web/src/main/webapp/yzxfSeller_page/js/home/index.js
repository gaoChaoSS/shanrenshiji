(function (angular, undefined) {

    var model = 'home';
    var entity = 'index';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //扫一扫遮挡框
        $scope.saoyisao = false;
        //发卡记录遮挡框
        $scope.sendCardLog = false;

        $scope.isLoadMore = true;
        //获取发卡点的信息
        $scope.getFactorInfo = function () {
            $scope.factorId = getCookie("_factor_id");
            var url = window.basePath + '/account/Factor/getFactorInfo';
            $http.get(url).success(function (re) {
                $scope.factorInfo = re.content;
                window.uploadWinObj = {
                    one: true,
                    entityName: 'Factor',
                    entityField: 'icon',
                    entityId: getCookie('_factor_id'),
                    callSuccess: function (options) {
                        $rootScope.$apply(function () {
                            $scope.saveData(options.fileId);
                        })
                    }
                };

            })
        }
        $scope.iconImgUrl = function () {
            return $scope.factorInfo != null && $scope.factorInfo.icon != null ? ('/s_img/icon.jpg?_id=' + $scope.factorInfo.icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
        }

        //商家版用户登陆后发卡页面的权限
        $scope.getUserCanUseFactor = function () {
            var url = window.basePath + '/account/User/getUserCanUseFactor?userId='+getCookie('_user_id');
            $http.get(url).success(function (re) {
                if (re.content.items == null) {
                    $scope.showType = 'notAccess';
                    $rootScope.notFactor=true;
                    if($rootScope.notSeller && $rootScope.notFactor){
                        $rootScope.goPage('/account/login');
                    }
                } else {
                    $scope.showType = 'ok';
                    $scope.getFactorInfo();
                    $rootScope.notFactor=false;
                }
            }).error(function(){
                $rootScope.goPage('/account/login');
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.showType = 'loading';
                $scope.getUserCanUseFactor();
                window.setWindowTitle($rootScope, '服务站');
                //$rootScope.isIndex = true;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);