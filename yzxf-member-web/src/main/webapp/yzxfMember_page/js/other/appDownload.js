(function (angular, undefined) {

    var model = 'other';
    var entity = 'appDownload';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.appList = [
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

        $scope.setQrcode = function(index){
            if(window.isEmpty($scope.appList[index].url)){
                malert("暂未上架");
                return;
            }
            $scope.showMenu = false;
            $scope.selectedItem ={
                name: $scope.appList[index].name,
                url: $scope.appList[index].url
            };
            window.setWindowTitle($rootScope, $scope.selectedItem.name+'下载');
            var qr = qrcode(10, 'H');
            qr.addData($scope.appList[index].url);
            qr.make();
            $(".qrcode").html(qr.createImgTag());
            $(".qrcode img").addClass("iconImg2");
            $(".qrcode img").css("margin","0 auto")
        };

        $scope.goBackFun = function(){
            $scope.showMenu?goBack():$scope.showMenu=!$scope.showMenu;
            if($scope.showMenu){
                $scope.selectedItem.name='APP';
                window.setWindowTitle($rootScope, 'APP下载');
            }
        };

        $scope.goUrl=function(url){
            window.location.href=url;
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, 'APP下载');
                $scope.selectedItem={
                    name:'APP'
                };
                $rootScope.isIndex = false;
                $scope.showMenu = true;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);