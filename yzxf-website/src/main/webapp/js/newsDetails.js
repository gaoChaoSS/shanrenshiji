/**
 * Created by zq2014 on 17/1/18.
 */
(function () {
    $(document).ready(function () {
        $(".NavDiv a").eq(2).addClass("selected").siblings().removeClass("selected");

        var _id = window.location.search;
        if(_id != null){
            $.getJSON("/s_member/api/crm/Article/queryDetail"+_id,function (data){
                var item = data.content.items[0];
                $(".detailCon").append("<div class='detailTitle'>"+
                    item.title+
                "</div><div class='detailSource'>"+
                    "时间:"+new Date(item.createTime).showYFullTime()+"" +
                    "<span style='margin-left:20px'>来源:"+item.source+"</span>"+
                "</div>");
                for(var i=0;i<item.contents.length;i++){
                    var str = "<div class='detailText'>"+item.contents[i].desc;
                    if(item.contents[i].img!=null && item.contents[i].img!=''){
                        str +="<img src='/s_img/icon.jpg?_id="+item.contents[i].img+"'>";
                    }
                    $(".detailCon").append(str+"</div>");
                }
            });
        }
    })
})();