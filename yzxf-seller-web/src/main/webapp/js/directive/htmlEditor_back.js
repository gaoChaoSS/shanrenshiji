/**
 * 
 * @param angular
 * @param undefined
 */
(function(angular, undefined) {
    "use strict";
    var getEditSelection = function() {
        var selection = null;
        if (window.getSelection) {
            selection = window.getSelection();
        } else if (document.selection) {
            selection = document.selection;
        }
        return selection;
    }
    var getEditRange = function() {
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
    var getSelectionText = function() {
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
    var getPNode = function() {
        var range = getEditRange();
        var selection = getEditSelection();
        return $(selection.focusNode).parent() ? $(selection.focusNode).parent() : $(range.parentElement());
    }

    var getCursortPosition = function(ctrl) {
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
    var setCaretPosition = function(element, start, end) {
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
        'bold' : 'fontWeight',
        'underline' : 'borderBottom:'
    }
    var cssSetMap = {
        'bold' : 'font-weight:bold;',
        'underline' : 'border-bottom:2px solid red;'
    }
    var insertHtml = function(comm) {
        var sel = getEditSelection();
        var endHtml = '';
        if ('bold' == comm || 'underline' == comm) {
            var html = getSelectedHtml();
            var range = getEditRange();
            // range.deleteContents();
            console.log('start html:', html);

            var obj = $('<span>' + html + '</span>');
            $.each(obj.children(), function(k, v) {
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

    var insertHtml1 = function(html) {
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
    $(document).on('keydown', function(e) {
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
    window.app.directive('htmlEditor', function() {
        return {
            restrict : 'AE',
            transclude : true,
            replace : true,
            scope : {
                id : '@'
            },
            templateUrl : '/temp/htmlEditor.html',
            controller : function($scope, $element, $compile, $popWindow) {
                $element.parent().css('overflow', 'hidden');
                var htmlContentObj = null;
                // 初始化事件
                console.log(document.execCommand);
                $scope.$$selecteText = false;

                if ($scope.id == null) {
                    $scope.id = 'htmlEditor_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }
                $scope.focusEditor = function(e) {
                    $scope.$$focusEditor = true;
                    if (htmlContentObj == null) {
                        htmlContentObj = $(e.target);
                    }
                    console.log('$$focusEditor:', $scope.$$focusEditor);
                }
                $scope.blurEditor = function(e) {
                    if (htmlContentObj == null) {
                        htmlContentObj = $(e.target);
                    }
                    $scope.$$focusEditor = false;
                    console.log('$$focusEditor:', $scope.$$focusEditor);
                }
                $scope.contentChange = function(e) {
                    if (htmlContentObj == null) {
                        htmlContentObj = $(e.target);
                    }
                    var selectText = getSelectionText();
                    selectText = selectText == null ? '' : selectText.trim();
                    console.log('select text: ', selectText);
                    $scope.$$selecteText = selectText.length > 0;
                }
                var urlReg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/g;
                var checkUrl = function(url) {
                    return !!url.match(urlReg);
                }

                //优化格式
                $scope.formatCode = function () {
                    $element.find('img').each(function (k, v) {
                        console.log('img obj:', k, v.attributes);
                        $.each(v.attributes, function (kk, vv) {
                            if (vv != null && vv.name != null && vv.name.toLowerCase() != 'src') {
                                $(v).removeAttr(vv.name);
                            }
                        });
                    });
                    $element.find('div[contenteditable="true"]').find('*').removeAttr('style').removeAttr('class');
                }

                // var popWinIdImg = $scope.id + '_img_pop';
                $scope.exeComm = function(type) {
                    if ('createLink' == type) {
                        var age = prompt("输入要设置的链接地址:", "http://");
                        if (checkUrl(age)) {
                            document.execCommand(type, false, age);
                        } else {
                            malert('不是一个合法的url')
                        }
                    } else if ('img' == type) {
                        var fileObj = $('#fileUploadObj');
                        if (fileObj.size() == 0) {
                            fileObj = $('<input id="fileUploadObj" name="fileUploadObj" multiple="true" type="file"/>').appendTo('body');
                            fileObj.change(function(e) {
                                if (fileObj.data('changeAction') != null) {
                                    fileObj.data('changeAction')(e);
                                }
                            });
                        }
                        fileObj.data('changeAction', function(e) {
                            console.log('file obj', e.target.files);
                            var fileSize = e.target.files.length;
                            var i = 0;
                            if (fileSize > 0) {
                                $('#markLoading').show();
                                $('#dataLoading').show();
                            }
                            try {
                                for (var index = 0; index < e.target.files.length; index++) {

                                    var file = e.target.files[index];

                                    if (!window.isImageFile(file.name)) {
                                        i++;
                                        malert('[' + file.name + '] 不是图片格式，请选择gif，jpg，jpeg，png格式图片');
                                        continue;
                                    }
                                    // var s = window.genSizeStr(file.size);
                                    console.log('file:', file);
                                    // var readerLoc = new FileReader();
                                    // readerLoc.readAsDataURL(file);// base64的方式
                                    // 首先本地显示文件
                                    // readerLoc.onload = function(e) {
                                    // var url = this.result;
                                    // var imgHtml = '<p class="imgCon"><img src="' + url + '"/></p>'
                                    // document.execCommand('insertHTML', false, imgHtml);
                                    // }

                                    var formData = new FormData();
                                    formData.append(file.name, file);

                                    var xhr = new XMLHttpRequest();
                                    xhr.open("POST", window.basePath + "/file/FileItem/upload_byteIn"/* , async, default to true */);
                                    xhr.overrideMimeType("application/octet-stream");

                                    var fileId = genUUID();
                                    xhr.setRequestHeader("____id", fileId);
                                    xhr.setRequestHeader("___name", "file.jpg");
                                    xhr.setRequestHeader("___size", file.size);
                                    var _uploadProgress = function(evt) {
                                        if (evt.lengthComputable) {
                                            console.log(evt.lengthComputable, '%');
                                        } else {
                                            console.log('not:', evt.loaded, evt.total);
                                        }
                                    }
                                    var _uploadFailed = function uploadFailed(evt) {
                                        console.log("upload file error.");
                                    }
                                    var _uploadCanceled = function uploadCanceled(evt) {
                                        console.log("upload file by user cancel");

                                    }
                                    xhr.upload.addEventListener("progress", _uploadProgress, false);
                                    xhr.addEventListener("error", _uploadFailed, false);
                                    xhr.addEventListener("abort", _uploadCanceled, false);
                                    xhr.send(formData);
                                    xhr.onreadystatechange = function() {
                                        if (xhr.readyState == 4) {

                                            var isError = false, resp = xhr.responseText;
                                            if (xhr.status == 200) {
                                                console.log("upload complete");
                                                console.log("response: " + xhr.responseText);
                                            } else {
                                                console.log("upload error");
                                                isError = true;
                                            }

                                            if (++i == fileSize) {
                                                $('#markLoading').hide();
                                                $('#dataLoading').hide();
                                            }
                                            if (isError) {
                                                malert('上传文件错误！');
                                            } else {
                                                var url = '/s_img/' + encodeURIComponent(file.name) + '?_id=' + fileId + '&wh=650_0';
                                                var imgHtml = '<p class="imgCon"><img src="' + url + '"/></p>';
                                                htmlContentObj.focus();
                                                document.execCommand('insertHTML', false, imgHtml);
                                            }
                                        }
                                    }
                                }
                                if (i == fileSize) {
                                    $('#markLoading').hide();
                                    $('#dataLoading').hide();
                                }
                            } catch (e) {
                                console.error(e);
                                $('#markLoading').hide();
                                $('#dataLoading').hide();
                            }

                        });

                        return fileObj.click();

                        // $popWindow.add(popWinIdImg, '添加图片',
                        // 700, 400);
                        // $element.find('.center').click();
                        // document.execCommand('insertHTML',
                        // false, 'joey');
                        // } else if ('bold' == type ||
                        // 'underline' == type || 'italic' ==
                        // type) {
                        // insertHtml(type);

                    } else {
                        document.execCommand(type, false, true);
                    }
                }
                $scope.setContent = function(html) {
                    $element.find('.center').html(html);
                }
                $scope.getContent = function() {
                    return $element.find('.center').html();
                }

                exportUiApi($scope, [ 'close', 'setContent', 'addAction', 'getContent' ]);
            },

            compile : function(element, attrs, transclude) {

                return function($scope, $element, attrs, ctrl, transclude) {
                    $element.on('keydown', function(e) {

                        console.log(getSelectionText());
                        console.log(e.keyCode);
                        if (e.keyCode == 224) {
                            // insertHtml('<div
                            // style="color:red">Joey</div>')
                        }
                    })
                    $scope.$root.$broadcast($scope.id + '/init_end');
                };
            }
        }
    });

})(angular);