(function (angular, undefined) {

    var model = 'other';
    var entity = 'commonweal';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
            $scope.isGoodsLoadMore = true;
            $scope.saleCountMore = true;
            window.indexNum = 0;
            window.pageSize = 8;
            window.pageNo = 1;

            $scope.selectedOperate='类别';

            $scope.productInclude="/yzxfMember_page/view/store/i_product.jsp";

            //$scope.isLogin = function () {
            //    if (window.isEmpty(getCookie('_member_mobile'))) {
            //        malert('请先登录');
            //        $rootScope.goPage('/account/login');
            //    }
            //}
            $scope.query = function () {
                window.indexNum = 0;
                window.pageSize = 8;
                window.pageNo = 1;
                $scope.goodsList = [];
                $scope.queryCommonweal();
            }
            $scope.queryCommonweal = function () {
                var url = window.basePath + "/order/ProductInfo/querySpecial?" +
                    "indexNum=" + window.indexNum
                    + "&pageSize=" + window.pageSize
                    + "&pageNo=" + window.pageNo
                    + "&type=gongyi";
                if ($scope.saleCountMore != null) {
                    url += "&saleCountMore=" + $scope.saleCountMore;
                }

                if($rootScope['$$operateCommonweal']!=null){
                    url += "&operate="+$rootScope['$$operateCommonweal'].operateValue;
                    $scope.selectedOperate=$rootScope['$$operateCommonweal'].operateName;
                }else{
                    $scope.selectedOperate='类别';
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
            }
            $scope.moreGoods = function () {
                window.indexNum++;
                window.pageNo++;
                $scope.queryCommonweal();
            }
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
            $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
                onResume: function () {
                    window.setWindowTitle($rootScope, '公益专区');
                    $scope.goodsList = [];
                    //$scope.isLogin();
                    window.indexNum = 0;
                    window.pageSize = 8;
                    window.pageNo = 1;
                    $scope.queryCommonweal();
                    $scope.addScrollEvent();
                }
            };
            $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

        }
    );
})(angular);
