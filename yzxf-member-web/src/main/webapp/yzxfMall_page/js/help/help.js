(function (angular, undefined) {

    var model = 'help';
    var entity = 'help';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        // $scope.helpLeftNavigation = '/yzxfMall_page/temp_new/helpLeftNavigation.html';
        // $scope.helpRightContent = '/yzxfMall_page/temp_new/helpRightContent.html';
        initTypeGrid($rootScope, $scope, $http , $interval , $location);
        // initHelpGrid($rootScope, $scope, $http);
        //消费者
        // $scope.xLiList = [
        //     {id:0,title: '注册流程'},
        //     {id:1,title: '赚养老金流程'},
        //     {id:2,title: '支付方式'},
        //     {id:3,title: '找回密码'},
        // ]
        // //商家
        // $scope.cLiList = [
        //     {id:0,title: '入驻流程'},
        //     {id:1,title: '入驻优势'},
        //     {id:2,title: '入驻要求'},
        //     {id:3,title: '操作说明'},
        // ]
        // //厂家
        // $scope.jLiList = [
        //     {id:0,title: '入驻流程'},
        //     {id:1,title: '入驻优势'},
        //     {id:2,title: '入驻要求'},
        //     {id:3,title: '操作说明'},
        // ]
        // //售后服务
        // $scope.sLiList = [
        //     {id:0,title: '品质保证'},
        //     {id:1,title: '退货政策'},
        //     {id:2,title: '退货流程'},
        //     {id:3,title: '办理退货'},
        // ]
        // //联系我们
        // $scope.lLiList = [
        //     {id:0,title: '平台介绍'},
        //     {id:1,title: '联系我们'},
        //     {id:2,title: '帮助中心'},
        //     {id:3,title: '成为供应商'},
        // ]
        // $scope.ulList = [
        //     {id:0,title: '消费者', li: $scope.xLiList},
        //     {id:1,title: '商家', li: $scope.cLiList},
        //     {id:2,title: '厂家', li: $scope.jLiList},
        //     {id:3,title: '售后服务', li: $scope.sLiList},
        //     {id:4,title: '联系我们', li: $scope.lLiList}
        // ]

        // $scope.helpTxtArr=new Array(20);
        //
        // $scope.helpTxtArr[0] = [
        //     {Title:'第一步、挑选有养老金的商品',content:'访问本网站,浏览并选择自己喜欢的商品和中意的养老金。'},
        // ];
        // $scope.helpTxtArr[1] = [
        //     {Title:'第一步、挑选有养老金的商品',content:'访问本网站,浏览并选择自己喜欢的商品和中意的养老金。'},
        //     {Title:'第二步、放入购物车',content:'选定商品和养老金后,点击"放入购物车"按钮。'},
        // ];
        // $scope.helpTxtArr[2] = [
        //     {Title:'第一步、挑选有养老金的商品',content:'访问本网站,浏览并选择自己喜欢的商品和中意的养老金。'},
        //     {Title:'第二步、放入购物车',content:'选定商品和养老金后,点击"放入购物车"按钮。'},
        //     {Title:'第三步、提交订单和结算',content:'检查购物车并确认无误后,点击"结算"按钮,进入结算中心。填写收货人的地址、电话等详细信息,便于我们的配送' +
        //     '和其他服务。选择支付方式,我们提供的支付方式有:微信支付、支付宝、银联在线、网银、快钱等。'},
        // ];

        $scope.selectPage=function(menu){
            $rootScope.selectedHelpMenu = menu;
            $scope.showArt(menu);
        }

        //显示文章
        $scope.showArt=function(menu){
            if(window.isEmpty(menu) || window.isEmpty(menu._id)){
                return;
            }
            var url = window.basePath + "/crm/Article/getArticle?pId="+menu._id;
            $http.get(url).success(function(re){
                $scope.curArt=re.content;
                if(window.isEmpty(re.content) || window.isEmpty(re.content._id)){
                    $scope.curArt={
                        title:menu.name
                    }
                }
            });
        };

        //获取底部帮助模块
        $scope.getHelp2=function(){
            var url = window.basePath + "/crm/Article/getMenu?name='商城帮助'";
            $http.get(url).success(function(re){
                $rootScope.menuList = re.content.items;
                $scope.selectPage($rootScope.menuList[0]);
            });
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '帮助');
                if(!window.isEmpty($rootScope.selectedHelpMenu)){
                    $scope.selectPage($rootScope.selectedHelpMenu);
                }else{
                    $scope.getHelp2();
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);