/**
 * 
 * @param angular
 * @param undefined
 */
(function(angular, undefined) {
    "use strict";

    var sizeUnit = [ 'B', 'K', 'M', 'G', 'T' ];
    window.genSizeStr = function(size, unitIndex) {
        unitIndex = unitIndex == null ? 0 : unitIndex;
        if (size > 1024 && unitIndex < sizeUnit.length) {
            return window.genSizeStr(size / 1024, unitIndex + 1);
        } else {
            console.log(unitIndex, sizeUnit[unitIndex], size)
            return size.toFixed(2) + sizeUnit[unitIndex];
        }
    }
    window.isImageFile = function(fileName) {
        if (fileName == null) {
            return false;
        }
        var s = /\.(gif|jpg|jpeg|png)$/i.test(fileName.toLowerCase());
        return s;
    }
    var setLoadingPercent = function(ctx, p, width, height) {
        var fontSize = 15;
        var fontLeft = (width - fontSize * 2) / 2, fontTop = (height + fontSize / 2) / 2;
        var angle = p * 3.6;
        if (angle >= 360) {
            // clearInterval(timer);
        }
        ctx.clearRect(0, 0, width, height);
        // 画扇形
        ctx.save();
        ctx.translate(width / 2, height / 2);
        ctx.rotate(-Math.PI / 2)
        ctx.fillStyle = "rgba(0,0,0,0.4)";
        ctx.beginPath();
        ctx.arc(0, 0, width / 2, Math.PI / 180 * angle, Math.PI * 2, false);
        ctx.lineTo(0, 0);
        ctx.closePath();
        ctx.fill();
        ctx.restore();
        // 结束画扇形

        ctx.beginPath();
        ctx.fillStyle = 'rgba(0,0,0,0.2)';
        ctx.arc(width / 2, height / 2, 20, 0, 2 * Math.PI, true);
        ctx.closePath();
        ctx.fill();

        ctx.beginPath();
        ctx.fillStyle = 'rgb(255,255,255)';
        ctx.font = 'bold ' + fontSize + 'px arial';
        ctx.fillText(p + '%', fontLeft, fontTop);
    }

    window.app.directive('fileItem', function() {
        return {
            scope : {
                id : '@'
            },
            controller : function($scope, $element, $timeout) {
                // 发一个消息给父亲
                console.log('file item', $scope.item);
                var canvas = null, ctx = null, width = null, height = null, percent = 0, isRun = false, maxPercent = 0, endCallback = null;
                var setLoadingPercentAnimate = function(ctx, targetPer, width, height, callback) {
                    var cha = targetPer - percent;
                    maxPercent = maxPercent < targetPer ? targetPer : maxPercent;
                    maxPercent = maxPercent > 100 ? 100 : maxPercent;
                    if (callback != null) {// 将callback记录下来
                        endCallback = callback;
                    }
                    if (cha <= 0) {
                        return;
                    }
                    if (isRun) {
                        return;
                    }
                    isRun = true;
                    var _run = function() {
                        (function() {
                            if (percent > maxPercent) {
                                isRun = false;
                                return;
                            }
                            // console.log('percent', percent);
                            setLoadingPercent(ctx, percent, width, height);
                            if (percent == 100) {
                                console.log('endCallback', endCallback)
                                if (endCallback) {
                                    endCallback();
                                    return;
                                }
                            }
                            _run(++percent);
                        }).delay(0.006);
                    }
                    _run();
                }
                var timer = $timeout(function() {
                    var canvas = $element.find('canvas');
                    if (canvas == null || canvas.length == 0) {
                        return;
                    }
                    console.log('canvas', canvas);
                    ctx = canvas[0].getContext("2d");
                    width = canvas[0].width;
                    height = canvas[0].height;
                    console.log('width', width, 'height', height);
                    setLoadingPercent(ctx, percent, width, height);
                    $scope.$emit('/uploadFile_item_ok', $scope.id);
                }, 1);
                timer.then(function() {
                    console.log("Timer resolved!", Date.now());
                    $timeout.cancel(timer);
                });
                // -- 动画设置结束 --

                $scope.uploadFile = function(fileObj) {
                    console.log('-- start upload file:', fileObj);

                    // 构造 XMLHttpRequest 对象，发送文件 Binary 数据
                    var formData = new FormData();
                    formData.append(fileObj.file.name, fileObj.file);

                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", window.basePath + "/file/FileItem/upload_byteIn"/* , async, default to true */);
                    xhr.overrideMimeType("application/octet-stream");

                    xhr.setRequestHeader("____id", fileObj.fileId);
                    xhr.setRequestHeader("___name", fileObj.file.name);
                    xhr.setRequestHeader("___size", fileObj.file.size);
                    var _uploadProgress = function(evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = Math.round(evt.loaded * 100 / evt.total);
                            console.log(percentComplete, '%');
                            setLoadingPercentAnimate(ctx, percentComplete, width, height);
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
                            setLoadingPercentAnimate(ctx, 100, width, height, function() {
                                console.log('send end msg!', $scope.id)
                                $scope.$emit('/uploadFile_item_upload_end', {
                                    fileId : $scope.id,
                                    isError : isError,
                                    resp : resp
                                });
                            });
                        }
                    }
                }

                $scope.setData = function(obj) {
                    $scope[obj.key] = obj.value;
                }
                window.exportUiApi($scope, [ 'setData', 'uploadFile' ]);
            }
        }
    })

    window.app.directive('fileUpload', function() {
        return {
            restrict : 'AE',
            transclude : true,
            replace : true,
            scope : {
                id : '@'
            },
            templateUrl : '/temp/fileUpload.html',
            controller : function($scope, $element, $compile, $popWindow) {
                if ($scope.id == null) {
                    $scope.id = 'fileupload_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }
                $scope.files = [];
                $scope.fileDefMap = {};
                $scope.fileSelect = function(fileEl) {
                    if (typeof FileReader == 'undefined') {
                        malert('你的浏览器不支持FileReader接口！');
                        return;
                    }
                    if (fileEl.files) {
                        $.each(fileEl.files, function(k, file) {
                            var s = window.genSizeStr(file.size);
                            // console.log('xxx', s);
                            var readerLoc = new FileReader();
                            readerLoc.readAsDataURL(file);// base64的方式
                            // 首先本地显示文件
                            readerLoc.onload = function(e) {
                                var url = this.result;
                                $scope.$apply(function() {
                                    var fileId = genUUID();
                                    var fitem = {
                                        _id : fileId,
                                        name : file.name,
                                        size : s,
                                        url : url,
                                        $$uploading : true,
                                        isImage : window.isImageFile(file.name)
                                    };
                                    $scope.files.unshift(fitem);
                                    $scope.fileDefMap[fileId] = $scope.files[0];
                                    $scope.fileMap[fileId] = file;
                                });
                            }
                        });
                    }
                }
                // 等到item的canvas加载完毕
                $scope.fileMap || ($scope.fileMap = {});
                $scope.$on('/uploadFile_item_ok', function(event, fileId) {
                    if ($scope.fileMap[fileId] != null) {
                        window.reqUIAction($scope, fileId + '/uploadFile', {
                            file : $scope.fileMap[fileId],
                            fileId : fileId
                        });
                    }
                });
                // 上传结束
                $scope.$on('/uploadFile_item_upload_end', function(event, obj) {
                    // console.log('up_ok:', obj.fileId, $scope.fileDefMap[obj.fileId]);
                    (function() {
                        $scope.$apply(function() {
                            $scope.fileDefMap[obj.fileId].$$uploading = false;
                            // 将服务器的图片替换本地图片
                            // /services/api/file/FileItem/showImg_byteOut?wh=300_0&_id=2D127344-373C-4BD2-A615-67E0DB84930F
                            var imgUrl = window.basePath + '/file/FileItem/showImg_byteOut?wh=300_300&_id=' + obj.fileId;
                            console.log('imgUrl: ' + imgUrl);
                            $scope.fileDefMap[obj.fileId].url = imgUrl;
                        });
                    }).delay(1);
                });

                // 放大图片
                $scope.full = function(item) {
                    window.fullImg(item.url);
                }
                $scope.getSelected = function() {
                    var s = [];
                    $.each($scope.files, function(k, v) {
                        if (v.$$selected) {
                            s.push(v);
                        }
                    })

                    window.reqUIAction($scope, $scope.id + '/getSelected_return', s);
                }
                window.exportUiApi($scope, [ 'getSelected' ]);
            },

            compile : function($element, attrs, transclude) {
                return function($scope, $element, attrs, ctrl, transclude) {
                    window.reqUIAction($scope, $scope.id + '/init_end');
                };
            }
        }
    });
})(angular);