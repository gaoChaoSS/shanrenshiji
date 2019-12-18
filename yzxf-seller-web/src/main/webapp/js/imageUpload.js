(function () {
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
        var sendData = options.fileId + '/' + encodeURIComponent(options.name) + '/' + options.entityName + '/' + options.entityField + '/' + options.entityId + ',' + options.data;
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
    /**
     * options.files:文件对象数组
     * options.name:文件名
     * options.entityName:实体名,即表名
     * options.entityField:实体字段
     * options.entityId:实体主键值
     * options.width:图片压缩的宽度,默认750
     * options.callSuccess:上传成功的回调
     * options.callError:上传失败的回调
     * @param options
     */
    window.uploadFile = function () {
        var options = window.uploadWinObj;
        if (options == null) {
            malert('upload 配置错误');
            return;
        }
        var file = {};
        if (options.files.length == 0) {
            malert('请先选择文件');
            return;
        } else if (options.files.length > 1 && options.one) {
            malert('一次只允许上传1个文件!');
            return;
        } else {
            options.fileId = genUUID();
            file = options.files[0];
        }

        console.log(file);
        if (!window.isImageFile(file.name)) {
            malert('不是一个有效的图片文件格式!,仅支持jpg,jpeg,gif,png格式图片');
            return;
        }

        options.name = file.name;

        if (file.size > 1024 * 1024 * 5) {
            malert('文件大小不能超过5m');
            return;
        }

        window.showLoading();
        //压缩图片,最大宽度750
        options.width || ( options.width = 750);
        canvasResize(file, {
            width: options.width,
            height: 0,
            crop: false,
            quality: 80,
            // rotate: 90,
            callback: function (data, w, h) {
                //alert(w + ':' + h);
                //console.log('imgData:' + );
                //sendFileToSerer(fileObj.fileId, fileObj.file.name, type, window.dataURLtoBlob(data));
                options.data = data;
                sendFileToSerer(options);
            }
        });
    }
    window.uploadFileBig = function () {
        var options = window.uploadWinObj;
        if (options == null) {
            malert('upload 配置错误');
            return;
        }
        var file = {};
        if (options.files.length == 0) {
            malert('请先选择文件');
            return;
        } else if (options.files.length > 1 && options.one) {
            malert('一次只允许上传1个文件!');
            return;
        } else {
            options.fileId = genUUID();
            file = options.files[0];
        }

        console.log(file);
        if (!window.isImageFile(file.name)) {
            malert('不是一个有效的图片文件格式!,仅支持jpg,jpeg,gif,png格式图片');
            return;
        }

        options.name = file.name;

        if (file.size > 1024 * 1024 * 5) {
            malert('文件大小不能超过5m');
            return;
        }

        window.showLoading();
        //压缩图片,最大宽度750
        options.width || ( options.width = 2560);
        canvasResize(file, {
            width: options.width,
            height: 0,
            crop: false,
            quality: 80,
            // rotate: 90,
            callback: function (data, w, h) {
                //alert(w + ':' + h);
                //console.log('imgData:' + );
                //sendFileToSerer(fileObj.fileId, fileObj.file.name, type, window.dataURLtoBlob(data));
                options.data = data;
                sendFileToSerer(options);
            }
        });
    }

    /** 下面是拖拽文件上传的实现 **/
    if (!window.isInitUpload) {
        window.isInitUpload = true;
        if ($('#dropUploadCon').size() == 0) {
            $('body').append('<div id="dropUploadCon">拖拽文件到此区域...</div>');
        }

        window.addEventListener("drag", function (event) {
            console.log('drag');
        });

        window.addEventListener("dragenter", function (e) {
            console.log('dragenter');
            if (window.uploadWinObj != null) {
                $('#dropUploadCon').show();
            }
            e.stopPropagation();
            e.preventDefault();
            return false;
        }, false);
        window.addEventListener("dragover", function (e) {
            //$('#dropUploadCon').show()
            console.log('dragover');
            e.stopPropagation();
            e.preventDefault();
            return false;
        }, false);


        $('#dropUploadCon')[0].addEventListener("dragleave", function (e) {
            console.log('dragleave');
            if (window.uploadWinObj != null) {
                $('#dropUploadCon').hide();
            }
            e.stopPropagation();
            e.preventDefault();
            return false;
        }, false);
        //拖动进入
        window.addEventListener("drop", function (e) {
            console.log('drop');
            if (window.uploadWinObj != null) {
                $('#dropUploadCon').hide();
                console.log(e.dataTransfer.files);
                window.uploadWinObj.files = e.dataTransfer.files;
                window.uploadFile();
            }
            e.stopPropagation();
            e.preventDefault();
            //禁止浏览器默认行为
            return false;//禁止浏览器默认行为
        }, false);
        //拖动结束
        window.addEventListener("dragend", function (event) {
            console.log('dragend');

            e.stopPropagation();
            e.preventDefault();
            return false;
        }, false);
    }
})();