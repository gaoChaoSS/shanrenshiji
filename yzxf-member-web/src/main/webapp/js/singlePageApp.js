(function () {
    window.initWindowConf = function () {
        window.singlePageAppVersion = '0.1';
        window.viewPath = '/' + window.appName + '_page/view';
        window.viewJsPath = '/' + window.appName + '_page/js';

        window.setWindowTitle = function ($rootScope, title) {
            $rootScope.windowTitle = title;
            document.title = title;
            if ($rootScope.currentUrl != null && $rootScope.pageContextMap[$rootScope.currentUrl] != null) {
                $rootScope.pageContextMap[$rootScope.currentUrl].title = title;
            }
            $rootScope.savePageList();
        }

        //页面处理数据时排它操作
        window.showLoading = function () {
            var markLoading = $('#markLoading');
            if (markLoading.size() == 0) {
                markLoading = $('<div id="markLoading"></div>');
                markLoading.mousedown(function () {
                    malert('处理中,请稍后!');
                    return false;
                });
                $('body').append(markLoading);
            }
            markLoading.css('zIndex', ++zIndex).show();

            var dataLoading = $('#dataLoading');
            if (dataLoading.size() == 0) {
                dataLoading = $('<div id="dataLoading">加载中...</div>');
                dataLoading.mousedown(function () {
                    malert('处理中,请稍后!');
                    return false;
                });
                $('body').append(dataLoading);
            }
            dataLoading.showCenter(150, 50).css('zIndex', ++zIndex).show();
        }

        window.hideLoading = function () {
            $('#markLoading').hide();
            $('#dataLoading').fadeOut(300);
        }

        //开始Angular的配置
        window.app = angular.module('phonecat', []);
        window.app.config(function ($controllerProvider, $compileProvider, $filterProvider, $provide, $locationProvider) {
            app.register = {
                controller: $controllerProvider.register,
                directive: $compileProvider.directive,
                filter: $filterProvider.register,
                factory: $provide.factory,
                service: $provide.service
            };
            $locationProvider.html5Mode(true);
        });
        // 配置http的服务
        window.app.config(['$httpProvider', function ($httpProvider) {
            // Initialize get if not there
            if (!$httpProvider.defaults.headers.get) {
                $httpProvider.defaults.headers.get = {};
            }
            $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
            $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';

            $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
                return {
                    'request': function (request) {
                        // window.showDataLoading();
                        // console.log('---request start:');
                        if (window.deviceId == null || window.deviceId == '') {
                            // malert('设备未注册！');
                        }
                        // console.log("-------:" + request.isSingle)
                        if (!request.hideLoading) {
                            window.showLoading();
                        }
                        if (request.data != null) {
                            //request.data = window._fixSubmitData(request.data);
                        }
                        return request;
                    },
                    'response': function (response) {
                        window.hideLoading();
                        return response;
                    },
                    'responseError': function (rejection) {
                        window.hideLoading();

                        if (rejection.status === 401) {
                            // 记录当前的url,便于恢复后,重新刷新
                            // setCookie('___login_after_url', window.location.href);
                            // $rootScope.goPage('/account/login');
                            $rootScope.showLogin();
                        } else {
                            var msg = '服务器忙';
                            if (rejection.data != null && rejection.data.content != null && rejection.data.content.errMsg != null) {
                                msg = rejection.data.content.errMsg;
                                if ("checkCode_is_error" == msg) {
                                    var now = new Date();
                                    $("#checkCode").attr("src", "./checkCode.jsp?code=" + now.getTime());
                                }
                            }

                            if (msg != null && msg != 'org.eclipse.jetty.io.EofException') {
                                // 服务器反馈消息
                                malert(msg);
                            }
                        }
                        return $q.reject(rejection);
                    }
                };
            });
        }]);

        //滚动分页需要做的工作
        window.app.directive('onFinishRenderFilters', function ($timeout) {
            return {
                restrict: 'A',
                link: function ($scope, $element, attr) {
                    if ($scope.$last === true) {
                        //$timeout(function () {
                        //$rootScope.scrollConTop = obj.offset().top + obj.height();
                        var obj = $($element).parent();
                        $scope.$emit('ngRepeatFinished', obj);
                        //});
                    }
                }
            };
        });
        window.scrollAddDataToList = function ($rootScope, $scope, pageObj, pname, addMore) {
            //$scope.dataPage || ($scope.dataPage = {});
            $scope[pname] || ($scope[pname] = {});
            $scope[pname].items || ($scope[pname].items = []);
            if (addMore) {
                $scope[pname].items.addAll(pageObj.items);
                $scope[pname].pageNo = pageObj.pageNo;
                $scope[pname].totalNum = pageObj.totalNum;
                $scope[pname].totalPage = pageObj.totalPage;
            } else {
                $scope[pname] = pageObj;
            }
            //$scope.filter.pageNo = pageObj.pageNo;
            //滚动分页
            $rootScope.isScrollLoadData = pageObj.pageNo < pageObj.totalPage;
            $rootScope.loadMoreDataAction = $scope.loadMore;
        }
        //pc 分页
        window.putPCPageObj = function ($scope, dataPage, queryCurrentPage) {
            $scope.queryCurrentPage = queryCurrentPage;
            dataPage.$$pageList = [];
            var start = dataPage.pageNo - 5;
            var end = dataPage.pageNo + 5;
            start = start < 1 ? 1 : start;
            end = end > dataPage.totalPage ? dataPage.totalPage : end;
            if (end > 1) {
                for (var i = start; i <= end; i++) {
                    dataPage.$$pageList.push(i)
                }
            }
        }
    }

    //初始化rootScope全局函数
    window.initAngularRootScope = function ($rootScope, $scope, $http, $location) {
        //判断是否在微信中测试
        $rootScope.isWechat = window.isWechat();

        //判断是否在pc端, 注意尽量在需要在用的时候即时调用
        $rootScope.isPc = function () {
            return $('#isPc:visible').size() > 0;
        }

        var token = getCookie(window.tokenName);
        $rootScope.notLogin = token == null || token.length < 5;

        //是否选中了底部菜单
        $rootScope.hasSelectedBottom = function (item) {
            return $rootScope.currentUrl != null && $rootScope.currentUrl.startsWith(item.url);
        }

        //******* 滚动分页相关 *******
        /**
         * 滚动分页
         */
        $rootScope.isScrollLoadData = false;
        $('#toTop').click(function () {
            $(window).scrollTop(0);
        });
        $(window).on("resize", function () {
            (function () {
                $rootScope.$apply(function () {
                    $rootScope.isPc();
                });
            }).delay(0.8);
        }).on('scroll', function (e) {
            if ($(window).scrollTop() > 0) {
                $('#toTop').fadeIn();
            } else {
                $('#toTop').fadeOut();
            }
            if (!$rootScope.isScrollLoadData || !$rootScope.isScrollPage) {
                return;
            }
            var move = $(window).scrollTop() + $(window).height();
            var top = 0, height = 0;
            if ($rootScope.scrollConObj) {
                top = $rootScope.scrollConObj.offset().top;
                height = $rootScope.scrollConObj.outerHeight();
            }

            if ($rootScope.scrollConObj != null && $rootScope.loadMoreDataAction != null) {
                if ((parseInt(top) + parseInt(height)) < move) {
                    $rootScope.isScrollLoadData = false;
                    (function () {
                        $rootScope.$apply(function () {
                            console.log($rootScope.isScrollLoadData, (parseInt(top) + parseInt(height)) - move);
                            $rootScope.loadMoreDataAction();
                            $rootScope.isScrollLoadData = true;
                        });
                    }).delay(0.5);
                }
            }
        });
        $rootScope.$on('ngRepeatFinished', function (event, obj) {
            console.log('list pos:', obj, obj.offset().top, obj.outerHeight());
            $rootScope.scrollConObj = obj;
        });


        //********* 内部page相关 *********
        //关闭一个页面
        $rootScope.closePage = function ($index) {
            var isCurrent = $rootScope.currentUrl == $rootScope.pageList[$index];
            $rootScope.finishPage($index);
            var size = $rootScope.pageList.length;
            if (size > 0 && isCurrent) {
                var cindex = $index - 1;
                cindex = cindex < 0 ? 0 : cindex;
                var url = $rootScope.pageList[cindex];
                if (url != null) {
                    $rootScope.goPage(url);
                }
            }
        }
        //关闭所有页面
        $rootScope.closeAllPage = function () {
            $rootScope.pageList.length = 0;
            $rootScope.pageContextMap = {};
        }
        //用于页面显示当前该显示那个页面
        $rootScope.isShowPage = function ($index) {
            // var lastIndex = ($rootScope.pageList.length - 1);
            var isShow = $rootScope.pageList[$index] == $rootScope.currentUrl;
            //var isPopParent = $rootScope.pageList[lastIndex].startsWith("/pop/") && $index == lastIndex - 1;//弹出窗口的父亲页面
            var isPopParent = $rootScope.pageList[$index] == $rootScope.popParentUrl;
            return isShow || isPopParent;
        }
        //获取当前页面的样式,主要使用在pop页面和正常页面
        $rootScope.pageConClass = function ($index) {
            var s = $rootScope.pageList[$index].startsWith('/pop/') ? 'popCon' : '';
            s = $rootScope.pageList[$index].startsWith('/home/index') ? 'index' : s;
            if (window.isWechat()) {
                s += ' wechatCon'
            }
            if(!$rootScope.showTop){
                s+=' hideTop'
            }
            return s;
        }

        //页面回退
        window.goBack = function () {
            window.history.back();
        }
        $rootScope.goBack = window.goBack;
        //隐藏或显示每个页面的进度条
        $rootScope.showPageLoading = function (isShow) {
            $scope.loadingPage = isShow;
            if (isShow) {
                $('#pageLoadingCon').show();
            } else {
                (function () {
                    $('#pageLoadingCon').fadeOut('fast');
                }).delay(1);

            }
        }
        //产生一个页面的路径
        $rootScope.genPath = function (p) {
            var s = {};
            $.extend(true, s, $rootScope.pathParams, p);
            var ps = '';
            $.each(s, function (k, v) {
                ps += '/' + k + '/' + v;
            })
            return $rootScope.currentPageUrl + ps;
        }
        //注销登录
        $rootScope.doLogout = function () {
            if (confirm("你是否确定注销本次登录?")) {
                deleteCookie(window.tokenName);
                $rootScope.closeAllPage();
                $rootScope.showLogin();
            }
        }
        //登录失效后显示登录页面
        $rootScope.showLogin = function () {
            $rootScope.notLogin = true;
            if ($rootScope.loginTemp == null) {
                var dependencies = [];
                dependencies.push(window.viewJsPath + '/account/login.js?v=' + window.angular_temp_version);
                $script(dependencies, function () {
                    $rootScope.$apply(function () {
                        $rootScope.loginTemp = window.viewPath + '/account/login.jsp?v=' + window.angular_temp_version;
                    });
                });
            }
        }

        //*********定义整个组件的堆栈及页面操作
        $rootScope.pageList = [];//页面的完整url
        $rootScope.pathMap = {};//访问路径对应页面Path
        $rootScope.pagePathMap = {};//页面的jsp路徑
        $rootScope.pageActionMap = {};//页面各个阶段执行的函数,onCreate:每次被创建执行的函数,onResume:每次被显示时执行的函数,onDrop:每次被删除执行的函数
        $rootScope.pageContextMap = {};//页面的实例及绑定的数据

        //持久当前的页面列表
        $rootScope.savePageList = function () {
            var li = [], titleMap = {};
            $.each($rootScope.pageList, function (k, v) {
                if (!v.startsWith('/pop/')) {
                    li.push(v);
                    if ($rootScope.pageContextMap[v] != null) {
                        titleMap[v] = $rootScope.pageContextMap[v].title;
                    }
                }
            });
            console.log('save page list: ', li.join('\n'));
            window.localStorage.setItem("historyUrls", li.join(","));
            window.localStorage.setItem("historyUrls_title", JSON.stringify(titleMap));
        }
        //结束某个页面
        $rootScope.finishPage = function ($index) {
            var url = $rootScope.pageList[$index];
            if ($rootScope.pageContextMap[url] != null) {
                delete $rootScope.pageContextMap[url];
            }
            var path = $rootScope.pathMap[url];
            if ($rootScope.pageActionMap[url] != null) {
                delete $rootScope.pageActionMap[url]
            }
            delete  $rootScope.pathMap[url];
            $rootScope.pageList.splice($index, 1);
            $rootScope.savePageList();
        }
        //结束当前显示的页面
        $rootScope.finishCurrentPage = function (newUrl) {
            $.each($rootScope.pageList, function (k, v) {
                if (v == $rootScope.currentUrl) {
                    $rootScope.finishPage(k);
                    return false;
                }
            });
            if (newUrl == null && $rootScope.pageList.length > 0) {
                newUrl = $rootScope.pageList[$rootScope.pageList.length - 1];
            }
            if (newUrl != null) {
                $rootScope.goPage(newUrl);
            }
        }
        //结束最后一个页面,一般只在手机模式下使用
        $rootScope.finishLastPage = function () {
            if ($rootScope.pageList.length > 0) {
                $rootScope.finishPage($rootScope.pageList.length - 1);
            }
        }


        //主菜单被点击后的处理
        $rootScope.mainMenuClick = {};
        $rootScope.$on('mainMenuList/clickItemAction', function (e, item) {
            var pUrl = null;
            if ($rootScope.pageList.length > 1) {
                pUrl = $rootScope.pageList[$rootScope.pageList.length - 2]
            }
            if (pUrl != null && $rootScope.mainMenuClick[pUrl] != null) {
                $rootScope.mainMenuClick[pUrl](e, item);
            }
        });


        // path发生改变的事件
        //--------------私有方法定义
        var addPage = function (gPath) {
            if ($rootScope.pageContextMap[gPath] == null) {
                $rootScope.pageContextMap[gPath] = {};
                $rootScope.pageList.push(gPath);
                $rootScope.savePageList();
            }
        }

        //从本地存储中读取访问历史
        var readPageList = function () {
            var urls = window.localStorage.getItem("historyUrls");
            var titles = window.localStorage.getItem("historyUrls_title");
            urls = urls == null ? "" : urls;
            titles = titles == null ? "" : titles;
            var titleMap = {}
            if (titles != "") {
                titleMap = JSON.parse(titles);
            }
            console.log("urls:", urls);
            $.each(urls.split(","), function (k, v) {
                if (v != null && v.length > 1) {
                    addPage(v);
                    $rootScope.pageContextMap[v].title = titleMap[v];
                }
            });
            $rootScope.savePageList();
        }
        readPageList();

        $rootScope.$on('$locationChangeStart', function (e, path) {
            console.log('$locationChangeStart=', path);
            //大部分情况不使用页面进度条.
            //$rootScope.showPageLoading(false);

            var gPath = $location.path();
            //预处理URL
            var vList = gPath.split('/');
            var modelName = null, actionName = null, paramName = null, params = {};
            modelName = vList[1];
            actionName = vList[2];
            $.each(vList, function (k, v) {
                if (k > 2) {
                    if (paramName == null) {
                        paramName = v;
                    } else {
                        params[paramName] = v;
                        paramName = null;
                    }
                }
            });

            //if (gPath.indexOf('/storeNo/') == -1) {
            //    gPath += '/storeNo/' + getCookie('storeNo');
            //}

            //1.如果当前是主页并且在移动模式,就清除所有的历史
            //if ($rootScope.isIndex && !$rootScope.isPc()) {
            //    $rootScope.closeAllPage();
            // }

            //2.如果是弹出层,类似alert的gPath对象,就执行自己的逻辑
            $rootScope.isPop = false;

            if (gPath.startsWith('/pop/')) {
                $rootScope.pageList.push(gPath);
                $rootScope.isPop = true;
                if ($rootScope.currentUrl != null) {
                    $rootScope.popParentUrl = $rootScope.currentUrl;
                }
                $rootScope.currentUrl = gPath;
            } else {
                if ($rootScope.popParentUrl != null) {
                    $rootScope.popParentUrl = null;
                }
                $rootScope.currentUrl = gPath;
                //var size = $rootScope.pageList.length;
                //如果最后一个页面是pop,则结束弹出窗口
                if ($rootScope.pageList.length > 0 && ($rootScope.pageList[$rootScope.pageList.length - 1]).startsWith('/pop')) {
                    $rootScope.finishLastPage();
                }
                if ($rootScope.isPc()) {//pc

                } else {//mobile
                    if ($rootScope.pageList.length > 1 && $rootScope.pageList[$rootScope.pageList.length - 2] == gPath) {//移动端back的情况
                        $rootScope.finishLastPage();
                        if ($rootScope.returnData != null) {
                            //返回的数据
                            $rootScope.pageContextMap[gPath].returnData = $rootScope.returnData;
                        }
                    }
                    //如果以前已经访问了该页面就删除
                    $.each($rootScope.pageList, function (k, v) {
                        if (v == gPath) {
                            $rootScope.pageList.splice(k, 1);
                            $rootScope.pageContextMap[v] = null;
                            $rootScope.savePageList();
                            return false;
                        }
                    })
                }
                addPage(gPath);
            }


            var genTemplateAndJs = function (curl, isSet) {
                var pagePath = '/' + modelName + '/' + actionName;

                if (isSet) {
                    $rootScope.pathParams = params;
                    $rootScope.currentPageUrl = pagePath;
                }
                $rootScope.pathMap[curl] = pagePath;
                //如果未登录
                //if ($rootScope.notLogin && !$rootScope.notLoginPage[pagePath]) {
                //    $rootScope.pagePathMap[curl] == null;
                //    setCookie('___login_after_url', curl);
                //    $rootScope.showLogin();
                //    return;
                //}

                if ($rootScope.pagePathMap[curl] == null) {
                    // 加载js和jsp
                    var dependencies = [];
                    //先加载页面额外需要的共有js
                    //判断是否需要跳转到登录页面

                    if ($rootScope.pageCommonJs[pagePath] != null) {
                        $.each($rootScope.pageCommonJs[pagePath], function (k, v) {
                            dependencies.push(v + '?v=' + window.angular_temp_version);
                        });
                    }
                    dependencies.push(window.viewJsPath + pagePath + '.js?v=' + window.angular_temp_version);
                    $script(dependencies, function () {
                        $rootScope.$apply(function () {
                            $rootScope.pagePathMap[curl] = window.viewPath + pagePath + ".jsp?v=" + window.angular_temp_version;
                        });
                    });
                }
            }

            //var currentUrl = $rootScope.pageList[$rootScope.pageList.length - 1];
            genTemplateAndJs(gPath, true);

            if ($rootScope.isPop) {//如果是pop,则需要将倒数的二个页面也要加载,因为pop是半透明的
                var parentUrl = $rootScope.pageList.length > 1 ? $rootScope.pageList[$rootScope.pageList.length - 2] : null;
                if (parentUrl != null) {
                    genTemplateAndJs(parentUrl, false);
                }
            }

            //每到一个页面的前置处理
            if ($rootScope.pageLoadBefore != null) {
                $rootScope.pageLoadBefore();
            }

            //重置一些基本设定
            $rootScope.goBack = window.goBack;
            $rootScope.windowTitleHide = false;
            $rootScope.isIndex = false;
            $rootScope.isScrollPage = false;
            $rootScope.notLogin = false;

            window.uploadWinObj = null;

            //去到某个页面后又重新回来的情況, 执行该方法
            $rootScope.pageActionMap[$rootScope.currentPageUrl] || ($rootScope.pageActionMap[$rootScope.currentPageUrl] = {});
            if (!$rootScope.notReload && $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume != null) {
                $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
            }
            $rootScope.notReload = false;
        });
        $rootScope.$on('$locationChangeSuccess', function (e, path) {
            // console.log('$locationChangeSuccess=', path)
        });
    }

})();