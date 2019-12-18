(function (angular, undefined) {

    var model = 'other';
    var entity = 'special';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.isGoodsLoadMore = true;
        $scope.saleCountMore = true;
        window.indexNum = 0;
        window.pageSize = 8;
        window.pageNo = 1;
        $scope.selectedOperate = '类别';

        $scope.productInclude="/yzxfMember_page/view/store/i_product.jsp";

        $scope.moreGoods = function () {
            $scope.isGoodsLoadMore = true;
            window.indexNum++;
            window.pageNo++;
            $scope.getList();
        }
        $scope.query = function () {
            window.indexNum = 0;
            window.pageSize = 8;
            window.pageNo = 1;
            $scope.goodsList = [];
            $scope.getList();
        }

        $scope.getList=function(){
            if(!window.isEmpty($rootScope.pathParams.productType) && $rootScope.pathParams.productType=="hot"){
                $scope.getProductByHot();
            }else{
                $scope.querySpecial();
            }
        }

        $scope.querySpecial = function () {
            var url = window.basePath + "/order/ProductInfo/querySpecial?indexNum=" + window.indexNum + "&pageSize=" + window.pageSize + "&pageNo=" + window.pageNo;
            if ($scope.saleCountMore != null) {
                url += "&saleCountMore=" + $scope.saleCountMore;
            }
            if ($rootScope['$$operateSpecial'] != null) {
                url += "&operate=" + $rootScope['$$operateSpecial'].operateValue;
                $scope.selectedOperate = $rootScope['$$operateSpecial'].operateName;
            } else {
                $scope.selectedOperate = '类别';
            }
            $http.get(url).success(function (re) {
                $.each(re.content.items, function (k, v) {
                    $scope.goodsList.push(v);
                })
                $scope.totalGoodsNumber = re.content.totalNum;
                $scope.totalGoodsPage = re.content.totalPage;
                if ($scope.totalGoodsPage <= window.pageNo) {
                    $scope.isGoodsLoadMore = false;
                }
            });
        };

        //获取热门商品
        $scope.getProductByHot=function(){
            var url = window.basePath + "/account/Seller/getCommodityForType?selectedIndex=3&pageSize=" + window.pageSize + "&pageNo=" + window.pageNo+"&isRemen=1";
            if ($rootScope['$$operateSpecial'] != null) {
                url += "&operate=" + $rootScope['$$operateSpecial'].operateValue;
                $scope.selectedOperate = $rootScope['$$operateSpecial'].operateName;
            } else {
                $scope.selectedOperate = '类别';
            }
            if(!$scope.saleCountMore){//排序方式
                url+="&orderByType=asc";
            }
            $http.get(url).success(function (re) {
                $.each(re.content.items, function (k, v) {
                    $scope.goodsList.push(v);
                })
                $scope.totalGoodsNumber = re.content.totalNum;
                $scope.totalGoodsPage = re.content.totalPage;
                if ($scope.totalGoodsPage <= window.pageNo) {
                    $scope.isGoodsLoadMore = false;
                }
            });
        };


        $scope.scrollEvent = function () {
            if ($('#moreGButton').size() == 0) {
                return;
            }
            var loadNextPage = function () {
                window.stopScroll--;
                if (window.stopScroll > 0) {
                    return;
                }
                if ($scope.totalGoodsPage <= window.pageNo) {
                    return;
                }
                $scope.moreGoods();
            }
            if ($('#moreGButton').offset().top < ($(window).scrollTop() + $(window).height())) {
                window.stopScroll || (window.stopScroll = 0);
                window.stopScroll++;
                loadNextPage.delay(0.5);
            }
        }

        $scope.addScrollEvent = function () {
            $('.overflowPC').unbind('scroll');
            $(window).unbind('scroll');
            $('#specialScroll').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.initGoods=function(){
            $scope.goodsList = [];
            window.indexNum = 0;
            window.pageSize = 8;
            window.pageNo = 1;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.titleName='天天特价';
                if(!window.isEmpty($rootScope.pathParams.productType) && $rootScope.pathParams.productType=="hot"){
                    $scope.titleName='热门商品';
                }
                window.setWindowTitle($rootScope,  $scope.titleName);
                $scope.initGoods();
                $scope.getList();
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    })
})(angular);
