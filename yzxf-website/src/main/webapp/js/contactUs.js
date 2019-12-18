
(function () {
    $(document).ready(function () {
        $(".NavDiv a").eq(5).addClass("selected").siblings().removeClass("selected");

        //百度地图
        var map = new BMap.Map("container");          // 创建地图实例
        var point = new BMap.Point(104.025607,30.716465);  // 创建点坐标
        map.centerAndZoom(point, 17);
        var marker = new BMap.Marker(point);  // 创建标注
        map.addOverlay(marker);
        map.panTo(point);
        map.enableScrollWheelZoom(true);
        //获取顶部图片
        function getBanner() {
            $.getJSON("/s_member/api/account/User/getWebSiteBanner?type=6&serialNum=5", function (data) {
                $(".banner").append("<div class=\"bannerImg\" style=\"background: url('/s_img/icon.jpg\?_id="+data.content.items[0].icon+"') 50% 50% no-repeat;height: 400px;\"></div>")
            });
        }
        getBanner();

    })
})();