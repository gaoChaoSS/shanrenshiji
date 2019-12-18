/**
 * Created by zq2014 on 17/1/18.
 */
(function () {
    $(document).ready(function () {
        $(".NavDiv a").eq(1).addClass("selected").siblings().removeClass("selected");

        //初始化标签位置
        var tapList = new Array(7);
        //滚动标签
        $(document).scroll(function () {
            var articleBottom = $('.footerCon').offset().top - 400;
            var cur = $(window).scrollTop();
            for (var i = 0, len = tapList.length; i < len; i++) {
                tapList[i] = $('#article' + i).offset().top-50;
            }
            //当滚到第一个标签位置时,改变左侧导航栏定位
            if (cur >= (tapList[0]) && cur <= articleBottom) {
                $('#mod-nav').addClass("mod-nav-fixed");
                $('#mod-nav').css('left', $('.mod-article').offset().left - 250 + 'px');
            } else {
                $('#mod-nav').removeClass("mod-nav-fixed");
                $('#mod-nav').css('left', 0);
            }

            for (var i = 0, len = tapList.length; i < len; i++) {
                if ((i != len - 1 && cur >= tapList[i] && cur < tapList[i + 1]) || (i == len - 1 && cur >= tapList[i])) {
                    $('#mod-nav>div').eq(i).addClass("nav-selected");
                } else {
                    $('#mod-nav>div').eq(i).removeClass("nav-selected");
                }
            }
        });
        $(window).resize(function() {
            $('#mod-nav').css('left', $('.mod-article').offset().left - 250 + 'px');
        });

        $(".mod-nav>div").click(function (){
            var index= $(this).index();
            //$(document).scrollTop(tapList[index]+3);
            $("html,body").animate({scrollTop:tapList[index]},500);
        });

        //获取顶部图片
        function getBanner() {
            $.getJSON("/s_member/api/account/User/getWebSiteBanner?type=6&serialNum=1", function (data) {
                $(".banner").append("<div class=\"bannerImg\" style=\"background: url('/s_img/icon.jpg\?_id="+data.content.items[0].icon+"') 50% 50% no-repeat;height: 400px;\"></div>")
            });
        }
        getBanner();
    })
})();