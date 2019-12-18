(function() {
    $.fn.initCloseBtn = function() {
        $(this).click(function() {
            $(this).parent().parent().hide();
            $('#allConMark').hide();
        });
    }

    window.fullImg = function(src, list) {
        window.fullImgList = list;
        window.fullImgList || (window.fullImgList = []);
        if (window.fullImgList.length == 0) {
            window.fullImgList.push(src);
        }
        $.each(window.fullImgList, function(k, v) {
            if (src == v) {
                window.fullImgIndex = k;
            }
        });
        window.closePop = window.fullImgExist;
        $('#allConMark').show();
        var fullImageCon = $('#fullImageCon');
        if (fullImageCon.size() == 0) {
            fullImageCon = $('<div id="fullImageCon"></div>');
            fullImageCon.append('<div class="content"></div>')
            $('body').append(fullImageCon);
        }

        var fullImageLoadCon = $('#fullImageLoadCon');
        if (fullImageLoadCon.size() == 0) {
            fullImageLoadCon = $('<img src="/images/loading.gif"/ id="fullImageLoadCon"/>');
            $('body').append(fullImageLoadCon);
        }
        fullImageLoadCon.show();

        var contentCon = fullImageCon.find('.content');
        var fullImageLeft = $('#fullImageLeft');
        if (fullImageLeft.size() == 0) {
            fullImageLeft = $('<div class="icon-arrow-left2" id="fullImageLeft"></div>');
            contentCon.append(fullImageLeft);
            fullImageLeft.click(function() {
                var srcGo = window.fullImgList[--window.fullImgIndex];
                showImg(srcGo);
            });
        }
        var fullImageRight = $('#fullImageRight');
        if (fullImageRight.size() == 0) {
            fullImageRight = $('<div class="icon-arrow-right" id="fullImageRight" style="padding-left:4px"></div>');
            contentCon.append(fullImageRight);
            fullImageRight.click(function() {
                var srcGo = window.fullImgList[++window.fullImgIndex];
                showImg(srcGo);
            });
        }
        var showImg = function(srcGo, isFrist) {
            $('#fullImageLeft').hide();
            $('#fullImageRight').hide();
            contentCon.find('img').remove();
            if (isFrist) {
                fullImageCon.showCenter(180, 180).show();
            }
            fullImageLoadCon.showCenter(25, 25).show();
            imgReady(srcGo, function(w, h) {
                fullImageCon.showCenter(w + 12, h + 12, true, function() {
                    var img = $('<img src="' + srcGo + '" style="width:100%;height:100%;"/>').hide();
                    contentCon.append(img);
                    img.click(window.fullImgExist);
                    fullImageLoadCon.hide();
                    img.fadeIn('fast', function() {
                        showLeftRightBtn();
                    });
                });
            });
        }

        var showLeftRightBtn = function() {
            if (fullImageLoadCon.is(':visible')) {
                return;
            }
            if (window.fullImgList.length > 0) {
                var leftBtnSize = 60;
                var top = (fullImageCon.height() - leftBtnSize) / 2;
                if (window.fullImgIndex > 0) {
                    $('#fullImageLeft').css({
                        left : 0,
                        top : top,
                        opacity : 0.9
                    }).fadeIn('fast');
                }
                if (window.fullImgIndex < (window.fullImgList.length - 1)) {
                    $('#fullImageRight').css({
                        left : fullImageCon.width() - leftBtnSize,
                        top : top,
                        opacity : 0.9
                    }).fadeIn('fast');
                }
            }
        }

        fullImageCon.mouseenter(function() {
            showLeftRightBtn();
        }).mouseleave(function() {
            $('#fullImageLeft').fadeOut('fast');
            $('#fullImageRight').fadeOut('fast');
        });
        showImg(src, true);
    }
    window.fullImgExist = function() {
        $('#fullImageCon').fadeOut(function() {
            $('#fullImageCon').hide();
            $('#fullImageLoadCon').hide();
            $('#allConMark').hide();
        });
    }
})();