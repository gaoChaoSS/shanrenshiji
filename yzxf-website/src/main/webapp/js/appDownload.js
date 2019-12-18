(function () {
    $(document).ready(function () {
        var appList = [
            {
                name:"安卓会员版",
                url:"https://www.phsh315.com/phsh_files/android_member_1.43.apk"
            },{
                name:"安卓商家版",
                url:"https://www.phsh315.com/phsh_files/android_seller_1.44.apk"
            },{
                name:"苹果会员版",
                url:"https://itunes.apple.com/cn/app/%E6%99%AE%E6%83%A0%E7%94%9F%E6%B4%BB%E4%BC%9A%E5%91%98%E7%89%88/id1315919784?mt=8"
            },{
                name:"苹果商家版",
                url:"https://itunes.apple.com/cn/app/id1315919462?mt=8"
            },
        ];

        var createQrcode=function(){
            $.each(appList,function(k,v){
                var qr = qrcode(10, 'H');
                qr.addData(v.url);
                qr.make();
                $(".qrcodeImg"+k).html(qr.createImgTag());
                $(".qrcodeText"+k).text(v.name);

            });
        }

        createQrcode();

    })
})();