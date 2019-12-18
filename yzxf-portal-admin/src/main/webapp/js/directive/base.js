/**
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";
    $(window).click(function (e) {
        $('.menuCon').hide();
    });


    // 表单验证相关
    window.checkTypes = {
        'int': {
            name: 'int',
            desc: '整数',
            reg: /^\-?\d*$/
        },
        'long': {
            name: 'long',
            desc: '整数',
            reg: /^\-?\d*$/
        },
        'float': {
            name: 'float',
            desc: '小数',
            reg: /^\-?\d+((\.|\,)\d+)?$/
        },
        'double': {
            name: 'double',
            desc: '小数',
            reg: /^\-?\d+((\.|\,)\d+)?$/
        },
        'mobile': {
            name: 'mobile',
            desc: '手机号码',
            reg: /^(1[3-9][0-9])[0-9]{8}$/
        },
        'password': {
            name: 'password',
            desc: '密码格式',
            reg: /^[A-Za-z0-9_]{6,20}$/
        },
        'idcard': {
            name: 'idcard',
            desc: '身份证',
            reg: /^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i
        }
    };
    // 消息发送和接收的全局函数
    // 1.将指令的api通过消息的方式暴露出去
    // 2.并且设置一些基础的方法
    window.exportUiApi = function ($scope, funcs) {
        $scope.setData = function (data) {
            console.log('-set data:', data);
            console.log('-set data $scope:', $scope);
            $.each(data, function (k, v) {
                $scope[k] = v;
            })
            // $.extend(true, $scope, data);
            console.log('-set data after:', $scope);

        }
        $scope.getData = function (key) {
            console.log('-get data:', $scope);
            return $scope[key];
        }
        funcs.push('setData');
        funcs.push('getData');
        $.each(funcs, function (k, v) {
            // console.log('--export api:', k, $scope.id, v)
            $scope.$on($scope.id + '/' + v, function (event, obj) {
                console.log(event.name + ' has called');
                var re = ($scope[v] || angular.noop)(obj.content);
                window.respUIAction($scope, $scope.id + '/' + v, {
                    __msgId: obj.__msgId,
                    content: re
                });
            });
        })
    }

    // 用于请求执行一个ui组件的暴露的方法
    window.reqUIAction = function ($scope, path, data, callback) {
        if (path.indexOf('_return') > -1) {
            console.error('请求的path 不能包含 _return');
            return;
        }

        var root = $scope.$root;
        root.callbacks || (root.callbacks = {});

        var msgData = {
            __msgId: genUUID(),
            content: data
        };
        if (callback != null) {
            root.callbacks[msgData.__msgId] = callback;
        }
        // 注册回调
        root.actions || (root.actions = {});// 记录那些action已经添加到$on
        var returnPath = path + '_return';
        if (!root.actions[returnPath]) {
            root.$on(returnPath, function (event, obj) {
                console.log('--callback[' + obj.__msgId + ']:' + root.callbacks[obj.__msgId])
                if (root.callbacks[obj.__msgId]) {
                    root.callbacks[obj.__msgId](obj.content);// 根据每条消息响应访问对应的callback
                    delete root.callbacks[obj.__msgId];
                }
            });
            root.actions[returnPath] = true;
        }
        // 发送请求
        root.$broadcast(path, msgData);
    }
    // 内部方法被执行后的响应值
    window.respUIAction = function ($scope, path, data) {
        if (data.__msgId == null) {
            console.error('响应的的数据必须包含__msgId');
            return;
        }
        if (path.indexOf('_return') > -1) {
            console.error('响应的的path 也不能包含 _return');
            return;
        }
        $scope.$root.$broadcast(path + "_return", data);
    }

    // 配置http的服务
    window.app.config(['$httpProvider', function ($httpProvider) {
        // Initialize get if not there
        if (!$httpProvider.defaults.headers.get) {
            $httpProvider.defaults.headers.get = {};
        }
        $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
        $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';

        $httpProvider.interceptors.push(function ($q) {
            return {
                'request': function (request) {
                    // window.showDataLoading();
                    // console.log('---request start:');
                    if (window.deviceId == null || window.deviceId == '') {
                        // malert('设备未注册！');
                    }
                    // console.log("-------:" + request.isSingle)
                    if (!request.isBackRun) {
                        window.showLoading();
                    }
                    //if (request.data != null) {
                    //    request.data = window._fixSubmitData(request.data);
                    // }
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
                        goPage('#/account/login');
                    } else {
                        var msg = '服务器忙';
                        if (rejection.data != null && rejection.data.content != null && rejection.data.content.errMsg != null) {
                            msg = rejection.data.content.errMsg;
                            if ("checkCode_is_error" == msg) {
                                var now = new Date();
                                $("#checkCode").attr("src", "./checkCode.jsp?code=" + now.getTime());
                            }
                        }

                        if (msg != null) {
                            // 服务器反馈消息
                            malert('服务器: ' + msg);
                        }
                    }
                    return $q.reject(rejection);
                }
            };
        });
    }]);

    // 注册一个全局的popwindow服务
    window.app.factory('$popWindow', function ($rootScope) {
        var alertService = {};
        // 创建一个全局的 alert 数组
        $rootScope.popWindows = [];

        alertService.add = function (name, title, width, height, temp, hideTop, hideDown) {
            width || (width = 500);
            height || (height = 400);
            var obj = {
                'name': name,
                'title': title,
                'width': width,
                'height': height,
                'temp': temp,
                'hideTop': hideTop,
                'hideDown': hideDown,
                'close': function () {
                    alertService.closeWin(this);
                }
            };
            $rootScope.popWindows.push(obj);
            return $rootScope.popWindows.length - 1;
        };

        alertService.closeWin = function (win) {
            alertService.closeWinInx($rootScope.popWindows.indexOf(win));
        };

        alertService.closeWinInx = function (index) {
            $rootScope.popWindows.splice(index, 1);
        };

        return alertService;
    })
    window.reImgSize = function (type, w, h, oldWidth, oldHeight) {
        var left = 0, top = 0, width, height;// 计算后的宽和高，和位移

        if (w == 0) {
            width = oldWidth * h / oldHeight;
        } else if (h == 0) {
            height = oldHeight * w / oldWidth;
        } else {
            height = w / oldWidth * oldHeight;
            if (type == 'max') {// 放大的方式充满整个区域，图片不变形截取多余的图片
                // 这种情况可能要裁图
                if (height > h) {// 截高的情况
                    top = (height - h) / 2 * -1;
                } else if (height < h) {// 截宽的情况
                    width = h / oldHeight * oldWidth;
                    height = h;
                    left = (width - w) / 2 * -1;
                }
            } else if (type == 'min') {// 缩小的方式充满整个区域不截图，只保证充满高或宽的任何一项
                // 这种情况可能要位移
                if (height < h) {// 移到中间
                    console.log(w, h, oldWidth, oldHeight, '=', height)

                    top = (h - height) / 2;
                } else if (height > h) {// 移动宽的情况
                    width = h / oldHeight * oldWidth;
                    height = h;
                    left = (w - width) / 2;
                }
            }

        }

        return {
            left: left,
            top: top,
            width: width,
            height: height
        };
    }
    /** 基础组件 **/
        // 完善img的功能
    window.app.directive('img', function () {
        return {
            restrict: 'AE',
            scope: {
                baseSrc: '@',
                baseType: '@',
                baseWidth: '@',
                baseHeight: '@'
            },
            controller: function ($rootScope, $scope, $element, $http, $templateCache, $compile) {
                if ($scope.baseType == 'fix') {
                    var parent = $element.parent().css({
                        'position': 'relative',
                        'overflow': 'hidden'
                    });
                    $element.css('position', 'absolute');
                    var w = $scope.baseWidth == null ? parent.width() : parseInt($scope.baseWidth);
                    var h = $scope.baseHeight == null ? parent.height() : parseInt($scope.baseHeight);
                    console.log(w, h);
                    if (w == 0 && h == 0) {
                        console.error('img width and height error', width, height);
                    }
                    $element.attr('src', '/img/hui_logo.png');
                    window.imgReady($scope.baseSrc, function (oldWidth, oldHeight) {
                        var pos = window.reImgSize('min', w, h, oldWidth, oldHeight);
                        $element.css(pos).hide().attr('src', $scope.baseSrc).fadeIn('fast');
                    });
                } else {
                    if ($scope.baseSrc != null && $scope.baseSrc != '') {
                        window.imgReady($scope.baseSrc, function (w, h) {
                            $element.attr('src', $scope.baseSrc);
                        })
                    }
                }
            }
        }
    });
    // 完善input的功能
    window.app.directive('input', function () {
        return {
            restrict: 'AE',
            scope: {
                menuClick: '&',
                filter: '=',
                entity: '@',
                model: '@'
            },
            controller: function ($rootScope, $scope, $element, $http, $templateCache, $compile) {

                if ($element.attr('auto-complate') == 'true') {
                    $scope.menuItemClick = function ($event, item) {
                        $scope.filter.keywordValue = item.name;
                        $scope.menuClick();
                        $('#menuList').hide();
                        $event.stopPropagation();
                    }
                    var getMenuList = function (e) {
                        if (window.queryData++ > 0 || e.keyCode == 13) {
                            return;
                        }
                        var eObj = $(e.target);
                        var mObj = $('#menuList');
                        if (mObj.size() == 0) {
                            mObj = $('<div id="menuList" class="menuCon" ng-click=""></div>');
                            $('body').append(mObj);
                        }

                        mObj.css({
                            top: eObj.offset().top + eObj.height() + 12,
                            left: eObj.offset().left,
                            zIndex: zIndex++
                        }).show();
                        var model = $element.attr('model');
                        var entity = $element.attr('entity');
                        var keywords = $element.attr('keywords');
                        keywords || (keywords = 'name');

                        var value = $(e.target).val();
                        if (value == null || value == '') {
                            $scope.menuList = {
                                items: []
                            };
                            e.stopPropagation();
                            return;
                        }
                        var data = {
                            keywordValue: value,
                            keywords: keywords,
                            pageSize: 10
                        }
                        // 查询队列
                        $http({
                            url: window.basePath + '/' + model + '/' + entity + '/query' + mapToQueryString(data),
                            isBackRun: true
                        }).success(function (re) {
                            // console.log('---' + re.content);
                            mObj.empty();
                            if (re.content.items.length > 0) {
                                $scope.menuList = re.content;
                                var tmp = '/temp/menuItem.html?v=' + window.angular_temp_version;
                                var temp = $templateCache.get(tmp);
                                if (temp == null) {
                                    $http.get(tmp).success(function (re) {
                                        $templateCache.put(tmp, re);
                                        mObj.append(temp);
                                        var link = $compile(mObj.contents());
                                        link($scope);
                                    })
                                } else {
                                    mObj.append(temp);
                                    var link = $compile(mObj.contents());
                                    link($scope);
                                }

                                // $compile.
                                // alert($templateCache.get('menuItem.html'));

                            }
                        });
                        e.stopPropagation();
                    }
                    $element.on('keyup', getMenuList);
                    // .on('mousedown', getMenuList);
                }

            },
            compile: function (element, attrs, transclude) {
                return function ($scope, $element, attrs, ctrl, transclude) {
                    if ($element.attr('auto-focus') == 'true') {
                        console.log('focus:' + $element.attr('auto-focus'))
                        $element.focus().select();
                    }
                }
            }
        }
    });

})(angular);