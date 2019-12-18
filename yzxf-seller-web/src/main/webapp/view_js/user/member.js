(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {


        $scope.tempGridList = '/view/user/t_member_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_member_grid_filter.jsp';
        $scope.model = 'crm';
        $scope.entity = 'Member';
        $scope.entityTitle = "会员管理";
        $scope.windowInfo = '/view/user/t_memberInfo_grid.jsp';

        $scope.adminCheck=false;
        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            if ($rootScope.agent.level==1) {
                $scope.adminCheck=true;
            }
            if($rootScope.agent.level==1 || $rootScope.agent.level==4){
                $scope.addBtn('换卡',$scope.customBtn);
            }
            if($rootScope.agent.level==1){
                $scope.addBtn('修改',$scope.modifyData);
                $scope.windowAdd = '/view/user/t_memberAdd_grid.jsp';
                $scope.addBtn('激活',$scope.activeBtn);
                $scope.addBtn('创建',$scope.regBtn);
            }
            $scope.addBtn('投保记录',$scope.insureBtn);
            $scope.addBtn('团队关系',$scope.teamBtn);
            // $scope.addBtn('账号关联',$scope.teamBtn);
        }

        // 团队关系
        $scope.teamBtn = function(){
            teamGrid($rootScope, $scope, $http);
        }

        $scope.regBtn = function(){
            $scope.popWindowTemp = '/view/user/t_memberReg_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '创建会员';

            $scope.modifyType='add';
            modifyMemberGrid($rootScope, $scope, $http);
            // window.open("https://m.yzxf8.com/yzxfMember/account/reg");
        }

        $scope.insureBtn=function(){
            insureLogGrid($rootScope, $scope, $http);
        }

        $scope.addBtn=function(name,action){
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name==name){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: name, action: action
            });
        };

        $scope.activeBtn=function(){
            if(!$scope.checkSelected()){
                return;
            }
            $scope.userInfo={};
            var url = window.basePath + "/" + $scope.model + "/" + $scope.entity + "/show?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo = re.content;

                if(window.isEmpty($scope.userInfo)){
                    malert("获取会员信息失败");
                    return;
                }
                if(window.isEmpty($scope.userInfo.isRealName) || !$scope.userInfo.isRealName){
                    malert("请先实名认证");
                    return;
                }else if($scope.userInfo.isBindCard){
                    malert("该会员已激活会员卡");
                    return;
                }

                $scope.popWindowTemp = '/view/user/t_memberActive_grid.jsp';
                $rootScope.showPopWin = true;
                $rootScope.popWinTitle = '激活会员卡';
            });
        };

        initGrid($rootScope, $scope, $http);

        $scope.modifyInfo=function(){
            $scope.modifyType='modify';
            modifyMemberGrid($rootScope, $scope, $http);
        };

        $scope.checkSelected=function(){
            if(window.isEmpty($scope.dataPage) || window.isEmpty($scope.dataPage.$$selectedItem)
                || window.isEmpty($scope.dataPage.$$selectedItem._id)){
                malert("请选择一个用户");
                return false;
            }
            return true;
        }

        $scope.getNotActiveCardNo=function(){
            var factorId = '';
            if(!window.isEmpty($scope.filter._areaValue)){
                factorId = $scope.filter._areaValue.replace("___like_","");
                factorId = factorId.substring(2,factorId.length-2).split("\\_");
                if(factorId.length<5){
                    return;
                }
                factorId = factorId[4];
            }
            var url = window.basePath + "/account/Seller/getNotSendCardLog";
            $http.post(url,{
                checkCard:1,
                factorId:factorId,
                pageNo:1,
                pageSize:1
            }).success(function (re) {
                if(re.content!=null && re.content.sendCardList!=null){
                    $scope.userInfo.cardNo=re.content.sendCardList[0].startCardNo;
                }
            });
        }

        $scope.activeMember=function(){
            if(window.isEmpty($scope.dataPage.$$selectedItem._id)){
                malert("获取会员数据失败");return;
            }
            if(window.isEmpty($scope.filter._areaValue)){
                malert("获取上级归属失败");return;
            }

            var url = window.basePath + "/crm/Member/activeMember";
            var data = {
                memberId:$scope.dataPage.$$selectedItem._id,
                belongAreaValue:$scope.filter._areaValue.split("___like_")[1].replace(/\\_/g,"_")
            };
            if(!window.isEmpty($scope.userInfo.cardNo)){
                data["activeCard"]=$scope.userInfo.cardNo;
            }
            $http.post(url,data).success(function () {
                $rootScope.showPopWin=false;
                malert("激活成功");
                $scope.queryCurrentList();
            });
        };

        $scope.customFun=function(){
            if(!$scope.checkSelected()){
                return;
            }

            $scope.popWindowTemp = '/view/user/t_memberCard_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '换卡';
            $scope.userInfo={};
            $scope.showOtherData();
        }

        $scope.getInfo=function(){
            var url = window.basePath + "/" + $scope.model + "/" + $scope.entity + "/show?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo = re.content;
            });
        }

        $scope.showOtherData = function () {
            $scope.cardObj={};
            window.pageNo2=1;
            window.pageSize2=20;
            window.indexNum=0;
            $scope.userInfo={};
            $scope.getInfo();

            var url = window.basePath + "/order/OrderInfo/getMemberMoneyPension?memberId=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.memberAccount = re.content;
                $scope.memberAccount || ($scope.memberAccount = {});
            });
            //url = window.basePath + "/crm/Member/getMemberBelongField?memberId="+$scope.dataPage.$$selectedItem._id;
            //$http.get(url).success(function (re){
            //    $scope.belongCardField = re.content.items;
            //})
            url = window.basePath + "/crm/Member/getMemberCardTime?memberId="+$scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re){
                if(re.content.active!=null){
                    $scope.activeTime = re.content.active.activeTime;
                }
                if(re.content.exchange!=null){
                    $scope.exchangeTime = re.content.exchange.createTime;
                }
            })
            $scope.getAgentExchangeCardList($scope.dataPage.$$selectedItem._id);
        }

        $scope.getSex=function(sex){
            if(window.isEmpty(sex)){
                return '未知';
            }else if(sex == 1){
                return '男';
            }else if(sex == 2){
                return '女';
            }
        }

        $scope.getBelongArea=function(belongArea,card){
            if(!window.isEmpty(belongArea)){
                return belongArea;
            }
            if(window.isEmpty(card)){
                return '未绑定会员卡';
            }else if (card.substring(0,1)=="C"){
                return '平台';
            }
        }

        $scope.cardNO = function(startCardEnd){
            $scope.cardNofield ="";
            for(var i= 0;i<8-startCardEnd.toString().length;i++){
                $scope.cardNofield += '0';
            }
            return $scope.cardNofield+startCardEnd;
        }

        $scope.setMemberCanUse=function(){
            var url = window.basePath + "/crm/Member/setMemberCanUse?memberId=" + $scope.userInfo._id+"&canUse="+$scope.userInfo.canUse;
            $http.get(url).success(function () {
                malert("修改成功!");
                $rootScope.showPopWin = false;
                $scope.closePopWin();
                $scope.queryCurrentList();
            });
        }
        $scope.getAgentExchangeCardList = function(memberId){
            var url = window.basePath + '/crm/Member/getExchangeCardListForAgent?1=1';
            var date = {
                pageNo: window.pageNo2,
                pageSize: window.pageSize2,
                indexNum: window.indexNum,
                memberId:memberId
            }
            $.each(date, function (k, v) {
                if (typeof(v) == "undefined" || v == null || v == "null") {
                    return true;
                }
                url += '&' + k + '=' + encodeURIComponent(v);
            });
            $http.get(url).success(function(re){
                $scope.exchangeCardList = [];
                $.each(re.content.exchangeCardList, function (k, v) {
                    $scope.exchangeCardList.push(v);
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

        $scope.exchangeCard = function(phoneNumber,oldCard,newCard){
            if(window.isEmpty(oldCard)){
                malert('请输入旧卡卡号');
                return;
            }
            if(window.isEmpty(newCard)){
                malert('请输入新卡卡号');
                return;
            }
            var url = window.basePath + '/crm/Member/exchangeCardTest';
            var data = {
                phoneNumber:phoneNumber,
                oldCard:oldCard,
                newCard:newCard
            }
            $http.post(url,data).success(function(re){
                $rootScope.showPopWin = false;
                malert('换卡成功!');
                $scope.queryCurrentList();
            })

        }
        $("table").click(function(){
            alert("1")
        })
        //$scope.check = function (memberId) {
        //    $scope.checkInfo = !$scope.checkInfo;
        //    $rootScope.memberId = memberId;
        //    var url = window.basePath + "/crm/Member/agentGetMemberInfoById";
        //    var url2 = window.basePath + "/crm/Member/agentGetMemberTradeById";
        //    var data = {
        //        memberId: $rootScope.memberId,
        //        pageNo: window.pageNoInfo,
        //        pageSize: window.pageSizeInfo
        //    }
        //    $http.post(url, {memberId: memberId}).success(function (re) {
        //        $scope.memberInfo = re.content;
        //        $scope.isRenzheng = $scope.memberInfo.isRealName ? true : false;
        //    })
        //    $http.post(url2, data).success(function (re) {
        //        $.each(re.content.memberAccountList, function (k, v) {
        //            $scope.memberAccountList.push(v);
        //        });
        //        $scope.totalNumberInfo = re.content.totalNum;
        //        $scope.totalPageInfo = re.content.totalPage;
        //        //$scope.totalPage = 5;
        //
        //        //页码集合
        //        $scope.pageListInfo = [];
        //        //当前显示的页码从第几页开始
        //        var listCurInfo = 1;
        //        var listCurCountInfo = 5;
        //        if (window.pageNoInfo <= 5 && $scope.totalPageInfo <= 5) {
        //            listCurInfo = 1;
        //            listCurCountInfo = $scope.totalPageInfo;
        //        } else {
        //            listCurInfo = window.pageNoInfo - 2;
        //            if (listCurInfo <= 1) {
        //                listCurInfo = 1;
        //            } else if ((window.pageNoInfo > $scope.totalPageInfo - 3 && listCurInfo >= 4) || $scope.totalPageInfo == 6) {
        //                listCurInfo = $scope.totalPageInfo - 4;
        //            }
        //        }
        //        for (var index = 0; index < listCurCountInfo; index++, listCurInfo++) {
        //            $scope.pageListInfo.push({num: listCurInfo});
        //        }
        //        //选中的是第几个页码
        //        $scope.pageIndexInfo = window.pageNoInfo;
        //        //是否显示最后一页的页码
        //        $scope.isLastPageInfo = ($scope.pageIndexInfo < $scope.totalPageInfo - 2) && $scope.totalPageInfo > 5;
        //        //是否显示第一页的页码
        //        $scope.isFirstPageInfo = $scope.pageIndexInfo > 4 && $scope.totalPageInfo > 5;
        //    })
        //}
        //$scope.payType = function (type) {
        //    if (type == '1') {
        //        return '余额支付';
        //    } else if (type == '2') {
        //        return '微信';
        //    } else if (type == '3') {
        //        return '支付宝';
        //    } else {
        //        return '线下支付';
        //    }
        //}
        //$scope.tradeType = function (type) {
        //    if (type == '1') {
        //        return '替朋友充值';
        //    } else if (type == '2') {
        //        return '充值';
        //    } else if (type == '3') {
        //        return '线上消费';
        //    } else {
        //        return '线下消费';
        //    }
        //}
    });
})(angular);
