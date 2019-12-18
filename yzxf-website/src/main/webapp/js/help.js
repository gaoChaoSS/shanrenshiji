/**
 * Created by zq2014 on 17/1/18.
 */
(function () {
    $(document).ready(function () {
        $(".NavDiv a").eq(4).addClass("selected").siblings().removeClass("selected");

        $(".helpNav li").click(function (){
            $(this).addClass("helpBannerSelected").siblings().removeClass("helpBannerSelected");
            var name = $(this).attr("id");
            $("."+name).show().siblings().hide();
        });
        $(".helpNav li").eq(0).trigger('click');

        //$(document).scroll(function() {
        //    if($(document).scrollTop()>580){
        //        $(".helpNav").addClass("fix");
        //    } else {
        //        $(".helpNav").removeClass("fix");
        //    }
        //});

        $.getJSON("/s_member/api/crm/Article/showHelpList",function (data){
            var items = data.content.items;
            for(var i=0;i<items.length;i++){
                $(".helpNav").append("<div class='helpBannerTitle'>"+items[i].name+"</div>");
                for(var j=0;j<items[i].list.length;j++){
                    $(".helpNav").append("<li id="+items[i].list[j]._id+" onclick=loadArticle('"+items[i].list[j]._id+"',"+1+","+5+")>"+items[i].list[j].name+"</li>");
                }
            }
            loadArticle(items[0].list[0]._id,1,5);
        });

        window.loadArticle = function (pId,pageNo,pageSize){
            var scrollTop = $(document).scrollTop();
            if(scrollTop != 0){
                document.body.scrollTop = document.documentElement.scrollTop = 580;
            }
            $("#"+pId).addClass("helpBannerSelected").siblings().removeClass("helpBannerSelected");
            $.getJSON("/s_member/api/crm/Article/query?_pId="+pId+"&pageNo="+pageNo+"&pageSize="+pageSize,function (data){
                $(".helpCon").html("");
                $(".pages").html("");
                var items = data.content.items;
                var pages = data.content.totalPage;
                var totalNum = data.content.totalNum;
                for(var i=0;i<items.length;i++){
                    $(".helpCon").append("<div style='padding: 0 20px 30px 20px;'>"+
                        "<div class='helpConTitle'>"+items[i].title+"</div>"+
                        items[i].contents+
                        "</div>");
                }
                if(totalNum != 0){
                    for(var j=0;j<pages;j++){
                        $(".pages").append("<a onclick=loadArticle('"+pId+"',"+(j+1)+","+pageSize+")>"+(j+1)+"</a>");
                    }
                }
                $(".pages a").eq(pageNo-1).addClass("selectedPages").siblings().removeClass("selectedPages");
                hidePages(pageNo);
                if(items.length == 0){
                    $(".helpCon").append("<div style='text-align: center'><img style='width: 60%' src='../img/blankCon.png'></div>");
                }
            });
        };

        //页数隐藏
        window.hidePages = function(page){
            if($(".pages a").length>10){
                var totlePage = $(".pages a").length;
                var pageNum = parseInt(page);
                for(var b=0;b<$(".pages a").length;b++){
                    var text = $(".pages a").eq(b).text();
                    if(text != pageNum-2&&text != pageNum-1&&text != pageNum&&text != pageNum+1&&text != pageNum+2){
                        $(".pages a").eq(b).css("display", "none");
                    }
                }
                $(".pages a").eq(0).show();
                $(".pages a").eq(totlePage-1).show();
                if(pageNum-3>1){
                    $(".pages a").eq(0).after("<b>...</b>")
                }if(pageNum+3<totlePage){
                    $(".pages a").eq(totlePage-1).before("<b>...</b>")
                }
            }
        };
    })
})();