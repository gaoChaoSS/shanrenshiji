/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeAccount';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.menuCheck = false;
        $scope.bankCheck = false;
        //商家总收入
        $scope.sellerAccountTotal=0;
        //默认显示当月的记录
        $scope.startDateTime = new Date().setHours(0, 0, 0, 0);
        $scope.endDateTime = new Date().setHours(23, 59, 59, 999);

        $(".enterInput input").bind('input propertychange', function () {
            var as = $(this).val();
            if ($(this).val() != null) {
                if ($(this).next().val() == null) {
                    $(this).blur();
                } else {
                    $(this).next().focus();
                }
            }
        });

        $scope.getBankInfo = function () {
            var url = window.basePath + '/account/Seller/getBankInfoBySeller';
            $http.get(url).success(function (re) {
                $scope.bankInfo = re.content;
                if (window.isEmpty($scope.bankInfo.bankId)) {
                    $scope.bankCheck = false;
                } else {
                    $scope.bankCheck = true;
                }
            });
        }
        $scope.isSellerSetPayPwd = function () {
            var url = window.basePath + '/account/Seller/isSellerSetPayPwd';
            $http.get(url).success(function (re) {
                if (re.content.flag) {
                    $rootScope.goPage('/home/paySetPassword/userType/seller');
                } else {
                    $rootScope.goPage('/home/payModifyPassword/userType/seller');
                }
            })
        }
        $scope.isSetPayPwd = function () {
            var url = window.basePath + '/account/Seller/isSellerSetPayPwd';
            $http.get(url).success(function (re) {
                if (re.content.flag) {
                    $rootScope.goPage('/home/paySetPassword/userType/seller');
                } else {
                    var len = $scope.bankInfo.bankId.length;
                    $rootScope.goPage('/store/withdraw/userType/Seller/orderType/9/bankId/'+$scope.bankInfo.bankId.substring(len-4,len)+"/bankName/"+$scope.bankInfo.bankName);
                }
            })
        };

        $scope.getStorePayType = function(payType){
            if(payType=='4') {
                return '支付宝支付';
            }else if(payType=='10'){
                return '微信支付';
            }else if(payType=='3'){
                return '余额支付';
            }else if(payType=='6'){
                return '现金支付';
            }
        }
        $scope.getStoreTradeType = function(type){
            if (type == "0") {
                return '会员扫码';
            } else if (type == "1") {
                return '现金交易';
            } else if (type == "2") {
                return '非会员扫码';
            } else if (type == "3") {
                return '商家充值';
            } else if (type == "4") {
                return '服务站充值';
            } else if (type == "5") {
                return '会员充值';
            } else if (type == "6") {
                return '会员替朋友充值';
            } else if (type == "7") {
                return '服务站激活会员卡';
            } else if (type == "8") {
                return '会员端激活会员卡';
            } else if (type == "9") {
                return '商家提现';
            } else if (type == "10") {
                return '服务站提现';
            } else if (type == "11") {
                return '线上交易';
            }
        }
        //商家收入表
        $scope.getSellerSales = function () {
            $scope.nowDay = new Date().getTime();
            $scope.nowTime = new Date($scope.nowDay).setHours(0, 0, 0, 0);
            var url = window.basePath + '/order/OrderInfo/getSellerAccountByWeek';
            $http.get(url).success(function (re) {
                var data = [];
                //数组遍历
                for (var i = 0; i < re.content.items.length; i++) {
                    data.push(
                        {
                            week: '星期' + '日一二三四五六'.charAt(re.content.items[i].week),
                            money: re.content.items[i].daySales
                        }
                    );
                    $scope.sellerAccountTotal+=re.content.items[i].daySales;
                }
                //折线走势图
                var defs = {
                    'week': {
                        alias: '日期'
                    },
                    'money': {
                        alias: '金额'
                    }
                };
                var winWidth = null;
                if ((document.body) && (document.body.clientWidth) < 750) {
                    winWidth = document.body.clientWidth;
                }
                else if ((document.body) && (document.body.clientWidth) > 750) {
                    winWidth = 500;
                }

                var chart = new G2.Chart({
                    id: 'Accounts',
                    width: winWidth + 120,
                    height: 180
                });

                $("#Accounts").css({marginLeft: -60});

                chart.source(data, defs);
                chart.axis('week', {
                    position: 'bottom', // 设置坐标轴的显示位置，可取值 top bottom left right
                    line: null, // 设置坐标轴线的样式，如果值为 null，则不显示坐标轴线 图形属性
                    tickLine: null,
                    labels: {
                        label: {
                            textAlign: 'center', // 文本对齐方向，可取值为： left center right
                            fill: '#fff', //文本的颜色
                            fontSize: '10', // 文本大小
                            fontWeight: 'bold', // 文本粗细
                        }, // 设置坐标轴文本的显示样式，如果值为 null，则不显示坐标轴文本 文本属性
                        autoRotate: null // 是否需要自动旋转
                    },
                    title: null, // 坐标轴标题设置，如果值为 null，则不显示标题 文本属性
                    grid: null,
                });
                chart.axis('money', {
                    position: 'left', // 设置坐标轴的显示位置，可取值 top bottom left right
                    line: null, // 设置坐标轴线的样式，如果值为 null，则不显示坐标轴线 图形属性
                    labels: null,
                    tickLine: null,
                    title: null, // 坐标轴标题设置，如果值为 null，则不显示标题 文本属性
                    grid: null,
                });

                chart.line().position('week*money').color('#fff').label('money', {
                    offset: 10, // 文本距离图形的距离
                    label: {
                        fill: '#fff',
                        shadowBlur: 5, // 文本阴影模糊
                        shadowColor: '#fff' // 阴影颜色
                    },
                }).size(1);
                chart.render();
            })
        };
        //账户
        $scope.getSellerBill = function () {
            var url = window.basePath + '/order/OrderInfo/getSellerAccount';
            $http.get(url).success(function (re) {
                $scope.accountTotal=re.content;
            })
        }
        //今日交易
        // $scope.getSellerBillYesterday = function () {
        //     $scope.nowDay = new Date().getTime();
        //     $scope.nowTime = new Date($scope.nowDay).setHours(0, 0, 0, -1);
        //     var url = window.basePath + '/order/OrderInfo/getSellerAccountByYesterday';
        //     $http.get(url).success(function (re) {
        //         $scope.accountYesterday=re.content.account;
        //     })
        // }
        //查询商户点余额
        // $scope.queryCashCount = function () {
        //     var url = window.basePath + '/account/Seller/querySellerCashCount';
        //     $http.get(url).success(function (re) {
        //         $scope.sellerCashCount = re.content.cashCount;
        //     })
        //
        // }
        $scope.changeSelect = function(selectTradeType){
            $scope.sellerTransactionList = [];
            window.indexNum = 0;
            window.pageNo = 1;
            window.pageSize = 8;
            $scope.isLoadMore = true;
            $scope.selectTradeType = selectTradeType;
            $scope.queryTransaction();
        }
        //查询交易记录
        $scope.queryTransaction = function () {
            var s = localStorage.getItem("select_pop_date_startDate");
            if (!window.isEmpty(s)) {
                $scope.startDateTime = parseInt(s);
            }
            localStorage.removeItem("select_pop_date_startDate");

            var e = localStorage.getItem("select_pop_date_endDate");
            if (!window.isEmpty(e)) {
                $scope.endDateTime = parseInt(e);
            }
            localStorage.removeItem("select_pop_date_endDate");

            $scope.startDateTime = new Date($scope.startDateTime).setHours(0, 0, 0, 0);
            $scope.endDateTime = new Date($scope.endDateTime).setHours(0, 0, 0, 0) + (1000 * 60 * 60 * 24 - 1);
            if ($scope.startDateTime > $scope.endDateTime) {
                $scope.startDateTime = $scope.endDateTime - (1000 * 60 * 60 * 24 - 1);
                // malert("开始时间不能大于结束时间");
            }

            var url = window.basePath + "/account/Seller/queryTransaction?"
                + "indexNum=" + window.indexNum
                + "&pageNo=" + window.pageNo
                + "&pageSize=" + window.pageSize
                + "&startTime="+$scope.startDateTime
                + "&endTime="+$scope.endDateTime;

            if(!window.isEmpty($scope.selectTradeType)){
                url+="&tradeType="+$scope.selectTradeType;
            }
            $http.get(url).success(function (re) {
                $.each(re.content.items, function (k, v) {
                    $scope.sellerTransactionList.push(v);
                });
                $scope.total=re.content;
                $scope.totalNumer = re.content.totalNum;
                $scope.totalPage = re.content.totalPage;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            })
        }
        $scope.scrollEvent = function () {
            if ($('#moreButton').size() == 0) {
                return;
            }
            var loadNextPage = function () {
                window.stopScroll--;
                if (window.stopScroll > 0) {
                    return;
                }
                if ($scope.totalPage <= window.pageNo) {
                    return;
                }
                $scope.more();
            }
            if ($('#moreButton').offset().top < ($(window).scrollTop() + $(window).height())) {
                window.stopScroll || (window.stopScroll = 0);
                window.stopScroll++;
                loadNextPage.delay(0.5);
            }
        }

        $scope.addScrollEvent = function () {
            $('.overflowPC').unbind('scroll');
            $(window).unbind('scroll');
            $('.overflowPC').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.more = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.queryTransaction();
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商家账户');

                $scope.sellerTransactionList = [];
                $scope.sellerAccountTotal=null;
                $scope.selectTradeType="";
                $scope.isLoadMore = true;
                $scope.selectTradeType ='';
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.getBankInfo();
                $scope.getSellerBill();
                $scope.getSellerSales();
                // $scope.getSellerBillYesterday();
                // $scope.queryCashCount();
                $scope.queryTransaction();
                $scope.addScrollEvent();
                $("#Accounts").remove();
                $("#AccountsP").append("<div id=\"Accounts\"></div>");
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
