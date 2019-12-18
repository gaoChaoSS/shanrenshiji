(function () {
    if (!window.console) {
        window.console = {
            log: function (msg) {
                // TODO about console
            },
            error: function (msg) {
                alert('errpr:' + msg);
            }
        }
    }
    // 保存临时数据
    window.saveTempData = function () {
        //
    }
    // 暂时解决$.browser被移除的问题；
    $.browser = {};
    $.browser.mozilla = /firefox/.test(navigator.userAgent.toLowerCase());
    $.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
    $.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
    $.browser.msie = /msie/.test(navigator.userAgent.toLowerCase());

    window.isMobile = function () {
        if (window._isMoble == null) {
            var userAgent = navigator.userAgent.toLowerCase();
            window._isMoble = ((userAgent.indexOf("mobile") > -1)//
                || (userAgent.indexOf("iphone") > -1)//
                || (userAgent.indexOf("ipad") > -1)//
                || (userAgent.indexOf("android") > -1)//
                || (userAgent.indexOf("micromessenger") > -1)//
            );
        }
        if ($('#topMCon').is(':visible')) {
            window._isMoble = true;
        }

        return window._isMoble;
    }
    window.isWechat = function () {
        if (window._isWechat == null) {
            var userAgent = navigator.userAgent.toLowerCase();
            window._isWechat = userAgent.indexOf("micromessenger") > -1;
        }
        return window._isWechat;
    }

    // 随机打乱一个数组，用于洗牌算法
    var _randomArray = function (size) {
        var arr = [];
        for (var i = 0; i < size; i++) {
            arr[i] = i;
        }
        arr.sort(function () {
            return 0.5 - Math.random()
        })
        // var str = arr.join();
        // alert(str);
        return arr;
    }

    var _S4 = function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    // alert(new Number(2500000).toString(8));
    // alert(new Number(2500000).toString(10));
    // alert(new Number(2500000).toString(16));
    // alert(new Number(2500000).toString(32));
    var _genShortId = function () {
        return (((1 + Math.random()) * 0x100000000) | 0).toString(32).substring(1);
    }
    // alert(_genShortId());
    // var _genUUID = function() {
    // return (_S4() + _S4() + "-" + _S4() + "-" + _S4() + "-" + _S4() + "-" +
    // _S4() + _S4() + _S4());
    // }

    /*
     * \ Returns RFC4122, version 4 ID \
     */
    var _genUUID = (function (uuidRegEx, uuidReplacer) {
        return function () {
            return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(uuidRegEx, uuidReplacer).toUpperCase();
        };
    })(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == "x" ? r : (r & 3 | 8);
        return v.toString(16);
    });

    window.PositionHelp = {
        getObjPos: function (obj) {
            var x = y = 0;
            if (obj.getBoundingClientRect) {
                var box = obj.getBoundingClientRect();
                var D = document.documentElement;
                x = box.left + Math.max(D.scrollLeft, document.body.scrollLeft) - D.clientLeft;
                y = box.top + Math.max(D.scrollTop, document.body.scrollTop) - D.clientTop;
            } else {
                for (; obj != document.body; x += obj.offsetLeft, y += obj.offsetTop, obj = obj.offsetParent)
                    ;
            }
            return {
                'x': x,
                'y': y
            };
        },
        getCurPos: function (e) {
            e = e || window.event;
            var D = document.documentElement;
            if (e.pageX)
                return {
                    x: e.pageX,
                    y: e.pageY
                };
            return {
                x: e.clientX + D.scrollLeft - D.clientLeft,
                y: e.clientY + D.scrollTop - D.clientTop
            };
        }
    };
    var _isEmpty = function (str) {
        return str == null || str == '';
    };

    var _imgReady = function (url, callback, error) {
        var width, height, intervalId, check, div, img = new Image(), body = document.body;
        img.src = url;
        // 从缓存中读取
        if (img.complete) {
            return callback(img.width, img.height);
        }
        // 通过占位提前获取图片头部数据
        if (body) {
            div = document.createElement('div');
            div.style.cssText = 'visibility:hidden;position:fixed;left:0;top:0;width:1px;height:1px;overflow:hidden';
            div.appendChild(img)
            body.appendChild(div);
            width = img.offsetWidth;
            height = img.offsetHeight;
            check = function () {
                if (img.offsetWidth !== width || img.offsetHeight !== height) {
                    clearInterval(intervalId);
                    callback(img.offsetWidth, img.clientHeight);
                    img.onload = null;
                    div.innerHTML = '';
                    div.parentNode.removeChild(div);
                }
            };
            intervalId = setInterval(check, 150);
        }
        // 加载完毕后方式获取
        img.onload = function () {
            callback(img.width, img.height);
            img.onload = img.onerror = null;
            if (intervalId) {
                clearInterval(intervalId);
            }
            if (div != null && div.parentNode != null) {
                div.parentNode.removeChild(div);
            }
        };
        // 图片加载错误
        img.onerror = function () {
            error && error();
            if (intervalId) {
                clearInterval(intervalId);
            }
            if (div != null && div.parentNode != null) {
                div.parentNode.removeChild(div);
            }
        };
    };
    // 图片异步加载的jquery扩展
    $.fn.lazyLoad = function (callback) {
        var obj = $(this);
        // var src = obj.attr('src');
        var url = obj.attr('data-src');
        if (url != null && url != '') {
            obj.data('url', url);
            window.imgReady(url, function (w, h) {
                this.attr('src', this.data('url'));
                if (callback) {
                    callback.call(this);
                }
            }.bind(obj), function () {
                if (callback) {
                    callback.call(this);
                }
            });
        } else {
            if (callback) {
                callback.call(this);
            }
            // console.log(url);
        }
    }
    $.fn.showCenter = function (width, height, animate, callback) {
        var cha = 20;// 将距离边的距离
        // 如果没有传入，就按照当前的大小来换算
        width || (width = parseInt($(this).attr('win-width')));
        height || (height = parseInt($(this).attr('win-height')));
        if (width) {
            $(this).data('width', width);
        } else {
            width = $(this).data('width');
        }
        if (height) {
            $(this).data('height', height);
        } else {
            height = $(this).data('height');
        }
        var _width = width, _height = height;
        var maxWidth = $(window).width() - cha;
        var maxHeight = $(window).height() - cha;
        if (maxWidth < width) {
            _width = maxWidth;
            _height = (height / width) * _width;
        }
        if (maxHeight < _height) {
            _height = maxHeight;
            _width = (width / height) * _height;
        }

        var top = (maxHeight - _height + cha) / 2;
        var left = (maxWidth - _width + cha) / 2;
        var point = {
            width: _width,
            height: _height,
            top: top,
            left: left
        };
        if (animate) {
            $(this).animate(point, 'fast', callback);
        } else {
            $(this).css(point);
        }
        return $(this);
    }
    $.fn.toShowHtml = function () {
        if ($(this).size() > 0) {
            $(this).each(function (k, v) {
                var t = $(v).text();
                if (t != null && t.length > 0) {
                    $(v).html(t.toShowHtml());
                }
            })
        } else {
            var t = $(this).text();
            if (t != null && t.length > 0) {
                $(this).html(t.toShowHtml());
            }
        }

    }

    var _serializeFormToJson = function (formId) {
        var jsonObj = {};
        $("#" + formId).find(":input").each(function (k, v) {
            if ($(v).attr("id") != null && $(v).attr("id") != "") {
                jsonObj[$(v).attr("id")] = $(v).val();
            }
        });
        return jsonObj;
    }

    var _showDataSelector = function (input) {
        var obj = $(input);
        var valueStr = obj.val();
        var value = new Date();
        if (valueStr != '') {
            value = new Date().setDateByStr(valueStr, '-');
        }
        $.showDateSelect({
            isShowTime: false,
            value: value,
            top: obj.offset().top + obj.outerHeight(),
            left: obj.offset().left,
            selectDateAction: function (value) {
                obj.val(value.showDate());
                if (window.menuObj != null) {
                    window.menuObj.hide();
                    window.menuObj = null;
                }
            }
        });
    }

    var _formatFloat = function (src, pos) {
        return Math.round(src * Math.pow(10, pos)) / Math.pow(10, pos);
    }
    // 获取window的宽和高
    var _getWindowSize = function () {
        var e = window, a = 'inner';
        if (!('innerWidth' in window)) {
            a = 'client';
            e = document.documentElement || document.body;
        }

        return {
            width: e[a + 'Width'],
            height: e[a + 'Height']
        };
    };
    var _checkMobilePhone = function (number) {
        return number.match(/^1[3|4|5|8][0-9]\d{4,8}$/);
    }

    var _checkEmail = function (email) {
        return email.match(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/);
    }
    var _formatPrice = function (price) {
        if (price == null)
            return 0;
        var totalpriceDis = Math.ceil(price * 10) / 10;
        return totalpriceDis.toFixed(2);
    }

    window.zIndex = 20;
    window.getWindowSize = _getWindowSize;
    window.imgReady = _imgReady;
    window.isEmpty = _isEmpty;
    window.genUUID = _genUUID;
    window.genShortId = _genShortId;
    window.randomArray = _randomArray;
    window.serializeFormToJson = _serializeFormToJson;
    window.showDataSelector = _showDataSelector;
    window.formatFloat = _formatFloat;
    window.checkMobilePhone = _checkMobilePhone;
    window.checkEmail = _checkEmail;
    window.formatPrice = _formatPrice;
})();

(function () {
    // 自动关闭提示框
    alertViewStack = [];
    var closeTime = null

    function _malert(str, time) {
        window.malertBottom || (window.malertBottom = 10);
        window.malertBottom += 30;
        var text = $("<div class='alertMsgText'></div>").appendTo($('body'));
        text.html(str);
        text.css({
            zIndex: window.zIndex + 10000
        }).show();
        var left = ($(window).width() - text.width()) / 2;
        left = left < 0 ? 0 : left;
        text.css({
            left: left,
            bottom: window.malertBottom
        }).hide().fadeIn(500);

        var closeTime = setTimeout(function () {
            text.fadeOut('slow', function () {
                $(this).remove();
            });
            window.malertBottom = 10;
        }, time || 5000);
    }

    window.malert = _malert;
})();

(function () {
    var proxy = function (fn, target) {
        var proxy = function () {
            if (2 < arguments.length) {
                var privateArgs = Array.prototype.slice.call(arguments, 2);
                return function () {
                    var args = Array.prototype.slice.call(arguments);
                    Array.prototype.unshift.apply(args, privateArgs);
                    return fn.apply(target, args);
                }
            }
            return function () {
                return fn.apply(target, arguments);
            }
        }
        return proxy.apply(null, arguments);
    };
    /* 支持原生的使用原生的 */
    Function.prototype.bind = Function.prototype.bind || function (target) {
            if (1 < arguments.length) {
                var args = Array.prototype.slice.call(arguments, 1);
                args.unshift(this, target);
                return proxy.apply(null, args);
            }
            return proxy(this, target);
        };
    Function.prototype.delay = function (timeout) {
        var __method = this, args = Array.prototype.slice.call(arguments, 1);
        timeout = timeout * 1000
        return window.setTimeout(function () {
            return __method.apply(__method, args);
        }, timeout);
    }

    // 数组相关
    Array.prototype.remove = function (index) {// 删除某个位置的元素
        return this.splice(index, 1);
    }
    Array.prototype.firstAdd = function (item) {// 从前面添加
        return this.splice(0, 0, item);
    }
    Array.prototype.addOrUpdate = function (item) {// 存在更新，不存在就添加
        var exist = false;
        for (key in this) {
            if (item == this[key]) {
                this[key] = item;
                exist = true;
                break;
            }
        }
        if (!exist) {
            this.push(item);
        }
    }
    Array.prototype.removeByObj = function (item) {// 删除数组存在的某个对象
        for (key in this) {
            if (item == this[key]) {
                this.splice(key, 1);
                break;
            }
        }
    }
    Array.prototype.addAll = function ($array) {
        if ($array == null || $array.length == 0)
            return;
        if ($array == this) {
            console.error('不能自己添加自己!', this, $array);
        }
        for (var $i = 0; $i < $array.length; $i++)
            this.push($array[$i]);
    }

    Array.prototype.max = function () {
        return Math.max.apply({}, this)
    }
    Array.prototype.min = function () {
        return Math.min.apply({}, this)
    }
    Array.prototype.unique2 = function () {// 去重并排序
        this.sort(); // 先排序
        var res = [this[0]];
        for (var i = 1; i < this.length; i++) {
            if (this[i] !== res[res.length - 1]) {
                res.push(this[i]);
            }
        }
        return res;
    }
    // extend String

    if (typeof String.prototype.startsWith != 'function') {
        // see below for better implementation!
        String.prototype.startsWith = function (str) {
            return this.indexOf(str) == 0;
        };
    }
    if (typeof String.prototype.trim != 'function') {
        String.prototype.trim = function () {
            return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
        }
    }
    String.prototype.toShowHtml = function () {
        var str = this;
        str = str.replace(/>/g, '&gt;').replace(/</g, '&lt;').replace(/\n/g, '<br/>').replace(/ /g, '&nbsp;');
        // link，b，_替换
        str = str.replace(/\[B\]/g, '<strong>').replace(/\[\/B\]/g, '</strong>');
        str = str.replace(/\[_\]/g, '<u>').replace(/\[\/_\]/g, '</u>');
        // 处理链接：
        str = str.replace(/\[\/A\]/g, '</a>');

        var regx = /\[A\[[^\]]+\]\]/g;
        var rs = str.match(regx);
        if (rs != null && rs.length > 0) {
            var allStr = '';
            var estr = str;
            $.each(rs, function (k, value) {
                if (value != null) {
                    var href = (value + '').replace('[A[', '').replace(']]', '');
                    var index = estr.search(regx);
                    if (index > -1) {
                        allStr += estr.substr(0, index) + '<a href="' + href + '" target="_blank">';
                        estr = estr.substr(index + value.length);
                    }
                }
            });
            str = allStr + estr;
        }
        return str;
    }
})();
// 从查询字符串获取相应的值
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return unescape(r[2]);
    return null;
}
function mapToQueryString(map) {
    var ss = [];
    for (var key in map) {
        if (key != null && map[key] != null) {
            ss.push(key + '=' + map[key]);
        }
    }
    if (ss.length == 0) {
        return '';
    } else {
        return '?' + ss.join('&');
    }
}
// js加载<script>
function loadJs(id, url, callback) {
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    script.id = id;
    script.onload = script.onreadystatechange = function () {
        // alert(script.readyState);
        if (script.readyState && script.readyState != 'loaded' && script.readyState != 'complete')
            return;
        script.onreadystatechange = script.onload = null
        if (callback)
            callback();
    }
    document.body.appendChild(script);
}
// 数字前补0生成固定长度string
window.greStrInt = function (input, numLen) {
    var s = input + '';
    var len = s.length;
    if (len > numLen) {
        s = s.substring(0, numLen);
    } else {
        for (var i = 0; i < numLen - len; i++) {
            s = "0" + s;
        }
    }
    return s;
}
// 获取随机数
window.getRandom = function (min, max) {
    if (max == null) {// 如果只有一个参数
        max = min;
        min = 0;
    }
    return min + Math.floor(Math.random() * (max - min))
}

// 时间显示
window.yearOptions = [], window.monthOptions = [], window.dayOptions = [];
for (var i = 1920; i < 2020; i++) {
    window.yearOptions.push(i);
}
for (var i = 1; i <= 12; i++) {
    window.monthOptions.push(i);
}
for (var i = 1; i <= 31; i++) {
    window.dayOptions.push(i);
}

// 数学相关
Number.prototype.toMoney = function () {
    var f_x = Math.round(this * 100) / 100;
    var s_x = f_x.toString();
    var pos_decimal = s_x.indexOf('.');
    if (pos_decimal < 0) {
        pos_decimal = s_x.length;
        s_x += '.';
    }
    while (s_x.length <= pos_decimal + 2) {
        s_x += '0';
    }
    return s_x;
}

// 时间相关
Date.prototype.addDays = function (d) {
    this.setDate(this.getDate() + d);
    return this;
};

Date.prototype.addWeeks = function (w) {
    this.addDays(w * 7);
    return this;
};

Date.prototype.addMonths = function (m) {
    var d = this.getDate();
    this.setMonth(this.getMonth() + m);

    if (this.getDate() < d)
        this.setDate(0);
    return this;
};

Date.prototype.addYears = function (y) {
    var m = this.getMonth();
    this.setFullYear(this.getFullYear() + y);

    if (m < this.getMonth()) {
        this.setDate(0);
    }
    return this;
};

Date.prototype.setDateByStr = function (str, sp) {
    if (window.isEmpty(str)) {
        return null;
    }
    sp || (sp = '-');
    var strs = str.split(sp);
    this.setFullYear(parseInt(strs[0]));
    this.setMonth(parseInt(strs[1], 10) - 1, parseInt(strs[2], 10));
    this.setHours(0);
    this.setMinutes(0);
    this.setSeconds(0);
    // this.setDate(parseInt(strs[2]));
    return this;
}
Date.prototype.setTimeByStr = function (str, sp) {
    sp || (sp = ':');
    var strs = str.split(sp);
    // this.setDate(parseInt(strs[2]));
    this.setHours(parseInt(strs[0]));
    this.setMinutes(parseInt(strs[1]));
    this.setSeconds(0);
    return this;
}

// 获取月份最后一天
window.getMonthLastDay = function (year, month) {
    var new_year = year; // 取当前的年份
    var new_month = month++;// 取下一个月的第一天，方便计算（最后一天不固定）
    if (month > 12) {
        new_month -= 12; // 月份减
        new_year++; // 年份增
    }
    var new_date = new Date(new_year, new_month, 1); // 取当年当月中的第一天
    return (new Date(new_date.getTime() - 1000 * 60 * 60 * 24)).getDate();// 获取当月最后一天日期
}

Date.prototype.showYFullTime = function () {

    return this.getFullYear() + '-' + greStrInt(this.getMonth() + 1, 2) + '-' + greStrInt(this.getDate(), 2) + ' ' + greStrInt(this.getHours(), 2) + ':' + greStrInt(this.getMinutes(), 2) + ':'
        + greStrInt(this.getSeconds(), 2);
};
Date.prototype.showDateTime = function () {
    return greStrInt(this.getMonth() + 1, 2) + '-' + greStrInt(this.getDate(), 2) + ' ' + greStrInt(this.getHours(), 2) + ':' + greStrInt(this.getMinutes(), 2);
};
Date.prototype.showTime = function () {
    return greStrInt(this.getHours(), 2) + ':' + greStrInt(this.getMinutes(), 2);
};
// 时间date
Date.prototype.showDate = function (sp) {
    sp || (sp = '-');
    return this.getFullYear() + sp + greStrInt(this.getMonth() + 1, 2) + sp + greStrInt(this.getDate(), 2);
};
// year month
Date.prototype.showYearMonth = function (sp) {
    sp || (sp = '-');
    return this.getFullYear() + sp + greStrInt(this.getMonth() + 1, 2);
};
Date.prototype.showMonthDay = function (sp) {
    sp || (sp = '-');
    return greStrInt(this.getMonth() + 1, 2) + sp + greStrInt(this.getDate(), 2);
};
Date.prototype.showChatTime = function () {
    var today = new Date();
    if (today.getFullYear() == this.getFullYear() && today.getMonth() == this.getMonth() && today.getDate() == this.getDate()) {// 今天
        return greStrInt(this.getHours(), 2) + ':' + greStrInt(this.getMinutes(), 2);
    } else if (today.getFullYear() == this.getFullYear()) {
        return greStrInt(this.getMonth() + 1, 2) + '月' + greStrInt(this.getDate(), 2) + '日 ' + greStrInt(this.getHours(), 2) + ':' + greStrInt(this.getMinutes(), 2);
    } else {
        var sp = '-';
        return this.getFullYear() + sp + greStrInt(this.getMonth() + 1, 2) + sp + greStrInt(this.getDate(), 2);
    }
}
Date.prototype.showCoolTime = function () {
    var today = new Date();
    var longNow = today.getTime();
    // var date = new Date(longtime);
    var cha = longNow - this.getTime();
    if (cha <= 1000 * 60) {
        return '刚刚';
        // } else if (cha <= 1000 * 60) {
        // return parseInt(cha / (1000)) + '秒前';
    } else if (cha <= 1000 * 60 * 60) {
        return parseInt(cha / (1000 * 60)) + '分前';
    } else if (cha <= 1000 * 60 * 60 * 24) {
        return parseInt(cha / (1000 * 60 * 60)) + '小时前';
    } else if (cha <= 1000 * 60 * 60 * 24 * 3) {
        return parseInt(cha / (1000 * 60 * 60 * 24)) + '天前';
    } else {
        // if (today.getFullYear() == this.getFullYear()) {
        // return this.showMonthDay() + ' ' + this.showTime();
        // } else {
        return this.showDate() + ' ' + this.showTime();
        // }
    }
};
// -------cookies-----------------------
(function () {
    function setCookie(name, value, expMinutes, expDay) { // 设置名称为name,值为value的Cookie
        if(expMinutes == null&&expDay == null){
            document.cookie = name + "=" + encodeURIComponent(value) + ";path=/";
            return;
        }
        var exp = new Date();
        if (expMinutes != null) {
            exp.setTime(exp.getTime() + expMinutes * 60 * 1000);
        } else {
            var Days = expDay == null ? 60 : expDay;
            exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
        }
        document.cookie = name + "=" + encodeURIComponent(value) + ";expires=" + exp.toGMTString() + ";path=/";
    }

    function deleteCookie(name) { // 删除名称为name的Cookie
        var exp = new Date();
        exp.setTime(exp.getTime() - 10000);
        var cval = getCookie(name);
        if (cval != null)
            document.cookie = name + "=" + encodeURIComponent(cval) + ";expires=" + exp.toGMTString() + ";path=/";
    }

    function getCookieVal(offset) { // 取得项名称为offset的cookie值
        var endstr = document.cookie.indexOf(";", offset);
        if (endstr == -1)
            endstr = document.cookie.length;
        return decodeURIComponent(document.cookie.substring(offset, endstr));
    }

    function getCookie(name) { // 取得名称为name的cookie值
        var arg = name + "=";
        var alen = arg.length;
        var clen = document.cookie.length;
        var i = 0;
        while (i < clen) {
            var j = i + alen;
            if (document.cookie.substring(i, j) == arg)
                return getCookieVal(j);
            i = document.cookie.indexOf(" ", i) + 1;
            if (i == 0)
                break;
        }
        return null;
    }

    window.setCookie = setCookie;
    window.getCookie = getCookie;
    window.deleteCookie = deleteCookie;
})();
(function ($) {
    // 自动将textarea变高
    $.fn.autoHeightTextarea = function () {
        var cssObj = {
            'overflow': 'hidden',
            'wordBreak': 'break-all',
            'resize': 'none',
            'padding': 4,
            'border': '1px solid #ddd',
            'lineHeight': '24px',

            'borderRadius': 4,
            'minHeight': 10
        };
        $(this).css({
            'height': 34
        }).css(cssObj);

        var div = $('#outHeightDiv');
        if (div.size() == 0) {
            div = $('<div></div>').attr('id', 'outHeightDiv').css({
                top: -9999,
                left: -9999,
                position: "fixed"
            }).css(cssObj)
            $('body').append(div);

            window.changeTextareaHeight = function () {
                var width = $(this).width();
                var t = $(this).val();
                t = t == '' ? 'a' : t;
                div.html(t.toShowHtml() + (t.substring(t.length - 1) == '\n' ? 'a' : '')).width(width);
                $(this).height(div.height());
            }
        }

        $(this).bind("propertychange", window.changeTextareaHeight);// for IE
        $(this).bind("input", window.changeTextareaHeight);// for !IE
        return $(this);
    }
    $.fn.getSizeStr = function (type) {
        var str = $(this).css(type);
        if (str == null)
            return 0;
        if (str.indexOf(' ') > -1) {
            str = str.split(' ')[0];
        }
        str = str.replace(/[^\d|\.]+/g, '');
        str = str == '' || str == null ? '0' : str;
        return parseFloat(str);
    }
    $.fn.fGetCha = function (isWidth) {
        var size = 0;
        var arr = ['padding', 'border', 'margin'];
        $.each(arr, function (k, v) {
            var array = isWidth ? ['Left', 'Right'] : ['Top', 'Bottom'];
            $.each(array, function (kk, vv) {
                size += $(this).getSizeStr(v + vv + (v == 'border' ? 'Width' : ''));
            }.bind(this));
        }.bind(this));
        return size;
    }
    $.fn.animateRotate = function () {//div转动的动画
        var obj = $(this);
        if (obj.data('isRun')) {
            return;
        }
        obj.data('r', 0).data('isRun', true);
        var setR = function () {
            var isRun = obj.data('isRun');
            isRun = isRun ? isRun : false;
            if (!isRun) {
                return;
            }
            var r = obj.data('r');
            r += 45;
            r = r % 360;
            obj.css({'transform': 'rotate(' + r + 'deg)'}).data('r', r);
            setR.delay(0.1);
            //console.log(r);
        }
        setR();
    }
})(jQuery);

// -------------------------------------------- rest default ----------------
window.showAjaxError = function (err) {
    var msgError = '';
    if (err != null && err.responseText != null) {
        try {
            var errObj = JSON.parse(err.responseText);
            msgError = errObj.errMsg;
        } catch (e) {
        }
    }
    malert(msgError == '' ? '服务器内部错误' : msgError);
};
window.restAJAXObj = {
    statusCode: {
        '201': function (secc) {// created 创建资源成功
            // showAlert('添加成功!');
        },
        '202': function (secc) {// Accepted 服务端开始处理
            // showAlert('服务端开始处理!');
        },
        '204': function (secc) {// NoContent 指示已成功处理请求并且响应已被设定为无内容,用在删除后的响应
            // showAlert('删除成功!', 5000);
        },
        '303': function (secc) {// 转向请求Location头设定的url

        },
        '400': function (err) {// BadRequest指示服务器未能识别请求
            window.showAjaxError(err);
        },
        '401': function (err, o, obj) {// 指示请求的资源要求身份验证,WWW-Authenticate头包含如何执行身份验证的详细信息
            // 引导用户登录
            malert('请先登录!', 5000);
        },
        '403': function (err) {// NotFound
            // showAlert('服务端拒绝您的请求,可能您没有访问的权限!', 7000);
        },
        '404': function (err) {
            // showAlert('访问的数据不存在！', 7000);
        },
        '405': function (err) {
            alert('使用错误的方法访问资源');
        },
        '412': function (err) {
            // showAlert('操作的数据不是最新的！请重新获取后再执行该操作', 7000);
        },
        '500': function (err, obj, obj1) {// InternalServerError 指示服务器上发生了一般错误
            window.showAjaxError(err);
        },
        '501': function (err) {// NotImplemented 指示服务器不支持请求的函数
            // showAlert('服务端不支持该操作！', 7000);
        },
        '504': function (err) {// NotImplemented 指示服务器不支持请求的函数
            // showAlert('网路通讯错误，响应超时！', 7000);
        }
    },
    'contentType': 'application/json',
    'dataType': 'json',
    'async': true,
    'cache': false,
    'beforeSend': function () {
        if (this._submitBtn != null) {
            var value = this._submitBtn.text();
            this._submitBtn.data('btnText', value).text('处理中...').attr('disabled', true);
        }
        if ($('#ajaxLoadingCon').size() > 0 && window.isMobile() && this.showLoadingCon) {
            $('#ajaxLoadingCon').css({
                'opacity': 0.3
            }).show();
            $('#ajaxLoadingConText').show();
        }
    },
    'complete': function (a, b, c, d) {
        if (this._submitBtn != null) {
            (function () {
                console.log(this);
                var oldValue = this._submitBtn.data('btnText');
                this._submitBtn.text(oldValue).attr('disabled', false);
            }.bind(this)).delay(1);
        }
        if ($('#ajaxLoadingCon').size() > 0 && window.isMobile() && this.showLoadingCon) {
            (function () {
                $('#ajaxLoadingConText').hide();
                $('#ajaxLoadingCon').fadeOut('slow', function () {
                });
            }).delay(0.1);
        }

    }
};
$.ajaxSetup(window.restAJAXObj);

var getJQueryObjById = function (elId, pObj, elName, className) {
    var qName = '#' + elId;
    elName || (elName = 'div');
    if ($(qName).size() == 0) {
        var v = $('<' + elName + '></' + elName + '>').attr('id', elId).appendTo(pObj);
        if (className) {
            v.addClass(className);
        }
    }
    return $(qName);
}
// 显示
