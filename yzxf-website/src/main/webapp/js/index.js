/**
 * Created by zq2014 on 17/1/18.
 */
(function () {
    window.onload=function(){
        $(".NavDiv a").eq(0).addClass("selected").siblings().removeClass("selected");


        var banner = [
            //{img:'/img/bannerImg.jpg',url:''},
            //{img:'/img/bannerImg2.jpg',url:''},
        ];
        var curIndex = 0;
        var imgWidth = 2560;
        //若不是window系统
        if(navigator.userAgent.indexOf("Windows",0) == -1){
            imgWidth = window.screen.width;
        }
        //判断是否是手机端
        if ((navigator.userAgent).match(/AppleWebKit.*Mobile.*/)) {
            $(".bannerImg").css("height", '310px');
            imgWidth = 980;
        }

        function getWebSiteBanner() {
            $.getJSON("/s_member/api/account/User/getWebSiteBanner?type=5", function (data) {
                banner = data.content.items;
                //设置页面图片/页码
                var len = banner.length;

                for (var i = 0; i < len; i++) {
                    $(".bannerImg").append("<img src='/s_img/icon.jpg\?_id=" + banner[i].icon + "' onclick=window.open('" + banner[i].entityId + "')>");
                    $(".bannerImg").css({height:$(window).height()-90});
                    $(".bannerImg img").eq(i).css({width:imgWidth});
                    //console.log("img:"+$(".bannerImg img").eq(i).height()+",window:"+parseInt($(window).height()-90));
                    if(parseInt($(".bannerImg img").eq(i).height())<parseInt($(window).height()-90)){
                        $(".bannerImg img").eq(i).css({width:imgWidth+'px',height:$(window).height()-90});
                    }
                }


                $(".bannerImg img").eq(0).animate({left: -(imgWidth - $(window).width()) / 2 + 'px'});
                if ($(window).width() > imgWidth) {
                    $(".bannerImg img").eq(0).nextAll().hide();
                }

                //图片滚动
                var rollTime = setInterval(function () {
                    if (curIndex == len) {
                        curIndex = 0;
                    }
                    roll();
                    curIndex++;
                }, 5000);
            });
        }

        var roll = function() {
            var width=$(window).width();
            var left=(imgWidth-width)/2;
            if(width>imgWidth){
                $(".bannerImg img").eq(curIndex).fadeIn(100);
                $(".bannerImg img").not($(".bannerImg img").eq(curIndex)).fadeOut(100);
            }
            $(".bannerImg img").eq(curIndex).prevAll().animate({left: -(imgWidth+parseInt(left))+'px'});
            $(".bannerImg img").eq(curIndex).nextAll().animate({left: imgWidth+parseInt(left)+'px'});
            $(".bannerImg img").eq(curIndex).animate({left: -left+'px'});

        };

        getWebSiteBanner();


        //初始化标签位置
        var tapList = new Array(7);
        var lastTime= new Date().getTime();
        //初始化每个模块的高度
        var initModHeight=function(){
            var cur = $(window).height();
            //不处理第一个和最后一个
            for (var i = 1,len = tapList.length-1; i < len; i++) {
                var modHeight = parseFloat($("#mod"+i+">div").css("height"));
                if(cur>=modHeight){
                    $("#mod"+i).css("height",cur+"px");
                    $("#mod"+i).css("padding",(cur-modHeight)/2.0+"px 0");
                    //console.log("mod"+i+"===height:"+cur+",padding:"+(cur-modHeight)/2.0);
                }
            }
            //处理最后一个
            var last1=parseFloat($("#mod6>div").eq(0).children("div").css("height"));
            var last2=parseFloat($("#mod6>div").eq(1).css("height"));
            if(cur>=(last1+last2)){
                $("#mod6").css("height",cur+"px");
                $("#mod6 div").eq(0).css("height",cur-last2+"px");
                $("#mod6 div").eq(0).css("padding",(cur-last2-last1)/2.0+"px 0");
                //console.log("mod6===last1:"+last1+",last2:"+last2+",height:"+(cur-last2)+",padding:"+(cur-last2-last1)/2.0);
            }
        };
        initModHeight();
        //滚动动画,只有第一次滚动的时候有特效
        var animateDown=function(){
            var cur = $(window).scrollTop();
            for (var i = 0,len = tapList.length-1; i < len; i++) {
                if((cur>=tapList[i] && cur<tapList[i+1])){
                    $("#mod"+(parseInt(i)+1)+">div").addClass('scroll-animate1');
                    clearAnimateDown(parseInt(i)+1);
                    break;
                }
            }
        };

        //清除动画
        var clearAnimateDown=function(index){
            for (var i = 0,len = tapList.length-1; i < len; i++) {
                if(i!=index){
                    $("#mod"+i+">div").removeClass('scroll-animate1');
                }
            }
            setTimeout(function(){
                $("#mod"+index+">div").removeClass('scroll-animate1');
            },1500);
        };

        //判断滚动位置
        var checkPosition=function(num){
            var cur = $(window).scrollTop();
            var len = tapList.length;
            if(window.isEmpty(tapList[len-1]) || tapList[len-1]==0){
                for (var i = 0; i < len; i++) {
                    tapList[i] = $('#mod' + i).offset().top;
                    //console.log(i+":"+tapList[i]);
                }
            }

            for (i = 0; i < len; i++) {
                if(i!=len-1 && cur>=tapList[i] && cur<tapList[i+1]){
                    if(i+num<0){
                        return tapList[0];
                    }else if(i+num>=len){
                        return tapList[len-1];
                    }else{
                        return tapList[i+num];
                    }
                }else if(i==len-1){
                    return tapList[len-1+num];
                }
            }
        };

        //滚动事件
        $(document).on("mousewheel DOMMouseScroll", function (e) {
            var curTime = new Date().getTime();
            //console.log("curTime:"+curTime+"___lastTime:"+lastTime+"_____=="+(curTime-lastTime));
            if(curTime-lastTime<1000){//n秒钟内最多滚动一次
                e.preventDefault();//清除默认事件
                return;
            }
            lastTime=curTime;
            var delta = (e.originalEvent.wheelDelta && (e.originalEvent.wheelDelta > 0 ? 1 : -1)) ||  // chrome & ie
                (e.originalEvent.detail && (e.originalEvent.detail > 0 ? -1 : 1));              // firefox
            if (delta > 0) {// 向上滚
                $("html,body").animate({scrollTop:checkPosition(-1)},500);
            } else if (delta < 0) {// 向下滚
                $("html,body").animate({scrollTop:checkPosition(1)},500);
                animateDown();
            }
        });

        //改变屏幕大小
        $(window).resize(function() {
            initModHeight();
        });

        //默认滚动到最顶部
        $("html,body").animate({scrollTop:0},1000);


        $("#video").bind('ended', function () {
            $("#videoDiv").slideToggle();
        });
        $(".closeIcon").click(function () {
            $("#video")[0].pause();
            $("#videoDiv").slideToggle();
        });
        if ($("#videoSession").text() == "false") {
            $("#video").attr("src", "");
            $("#videoDiv").remove();
        }

    }
})();