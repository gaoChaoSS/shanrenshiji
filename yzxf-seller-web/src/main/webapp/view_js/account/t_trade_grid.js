/**
 * Created by luoyunze on 17/6/15.
 */
(function () {
    window.initTrade = function ($rootScope, $scope, $http) {
        //例如：1转账、2充值、3线上消费、4现金交易 5.会员扫码
        $scope.getTradeType = function (type) {
            if (type == "0") {
                return '会员扫码';
            } else if (type == "1") {
                return '现金交易';
            } else if (type == "2") {
                return '互联网收款';
            } else if (type == "3") {
                return '商家充值';
            } else if (type == "4") {
                return '发卡点充值';
            } else if (type == "5") {
                return '会员充值';
            } else if (type == "6") {
                return '会员代充值';
            } else if (type == "7") {
                return '服务站激活会员卡';
            } else if (type == "8") {
                return '会员端激活会员卡';
            } else if (type == "9") {
                return '商家提现';
            } else if (type == "10") {
                return '发卡点提现';
            } else if (type == "11") {
                return '会员在线购买';
            } else if (type == "12") {
                return '快易帮现金收款';
            } else if (type == "13") {
                return '养老金激活会员';
            } else if (type == "14") {
                return '会员提现';
            }
        };

        $scope.getPayType=function(type){
            if(type=='4' || type=='14') {
                return '支付宝';
            }else if(type=='10' || type=='13'){
                return '微信';
            }else if(type=='3'){
                return '余额支付';
            }else if(type=='6'){
                return '现金'
            }else if(type=='16'){
                return '养老金'
            }
        };

        $scope.getOrderStatus = function (status) {
            if (status == 0) {
                return "草稿";
            } else if (status == 1) {
                return "未支付";
            } else if (status == 2) {
                return "已支付,等待商家发货";
            } else if (status == 3) {
                return "商家打包制作中";
            } else if (status == 4) {
                return "商家已发货";
            } else if (status == 5) {
                return "买家已收货";
            } else if (status == 6) {
                return "[退款]买家申请退货";
            } else if (status == 7) {
                return "[退款]等待买家发货";
            } else if (status == 8) {
                return "[退款]等待商家确认收货";
            } else if (status == 9) {
                return "[退款]已退款";
            } else if (status == 100) {
                return "订单完结,交易成功";
            }
        };

        $scope.getSpec = function (spec) {
            var text = '【' + spec.name + ' : ' + spec.items;
            if (!window.isEmpty(spec.addMoney) && spec.addMoney != 0) {
                text += ', 加价:' + spec.addMoney + '元';
            }
            text += '】';
            return text;
        };

        //获取归属
        $scope.getBelongArea = function (m, s, f, cardNo, orderType) {
            if (/^([012568]|(11)|(12)|(13)|(14))$/.test(orderType)) {
                if (window.isEmpty(m)) {
                    if (window.isEmpty(cardNo) || cardNo.substring(0, 1) == "C") {
                        return '平台';
                    }
                }
                return m;
            } else if (/^[39]$/.test(orderType)) {
                return s;
            } else if (/^(4)|(7)|(10)$/.test(orderType)) {
                return f;
            }
        }

        //获取付款人名称
        $scope.getNameByPay = function (index, m, s, f, orderType) {
            var dataPageItems;
            if($scope.isInitBill){
                dataPageItems=$scope.dataPage.$$selectedItem;
            }else{
                dataPageItems=$scope.dataPage.items[index];
            }

            if (/^([01568]|(11)|(12)|(13))$/.test(orderType)) {
                dataPageItems.payName = m;
                return m;
            } else if (/^[3]$/.test(orderType)) {
                dataPageItems.payName = s;
                return s;
            } else if (/^[47]$/.test(orderType)) {
                dataPageItems.payName = f;
                return f;
            } else if (/^[2]$/.test(orderType)) {
                dataPageItems.payName = '非会员';
                return '非会员';
            } else if (/^(9)|(10)|(14)$/.test(orderType)) {
                dataPageItems.payName = '平台';
                return '平台';
            }
        }

        //获取收款人名称
        $scope.getNameByAcq = function (index, m, m2, s, f, orderType) {
            var dataPageItems;
            if($scope.isInitBill){
                dataPageItems=$scope.dataPage.$$selectedItem;
            }else{
                dataPageItems=$scope.dataPage.items[index];
            }
            if (/^[5]|(14)$/.test(orderType)) {
                dataPageItems.acqName = m;
                return m;
            } else if (/^[6]$/.test(orderType)) {
                dataPageItems.acqName = m2;
                return m2;
            } else if (/^([01239]|(11)|(12))$/.test(orderType)) {
                dataPageItems.acqName = s;
                return s;
            } else if (/^(4)|(7)|(10)$/.test(orderType)) {
                dataPageItems.acqName = f;
                return f;
            } else if (/^[78]|(13)$/.test(orderType)) {
                dataPageItems.acqName = '平台';
                return '平台';
            }
        }

        $scope.setSelectData = function (index) {
            $scope.dataPage.$$selectedItem.payName = $scope.dataPage.items[index].payName;
            $scope.dataPage.$$selectedItem.acqName = $scope.dataPage.items[index].acqName;
        }

        //获取详情显示的模块
        $scope.getModel = function (orderType) {
            //利润,会员卡分润,会员,商家,发卡点,会员朋友
            $scope.modelList = {
                profit: false,
                card: false,
                member: false,
                seller: false,
                factor: false,
                friend: false
            };
            $scope.userList = [];
            $scope.team = [];
            $scope.orderItem = null;
            if (/^([01]|(11)|(12))$/.test(orderType)) {
                $scope.modelList.profit = true;//利润
            }
            if (/^[56]$/.test(orderType)) {
                $scope.modelList.recharge = true;//充值分润
            }
            if (/^[78]|(13)$/.test(orderType)) {
                $scope.modelList.card = true;//会员卡分润
            }
            if (/^([015678]|(11)|(12)|(13)|(14))$/.test(orderType)) {
                $scope.modelList.member = true;//会员
                $scope.getUserInfo(0, "/crm/Member/show?_id=" + $scope.dataPage.$$selectedItem.memberId);
                $scope.getBelong(0, $scope.dataPage.$$selectedItem.belongValueMember);
            }
            if (/^([01239]|(11)|(12))$/.test(orderType)) {
                $scope.modelList.seller = true;//商家
                $scope.getUserInfo(1, "/account/Seller/getSellerInfoById?sellerId=" + $scope.dataPage.$$selectedItem.sellerId);
                $scope.getBelong(1, $scope.dataPage.$$selectedItem.belongValueSeller);
            }
            if (/^(4)|(7)|(10)$/.test(orderType)) {
                $scope.modelList.factor = true;//发卡点
                $scope.getUserInfo(2, "/account/Factor/getFactorById?factorId=" + $scope.dataPage.$$selectedItem.factorId);
                $scope.getBelong(2, $scope.dataPage.$$selectedItem.belongValueFactor);
            }
            if (/^[6]$/.test(orderType)) {
                $scope.modelList.friend = true;//会员朋友
                $scope.getUserInfo(3, "/crm/Member/show?_id=" + $scope.dataPage.$$selectedItem.friendId);
                $scope.getBelong(3, $scope.dataPage.$$selectedItem.belongValueFriend);
            }
            if (orderType == 11) {//在线订单
                $scope.getOrderItem();
            }
            if (orderType == 14) {//会员提现
                $scope.isInitBill = true;
            }
        }

        //获取订单产品信息
        $scope.getOrderItem = function () {
            var url = window.basePath + "/order/OrderInfo/queryMyOrder?orderId=" + $scope.dataPage.$$selectedItem._id +
                "&userType=Agent";
            $http.get(url).success(function (re) {
                $scope.orderItem = re.content.orderList[0];
                $scope.getCoupon();
            });
        }
        //获取订单关联的卡券信息
        $scope.getCoupon = function () {
            if (window.isEmpty($scope.orderItem.couponId)) {
                return;
            }
            var url = window.basePath + "/crm/Coupon/getCouponByLinkId?isGetAll=true&linkId=" + $scope.orderItem.couponId;
            $http.get(url).success(function (re) {
                $scope.couponLink = re.content.couponLink;
                $scope.coupon = re.content.coupon;
            });
        };

        //如果没有选中的订单基本信息,那么先生成
        $scope.getBill = function (params) {
            var url = window.basePath + "/order/OrderInfo/getBill?pageNo=1&pageSize=99"+params;
            $http.get(url).success(function (re) {
                if(re.content.items==null || re.content.items.length==0){
                    malert("无法获取订单数据");
                    return;
                }
                $scope.orderList = re.content.items;
                $scope.setSelectedItem($scope.orderList[0]);
            });
        };

        //设置 需要显示的订单详情
        $scope.setSelectedItem=function(item){
            if(item._id==$scope.dataPage.$$selectedItem._id){
                return;
            }
            $scope.dataPage.$$selectedItem = item;

            $scope.dataPage.$$selectedItem.payName = $scope.getNameByPay(0,$scope.dataPage.$$selectedItem.nameMember,
                $scope.dataPage.$$selectedItem.nameSeller,$scope.dataPage.$$selectedItem.nameFactor
                ,$scope.dataPage.$$selectedItem.orderType);
            $scope.dataPage.$$selectedItem.acqName = $scope.getNameByAcq(0,$scope.dataPage.$$selectedItem.nameMember,
                $scope.dataPage.$$selectedItem.nameMemberAcq,$scope.dataPage.$$selectedItem.nameSeller,
                $scope.dataPage.$$selectedItem.nameFactor,$scope.dataPage.$$selectedItem.orderType);
            $scope.initOrderInfo();
        };

        //初始化订单详情
        $scope.initOrderInfo=function(){
            $scope.team = [];
            if (/^[78]|(13)$/.test($scope.dataPage.$$selectedItem.orderType)) {
                $scope.getProfit(4);
            } else if (/^([01]|(11)|(12))$/.test($scope.dataPage.$$selectedItem.orderType)) {
                $scope.getProfit(3);
            } else if(/^[56]$/.test($scope.dataPage.$$selectedItem.orderType)){
                $scope.getProfit(5);
            }
            $scope.getModel($scope.dataPage.$$selectedItem.orderType);
        };

        $scope.showOtherData = function () {
            $scope.showImg='';
            if($scope.isInitBill){
                if($scope.dataPage.$$selectedItem.orderType==11 && $scope.dataPage.$$selectedItem.pid==-1){//如果是一个总订单,则获取下面所有子订单
                    $scope.getBill("&_pid="+$scope.dataPage.$$selectedItem.orderId);
                }else{
                    $scope.getBill("&_isGetOne="+$scope.dataPage.$$selectedItem.orderNo+($scope.entity=='payLog'?'':"&_notOrderStatus=0,1"));
                }
            }else{
                $scope.initOrderInfo();
            }
        };

        // $scope.getCardProfit = function (type) {
        //     var url = window.basePath + '/order/Team/getTeamEarnings?orderNo=' + $scope.dataPage.$$selectedItem.orderNo;
        //     if(!window.isEmpty(type)){
        //         url += "&type="+type;
        //     }
        //     $http.get(url).success(function (re) {
        //         $scope.$$agentLog = re.content.agent;
        //
        //         $scope.$$agentLogList = [{}, {}, {}, {}];//平台
        //         if ($scope.$$agentLog == null || $scope.$$agentLog.length < 1) {
        //             return;
        //         }
        //         var countMoney = 0;
        //         for (var i = 0; i < 4; i++) {
        //             var obj = $scope.$$agentLog[i];
        //             if (obj == null) {
        //                 obj = {orderCash: 0.0};
        //             }
        //             countMoney += $scope.$$agentLog[i].orderCash;
        //             $scope.$$agentLogList[i] = obj;
        //         }
        //         $scope.factor = re.content.factor;
        //     });
        // }

        $scope.getProfit = function (type) {
            var url = window.basePath + '/order/Team/getTeamEarnings?type='+type+'&orderNo=' + $scope.dataPage.$$selectedItem.orderNo;
            $http.get(url).success(function (re) {
                $scope.team = re.content;
                if($scope.team===null || jQuery.isEmptyObject($scope.team)){
                    return;
                }
                $scope.$$team = [];

                var total = 0;
                // 代理商
                if(!jQuery.isEmptyObject($scope.team.agent)){
                    $.each($scope.team.agent,function(k,v){
                        total += parseFloat(v.orderCash);
                        $scope.$$team.push(v);
                    });
                }

                // 服务站
                if(!jQuery.isEmptyObject($scope.team.factor) && $scope.team.factor.length>0){
                    total+=$scope.team.factor[0].orderCash;
                    $scope.$$team.push($scope.team.factor[0]);
                }

                // 商家
                if(!jQuery.isEmptyObject($scope.team.seller) && $scope.team.seller.length>0){
                    total+=$scope.team.seller[0].orderCash;
                    $scope.$$team.push($scope.team.seller[0]);
                }

                // 三级会员
                if(!jQuery.isEmptyObject($scope.team.member) && $scope.team.member.length>0){
                    $.each($scope.team.member,function(k,v){
                        total += parseFloat(v.orderCash);
                        v['name'] = v['realName'];
                        $scope.$$team.push(v);
                    });
                }
                // 养老金 ，商家货款
                if(/^[01]|(11)$/.test($scope.dataPage.$$selectedItem.orderType)){
                    total+=$scope.dataPage.$$selectedItem.pensionMoney;
                    $scope.$$team.push({
                        name:"会员养老金",
                        orderCash:$scope.dataPage.$$selectedItem.pensionMoney
                    });
                    $scope.$$team.push({
                        name:"商家货款",
                        orderCash:$scope.dataPage.$$selectedItem.payMoney - total
                    });
                }
            });
        }

        $scope.getUserInfo = function (index, api) {
            $http.get(window.basePath + api).success(function (re) {
                $scope.userList[index] = re.content;
            });
        }

        $scope.getBelong = function (index, belongValue) {
            if (window.isEmpty(belongValue)) {
                return;
            }
            var time = setTimeout(function(){
                var url = window.basePath + "/account/Agent/getAgentAreaValueById?areaValue=" + belongValue;
                $http.get(url).success(function (re) {
                    $scope.userList[index]["belongArea"] = re.content.agentNameAll;
                    clearTimeout(time);
                });
            },1000);
        }

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }

        $scope.closeImgFun=function(){
            $scope.showImg='';
        }
    }
})();