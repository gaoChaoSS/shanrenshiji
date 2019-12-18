(function () {
    window.incomeGrid = function ($rootScope, $scope, $http) {
        $scope.init=function(){
            $scope.orderList2 = [];
            window.pageNo2 = 1;
            window.pageSize2 = 10;
            $scope.isNullPage2 = false;
            $scope.pageList2 = [];
            $scope.getBillFun();
            $scope.isSeller=$scope.dataPage.$$selectedItem.userId.substring(0,1)=="S";
            if($scope.entityTitle != "会员交易排行"){
                $scope.getIncomeTradeSum($scope.dataPage.$$selectedItem.areaValue,$scope.dataPage.$$selectedItem.month);
            }
        }
        $scope.getIncomeTradeSum = function(areaValue,month){
            var url = window.basePath + '/order/OrderInfo/getIncomeTradeSum?areaValue='+areaValue+'&month='+month;
            $http.get(url).success(function(re){
                $scope.incomeTradeSum = re.content.items[0].incomeTradeSum
            })
        }

        //上一页/下一页
        $scope.pageNext = function (num,isCriteria) {
            if (window.pageNo2 + num < 1 || $scope.totalPage2 < window.pageNo2 + num) {
                return;
            }
            window.pageNo2 += num;
            $scope.getBillFun();
        }

        //跳转页码
        $scope.pageNumber = function (num) {
            if (num < 1 || $scope.totalPage2 < num) {
                return;
            }
            window.pageNo2 = num;
            $scope.getBillFun();
        }

        $scope.pageCur=function(index){
            $scope.pageIndex2=index;
        }

        $scope.getBillFun = function () {
            var createTime;
            if(!window.isEmpty($scope.dataPage.$$selectedItem.month)){
                var year = parseInt($scope.dataPage.$$selectedItem.month.toString().substring(0, 4));
                var month = parseInt($scope.dataPage.$$selectedItem.month.toString().substring(4, 6));

                var startDate = new Date();
                startDate.setFullYear(year, month - 1, 1);
                startDate.setHours(0, 0, 0, 0);
                var endDate = new Date();
                endDate.setFullYear(year, month, 1);
                endDate.setHours(0, 0, 0, 0);

                var startDateLong = startDate.getTime();
                var endDateLong = endDate.getTime() - 1;
                createTime="___in_"+startDateLong+"-"+endDateLong;
            }

            var url = window.basePath + '/order/OrderInfo/getBill?1=1';
            var date = {
                _createTime: createTime,
                pageNo: window.pageNo2,
                pageSize: window.pageSize2,
                _orderStatus:100,
                _queryType:'endTime'
            }
            if($scope.entityTitle == "会员交易排行"){
                date._search=$scope.dataPage.$$selectedItem.userId;
                date._notOrderType=7;
                if($scope.isCashOrder){//是否余额支付
                    date._payType="3";
                }
            }else{
                date._userId=$scope.dataPage.$$selectedItem.userId;
                date._areaValue=$scope.dataPage.$$selectedItem.areaValue.replace(/_/g, "\\_")
            }
            $.each(date, function (k, v) {
                if (typeof(v) == "undefined" || v == null || v == "null") {
                    return true;
                }
                url += '&' + k + '=' + encodeURIComponent(v);
            });
            $http.get(url).success(function (re) {
                $scope.orderList2 = [];
                $.each(re.content.items, function (k, v) {
                    $scope.orderList2.push(v);
                });
                $scope.totalNumber = re.content.totalNum;
                $scope.totalPage2 = re.content.totalPage;
                //页码集合
                $scope.pageList2 = [];
                //当前显示的页码从第几页开始
                var listCur = 1;
                var listCurCount = 5;
                if (window.pageNo2 <= 5 && $scope.totalPage2 <= 5) {
                    listCur = 1;
                    listCurCount = $scope.totalPage2;
                } else {
                    listCur = window.pageNo2 - 2;
                    if (listCur <= 1) {
                        listCur = 1;
                    } else if ((window.pageNo2 > $scope.totalPage2 - 3 && listCur >= 4) || $scope.totalPage2 == 6) {
                        listCur = $scope.totalPage2 - 4;
                    }
                }
                for (var index = 0; index < listCurCount; index++, listCur++) {
                    $scope.pageList2.push({num: listCur});
                }
                //选中的是第几个页码
                $scope.pageIndex2 = window.pageNo2;
                //是否显示最后一页的页码
                $scope.isLastPage2 = ($scope.pageIndex2 < $scope.totalPage2 - 2) && $scope.totalPage2 > 5;
                //是否显示第一页的页码
                $scope.isFirstPage2 = $scope.pageIndex2 >= 4 && $scope.totalPage2 > 5;

                $scope.isNullPage2 = ($scope.totalPage2 < 1) || ($scope.totalPage2 == null);
            })
        }

        //例如：1转账、2充值、3线上消费、4现金交易 5.会员扫码
        $scope.getTradeType = function (type) {
            $rootScope.getOrderType(type);
        };

        $scope.getPayType=function(type){
            if(type==3){
                return '余额';
            }else if(type==4){
                return '支付宝';
            }else if(type==6){
                return '现金';
            }else if(type==10){
                return '微信';
            }
        };

        //获取归属
        $scope.getBelongArea=function(m,s,f,cardNo,orderType){
            if(/^([012568]|(11)|(14))$/.test(orderType)){
                if(window.isEmpty(m)){
                    if(window.isEmpty(cardNo) || cardNo.substring(0,1)=="C"){
                        return '平台';
                    }
                }
                return m;
            }else if(/^[39]$/.test(orderType)){
                return s;
            }else if(/^(4)|(7)|(10)$/.test(orderType)){
                return f;
            }
        }

        //获取付款人名称
        $scope.getNameByPay=function(index,m,s,f,orderType){
            if(/^([01568]|(11))$/.test(orderType)){
                return m;
            }else if(/^[3]$/.test(orderType)){
                return s;
            }else if(/^[47]$/.test(orderType)){
                return f;
            }else if(/^[2]$/.test(orderType)){
                return '非会员';
            }else if(/^(9)|(10)|(14)$/.test(orderType)){
                return '平台';
            }
        }

        //获取收款人名称
        $scope.getNameByAcq=function(index,m,m2,s,f,orderType){
            if(/^[5]|(14)$/.test(orderType)){
                return m;
            }else if(/^[6]$/.test(orderType)){
                return m2;
            }else if(/^([01239]|(11))$/.test(orderType)){
                return s;
            }else if(/^(4)|(7)|(10)$/.test(orderType)){
                return f;
            }else if(/^[78]$/.test(orderType)){
                return '平台';
            }
        }

        $scope.init();
    }
})();