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
    window.app.directive('fileBrowser', function() {
        return {
            restrict : 'AE',
            transclude : true,
            replace : true,
            scope : {
                id : '@',
            },
            templateUrl : '/temp/fileBrowser.html',
            controller : function($scope, $element, $compile, $popWindow, $http) {
                console.log($scope.id, $element.attr('id'))
                if ($scope.id == null) {
                    $scope.id = 'file_browser_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }
                $scope.layoutId = $scope.id + "_layout";
                $scope.leftTreeId = $scope.id + "_left_tree";
                $scope.rightGridId = $scope.id + "_right_grid";

                $element.find('.layout').attr('id', $scope.layoutId);
                var endUI = 0;

                $scope.$on($scope.layoutId + '/init_end', function() {
                    endUI++;
                    if (endUI == 3) {
                        $scope.$root.$broadcast($scope.id + '/init_end');
                    }
                });

                // 树加载完成
                $scope.$on($scope.leftTreeId + '/init_end', function(event, obj) {
                    endUI++;
                    if (endUI == 3) {
                        $scope.$root.$broadcast($scope.id + '/init_end');
                    }

                    window.reqUIAction($scope, $scope.leftTreeId + '/itemExpended');
                    window.reqUIAction($scope, $scope.leftTreeId + '/itemClicked');
                });
                // 子对象主动发送消息过来
                $scope.$on($scope.leftTreeId + '/itemClicked_after', function(event, obj) {
                    // 查询grid
                    var data = {};
                    if (obj._id == null || obj._id == '-1') {
                        window.reqUIAction($scope, $scope.rightGridId + '/resetFilter');
                        window.reqUIAction($scope, $scope.rightGridId + '/query');
                    } else {
                        data.filter = {
                            _fileDirList : obj._id
                        }
                        window.reqUIAction($scope, $scope.rightGridId + '/genMDataAndQuery', data);

                    }
                });

                // 文件上传
                $scope.fileSelect = function(fileEl) {
                    if (typeof FileReader == 'undefined') {
                        malert('你的浏览器不支持FileReader接口！');
                        return;
                    }
                    if (fileEl.files) {
                        $.each(fileEl.files, function(k, file) {
                            if (!window.isImageFile(file.name)) {
                                malert('只能上传图片格式的文件');
                                return;
                            }
                            var s = window.genSizeStr(file.size);
                            // console.log('xxx', s);
                            var readerLoc = new FileReader();
                            readerLoc.readAsDataURL(file);// base64的方式
                            // 首先本地显示文件
                            readerLoc.onload = function(e) {
                                var url = this.result;
                                $scope.$apply(function() {
                                    var fileId = genUUID();
                                    var fileItem = {
                                        _id : fileId,
                                        name : file.name,
                                        size : s,
                                        icon : url,
                                        file : file,
                                        $$uploading : true,
                                        isImage : window.isImageFile(file.name)
                                    };
                                    window.reqUIAction($scope, $scope.rightGridId + '/getData', '$$items', function(obj) {
                                        obj || (obj = []);
                                        obj.firstAdd(fileItem);
                                        window.reqUIAction($scope, $scope.rightGridId + '/setData', {
                                            '$$items' : obj
                                        });
                                    });
                                    $scope.sendFileToServer(fileItem);
                                });

                            }
                        });
                    }
                }
                // 发送文件数据到服务器
                $scope.sendFileToServer = function(fileObj) {
                    console.log('-- start upload file:', fileObj);

                    // 构造 XMLHttpRequest 对象，发送文件 Binary 数据
                    var formData = new FormData();
                    formData.append(fileObj.file.name, fileObj.file);

                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", window.basePath + "/file/FileItem/upload_byteIn"/* , async, default to true */);
                    xhr.overrideMimeType("application/octet-stream");

                    xhr.setRequestHeader("____id", fileObj._id);
                    xhr.setRequestHeader("___name", encodeURIComponent(fileObj.file.name));
                    xhr.setRequestHeader("___size", fileObj.file.size);
                    var _uploadProgress = function(evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = Math.round(evt.loaded * 100 / evt.total);
                            console.log(percentComplete, '%');
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
                            if (isError) {
                                malert('上传失败!');
                                return;
                            }
                            $scope.$apply(function() {
                                window.reqUIAction($scope, $scope.rightGridId + '/getData', '$$items', function(obj) {
                                    obj || (obj = []);
                                    $.each(obj, function(k, v) {
                                        if (v._id == fileObj._id) {
                                            v.$$uploading = false;
                                            v.__selected = true;
                                            v.icon = '/s_img/' + encodeURIComponent(v.name) + '?_id=' + v._id + '&wh=300_300';
                                            return false;
                                        }
                                    });
                                    window.reqUIAction($scope, $scope.rightGridId + '/setData', {
                                        '$$items' : obj
                                    });
                                });
                            })
                        }
                    }
                }

                // grid加载完成
                $scope.$on($scope.rightGridId + '/init_end', function(event, obj) {
                    endUI++;
                    if (endUI == 3) {
                        $scope.$root.$broadcast($scope.id + '/init_end');
                    }

                    window.reqUIAction($scope, $scope.rightGridId + '/setData', {
                        'contentTemp' : '/temp/inc_grid_file.html',
                        'showEditBtn' : false,
                        'showShowTypeBtn' : false,
                        'hideSearch' : true,
                        'hideQueryBtn' : true,
                        'hideQueryBtnMore' : true,
                        'selectDataType' : $scope.selectDataType ? $scope.selectDataType : 'one',
                        'fixQueryItem' : function(v, header) {
                            if (window.isImageFile(v.name)) {
                                v.icon = '/s_img/' + encodeURIComponent(v.name) + '?_id=' + v._id + '&wh=300_300';
                            }
                            return v;
                        }
                    });
                    window.reqUIAction($scope, $scope.rightGridId + '/addTopBtn', {
                        name : '本地上传...',
                        icon : 'icon-upload',
                        click : function() {
                            // console.log('file input :', )
                            $element.find('input[type="file"]').click();
                        }
                    });
                    window.reqUIAction($scope, $scope.rightGridId + '/genMDataAndQuery', {
                        actionPath : window.basePath + '/file/FileItem',
                        filter : {
                            includeKeys : 'name,size,createTime'
                        }
                    });
                })

                exportUiApi($scope, [ 'setContent' ]);
            },
            compile : function(element, attrs, transclude) {
                return function($scope, $element, attrs, ctrl, transclude) {

                };
            }
        }
    });

})(angular);