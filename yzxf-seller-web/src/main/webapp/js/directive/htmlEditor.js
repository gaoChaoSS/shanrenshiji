/**
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";
    var getEditSelection = function () {
        var selection = null;
        if (window.getSelection) {
            selection = window.getSelection();
        } else if (document.selection) {
            selection = document.selection;
        }
        return selection;
    }
    var getEditRange = function () {
        var selection = getEditSelection();
        var range = null;
        if (selection.getRangeAt)
            range = selection.getRangeAt(0);
        else if ($.browser.msie) {
            range = selection.createRange();
        } else {// 较老版本Safari!
            range = document.createRange();
            range.setStart(selection.anchorNode, selection.anchorOffset);
            range.setEnd(selection.focusNode, selection.focusOffset);
        }
        return range;
    }
    // 获取选择的文本
    var getSelectionText = function () {
        var selectionText = null;
        var selection = getEditSelection();
        var range = getEditRange();
        if (selection.text) {
            selectionText = selection.text;
        } else if ($.browser.msie) {
            selectionText = range.text;
        } else {
            selectionText = selection + '';
        }
        return selectionText;
    }

    function getSelectedHtml() {
        if (window.getSelection) { // chrome,firefox,opera
            var range = window.getSelection().getRangeAt(0);
            var container = document.createElement('div');
            container.appendChild(range.cloneContents());
            return container.innerHTML;
            // return window.getSelection(); //只复制文本
        } else if (document.getSelection) { // 其他
            var range = window.getSelection().getRangeAt(0);
            var container = document.createElement('div');
            container.appendChild(range.cloneContents());
            return container.innerHTML;
            // return document.getSelection(); //只复制文本
        } // by www.jquerycn.cn
        else if (document.selection) { // IE特有的
            return document.selection.createRange().htmlText;
            // return document.selection.createRange().text; //只复制文本
        }
    }

    // 获取选择对象的父亲标签
    var getPNode = function () {
        var range = getEditRange();
        var selection = getEditSelection();
        return $(selection.focusNode).parent() ? $(selection.focusNode).parent() : $(range.parentElement());
    }

    var getCursortPosition = function (ctrl) {
        var CaretPos = 0; // IE Support
        if (document.selection) {
            ctrl.focus();
            var Sel = document.selection.createRange();
            Sel.moveStart('character', -ctrl.value.length);
            CaretPos = Sel.text.length;
        }
        // Firefox support
        else if (ctrl.selectionStart || ctrl.selectionStart == '0')
            CaretPos = ctrl.selectionStart;
        return (CaretPos);
    }
    var setCaretPosition = function (element, start, end) {
        var r = getEditRange();
        if ($.browser.msie) {
            r.moveStart('character', position);
            r.collapse(true);
            r.select();
        } else {
            // r.setStart(element, 2);
            // r.setEnd(element, 3);
        }
    }
    // 插入html数据到选择的地方
    var cssMap = {
        'bold': 'fontWeight',
        'underline': 'borderBottom:'
    }
    var cssSetMap = {
        'bold': 'font-weight:bold;',
        'underline': 'border-bottom:2px solid red;'
    }
    var insertHtml = function (comm) {
        var sel = getEditSelection();
        var endHtml = '';
        if ('bold' == comm || 'underline' == comm) {
            var html = getSelectedHtml();
            var range = getEditRange();
            // range.deleteContents();
            console.log('start html:', html);

            var obj = $('<span>' + html + '</span>');
            $.each(obj.children(), function (k, v) {
                $(v).css(cssMap[comm], '');
                console.log(k, 'style:', $(v).attr('style'));
            });
            endHtml = obj.html().replace(/<span style="">/g, '').replace(/<\/span>/g, '');
            endHtml = '<span style="' + cssSetMap[comm] + '">' + endHtml + '</span>';
            console.log('end:', endHtml)
        }

        if (range.pasteHTML) {
            range.pasteHTML(endHtml);
        } else {
            document.execCommand('insertHTML', false, endHtml);
            return;
            // var frg = range.createContextualFragment(endHtml);
            // range.insertNode(frg);
        }
    }

    var insertHtml1 = function (html) {
        html = 'xxxx'
        var sel = getEditSelection();
        var range = getEditRange();
        if (range.startContainer) { // DOM下
            sel.removeAllRanges(); // 删除Selection中的所有Range
            range.deleteContents(); // 清除Range中的内容
            // 获得Range中的第一个html结点
            var container = range.startContainer;
            // 获得Range起点的位移
            var pos = range.startOffset;
            // 建一个空Range
            range = document.createRange();
            // 插入内容
            var cons = document.createTextNode(html);
            if (container.nodeType == 3) {// 如是一个TextNode
                container.insertData(pos, cons.nodeValue);
                // 改变光标位置
                range.setEnd(container, pos + cons.nodeValue.length);
                range.setStart(container, pos + cons.nodeValue.length);
            } else {// 如果是一个HTML Node
                var afternode = container.childNodes[pos];
                container.insertBefore(cons, afternode);

                range.setEnd(cons, cons.nodeValue.length);
                range.setStart(cons, cons.nodeValue.length);
            }
            sel.addRange(range);
            $(sel.focusNode.parentNode).focus();
        }
    }

    // 禁止后退键 作用于IE、Chrome
    $(document).on('keydown', function (e) {
        return;
        if (e.keyCode == 8) {
            console.log(e.target);
            var el = $(e.target);
            console.log(el.attr('tagName'))
            var tagName = el[0].tagName.toLowerCase();

            var inputType = '';
            if (el.attr('type') != null) {
                inputType = el.attr('type').toLowerCase();
            }
            var isInput = tagName = 'input' && (inputType == 'password' || inputType == 'email' || inputType == 'text');
            if (!(el.attr('contenteditable') || tagName == 'textarea' || isInput)) {
                return false;
            }
        }
        return true;
    });
    window.showImgAction = function (el) {

    }

    window.app.directive('htmlEditor', function () {
        return {
            restrict: 'AE',
            transclude: true,
            replace: true,

            scope: {
                id: '@',
                model: '@',
                entity: '@',
                fieldName: '@',
                entityId: '@',
                isEdit: '='
            },
            templateUrl: '/temp/htmlEditor.html',
            controller: function ($scope, $element, $http, $compile, $popWindow) {
                $element.parent().css('overflow', 'hidden');
                var htmlContentObj = null;
                // 初始化事件
                console.log(document.execCommand);
                $scope.$$selecteText = false;

                if ($scope.id == null) {
                    $scope.id = 'htmlEditor_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }

                $scope.actionPath = window.basePath + '/' + $scope.model + "/" + $scope.entity;

                $scope.focusEditor = function (e) {
                    $scope.$$focusEditor = true;
                    if (htmlContentObj == null) {
                        htmlContentObj = $(e.target);
                    }
                    console.log('$$focusEditor:', $scope.$$focusEditor);
                }
                $scope.blurEditor = function (e) {
                    if (htmlContentObj == null) {
                        htmlContentObj = $(e.target);
                    }
                    $scope.$$focusEditor = false;
                    console.log('$$focusEditor:', $scope.$$focusEditor);
                }
                $scope.contentChange = function (e) {
                    if (htmlContentObj == null) {
                        htmlContentObj = $(e.target);
                    }
                    var selectText = getSelectionText();
                    selectText = selectText == null ? '' : selectText.trim();
                    console.log('select text: ', selectText);
                    $scope.$$selecteText = selectText.length > 0;
                    $scope.$$isEmpty = $element.find('.htmlEditContent').html().length > 0;
                }
                var urlReg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/g;
                var checkUrl = function (url) {
                    return !!url.match(urlReg);
                }

                //通过canvas转换成base64
                var getBase64Image = function (img) {
                    var canvas = document.createElement("canvas");
                    canvas.width = img.width;
                    canvas.height = img.height;
                    var ctx = canvas.getContext("2d");
                    ctx.drawImage(img, 0, 0, img.width, img.height);
                    //var ext = img.src.substring(img.src.lastIndexOf(".") + 1).toLowerCase();
                    var dataURL = canvas.toDataURL("image/jpg");
                    return dataURL;
                }

                //优化格式
                $scope.formatCode = function () {
                    $element.find('.htmlEditContent').find('*').removeAttr('style').removeAttr('class');
                    var len = 0;
                    $element.find('img').each(function (k, v) {
                        console.log('img obj:', k, v.attributes);
                        $.each(v.attributes, function (kk, vv) {
                            if (vv != null && vv.name != null && vv.name.toLowerCase() != 'src') {
                                $(v).removeAttr(vv.name);
                            } else {
                                if (!window.isEmpty(vv.value) && vv.value.toLowerCase().startsWith('http')) {
                                    len++;
                                    $(v).addClass('outSiteImg');
                                    $(v).attr('title', '站外图片, 点击:"下载站外图片" 即可下载图片到本站');
                                }
                            }
                        });
                    });
                    if (len > 0) {
                        $scope.statusMsg = '共有 ' + len + ' 张外站图片,用红色边框标出!';
                    }
                    malert('优化格式成功!');
                }
                $scope.editBtn = function (isEdit) {
                    isEdit = isEdit == null ? !$scope.isEdit : isEdit;
                    $scope.isEdit = isEdit;
                }
                //通过服务器下载站外图片,防止图片过期的问题
                $scope.uploadOtherPic = function () {

                }
                $scope.uploadImg = function (e) {
                    console.log('file obj', e.files);
                    var fileSize = e.files.length;
                    var i = 0;
                    if (fileSize > 0) {
                        window.showLoading();
                    }
                    try {
                        for (var index = 0; index < fileSize; index++) {
                            i++;
                            var file = e.files[index];
                            var fileId = genUUID();
                            var url = '/s_img/' + encodeURIComponent(file.name) + '?_id=' + fileId + '&wh=650_0';
                            var imgHtml = '<img src="' + url + '"/>';
                            htmlContentObj.focus();
                            document.execCommand('insertHTML', false, imgHtml);
                        }
                        if (i == fileSize) {
                            window.hideLoading();
                        }
                    } catch (e) {
                        console.error(e);
                        window.hideLoading();
                    }
                }
                var popWinId = $scope.id + '_popWin', popWinType = null;
                // var popWinIdImg = $scope.id + '_img_pop';
                $scope.exeComm = function (type) {
                    if ('createLink' == type) {
                        var age = prompt("输入要设置的链接地址:", "http://");
                        if (window.isEmpty(age)) {
                            return;
                        }
                        if (checkUrl(age)) {
                            document.execCommand(type, false, age);
                            $element.find('a').attr('target', '_blank');
                            $scope.saveData(true);
                        } else {
                            malert('不是一个合法的url')
                        }
                    } else if ('img' == type) {
                        popWinType = 'selectImg';
                        $popWindow.add(popWinId, '添加图片', 700, 400);
                    } else if ('bold' == type ||
                        'underline' == type || 'italic' ==
                        type) {
                        insertHtml(type);

                    } else {
                        document.execCommand(type, false, true);
                        $scope.saveData(true);
                    }

                }
                $scope.saveData = function (notShowAlert) {
                    var data = {_id: $scope.entityId};
                    data[$scope.fieldName] = $scope.getContent();
                    $http({
                        method: 'post',
                        url: $scope.actionPath + '/save',
                        data: data,
                        isBackRun: true
                    }).success(function (re) {
                        if (!notShowAlert) {
                            malert('保存成功!');
                        }
                        $scope.statusMsg = new Date().showYFullTime() + ' 保存成功!';
                    });
                }
                $scope.queryData = function () {
                    $http.get($scope.actionPath + '/show?_id=' + $scope.entityId).success(function (re) {
                        $scope.setContent(re.content[$scope.fieldName]);
                        $scope.statusMsg = new Date().showYFullTime() + ' 加载服务器数据成功!';
                    });
                }

                $scope.setContent = function (html) {
                    $element.find('.center').html(html);
                }
                $scope.getContent = function () {
                    return $element.find('.center').html();
                }

                $scope.$on(popWinId + '/init_end', function (event, data) {
                    if (popWinType == 'selectImg') {
                        window.reqUIAction($scope, popWinId + '/setData', {
                            entity: $scope.entity,
                            model: $scope.model,
                            entityId: $scope.entityId,
                            entityField: $scope.fieldName,
                            selectDataType: 'more',
                            inputType: 'fileMore',
                            isEdit: $scope.isEdit
                        });
                        window.reqUIAction($scope, popWinId + '/setTemplate', '/temp/inc_grid_file.html');
                        window.reqUIAction($scope, popWinId + '/addAction', {
                            name: 'ok',
                            title: '确定',
                            isHigh: true,
                            click: function () {
                                var fileUI = 'file_' + $scope.entity + '_' + $scope.fieldName;
                                window.reqUIAction($scope, fileUI + '/getData', 'selectIdMap', function (obj) {
                                    console.log('-- selectIdMap:', obj);
                                    if (obj != null) {
                                        $.each(obj, function (k, v) {
                                            var url = '/s_img/img.jpg?_id=' + k + '&wh=650_0';
                                            var imgHtml = '<img onmousedown="showImgAction(this)" src="' + url + '"/>';
                                            htmlContentObj.focus();
                                            document.execCommand('insertHTML', false, imgHtml);
                                        });
                                        $scope.saveData();
                                    }
                                });
                                window.reqUIAction($scope, popWinId + '/close');
                            }
                        });
                    }
                })

                $scope.queryData();
                exportUiApi($scope, ['close', 'saveData', 'setContent', 'addAction', 'getContent']);
            },

            compile: function (element, attrs, transclude) {

                return function ($scope, $element, attrs, ctrl, transclude) {
                    $element.on('keydown', function (e) {
                        console.log(getSelectionText());
                        console.log(e.keyCode);
                        if (e.keyCode == 224) {
                            // insertHtml('<div
                            // style="color:red">Joey</div>')
                        }
                    });
                    $scope.statusMsg = '初始化编辑器完成';
                    $scope.$root.$broadcast($scope.id + '/init_end');
                };
            }
        }
    });
})(angular);