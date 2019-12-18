(function (angular, undefined) {

    var model = 'home';
    var entity = 'index';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.navCarousel = '/yzxfMall_page/temp_new/navCarousel.html';
        $scope.indexMain = '/yzxfMall_page/temp_new/indexMain.html';

        $scope.txtOrImg=-1;
        $scope.txtIsHover=false;
        $scope.isHover=false;
        $('.indexLeftNavDiv').hide();
        $('.indexRightNavDiv').hide();
            //"/yzxfMall_page/img/banner1.jpg",
            //"/yzxfMall_page/img/banner2.jpg",
            //"/yzxfMall_page/img/banner3.jpg"
        $scope.nowTime = new Date().getTime();
        $scope.moduleList= [ ];
        $scope.queryCommodityInType = function (selectedValue,index){
            var url = window.basePath + "/account/Seller/queryCommodityList?selectType="+selectedValue;
            $http.get(url).success(function (re) {
                var items=re.content.items;
                $scope.operate[index].productList=items[0];
                items.remove(0);
                $scope.operate[index].moduleList=items;
            })
        }

        $scope.queryType = function () {
            var url = window.basePath + "/account/Seller/queryTypeForMall";
            $http.get(url).success(function (re) {
                $scope.operate = re.content.items;
                var selectedValue = '';
                for(var i = 0 ;i<$scope.operate.length;i++){
                    if($scope.operate[i].level==1){
                        selectedValue="_"+$scope.operate[i].value+"_";
                    }else{
                        selectedValue=$scope.operate[i].pvalue+"_"+$scope.operate[i].value+"_";
                    }
                    $scope.queryCommodityInType(selectedValue,i);
                }
            })
        }
        $scope.getMallRectangle = function(){
            var url = window.basePath +'/account/User/getMallRectangle';
            $http.get(url).success(function(re){
                $scope.mallRectangle = re.content.items;
                $scope.advertList=[]
                for(var i = 0,len = re.content.items.length;i<len;i++){
                    $scope.advertList.push({id:i*2,url:re.content.items[i].icon});
                }
            })
        }

        $scope.getAdvertList=function(index){
            if(window.isEmpty($scope.advertList)){
                return;
            }
            for(var i= 0,len=$scope.advertList.length;i<len;i++){
                if(index==$scope.advertList[i].id){
                    return $scope.advertList[i].url;
                }
            }
            return null;
        }
        $scope.mouseNavUp=function(index){
            $scope.txtOrImg=index;
        }
        $scope.mouseNavDown=function(){
            $scope.txtOrImg=-1;
        }
        $scope.goBackTop = function(){
            $scope.txtIsHover = true;
        }
        $scope.goBackTopNo = function(){
            $scope.txtIsHover = false;
        }
        $scope.codeClick = function(){
            $scope.isHover = true;
        }
        $scope.codeClickNo = function(){
            $scope.isHover = false;
        }
        $scope.goTopHref = function(){
            $rootScope.goPage('/home/index#head');
            if($location.path().substr(0, 11) == '/home/index'){
                $location.url('/home/index');
            }
        }
        $(".goTop").click(function(){
            window.scrollTo(0,0);
        });

        var getNavHeight = function(){
            var navHeight = $("#indexLeftNavDiv").css("height");
            return navHeight.split("px")[0];
        };

        $(window).scroll(function(event){
            var wScrollY = window.scrollY; // 当前滚动条位置
            if (wScrollY < 480) {
                $('.indexLeftNavDiv').hide();
                $('.indexRightNavDiv').hide();
            }else{
                $('.indexLeftNavDiv').show();
                $('.indexRightNavDiv').show();
            }


            if(document.body.clientWidth<1600){
                $("#hideLeftNavBtn").css("display","block");
                $("#indexLeftNavDiv").css("left",getNavHeight()==30?'10px':'0px');
            }else{
                $("#indexLeftNavDiv").css("left",$('#mainBody').offset().left-100+'px');
                $("#hideLeftNavBtn").css("display","none");
            }
        });

        //点击缩小左侧导航栏
        $("#hideLeftNavBtn").click(function(){
            if(getNavHeight()==30){
                $("#hideLeftNavBtn").css({"transform":"rotate(0deg)"});
                $("#indexLeftNavDiv").css({top:"10%",left:"0",height:"auto"});
            }else{
                $("#hideLeftNavBtn").css({"transform":"rotate(180deg)"});
                $("#indexLeftNavDiv").css({top:"10px",left:"10px",height:"30px",overflow:"hidden"});
            }
        });

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '首页');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
                $scope.queryType();
                $scope.getMallRectangle();
                $scope.imgCarousel();
                $scope.isHideNav=false;
                $scope.indexTypeIsShow = true;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);