(function () {
    window.initBalance = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            $scope.popWindowTemp = '/view/account/t_balanceCheck_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '对账条件';
            $scope.balanceData = {
                date : new Date()
            };

        };


        $scope.checkBalance = function () {
            if (isEmpty($scope.balanceData.date)) {
                malert("请填写对账日期");
                return;
            }
            var date = $scope.balanceData.date.showDate().replace(/-/g,'');
            $http.post(window.basePath + "/payment/Gpay/downloadBill",{date:date}).success(function () {
                malert("对账文件已生成,请前往贵商FTP文件服务器进行下载!");
            })
        }

        $scope.init();
    }
})();