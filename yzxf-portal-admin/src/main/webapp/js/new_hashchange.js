(function() {
    var site = {};
    site.name = "友来社区";
    site.desc = "友来社区，打造私密的网上家园";
    site.pages = [];
    site.loadingIcon = '/front_img/logo.jpg';
    site.loadingTitle = '友来社区，打造私密的网上家园';
    site.colorMap = {
        normal : '#444',// 默认的前景色，如字体的颜色
        highlight : '#f54f00',// 高亮的颜色，提示，警告，尤其突出显示等
        bule : '#2d64b3',// 一般突出显示的颜色
        notHigh : '#888',// 不重视的颜色，不重要的颜色
        borderColor : '#ddd',// 一般的边框的颜色
        normal_back : '#eee',// 默认的背景色
        block_back : '#fff',// 二级菜单或块状列表的背景色
        loading_back : '#fff',// 启动的过度层的背景
        menu_back : '#555',// 一级菜的背景色
        menu_front : '#fff',// 一级菜单的前景色
        title_back : '#333',// 标题的背景色
        title_front : '#fff',// 按钮的前景色
        button_front : '#fff'// 按钮的前景色
    }
    window.site = site;

    window.zindex = 10;
    window.showPage = function(con, params) {
        if (Object.prototype.toString.call(con).indexOf('String') > -1) {
            con = $('#' + con);
        }
        $('#mark').hide();
        if (window.popWindow != null) {
            window.popWindow.hide();
        }
        if (params.isPop) {
            $('#mark').css('zIndex', ++zindex).show();
            window.popWindow = con;
        } else {
            if (window.currentPageCon != null) {
                window.currentPageCon.hide();
            }
        }
        window.currentPageCon = con;
        con.css('zIndex', ++zindex).show();
        if ('popWin' == con.attr('id')) {
            $('#popWin_close').show();
        }
    }
    // 单页面通过书签跳转的实现
    $(window).hashchange(function() {
        var hash = location.hash;
        // Set the page title based on the hash.
        // Iterate over all nav links, setting the "selected" class
        // as-appropriate.
        // alert(hash);

        // if (window.history && window.history.length < 2) {// 隐藏箭头
        // $('#mobileBack').hide();
        // } else {
        // $('#mobileBack').show();
        // }
        hash = hash == null ? '' : hash;
        if (hash == '') {
            window.location.href = '#main';
            return;
        }
        if (hash.indexOf('?') > -1) {
            hash = hash.split('?')[0];
        }
        var method = hash;
        var params = {};
        if (hash.indexOf('/') > 0) {
            var paths = hash.split('/');
            method = paths[0];
            if (paths.length > 2) {
                for (var i = 1; i < (paths.length - 1); i++) {
                    params[paths[i]] = paths[++i];
                }
            }
        }

        // if (window.isMobile()) {
        // $(method).parent().scrollTop(0);
        // }

        // popwin处理
        if (method == '#popWin') {
            showPage('popWin');
            return;
        }

        window._oldMethod = method;
        window.titleMap || (window.titleMap = {});
        var obj = window.titleMap[method];
        if (obj == null) {
            console.log('titleMap[' + method + '] is null');
            return;
        }
        // document.title = obj.name;
        if (Object.prototype.toString.call(obj).indexOf('String') > -1) {
            window.location.href = obj;
            return;
        }

        obj.path = method;
        obj.hash = hash;

        window.pageExeActions = [];
        // 前置拦截
        if (window.titleMap._beforeAction) {
            window.pageExeActions.push(function(next) {
                obj.next = next;
                var reNext = window.titleMap._beforeAction.call(obj, params);
                if (reNext == null) {
                    next();
                }
            });
        }
        window.pageExeActions.push(function(next) {
            // $('.page').hide();
            window.showPage($(method + '_content'), params);
            next();
        });
        if (obj.action) {
            window.pageExeActions.push(function(next) {
                obj.next = next;
                var reNext = obj.action.call(obj, params);
                if (reNext == null) {
                    next();
                }
            });
        }
        // 拦截
        if (window.titleMap._afterAction) {
            window.pageExeActions.push(function(next) {
                obj.next = next;
                var reNext = window.titleMap._afterAction.call(obj, params);
                if (reNext == null) {
                    next();
                }
            });
        }

        $(document).queue('init', window.pageExeActions);
        $(document).dequeue('init');
    });
})();