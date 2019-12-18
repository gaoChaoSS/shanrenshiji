(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'order';
        $scope.entity = 'OrderInfo';
        $scope.entityTitle = "提现申请";
        $scope.tempGridList = '/view/pending/t_withdrawPend_grid.jsp';
        $scope.windowInfo = '/view/pending/t_withdrawPendInfo_grid.jsp';
        $scope.tempGridFilter = '/view/pending/t_withdrawPend_grid_filter.jsp';
        $scope.fullQueryApi = window.basePath + "/order/OrderInfo/getWithdrawPend";

        $scope.setUserType=function(){
            $scope.filter._status="0";
        }

        initGrid($rootScope, $scope, $http);

        $scope.showPendingData = function () {
            $scope.checkBtn=[false,false];
            var url = window.basePath;
            if($scope.dataPage.$$selectedItem.userType==='Member'){
                url+="/crm/";
            }else{
                url+="/account/";
            }
            url+=$scope.dataPage.$$selectedItem.userType+"/show?_id=" + $scope.dataPage.$$selectedItem.userId;
            $http.get(url).success(function (re) {
                $scope.userInfo=re.content;

                if($scope.dataPage.$$selectedItem.userType==='Member'){
                    $scope.userInfo.name = !isEmpty($scope.userInfo.realName)?$scope.userInfo.realName:$scope.userInfo.mobile;
                    $scope.userInfo.realCard = $scope.userInfo.idCard;
                    $scope.userInfo.contactPerson = $scope.userInfo.name;
                    $scope.userInfo.area = $scope.userInfo.realArea?$scope.userInfo.realArea:"";
                    $scope.userInfo.address = "";
                }
            });
        }

        $scope.getUserType=function(type){
            if(type=="Seller"){
                return "商家";
            }else if(type=="Factor"){
                return "服务站";
            }else if(type=="Member"){
                return "会员";
            }else{
                return "";
            }
        }

        $scope.getStatus=function(status){
            if(status==0){
                return '待处理';
            }else if(status==1){
                return '提现中';
            }else if(status==2){
                return '已提现';
            }else if(status==3){
                return '失败';
            }
        }

        $scope.checkBtnFun=function(index){
            for(var i= 0,len=$scope.checkBtn.length;i<len;i++){
                if(i==index){
                    $scope.checkBtn[i]=true;
                }else{
                    $scope.checkBtn[i]=false;
                }
            }
        }

        $scope.submitForm=function(){
            if(window.isEmpty($scope.dataPage.$$selectedItem.voucher) || $scope.dataPage.$$selectedItem.voucher.length>64){
                malert("请输入转账银行提供的凭证号码或唯一识别码（64位字符长度以内）");
                return;
            }
            var url = window.basePath + "/order/OrderInfo/updateWithdraw" +
                "?withdrawId="+$scope.dataPage.$$selectedItem._id+
                "&modifyStatus=1" +
                "&voucher="+$scope.dataPage.$$selectedItem.voucher;
            $http.get(url).success(function (re) {
                $scope.checkBtnFun(1);
            });
        }

        $scope.closeWin = function () {
            $scope.checkBtn = [false, false];
            $rootScope.showPopWin = false;
            $scope.closePopWin();
        }

    });
})(angular);
