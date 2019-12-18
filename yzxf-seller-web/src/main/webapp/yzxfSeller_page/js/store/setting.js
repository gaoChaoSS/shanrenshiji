/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'setting';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.logout = function () {
            deleteCookie('___USER_TOKEN');
            deleteCookie('_user_name');
            deleteCookie('_user_icon');
            deleteCookie('_user_id');
            deleteCookie('_seller_id');
            deleteCookie('_factor_id');
            $rootScope.loginInfo = null;
            $rootScope.goPage('/account/login');
        };
        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.sellerInfo = re.content;
                window.uploadWinObj = {
                    one: true,
                    entityName: 'Seller',
                    entityField: 'icon',
                    entityId: getCookie('_seller_id'),
                    callSuccess: function (options) {
                        $rootScope.$apply(function () {
                            $scope.saveDataSeller(options.fileId);
                        })
                    }
                };
            });
        }

        $scope.uploadFile = function (inputObj, type) {
            window.uploadWinObj.files = inputObj.files;
            window.uploadWinObj.entityField = type;
            window.uploadFile();
        }

        $scope.saveDataFactor = function (iconStr) {
            var url = window.basePath + '/account/Factor/saveFactorIcon';
            $http.put(url, {icon: iconStr}).success(function () {
                malert('修改成功!');
                $scope.factorInfo.icon = iconStr;
            });
        }
        $scope.saveDataSeller = function (iconStr) {
            var url = window.basePath + '/account/Seller/saveSellerIcon';
            $http.put(url, {icon: iconStr}).success(function () {
                malert('修改成功!');
                $scope.sellerInfo.icon = iconStr;
            });
        }
        $scope.getFactorInfo = function () {
            var url = window.basePath + '/account/Factor/getFactorInfo';
            $http.get(url).success(function (re) {
                $scope.factorInfo = re.content;
                $scope.iconImgUrlFactor = function () {
                    return $scope.factorInfo != null && $scope.factorInfo.icon != null ? ('/s_img/icon.jpg?_id=' + $scope.factorInfo.icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
                }
                window.uploadWinObj = {
                    one: true,
                    entityName: 'Factor',
                    entityField: 'icon',
                    entityId: getCookie('_factor_id'),
                    callSuccess: function (options) {
                        $rootScope.$apply(function () {
                            $scope.saveDataFactor(options.fileId);
                        })
                    }
                };
            });
        }

        $scope.showSet=function(){
            if($scope.userType=='seller'){
                return true;
            }
            return false;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '设置');
                $scope.userType = $rootScope.pathParams.userType;
                if($scope.userType=='seller'){
                    $scope.getStoreInfo();
                }else{
                    $scope.getFactorInfo();
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
