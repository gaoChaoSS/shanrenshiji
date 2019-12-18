(function (angular, undefined) {

    var model = 'my';
    var entity = 'wallet';
    var entityUrl = '/' + model + '/' + entity;
    window.wechatAppId = 'wx9b369a21e6ec245d';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';

        $scope.getWallet = function(){
            var url = window.basePath + '/crm/Member/getWallet';
            $http.get(url).success(function(re){
                $scope.wallet = re.content.items[0].cashCount;
            })
        }

        $scope.getIsPayPwd=function(){
            var url = window.basePath + '/crm/Member/isMemberPayPwd';
            $http.get(url).success(function(re){
                $scope.isPayPwd=re.content.flag;
            });
        }
        $scope.setPayPassword = function(){
            var url = window.basePath + '/crm/Member/setPayPassword?firstPwd='+$scope.firstPwd+'&secondPwd='+$scope.secondPwd;
            $http.get(url).success(function (re) {
                malert("设置支付密码成功!");
                $scope.isPayPwd=false;
            })
        }

        $scope.changeUpType=function(type){
            $scope.topUpType=type;
            $scope.money="";
            $scope.friendPhone="";
            $scope.submitCheck=false;

            $scope.oldPwd="";
            $scope.newPwd="";
            $scope.secondPwd="";
            $scope.phoneNumber="";
            $scope.verification="";
        }

        $scope.payForgotPwd = function (){
            var url = window.basePath +'/crm/Member/payForgotPassword';
            var data = {
                loginName:$scope.phoneNumber,
                newPassword:$scope.newPassword,
                verification:$scope.verification
            }
            $http.put(url,data).success(function(){
                malert('密码找回成功!');
                $scope.phoneNumber="";
                $scope.newPassword="";
                $scope.verification="";
                $scope.okOrFail=true;
                $scope.isBox=true;
            })

        }
        $scope.getPhoneCheckCode = function(){
            if(!/^1[34578]{1}\d{9}$/.test($scope.phoneNumber)){
                malert('请输入正确的手机号!');
                return;
            }
            var url = window.basePath + '/crm/Member/getMobileIsReg?mobile='+$scope.phoneNumber;
            $http.get(url).success(function(re){
                if(re.content._id==null){
                    malert('抱歉!该账号尚未注册,请先注册');
                }else {
                    var url1 = window.basePath +'/common/Sms/getCheckCode';
                    var data1 = {
                        loginName:+$scope.phoneNumber,
                        type:'change_password'
                    }
                    $http.put(url1,data1).success(function(){
                        malert('获取验证码成功!');
                    })
                }
            })
        }

        $scope.modifyMyPayPwd = function(){
            var url = window.basePath + '/crm/Member/modifyMyPayPwd';
            var data={
                oldPwd: $scope.oldPwd,
                firstPwd: $scope.newPwd,
                secondPwd: $scope.secondPwd
            };
            $http.post(url,data).success(function (re) {
                $scope.oldPwd = '';
                $scope.newPwd = '';
                $scope.secondPwd = '';
                malert('密码修改成功!');
                $scope.okOrFail=true;
                $scope.isBox=true;
            })
        }

        $scope.changeWin=function(){
            $scope.showWinConWallet=!$scope.showWinConWallet;
        }
        $scope.closeSuccessWin=function(){
            $scope.isSuccessWin=false;
            $scope.sureOrder=false;
        }
        $scope.closeQrcode=function(){
            $scope.isQrcode=false;
            $scope.submitCheck=false;
            clearInterval($scope.queryTask);
        }

        //检查订单
        $scope.checkOrder = function () {
            if (!window.isEmpty($scope.order.orderNo)) {
                $scope.isSuccess=0;
                var url = window.basePath + '/order/OrderInfo/checkOrderStatus';
                var data = {
                    orderNo: $scope.order.orderNo
                }
                $http.post(url, data).success(function (reData) {
                    if (reData.content.payStatus == 'SUCCESS') {
                        clearInterval($scope.queryTask);
                        $scope.isQrcode=false;
                        $scope.isSuccess=1;
                        $scope.getIcon();
                        $scope.submitCheck = false;
                        $scope.isSuccessWin=true;
                        $scope.getWallet();
                    }
                });
            } 
        }

        $scope.genQrCode = function () {
            var postData = {};
            postData["type"] = "pushCash";
            postData["payType"] = $scope.payType;
            postData["clientType"] = "QrCode";
            postData["sellerId"] = $scope.order.sellerId;
            postData["totalFee"] = $scope.order.totalPrice;
            postData["storeId"] = "";
            postData["orderId"] = $scope.order._id;
            postData["memberId"] = $scope.order.memberId;
            var url = window.basePath + '/payment/Pay/prepay';
            $http.post(url, postData).success(function (re) {
                url = window.basePath + '/payment/Pay/startPay';
                postData = {};
                postData._id = re.content._id;
                $scope.payId = postData._id;
                $scope.payOrderNo = re.content.orderId;
                $http.post(url, postData).success(function (reData) {
                    if (reData.content && reData.content.code_url) {
                        // 生产2维码图片
                        var qr = qrcode(10, 'H');
                        qr.addData(reData.content.code_url);
                        qr.make();
                        $(".qrcodeImg").html(qr.createImgTag());
                        $(".qrcodeImg > img").css({
                            width: 250,
                            height: 250
                        });
                        $scope.queryTask = setInterval(function () {
                            $scope.checkOrder();
                            if ($location.path().indexOf("wallet") == -1) {
                                clearInterval($scope.queryTask);
                            }
                        }, 5000);
                    } else {
                        alert('生成支付二维码失败，请稍后再试');
                    }
                }).error(function (ex) {
                    console.log(ex)
                });
            }).error(function (ex) {
                console.log(ex)
            });
        };

        $scope.submitMy = function () {
            $scope.submitCheck = 'disabled';
            if ($scope.payType != 4 && $scope.payType != 10) {
                malert('请选择正确的支付方式');
                $scope.submitCheck = false;
                return;
            }
            if ($scope.money == null || !/^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                malert('请输入正确的消费金额');
                $scope.submitCheck = false;
                return;
            }

            var url = window.basePath + '/order/OrderInfo/createRecharge';
            var data = {
                totalPrice: $scope.money,
                userType: "member"
            }
            $http.post(url, data).success(function (re) {
                $scope.order = re.content;
                $scope.isQrcode=true;
                $scope.sureOrder=false;
                $scope.genQrCode();
            }).error(function () {
                $scope.submitCheck = false;
            });
        }

        $scope.submitFriend = function () {
            $scope.submitCheck = 'disabled';
            $scope.showWinConWallet=false;
            if(!/^1[3456789]{1}\d{9}$/.test($scope.friendPhone)){
                malert("手机号码格式错误!");
                $scope.submitCheck=false;
                return;
            }
            if(!/^\d+(?:\.\d{1,2})?$/.test($scope.money)){
                malert("充值金额错误!");
                $scope.submitCheck=false;
                return;
            }
            if ($scope.money != null && /^[0-9]+(.[0-9]{1,})?$/.test($scope.money)) {
                var url = window.basePath + '/order/OrderInfo/createRecharge';
                var data = {
                    totalPrice: $scope.money,
                    userType: "memberFriend",
                    payType:$scope.payType,
                    friendMobile:$scope.friendPhone
                }
                if($scope.payType==3){
                    data.payPwd=$scope.pwd1 + $scope.pwd2 + $scope.pwd3 + $scope.pwd4 + $scope.pwd5 + $scope.pwd6;
                    if(window.isEmpty(data.payPwd) || data.payPwd.length!=6){
                        malert("请输入支付密码");
                        $scope.submitCheck=false;
                        return;
                    }
                }
                $http.post(url, data).success(function (re) {
                    $scope.order = re.content;
                    if ($scope.payType == 3){
                        $scope.isSuccessWin=true;
                        if(re.content.orderStatus==5){
                            $scope.isSuccess=1;
                        }else{
                            $scope.isSuccess=0;
                        }
                        $scope.getIcon();
                        $scope.getWallet();
                    }else{
                        $scope.order = re.content;
                        $scope.isQrcode=true;
                        $scope.sureOrder=false;
                        $scope.genQrCode();
                    }
                }).error(function () {
                    $scope.submitCheck = false;
                });
            } else {
                malert('请输入正确的消费金额');
            }
            $scope.submitCheck = false;
        }

        //支付密码输入框
        $scope.getPwdWin = function () {
            if (window.isEmpty($scope.friendPhone)) {
                malert('请输入需要充值的号码');
            } else if ($scope.money < 0) {
                malert('请输入正数');
            } else if(window.isEmpty($scope.money)){
                malert('请输入充值金额');
            }else if($scope.payType==3){
                $scope.showWinConWallet = true;
                $scope.pwd1 = null;
                $scope.pwd2 = null;
                $scope.pwd3 = null;
                $scope.pwd4 = null;
                $scope.pwd5 = null;
                $scope.pwd6 = null;
                setTimeout(function(){
                    $(".enterInput input").eq(0).focus();
                },200)
            }else{
                $scope.submitFriend();
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

        //0:处理中;1:成功;2:失败
        $scope.getIcon=function(){
            if($scope.isSuccess==1){
                $scope.statusIcon= 'bkColorGreen1 icon-gou';
                $scope.statusText='支付成功';
            }else if($scope.isSuccess==2){
                $scope.statusIcon= 'bgNone font100px colorRed1 icon-cuowu';
                $scope.statusText='支付失败';
            }else if($scope.isSuccess==3){
                $scope.statusIcon= 'bgNone font100px colorRed1 icon-cuowu';
                $scope.statusText='获取订单失败';
            }else if($scope.isSuccess==0){
                $scope.statusIcon= 'bkColorBlue1 font70px icon-gengduo';
                $scope.statusText='3333支付处理中';
            }
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的钱包');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $scope.titleText="wallet";
                $scope.topUpType='1';
                $scope.updateOrForgot = '1';
                $scope.hoverTopUp=false;
                $scope.isBox=false;
                $scope.showWinConWallet=false;
                $scope.isPayPwd=false;
                $scope.getIsPayPwd();

                $scope.sureOrder = false;
                $scope.isQrcode=false;
                $scope.isSuccessWin=false;

                $scope.payType=4;
                $scope.getWallet();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);