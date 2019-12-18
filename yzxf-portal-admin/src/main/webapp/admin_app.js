(function (angular, undefined) {
    window.basePath = '/s_admin/api'
    window.app = angular.module('phonecat', ['ngRoute']);
    window.app.config(function ($controllerProvider, $compileProvider, $filterProvider, $provide) {
        app.register = {
            controller: $controllerProvider.register,
            directive: $compileProvider.directive,
            filter: $filterProvider.register,
            factory: $provide.factory,
            service: $provide.service
        };
    });
    window.app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/:modelName/:actionName', {
            templateUrl: function (rd) {
                return '/view/' + rd.modelName + '/' + rd.actionName + '.jsp';
            },
            // 关键点，用于动态加载js
            resolve: {
                load: function ($q, $route, $rootScope) {
                    var deferred = $q.defer();
                    var dependencies = ['/view_js/' + $route.current.params.modelName + '/' + $route.current.params.actionName + '.js?v=' + window.angular_temp_version];
                    $script(dependencies, function () {
                        $rootScope.$apply(function () {
                            deferred.resolve();
                        });
                    });
                    return deferred.promise;
                }
            }
        });
    }]);
    window.app.controller('allBodyCtrl', function ($rootScope, $scope, $http, $location) {
        $rootScope.sites = {};
        $rootScope.sites.title = "";
        $rootScope.clickAllBody = function () {
            $rootScope.$broadcast('/allMenuHide');
        }


        $scope.loginFormType = 'login';

        console.log("----start-----");
        window.deviceId = getCookie('deviceId');
        var actions = [];
        if (window.deviceId == null) {
            actions.push(function (next) {
                // 注册设备
                var data = genDeviceData();
                data._id = genUUID();
                $http.put(window.basePath + '/common/Device/save', data).success(function (re) {
                    window.deviceId = re.content._id;
                    setCookie('deviceId', window.deviceId);
                    next();
                }).error(next);
            });
        }
        // 加载系统信息
        actions.push(function (next) {
            $http.get(window.basePath + '/common/Setting/site').success(function (re) {
                $scope.site = {};
                $.each(re.content.items, function (k, v) {
                    $scope.site[v.name] = v.value;
                })
                next();
            }).error(next);
        });
        // 加载个人信息
        //actions.push(function (next) {
        //    $http.get(window.basePath + '/account/AdminUser/myInfo').success(function (re) {
        //        $scope.my = re.content;
        //        if (re.content != null) {
        //            $scope.my.$$name = re.content.name ? re.content.name : re.content.loginName;
        //
        //        }
        //        next();
        //    }).error(next);
        //});
        // 加载我的角色
        //actions.push(function (next) {
        //    $http.get(window.basePath + '/account/User/myRoles').success(function (re) {
        //        $scope.my || ($scope.my = {})
        //        $scope.my.roles = re.content.items;
        //        $scope.my.$$roles = '';
        //        $.each($scope.my.roles, function (k, v) {
        //            $scope.my.$$roles += (k == 0 ? '' : ',') + v.title;
        //        })
        //        next();
        //    }).error(next);
        //});
        actions.push(function (next) {
            // 加载菜单
            $http.get(window.basePath + '/common/Menu/queryAll').success(function (re) {
                console.log(re);
                $rootScope.menus = [];
                var path = $location.path();
                if (path != null && path.indexOf('/') > -1) {
                    var paths = path.split('/')
                    $rootScope.menus.__selectMenuId = paths.length > 1 ? paths[1] : null;
                    actionName = paths.length > 2 ? paths[2] : null;
                }

                var menuMap = {};
                $.each(re.content.items, function (k, v) {
                    if (v.pid == "-1") {
                        $rootScope.menus.push(v);
                    } else {
                        menuMap[v.pid] || (menuMap[v.pid] = []);
                        if (v.pid == $rootScope.menus.__selectMenuId && v._id == actionName) {
                            v.__selected = true;
                            $scope.selectedMenu = v;
                        }
                        $rootScope.menus.__openMenu = true;
                        menuMap[v.pid].push(v);
                    }
                });
                $.each($rootScope.menus, function (k, v) {
                    v.items = menuMap[v._id];
                })
                next();
            }).error(next);
        });
        actions.push(function (next) {
            // 链接webscoket
            if (getCookie('___ADMIN_TOKEN') != null) {
                //window.initWebsocket();
            }

            (function () {
                $("#allLoadingCon").fadeOut(100);
            }).delay(0.4);
        });

        $(document).queue('init', actions);
        $(document).dequeue('init');
        $scope.sellerId = '';
        $scope.ccc = function () {
            console.log($scope.sellerId);
        }
        // $rootScope.selectSeller = function (sellerId) {
        //     $rootScope.sellerId = sellerId;
        //     setCookie("sellerId", $rootScope.sellerId);
        //     //同时加载店铺数据:
        //     $http.get(window.basePath + '/common/StoreBase/query?pageSize=500&_sellerId=' + $rootScope.sellerId).success(function (re) {
        //         $scope.storeList = re.content.items;
        //     });
        // }
        // $scope.selectStore = function (storeId) {
        //     $rootScope.storeId = storeId;
        //     if (!window.isEmpty($rootScope.storeId)) {
        //         setCookie("storeId", $rootScope.storeId);
        //     }
        //     //同时加载店铺数据:
        // }

        // init EVENT
        $scope.clickLeftMenu = function (item) {
            var menus = $rootScope.menus;
            if (menus.__selectMenuId == item._id) {
                menus.__openMenu = !menus.__openMenu;
            } else {
                menus.__openMenu = true;
            }
            menus.__selectMenuId = item._id;
        }
        $scope.doMenuAction = function (item, url) {
            goPage('#' + url);
            if ($scope.selectedMenu != null) {
                $scope.selectedMenu.__selected = false;
            }
            item.__selected = true;
            $scope.selectedMenu = item;
        }
        $scope.doLogout = function () {
            if (confirm("你是否确定注销本次登录?")) {
                deleteCookie('___ADMIN_TOKEN');
                $scope.showLogin = true;
                $http.put(window.basePath + '/account/AdminUser/logout').success(function () {
                    goPage('#/account/login');
                });

            }
        }
        $scope.doLogin = function () {
            if ($scope.login.loginName != null && $scope.login.loginName.length > 0) {
                setCookie('loginName', $scope.login.loginName);
            }
            var con = {
                loginName: $scope.login.loginName,
                deviceId: deviceId,
                password: $scope.login.password
            };
            if ($scope.loginFormType == 'reg') {
                con.name = $scope.login.name;
            }
            var url = window.basePath + '/account/AdminUser/login';
            $http.post(url, con).success(function (re) {
                setCookie('clientType', 'admin');
                setCookie('___ADMIN_TOKEN', re.content.token);
                $(document).queue('init', actions);
                $(document).dequeue('init');
                $scope.showLogin = false;
                goPage('#');
            });
        }
    });
})(angular);