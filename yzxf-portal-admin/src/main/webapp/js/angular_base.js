(function (angular, undefined) {
    "use strict";
    window.baseApiPath = '/services/api';
    var apiVersion = 1;
    var jsVersion = 0.002;
    setCookie('apiVersion', apiVersion);

    $(window).resize(function () {
        if ($('#popWin').is(':visible')) {
            $('#popWin').showCenter();
        }
    });
    window.goPage = function (url) {
        window.location.href = url;
    }
    // 保存到数据库时删除多余的显示字段
    window._deleteField = function (value) {
        if (value == null) {
            return value;
        }
        for (var k in value) {
            if (k.startsWith && k.startsWith("__")) {
                delete value[k];
            } else {
                var v = value[k]
                if ($.isArray(v) || $.isPlainObject(v)) {
                    value[k] = _deleteField(v);
                }
            }
        }
        return value;
    }
    window._fixSubmitData = function (data) {
        var value = {};
        $.extend(true, value, data);
        value = _deleteField(value);
        // alert(JSON.stringify(value));
        return value;
    }
    window.closePopWin = function () {
        $('#mark').hide();
        $('#popWin').hide();
    }

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
                    var dependencies = ['/view_js/' + $route.current.params.modelName + '/' + $route.current.params.actionName + '.js?v=' + jsVersion];
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
    window.app.config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push(function ($q) {
            return {
                'request': function (request) {
                    // window.showDataLoading();
                    console.log('---request start:');
                    console.log(request);
                    if (window.deviceId == null || window.deviceId == '') {
                        malert('设备未注册！');
                    }
                    $('#markLoading').css('zIndex', ++zIndex).show();
                    var loadDiv = $('#dataLoading').show();
                    $('#dataLoading').css({
                        'zIndex': ++zIndex,
                        top: ($(window).height() - loadDiv.height()) / 2,
                        left: ($(window).width() - loadDiv.width()) / 2
                    });
                    if (request.data != null) {
                        request.data = window._fixSubmitData(request.data);
                    }
                    return request;
                },
                'response': function (response) {
                    $('#markLoading').hide();
                    $('#dataLoading').fadeOut(400);
                    return response;
                },
                'responseError': function (rejection) {
                    $('#markLoading').hide();
                    $('#dataLoading').fadeOut(400);

                    if (rejection.status === 401) {
                        goPage('#/login');
                    } else {
                        var msg = '服务器忙';
                        if (rejection.data != null && rejection.data.errMsg != null) {
                            msg = rejection.data.errMsg;
                            if ("checkCode_is_error" == msg) {
                                var now = new Date();
                                $("#checkCode").attr("src", "./checkCode.jsp?code=" + now.getTime());
                            }
                        }

                        if (msg != null) {
                            malert('s:' + msg);
                        }
                    }
                    return $q.reject(rejection);
                }
            };
        });
    }]);
    window.actions || (window.actions = {});
    window.app.run(function ($q, $rootScope, $location, $http, $compile) {
        var locationChangeStartOff = $rootScope.$on('$locationChangeStart', locationChangeStart);
        // var locationChangeSuccessOff = $rootScope.$on('$locationChangeSuccess', locationChangeSuccess);

        var routeChangeStartOff = $rootScope.$on('$routeChangeStart', routeChangeStart);
        // var routeChangeSuccessOff = $rootScope.$on('$routeChangeSuccess', routeChangeSuccess);

        function locationChangeStart(event, $scope) {
            console.log('locationChangeStart');
            $scope.text = 'joey';
            console.log($scope);
            // alert('数据加载中....')
        }

        function doLoadPage($q, $rootScope, $location) {
            var deferred = $q.defer();

            var elName = $location.path().substring(1) + 'Content';
            $http.get('/base/page' + $location.path() + '.jsp').success(function (re) {
                var el = angular.element(re).attr('id', elName);
                var elObj = angular.element('#allContent');
                elObj.empty().append(el);
                $script([$location.path() + '.js?v='], 'abc');
                $script.ready('abc', function (re) {
                    alert('s');
                    // $rootScope.$apply();
                    var link = $compile(elObj.contents());
                    link($rootScope);
                });
            }).error(function () {
                var elObj = angular.element('#allContent');
                var el = angular.element('<div>服务器忙，请稍候再试！</div>');
                elObj.empty().append(el);
            });
            return deferred.promise;
        }

        function locationChangeSuccess(event) {
            console.log('locationChangeSuccess');
            console.log(arguments);

            if ($location.path() == '/pop') {
                $('#mark').css({
                    zIndex: ++zIndex
                }).show();
                $('#popWin').css({
                    zIndex: ++zIndex
                }).show();
                return;
            } else {
                $('#mark,#popWin').hide();
            }
            // 先加载js
            doLoadPage($q, $rootScope, $location);
        }

        function routeChangeStart(event) {
            console.log('routeChangeStart');
            console.log(arguments);
        }

        function routeChangeSuccess(event) {
            console.log('routeChangeSuccess');
            console.log(arguments);
        }

        // Configures $urlRouter's listener *after* your custom listener
    });

})(angular);
