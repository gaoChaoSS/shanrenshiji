/**
 * Created by tianchangsen on 17/1/24.
 */
/**
 * Created by tianchangsen on 17/1/23.
 */
(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $rootScope.getFixed($scope.model, $scope.entity);
        $scope.pageSize = 10;
        $scope.pageNo = 1;
        $scope.clearSellectData = function () {
            $scope.sellerName = null;
            $scope.productName = null;
            $scope.productStatus = null;
            $scope.productStock = null;
            $scope.productAudit = null;
            $scope.eventProduct = null;
            $scope.pageNo = 1;
            $scope.queryData();
        }
        $scope.queryData = function (p) {
            if (p != null) {
                $scope.pageNo = p;
            }
            var url = window.basePath + '/account/Seller/queryAllProduct?pageSize=' + $scope.pageSize + "&pageNo=" + $scope.pageNo;
            if ($scope.sellerName != null) {
                url += "&sellerName=" + $scope.sellerName;
            }
            if ($scope.productName != null) {
                url += "&productName=" + $scope.productName;
            }
            if ($scope.productStock != null) {
                url += "&productStock=" + $scope.productStock;
            }
            if ($scope.productAudit != null) {
                url += "&productAudit=" + $scope.productAudit;
            }
            if ($scope.eventProduct != null) {
                url += "&isAct=" + $scope.eventProduct;
            }
            $http.get(url).success(function (re) {
                $scope.productList = re.content.productList;
                $scope.totalPage = re.content.totalPage;
                $scope.sellerPage = [];

                $scope.totalNumber = re.content.totalCount;
                //页码集合
                $scope.pageList = [];
                //当前显示的页码从第几页开始
                var listCur = 1;
                var listCurCount = 5;
                if ($scope.pageNo <= 5 && $scope.totalPage <= 5) {
                    listCur = 1;
                    listCurCount = $scope.totalPage;
                } else {
                    listCur = $scope.pageNo - 2;
                    if (listCur <= 1) {
                        listCur = 1;
                    } else if (($scope.pageNo > $scope.totalPage - 3 && listCur >= 4) || $scope.totalPage == 6) {
                        listCur = $scope.totalPage - 4;
                    }
                }
                for (var index = 0; index < listCurCount; index++, listCur++) {
                    $scope.pageList.push({num: listCur});
                }
                //选中的是第几个页码
                $scope.pageIndex = $scope.pageNo;
                //是否显示最后一页的页码
                $scope.isLastPage = ($scope.pageIndex < $scope.totalPage - 2) && $scope.totalPage > 5;
                //是否显示第一页的页码
                $scope.isFirstPage = $scope.pageIndex >= 4 && $scope.totalPage > 5;

                $scope.isNullPage = ($scope.totalPage < 1) || ($scope.totalPage == null);
            });
        }
        $scope.pageNext = function (num) {
            if ($scope.pageNo + num < 1 || $scope.totalPage < $scope.pageNo + num) {
                return;
            }
            $scope.pageNo += num;
            $scope.queryData();
        }

        //跳转页码
        $scope.pageNumber = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $scope.pageNo = num;
            $scope.queryData();
        }

        $scope.pageCur = function (index) {
            $scope.pageIndex = index;
        }

        $scope.queryData();
        $scope.queryProduct = function () {
            $scope.pageNo = 1;
            $scope.queryData();
        }
    });
})(angular);
