/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'my';
    var entity = 'withdraw';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.getMoneyCharge=function(){
            $scope.moneyCharge=$scope.getMoney($scope.money*0.01*$scope.poundageRatio);
        }

        $scope.tixian = function () {
            if(!/^\d+(?:\.\d{1,2})?$/.test($scope.money)){
                malert("请输入正确的积分,不超过两位小数");
            } else if ($scope.money < 0) {
                malert('请输入正数');
            } else {
                $scope.menuCheck = true;
                $scope.pwd1 = null;
                $scope.pwd2 = null;
                $scope.pwd3 = null;
                $scope.pwd4 = null;
                $scope.pwd5 = null;
                $scope.pwd6 = null;
                setTimeout(function(){
                    $(".enterInput input").eq(0).focus();
                },200)

            }
            return false;
        }
        $(".enterInput input").bind('keyup', function (e) {
            var currKey = 0, e = e || event;
            currKey = e.keyCode || e.which || e.charCode;
            if (currKey == 8) {
                $(this).val('');
                $(this).prev().val('').focus();
            } else {
                if ($(this).val() == '') {
                    return;
                }
                $(this).next().focus();
            }
        });

        $scope.Withdrawal = function () {
            if(!$scope.wallet.canWithdrawMoney || Number($scope.money)+Number($scope.moneyCharge)>$scope.wallet.canWithdrawMoney){
                malert("可提现积分不足(含手续费),无法提现");
                return;
            }
            var url = window.basePath+"/order/OrderInfo/createWithdrawLog";
            var data = {
                payPwd: $scope.pwd1 + $scope.pwd2 + $scope.pwd3 + $scope.pwd4 + $scope.pwd5 + $scope.pwd6,
                withdrawMoney: $scope.money,
                userType:"Member"
            }
            $http.post(url, data).success(function () {
                malert("提现申请已提交");
                $rootScope.goBack();
            })
        }
        $scope.getWithdrawalProportion = function(){
            var url = window.basePath + '/order/OrderInfo/getWithdrawalProportion?type=Member';
            $http.get(url).success(function(re){
                $scope.poundageRatio = re.content.poundageRatio;
            })
        }

        $scope.getBankInfo = function(){
            var url = window.basePath + '/crm/Member/getBankInfo';
            $http.get(url).success(function(re){
                $scope.bankInfo = re.content;
                if(!isEmpty($scope.bankInfo.bankId)){
                    $scope.bankInfo.bankId = $scope.bankInfo.bankId.substring($scope.bankInfo.bankId.length-4,$scope.bankInfo.bankId.length);
                }
            })
        }

        $scope.getWallet = function(){
            var url = window.basePath + '/crm/Member/getWallet';
            $http.get(url).success(function(re){
                $scope.wallet = re.content.items[0];
            })
        }

        $scope.menuCheck = false;
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '积分转现');
                $scope.money = null;
                $scope.menuCheck = false;
                $scope.type = $rootScope.pathParams.userType;
                $('#inputMoney').focus();
                $scope.moneyCharge=0;
                $scope.getWithdrawalProportion();
                $scope.getBankInfo();
                $scope.getWallet();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    })
    ;
})
(angular);
