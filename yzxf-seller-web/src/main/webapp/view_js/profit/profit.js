(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;


        $scope.tempGridList = '/view/profit/t_profit_grid.jsp';
        $scope.tempGridFilter = '/view/profit/t_profit_filter.jsp';

        $scope.entityTitle = "利润比例";
        $scope.Log = false;
        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getLogByModify";


        initGrid($rootScope, $scope, $http);
        //$scope.filter._modifyType='1';

        $scope.selectModifyType=function(){
            if(/^[234678]$/.test($scope.filter._modifyType)){
                $scope.tempGridList = '/view/profit/t_profit_grid2.jsp';
            }else{
                $scope.tempGridList = '/view/profit/t_profit_grid.jsp';
            }
            //if($scope.filter._modifyType==2){
            //    $scope.tempGridList = '/view/profit/t_profit_grid2.jsp';
            //}else if($scope.filter._modifyType==""){
            //    $scope.tempGridList = '/view/profit/t_profit_grid.jsp';
            //}else if($scope.filter._modifyType==0){
            //    $scope.tempGridList = '/view/profit/t_profit_grid.jsp';
            //}else if($scope.filter._modifyType==3){
            //    $scope.tempGridList = '/view/profit/t_profit_grid2.jsp';
            //}else if($scope.filter._modifyType==4) {
            //    $scope.tempGridList = '/view/profit/t_profit_grid2.jsp';
            //}
            $scope.queryCurrentList();

        }
        $scope.setUserType=function(){
            $scope.filter._modifyType="";
            $scope.selectModifyType();
        }

        $rootScope.getFixed($scope.model, $scope.entity);
        $scope.profitSaveOrModify = false;
        $scope.pensionSaveOrModify = false;
        $scope.minSaveOrModify = false;
        $scope.poundageSaveOrModify = false;
        $scope.rechargeRatioSaveOrModify = false;
        $scope.rechargePensionRatioSaveOrModify = false;
        $scope.activeMoneySaveOrModify = false;
        $scope.width = 0.0;
        $scope.totalRatioT = null;
        $scope.profitList = [];
        $scope.modifyList = [];
        $scope.profitSaveOrModifyCard = false;
        $scope.profitSaveOrModifyRecharge = false;
        $scope.widthCard = 0.0;
        $scope.totalRatioTCard = null;
        $scope.profitListCard = [];
        $scope.modifyListCard = [];
        $scope.modifyListRecharge = [];

        $scope.agentLevel = function (level) {
            if (level == '1') {
                return '平台';
            } else if (level == '2') {
                return '一级代理(省)';
            } else if (level == '3') {
                return '二级代理(市)';
            } else if (level == '4') {
                return '三级代理(县)';
            } else if (level == '5') {
                return '服务中心';
            }
        }


        $scope.updateOrSaveRatio = function () {
            $scope.modifyList = [];
            $scope.width = 0.0;
            $scope.totalRatioT = null;
            for (var x = 0; x < 5; x++) {
                $scope.totalRatioT += parseFloat($scope.profitList[x].profitRatio);
            }
            if ($scope.totalRatioT != 100) {
                malert('各级比例总和只能为100%');
                return;
            } else if (/^[0-9]*[1-9][0-9]*$/.test($scope.totalRatioT)) {
                for (var i = 0; i < $scope.profitList.length; i++) {
                    $scope.modifyList.push({num: $scope.profitList[i].profitRatio});
                }
                var url = window.basePath + '/order/OrderInfo/updateProfitRatio';
                var data = {
                    ratio1: $scope.profitList[0].profitRatio,
                    ratio2: $scope.profitList[1].profitRatio,
                    ratio3: $scope.profitList[2].profitRatio,
                    ratio4: $scope.profitList[3].profitRatio,
                    ratio5: $scope.profitList[4].profitRatio,
                }
                $http.post(url, data).success(function () {
                    $scope.profitSaveOrModify = false;
                    var url1 = window.basePath + '/order/OrderInfo/updateProfitLog';
                    $http.get(url1).success(function(){
                        $scope.getProfitRatio();
                    })
                })
            } else {
                malert('请输入正整数');
            }
        }
        $scope.updateOrSaveRatioCard = function () {
            $scope.modifyListCard = [];
            $scope.width = 0.0;
            $scope.totalRatioT = null;
            for (var x = 0; x < 5; x++) {
                $scope.totalRatioT += parseFloat($scope.profitListCard[x].profitRatioCard);
            }
            if ($scope.totalRatioT != 100) {
                malert('各级比例总和只能为100%');
                return;
            } else if (/^[0-9]*[1-9][0-9]*$/.test($scope.totalRatioT)) {
                for (var i = 0; i < $scope.profitListCard.length; i++) {
                    $scope.modifyListCard.push({num: $scope.profitListCard[i].profitRatioCard});
                }
                var url = window.basePath + '/order/OrderInfo/updateProfitRatioCard';
                var data = {
                    ratio1: $scope.profitListCard[0].profitRatioCard,
                    ratio2: $scope.profitListCard[1].profitRatioCard,
                    ratio3: $scope.profitListCard[2].profitRatioCard,
                    ratio4: $scope.profitListCard[3].profitRatioCard,
                    ratio5: $scope.profitListCard[4].profitRatioCard,
                }
                $http.post(url, data).success(function () {
                    $scope.profitSaveOrModifyCard = false;
                    var url1 = window.basePath + '/order/OrderInfo/updateProfitLogCard';
                    $http.get(url1).success(function(){
                        $scope.getProfitRatioCard();
                    })
                })
            } else {
                malert('请输入正整数');
            }
        }

        $scope.updateOrSaveRatioRecharge = function () {
            $scope.modifyListRecharge = [];
            $scope.width = 0.0;
            $scope.totalRatioT = null;
            for (var x = 0; x < 5; x++) {
                $scope.totalRatioT += parseFloat($scope.recharge[x].profitRatio);
            }
            if ($scope.totalRatioT != 100) {
                malert('各级比例总和只能为100%');
                return;
            } else if (/^[0-9]*[1-9][0-9]*$/.test($scope.totalRatioT)) {
                for (var i = 0; i < $scope.recharge.length; i++) {
                    $scope.modifyListRecharge.push({num: $scope.recharge[i].profitRatio});
                }
                var url = window.basePath + '/order/OrderInfo/updateProfitRatioRecharge';
                var data = {
                    ratio1: $scope.recharge[0].profitRatio,
                    ratio2: $scope.recharge[1].profitRatio,
                    ratio3: $scope.recharge[2].profitRatio,
                    ratio4: $scope.recharge[3].profitRatio,
                    ratio5: $scope.recharge[4].profitRatio,
                }
                $http.post(url, data).success(function () {
                    $scope.profitSaveOrModifyRecharge = false;
                    $scope.getRecharge();
                })
            } else {
                malert('请输入正整数');
            }
        }

        $scope.notUpdateRatio = function () {
            $scope.profitSaveOrModify = false;
            $scope.getProfitRatio();
        }
        $scope.notUpdateRatioCard = function () {
            $scope.profitSaveOrModifyCard = false;
            $scope.getProfitRatioCard();
        }
        $scope.notUpdateRatioRecharge = function () {
            $scope.profitSaveOrModifyRecharge = false;
            $scope.getRecharge();
        }
        $scope.updateOrSaveMaxPension = function () {
            $scope.width = 0.0;
            if(window.isEmpty($scope.maxPensionMoney)){
                malert('养老金上限不能为空!');
                return;
            }
            if (parseFloat($scope.maxPensionMoney) <= 0) {
                malert('养老金上限只能大于0');
                $scope.pensionSaveOrModify = false;
                $scope.getProfitRatio();
            } else if (/^\d{0,4}(\.\d{0,2})?$/g.test($scope.maxPensionMoney)) {
                var url = window.basePath + '/order/OrderInfo/updateMaxPensionMoney';
                var data = {
                    maxPensionMoney: $scope.maxPensionMoney,
                }
                $http.post(url, data).success(function (re) {
                    $scope.pensionSaveOrModify = false;
                    var url1 = window.basePath + '/order/OrderInfo/updatePensionLog?modifyType=2';
                    $http.get(url1).success(function(){
                        $scope.getProfitRatio();
                        $scope.getProfitRatioCard();
                    })
                })
            } else {
                malert('最多4位整数2位小数!');
            }
        }
        $scope.updateOrSaveMin = function () {
            $scope.width = 0.0;
            if(window.isEmpty($scope.minWithdrawalMoney)){
                malert('最低提现额度不能为空!');
                return;
            }
            if (parseFloat($scope.minWithdrawalMoney) <= 0) {
                malert('最低提现额度不能小于0');
                $scope.minSaveOrModify = false;
                $scope.getProfitRatio();
            } else if (/^\d{0,4}(\.\d{0,2})?$/g.test($scope.minWithdrawalMoney)) {
                var url = window.basePath + '/order/OrderInfo/updateOrSaveMin';
                var data = {
                    minWithdrawalMoney: $scope.minWithdrawalMoney,
                }
                $http.post(url, data).success(function (re) {
                    $scope.minSaveOrModify = false;
                    var url1 = window.basePath + '/order/OrderInfo/updatePensionLog?modifyType=3';
                    $http.get(url1).success(function(){
                        $scope.getProfitRatio();
                        $scope.getProfitRatioCard();
                    })
                })
            } else {
                malert('最多4位整数2位小数!');
            }
        }
        $scope.updateOrSavePoundage = function () {
            $scope.width = 0.0;
            if(window.isEmpty($scope.poundageRatio)){
                malert('手续费比例不能为空!');
                return;
            }
            if (parseFloat($scope.poundageRatio) > 100) {
                malert('手续费比例最高不能超过100%');
                $scope.poundageSaveOrModify = false;
                $scope.getProfitRatio();
            } else if (/^\d{0,3}(\.\d{0,1})?$/g.test($scope.poundageRatio)) {
                var url = window.basePath + '/order/OrderInfo/updateOrSavePoundage';
                var data = {
                    poundageRatio: $scope.poundageRatio,
                }
                $http.post(url, data).success(function (re) {
                    $scope.poundageSaveOrModify = false;
                    var url1 = window.basePath + '/order/OrderInfo/updatePensionLog?modifyType=4';
                    $http.get(url1).success(function(){
                        $scope.getProfitRatio();
                        $scope.getProfitRatioCard();
                    })
                })
            } else {
                malert('最多3位整数1位小数!');
            }
        }

        //公共方法:保存比例
        $scope.updateRatio = function (entityName,ratio,modifyType) {
            $scope.width = 0.0;
            if(window.isEmpty($scope[entityName])){
                malert('比例不能为空!');
                return;
            }
            if (parseFloat($scope[entityName]) > 100) {
                malert('比例最高不能超过100%');
                $scope[entityName+'SaveOrModify'] = false;
                $scope.getProfitRatio();
            } else if (/^\d{0,3}(\.\d{0,1})?$/g.test(ratio)) {
                var url = window.basePath + '/order/OrderInfo/updateOrSaveRatio';
                var data = {
                    ratio: ratio,
                    entityName:entityName,
                    modifyType:modifyType
                }
                $http.post(url, data).success(function () {
                    $scope[entityName+'SaveOrModify'] = false;
                    $scope.getProfitRatio();
                })
            } else {
                malert('最多3位整数1位小数!');
            }
        };

        $scope.updateOrSaveActiveMoney = function () {
            $scope.width = 0.0;
            if(window.isEmpty($scope.activeMoney)){
                malert('激活金额不能为空!');
                return;
            }
            if (parseFloat($scope.activeMoneySaveOrModify) <= 0) {
                malert('激活金额不能小于0');
                $scope.activeMoneySaveOrModify = false;
                return;
            }
            if (!/^\d{0,4}(\.\d{0,2})?$/g.test($scope.activeMoney)) {
                malert('最多4位整数2位小数!');
                return;
            }
            var url = window.basePath + '/order/OrderInfo/updateOrSaveActiveMoney';
            var data = {activeMoney: $scope.activeMoney}
            $http.post(url, data).success(function () {
                $scope.activeMoneySaveOrModify = false;
                $scope.activeMoneyTemp=$scope.activeMoney;
                malert("修改成功");
            })
        }


        $scope.notUpdateOrSaveMaxPension = function () {
            $scope.pensionSaveOrModify = false;
            $scope.maxPensionMoney = $scope.pensionMoneyCeiling;
        }
        $scope.notUpdateOrSaveMin = function () {
            $scope.minSaveOrModify = false;
            $scope.minWithdrawalMoney = $scope.minWithdrawalMoneyCeiling;
        }
        $scope.notUpdateOrSavePoundage = function () {
            $scope.poundageSaveOrModify = false;
            $scope.poundageRatio = $scope.poundageRatioCeiling;
        }

        //公共方法:不保存
        $scope.notUpdate = function (entityName) {
            $scope[entityName+'SaveOrModify'] = false;
            $scope[entityName] = $scope[entityName+'Temp'];
        }


        $scope.totalProfit = function () {
            $scope.totalRatio = 0;
            $.each($scope.profitList, function (k, v) {
                v.profitRatio = parseFloat(v.profitRatio);
                v.profitRatio = v.profitRatio > 100 ? 100 : v.profitRatio;
                $scope.totalRatio += parseFloat(v.profitRatio);
            });
            $scope.totalRatio = $scope.totalRatio > 100 ? 100 : $scope.totalRatio;
            return $scope.totalRatio;
        }
        $scope.totalProfitCard = function () {
            $scope.totalRatioCard = 0;
            $.each($scope.profitListCard, function (k, v) {
                v.profitRatioCard =  parseFloat(v.profitRatioCard);
                v.profitRatioCard = v.profitRatioCard > 100 ? 100 : v.profitRatioCard;
                $scope.totalRatioCard += parseFloat(v.profitRatioCard);
            });
            $scope.totalRatioCard = $scope.totalRatioCard > 100 ? 100 : $scope.totalRatioCard;
            return $scope.totalRatioCard;
        }
        //校验数据
        $scope.checkRatio = function (total,list) {
            $scope[total] = 0;
            $.each($scope[list], function (k, v) {
                v.profitRatio =  parseFloat(v.profitRatio);
                v.profitRatio = v.profitRatio > 100 ? 100 : v.profitRatio;
                $scope[total] += parseFloat(v.profitRatio);
            });
            $scope[total] = $scope[total] > 100 ? 100 : $scope[total];
            return $scope[total];
        }

        $scope.getProfitRatio = function () {
            $scope.totalRatio = null;
            var url = window.basePath + '/order/OrderInfo/getAgentProfitRatio';
            $http.get(url).success(function (re) {
                $scope.pensionMoneyCeiling = re.content != null && re.content.items != null && re.content.items.length > 0 ? re.content.items[0].maxPensionMoney : 20;
                $scope.minWithdrawalMoneyCeiling = re.content != null && re.content.items != null && re.content.items.length > 0 ? re.content.items[0].minWithdrawalMoney : 20;
                $scope.poundageRatioCeiling = re.content != null && re.content.items != null && re.content.items.length > 0 ? re.content.items[0].poundageRatio : 20;
                $scope.rechargeRatioTemp = re.content != null && re.content.items != null && re.content.items.length > 0 ? re.content.items[0].rechargeRatio : 20;
                $scope.rechargePensionRatioTemp = re.content != null && re.content.items != null && re.content.items.length > 0 ? re.content.items[0].rechargePensionRatio : 20;
                $scope.activeMoneyTemp = re.content != null && re.content.items != null && re.content.items.length > 0 ? re.content.items[0].activeMoney : 20;
                $scope.maxPensionMoney = $scope.pensionMoneyCeiling;
                $scope.minWithdrawalMoney = $scope.minWithdrawalMoneyCeiling;
                $scope.poundageRatio = $scope.poundageRatioCeiling;
                $scope.rechargeRatio = $scope.rechargeRatioTemp;
                $scope.rechargePensionRatio = $scope.rechargePensionRatioTemp;
                $scope.activeMoney = $scope.activeMoneyTemp;
                $scope.profitList = re.content.items;
                if ($scope.profitList == null || $scope.profitList.length == 0) {
                    $scope.profitList = [
                        {level: 1, profitRatio: 20}
                        , {level: 2, profitRatio: 20}
                        , {level: 3, profitRatio: 20}
                        , {level: 4, profitRatio: 20}
                        , {level: 5, profitRatio: 20}
                    ];
                }

                $scope.totalProfit();
            });
        }
        $scope.getProfitRatioCard = function () {
            $scope.totalRatioCard = null;
            var url = window.basePath + '/order/OrderInfo/getAgentProfitRatioCard';
            $http.get(url).success(function (re) {
                $scope.profitListCard = re.content.items;
                if ($scope.profitListCard == null || $scope.profitListCard.length == 0) {
                    $scope.profitListCard = [
                        {levelCard: 1, profitRatioCard: 20}
                        , {levelCard: 2, profitRatioCard: 20}
                        , {levelCard: 3, profitRatioCard: 20}
                        , {levelCard: 4, profitRatioCard: 20}
                        , {levelCard: 5, profitRatioCard: 20}
                    ];
                }

                $scope.totalProfitCard();
            });
        };
        $scope.getRecharge = function () {
            $scope.totalRatioRecharge = null;
            var url = window.basePath + '/order/OrderInfo/getAgentProfitRatioRecharge';
            $http.get(url).success(function (re) {
                $scope.recharge = re.content.items;
                if ($scope.recharge == null || $scope.recharge.length == 0) {
                    $scope.recharge = [
                        {level: 1, profitRatio: 20}
                        , {level: 2, profitRatio: 20}
                        , {level: 3, profitRatio: 20}
                        , {level: 4, profitRatio: 20}
                        , {level: 5, profitRatio: 20}
                    ];
                }

                $scope.checkRatio("totalRatioRecharge","recharge");
            });
        };


        $scope.getProfitRatio();
        $scope.getProfitRatioCard();
        $scope.getRecharge();

    });
})(angular);
