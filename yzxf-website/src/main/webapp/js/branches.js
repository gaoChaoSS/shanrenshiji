(function () {
    $(document).ready(function () {

        var tapList = [];

        function getBranches() {
            $.getJSON("/s_member/api/account/Factor/getBranches?isFormat=true&pageNo=1&pageSize=9999", function (data) {
                var cityList = data.content.items;
                var str = '';
                for(var i= 0,len=cityList.length;i<len;i++) {
                    var obj = $('<a id="top_'+i+'">'+cityList[i].cityName+'</a>');
                    $('#cityList').append(obj);


                    str += '<div class="serviceCon" id="city'+i+'">';
                    str += '<a class="serviceName">' + cityList[i].cityName + '</a>';
                    str += '    <div class="serviceItemList">';
                    for(var j= 0,jlen=cityList[i].branches.length;j<jlen;j++) {
                        str += '        <div class="serviceItem">';
                        str += '            <div class="serviceItemCon">';
                        str += '                <div class="title">' + cityList[i].branches[j].name + '</div>';
                        str += '                <div class="notHigh">联系电话：' + cityList[i].branches[j].mobile + '</div>';
                        str += '                <div class="notHigh">联系地址：' + cityList[i].branches[j].address + '</div>';
                        str += '            </div>';
                        str += '        </div>';
                    };
                    str += '    </div>';
                    str += '</div>';
                };
                $('#branchCompany').html(str);
                //$($('#cityList>a')[0]).click();
                for(var i= 0,len=cityList.length;i<len;i++) {
                    var obj = $('<a>' + cityList[i].cityName + '</a>');
                    $('#cloneCity').append(obj);
                }
                //设置一个隐藏的占位的div
                var tapHeight=$("#cityList").css("height");
                $('#cloneCity').css({visibility:"hidden",display:"none",marginBottom:tapHeight});

                var len = cityList.length;
                var tapList = new Array(len);

                for(var i= 0;i<len;i++){
                    var cityId= "#city"+i;
                    tapList[i]=parseInt($(cityId).offset().top)-parseInt(tapHeight);
                }

                $('#cityList>a').click(function(){
                    var index = $(this).attr("id").split("_")[1];
                    $("html,body").animate({scrollTop:tapList[index]},500);
                });

                //滚动
                $(document).scroll(function () {
                    var cur = $(document).scrollTop();
                    var top = $("#branchCompany").offset().top-100;

                    for(var i= 0,len=cityList.length;i<len;i++){
                        var cityId= "#city"+i;
                        tapList[i]=parseInt($(cityId).offset().top)-parseInt(tapHeight);
                    }

                    if (cur>=top) {
                        $('#navTop').addClass("navTop");
                        $("#cloneCity").css("display","block");

                        for (var i = 0, len = tapList.length; i < len; i++) {
                            if ((i != len - 1 && cur >= tapList[i] && cur < tapList[i + 1]) || (i == len - 1 && cur >= tapList[i])) {
                                $('#cityList>a').eq(i).addClass("hotCitySelected");
                            } else {
                                $('#cityList>a').eq(i).removeClass("hotCitySelected");
                            }
                        }
                    } else {
                        $('#navTop').removeClass("navTop");
                        $("#cloneCity").css("display","none");
                    }
                });
            });
        }
        getBranches();

        $(".NavDiv a").eq(3).addClass("selected").siblings().removeClass("selected");





        //获取顶部图片
        function getBanner() {
            $.getJSON("/s_member/api/account/User/getWebSiteBanner?type=6&serialNum=3", function (data) {
                $(".banner").append("<div class=\"bannerImg\" style=\"background: url('/s_img/icon.jpg\?_id="+data.content.items[0].icon+"') 50% 50% no-repeat;height: 400px;\"></div>")
            });
        }
        getBanner();
    });
})();