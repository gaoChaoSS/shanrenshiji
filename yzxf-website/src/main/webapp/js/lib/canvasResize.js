/*
 * 
 * canvasResize
 * 
 * Version: 1.2.0 
 * Date (d/m/y): 02/10/12
 * Update (d/m/y): 14/05/13
 * Original author: @gokercebeci 
 * Licensed under the MIT license
 * - This plugin working with binaryajax.js and exif.js 
 *   (It's under the MPL License http://www.nihilogic.dk/licenses/mpl-license.txt)
 * Demo: http://canvasResize.gokercebeci.com/
 * 
 * - I fixed iOS6 Safari's image file rendering issue for large size image (over mega-pixel)
 *   using few functions from https://github.com/stomita/ios-imagefile-megapixel
 *   (detectSubsampling, )
 *   And fixed orientation issue by using https://github.com/jseidelin/exif-js
 *   Thanks, Shinichi Tomita and Jacob Seidelin
 */

(function ($) {
    var pluginName = 'canvasResize',
        methods = {
            newsize: function (w, h, W, H, C) {
                var c = C ? 'h' : '';
                if ((W && w > W) || (H && h > H)) {
                    var r = w / h;
                    if ((r >= 1 || H === 0) && W && !C) {
                        w = W;
                        h = (W / r) >> 0;
                    } else if (C && r <= (W / H)) {
                        w = W;
                        h = (W / r) >> 0;
                        c = 'w';
                    } else {
                        w = (H * r) >> 0;
                        h = H;
                    }
                }
                return {
                    'width': w,
                    'height': h,
                    'cropped': c
                };
            },
            dataURLtoBlob: function (data) {
                var mimeString = data.split(',')[0].split(':')[1].split(';')[0];
                var byteString = atob(data.split(',')[1]);
                var ab = new ArrayBuffer(byteString.length);
                var ia = new Uint8Array(ab);
                for (var i = 0; i < byteString.length; i++) {
                    ia[i] = byteString.charCodeAt(i);
                }
                var bb = (window.BlobBuilder || window.WebKitBlobBuilder || window.MozBlobBuilder);
                if (bb) {
                    //    console.log('BlobBuilder');
                    bb = new (window.BlobBuilder || window.WebKitBlobBuilder || window.MozBlobBuilder)();
                    bb.append(ab);
                    return bb.getBlob(mimeString);
                } else {
                    //    console.log('Blob');
                    bb = new Blob([ab], {
                        'type': (mimeString)
                    });
                    return bb;
                }
            },
            /**
             * Detect subsampling in loaded image.
             * In iOS, larger images than 2M pixels may be subsampled in rendering.
             */
            detectSubsampling: function (img) {
                var iw = img.width, ih = img.height;
                if (iw * ih > 1048576) { // subsampling may happen over megapixel image
                    var canvas = document.createElement('canvas');
                    canvas.width = canvas.height = 1;
                    var ctx = canvas.getContext('2d');
                    ctx.drawImage(img, -iw + 1, 0);
                    // subsampled image becomes half smaller in rendering size.
                    // check alpha channel value to confirm image is covering edge pixel or not.
                    // if alpha value is 0 image is not covering, hence subsampled.
                    return ctx.getImageData(0, 0, 1, 1).data[3] === 0;
                } else {
                    return false;
                }
            },
            /**
             * Update the orientation according to the specified rotation angle
             */
            rotate: function (orientation, angle) {
                var o = {
                    // nothing
                    1: {90: 6, 180: 3, 270: 8},
                    // horizontal flip
                    2: {90: 7, 180: 4, 270: 5},
                    // 180 rotate left
                    3: {90: 8, 180: 1, 270: 6},
                    // vertical flip
                    4: {90: 5, 180: 2, 270: 7},
                    // vertical flip + 90 rotate right
                    5: {90: 2, 180: 7, 270: 4},
                    // 90 rotate right
                    6: {90: 3, 180: 8, 270: 1},
                    // horizontal flip + 90 rotate right
                    7: {90: 4, 180: 5, 270: 2},
                    // 90 rotate left
                    8: {90: 1, 180: 6, 270: 3}
                };
                return o[orientation][angle] ? o[orientation][angle] : orientation;
            },
            /**
             * Transform canvas coordination according to specified frame size and orientation
             * Orientation value is from EXIF tag
             */
            transformCoordinate: function (canvas, width, height, orientation) {
                switch (orientation) {
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        canvas.width = height;
                        canvas.height = width;
                        break;
                    default:
                        canvas.width = width;
                        canvas.height = height;
                }
                var ctx = canvas.getContext('2d');
                switch (orientation) {
                    case 1:
                        // nothing
                        break;
                    case 2:
                        // horizontal flip
                        ctx.translate(width, 0);
                        ctx.scale(-1, 1);
                        break;
                    case 3:
                        // 180 rotate left
                        ctx.translate(width, height);
                        ctx.rotate(Math.PI);
                        break;
                    case 4:
                        // vertical flip
                        ctx.translate(0, height);
                        ctx.scale(1, -1);
                        break;
                    case 5:
                        // vertical flip + 90 rotate right
                        ctx.rotate(0.5 * Math.PI);
                        ctx.scale(1, -1);
                        break;
                    case 6:
                        // 90 rotate right
                        ctx.rotate(0.5 * Math.PI);
                        ctx.translate(0, -height);
                        break;
                    case 7:
                        // horizontal flip + 90 rotate right
                        ctx.rotate(0.5 * Math.PI);
                        ctx.translate(width, -height);
                        ctx.scale(-1, 1);
                        break;
                    case 8:
                        // 90 rotate left
                        ctx.rotate(-0.5 * Math.PI);
                        ctx.translate(-width, 0);
                        break;
                    default:
                        break;
                }
            },
            /**
             * Detecting vertical squash in loaded image.
             * Fixes a bug which squash image vertically while drawing into canvas for some images.
             */
            detectVerticalSquash: function (img, iw, ih) {
                var canvas = document.createElement('canvas');
                canvas.width = 1;
                canvas.height = ih;
                var ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0);
                var data = ctx.getImageData(0, 0, 1, ih).data;
                // search image edge pixel position in case it is squashed vertically.
                var sy = 0;
                var ey = ih;
                var py = ih;
                while (py > sy) {
                    var alpha = data[(py - 1) * 4 + 3];
                    if (alpha === 0) {
                        ey = py;
                    } else {
                        sy = py;
                    }
                    py = (ey + sy) >> 1;
                }
                var ratio = py / ih;
                return ratio === 0 ? 1 : ratio;
            },
            callback: function (d) {
                return d;
            },
            extend: function () {
                var target = arguments[0] || {}, a = 1, al = arguments.length, deep = false;
                if (target.constructor === Boolean) {
                    deep = target;
                    target = arguments[1] || {};
                }
                if (al === 1) {
                    target = this;
                    a = 0;
                }
                var prop;
                for (; a < al; a++)
                    if ((prop = arguments[a]) !== null)
                        for (var i in prop) {
                            if (target === prop[i])
                                continue;
                            if (deep && typeof prop[i] === 'object' && target[i])
                                methods.extend(target[i], prop[i]);
                            else if (prop[i] !== undefined)
                                target[i] = prop[i];
                        }
                return target;
            }
        },
        defaults = {
            width: 300,
            height: 0,
            crop: false,
            quality: 80,
            rotate: 0,
            'callback': methods.callback
        };

    function Plugin(file, options) {
        this.file = file;
        // EXTEND
        this.options = methods.extend({}, defaults, options);
        this._defaults = defaults;
        this._name = pluginName;
        this.init();
    }

    Plugin.prototype = {
        init: function () {
            //this.options.init(this);
            var $this = this;
            var file = this.file;

            var reader = new FileReader();
            reader.onloadend = function (e) {

                var dataURL = e.target.result;

                var byteString = atob(dataURL.split(',')[1]);
                var binary = new BinaryFile(byteString, 0, byteString.length);
                var exif = EXIF.readFromBinaryFile(binary);

                var img = new Image();
                img.onload = function (e) {

                    var orientation = exif['Orientation'] || 1;
                    orientation = methods.rotate(orientation, $this.options.rotate);

                    // CW or CCW ? replace width and height
                    var size = (orientation >= 5 && orientation <= 8)
                        ? methods.newsize(img.height, img.width, $this.options.width, $this.options.height, $this.options.crop)
                        : methods.newsize(img.width, img.height, $this.options.width, $this.options.height, $this.options.crop);

                    var iw = img.width, ih = img.height;
                    var width = size.width, height = size.height;

                    var canvas = document.createElement("canvas");
                    var ctx = canvas.getContext("2d");
                    ctx.save();
                    methods.transformCoordinate(canvas, width, height, orientation);

                    // over image size
                    if (methods.detectSubsampling(img)) {
                        iw /= 2;
                        ih /= 2;
                    }
                    var d = 1024; // size of tiling canvas
                    var tmpCanvas = document.createElement('canvas');
                    tmpCanvas.width = tmpCanvas.height = d;
                    var tmpCtx = tmpCanvas.getContext('2d');
                    var vertSquashRatio = methods.detectVerticalSquash(img, iw, ih);
                    var sy = 0;
                    while (sy < ih) {
                        var sh = sy + d > ih ? ih - sy : d;
                        var sx = 0;
                        while (sx < iw) {
                            var sw = sx + d > iw ? iw - sx : d;
                            tmpCtx.clearRect(0, 0, d, d);
                            tmpCtx.drawImage(img, -sx, -sy);
                            var dx = Math.floor(sx * width / iw);
                            var dw = Math.ceil(sw * width / iw);
                            var dy = Math.floor(sy * height / ih / vertSquashRatio);
                            var dh = Math.ceil(sh * height / ih / vertSquashRatio);
                            ctx.drawImage(tmpCanvas, 0, 0, sw, sh, dx, dy, dw, dh);
                            sx += d;
                        }
                        sy += d;
                    }
                    ctx.restore();
                    tmpCanvas = tmpCtx = null;

                    // if rotated width and height data replacing issue 
                    var newcanvas = document.createElement('canvas');
                    newcanvas.width = size.cropped === 'h' ? height : width;
                    newcanvas.height = size.cropped === 'w' ? width : height;
                    var x = size.cropped === 'h' ? (height - width) * .5 : 0;
                    var y = size.cropped === 'w' ? (width - height) * .5 : 0;
                    newctx = newcanvas.getContext('2d');
                    newctx.drawImage(canvas, x, y, width, height);

                    console.log(file, file.type);
                    if (file.type === "image/png") {
                        var data = newcanvas.toDataURL(file.type);
                    } else {
                        var data = newcanvas.toDataURL("image/jpeg", ($this.options.quality * .01));
                    }

                    // CALLBACK
                    $this.options.callback(data, newcanvas.width, newcanvas.height);

                    // });
                };
                img.src = dataURL;
                // =====================================================

            };
            reader.readAsDataURL(file);
            //reader.readAsBinaryString(file);

        }
    };
    $[pluginName] = function (file, options) {
        if (typeof file === 'string')
            return methods[file](options);
        else
            new Plugin(file, options);
    };

    window.dataURLtoBlob = methods.dataURLtoBlob;

    //压缩的方式上传图片
    window.isImageFile = function (fileName) {
        if (fileName == null) {
            return false;
        }
        var s = /\.(gif|jpg|jpeg|png)$/i.test(fileName.toLowerCase());
        return s;
    }

    /**
     * 上传一段base64的图片数据到服务器
     * @param options
     * @param callback
     */
    window.sendFileToSerer = function (options, callback) {

        var xhr = new XMLHttpRequest();
        xhr.open("POST", window.basePath + "/file/FileItem/uploadBase64_byteIn"/* , async, default to true */);
        xhr.overrideMimeType("application/octet-stream");

        xhr.setRequestHeader("____id", options.fileId);
        xhr.setRequestHeader("___name", encodeURIComponent(options.fileName));
        //xhr.setRequestHeader("___size", data.len);
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
        }
        var _uploadCanceled = function uploadCanceled(evt) {
            console.log("upload file by user cancel");

        }
        xhr.upload.addEventListener("progress", _uploadProgress, false);
        xhr.addEventListener("error", _uploadFailed, false);
        xhr.addEventListener("abort", _uploadCanceled, false);
        xhr.send(options.data);
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    console.log("upload complete");
                    console.log("response: " + xhr.responseText);
                    if (callback) {
                        callback(options);
                    }
                } else {
                    $('#dataLoading').hide();
                    $('#markLoading').hide();
                    malert('上传失败');
                    console.log("upload error");
                }
                //setLoadingPercentAnimate(ctx, 100, width, height, function () {
                //    console.log('send end msg!', $scope.id)
                //    $scope.$emit('/uploadFile_item_upload_end', {
                //        fileId: $scope.id,
                //        isError: isError,
                //        resp: resp
                //    });
                //});
            }
        }
    }

    /**
     * 本地上传一张图片
     * @param inputObj :上传文件的文件控件
     * @param options: 额外的数据,被回调函数使用
     * @param callback: 上传成功的回调函数
     */
    window.localUploadPhoto = function (inputObj, options, callback) {

        options || (options = {});
        if (inputObj.files.length == 0) {
            return;
        } else {
            options.fileId = genUUID();
            options.file = inputObj.files[0];
            options.fileName = options.file.name;
            options.fileSize = options.file.size;
        }

        console.log(options.file);
        if (!window.isImageFile(options.fileName)) {
            malert('不是一个有效的图片文件格式!,仅支持jpg,jpeg,gif,png格式图片');
            return;
        }
        if (options.fileSize > 1024 * 1024 * 5) {
            malert('文件大小不能超过5m');
            return;
        }

        window.showLoading();
        //压缩图片
        options.width || (options.width = 750);
        options.height || (options.height = 0);
        options.rotate || (options.rotate = 0);
        canvasResize(options.file, {
            width: options.width,
            height: options.height,
            crop: false,
            quality: 80,
            rotate: options.rotate,
            callback: function (data, w, h) {
                //alert(w + ':' + h);
                //console.log('imgData:' + );
                //sendFileToSerer(fileObj.fileId, fileObj.file.name, type, window.dataURLtoBlob(data));
                options.data = data;
                options.width = w;
                options.height = h;
                window.sendFileToSerer(options, callback);
            }
        });
        return;
    }

})(window);


