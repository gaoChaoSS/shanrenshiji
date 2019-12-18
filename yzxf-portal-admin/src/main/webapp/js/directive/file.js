/**
 * 使用例子：(须注意先后顺序)
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";

    var sizeUnit = ['B', 'K', 'M', 'G', 'T'];
    //获取文件的大小
    window.genSizeStr = function (size, unitIndex) {
        unitIndex = unitIndex == null ? 0 : unitIndex;
        if (size > 1024 && unitIndex < sizeUnit.length) {
            return window.genSizeStr(size / 1024, unitIndex + 1);
        } else {
            console.log(unitIndex, sizeUnit[unitIndex], size)
            return size.toFixed(2) + sizeUnit[unitIndex];
        }
    }
    //判断是否为图片
    window.isImageFile = function (fileName) {
        if (fileName == null) {
            return false;
        }
        var s = /\.(gif|jpg|jpeg|png)$/i.test(fileName.toLowerCase());
        return s;
    }


    window.app.directive('myfile', function () {
            return {
                restrict: 'AE',
                transclude: true,
                replace: true,
                scope: {
                    id: '@',
                    model: '@',
                    entity: '@',
                    entityField: '@',
                    entityId: '@',
                    fileId: '=',
                    isEdit: '=',//是否可编辑
                    inputType: '@',//元数据类型
                    selectDataType: '@',
                    onlyImg: '@',
                    showType: '@'
                },
                templateUrl: '/temp/myfile.html?_v=' + window.angular_temp_version,
                controller: function ($rootScope, $scope, $element, $http, $q, $compile, $popWindow, $templateCache) {
                    if (window.isEmpty($scope.id)) {
                        $scope.id = 'file_' + getRandom(100000000);
                        $element.attr('id', $scope.id);
                    }
                    $scope.isMultiple = $scope.inputType == 'fileMore';
                    $scope.onlyImg = $scope.onlyImg == 'true';
                    $scope.fileOptions || ($scope.fileOptions = []);
                    $scope.selectDataType || ($scope.selectDataType = 'none');

                    if ($scope.isMultiple) {
                        var url = window.basePath + '/file/FileEntityLink/query?orderBy=createTime&orderByAsc=-1&_entityName=' + $scope.entity + '&_entityField=' + $scope.entityField + '&_entityId=' + $scope.entityId;
                        $http.get(url).success(function (re) {
                            if (re.content.items != null) {
                                $.each(re.content.items, function (k, v) {
                                    var options = {fileId: v.fileId};
                                    options.entityName = $scope.entity;
                                    options.entityField = $scope.entityField;
                                    options.entityId = $scope.entityId;
                                    options.isImg = true;
                                    options.$$url = '/s_img/icon.jpg?_id=' + v.fileId + '&wh=300_300';
                                    $scope.fileOptions.push(options);
                                })
                            }
                        });
                    } else {
                        if (!window.isEmpty($scope.fileId)) {
                            var options = {fileId: $scope.fileId};
                            options.entityName = $scope.entity;
                            options.entityField = $scope.entityField;
                            options.entityId = $scope.entityId;
                            options.isImg = true;
                            options.$$url = '/s_img/icon.jpg?_id=' + $scope.fileId + '&wh=300_300';
                            $scope.fileOptions.push(options);
                        }
                    }


                    /**
                     * 传送图片到服务器
                     * @param options
                     */
                    var sendFileToSerer = function (options) {
                        // console.log('---data:', data);
                        //var formData = new FormData();
                        //formData.append(name, data);
                        //fileId, name, entityName, entityField, entityId, data

                        var xhr = new XMLHttpRequest();
                        xhr.open("POST", window.basePath + "/file/FileItem/uploadBase64"/* , async, default to true */);
                        xhr.overrideMimeType("application/octet-stream");

                        //xhr.setRequestHeader("____id", fileId);
                        //xhr.setRequestHeader("___name", encodeURIComponent(name));
                        //xhr.setRequestHeader("___size", data.len);

                        options || (options = {});
                        if (window.isEmpty(options.entityName)) {
                            window.hideLoading();
                            malert('必须设置entityName')
                            return;
                        }
                        if (window.isEmpty(options.entityField)) {
                            window.hideLoading();
                            malert('必须设置entityField')
                            return;
                        }
                        if (window.isEmpty(options.entityId)) {
                            window.hideLoading();
                            malert('必须设置entityId')
                            return;
                        }

                        var sendData = options.fileId + '/' + encodeURIComponent(options.name) + '/' + options.entityName + '/' + options.entityField + '/' + options.entityId + ',' + options.$$url;
                        console.log('data: ', sendData);
                        var _uploadProgress = function (evt) {
                            if (evt.lengthComputable) {
                                var percentComplete = Math.round(evt.loaded * 100 / evt.total);
                                console.log(percentComplete, '%');
                                //setLoadingPercentAnimate(ctx, percentComplete, width, height);
                            } else {
                                console.log('not:', evt.loaded, evt.total);
                            }
                        }
                        var _uploadFailed = function uploadFailed(evt) {
                            console.log("upload file error.");
                            window.hideLoading();
                            if (options.callError) {
                                options.callError(options, evt);
                            }
                        }
                        var _uploadCanceled = function uploadCanceled(evt) {
                            console.log("upload file by user cancel");
                            window.hideLoading();
                            malert('文件上传被取消!');
                        }
                        xhr.upload.addEventListener("progress", _uploadProgress, false);
                        xhr.addEventListener("error", _uploadFailed, false);
                        xhr.addEventListener("abort", _uploadCanceled, false);
                        xhr.send(sendData);
                        xhr.onreadystatechange = function () {
                            if (xhr.readyState == 4) {
                                window.hideLoading();
                                var isError = false, resp = xhr.responseText;
                                if (xhr.status == 200) {

                                    if (options.callSuccess) {
                                        options.callSuccess(options, resp);
                                    }
                                } else {
                                    malert('文件上传错误!');
                                    if (options.callError) {
                                        options.callError(options, resp);
                                    }
                                }
                            }
                        }
                    }

                    var uploadSuccess = function (options, resp) {
                        $scope.$apply(function () {
                            if (!window.isEmpty(options.oldFileId)) {
                                $scope.delItem(0, function () {
                                });
                            }

                            $scope.fileId = options.fileId;
                            //$scope.fileOptions[options.index]
                            options.complate = true;
                            options.success = true;
                            (function () {
                                $scope.$apply(function () {
                                    if (options && options.complate) {
                                        options.complate = false;
                                    }
                                });
                            }).delay(2);
                            $rootScope.$broadcast('/uploadSuccess', options);
                        });

                    }
                    var uploadError = function (options, resp) {
                        $scope.$apply(function () {
                            $scope.fileId = options.fileId;
                            options.complate = true;
                            options.success = false;
                            $rootScope.$broadcast('/uploadError', options);
                        });
                    }
                    $scope.selectItem = function (item) {
                        if ($scope.selectDataType == 'none') {
                            return false;
                        } else if ($scope.selectDataType == 'more') {
                            $scope.selectIdMap || ($scope.selectIdMap = {});
                            $scope.selectIdMap[item.fileId] = !$scope.selectIdMap[item.fileId];
                        } else if ($scope.selectDataType == 'one') {
                            if ($scope.selectIdMap != null && $scope.selectIdMap[item.fileId] != null) {
                                $scope.selectIdMap[item.fileId] = !$scope.selectIdMap[item.fileId];
                            } else {
                                $scope.selectIdMap = {};
                                $scope.selectIdMap[item.fileId] = true;
                            }
                        }
                    }

                    $scope.uploadFile = function (fileObj) {

                        var options = {
                            files: fileObj.files,
                            callSuccess: uploadSuccess,
                            callError: uploadError
                        };

                        if (options.files.length == 0) {
                            malert('请先选择文件');
                            return;
                        }
                        if (options.files.length > 1 && !$scope.isMultiple) {
                            malert('一次只允许上传1个文件!');
                            return;
                        }

                        options.entityName = $scope.entity;
                        options.entityField = $scope.entityField;
                        options.entityId = $scope.entityId;

                        $.each(options.files, function (k, file) {
                            var d = {};
                            $.extend(true, d, options);
                            d.fileId || (d.fileId = genUUID());
                            var fileName = file.name;
                            d.name = fileName;
                            d.size = file.name;
                            d.sizeStr = genSizeStr(file.size);

                            $scope.$apply(function () {
                                if (!$scope.isMultiple) {
                                    if ($scope.fileOptions.length > 0) {
                                        d.oldFileId = $scope.fileOptions[0].fileId;
                                    }
                                    $scope.fileOptions = [];
                                    $scope.fileOptions.push(d);
                                } else {
                                    d.index = $scope.fileOptions.length;
                                    $scope.fileOptions.push(d);
                                }
                            });

                            d.isImg = window.isImageFile(fileName);
                            if (d.isImg && $scope.onlyImg) {
                                malert('只能上传图片格式文件,如:jpg,jpeg,gif,png');
                                return true;
                            }
                            if (file.size > 1024 * 1024 * 5) {
                                malert('文件大小不能超过5m');
                                return true;
                            }
                            window.showLoading();

                            if (d.isImg) {
                                //压缩图片,最大宽度750
                                d.width || ( d.width = 750);
                                var resize = function () {
                                    canvasResize(file, {
                                        width: d.width,
                                        height: 0,
                                        crop: false,
                                        quality: 90,
                                        // rotate: 90,
                                        callback: function (imgData, w, h) {
                                            //alert(w + ':' + h);
                                            //console.log('imgData:' + );
                                            //sendFileToSerer(fileObj.fileId, fileObj.file.name, type, window.dataURLtoBlob(data));
                                            d.$$url = imgData;
                                            sendFileToSerer(d);
                                        }
                                    });
                                }
                                if (window.canvasResize == null) {
                                    $script(['/js/binaryajax.js', '/js/canvasResize.js', '/js/exif.js'], function () {
                                        resize();
                                    });
                                } else {
                                    resize();
                                }
                            } else {
                                //TODO 上传文件的情况
                            }
                        });
                    }
                    $scope.delItem = function (index, callback) {
                        var fileId = $scope.fileOptions[index].fileId;
                        var url = window.basePath + '/file/FileItem/delFileLink';
                        $http.post(url, {fileId: fileId}).success(function (re) {
                            if (callback) {
                                callback();
                            } else {
                                $rootScope.$broadcast('/delFileItem', $scope.fileOptions[index]);
                                malert('删除成功!');
                                $scope.fileOptions.splice(index, 1);
                            }
                        });
                    }

                    $scope.$on($scope.id, function (event, obj) {
                        if (obj.action == 'setNewData') {
                            $scope.fileOptions = [];
                        }
                    })

                    window.exportUiApi($scope, ['setOptions']);
                    $rootScope.$broadcast($scope.id + '/init_end');
                },

                compile: function (element, attrs, transclude) {
                    return function ($scope, element, attrs, ctrl, transclude) {
                    };
                }
            }
        }
    );

})(angular);