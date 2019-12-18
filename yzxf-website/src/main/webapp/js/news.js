/**
 * Created by zq2014 on 17/1/18.
 */
(function () {
    $(document).ready(function () {
        $(".NavDiv a").eq(2).addClass("selected").siblings().removeClass("selected");

        window.goPage = function(_id){
            window.open("/action/Article/show.html?_id="+_id);
        };


        $.getJSON("/s_member/api/crm/Article/queryFlexSlider", function (data) {
            var items = data.content.items;

            //var scrollTitle=[
            //    '著名经济学家、"消费资本论之父"陈瑜教授莅临省重建基金会指导',
            //    '泸州市消费养老项目媒体见面会',
            //    '四川省养老产业高峰论坛'
            //];
            var len = items.length;
            if(len==0){
                $("#flexSlider").hide();
                return;
            }
            //设置页面图片/页码
            for(var i= 0;i<len;i++){
                $(".rollPicture").append("<img src='/s_img/icon.jpg?_id="+items[i].pictureId+"' onclick=goPage('"+items[i]._id+"')>");
                if(len==1){
                    $(".rollNumber").hide();
                    break;
                }else{
                    $(".rollNumber").append("<div>"+(i+1)+"</div>");
                }
            }

            $(".scroll-title").text(items[0].title);

            if(len<=1){
                return;
            }else{
                $(".rollNumber>div").eq(0).addClass("currentRollNum");
            }

            //图片滚动
            $(".rollNumber>div").click(function () {
                var thisText = $(this).text();
                clearInterval(rollTime);
                a = thisText;
                rollTime = setInterval(function () {
                    if (a == len) {
                        a = 0;
                    }
                    roll($(".rollNumber>div").eq(a));
                    $(".scroll-title").text(items[a].title);
                    a++;
                }, 5000);
                roll($(this));
            });

            var a = 1;
            var rollTime = setInterval(function () {
                if (a == len) {
                    a = 0;
                }
                roll($(".rollNumber>div").eq(a));
                $(".scroll-title").text(items[a].title);
                a++;
            }, 5000);

            function roll($$this) {
                $$this.addClass("currentRollNum").siblings().removeClass("currentRollNum");
                var num = $$this.text() - 1;
                $(".rollPicture img").eq(num).prevAll().animate({left: "-100%"});
                $(".rollPicture img").eq(num).nextAll().animate({left: "+100%"});
                $(".rollPicture img").eq(num).animate({left: "0px"});
                $(".scroll-title").text(items[num].title);
            }
        });






        window.details = function (_id) {
            window.location.href="/newsDetails.jsp?_id=" + _id;
        };


        $(document).scroll(function () {
            if ($(document).scrollTop() > 580) {
                $(".newsNav").addClass("fix");
            } else {
                $(".newsNav").removeClass("fix");
            }
        });

        $.getJSON("/s_member/api/crm/Article/showNewsList", function (data) {
            var items = data.content.items;
            for (var i = 0; i < items.length; i++) {
                $(".newsNav").append("<li id=" + items[i]._id + " onclick=loadArticle('" + items[i]._id + "'," + 1 + "," + 5 + ")>" + items[i].name + "</li>")
            }
            loadArticle(items[0]._id, 1, 5);
        });

        window.pictureAttr = function (src) {
            var i = new Image();
            i.src = src;
            var rate = 0;
            if (i.width != 0 || i.height != 0) {
                if ((i.width / i.height) > (300 / 180)) {
                    rate = (180 / ((300 / i.width) * i.height)) * 100;
                } else {
                    rate = 100;
                }
            } else {
                rate = "";
            }
            return rate;
        };

        window.loadArticle = function (pId, pageNo, pageSize) {
            //if (pId != $(".newsNav li").eq(0).attr("id")) {
            //    $(".rollPicture").hide();
            //    $(".rollNumber").hide();
            //} else {
            //    $(".rollPicture").show();
            //    $(".rollNumber").show();
            //}
            var scrollTop = $(document).scrollTop();
            if (scrollTop != 0) {
                document.body.scrollTop = document.documentElement.scrollTop = 583;
            }
            $("#" + pId).addClass("newsNavSelect").siblings().removeClass("newsNavSelect");
            $.getJSON("/s_member/api/crm/Article/query?_pId=" + pId + "&pageNo=" + pageNo + "&pageSize=" + pageSize, function (data) {
                $("#articles").html("");
                $(".pages").html("");
                var items = data.content.items;
                var pages = data.content.totalPage;
                var totalNum = data.content.totalNum;
                for (var i = 0; i < items.length; i++) {
                    var rate = pictureAttr("/s_img/icon.jpg?_id=" + items[i].pictureId);
                    $(".line").next().append(
                        "<div class='newsArticle'>" +
                            "<div class=\"articlePicture\" style=\"background: url('/s_img/icon.jpg\?_id="+items[i].pictureId+"\&wh=650_0') 50% 0 no-repeat;background-position:center\">" +
                                //"<img src='/s_img/icon.jpg\?_id="+items[i].pictureId+"\&wh=650_0'/>" +
                            "</div>" +
                            "<div style='margin-left:350px;min-height: 120px;'>" +
                                "<div class='articleTitle'>" +
                                    "<a href='/action/Article/show.html?_id="+items[i]._id+"' target='_blank'>"+items[i].title+"</a>" +
                                    "<div class='articleSource'>发布于: " + new Date(items[i].createTime).showYFullTime() + "</div>" +
                                "</div>" +
                                "<div class='newsDesc'>" + items[i].desc + "</div>" +
                            "</div>" +
                        "</div>"
                    );
                }
                if (totalNum != 0) {
                    for (var j = 0; j < pages; j++) {
                        $(".pages").append("<a onclick=loadArticle('" + pId + "'," + (j + 1) + "," + pageSize + ")>" + (j + 1) + "</a>");
                    }
                }
                $(".pages a").eq(pageNo - 1).addClass("selectedPages").siblings().removeClass("selectedPages");
                hidePages(data.content);
                if (items.length == 0) {
                    $(".line").next().append("<div style='text-align: center'><img style='width: 100%' src='/img/blankCon.png'></div>");
                }

                //$("#createTime1").val(new Date ($("#createTime1").val()).showTime())
            });
        };
        //页数隐藏
        window.hidePages = function (item) {
            if ($(".pages a").length > 10) {
                var totlePage = $(".pages a").length;
                var pageNo = parseInt(item.pageNo);
                for (var b = 0; b < $(".pages a").length; b++) {
                    var text = $(".pages a").eq(b).text();
                    if (text != pageNo - 2 && text != pageNo - 1 && text != pageNo && text != pageNo + 1 && text != pageNo + 2) {
                        $(".pages a").eq(b).css("display", "none");
                    }
                }
                $(".pages a").eq(0).show();
                $(".pages a").eq(totlePage - 1).show();
                if (pageNo - 3 > 1) {
                    $(".pages a").eq(0).after("<b>...</b>");
                }

                if (pageNo + 3 < totlePage) {
                    $(".pages a").eq(totlePage - 1).before("<b>...</b>");
                    if(pageNo!=totlePage){

                    }
                }
            }
            if(item.pageNo!=1){
                $(".pages a").eq(0).before("<a onclick=loadArticle('" + item.items[0].pId + "'," + 1 + "," + item.pageSize + ")> &lt;&lt; </a>" +
                    "<a onclick=loadArticle('" + item.items[0].pId + "'," + (item.pageNo-1) + "," + item.pageSize + ")> &lt; </a>");
            }
            if(item.pageNo!=item.totalPage){
                $(".pages a").eq($(".pages a").length-1).after(
                    "<a onclick=loadArticle('" + item.items[0].pId + "'," + (item.pageNo+1) + "," + item.pageSize + ")> &gt; </a>"+
                    "<a onclick=loadArticle('" + item.items[0].pId + "'," + item.totalPage + "," + item.pageSize + ")> &gt;&gt; </a>");
            }

        };

        //获取顶部图片
        function getBanner() {
            $.getJSON("/s_member/api/account/User/getWebSiteBanner?type=6&serialNum=2", function (data) {
                $(".banner").append("<div class=\"bannerImg\" style=\"background: url('/s_img/icon.jpg\?_id="+data.content.items[0].icon+"') 50% 50% no-repeat;height: 400px;\"></div>")
            });
        }
        getBanner();
    })
})();